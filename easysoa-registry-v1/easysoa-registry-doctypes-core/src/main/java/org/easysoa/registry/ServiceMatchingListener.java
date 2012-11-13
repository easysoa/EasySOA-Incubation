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
            if (sourceDocument.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE) == null) {
            	SoaNodeId serviceImplId = documentService.createSoaNodeId(sourceDocument);
            	String[] splitName = serviceImplId.getName().split("\\=");
            	String infoServiceQuery = NXQLQueryBuilder.getQuery("SELECT * FROM " + InformationService.DOCTYPE + 
            			" WHERE " + InformationService.XPATH_SOANAME + " = ?",
            			new Object[] { splitName[0] },
            			true, true);
            	DocumentModelList foundInfoServices = documentService.query(documentManager,
            			infoServiceQuery, true, false);
            	if (foundInfoServices.size() > 0) {
            		sourceDocument.setPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE,
            				foundInfoServices.get(0).getId());
            		if (!event.getName().equals(DocumentEventTypes.BEFORE_DOC_UPDATE)) {
            			documentManager.saveDocument(sourceDocument);
            			documentManager.save();
            		}
            	}
            }
        }

        // Information service: Find matching serviceimpls
        if (documentService.isTypeOrSubtype(documentManager, sourceDocument.getType(), InformationService.DOCTYPE)) {
        	String serviceImplQuery = NXQLQueryBuilder.getQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE + 
        			" WHERE " + ServiceImplementation.XPATH_SOANAME + " LIKE ?",
        			new Object[] { documentService.createSoaNodeId(sourceDocument).getName() + '%' },
        			true, true);
        	DocumentModelList foundServiceImpls = documentService.query(documentManager,
        			serviceImplQuery, true, false);
        	if (foundServiceImpls.size() > 0) {
            	for (DocumentModel serviceImpl : foundServiceImpls) {
            		serviceImpl.setPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE, sourceDocument.getId());
            		documentManager.saveDocument(serviceImpl);
            	}
    			documentManager.save();
        	}
        }
        
	}

}
