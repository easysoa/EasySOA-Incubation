package org.easysoa.registry;

import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.registry.systems.IntelligentSystemTreeService;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
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
public class RepositoryManagementListener implements EventListener {

    private static Logger logger = Logger.getLogger(RepositoryManagementListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
    	
        // Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        if (!sourceDocument.hasSchema(SoaNode.SCHEMA)) {
            return;
        }
        
        // Initialize
        CoreSession documentManager = documentContext.getCoreSession();
        DocumentService documentService;
        SoaMetamodelService metamodelService;
        try {
			documentService = Framework.getService(DocumentService.class);
			metamodelService = Framework.getService(SoaMetamodelService.class);
		} catch (Exception e) {
			logger.error("A required service is missing, aborting SoaNode repository management: " + e.getMessage());
			return;
		}
        
        try {
        	metamodelService.validateIntegrity(sourceDocument);
		} catch (ModelIntegrityException e) {
			logger.error("Aborting repository management on " + sourceDocument.getTitle() + ": " + e.getMessage());
			return;
		}
        
        boolean isCreationOnCheckinMode = "ProjetWebServiceImpl".equals(sourceDocument.getPropertyValue("dc:title")); // isJwt(OrCmis)
        boolean isCreationOnCheckin = isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CHECKEDIN.equals(event.getName());
        boolean isCreation = isCreationOnCheckin
        		|| !isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName());
        
        if (isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
        	sourceDocument.setPropertyValue("dc:description", "jwtJustCreated");
        } else
        
        if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) // not a remove
        		&& (!isCreationOnCheckinMode || isCreationOnCheckin && "jwtJustCreated".equals(sourceDocument.getPropertyValue("dc:description")))) { // not a cmis creation (event handling reported at cmis checkin that will come next)
        	if (isCreationOnCheckinMode && "jwtJustCreated".equals(sourceDocument.getPropertyValue("dc:description"))) {
        		sourceDocument.setPropertyValue("dc:description", "done"); // cleaning
        	}
        	
	        try {
	            // Working copy/proxies management
	            sourceDocument = manageRepositoryStructure(documentManager, documentService, sourceDocument);

	    		// Intelligent system trees update
	    		IntelligentSystemTreeService intelligentSystemTreeServiceCache =
	    		        Framework.getService(IntelligentSystemTreeService.class);
	    		intelligentSystemTreeServiceCache.handleDocumentModel(documentManager, sourceDocument,
	    		        !isCreation);
	    		documentManager.save();
	        } catch (Exception e) {
	            logger.error("Failed to check document after creation", e);
	        }
        }
        
        if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) || sourceDocument.isProxy()) {
        	if (DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) && sourceDocument.isProxy()) {
				// Proxy deleted, update the true document
				sourceDocument = documentManager.getWorkingCopy(sourceDocument.getRef());
        	}
        	
    		// Update parents info
        	if (!DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
		    	try {
		        	updateParentIdsMetadata(documentManager, documentService, sourceDocument);
				} catch (Exception e) {
		        	logger.error("Failed to maintain parents information", e);
				}
		    }

	    	Set<String> inheritedFacets = metamodelService.getInheritedFacets(sourceDocument.getFacets());
	    	if (!inheritedFacets.isEmpty()) {
	    		try {
			        // Copy metadata from inherited facets to children
		    		if (DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
		    			metamodelService.applyFacetInheritance(documentManager, sourceDocument, true);
		    		}
		    		else {
		    	        // Reset metadata after move/deletion
		    			if (DocumentEventTypes.DOCUMENT_MOVED.equals(event.getName())
		    					|| DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
		    				metamodelService.resetInheritedFacets(sourceDocument);
		    				documentManager.saveDocument(sourceDocument);
				        }
		    	        // Copy metadata from inherited facets from parents
		    			if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
		    				metamodelService.applyFacetInheritance(documentManager, sourceDocument, false);
					    	if (DocumentEventTypes.DOCUMENT_MOVED.equals(event.getName())) {
			    				documentManager.saveDocument(sourceDocument);
					    	}
		    			}
		    		}
	    		}
		        catch (Exception e) {
		        	logger.error("Failed to manage inherited facets of an SoaNode", e);
		        }
	    	}

    	}
        
    }

	private DocumentModel manageRepositoryStructure(CoreSession documentManager,
			DocumentService documentService, DocumentModel sourceDocument)
			throws ClientException, PropertyException, Exception {
		
		// If a document has been created through the Nuxeo UI, move it to the repository and leave only a proxy
		String sourceFolderPath = documentService.getSourceFolderPath(sourceDocument.getType());
		DocumentModel parentModel = documentManager.getDocument(sourceDocument.getParentRef());
		if (!sourceDocument.isProxy() && !parentModel.getPathAsString().equals(sourceFolderPath)
		        || sourceDocument.isProxy() && parentModel.hasSchema(SoaNode.SCHEMA)
		            && !sourceDocument.getPathAsString().startsWith(Repository.REPOSITORY_PATH)) {
		    documentService.ensureSourceFolderExists(documentManager, sourceDocument.getType());
		    
		    // Build SoaNodeId
		    String soaName = (String) sourceDocument.getPropertyValue(SoaNode.XPATH_SOANAME);
		    if (soaName == null || soaName.isEmpty()) {
		        //sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, sourceDocument.getName());
		    	soaName = sourceDocument.getName();
		    }
		    SoaNodeId soaNodeId = new SoaNodeId(sourceDocument.getType(), soaName);
		    
		    DocumentModel repositoryDocument = documentService.find(documentManager, soaNodeId);
		    if (repositoryDocument != null) {
		        // If the source document already exists, only keep one
		        repositoryDocument.copyContent(sourceDocument); // Merge //
		        documentManager.saveDocument(repositoryDocument);//
		        documentManager.save();//
		        documentManager.removeDocument(sourceDocument.getRef());
		        //documentManager.save();
		        sourceDocument = repositoryDocument;
		    }
		    else {
		        // Move to repository otherwise
		        sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, soaName);
		    	sourceDocument = documentManager.move(sourceDocument.getRef(),
		            new PathRef(sourceFolderPath), sourceDocument.getName());
		    }
		    
		    // Create a proxy at the expected location
		    if (documentService.isSoaNode(documentManager, parentModel.getType())) {
		        parentModel = documentService.find(documentManager, documentService.createSoaNodeId(parentModel));
		    }
		    documentManager.createProxy(sourceDocument.getRef(), parentModel.getRef());
		}
		documentManager.save();
		
		return sourceDocument;
	}

	private void updateParentIdsMetadata(CoreSession documentManager,
			DocumentService documentService, DocumentModel sourceDocument) throws Exception {
		DocumentModelList parentModels = documentService.findAllParents(documentManager, sourceDocument);
		SoaNode sourceSoaNode = sourceDocument.getAdapter(SoaNode.class);
		DocumentModelList soaNodeParentModels = new DocumentModelListImpl();
		for (DocumentModel parentModel : parentModels) {
			if (documentService.isSoaNode(documentManager, parentModel.getType())) {
				soaNodeParentModels.add(parentModel);
			}
		}
		sourceSoaNode.setParentIds(documentService.createSoaNodeIds(soaNodeParentModels.toArray(new DocumentModel[]{})));
	}

}
