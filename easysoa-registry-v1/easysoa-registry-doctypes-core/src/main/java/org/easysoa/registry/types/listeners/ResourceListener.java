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

package org.easysoa.registry.types.listeners;

import org.apache.log4j.Logger;
import org.easysoa.registry.SoaMetamodelService;
import org.easysoa.registry.dbb.ProbeConfUtil;
import org.easysoa.registry.dbb.ResourceDownloadService;
import org.easysoa.registry.dbb.ResourceParsingService;
import org.easysoa.registry.dbb.ResourceUpdateService;
import org.easysoa.registry.types.Resource;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.common.collections.ScopeType;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Triggers SOA resource download & parse as required.
 *
 * Should be registered on events : documentCreated, documentModified
 * (and not beforeDocumentModification because digest not yet computed at storage level)
 * 
 * @author jguillemotte
 */
public class ResourceListener implements EventListener {
	
    private static final String CONTEXT_REQUEST_RESOURCE_ALREADY_UPDATED = "resourceAlreadyUpdated";

    private static Logger logger = Logger.getLogger(ResourceListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
        
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
           return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        
        // Prevent looping on source document changes
        // (maybe ex. setting downloaded resource blob)
        if (context.hasProperty(CONTEXT_REQUEST_RESOURCE_ALREADY_UPDATED)) {
            return;
        }
        sourceDocument.getContextData().putScopedValue(ScopeType.REQUEST,
        		CONTEXT_REQUEST_RESOURCE_ALREADY_UPDATED, true);
        
        // NB. available if beforeDocumentModification event
        DocumentModel previousDocumentModel = (DocumentModel) context.getProperty("previousDocumentModel");        
        
        // if is an RDI (external, to be downloaded resource), update it
        if (isResourceDownloadInfo(sourceDocument)) { // TODO extract to isResourceDocument()
            
            // Get & use probe conf
            String probeType = (String) sourceDocument.getPropertyValue(ResourceDownloadInfo.XPATH_PROBE_TYPE);//TODO
            String probeInstanceId = (String) sourceDocument.getPropertyValue(ResourceDownloadInfo.XPATH_PROBE_INSTANCEID);//TODO
            // exit if document change but probe conf says it triggers using dedicated event
            if (ProbeConfUtil.isResourceProbeEventCustom(probeType, probeInstanceId)) {
                return;
            }
            ResourceDownloadService probeResourceDownloadService = ProbeConfUtil.getResourceDownloadService(probeType, probeInstanceId);
        
            // Starting update :
            try {
                ResourceUpdateService resourceUpdateService = Framework.getService(ResourceUpdateService.class);
                resourceUpdateService.updateResource(sourceDocument, previousDocumentModel,
                        sourceDocument, probeResourceDownloadService);
            }
            catch(Exception ex){
                logger.error("Error during the update", ex);
                throw new ClientException("Error during the update", ex);
            }
            
            // NB. parsing is triggered by (possibly async) resourceDownloaded event fired by resourceUpdateService
            
        } else if (isResource(sourceDocument)) {
            // TODO trigger event to call (WSDL) parsing listener
        }

        // TODO test it
        // TODO copy this file to ResourceListener & replace it in *listener.xml conf,
        // remove the following code from it, hook WsdlParsingListener rather on resourceDownloaded event in xml conf,
        // trigger resourceDownloaded event in ResourceUpdateService impl
    }

    /**
     * Is it a (downloadable, external) Resource that has to be downloaded before being parsed ?
     * @param docModel
     * @return
     * @throws ClientException 
     */
    private boolean isResourceDownloadInfo(DocumentModel docModel) throws ClientException {
        if (docModel.getFacets().contains(ResourceDownloadInfo.FACET_RESOURCEDOWNLOADINFO)) {
            // also checking that there is an url, else may be a resource with static RDI facet ex. iserv/endpoint
            String url = (String) docModel.getPropertyValue(ResourceDownloadInfo.XPATH_URL);
            return url != null && url.length() != 0;
        }
        return false;
    }

    /**
     * Is it an (internal) Resource that can be directly parsed ?
     * @param sourceDocument
     * @return true if Resource subtype or (HACK) iserv/endpoint (isWsdlDocumentType) with empty url
     */
    private boolean isResource(DocumentModel docModel) throws ClientException {
        //SoaMetamodelService soaMetaModelService = Framework.getService(SoaMetamodelService.class);
        //return soaMetaModelService.
        
        ResourceParsingService resourceParsingService;
        try {
            resourceParsingService = Framework.getService(ResourceParsingService.class);
        }
        catch(Exception ex){
            throw new ClientException(ex);
        }

        if (Resource.DOCTYPE.equals(docModel.getType())){
            return true;
        } else if(resourceParsingService.isWsdlDocumentType(docModel.getType())){
            String url = (String)docModel.getPropertyValue(ResourceDownloadInfo.XPATH_URL);
            if(url == null || url.length() == 0){
                return true;
            }
        }    
        return false;
    }

}
