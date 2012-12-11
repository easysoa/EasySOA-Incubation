package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
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
public class SoaNodeMatchingListener implements EventListener {

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
		DocumentService documentService;
		ServiceMatchingService serviceMatchingService;
		EndpointMatchingService endpointMatchingService;
		try {
			documentService = Framework.getService(DocumentService.class);
			serviceMatchingService = Framework.getService(ServiceMatchingService.class);
			endpointMatchingService = Framework.getService(EndpointMatchingService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return;
		}

        // Endpoint: Link to service implementation
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), Endpoint.DOCTYPE)) {
        	findAndMatchServiceImplementation(documentManager, documentService, endpointMatchingService, sourceDocument);
        }
        
        // Service impl: Link to information service
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), ServiceImplementation.DOCTYPE)) {
        	findAndMatchInformationService(documentManager, serviceMatchingService, sourceDocument);
        }

        // Information service: Find matching serviceimpls
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), InformationService.DOCTYPE)) {
        	DocumentModelList foundServiceImpls = serviceMatchingService.findServiceImplementations(documentManager, sourceDocument);
        	if (foundServiceImpls.size() > 0) {
            	for (DocumentModel serviceImpl : foundServiceImpls) {
            		findAndMatchInformationService(documentManager, serviceMatchingService, serviceImpl);
            	}
        	}
        }
        
	}

	private void findAndMatchServiceImplementation(CoreSession documentManager,
			DocumentService docService, EndpointMatchingService endpointMatchingService,
			DocumentModel endpointDocument) throws ClientException {
	    if (endpointMatchingService.isEndpointAlreadyMatched(endpointDocument, documentManager)) {
	        return;
	    }
	    
		DocumentModelList foundImpls = endpointMatchingService.findServiceImpls(
				documentManager, endpointDocument, null, false);
		if (foundImpls.size() == 1) {
			try { // endpointMatchingService IMPROVE THAT called 4 times when links endpoints : endpoint created, impl modified, ..., endpoint proxy created
			    endpointMatchingService.linkServiceImplementation(documentManager,
						docService.createSoaNodeId(endpointDocument),
						docService.createSoaNodeId(foundImpls.get(0)), true);
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
		else { //if (foundImpls.size() == 0) { // TODO better : find IS only among foundImpls if any
			DocumentModelList foundIS = endpointMatchingService.findInformationServices(
					documentManager, endpointDocument, null);
			if (foundIS.size() == 1) {
				try {
				    endpointMatchingService.linkInformationServiceThroughPlaceholder(documentManager,
					        endpointDocument, foundIS.get(0), true);
				}
				catch (Exception e) {
					logger.error(e);
				}
			}
		}
		
	}

    private void findAndMatchInformationService(CoreSession documentManager,
			ServiceMatchingService serviceMatchingService, DocumentModel implDocument)
			throws ClientException {
	    if (serviceMatchingService.isServiceImplementationAlreadyMatched(implDocument)) {
	        return;
	    }
	    
		DocumentModelList foundInformationServices = serviceMatchingService.findInformationServices(
				documentManager, implDocument, null, false);
		if (foundInformationServices.size() == 1) {
		    serviceMatchingService.linkInformationService(documentManager, implDocument,
					foundInformationServices.get(0).getId(), true);
		}
	}

}
