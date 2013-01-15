package org.easysoa.registry;

import static org.easysoa.registry.utils.NuxeoListUtils.getIds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easysoa.registry.types.Subproject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class SubprojectServiceImpl {

    

    private static final Object[] EMPTY_STRING_ARRAY = new String[0];
    
    private static String projectRootPath = "/default-domain";
    /**
     * Creates but doesn't save session
     * @param documentManager
     * @param name
     * @return
     * @throws ClientException 
     */
    public static DocumentModel createProject(CoreSession documentManager, String name) throws ClientException {
        DocumentModel projectModel = documentManager.createDocumentModel("Project");
        projectModel.setPathInfo(projectRootPath, name); // TODO safeName()
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
        DocumentModel subprojectModel = documentManager.createDocumentModel("Subproject"); // triggers DocumentEventTypes.EMPTY_DOCUMENTMODEL_CREATED
        subprojectModel.setPathInfo(projectModel.getPathAsString(), name); // safeName()
        subprojectModel.setPropertyValue("dc:title", name);
        subprojectModel = documentManager.createDocument(subprojectModel); // save required to have id below
        subprojectModel.setPropertyValue(Subproject.XPATH_SUBPROJECT, subprojectModel.getId()); // to get copied
        if (parentSubprojectModels != null && parentSubprojectModels.size() != 0) {
            subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, getIds(parentSubprojectModels).toArray(EMPTY_STRING_ARRAY));
        } else {
            subprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, new String[0]); // else inits to new Serializable[0] TODO ??
        }
        //String visibleSubprojectIdsCsv = computeVisibleSubprojects(documentManager, subprojectModel);
        //subprojectModel.setPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS, visibleSubprojectIdsCsv);
        computeAndSetVisibleSubprojects(documentManager, subprojectModel);
        subprojectModel = documentManager.saveDocument(subprojectModel);
        return subprojectModel;
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
    
}
