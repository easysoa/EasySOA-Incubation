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
import org.easysoa.registry.dbb.ProbeConfUtil;
import org.easysoa.registry.dbb.ResourceDownloadService;
import org.easysoa.registry.dbb.ResourceUpdateService;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class ResourceListener implements EventListener {

    private static Logger logger = Logger.getLogger(ResourceListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
        
        EventContext context = event.getContext();
        // TODO needed ?
        /*if (!(context instanceof DocumentEventContext)) {
            return;
        }*/       
        
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        
        // NB. available if beforeDocumentModification event
        DocumentModel previousDocumentModel = (DocumentModel) context.getProperty("previousDocumentModel");        
        
        if (sourceDocument.getFacets().contains(ResourceDownloadInfo.FACET_RESOURCEDOWNLOADINFO)) { // TODO extract to isResourceDocument()
            
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
        }
        
        // TODO test it
        // TODO copy this file to ResourceListener & replace it in *listener.xml conf,
        // remove the following code from it, hook WsdlParsingListener rather on resourceDownloaded event in xml conf,
        // trigger resourceDownloaded event in ResourceUpdateService impl        
    }

}
