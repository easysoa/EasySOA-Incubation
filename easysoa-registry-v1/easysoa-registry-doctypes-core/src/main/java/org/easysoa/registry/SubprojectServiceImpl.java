package org.easysoa.registry;

import static org.easysoa.registry.utils.NuxeoListUtils.getIds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easysoa.registry.types.Project;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;

public class SubprojectServiceImpl {

    public static final PathRef defaultProjectPathRef = new PathRef(Project.DEFAULT_PROJECT_PATH);
    public static final PathRef defaultSubprojectPathRef = new PathRef(Subproject.DEFAULT_SUBPROJECT_PATH);

    private static final Object[] EMPTY_STRING_ARRAY = new String[0];
    
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
        
        onSubprojectAboutToCreate(documentManager, subprojectModel, parentSubprojectModels);
        
        subprojectModel = documentManager.saveDocument(subprojectModel);
        return subprojectModel;
    }
    
    /**
     * To be called on aboutToCreate
     * @param subprojectModel 
     * @param documentManager 
     * @param parentSubprojectModels 
     * @throws ClientException 
     */
    public static void onSubprojectAboutToCreate(CoreSession documentManager,
            DocumentModel subprojectModel, List<DocumentModel> parentSubprojectModels) throws ClientException {
        subprojectModel = documentManager.createDocument(subprojectModel); // save required to have id below // TODO rm thanks to path
        subprojectModel.setPropertyValue(Subproject.XPATH_SUBPROJECT, subprojectModel.getId()); // to get copied
        if (parentSubprojectModels != null && parentSubprojectModels.size() != 0) {
            subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, getIds(parentSubprojectModels).toArray(EMPTY_STRING_ARRAY));
        } else {
            subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, new String[0]); // else inits to new Serializable[0] TODO ??
        }
        //String visibleSubprojectIdsCsv = computeVisibleSubprojects(documentManager, subprojectModel);
        //subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS, visibleSubprojectIdsCsv);
        computeAndSetVisibleSubprojects(documentManager, subprojectModel);
        // NB. saveDocument() is auto done after aboutToCreate even by event system
        // TODO on documentCreated, update all below
    }

    /**
     * TODO only set if changed & then return true
     * TODO not public ??
     */
    public static void computeAndSetVisibleSubprojects(CoreSession documentManager, DocumentModel subprojectModel) throws ClientException {
        String[] parentSubprojectIds = (String[]) subprojectModel.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS);
        
        //String[] visibleSubprojectIds = (String[]) subprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS);
        ArrayList<String> visibleSubprojectIdList = new ArrayList<String>(parentSubprojectIds.length * 2);
        visibleSubprojectIdList.add(subprojectModel.getId());
        
        StringBuffer visibleSubprojectIdsCsvSbuf = new StringBuffer("'"); // NB. '\'' doesn't work, counts as int !!
        visibleSubprojectIdsCsvSbuf.append(subprojectModel.getId());
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
        Serializable visibleSubprojectIdsCsv = visibleSubprojectIdsCsvSbuf.toString();
        subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV, visibleSubprojectIdsCsv);
        // TODO only set if changed & then return true 
    }

    /**
     * TODO not public ??
     * @obsolete
     */
    private static String computeVisibleSubprojects(CoreSession documentManager, DocumentModel subprojectModel) throws ClientException {
        String[] parentSubprojectIds = (String[]) subprojectModel.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS);
        StringBuffer visibleSubprojectIdsSbuf = new StringBuffer("'"); // NB. '\'' doesn't work, counts as int !!
        visibleSubprojectIdsSbuf.append(subprojectModel.getId());
        visibleSubprojectIdsSbuf.append('\'');
        
        if (parentSubprojectIds != null && parentSubprojectIds.length != 0) {
            for (String parentSubprojectId : parentSubprojectIds) {
                DocumentModel parentSubprojectModel = documentManager.getDocument(new IdRef(parentSubprojectId));
                String parentVisibleSubprojectIds = (String) parentSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS);
                //  NB. should always be set, at least to itself
                visibleSubprojectIdsSbuf.append(',');
                visibleSubprojectIdsSbuf.append(parentVisibleSubprojectIds);
            }
        }
        return visibleSubprojectIdsSbuf.toString();
    }

    public static DocumentModel createSubrojectSpecifications(CoreSession documentManager, DocumentModel projectModel) throws ClientException {
        DocumentModel specificationsSubprojectModel = createSubproject(documentManager, "Specifications", projectModel, null);
        return specificationsSubprojectModel;
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
            subprojectId = SubprojectServiceImpl.getOrCreateDefaultSubproject(documentManager).getId();
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
            return getOrCreateDefaultSubproject(documentManager).getId();
        } else {
            return subprojectId;
        }
    }

    public static DocumentModel getSubprojectOrCreateDefault(CoreSession documentManager,
            String subprojectId) throws ClientException {
        if (subprojectId == null) {
            // default subproject mode (TODO rather create it at startup ? or explode ?? by query or path ?)
            return getOrCreateDefaultSubproject(documentManager);
        } else {
            // getting latest subproject conf (replacing it in case it is older)
            /*DocumentModelList subprojectResults = documentManager.query(
                    "SELECT * FROM Subproject WHERE ecm:uuid='" + subprojectId + "'");
            if (subprojectResults == null || subprojectResults.size() != 1) {
                throw new ClientException("No (or too much) subproject (" + subprojectResults + ") with id " + subprojectId);
            }
            subprojectDocModel = subprojectResults.get(0);*/
            return documentManager.getDocument(new IdRef(subprojectId)); // explodes if none
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
