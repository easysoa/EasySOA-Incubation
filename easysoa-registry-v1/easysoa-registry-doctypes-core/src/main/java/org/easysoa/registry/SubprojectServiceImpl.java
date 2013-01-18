package org.easysoa.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Project;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.NuxeoListUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;

public class SubprojectServiceImpl {

    public static final PathRef defaultProjectPathRef = new PathRef(Project.DEFAULT_PROJECT_PATH);
    public static final PathRef defaultSubprojectPathRef = new PathRef(Subproject.DEFAULT_SUBPROJECT_PATH);

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String  SUBPROJECT_ID_VERSION_SEPARATOR = "_v";

    private static Logger logger = Logger.getLogger(SubprojectServiceImpl.class);
    
    /**
     * Creates but doesn't save session
     * @param documentManager
     * @param name
     * @return
     * @throws ClientException 
     */
    public static DocumentModel createProject(CoreSession documentManager, String name) throws ClientException {
        DocumentModel projectModel = documentManager.createDocumentModel(Project.DOCTYPE);
        projectModel.setPathInfo(Repository.DEFAULT_DOMAIN_PATH, name); // TODO safeName()
        projectModel.setPropertyValue("dc:title", name);
        projectModel = documentManager.createDocument(projectModel);
        return projectModel;
    }

    /**
     * Creates but doesn't save session
     * @param documentManager
     * @param name
     * @param projectModel
     * @param parentSubprojectModels
     * @return
     * @throws ClientException
     */
    public static DocumentModel createSubproject(CoreSession documentManager, String name,
            DocumentModel projectModel, List<DocumentModel> parentSubprojectModels) throws ClientException {
        DocumentModel subprojectModel = documentManager.createDocumentModel(Subproject.DOCTYPE); // triggers DocumentEventTypes.EMPTY_DOCUMENTMODEL_CREATED
        subprojectModel.setPathInfo(projectModel.getPathAsString(), name); // TODO safeName()
        subprojectModel.setPropertyValue("dc:title", name);
        
        String[] parentSubprojectEcmUuids;
        if (parentSubprojectModels == null) {
            parentSubprojectEcmUuids = EMPTY_STRING_ARRAY;
        } else {
            parentSubprojectEcmUuids = NuxeoListUtils.getIds(parentSubprojectModels).toArray(EMPTY_STRING_ARRAY);
        }
        subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, parentSubprojectEcmUuids);
        
        onSubprojectAboutToCreate(documentManager, subprojectModel);
        
