package org.easysoa.registry;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.registry.systems.IntelligentSystemTreeService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.types.listeners.EventListenerBase;
import org.easysoa.registry.utils.RepositoryHelper;
import org.nuxeo.common.collections.ScopeType;
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
public class RepositoryManagementListener extends EventListenerBase implements EventListener {
	
	private static final String CONTEXT_REQUEST_REPOSITORY_MANAGEMENT_LOOP_COUNT = "repositoryManagementLoopCount";


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
        
        // Prevent looping on source document changes
        // (required at least when changing SOA name)
        if (areListenersDisabled(context)) {
            return;
        }
        int repositoryManagementLoopCount;
        if (context.hasProperty(CONTEXT_REQUEST_REPOSITORY_MANAGEMENT_LOOP_COUNT)) {
            repositoryManagementLoopCount = 1 + (Integer) context.getProperty(CONTEXT_REQUEST_REPOSITORY_MANAGEMENT_LOOP_COUNT);
            if (repositoryManagementLoopCount > 10) {
                throw new ClientException("RepositoryManagementListener : Infinite loop on " + sourceDocument);
            }
        } else {
            repositoryManagementLoopCount = 1;
        }
        sourceDocument.getContextData().putScopedValue(ScopeType.REQUEST,
                CONTEXT_REQUEST_REPOSITORY_MANAGEMENT_LOOP_COUNT, repositoryManagementLoopCount);
        
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
        
