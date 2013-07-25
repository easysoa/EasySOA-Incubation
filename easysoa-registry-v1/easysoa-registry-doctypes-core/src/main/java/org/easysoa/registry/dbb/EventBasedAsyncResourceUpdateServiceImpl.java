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

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Async Resource Update impl that triggers a custom async event
 * to achieve asynchronicity.
 *
 * @author mdutoo
 */
public class EventBasedAsyncResourceUpdateServiceImpl extends ResourceUpdateServiceImpl implements AsyncResourceUpdateService {

    /** triggers async download */
    public static final String READY_TO_DOWNLOAD_RESOURCE_EVENT = "readyToDownloadResource";

    public static final String OLD_RDI_EVENT_PROP = "oldRdi";
    public static final String NEW_RDI_EVENT_PROP = "newRdi";
    
    private static Logger logger = Logger.getLogger(EventBasedAsyncResourceUpdateServiceImpl.class);

    @Override
    public void updateResource(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate) throws ClientException {

        // Check if update is needed
        if (!isNewResourceRetrieval(newRdi, oldRdi)) {
            // may happen when document Resource saved at parsing time
            return;
        }
        
        fireReadyToDownloadResourceEvent(newRdi, oldRdi, documentToUpdate);
    }
    
    public void fireReadyToDownloadResourceEvent(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel document) throws ClientException {
        // Trigger a resourceDownloaded event
        EventProducer eventProducer;
        try {
            eventProducer = Framework.getService(EventProducer.class);
        } catch (Exception ex) {
            throw new ClientException("Cannot get EventProducer", ex);
        }

        DocumentEventContext ctx = new DocumentEventContext(document.getCoreSession(),
                document.getCoreSession().getPrincipal(), document);
        ctx.setProperty(OLD_RDI_EVENT_PROP, oldRdi);
        ctx.setProperty(NEW_RDI_EVENT_PROP, newRdi);

        Event event = ctx.newEvent(READY_TO_DOWNLOAD_RESOURCE_EVENT);
        try {
            eventProducer.fireEvent(event);
        } catch (ClientException ex) {
            throw new ClientException("Cannot fire event", ex);
        }
    }

}
