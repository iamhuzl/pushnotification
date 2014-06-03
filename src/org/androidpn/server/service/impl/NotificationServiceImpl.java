/**
 * 
 */
package org.androidpn.server.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.service.NotificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 通知管理.
 * @author 史明松
 */
public class NotificationServiceImpl implements NotificationService {
	protected final Log log = LogFactory.getLog(getClass());

	@Resource
	private HibernateTemplate hibernateTemplate; 
	 
	@Transactional
	public void deleteNotification(Long id) {
		log.info(" delete notification:id =" + id);
		hibernateTemplate.delete(queryNotificationById(id));
	}


	public NotificationMO queryNotificationById(Long id) {
		log.info(" query notification:id =" + id);
		NotificationMO notificationMO = (NotificationMO) hibernateTemplate
				.get(NotificationMO.class, id);
		return notificationMO;
	}

	@Transactional
	public void saveNotification(NotificationMO notificationMO) {
		log.info(" create a new notification:username = " + notificationMO.getUsername());
		hibernateTemplate.save(notificationMO);
	}

	@Transactional
	public void updateNotification(NotificationMO notificationMO) {
		log.info(" update notification : stauts = " +notificationMO.getStatus());
		hibernateTemplate.update(notificationMO);
	}
	@Transactional
	public void createNotifications(List<NotificationMO> notificationMOs) {
		for (NotificationMO notificationMO : notificationMOs) {
			saveNotification(notificationMO);
		}
	}
	
	@SuppressWarnings("unchecked")
	public NotificationMO queryNotificationByUserName(String userName,String messageId){
		Object[] params = new Object[] {userName, messageId};
		List<NotificationMO> list = hibernateTemplate
				.find(
						"from NotificationMO n where n.username=? and n.messageId=? order by n.createTime desc",
						params); 
		return list.isEmpty() ? null : list.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotification(NotificationMO mo) { 
		List<NotificationMO> list = hibernateTemplate.findByExample(mo);
		if(list.isEmpty()){
			log.info(" query notifications : list is null");
		}else{
			log.info(" query notifications : size = " + list.size());
		}
		return list;
	}
 
	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotification(String username) { 
		List<NotificationMO> list =hibernateTemplate.find("from NotificationMO n where n.username=? and " +
				"(n.status=0 or n.status=1) order by n.createTime desc",new Object[] {username});
		if(list.isEmpty()){
			log.info(" 查询离线通知 : list is null");
		}else{
			log.info(" 离线通知 : size = " + list.size());
		}
		return list;
	} 
}