        subprojectModel = documentManager.createDocument(subprojectModel);
        return subprojectModel;
    }
    
    /**
     * To be called on aboutToCreate
     * @param subprojectModel 
     * @param documentManager 
     * @param parentSubprojectEcmUuids 
     * @throws ClientException 
     */
    public static void onSubprojectAboutToCreate(CoreSession documentManager,
            DocumentModel subprojectModel) throws ClientException {
        // TODO also on document change... (parents)
        computeAndSetVisibleSubprojects(documentManager, subprojectModel);
        // NB. saveDocument() is auto done after aboutToCreate even by event system
        // TODO on documentCreated, update all below
    }

    public static String subprojectToId(DocumentModel subprojectModel) {
        StringBuffer sbuf = new StringBuffer(subprojectModel.getPathAsString());
        sbuf.append(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (subprojectModel.isVersion()) {
            // TODO NB. never happens on aboutToCreate, by on aboutToCreateLeafVersionEvent
            sbuf.append(subprojectModel.getVersionLabel());
        }
        return sbuf.toString();
    }

    public static DocumentModel getSubprojectById(CoreSession documentManager, String subprojectId) throws ClientException {
        subprojectId = getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        String[] subprojectIdParts = subprojectId.split(SUBPROJECT_ID_VERSION_SEPARATOR);
        String path = subprojectIdParts[0];
        String versionLabelCriteria;
        if (subprojectIdParts.length == 1) {
            versionLabelCriteria = "";
        } else {
            String versionLabel = subprojectIdParts[1];
            versionLabelCriteria = " AND ecm:versionLabel='" + versionLabel + "'";
        }
        DocumentModelList res = documentManager.query("select * from Subproject where ecm:path='"
                + path + "'" + versionLabelCriteria
                + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER + DocumentService.PROXIES_QUERY_FILTER); // TODO ?
        if (res.size() == 1) {
            return res.get(0);
        } else if (res.isEmpty()) {
            return null;
        } else {
            logger.fatal("More than one subproject found for id " + subprojectId + " : " + res);
            return null;
        }
        
    }

    /**
     * TODO only set if changed & then return true
     * TODO not public ??
     */
    public static void computeAndSetVisibleSubprojects(CoreSession documentManager, DocumentModel subprojectModel) throws ClientException {
        String subprojectId = subprojectToId(subprojectModel);
        subprojectModel.setPropertyValue(Subproject.XPATH_SUBPROJECT, subprojectId); // to get copied
        
        Serializable foundParentSubprojectIds = subprojectModel.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS);
        String[] parentSubprojectIds;
        if (foundParentSubprojectIds instanceof String) {
            // for easier remote initialization
            parentSubprojectIds = ((String) foundParentSubprojectIds).split(",");
            subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, parentSubprojectIds);
        } else {
            parentSubprojectIds = (String[]) foundParentSubprojectIds;
        }
        
        //String[] visibleSubprojectIds = (String[]) subprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS);
        int estimatedSize = ((parentSubprojectIds == null) ? 0 : parentSubprojectIds.length * 2) + 1;
        ArrayList<String> visibleSubprojectIdList = new ArrayList<String>(estimatedSize);
        visibleSubprojectIdList.add(subprojectId);
        
        StringBuffer visibleSubprojectIdsCsvSbuf = new StringBuffer("'"); // NB. '\'' doesn't work, counts as int !!
        visibleSubprojectIdsCsvSbuf.append(subprojectId);
        visibleSubprojectIdsCsvSbuf.append('\'');
        
        if (parentSubprojectIds != null && parentSubprojectIds.length != 0) {
            for (String parentSubprojectId : parentSubprojectIds) {
                DocumentModel parentSubprojectModel = documentManager.getDocument(new IdRef(parentSubprojectId));

                String[] parentVisibleSubprojectIds = (String[]) parentSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS);
                visibleSubprojectIdList.addAll(Arrays.asList(parentVisibleSubprojectIds));
                
                String parentVisibleSubprojectIdsCsv = (String) parentSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV);
                //  NB. should always be set, at least to itself
                visibleSubprojectIdsCsvSbuf.append(',');
                visibleSubprojectIdsCsvSbuf.append(parentVisibleSubprojectIdsCsv);
            }
        }
        String[] visibleSubprojectIds = (String[]) visibleSubprojectIdList.toArray(EMPTY_STRING_ARRAY);
        subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS, visibleSubprojectIds);
        String visibleSubprojectIdsCsv = visibleSubprojectIdsCsvSbuf.toString();
        subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV, visibleSubprojectIdsCsv);
        // TODO only set if changed & then return true 
    }
    
    
    /**
     * Rightens identifier after calling getSubprojectIdOrCreateDefault()
     * @param documentManager
     * @param identifier
     * @return
     * @throws ClientException
     */
    public static String setDefaultSubprojectIfNone(CoreSession documentManager,
            SoaNodeId identifier) throws ClientException {
        String subprojectId = identifier.getSubprojectId();
        if (subprojectId == null || subprojectId.length() == 0) {
            // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
            subprojectId = (String) SubprojectServiceImpl.getOrCreateDefaultSubproject(documentManager)
                    .getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
            identifier.setSubprojectId(subprojectId);
        }
        return subprojectId;
    }

    /**
     * If possible, rather use setDefaultSubprojectIfNone()
     * @param documentManager
     * @param subprojectId
     * @return
     * @throws ClientException
     */
    public static String getSubprojectIdOrCreateDefault(CoreSession documentManager,
            String subprojectId) throws ClientException {
        if (subprojectId == null || subprojectId.length() == 0) {
            // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
            return (String) getOrCreateDefaultSubproject(documentManager)
                    .getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        } else {
            return subprojectId;
        }
    }

    public static DocumentModel getSubprojectOrCreateDefault(CoreSession documentManager,
            String subprojectId) throws ClientException {
        if (subprojectId == null || subprojectId.length() == 0) {
            // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
            return getOrCreateDefaultSubproject(documentManager);
        } else {
            return getSubprojectById(documentManager, subprojectId);
        }
    }

    private static DocumentModel getOrCreateDefaultSubproject(CoreSession documentManager) throws ClientException {
        // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
        //DocumentModelList subprojectResults = documentManager.query(
        //        "SELECT * FROM Subproject WHERE dc:title='Default'"); // TODO better
        DocumentModel subprojectDocModel;
        DocumentModelList subprojectResults = documentManager.query(
                "SELECT * FROM Subproject WHERE ecm:path='" + SubprojectServiceImpl.defaultSubprojectPathRef + "'");
        if (subprojectResults == null || subprojectResults.isEmpty()) {
            DocumentModel project = getOrCreateDefaultProject(documentManager);
            subprojectDocModel = SubprojectServiceImpl.createSubproject(documentManager,
                    Subproject.DEFAULT_SUBPROJECT_NAME , project, null);
            documentManager.save();
        } else {
            subprojectDocModel = subprojectResults.get(0);
        }
        return subprojectDocModel;
    }

    private static DocumentModel getOrCreateDefaultProject(CoreSession documentManager) throws ClientException {
        DocumentModel project;
        DocumentModelList projectResults = documentManager.query(
                "SELECT * FROM Project WHERE ecm:path='" + SubprojectServiceImpl.defaultProjectPathRef + "'");
        if (projectResults == null || projectResults.isEmpty()) {
            project = SubprojectServiceImpl.createProject(documentManager,
                    Project.DEFAULT_PROJECT_NAME);
            documentManager.save();
        } else {
            project = projectResults.get(0);
        }
        return project;
    }
    
}
