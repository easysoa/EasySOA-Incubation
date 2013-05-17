/**
 * EasySOA Proxy
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

import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class ProbeConfUtil {

    /**
     * 
     * @param probe
     * @param event
     * @return 
     */
    public boolean isResourceProbeEventCustom(Object probe, Event event){
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isSelfUpdated(){
        return false;
    }
    
    /**
     * 
     * @return A ResourceUpdaterService
     */
    public ResourceUpdateService getRegistryResourceUpdater() throws Exception {
        return Framework.getService(ResourceUpdateService.class);
    }
    
}
