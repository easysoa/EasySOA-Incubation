package org.easysoa.registry.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

@Name("documentModelHelperBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class DocumentModelHelperBean {

	public static final String PARENT_TYPE_CLASSIFICATION = "Classification";
	
	public static final String PARENT_TYPE_DOCTYPE = "Document types";
	
	public static final String PARENT_TYPE_MODEL = "Related SOA elements";

    private static Log log = LogFactory.getLog(DocumentModelHelperBean.class);
	
    @In
    private CoreSession documentManager;

    @In(create = true)
    private DocumentService documentService;

    public String getDocumentTypeLabel(DocumentModel model) throws Exception {
        return DocumentModelHelper.getDocumentTypeLabel(model.getType());
    }
    
    public DocumentModelList findAllParents(DocumentModel documentModel) throws Exception {
        return documentService.findAllParents(documentManager, documentModel);
    }

    public DocumentModel safeGetDocument(String pathOrId) throws Exception {
        if (pathOrId == null || pathOrId.length() == 0) {
            return null;
        }
    	DocumentRef ref;
    	if (pathOrId.contains("/")) {
    		ref = new PathRef(pathOrId);
    	}
    	else {
    		ref = new IdRef(pathOrId);
    	}
    	
    	try {
    		return documentManager.getDocument(ref);
    	}
    	catch (ClientException e) {
    		return null;
    	}
    }
    
    /**
     * Should be SoaNode TODO ??
     * @param documentModel
     * @return
     * @throws Exception
     */
    public Map<String, DocumentModelList> findAllParentsByType(DocumentModel documentModel) throws Exception {
    	Map<String, DocumentModelList> parentsByType = new HashMap<String, DocumentModelList>();
    	
    	String repositoryPath = null;
    	
    	DocumentModelList parentModels = findAllParents(documentModel);
        for (DocumentModel parentModel : parentModels) {
        	if (TaggingFolder.DOCTYPE.equals(parentModel.getType())) {
        		addParent(parentsByType, PARENT_TYPE_CLASSIFICATION, parentModel);
        	}
        	else if (IntelligentSystem.DOCTYPE.equals(parentModel.getType())
        			|| IntelligentSystemTreeRoot.DOCTYPE.equals(parentModel.getType())) {
        	    if (repositoryPath == null) {
        	        // lazily computing repositoryPath
        	        String subprojectId = (String) documentModel.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        	        repositoryPath = RepositoryHelper.getRepositoryPath(documentManager, subprojectId);
        	    }
        	    
        		if (parentModel.getPathAsString().startsWith(repositoryPath)) {
            		addParent(parentsByType, PARENT_TYPE_DOCTYPE, parentModel);
        		}
        		else {
            		addParent(parentsByType, PARENT_TYPE_CLASSIFICATION, parentModel);
        		}
        	}
        	else {
        		addParent(parentsByType, PARENT_TYPE_MODEL, parentModel);
        	}
        }
        return parentsByType;
    }

	private void addParent(Map<String, DocumentModelList> parentsByType,
			String parentTypeClassification, DocumentModel parentModel) {
		if (!parentsByType.containsKey(parentTypeClassification)) {
			parentsByType.put(parentTypeClassification, new DocumentModelListImpl());
		}
		parentsByType.get(parentTypeClassification).add(parentModel);
	}

	/**
	 * TODO rather in SubprojectServiceImpl ??
	 * @param phaseId
	 * @return
	 */
	private String displayPhaseId(String phaseId) {
		// formatting phaseId
		// (it would be simpler to get Phase / subproject and from there its version and parent project
		// but it could hide phaseId bugs)
		
		StringBuffer sbuf = new StringBuffer();

        int lastVersionSeparatorId = phaseId.lastIndexOf(Subproject.SUBPROJECT_ID_VERSION_SEPARATOR);
        if (lastVersionSeparatorId < 0) {
        	String msg = "getPathFromId() called on badly formatted subprojectId " + phaseId;
        	log.warn(msg);
            return msg;
        }
        String phasePath = phaseId.substring(0, lastVersionSeparatorId);
        String phaseVersion = phaseId.substring(lastVersionSeparatorId + 2);

        int firstSlashNotAtStartIndex = phasePath.indexOf('/', 1);
        int lastSlashIndex = phasePath.lastIndexOf('/');
        String projectName = phasePath.substring(firstSlashNotAtStartIndex + 1, lastSlashIndex);
        String phaseName = phasePath.substring(lastSlashIndex + 1, phasePath.length());
        
		sbuf.append(projectName);
		sbuf.append(" / ");
		sbuf.append(phaseName);
		
		if (!phaseVersion.isEmpty()) {
			sbuf.append(" (");
			sbuf.append(phaseVersion);
			sbuf.append(")");
		}
		return sbuf.toString();
	}

	public String displayPhase(DocumentModel subprojectNode) throws ClientException {
		try {
			String phaseId = (String) subprojectNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
			return displayPhaseId(phaseId);
    	} catch (ClientException e) {
    		return "";
    	}
	}

    public String displayDocument(DocumentModel docModel) throws Exception {
    	String docType =  docModel.getType();
    	if (documentService.isSoaNode(documentManager, docType)) {
    		StringBuffer sbuf = new StringBuffer();
    		sbuf.append(displayPhase(docModel));
    		sbuf.append(docModel.getPropertyValue(SoaNode.XPATH_SOANAME));
    		// not displaying version, since it is already within Phase / subproject
    		//if (docModel.isVersion()) {
    		//	sbuf.append(docModel.getPropertyValue(SoaNode.XPATH_SOANAME));
    		//}
    		return sbuf.toString();
    		
    	} else if (Subproject.DOCTYPE.equals(docType)) {
    		return displayPhase(docModel);
    		
    	} else {
    		// behaviour of Nuxeo JSF nxd:titleOrId(doc) excepts it returns ecm:name by default
    		String title = docModel.getTitle();
    		if (title != null && !title.isEmpty()) {
    			return title;
    		} else {
    			return docModel.getName();
    		}
    	}
    }

	public DocumentModel getSubprojectById(String subprojectOrDocId) {
		try {
			if (subprojectOrDocId.indexOf('/') == -1) {
				return documentManager.getDocument(new IdRef(subprojectOrDocId));
			}
			return SubprojectServiceImpl.getSubprojectById(documentManager, subprojectOrDocId);
    	} catch (ClientException e) {
    		return null;
    	}
	}
    
}
