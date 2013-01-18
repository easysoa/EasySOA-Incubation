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
import org.nuxeo.ecm.core.api.model.PropertyException;

public class SubprojectServiceImpl {

    public static final String  SUBPROJECT_ID_VERSION_SEPARATOR = "_v";
    
    public static final PathRef defaultProjectPathRef = new PathRef(Project.DEFAULT_PROJECT_PATH);
    public static final PathRef defaultSubprojectPathRef = new PathRef(Subproject.DEFAULT_SUBPROJECT_PATH);
    public static final String defaultSubprojectId = Subproject.DEFAULT_SUBPROJECT_PATH + SUBPROJECT_ID_VERSION_SEPARATOR;


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
            parentSubprojectEcmUuids = NuxeoListUtils.EMPTY_STRING_ARRAY;
        } else {
            parentSubprojectEcmUuids = NuxeoListUtils.getIds(parentSubprojectModels).toArray(NuxeoListUtils.EMPTY_STRING_ARRAY);
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
        if (getSubprojectById(documentManager, buildSubprojectId(subprojectModel)) != null) {
            throw new ClientException("Trying to create Subproject but already exists at "
                    + buildSubprojectId(subprojectModel));
        }
        // TODO also on document change... (parents)
        computeAndSetVisibleSubprojects(documentManager, subprojectModel);
        // NB. saveDocument() is auto done after aboutToCreate even by event system
        // TODO on documentCreated, update all below
    }

    public static String buildSubprojectId(DocumentModel subprojectModel) {
        StringBuffer sbuf = new StringBuffer(subprojectModel.getPathAsString());
        sbuf.append(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (subprojectModel.isVersion()) {
            // TODO NB. never happens on aboutToCreate, by on aboutToCreateLeafVersionEvent
            sbuf.append(subprojectModel.getVersionLabel());
        }
        return sbuf.toString();
    }
    
    public static String getPathFromId(String subprojectId) {
        int lastVersionSeparatorId = subprojectId.lastIndexOf(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (lastVersionSeparatorId < 0) {
            logger.warn("getPathFromId() called on badly formatted subprojectId " + subprojectId);
            return null;
        }
        return subprojectId.substring(0, lastVersionSeparatorId);
    }
    
    public static String buildCriteriaFromId(String subprojectId) {
        return DocumentService.NXQL_PATH_STARTSWITH + getPathFromId(subprojectId) + "'";
    }
    
    public static boolean isBeingVersionedSubprojectNode(DocumentModel subprojectNode)
            throws PropertyException, ClientException {
        String subprojectId = (String) subprojectNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        int lastVersionSeparatorId = subprojectId.lastIndexOf(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (lastVersionSeparatorId < 0) {
            logger.warn("isBeingVersionedSubprojectNode() called on badly formatted subprojectId " + subprojectId);
        }
        return lastVersionSeparatorId != subprojectId.length() - SUBPROJECT_ID_VERSION_SEPARATOR.length();
    }

    public static DocumentModel getSubprojectById(CoreSession documentManager, String subprojectId) throws ClientException {
        subprojectId = getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        // rather looking for Subproject with spnode:subprojet (simpler and more generic than using path)
        /*int lastVersionSeparatorId = subprojectId.lastIndexOf(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (lastVersionSeparatorId < 0) {
            logger.warn("getSubprojectById() called on badly formatted subprojectId " + subprojectId);
            return null;
        }
        
        String path = subprojectId.substring(0, lastVersionSeparatorId);
        String versionLabelCriteria;
        if (lastVersionSeparatorId == subprojectId.length() - SUBPROJECT_ID_VERSION_SEPARATOR.length()) {
            versionLabelCriteria = "";
        } else {
            String versionLabel = subprojectId.substring(lastVersionSeparatorId + SUBPROJECT_ID_VERSION_SEPARATOR.length());
            versionLabelCriteria = " AND ecm:versionLabel='" + versionLabel + "'";
        }
        DocumentModelList res = documentManager.query("select * from Subproject where ecm:path='"
                + path + "'" + versionLabelCriteria
                + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER + DocumentService.PROXIES_QUERY_FILTER); // TODO ?*/
        DocumentModelList res = documentManager.query("SELECT * FROM " + Subproject.DOCTYPE
                + " WHERE " + SubprojectNode.XPATH_SUBPROJECT + "='" + subprojectId + "'");
        if (res.size() == 1) {
            return res.get(0);
        } else if (res.isEmpty()) {
            return null;
        } else {
            logger.fatal("More than one subproject found for id " + subprojectId + " : " + res);
            return null;
        }
    }

    public static DocumentModel getSubprojectByName(CoreSession documentManager,
            DocumentModel project, String subprojectName) throws ClientException {
        String path = project.getPathAsString() + '/' + subprojectName;
        DocumentModelList res = documentManager.query("select * from Subproject where ecm:path='" + path
                + "'" + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER + DocumentService.PROXIES_QUERY_FILTER);
        if (res.size() == 1) {
            return res.get(0);
        } else if (res.isEmpty()) {
            return null;
        } else {
            logger.fatal("More than one subproject found at path " + path + " : " + res);
            return null;
        }
    }

    /**
     * TODO only set if changed & then return true
     * TODO not public ??
     */
    public static void computeAndSetVisibleSubprojects(CoreSession documentManager, DocumentModel subprojectModel) throws ClientException {
        String subprojectId = buildSubprojectId(subprojectModel);
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
        String[] visibleSubprojectIds = (String[]) visibleSubprojectIdList.toArray(NuxeoListUtils.EMPTY_STRING_ARRAY);
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
        DocumentModel foundSubproject = getSubprojectById(documentManager, subprojectId);
        if (foundSubproject == null && (subprojectId == null || subprojectId.length() == 0
                || subprojectId.equals(defaultSubprojectId))) {
            // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
            return getOrCreateDefaultSubproject(documentManager);
        }
        return foundSubproject;
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
