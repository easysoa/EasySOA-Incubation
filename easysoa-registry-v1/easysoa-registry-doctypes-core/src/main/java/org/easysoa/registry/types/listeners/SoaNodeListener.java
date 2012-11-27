package org.easysoa.registry.types.listeners;

import org.apache.log4j.Logger;
import org.easysoa.registry.ModelIntegrityException;
import org.easysoa.registry.SoaMetamodelService;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

public class SoaNodeListener implements EventListener {

    private static Logger logger = Logger.getLogger(SoaNodeListener.class);
    
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
        
        if (sourceDocument.hasSchema(SoaNode.SCHEMA)) {
	    	try {
	    		// Validate or set soaname
	        	SoaMetamodelService metamodelService = Framework.getService(SoaMetamodelService.class);
				String newSoaName = metamodelService.validateIntegrity(sourceDocument,
	        			DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName()));
	        	if (newSoaName != null) {
	        		sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, newSoaName);
	        		documentManager.saveDocument(sourceDocument);
	    			documentManager.save();
	        	}
			} catch (ModelIntegrityException e) {
				logger.error("Aborting soaname management on " + sourceDocument.getTitle() + ": " + e.getMessage());
				return;
			} catch (Exception e) {
				logger.error("Failed to manage soaname: " + e.getMessage());
			}
        }
        
	}

}
