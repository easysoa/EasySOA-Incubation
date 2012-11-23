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

package fr.axxx.pivotal.app.api;

import java.util.List;

import org.osoa.sca.annotations.Service;

import fr.axxx.pivotal.app.model.User;

/**
 * Manages users
 */
@Service
public interface UserService {

    /**
     * Log in 
     * 
     * @param username
     * @param password
     * @return Current user, or null if connection fails
     */
    User connect(String username,String password);
    
    /**
     * Create new account with the given parameters
     * 
     * @param login
     * @param password
     * @param confirmPassword
     * @param mail
     * @param name
     * @param surname
     * @param civility
     * @param town
     * @param country
     * @param birthday
     * @return
     */
    User createAccount(String login, String password, String confirmPassword,
            String mail, String name);
    
    /**
     * Search User with the corresponding <b>id</b>
     * 
     * @param userId
     * @return the user found
     */
    User searchUser(Long userId);
    
    /**
     * Search User with the corresponding <b>idString</b>
     * 
     * @param userIdString
     * @return the user found
     */
    User searchUser(String userIdString);

	List<User> getAllUsers();
    
}
