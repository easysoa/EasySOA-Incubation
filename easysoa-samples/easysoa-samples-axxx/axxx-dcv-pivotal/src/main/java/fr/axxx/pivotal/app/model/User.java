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

package fr.axxx.pivotal.app.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.axxx.pivotal.persistence.GenericEntity;

/**
 * AXXX Pivotal User
 */
@Entity // NB. "user" is a reserved key word (in postgresql at least)
@Table(name = "pivotal_user")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User")
@XmlRootElement(name = "User")
public class User extends GenericEntity<User> {
	
	private static final long serialVersionUID = -7733753144220182597L;
	
	@XmlAttribute(name = "login", required = true)
    protected String login;
    @XmlAttribute(name = "username")
    protected String username;
    @XmlAttribute(name = "password")
    protected String password;
    @XmlAttribute(name = "mail", required = true)
    protected String mail;

    public User() {
    }

    public User(String login, String username, String password, String mail) {
    	this();
        this.login = login;
        this.username = username;
        this.password = password;
        this.mail = mail;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String value) {
        this.mail = value;
    }
    
}
