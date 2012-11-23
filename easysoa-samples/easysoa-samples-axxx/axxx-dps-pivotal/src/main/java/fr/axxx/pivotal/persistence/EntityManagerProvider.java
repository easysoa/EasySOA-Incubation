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

import javax.persistence.EntityManager;

import org.osoa.sca.annotations.Service;

/**
 * Provides an initialized EntityManager
 * 
 * @author mdutoo
 *
 */
@Service
public interface EntityManagerProvider {

	public EntityManager get();
	
}
