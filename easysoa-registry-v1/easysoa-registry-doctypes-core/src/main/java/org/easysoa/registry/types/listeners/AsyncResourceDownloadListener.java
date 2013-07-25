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

package org.easysoa.registry.types.listeners;

import org.apache.log4j.Logger;
import org.easysoa.registry.dbb.SyncResourceUpdateService;
import org.easysoa.registry.dbb.WorkBasedAsyncResourceUpdateServiceImpl;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;


/**
 * Used by EventBasedAsyncResourceUpdateServiceImpl to do the download of SOA
 * resources & triggers parsing as required.
 * 
 * Must be triggered in async (which requires to implement PostCommitEventListener
 * and postCommit="true", see EventListenerDescriptor.java) manner by Nuxeo's event system, see
 * http://doc.nuxeo.com/display/NXDOC/Events+and+Listeners
 * (though it might also be called synchronously)
 *
 * Should be registered with async & postCommit on event : readyToDownload fired by AsyncResourceUpdateServiceImpl
 *
 * @author mdutoo
 */
public class AsyncResourceDownloadListener extends EventListenerBase
        implements PostCommitEventListener {

    private static Logger logger = Logger.getLogger(AsyncResourceDownloadListener.class);

    
    /**
     * Simplistic async / postcommit impl that dispatches to handleEvent(event)
     * @param events
     * @throws ClientException
     */
    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            try {
                this.handleEvent(event);
            } catch (Exception e) {
                logger.error("Error", e);
            }
        }
        
    }

    @Override
    public void handleEvent(Event event) throws ClientException {

        // NB. not checking event because only registered on readyToDownload
        
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
           return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();

        // NB. available if beforeDocumentModification event
        DocumentModel oldRdi = (DocumentModel) context.getProperty(WorkBasedAsyncResourceUpdateServiceImpl.OLD_RDI_EVENT_PROP);
        DocumentModel newRdi = (DocumentModel) context.getProperty(WorkBasedAsyncResourceUpdateServiceImpl.NEW_RDI_EVENT_PROP);

        SyncResourceUpdateService syncResourceUpdateService = Framework.getLocalService(SyncResourceUpdateService.class);

        // Update resource & fire event
        syncResourceUpdateService.updateResource(newRdi, oldRdi, sourceDocument);
    }

}
