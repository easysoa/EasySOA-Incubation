package org.easysoa.registry.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.SubprojectServiceImpl;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

/**
 * TODO merge with RepositoryHelper ??
 * 
 * @author mkalam-alami
 *
 */
public class DocumentModelHelper {

    public static final String WORKSPACEROOT_NAME = "workspaces";
    
    private static final Log log = LogFactory.getLog(DocumentModelHelper.class);

    public static String getDocumentTypeLabel(String doctype) {
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            return typeManager.getType(doctype).getLabel();
        } catch (Exception e) {
            log.warn("Failed to fetch document type label, falling back to type name instead.");
            return doctype;
        }
    }
    
    /**
     * TODO merge with RepositoryHelper ?
     * TODO create rather at Subproject creation (in event !) ??
     * TODO or (also) return DocumentModel ??
     * @param documentManager
     * @param subprojectId
     * @return
     * @throws ClientException
     */
    public static String getWorkspacesPath(CoreSession documentManager,
            String subprojectId) throws ClientException {
        DocumentModel subproject = SubprojectServiceImpl.getSubprojectOrCreateDefault(documentManager, subprojectId);
        //TODO or PathRef ??
        String subprojectPath = subproject.getPathAsString();
        PathRef subprojectWorkspacesRef = new PathRef(subprojectPath + '/' + DocumentModelHelper.WORKSPACEROOT_NAME);
        if (documentManager.exists(subprojectWorkspacesRef)) {
            //return documentManager.getDocument(subprojectWorkspacesRef);
            return subprojectWorkspacesRef.toString();
            
        } else {
            // create Repository for subproject
            // TODO or at Subproject creation (in event !) ??
            DocumentModel workspacesModel = documentManager.createDocumentModel(subprojectPath,
                    DocumentModelHelper.WORKSPACEROOT_NAME, "SystemTreeRoot");
            workspacesModel.setPropertyValue("dc:title", "My SOA documents");
            workspacesModel.setPropertyValue("dc:description", "Default SOA document tree");
            workspacesModel = documentManager.createDocument(workspacesModel);
            documentManager.save();//TODO ??
            return workspacesModel.getPathAsString();
        }
    }

}
