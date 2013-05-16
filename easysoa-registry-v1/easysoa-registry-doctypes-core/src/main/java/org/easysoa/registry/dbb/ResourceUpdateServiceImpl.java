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

import org.easysoa.registry.types.ResourceDownloadInfo;

/**
 *
 * @author jguillemotte
 */
public class ResourceUpdateServiceImpl implements ResourceUpdateService {
    
    // Default constructor
    public ResourceUpdateServiceImpl(){
        
    }

    @Override
    public boolean isNewResourceRetrieval(ResourceDownloadInfo rdi) {
        // Check if the rid.timestamp is not null and doesn't differ
        /*if(){
            
        }*/
        return true;
    }
    
    @Override
    public void syncUpdateResource(ResourceDownloadInfo rdi) throws Exception {
        
        // Get probe conf
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void asyncUpdateResource(ResourceDownloadInfo rdi) throws Exception {
        
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

