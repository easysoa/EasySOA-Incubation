package org.easysoa.registry;

import java.io.Serializable;

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
        
        // TODO check isSoaNode() ??
        
        DocumentModel previousDocumentModel = (DocumentModel) documentContext.getProperty("previousDocumentModel");
        // TODO check matching properties changes, in order to match only if they changed ??
        // TODO compute spnode props if subproject changed ??? NO would require to save (which would trigger event loop)
        
        match(documentManager, sourceDocument);
    }
    
    
    private void match(CoreSession documentManager, DocumentModel sourceDocument) throws ClientException {    
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
        
        // Service impl: Link to information service and endpoints
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), ServiceImplementation.DOCTYPE)
                // NOT for placeholder impls, else when saved loops on finding matching endpoints
                // TODO better : lock placeholder impls, merge placeholder impls... 
                && ((Boolean) sourceDocument.getPropertyValue(ServiceImplementation.XPATH_ISPLACEHOLDER)) != true) {
            DocumentModel serviceImpl = sourceDocument;
        	findAndMatchInformationService(documentManager, serviceMatchingService, serviceImpl);
        	
        	// notify compatible unmatched endpoints that they may have found a match
        	// (only required if impls not versioned, since impls should have been discovered before endpoints)
            DocumentModelList foundEndpoints = endpointMatchingService.findEndpointsCompatibleWithImplementation(documentManager, serviceImpl);
            if (foundEndpoints.size() > 0) {
                for (DocumentModel foundEndpoint : foundEndpoints) {
                    findAndMatchServiceImplementation(documentManager, documentService, endpointMatchingService, foundEndpoint);
                }
            }
            
            // NB. created placeholder impls :
            // - can't be matched by existing endpoints (since they already have impls, if placeholder ones)
            // - can't be further merged here since they are only created if no existing matching impl
            // - will have to be merged when new impls appear
        }

        // Information service: Find matching serviceimpls
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), InformationService.DOCTYPE)) {
            // notify compatible unmatched serviceImpls that they may have found a match
        	DocumentModelList foundServiceImpls = serviceMatchingService.findImplementationsCompatibleWithService(documentManager, sourceDocument);
        	if (foundServiceImpls.size() > 0) {
            	for (DocumentModel serviceImpl : foundServiceImpls) {
            		findAndMatchInformationService(documentManager, serviceMatchingService, serviceImpl);
            	}
        	}
        	
            // TODO notify compatible unmatched endpoints without impl that they may have found a match through aplaceholder
            // (only required if ISes (and impls) are not versioned, since IS should have been defined before endpoints)
        	
        	// TODO merge "placeholder" (i.e. created from source) service with Specifications one when
        	// referred Specifications change (add (remove) updateToVersion) 
        }
        
	}

	private void findAndMatchServiceImplementation(CoreSession documentManager,
			DocumentService docService, EndpointMatchingService endpointMatchingService,
			DocumentModel endpointDocument) throws ClientException {
	    if (endpointMatchingService.isEndpointAlreadyMatched(endpointDocument, documentManager)) {
	        return;
	    }
	    
		DocumentModelList foundImpls = endpointMatchingService.findServiceImplementations(
				documentManager, endpointDocument, null, false, true);
		if (foundImpls.size() == 1) {
			try { // endpointMatchingService IMPROVE THAT called 4 times when links endpoints : endpoint created, impl modified, ..., endpoint proxy created
			    endpointMatchingService.linkServiceImplementation(documentManager,
						docService.createSoaNodeId(endpointDocument),
						docService.createSoaNodeId(foundImpls.get(0)), true);
			    // TODO if impl still unmatched and endpoint enriches impl with platform metas, trigger again IS match
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
		else { //if (foundImpls.size() == 0) { // TODO better : find IS only among foundImpls if any
			DocumentModelList foundIS = endpointMatchingService.findInformationServices(
					documentManager, endpointDocument, null, true);
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
				documentManager, implDocument, null, false, true);
		if (foundInformationServices.size() == 1) {
		    serviceMatchingService.linkInformationService(documentManager, implDocument,
					foundInformationServices.get(0).getId(), true);
		}
	}

}
