package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
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
		try {
			documentService = Framework.getService(DocumentService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return;
		}

        // Service impl: Link to information service
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), ServiceImplementation.DOCTYPE)) {
        	lookForAnInformationServiceToMatch(documentManager, documentService, sourceDocument, 
        			!event.getName().equals(DocumentEventTypes.BEFORE_DOC_UPDATE));
        }

        // Information service: Find matching serviceimpls
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), InformationService.DOCTYPE)) {
        	String serviceImplQuery = NXQLQueryBuilder.getQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE + 
        			" WHERE " + ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME + " = '?'",
        			new Object[] { sourceDocument.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME) },
        			false, true);
        	DocumentModelList foundServiceImpls = documentService.query(documentManager,
        			serviceImplQuery, true, false);
        	if (foundServiceImpls.size() > 0) {
        		if (event.getName().equals(DocumentEventTypes.BEFORE_DOC_UPDATE)) {
	        		documentManager.saveDocument(sourceDocument);
	        		documentManager.save();
        		}
            	for (DocumentModel serviceImpl : foundServiceImpls) {
            		lookForAnInformationServiceToMatch(documentManager, documentService, serviceImpl, true);
            	}
        	}
        }
        
	}

	private void lookForAnInformationServiceToMatch(CoreSession documentManager,
			DocumentService documentService, DocumentModel serviceImplModel, boolean save) throws ClientException {
    	if (serviceImplModel.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE) == null) {
        	String portTypeName = (String) serviceImplModel.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME);
        	String infoServiceQuery = NXQLQueryBuilder.getQuery("SELECT * FROM " + InformationService.DOCTYPE + 
        			" WHERE " + InformationService.XPATH_WSDL_PORTTYPE_NAME + " = ?",
        			new Object[] { portTypeName },
        			true, true);
        	DocumentModelList foundInfoServices = documentService.query(documentManager,
        			infoServiceQuery, true, false);
        	if (foundInfoServices.size() > 0) {
        		serviceImplModel.setPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE,
        				foundInfoServices.get(0).getId());
        		if (save) {
        			documentManager.saveDocument(serviceImplModel);
        			documentManager.save();
        		}
        	}
        }
	}

}
