/**
 * EasySOA Registry
 * Copyright 2011-2013 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.dbb;


/**
 * Interface allowing to get configurable implementation of ResourceUpdateService
 * & configure it
 *
 * @author mdutoo
 */
public interface ConfigurableResourceUpdateService extends ResourceUpdateService {

    /**
     * If set to true, the synchronous update service will be used, else the asynchronous service
     * @param synchronous
     */
    void setSynchronousUpdateService(boolean synchronous);
    
}
