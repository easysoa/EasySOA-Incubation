package org.easysoa.registry;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.registry.systems.IntelligentSystemTreeService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.RepositoryHelper;
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
            return; // nothing to do on non SOA nodes
        }

        if (sourceDocument.isVersion()) {
            logger.warn("RepositoryManagementListener : skipping because isVersion (but not checked out nor isBeingVersionedSubprojectNodeEvent())"
                    //+ ", maybe isBeingVersionedSubprojectNodeEvent() " + SubprojectServiceImpl.isBeingVersionedSubprojectNodeEvent(event, sourceDocument)
                    + ", event " + event.getName());
            return; // nothing can be done on it since it is a version, internal proxies
            // are handled by tree snapshot itself (TODO nuxeo ID properties), and
            // outside references to it can only change through explicit action (updateToVersion)
        }
        
        if (SubprojectServiceImpl.isBeingVersionedSubprojectNodeEvent(event, sourceDocument)) {
            logger.warn("RepositoryManagementListener : skipping because isBeingVersionedSubprojectNodeEvent " + sourceDocument
                    + ", event " + event.getName());
            return; // this document is currently being tree snapshotted, do nothing here
        }

        if (sourceDocument.isProxy()) {
            // getting target document of proxy :
            DocumentModel proxyTargetDocument = documentContext.getCoreSession().getSourceDocument(sourceDocument.getRef());
            // NB. same as : proxyTargetDocument = coreSession.getDocument(new org.nuxeo.ecm.core.api.IdRef(sourceDocument.getSourceId()));
            // NB. DON'T USE coreSession.getWorkingCopy(sourceDocument.getRef()) because it never returns a version !
            if (proxyTargetDocument.isVersion()) {
                logger.warn("RepositoryManagementListener : skipping because is live proxy of version (but not isCheckedOut() nor isBeingVersionedSubprojectNodeEvent()) " + sourceDocument
                        //+ ", maybe isCheckedOut " + sourceDocument.isCheckedOut() + " "
                        //+ " or isBeingVersionedSubprojectNodeEvent() " + SubprojectServiceImpl.isBeingVersionedSubprojectNodeEvent(event, sourceDocument)
                        + ", event " + event.getName());
                return; // nothing can be done on it since it is a version, internal proxies
                // are handled by tree snapshot itself (TODO nuxeo ID properties), and
                // outside references to it can only change through explicit action (updateToVersion)
            }
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
        boolean documentModified = false;
        
        boolean isCreationOnCheckinMode = "ProjetWebServiceImpl".equals(sourceDocument.getPropertyValue("dc:title")); // isJwt(OrCmis)
        boolean isCreationOnCheckin = isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CHECKEDIN.equals(event.getName());
        boolean isCreation = isCreationOnCheckin
        		|| !isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName());

        if (DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
        	try {
	        	metamodelService.validateIntegrity(sourceDocument, false); // TODO [soaname change]
			} catch (ModelIntegrityException e) {
				logger.error("Aborting repository management on " + sourceDocument.getTitle() + ": " + e.getMessage());
				return;
			}
        }
        
        if (isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
        	sourceDocument.setPropertyValue("dc:description", "jwtJustCreated");
        	documentModified = true;
        } else
        
        if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) // not a remove
        		&& (!isCreationOnCheckinMode || isCreationOnCheckin && "jwtJustCreated".equals(sourceDocument.getPropertyValue("dc:description")))) { // not a cmis creation (event handling reported at cmis checkin that will come next)
        	if (isCreationOnCheckinMode && "jwtJustCreated".equals(sourceDocument.getPropertyValue("dc:description"))) {
        		sourceDocument.setPropertyValue("dc:description", "done"); // cleaning
                documentModified = true;
        	}
        	
	        try {
	    		// Validate or set soaname
				String newSoaName = metamodelService.validateIntegrity(sourceDocument,
	        			DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName()));
	        	if (newSoaName != null) {
	        		sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, newSoaName); // TODO [soaname change]
	                documentModified = false;//true
	                sourceDocument = documentManager.saveDocument(sourceDocument);//TODO required ?? when ?
	        	}
	        	
	            // Working copy/proxies management
	            sourceDocument = manageRepositoryStructure(documentManager, documentService, sourceDocument);
	            documentManager.save(); // required, else loops on classifySoaNode() within
	            // intelligentSystemTreeServiceCache.handleDocumentModel() below

	    		// Intelligent system trees update
	    		IntelligentSystemTreeService intelligentSystemTreeServiceCache =
	    		        Framework.getService(IntelligentSystemTreeService.class);
	    		intelligentSystemTreeServiceCache.handleDocumentModel(documentManager, sourceDocument,
	    		        !isCreation); // creates System tree or proxy or moves sourceDocument, but doesn't in itself change the sourceDocument (?)
	        } catch (ModelIntegrityException e) {
				String message = "Rollback needed because of integrity issue on " + sourceDocument.getTitle();
				logger.error(message + ": " + e.getMessage());
				event.markRollBack();
				throw new ModelIntegrityClientException(message, e);
			} catch (Exception e) {
	            logger.error("Failed to check document after creation", e);
	        }
        }
        
        if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) || sourceDocument.isProxy()) {
        	if (DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName()) && sourceDocument.isProxy()) {
				// (TODO necessarily live) Proxy deleted, update the true document, TODO if it is live
				sourceDocument = documentManager.getSourceDocument(sourceDocument.getRef());
				if (sourceDocument.isVersion()) {
				    return;
				    // since source document is version, can't modify it ; but no need either,
				    // because in any way proxy had been created after the version was
				}
				documentModified = false; // TODO reset documentModified for now, may change again below
        	}
        	
    		// Update parents info
        	if (!DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
		    	try {
		    	    documentModified = updateParentIdsMetadata(documentManager, documentService, sourceDocument) || documentModified;
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
		    			// NB. isFacetSource = true so doesn't change the sourceDocument itself
		    		}
		    		else {
		    	        // Reset metadata after move/deletion
		    			if (DocumentEventTypes.DOCUMENT_MOVED.equals(event.getName())
		    					|| DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
		    			    // reset metadata if document is target of metadata inheritance (transfer.to)
		    			    documentModified = metamodelService.resetInheritedFacets(sourceDocument) || documentModified;
                            if (documentModified) {
                                documentManager.saveDocument(sourceDocument);
                                documentModified = false;
                            }
				        }
		    	        // Copy metadata from inherited facets from parents
		    			if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
		    				metamodelService.applyFacetInheritance(documentManager, sourceDocument, false);
					    	if (DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())
					    			|| DocumentEventTypes.DOCUMENT_MOVED.equals(event.getName())) {
					    	    if (documentModified) {
					    	        documentManager.saveDocument(sourceDocument);
	                                documentModified = false;
					    	    }
					    	}
		    			}
		    		}
	    		}
		        catch (Exception e) {
		        	logger.error("Failed to manage inherited facets of an SoaNode", e);
		        }
	    	}

    	}

        try {
            boolean isDirty = sourceDocument.isDirty();
            boolean isProxy = sourceDocument.isProxy();
            if (documentModified || isDirty && isProxy) {
                // NB. !isDirty happens when handleEvent loops (an RML save itself triggers a save)
                // and isDirty && !documentModified happens on a just created document (or proxy), must be saved else inherited facet metadata won't be updated when child (proxy) moved
                if (!sourceDocument.isDirty()) {
                    logger.warn("RepositoryManagerListener : sourceDocument modified but not dirty ?!?");
                    return;
                }
                documentManager.saveDocument(sourceDocument);
            }
            //logger.debug("RepositoryManagerListener : Doing final save (for updateParentIdsMetadata ?)");
        } catch (Exception e) {
            logger.error("RepositoryManagerListener : Failed to do final save (for updateParentIdsMetadata ?)", e);
        }
    }

    /**
     * Saves sourceDocument only if deleted after merge with corresponding existing document,
     * otherwise may move it but in itself doesn't change it (?).
     * Saves session if repository structure changed.
     * @param documentManager
     * @param documentService
     * @param sourceDocument
     * @return
     * @throws ClientException
     * @throws PropertyException
     * @throws Exception
     */
	private DocumentModel manageRepositoryStructure(CoreSession documentManager,
			DocumentService documentService, DocumentModel sourceDocument)
			throws ClientException, PropertyException, Exception {
	    boolean structureChanged = false;
		
		// If a document has been created through the Nuxeo UI, move it to the repository and leave only a proxy
		String sourceFolderPath = documentService.getSourceFolderPath(documentManager, sourceDocument);
		DocumentModel parentModel = documentManager.getDocument(sourceDocument.getParentRef());
		
		SoaNodeId soaNodeId = documentService.createSoaNodeId(sourceDocument);
		if (!sourceDocument.isProxy() && !parentModel.getPathAsString().equals(sourceFolderPath)
		        || sourceDocument.isProxy() && parentModel.hasSchema(SoaNode.SCHEMA)
		        && !sourceDocument.getPathAsString().startsWith(
		                RepositoryHelper.getRepositoryPath(documentManager,
		                        (String) parentModel.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT)))) {
            //&& !sourceDocument.getPathAsString().startsWith(
            //        RepositoryHelper.getRepositoryPath(documentManager, soaNodeId.getSubprojectId()))) {
		    
		    documentService.getSourceFolder(documentManager, sourceDocument); // ensuring it exists
		    
		    DocumentModel repositoryDocument = documentService.find(documentManager, soaNodeId);
		    if (repositoryDocument != null) {
		    	if (!repositoryDocument.getRef().equals(sourceDocument.getRef())) {
			        // If the source document already exists, only keep one
			        repositoryDocument.copyContent(sourceDocument); // Merge
			        documentManager.removeDocument(sourceDocument.getRef());
			        documentManager.saveDocument(repositoryDocument);
			        documentManager.save();//TODO required here ?
			        structureChanged = false;
			        sourceDocument = repositoryDocument;
		    	}
		    }
		    else {
		        // Move to repository otherwise
		    	sourceDocument = documentManager.move(sourceDocument.getRef(),
		            new PathRef(sourceFolderPath), sourceDocument.getName());
	            structureChanged = true;
		    }
		    
		    // Create a proxy at the expected location
		    if (documentService.isSoaNode(documentManager, parentModel.getType())) {
		        parentModel = documentService.find(documentManager, documentService.createSoaNodeId(parentModel));
		    }
		    documentManager.createProxy(sourceDocument.getRef(), parentModel.getRef());
		    structureChanged = true;
		}
		if (structureChanged) {
		    documentManager.save();
		}
		
		return sourceDocument;
	}

	private boolean updateParentIdsMetadata(CoreSession documentManager,
			DocumentService documentService, DocumentModel sourceDocument) throws Exception {
		DocumentModelList parentModels = documentService.findAllParents(documentManager, sourceDocument);
		SoaNode sourceSoaNode = sourceDocument.getAdapter(SoaNode.class);
		boolean changed = false;
		DocumentModelList soaNodeParentModels = new DocumentModelListImpl();
		Iterator<SoaNodeId> oldParentIdIt = sourceSoaNode.getParentIds().iterator();
		for (DocumentModel parentModel : parentModels) {
			if (documentService.isSoaNode(documentManager, parentModel.getType())) {
				soaNodeParentModels.add(parentModel);
				if (!oldParentIdIt.hasNext() || !parentModel.equals(oldParentIdIt.next())) {
				    changed = true;
				}
			}
		}
		if (changed) {
		    sourceSoaNode.setParentIds(documentService.createSoaNodeIds(soaNodeParentModels.toArray(new DocumentModel[]{})));
		}
		return changed;
	}

}
