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

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author jguillemotte
 */
public interface ResourceUpdateService {

    /**
     * Check if the resource to update has already been updated
     * @param newRdi The new resource
     * @param oldRdi The old resource
     * @return true if the resource will be retrieved for the first time, false otherwise
     */
    public boolean isNewResourceRetrieval(DocumentModel newRdi, DocumentModel oldRdi) throws Exception;
    
    /**
     * Update resource method
     * @param newRdi The new resource
     * @param oldRdi The old resource
     * @param The registry document to update
     * @throws Exception 
     */
    public void updateResource(DocumentModel newRdi,
            DocumentModel oldRdi, DocumentModel documentToUpdate) throws Exception;
    
}
