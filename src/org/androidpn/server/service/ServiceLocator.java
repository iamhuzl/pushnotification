package org.androidpn.server.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

 
public class ServiceLocator implements BeanFactoryAware {
    private static BeanFactory beanFactory = null;

    private static ServiceLocator servlocator = null;

    public static String USER_SERVICE = "userService";

    public static String NOTIFICATION_SERVICE = "notificationService";

    public void setBeanFactory(BeanFactory factory) throws BeansException {
	this.beanFactory = factory;
    }

    public BeanFactory getBeanFactory() {
	return beanFactory;
    }

    public static ServiceLocator getInstance() {
	if (servlocator == null)
	    servlocator = (ServiceLocator) beanFactory.getBean("serviceLocator");
	return servlocator;
    }

    /**
     * 根据提供的bean名称得到相应的服务类
     * 
     * @param servName
     *            bean名称
     */
    public static Object getService(String servName) {
	return beanFactory.getBean(servName);
    }

    /**
     * 根据提供的bean名称得到对应于指定类型的服务类
     * 
     * @param servName
     *            bean名称
     * @param clazz
     *            返回的bean类型,若类型不匹配,将抛出异常
     */
    public static Object getService(String servName, Class clazz) {
	return beanFactory.getBean(servName, clazz);
    }

    /**
     * Obtains the user service.
     * 
     * @return the user service
     */
    public static UserService getUserService() {
	return (UserService) getService(USER_SERVICE);
    }

    public static NotificationService getNotificationService() {
	return (NotificationService) getService(NOTIFICATION_SERVICE);
    }
}
