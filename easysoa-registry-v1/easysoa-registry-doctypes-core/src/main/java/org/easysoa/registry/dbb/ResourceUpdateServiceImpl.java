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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.GregorianCalendar;

import org.easysoa.registry.types.ResourceDownloadInfo;
import org.easysoa.registry.types.listeners.EventListenerBase;
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
 * Impl of Resource Update Service that downloads Resource synchronously
 * and then triggers its parsing through (a priori synchronous) event
 *
 * @author jguillemotte
 */
public abstract class ResourceUpdateServiceImpl implements ResourceUpdateService  {

    /** triggers parsing */
    public static final String RESOURCE_DOWNLOADED_EVENT = "resourceDownloaded";

    @Override
    public boolean isNewResourceRetrieval(DocumentModel newRdi, DocumentModel oldRdi) throws ClientException {

        if (oldRdi == null) {
            return true;
        }

        // Check if the rdi.timestamp is not null and doesn't differ ??
        //String url = (String) newRdi.getProperty(ResourceDownloadInfo.XPATH_ACTUAL_URL);
        // NB. if url differs, timestamp should change
        GregorianCalendar newTimestamp = (GregorianCalendar) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP);
        GregorianCalendar oldTimestamp = (GregorianCalendar) oldRdi.getPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP);
        if(newTimestamp != null && oldTimestamp != null
                && newTimestamp.equals(oldTimestamp)) { // no date parsing but checking it is not "dumb"
            return false;
        }
        return true;
    }

    /**
     * To be called by implementations
     * @param newRdi
     * @param oldRdi
     * @param documentToUpdate
     * @param resourceDownloadService
     * @throws ClientException
     */
    protected void doUpdateResourceAndFireEvent(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate) throws ClientException {

    	documentToUpdate = this.doUpdateResource(newRdi, oldRdi, documentToUpdate);
    	
        this.fireResourceDownloadedEvent(documentToUpdate); // firing on persisted document (else no SQLBlob so no digest)
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

        DocumentEventContext ctx = new DocumentEventContext(document.getCoreSession(),
        		document.getCoreSession().getPrincipal(), document);

        Event event = ctx.newEvent(RESOURCE_DOWNLOADED_EVENT);
        try {
            eventProducer.fireEvent(event);
        } catch (ClientException ex) {
            throw new ClientException("Cannot fire event", ex);
        }
    }

    protected DocumentModel doUpdateResource(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate) throws ClientException {

        // Check if update is needed
        if (!isNewResourceRetrieval(newRdi, oldRdi)) {
            return documentToUpdate;
        }
        
        ResourceDownloadService resourceDownloadService = this.getProbeResourceDownloadService(newRdi);

        CoreSession coreSession = documentToUpdate.getCoreSession();

        // Get the new resource
        String newUrl = (String) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_URL);

        if(newUrl == null || newUrl.length() == 0){
            // No URL, happens on document without RDI (yet) but with static
            // RDI facet (ex. iserv/endpoint) (TODO should have been done in resourceListener),
            // or maybe none known yet (??)
            // For test only, to remove
            //newUrl = "http://footballpool.dataaccess.eu/data/info.wso?WSDL";
            return documentToUpdate;
        }
        ResourceDownloadInfo newRdiBean = new ResourceDownloadInfoImpl();
        newRdiBean.setUrl(newUrl);
        // TODO set other props
        // TODO LATER rather as adapter ?
        InputStream file;
        try {
            newRdiBean = resourceDownloadService.get(newRdiBean);
            file = new FileInputStream(newRdiBean.getFile());
        } catch (MalformedURLException ex) {
            throw new ClientException("Bad URL : " + newUrl, ex);
        } catch (Exception ex) {
            throw new ClientException("Error downloading " + newUrl, ex);
        }

        // Update registry with new resource
        Blob resourceBlob = FileUtils.createSerializableBlob(file, getWsdlFileName(newUrl), null);
        documentToUpdate.setPropertyValue("file:content", (Serializable) resourceBlob);
        documentToUpdate.setPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP,
                newRdiBean.getTimestamp()); // NB. nuxeo auto-converts it to GregorianCalendar (xs:dateTime)

        // saving :
        // first disabling events (the only one to be triggered is resourceDownloaded event,
        // but it's fired explicitly below)
        EventListenerBase.disableListeners(documentToUpdate);
        documentToUpdate = coreSession.saveDocument(documentToUpdate); // updates (& triggers events),
        // else Blob stays a StreamingBlob instead of becoming an SQLBlob and can't compute the new digest
        EventListenerBase.enableListeners(documentToUpdate); // reenabling them for resourceDownloaded event
        // NB. coreSession.save() is not required (and should not since when async save is done transactionnaly in Work) 
        
        return documentToUpdate; // returning persisted document
    }

    /**
     * Build a file name for WSDL file from the URL
     * @param url The url
     * @return The file name
     */
    public String getWsdlFileName(String url){
        String fileName = url.substring(url.lastIndexOf("/")+1).replace("?", ".");
        if(!fileName.toLowerCase().endsWith(".wsdl")){
            fileName = fileName.concat(".wsdl");
        }
        return fileName;
    }

    @Override
    public ResourceDownloadService getProbeResourceDownloadService(DocumentModel newRdi) throws ClientException {
        // Get & use probe conf
        String probeType = (String) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_PROBE_TYPE);//TODO
        String probeInstanceId = (String) newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_PROBE_INSTANCEID);//TODO

        ResourceDownloadService probeResourceDownloadService = ProbeConfUtil.getResourceDownloadService(probeType, probeInstanceId);
        return probeResourceDownloadService;
    }
    
}
