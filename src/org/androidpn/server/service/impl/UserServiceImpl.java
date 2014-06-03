package org.androidpn.server.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;

import org.androidpn.server.model.User;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 用户管理.
 * @author 史明松
 */
public class UserServiceImpl implements UserService {

    protected final Log log = LogFactory.getLog(getClass());

	@Resource
	private HibernateTemplate hibernateTemplate; 
 

    public User getUser(String userId) {
        return (User) hibernateTemplate.get(User.class,new Long(userId));
    }

    @SuppressWarnings("unchecked")
	public List<User> getUsers() {
    	 return hibernateTemplate.find(
                 "from User u order by u.createdDate desc");
    }
    @Transactional
    public User saveUser(User user) throws UserExistsException {
        try {
        	 hibernateTemplate.save(user);
            return user;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        }
    }

    @SuppressWarnings("unchecked")
	public User getUserByUsername(String username) throws UserNotFoundException {
    	 List<User> users =hibernateTemplate.find("from User where username=?",
                 username);
         if (users == null || users.isEmpty()) {
             throw new UserNotFoundException("User '" + username + "' not found");
         } else {
             return (User) users.get(0);
         } 
    }
    @Transactional
    public void removeUser(Long userId) {
        log.debug("removing user: " + userId);
        hibernateTemplate.delete(getUser(""+userId));
    }

}
