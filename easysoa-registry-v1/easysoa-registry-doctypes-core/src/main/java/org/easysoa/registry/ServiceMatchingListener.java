package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ServiceMatchingListener implements EventListener {

    private static Logger logger = Logger.getLogger(ServiceMatchingListener.class);
    
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
		DocumentService documentService;
		ServiceMatchingService matchingService;
		try {
			documentService = Framework.getService(DocumentService.class);
			matchingService = Framework.getService(ServiceMatchingService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return;
		}
        if (sourceDocument.hasSchema(SoaNode.SCHEMA)) {
        	documentService.checkSoaName(sourceDocument);
        }

        // Service impl: Link to information service
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), ServiceImplementation.DOCTYPE)) {
        	findAndMatchInformationService(documentManager, matchingService, sourceDocument);
        }

        // Information service: Find matching serviceimpls
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), InformationService.DOCTYPE)) {
        	DocumentModelList foundServiceImpls = matchingService.findServiceImplementations(documentManager, sourceDocument);
        	if (foundServiceImpls.size() > 0) {
            	for (DocumentModel serviceImpl : foundServiceImpls) {
            		findAndMatchInformationService(documentManager, matchingService, serviceImpl);
            	}
        	}
        }
        
	}

	private void findAndMatchInformationService(CoreSession documentManager,
			ServiceMatchingService matchingService, DocumentModel sourceDocument)
			throws ClientException {
		DocumentModelList foundInformationServices = matchingService.findInformationServices(
				documentManager, sourceDocument, null, false);
		if (foundInformationServices.size() > 0) {
			matchingService.linkInformationService(documentManager, sourceDocument,
					foundInformationServices.get(0).getId(), true);
		}
	}

}
