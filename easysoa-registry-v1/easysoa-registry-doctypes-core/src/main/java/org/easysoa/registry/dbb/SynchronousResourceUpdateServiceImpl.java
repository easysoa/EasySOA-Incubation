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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class SynchronousResourceUpdateServiceImpl implements ResourceUpdateService {
    
    private static Logger logger = Logger.getLogger(SynchronousResourceUpdateServiceImpl.class);
    // TODO : document service to get the rdi attributes
    // TODO : get the resourceDownloadService to get the resource
    // TODO : Update the endpoint or the information service with the updated resource
    
    // Default constructor
    public SynchronousResourceUpdateServiceImpl(){
        
    }

    @Override
    public boolean isNewResourceRetrieval(DocumentModel newRdi, DocumentModel oldRdi) throws ClientException {
        
        if (oldRdi == null) {
            return true;
        }
        
        // Check if the rdi.timestamp is not null and doesn't differ ??
        //String url = (String) newRdi.getProperty(ResourceDownloadInfo.XPATH_ACTUAL_URL);
        // NB. if url differs, timestamp should change
        String newTimestamp = (String) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP); // TODO dateTime
        String oldTimestamp = (String) oldRdi.getPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP); // TODO dateTime
        if(newTimestamp != null && oldTimestamp != null && newTimestamp.equals(oldTimestamp)){
            return false;
        }
        return true;
    }
    
    @Override
    public void updateResource(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate, ResourceDownloadService resourceDownloadService) throws ClientException {

        // Check if update is needed
        if (!isNewResourceRetrieval(newRdi, oldRdi)) {
            return;
        }
        
        CoreSession coreSession = documentToUpdate.getCoreSession();
        
        // Get the new resource
        String newUrl = (String) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_URL);
        
        // For test only, to remove
        if(newUrl == null){
            //newUrl = "http://footballpool.dataaccess.eu/data/info.wso?WSDL";
            return;
        }
        File resourceFile;
        InputStream file;
        try {
            resourceFile = resourceDownloadService.get(new URL(newUrl));
            file = new FileInputStream(resourceFile);
        } catch (MalformedURLException ex) {
            throw new ClientException("Bad URL : " + newUrl, ex);
        } catch (Exception ex) {
            throw new ClientException("Error downloading " + newUrl, ex);
        }
        
        // Update registry with new resource
        Blob resourceBlob = FileUtils.createSerializableBlob(file, resourceFile.getName(), null);
        //FileBlob fileBlob = new FileBlob(resourceFile);
        //fileBlob.setFilename(resourceFile.getName());
        //fileBlob.setMimeType("text/html");
        //fileBlob.setEncoding("UTF-8"); // this specifies that content bytes will be stored as UTF-8
        documentToUpdate.setProperty("file", "content", resourceBlob);
        
        //coreSession.saveDocument(documentToUpdate); // updates & triggers events ; TODO now or later ??
        coreSession.save(); // persists ; TODO now or later ??
        
        // Parse the updated document here or in the listener ?? Not here, just trigger a resourceDownloaded event
        EventProducer eventProducer;
        try {
            eventProducer = Framework.getService(EventProducer.class);
        } catch (Exception ex) {
            throw new ClientException("Cannot get EventProducer", ex);
        }
 
        DocumentEventContext ctx = new DocumentEventContext(coreSession, coreSession.getPrincipal(), documentToUpdate);
        //ctx.setProperty("myprop", "something"); // TODO any property to set ???
 
        Event event = ctx.newEvent("resourceDownloaded");
        try {
            eventProducer.fireEvent(event);
        } catch (ClientException ex) {
            throw new ClientException("Cannot fire event", ex);            
        }        
        
    }
    
}

