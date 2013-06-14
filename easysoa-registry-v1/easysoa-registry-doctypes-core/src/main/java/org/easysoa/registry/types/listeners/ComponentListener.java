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
import org.easysoa.registry.SoaNodeMatchingListener;
import org.easysoa.registry.types.Component;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ComponentListener implements EventListener {

    private static Logger logger = Logger.getLogger(SoaNodeMatchingListener.class);
    
	@Override
	public void handleEvent(Event event) throws ClientException {
		// Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        CoreSession documentManager = documentContext.getCoreSession();
        
        if (Component.DOCTYPE.equals(sourceDocument.getType())
        		&& sourceDocument.getPropertyValue(Component.XPATH_COMPONENT_ID) == null
        		&& !sourceDocument.isVersion() && !sourceDocument.isProxy()) {
        	// Clone (working copy's) ID on a property of the ComponentData facet,
        	// so that it can be copied by facet inheritance
        	try {
        		sourceDocument.setPropertyValue(Component.XPATH_COMPONENT_ID, sourceDocument.getId());
        		if (DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
        			documentManager.saveDocument(sourceDocument);
        			documentManager.save();
        		}
			} catch (Exception e) {
				logger.error("Failed to clone Component ID to inheritable property: " + e.getMessage());
				return;
			}
        }
	}

}
