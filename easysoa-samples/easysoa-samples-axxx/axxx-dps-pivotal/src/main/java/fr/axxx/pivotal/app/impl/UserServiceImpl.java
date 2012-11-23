/**
 * EasySOA AXXX Pivotal
 * 
 * Copyright (C) 2011-2012 Open Wide
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: http://www.easysoa.org
 *
 * Author: Marc Dutoo
 *
 * Contributor(s): 
 *
 */

package fr.axxx.pivotal.app.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import fr.axxx.pivotal.app.api.UserService;
import fr.axxx.pivotal.app.model.User;
import fr.axxx.pivotal.persistence.EntityManagerProvider;
import fr.axxx.pivotal.utils.DataUtils;

/**
 * UserService implementation on top of JPA 
 */
@Scope("COMPOSITE")
public class UserServiceImpl implements UserService {
    
    private final static Logger LOG = Logger.getLogger(UserServiceImpl.class.getCanonicalName());

    @Reference
    public EntityManagerProvider database;
    
    /**
     * @see Users#connect(String, String)
     */
    @Override
    public User connect(String login, String password) { 
        try {
            String encryptedPass = DataUtils.crypt(password);
            EntityManager entityManager = database.get();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login AND u.password = :password");
            query.setParameter("login", login);
            query.setParameter("password", encryptedPass);
            User user = (User) query.getSingleResult();
            
            return user;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while logging in", e);
            return null;
        }
    }

    /**
     * @see Users#createAccount(String, String, String, String, String, String, String, String, String, String)
     */
    @Override
    public User createAccount(String login, String password, String confirmPassword, String mail, String name){
        String encryptedPass = DataUtils.crypt(password);
        EntityManager entityManager = database.get();
        User user = null;
        try{
            user = new User(login, name, encryptedPass, mail);
        
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            
            entityManager.getTransaction().commit();
            
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to create account: " + e.getMessage(), e);
            return null;
        }
        
        //TODO - Send mail to user
      
        return user;
    }


    /**
     * Retrieves all users from database
     * 
     * @param entityManager
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        try {
            Query query = this.database.get().createQuery("SELECT u FROM User u");
            List<User> users = (List<User>) query.getResultList();
            return users;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
            return null;
        }
    }

    /**
     * Retrieves an user by login
     * 
     * @param entityManager
     * @param login
     * @return
     */
    public User getUser(String login) {
        try {
            Query query = this.database.get().createQuery("SELECT u FROM User u where u.login = :login");
            query.setParameter("login", login);
            User user = (User) query.getSingleResult();
            return user;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
            return null;
        }
    }

    /**
     * @see Users#searchUser(Long)
     */
    @Override
    public User searchUser(Long userId) {
        EntityManager entityManager = database.get();
        User user = entityManager.find(User.class, userId);
        return user;
    }
    
    /**
     * @see Users#searchUser(String)
     */
    @Override
    public User searchUser(String userIdString) {
        Long userId = Long.parseLong(userIdString);
        return this.searchUser(userId);
    }
    
    /**
     * @see Users#searchUser(String)
     */
    @Init
    public void fillDefaults() {
    	List<User> allUsers = this.getAllUsers();
    	if  (allUsers == null || allUsers.isEmpty()) {
            LOG.log(Level.WARNING, "Filling Users...");
            try {
            	this.database.get().getTransaction().begin();
            	
    	        User u1 = new User("admin", "Administrator", DataUtils.crypt("admin"), "admin@axxx.fr");
            	this.database.get().persist(u1);
            	
            	this.database.get().getTransaction().commit();
            } catch (Exception e) {
            	this.database.get().getTransaction().rollback();
                LOG.log(Level.SEVERE, "Error trying to create a user", e);
            }
    	}
    }

}
