package org.easysoa.registry.utils;

import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Repository;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * TODO merge with DocumentModelHelper ??
 * @author mdutoo
 *
 */
public class RepositoryHelper {
    
    public static DocumentModel getRepository(CoreSession documentManager,
            String subprojectId) throws ClientException {
        DocumentModel subproject = SubprojectServiceImpl.getSubprojectOrCreateDefault(documentManager, subprojectId);
        //TODO or PathRef ??
        String subprojectPath = subproject.getPathAsString();
        PathRef subprojectRepositoryRef = new PathRef(subprojectPath + '/' + Repository.REPOSITORY_NAME);
        if (documentManager.exists(subprojectRepositoryRef)) {
            return documentManager.getDocument(subprojectRepositoryRef);
            
        } else {
            // create Repository for subproject
            // TODO or at Subproject creation (in event !) ??
            DocumentModel repositoryModel = documentManager.createDocumentModel(subprojectPath,
                    Repository.REPOSITORY_NAME, Repository.DOCTYPE);
            repositoryModel.setPropertyValue("dc:title", Repository.REPOSITORY_TITLE);
            repositoryModel = documentManager.createDocument(repositoryModel);
            documentManager.save();//TODO ??
            return repositoryModel;
        }
    }

    public static String getRepositoryPath(CoreSession documentManager, String subprojectId) throws ClientException {
        DocumentModel subproject = SubprojectServiceImpl.getSubprojectOrCreateDefault(documentManager, subprojectId);
        //TODO or PathRef ??
        String subprojectPath = subproject.getPathAsString();
        return subprojectPath + '/' + Repository.REPOSITORY_NAME;
    }

    public static String getRepositoryPath(CoreSession documentManager, DocumentModel doc) throws ClientException {
    	String subprojectId = SubprojectServiceImpl.getSubprojectId(doc);
        DocumentModel subproject = SubprojectServiceImpl.getSubprojectOrCreateDefault(documentManager, subprojectId);
        //TODO or PathRef ??
        String subprojectPath = subproject.getPathAsString();
        return subprojectPath + '/' + Repository.REPOSITORY_NAME;
    }
    
}
