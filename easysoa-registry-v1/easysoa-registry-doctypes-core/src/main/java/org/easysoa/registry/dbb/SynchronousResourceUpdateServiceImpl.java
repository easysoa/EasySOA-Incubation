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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
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
        if(newTimestamp != null && oldTimestamp != null
                && newTimestamp.length() != 0 && newTimestamp.equals(oldTimestamp)) { // no date parsing but checking it is not "dumb"
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
        
        if(newUrl == null || newUrl.length() == 0){
            // No URL, happens on document without RDI (yet) but with static
            // RDI facet (ex. iserv/endpoint) (TODO should have been done in resourceListener),
            // or maybe none known yet (??)
            // For test only, to remove
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
        // TODO : replace resourceFile.getName by a method computing the name from the url
        //Blob resourceBlob = FileUtils.createSerializableBlob(file, resourceFile.getName(), null);
        Blob resourceBlob = FileUtils.createSerializableBlob(file, getWsdlFileName(newUrl), null);
        documentToUpdate.setProperty("file", "content", resourceBlob);
        
        // Compute timestamp
        /*Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ResourceDownloadInfo.TIMESTAMP_DATETIME_PATTERN);
        String formattedDate = sdf.format(date);*/
        //documentToUpdate.setPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP, formattedDate);
        
        //coreSession.saveDocument(documentToUpdate); // updates & triggers events ; TODO now or later ??
        coreSession.save(); // persists ; TODO now or later ??
        
        // Fire event
        fireResourceDownloadedEvent(documentToUpdate);

    }

    // TODO : complete, improve this method !!
    /**
     * Build a file name for WSDL file from the URL
     * @param url The url
     * @return The file name
     */
    private String getWsdlFileName(String url){
        String fileName = url.substring(url.lastIndexOf("/")).replace("?", ".");
        return fileName;
    }
    
    @Override
    public void fireResourceDownloadedEvent(DocumentModel document) throws ClientException {
        // Trigger a resourceDownloaded event
        EventProducer eventProducer;
        try {
            eventProducer = Framework.getService(EventProducer.class);
        } catch (Exception ex) {
            throw new ClientException("Cannot get EventProducer", ex);
        }
 
        DocumentEventContext ctx = new DocumentEventContext(document.getCoreSession(), document.getCoreSession().getPrincipal(), document);
        //ctx.setProperty("myprop", "something"); // TODO any property to set ???
 
        Event event = ctx.newEvent("resourceDownloaded");
        try {
            eventProducer.fireEvent(event);
        } catch (ClientException ex) {
            throw new ClientException("Cannot fire event", ex);
        }                
    }
    
}

