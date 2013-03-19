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
import org.nuxeo.ecm.core.api.DocumentModelIterator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.snapshot.Snapshot;
import org.nuxeo.snapshot.Snapshotable;

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
        String subprojectId = buildSubprojectId(subprojectModel);
        
        if (getSubprojectById(documentManager, subprojectId) != null) {
            throw new ClientException("Trying to create Subproject but already exists at "
                    + subprojectId);
        }
        
        subprojectModel.setPropertyValue(Subproject.XPATH_SUBPROJECT, subprojectId); // to get copied
        
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
    
    public static boolean isBeingVersionedSubprojectNodeEvent(Event event, DocumentModel subprojectNode)
            throws PropertyException, ClientException {
        if (!DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
            return false;
        }
        String subprojectId = (String) subprojectNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        DocumentModel subprojectInDatabase = getSubprojectById(subprojectNode.getCoreSession(), subprojectId);
        return subprojectInDatabase == null; // subproject ID about to change & being propagated but not yet saved
        /*
        String subprojectId = (String) context.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        int lastVersionSeparatorId = subprojectId.lastIndexOf(SUBPROJECT_ID_VERSION_SEPARATOR);
        if (lastVersionSeparatorId < 0) {
            logger.warn("isBeingVersionedSubprojectNode() called on badly formatted subprojectId " + subprojectId);
        }
        return lastVersionSeparatorId != subprojectId.length() - SUBPROJECT_ID_VERSION_SEPARATOR.length();
        */
    }

    /**
     * Uses db & spnode:subproject. To rather use getParent(), use getSubprojectOfNode()
     * @param documentManager
     * @param subprojectId
     * @return
     * @throws ClientException
     */
    public static DocumentModel getSubprojectById(CoreSession documentManager, String subprojectId) throws ClientException {
        subprojectId = getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        
        // NB. three differents ways :
        
        // 1. looking in db using path
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

        // 2.(rather) looking in db for Subproject with spnode:subprojet (simpler and more generic than using path)
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
        
        // 3. using getParent() => rather use getSubprojectOfNode()
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
        String subprojectId = (String) subprojectModel.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
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
    
    /*
    
    public static void computeAndSetVisibleSubprojects(CoreSession documentManager,
            DocumentModel subprojectModel, boolean aboutToBeVersioned) throws ClientException {
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
        
        int estimatedSize = ((parentSubprojectIds == null) ? 0 : parentSubprojectIds.length * 2) + 1;
        ArrayList<String> visibleVersionedSubprojectIdList = new ArrayList<String>(estimatedSize);
        // current subproject is necessarily live since is being modified :
        ArrayList<String> visibleLiveSubprojectIdList = new ArrayList<String>(estimatedSize);

        StringBuffer visibleVersionSubprojectIdsCsvSbuf = new StringBuffer("");
        // current subproject is necessarily live since is being modified :
        StringBuffer visibleLiveSubprojectIdsCsvSbuf = new StringBuffer("'"); // NB. '\'' doesn't work, counts as int !!
        if (aboutToBeVersioned) {
            visibleVersionedSubprojectIdList.add(subprojectId);
            visibleVersionSubprojectIdsCsvSbuf.append(subprojectId);
            visibleVersionSubprojectIdsCsvSbuf.append("',");
        } else {
            visibleLiveSubprojectIdList.add(subprojectId);
            visibleLiveSubprojectIdsCsvSbuf.append(subprojectId);
            visibleLiveSubprojectIdsCsvSbuf.append("',");
        }
        
        if (parentSubprojectIds != null && parentSubprojectIds.length != 0) {
            for (String parentSubprojectId : parentSubprojectIds) {
                DocumentModel parentSubprojectModel = documentManager.getDocument(new IdRef(parentSubprojectId));
                
                String[] parentVisibleSubprojectIds = (String[]) parentSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS);
                visibleLiveSubprojectIdList.addAll(Arrays.asList(parentVisibleSubprojectIds));
                
                String parentVisibleSubprojectIdsCsv = (String) parentSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV);
                //  NB. should always be set, at least to itself
                //visibleSubprojectIdsCsvSbuf.append(',');
                //visibleSubprojectIdsCsvSbuf.append(parentVisibleSubprojectIdsCsv);

                if (parentSubprojectModel.isVersion()) {
                    //visibleVersionSubprojectCriteriaSbuf.append("(ecm:isVersion=1 AND ecm:versionLabel='"
                    //        + parentSubprojectModel.getVersionLabel() + "' AND "
                    //        + SubprojectNode.XPATH_SUBPROJECT + "='') OR ");
                    visibleVersionSubprojectIdsCsvSbuf.append(parentVisibleSubprojectIdsCsv);
                    visibleVersionSubprojectIdsCsvSbuf.append(',');
                } else {
                    visibleLiveSubprojectIdsCsvSbuf.append(parentVisibleSubprojectIdsCsv);
                    visibleLiveSubprojectIdsCsvSbuf.append(',');
                }
            }
        }
        visibleVersionSubprojectIdsCsvSbuf.deleteCharAt(visibleVersionSubprojectIdsCsvSbuf.length() - 1);
        visibleLiveSubprojectIdsCsvSbuf.deleteCharAt(visibleLiveSubprojectIdsCsvSbuf.length() - 1);
        
        //String vcsv = visibleVersionSubprojectCriteriaSbuf.toString() + " OR (ecm:isVersion AND "
        //        + SubprojectNode.XPATH_SUBPROJECT + " IN (" + visibleLiveSubprojectCriteriaSbuf.toString() + ")"; 

        StringBuffer visibleVersionSubprojectCriteriaSbuf = new StringBuffer(
                DocumentService.NXQL_IS_VERSIONED + DocumentService.NXQL_AND
                + SubprojectNode.XPATH_SUBPROJECT + " IN ("
                + visibleVersionSubprojectIdsCsvSbuf.toString() + ")");
        StringBuffer visibleLiveSubprojectCriteriaSbuf = new StringBuffer(
                DocumentService.NXQL_IS_NOT_VERSIONED + DocumentService.NXQL_AND
                + SubprojectNode.XPATH_SUBPROJECT + " IN ("
                + visibleVersionSubprojectIdsCsvSbuf.toString() + ")");
        
        String[] visibleSubprojectIds = (String[]) visibleSubprojectIdList.toArray(NuxeoListUtils.EMPTY_STRING_ARRAY);
        subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS, visibleSubprojectIds);
        String visibleSubprojectIdsCsv = "((" + visibleVersionSubprojectCriteriaSbuf.toString()
                + ") OR (" + visibleLiveSubprojectCriteriaSbuf.toString() + "))";
        subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV, visibleSubprojectIdsCsv);
        // TODO only set if changed & then return true 
    }
    
    */
    
    
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
    
    public static String buildCriteriaInSubprojectUsingPathFromId(String subprojectId) {
        return DocumentService.NXQL_PATH_STARTSWITH + getPathFromId(subprojectId) + "'";
    }

    // TODO : Add a new param : visibitiy strict or depth
    public static String buildCriteriaSeenFromSubproject(DocumentModel subprojectNode/*, String visibility*/)
            throws PropertyException, ClientException {
        // Filter by subproject
        String implVisibleSubprojectIds = null;
        if (subprojectNode.hasFacet(SubprojectNode.FACET)) {
            implVisibleSubprojectIds = (String) subprojectNode
                    .getPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV);
        }
        if (implVisibleSubprojectIds == null) {
            throw new ClientException("visibleSubprojects should not be null on " + subprojectNode);
        }
        // ex. "AXXXSpecifications"; // or in 2 pass & get it from subProject ??
        return SubprojectNode.XPATH_SUBPROJECT + " IN (" + implVisibleSubprojectIds + ")";
    }

    public static String buildCriteriaSeesSubproject(DocumentModel subprojectNode)
            throws PropertyException, ClientException {
        String subprojectId = null;
        if (subprojectNode.hasFacet(SubprojectNode.FACET)) {
            subprojectId = (String) subprojectNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        }
        if (subprojectId == null) {
            throw new ClientException("subproject should not be null on " + subprojectNode);
        }
        ///DocumentModel subproject = getSubprojectOfNode(subprojectNode);
        ///return SubprojectNode.XPATH_VISIBLE_SUBPROJECTS + "='" + subproject.getId() + "'"; // NB. multivalued prop
        return SubprojectNode.XPATH_VISIBLE_SUBPROJECTS + "='" + subprojectId + "'"; // NB. multivalued prop
    }
    
    /**
     * Uses getParent. To rather use spnode:subproject, use getSubprojectById()
     * @param subprojectNode
     * @return
     * @throws ClientException
     */
    public static DocumentModel getSubprojectOfNode(DocumentModel subprojectNode) throws ClientException {
        CoreSession documentManager = subprojectNode.getCoreSession();
        DocumentModel parent = subprojectNode;
        while (!parent.getType().equals(Subproject.DOCTYPE)) {
            parent = documentManager.getDocument(parent.getParentRef());
            if (parent.getType().equals("Root")) {
                return null;
            }
        }
        return parent;
    }
    
    public static DocumentModel createSubprojectVersion(DocumentModel subproject,
            VersioningOption vOpt) throws ClientException {
        long oldVersionMajor = getVersion(subproject, "major_version");//"uid:major_version"
        long oldVersionMinor = getVersion(subproject, "minor_version");
        String newVersionLabel;
        if (vOpt == VersioningOption.MAJOR) {
            newVersionLabel = (oldVersionMajor + 1) + "." +  oldVersionMinor; 
        } else { // assuming minor
            newVersionLabel = oldVersionMajor + "." +  (oldVersionMinor + 1);
        }

        String liveSubprojectId = (String) subproject.getPropertyValue(Subproject.XPATH_SUBPROJECT);
        String newVersionedSubprojectId = liveSubprojectId + newVersionLabel;
        
        subproject.setPropertyValue(Subproject.XPATH_SUBPROJECT, newVersionedSubprojectId);
        //computeAndSetVisibleSubprojects(subproject.getCoreSession(), subproject);// TODO or evented ??
        ///incrementVersionedSuprojectIdRecursive(subproject, newVersionLabel);// TODO or evented ??
        subproject.getCoreSession().saveDocument(subproject); // triggers event that calls SubprojectNodeListener
              // which will recompute subproject properties on copy them on all descendants
        subproject.getCoreSession().save(); // just in case

        Snapshotable snapshotable = subproject.getAdapter(Snapshotable.class);
        Snapshot snapshot = snapshotable.createSnapshot(vOpt);
        //documentManager.save();

        subproject.setPropertyValue(Subproject.XPATH_SUBPROJECT, liveSubprojectId);
        //computeAndSetVisibleSubprojects(subproject.getCoreSession(), subproject);// TODO or evented ??
        ///incrementVersionedSuprojectIdRecursive(subproject, newVersionLabel);// TODO or evented ??
        subproject.getCoreSession().saveDocument(subproject); // triggers event that calls SubprojectNodeListener
              // which will recompute subproject properties on copy them on all descendants
        subproject.getCoreSession().save(); // just in case
        
        return snapshot.getDocument();
    }

    private static void incrementVersionedSuprojectIdRecursive(DocumentModel subproject,
            String newVersionLabel) throws PropertyException, ClientException {
        String subprojectId = (String) subproject.getPropertyValue(Subproject.XPATH_SUBPROJECT);
        
        String newVersionedSubprojectId = subprojectId + newVersionLabel;
        subproject.setPropertyValue(Subproject.XPATH_SUBPROJECT, newVersionedSubprojectId);//TODO versioned
        
        DocumentModelIterator childrenIt = subproject.getCoreSession().getChildrenIterator(subproject.getRef());
        while (childrenIt.hasNext()) {
            DocumentModel child = childrenIt.next();
            incrementVersionedSuprojectIdRecursive(child, newVersionLabel);
        }
    }

    // from StandardVersioningService TODO NUXEO OPEN IT UP
    protected static long getVersion(DocumentModel doc, String prop) throws ClientException {
        Object propVal = doc.getPropertyValue(prop);
        if (propVal == null || !(propVal instanceof Long)) {
            return 0;
        } else {
            return ((Long) propVal).longValue();
        }
    }

    public static void copySubprojectNodePropertiesOnChildrenRecursive(DocumentModel subproject)
            throws PropertyException, ClientException {
        DocumentModelIterator childrenIt = subproject.getCoreSession().getChildrenIterator(subproject.getRef());
        while (childrenIt.hasNext()) {
            DocumentModel child = childrenIt.next();
            if (child.isProxy()) {
                logger.warn("   copySubprojectNodePropertiesOnChildrenRecursive avoiding proxy " + child);
                continue;
            }
            copySubprojectNodeProperties(subproject, child);
            child.getCoreSession().saveDocument(child); // else not persisted
            copySubprojectNodePropertiesOnChildrenRecursive(child);
        }
    }

    public static void copySubprojectNodeProperties(DocumentModel fromSubprojectNode,
            DocumentModel toModel) throws PropertyException, ClientException {
        DocumentModel spnode = null;
        if (fromSubprojectNode != null && fromSubprojectNode.hasFacet(SubprojectNode.FACET)) {
            // subproject known through parent => recopy spnode properties from it
            spnode = fromSubprojectNode;
            if (!toModel.hasFacet(SubprojectNode.FACET)) {
                toModel.addFacet(SubprojectNode.FACET);
            }
            // in case of null or changing subprojectId
            toModel.setPropertyValue(Subproject.XPATH_SUBPROJECT,
                    fromSubprojectNode.getPropertyValue(Subproject.XPATH_SUBPROJECT));

        } else if (toModel.hasFacet(SubprojectNode.FACET)) { // TODO not required ?!
            // subproject should be provided through property => get spnode properties from subproject
            String subprojectId = (String) toModel.getPropertyValue(Subproject.XPATH_SUBPROJECT);
            spnode = toModel.getCoreSession().getDocument(new IdRef(subprojectId));
        } else {
            logger.info("About to create document outside a subproject " + toModel);
        }
        if (spnode != null) {
            toModel.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS,
                    fromSubprojectNode.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS));
            toModel.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV,
                    fromSubprojectNode.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV));
            // TODO or using facet inheritance through spnode:subproject ?? NOOO documentService.createDocument() ex. ITS...
        }
    }
    
}
