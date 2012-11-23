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

package fr.axxx.pivotal.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

import fr.axxx.pivotal.app.impl.UserServiceImpl;


/**
 * Initializes and provides a database connection
 *
 */
@Scope("COMPOSITE")
public class EntityManagerProviderImpl implements EntityManagerProvider {

    private final static Logger LOG = Logger.getLogger(UserServiceImpl.class.getCanonicalName());
    
    @Property(required = true)
    protected String persistenceUnitName = "jpa-easysoa";

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    
    
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }
    
    @Destroy
    public void destroy() {
        entityManagerFactory.close();
    }

    /**
     * @see Provider#get()
     */
    public EntityManager get() {
        return this.entityManager;
    }

    /**
     * Connects to database and sets the entity manager  
     */
    @Init
    public void setup() {
        try {
            entityManagerFactory = Persistence
                    .createEntityManagerFactory(persistenceUnitName);
            entityManager = entityManagerFactory.createEntityManager();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Fatal error initing database", e);
        }
    }
}