        boolean isCreationOnCheckinMode = "ProjetWebServiceImpl".equals(sourceDocument.getPropertyValue("dc:title")); // TODO isJwt(OrCmis) using probeType
        boolean isCreationOnCheckin = isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CHECKEDIN.equals(event.getName());
        boolean isCreation = isCreationOnCheckin
        		|| !isCreationOnCheckinMode && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName());
        
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
				// TODO not required if DocumentEventTypes.DOCUMENT_CREATED_BY_COPY : its soaname is already OK
				// NB. true copy is unavailable (triggers proxying), save in different Phase / subproject
                // because subproject meta is updated by its listener BEFORE this code (RepositoryManagementListener) happens
				// TODO still change soaname in this case
				// TODO LATER an alternative would be to have true "Create link / proxy" button in UI.
	        	if (newSoaName != null) {
	        		sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, newSoaName); // TODO [soaname change]
	                // NB. looping must be prevented here
	                sourceDocument = documentManager.saveDocument(sourceDocument);// required when created using Nuxeo UI not in repo, else when moved is revalidated and set to null
	                documentModified = false;//true
	        	}
	        	
	            // Working copy/proxies management
	            sourceDocument = manageRepositoryStructure(documentManager, documentService,
	                    sourceDocument, repositoryManagementLoopCount);
	            documentManager.save(); // else loops on classifySoaNode() within IST's handleDocumentModel() below
	            
	    		// Intelligent system trees update
	            ///sourceDocument.getContextData().remove(ScopeType.REQUEST.getScopedKey(
	            ///		CONTEXT_REQUEST_REPOSITORY_ALREADY_MANAGED)); // allowing one RML loop, else InheritedDataTest.testUuidSelectors() fails
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
            DocumentModel removedProxyDocumentIfAny = null;
            
        	if (DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())/* && sourceDocument.isProxy()*/) {
				// (TODO necessarily live) Proxy deleted, update the true document, TODO if it is live
                removedProxyDocumentIfAny = sourceDocument;
				sourceDocument = documentManager.getSourceDocument(removedProxyDocumentIfAny.getRef());
				if (sourceDocument.isVersion()) {
				    return;
				    // since source document is version, can't modify it ; but no need either,
				    // because in any way proxy had been created after the version was
				}
				documentModified = false; // TODO reset documentModified for now, may change again below
        	}
        	
    		// Update parents info
        	if (!DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName()) &&
        	        (sourceDocument.isProxy() || removedProxyDocumentIfAny != null ||
        	        // nothing to do if actual doc just created :
        	        !DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())
                    && !DocumentEventTypes.DOCUMENT_CREATED_BY_COPY.equals(event.getName()))) {
		    	try {
		    	    documentModified = updateParentIdsMetadata(documentManager, documentService,
		    	            sourceDocument, removedProxyDocumentIfAny) || documentModified;
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
                ///sourceDocument.getContextData().putScopedValue(ScopeType.REQUEST,
                ///		CONTEXT_REQUEST_REPOSITORY_ALREADY_MANAGED, true); // preventing loop from inherited facets at UPDATE in ex. DiscoveryServiceTest
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
			DocumentService documentService, DocumentModel sourceDocument,
			int repositoryManagementLoopCount)
			throws ClientException, PropertyException, Exception {
	    boolean structureChanged = false;
		
		// If a document has been created through the Nuxeo UI, move it to the repository and leave only a proxy

		DocumentModel parentModel = documentManager.getDocument(sourceDocument.getParentRef());
		String sourceRepositoryPath = RepositoryHelper.getRepositoryPath(documentManager, sourceDocument);
		// NB. if proxy, may be of a different subproject than parent, but anyway its place is in its parent's
        String sourceFolderPath = documentService.getSourceFolderPathBelowSubprojectRepository(
                RepositoryHelper.getRepositoryPath(documentManager, sourceDocument), sourceDocument.getType());
        DocumentModel parentParentModel = documentManager.getParentDocument(documentManager.getParentDocument(sourceDocument.getRef()).getRef());
		
		SoaNodeId soaNodeId = documentService.createSoaNodeId(sourceDocument);
		
		// if it's not a proxy
		if (!sourceDocument.isProxy()
		        ///&& !(sourceDocument.getType().equals(parentModel.getName()) // TODO RepositoryHelper.isRepository()
		        ///&& Repository.DOCTYPE.equals(parentParentModel.getType()))
		        
				// or if it's a proxy not in the Repository but having just been copied below a proxy of SoaNode (ex. TaggingFolder)...
		        || sourceDocument.isProxy() && parentModel.hasSchema(SoaNode.SCHEMA) && parentModel.isProxy()
		        && !sourceDocument.getPathAsString().startsWith(sourceRepositoryPath)) {
		    
		    DocumentModel repositoryDocument = documentService.findSoaNode(documentManager, soaNodeId);
		    if (repositoryDocument != null
		            && repositoryDocument.getPath().toString().startsWith(sourceRepositoryPath)) { // TODO else ???? & false (see above)
		    	if (!repositoryDocument.getRef().equals(sourceDocument.getRef()) // also equals if one proxies the other ?!?!
		    			&& !sourceDocument.isProxy()) { // if proxy, no differences and nothing to do

		    		// If there is already a corresponding repositoryDocument, merge and only keep one
			        repositoryDocument.copyContent(sourceDocument);
			        repositoryDocument.getContextData().putScopedValue(ScopeType.REQUEST,
			                CONTEXT_REQUEST_REPOSITORY_MANAGEMENT_LOOP_COUNT, repositoryManagementLoopCount);
			        documentManager.saveDocument(repositoryDocument); // may trigger another event
			        documentManager.removeDocument(sourceDocument.getRef());
			        
			        // If it's not already in its type's default Repository folder,
			        if (!parentModel.getPathAsString().equals(sourceFolderPath)) {
			        
				    // Create a proxy at the expected location
				    if (parentModel.isProxy() && documentService.isSoaNode(documentManager, parentModel.getType())) {
				    	// make sure parent SOA node is not a proxy (may happen also in the second case)
				        parentModel = documentService.findSoaNode(documentManager, documentService.createSoaNodeId(parentModel));
				        // NB. ?? Nuxeo UI requires also a proxy below the parent proxy, but getChild(parent, repositoryDocument.getName()) can't get it ?!
				        /*try {
					        if (documentManager.getChild(parentModel.getRef(), repositoryDocument.getName()) != null) { // if none yet
						        // creating a proxy under the right, non-proxy parent
						    	sourceDocument = documentManager.createProxy(repositoryDocument.getRef(), parentModel.getRef());
					        }
				        } catch (Exception e) {
				        	// happens if no child
				        }*/
				    }
				    
			        sourceDocument = documentManager.createProxy(repositoryDocument.getRef(), parentModel.getRef());
			        }
			        structureChanged = false;
			        
		    	} else if (!parentModel.getPathAsString().equals(sourceFolderPath)
		    	        && sourceDocument.isProxy() && parentModel.isProxy()
		    			&& documentService.isSoaNode(documentManager, parentModel.getType())) {
		            documentService.getSourceFolder(documentManager, sourceDocument); // ensuring it exists
		            
		    		// if both proxies, move child under its parent's repository document (happens ?!?)
			        DocumentModel actualParentModel = documentService.findSoaNode(documentManager, documentService.createSoaNodeId(parentModel));
			        sourceDocument = documentManager.move(sourceDocument.getRef(),
			        		actualParentModel.getRef(), sourceDocument.getName()); // NB. stays the same doc
		            structureChanged = true;
		        } // else already at right place : nothing to do 
		    }
		    else if (!parentModel.getPathAsString().equals(sourceFolderPath)) {
		        // Move to (the sourceDocument's) repository otherwise
                documentService.getSourceFolder(documentManager, sourceDocument); // ensuring it exists
		    	repositoryDocument = documentManager.move(sourceDocument.getRef(),
		            new PathRef(sourceFolderPath), sourceDocument.getName()); // NB. stays the same doc
			    // NB. save required after move before creating proxy (?!)
			    // Create a proxy at the expected location
			    if (parentModel.isProxy() && documentService.isSoaNode(documentManager, parentModel.getType())) {
			    	// make sure parent SOA node is not a proxy (may happen also in the second case)
			        parentModel = documentService.findSoaNode(documentManager, documentService.createSoaNodeId(parentModel));
			    }
		    	sourceDocument = documentManager.createProxy(repositoryDocument.getRef(), parentModel.getRef());
	            structureChanged = true;
		    } // else already at right place
		}
		if (structureChanged) {
		    documentManager.save();
		}
		
		return sourceDocument;
	}

	private boolean updateParentIdsMetadata(CoreSession documentManager,
			DocumentService documentService, DocumentModel sourceDocument,
			DocumentModel removedProxyDocumentIfAny) throws Exception {
        SoaNode sourceSoaNode = sourceDocument.getAdapter(SoaNode.class);
	    List<DocumentModel> parentModels = documentService.findAllParents(documentManager, sourceDocument);
		boolean changed = false;
		DocumentModelList soaNodeParentModels = new DocumentModelListImpl();
		Iterator<SoaNodeId> oldParentIdIt = sourceSoaNode.getParentIds().iterator();
		for (DocumentModel parentModel : parentModels) {
		    if (documentService.isSoaNode(documentManager, parentModel.getType())) {
			    if (removedProxyDocumentIfAny != null && parentModel.getRef().equals(removedProxyDocumentIfAny.getParentRef())) {
	                // don't re-add it ! happens on ABOUT_TO_REMOVE event because findAllParents() still
	                // returns proxyDocumentIfAny because it's not yet been removed
	                changed = true;
			    } else {
			        soaNodeParentModels.add(parentModel);
    				if (!changed && (!oldParentIdIt.hasNext() || !parentModel.equals(oldParentIdIt.next()))) {
    				    changed = true;
    				}
			    }
			}
		}
		if (changed) {
		    sourceSoaNode.setParentIds(documentService.createSoaNodeIds(soaNodeParentModels.toArray(new DocumentModel[]{})));
		}
		return changed;
	}

}
