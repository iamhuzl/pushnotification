/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import java.util.*;

import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.model.User;
import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.CopyMessageUtil;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/**
 * This class is to manage sending the notifcations to the users.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationManager {

	private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

	private final Log log = LogFactory.getLog(getClass());

	private SessionManager sessionManager;
	
	private NotificationService notificationService;
	
	private UserService userService;

	/**
	 * Constructor.
	 */
	public NotificationManager() {
		sessionManager = SessionManager.getInstance();
		notificationService = ServiceLocator.getNotificationService();
		userService = ServiceLocator.getUserService();
	}
	
	

	/**
	 * Broadcasts a newly created notification message to all connected users.
	 * 
	 * @param apiKey
	 *            the API key
	 * @param title
	 *            the title
	 * @param message
	 *            the message details
	 * @param uri
	 *            the uri
	 */
	public void sendBroadcast(String apiKey,String type, String title, String message,
			String uri,String attrs) {
		log.debug("sendBroadcast()...");
		List<NotificationMO> notificationMOs = new ArrayList<NotificationMO>();
		IQ notificationIQ = createNotificationIQ(apiKey,type, title, message, uri,attrs);

		for (ClientSession session : sessionManager.getSessions()) {
			// 创建通知对象
			NotificationMO notificationMO = new NotificationMO(apiKey,type, title,
					message, uri,attrs);
			try {
				notificationMO.setUsername(session.getUsername());
				notificationMO.setClientIp(session.getHostAddress());
				notificationMO.setResource(session.getAddress().getResource());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将消息的ID添加到通知对象
			CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);

			if (session.getPresence().isAvailable()) {
				notificationMO.setStatus(NotificationMO.STATUS_SEND);
				notificationIQ.setTo(session.getAddress());
				session.deliver(notificationIQ);
			} else {
				notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
			}
			//将每个通知加入集合中
			notificationMOs.add(notificationMO);
		}
		try{
			//批量入库
			notificationService.createNotifications(notificationMOs);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!");
		}
		
	}
	
	//给所有用户发送通知
	public void sendAllBroadcast(String apiKey,String type, String title, String message,
			String uri,String attrs) {
		//生产推送通知对象，之所以移到这里来，是为了保证同一批次推送的通知ID是一样的。没什么作用
		IQ notificationIQ = createNotificationIQ(apiKey,type, title, message, uri,attrs);
		List<User> list = userService.getUsers();
		for (User user : list) {
			this.sendNotificationToUser(apiKey,type, user.getUsername(), title, message, uri,attrs, notificationIQ, false);
		}
		
	}

	/**
	 * Sends a newly created notification message to the specific user.
	 * 
	 * @param apiKey
	 *            the API key
	 * @param title
	 *            the title
	 * @param message
	 *            the message details
	 * @param uri
	 *            the uri
	 */
	public void sendNotificationToUser(String apiKey,String type, String username,
                                       String title, String message, String uri,String attrs, IQ notificationIQ, boolean hasSession) {
		log.debug("sendNotificationToUser()...");
		ClientSession session = sessionManager.getSession(username);
        //用户未在线
        if(hasSession && session == null){
            return ;
        }
		// 创建通知对象
		NotificationMO notificationMO = new NotificationMO(apiKey,type, title,
				message, uri,attrs);
		//接收人
		notificationMO.setUsername(username);
		// 将消息的ID添加到通知对象
		CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);
		if (session != null && session.getPresence().isAvailable()) {
			notificationIQ.setTo(session.getAddress());
			//推送消息
			session.deliver(notificationIQ);
			//已发送状态
			notificationMO.setStatus(NotificationMO.STATUS_SEND);
			try {
				notificationMO.setClientIp(session.getHostAddress());
				notificationMO.setResource(session.getAddress().getResource());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			//未发送
			notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
		}
		try{
			//保存数据库
			notificationService.saveNotification(notificationMO);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!",e);
		}
	}

	/**
	 * Creates a new notification IQ and returns it.
	 */
	private IQ createNotificationIQ(String apiKey,String type, String title,
			String message, String uri,String strAttrs) {
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());
		// String id = String.valueOf(System.currentTimeMillis());

		Element notification = DocumentHelper.createElement(QName.get(
				"notification", NOTIFICATION_NAMESPACE));
        if(StringUtils.isNotEmpty(type))
            notification.addAttribute("type",type);
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText(apiKey);
        if(StringUtils.isNotEmpty(title))
		    notification.addElement("title").setText(title);
        if(StringUtils.isNotEmpty(message)) {
            notification.addElement("message").setText(message);
        }
        if(StringUtils.isNotEmpty(uri))
		    notification.addElement("uri").setText(uri);
        createAttrs(notification,strAttrs);
		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);

		return iq;
	}

    private static  void createAttrs(Element notification,String strAttrs){
        Map<String,String> attrs = CopyMessageUtil.line2Map(strAttrs);
        if(attrs.isEmpty())return;
        Iterator<String> keys = attrs.keySet().iterator();
        while (keys.hasNext()) {
            String name = keys.next();
            notification.addElement("attr").addAttribute("name",name).setText(attrs.get(name));
        }

    }
	
	public void sendNotifications(String apiKey,String type, String username,
			String title, String message, String uri,String attrs,boolean hasSession){
		//生产推送通知对象，之所以移到这里来，是为了保证同一批次推送的通知ID是一样的。没什么作用
		IQ notificationIQ = createNotificationIQ(apiKey,type, title, message, uri,attrs);
		//如果是多个用户，根据“;”分隔 ，循环发送
		if(username.indexOf(";")!=-1){
			String[] users = username.split(";");
			for (String user : users) {
				this.sendNotificationToUser(apiKey,type, user, title, message, uri,attrs, notificationIQ, hasSession);
			}
		}else{
			this.sendNotificationToUser(apiKey,type, username, title, message, uri,attrs, notificationIQ, hasSession);
		}
	}
	
	//发送离线消息
	public void sendOfflineNotification(NotificationMO notificationMO ) {
		log.debug("sendOfflineNotifcation()...");
		IQ notificationIQ = createNotificationIQ(notificationMO.getApiKey(),notificationMO.getType(), notificationMO.getTitle(), notificationMO.getMessage(), notificationMO.getUri(),notificationMO.getAttrs());
		//将生产通知ID替换成原来的ID
		notificationIQ.setID(notificationMO.getMessageId());
		ClientSession session = sessionManager.getSession(notificationMO.getUsername());
		if (session != null && session.getPresence().isAvailable()) {
			notificationIQ.setTo(session.getAddress());
			session.deliver(notificationIQ);
			try{
				//修改通知状态为已发送
				notificationMO.setStatus(NotificationMO.STATUS_SEND);
				//IP
				notificationMO.setClientIp(session.getHostAddress());
				//来源
				notificationMO.setResource(session.getAddress().getResource());
				notificationService.updateNotification(notificationMO);
			}catch (Exception e) {
				log.warn(" update notification status failure !");
			}
		}
	}


    public static void main(String[] args) {
        NotificationManager manager = new NotificationManager();
        IQ iq = manager.createNotificationIQ("a232","1","title","message",null,"key1=v1;key2=v2");
        System.out.println(iq.getChildElement().asXML());
        iq = manager.createNotificationIQ("a232","1","title","message",null,"key1=v1");
        System.out.println(iq.getChildElement().asXML());
        System.out.println(iq.getChildElement().asXML());
        iq = manager.createNotificationIQ("a232","1","title","message",null,null);
        System.out.println(iq.getChildElement().asXML());
    }
}
