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
