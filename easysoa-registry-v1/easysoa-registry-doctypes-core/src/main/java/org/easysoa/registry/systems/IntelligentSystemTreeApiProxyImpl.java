package org.easysoa.registry.systems;

import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;

public class IntelligentSystemTreeApiProxyImpl implements IntelligentSystemTreeApi {

    private static Logger logger = Logger.getLogger(IntelligentSystemTreeApiProxyImpl.class);
    
	private final CoreSession documentManager;
	
	private DocumentService documentService;

	public IntelligentSystemTreeApiProxyImpl(CoreSession documentManager) throws Exception {
		this.documentManager = documentManager;
		this.documentService = Framework.getService(DocumentService.class);
		if (this.documentService == null || this.documentManager == null) {
			throw new NullPointerException("Cant instantiate IST API, required session or doc service is null");
		}
	}

	public boolean intelligentSystemTreeExists(String name) throws ClientException {
        return findISTModel(name) != null;
	}

	public void createIntelligentSystemTree(String name, String title) throws ClientException {
		documentService.createDocument(documentManager,
				IntelligentSystemTreeRoot.DOCTYPE,
				name, "/default-domain/workspaces", title);
		documentManager.save();
	}

	@Override
	public void classifySoaNode(String treeName, SoaNodeId identifier, String classification) throws ClientException {
		DocumentModel istModel = findISTModel(treeName);
		if (istModel == null) {
			throw new ClientException("Tree '" + treeName + "' doesn't exist, can't classify SoaNode " + identifier);
		}
		
        // Ensure the parent systems exist
        PathRef expectedParentPath = new PathRef(istModel.getPathAsString() +
                (("".equals(classification)) ? "" : "/" + classification));
        if (!documentManager.exists(expectedParentPath)) {
            String[] parentSystems = classification.split("/");
            DocumentModel currentFolder = istModel;
            for (String parentSystem : parentSystems) {
                PathRef childPathRef = new PathRef(currentFolder.getPathAsString() + '/' + parentSystem);
                if (!documentManager.exists(childPathRef)) {
                    currentFolder = documentService.createDocument(documentManager,
                            IntelligentSystem.DOCTYPE, parentSystem,
                            currentFolder.getPathAsString(), parentSystem);
                } else {
                    currentFolder = documentManager.getDocument(childPathRef);
                }
            }
        }
        
        // If the model is missing, create a proxy
        DocumentModel existingModel = findSoaNodeModel(istModel, treeName, identifier);
        if (existingModel == null) {
    		DocumentModel sourceModel = documentService.find(documentManager, identifier);
            documentManager.createProxy(sourceModel.getRef(), expectedParentPath);
        }

        // If in the IST but not at the right place, move the document
        else if (!existingModel.getPathAsString().equals(expectedParentPath.toString() + '/' + existingModel.getName())){
            List<DocumentModel> parents = documentManager.getParentDocuments(existingModel.getRef());
            documentManager.move(existingModel.getRef(), expectedParentPath, existingModel.getName());
            removeEmptyParentSystems(parents);
        }
	}

	@Override
	public void deleteSoaNode(String treeName, SoaNodeId identifier) throws ClientException {
		 // If the model was in the IST, delete it
		DocumentModel istModel = findISTModel(treeName);
		if (istModel == null) {
			throw new ClientException("Tree '" + treeName + "' doesn't exist, can't remove SoaNode " + identifier);
		}
		
		DocumentModel soaNodeModel = findSoaNodeModel(istModel, treeName, identifier);
        if (soaNodeModel != null) {
            List<DocumentModel> parents = documentManager.getParentDocuments(soaNodeModel.getRef());
            documentManager.removeDocument(soaNodeModel.getRef());
            removeEmptyParentSystems(parents);
        }
	}

	private void removeEmptyParentSystems(List<DocumentModel> hierarchy) throws ClientException {
	    for (int i = hierarchy.size() - 2; i >= 0; i--) {
	        DocumentModel parentModel = hierarchy.get(i);
	        if (!documentManager.hasChildren(parentModel.getRef())
	                && IntelligentSystem.DOCTYPE.equals(parentModel.getType())) {
	            documentManager.removeDocument(parentModel.getRef());
	        }
	        else {
	            break;
	        }
	    }
	}

	private DocumentModel findISTModel(String name) throws ClientException {
		return documentService.findDocument(documentManager, IntelligentSystemTreeRoot.DOCTYPE, name);
	}

	private DocumentModel findSoaNodeModel(DocumentModel istModel, String treeName, SoaNodeId identifier) throws ClientException {
		String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE ecm:path STARTSWITH '?' AND ? = '?'",
				new Object[]{ identifier.getType(), istModel.getPathAsString(),
				SoaNode.XPATH_SOANAME, identifier.getName() },
				false, true);
		DocumentModelList matchingSoaNodes = documentService.query(documentManager, query, false, true);
		if (matchingSoaNodes.size() > 0) {
			if (matchingSoaNodes.size() > 1) {
				logger.error("The are " + matchingSoaNodes.size() + " instances of " + identifier 
						+ " in the " + treeName + " tree, but a maximum of one is normally supported. Will use the first one found.");
			}
			return matchingSoaNodes.get(0);
		}
		else {
			return null;
		}
	}
	
	
}
