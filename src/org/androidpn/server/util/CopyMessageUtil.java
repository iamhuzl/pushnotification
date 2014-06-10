/**
 * 
 */
package org.androidpn.server.util;

import org.androidpn.server.model.NotificationMO;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengqiang.liu
 *	将生成的通知ID给notificationMO对象入库 ，方便根据此ID修改回执状态。
 */
public class CopyMessageUtil {
	
	public static void IQ2Message(IQ iq,NotificationMO notificationMO){
		
		Element root = iq.getElement();
		Attribute idAttr=root.attribute("id");
		if(idAttr != null){
			String id = idAttr.getValue();
			notificationMO.setMessageId(id);
		}
	}

    public static Map<String,String> line2Map(String line){
        if(StringUtils.isEmpty(line))return Collections.emptyMap();
        Map<String,String> map = new HashMap<String, String>();
        String[] array = StringUtils.split(line,";");
        for (int i = 0; i < array.length; i++) {
            String pair = array[i];
            String[] keyV = StringUtils.split(pair,"=");
            if(StringUtils.isEmpty(keyV[0]))continue;
            map.put(keyV[0],keyV[1]);
        }
        return map;
    }
}
