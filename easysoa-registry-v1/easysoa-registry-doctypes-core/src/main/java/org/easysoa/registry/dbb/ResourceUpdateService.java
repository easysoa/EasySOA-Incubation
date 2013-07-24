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

import org.nuxeo.ecm.core.api.ClientException;
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
    public boolean isNewResourceRetrieval(DocumentModel newRdi, DocumentModel oldRdi) throws ClientException;
    
    /**
     * Update resource method
     * @param newRdi The new resource
     * @param oldRdi The old resource
     * @param documentToUpdate The registry document to update (must have a CoreSession)
     * @param resourceDownloadService to be used to download the resource from its url
     * @throws Exception 
     */
    public void updateResource(DocumentModel sourceDocument, DocumentModel previousDocumentModel,
            DocumentModel documentToUpdate) throws ClientException;
    
    /**
     * Fire a "resourceDownloaded" event, used by framework
     * @param document The document to associate with the event (must have a CoreSession)
     * @throws ClientException
     */
    public void fireResourceDownloadedEvent(DocumentModel document) throws ClientException;
    
    /**
     * Use by framework when async download
     * @param newRdi
     * @return
     * @throws ClientException 
     */
    public ResourceDownloadService getProbeResourceDownloadService(DocumentModel newRdi) throws ClientException;
    
}
