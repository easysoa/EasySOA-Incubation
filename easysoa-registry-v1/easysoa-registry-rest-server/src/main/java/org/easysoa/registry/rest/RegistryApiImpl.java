package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeRequest;
import org.easysoa.registry.rest.marshalling.SoaNodeResult;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * REST discovery API
 * 
 * For now try to put info discovered in source code the simplest way
 * 
 * later (once indicators are computed...) soanodeinformation may need to be be detailed (add correlation direction, full model...)
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/registry")
public class RegistryApiImpl implements RegistryApi {
    
    @Context HttpServletRequest request;

    public OperationResult post(SoaNodeRequest soaNodeRequest) throws Exception {
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);

            // Run discovery
            discoveryService.runDiscovery(documentManager, soaNodeRequest.getSoaNodeId(),
                    toObjectProperties(soaNodeRequest.getProperties()), soaNodeRequest.getParentIds());
            documentManager.save();
            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to update or create one the documents", e);
        }
    }
    
    public SoaNodeResult[] query(String query) throws Exception {
        try {
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentModelList modelList = documentManager.query(query + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER);
            SoaNodeResult soaNodes[] = new SoaNodeResult[modelList.size()];
            int i = 0;
            for (DocumentModel model : modelList) {
                soaNodes[i++] = SoaNodeInformationFactory.create(documentManager, model);
            }
            return soaNodes;
        } catch (Exception e) {
            throw new Exception("Failed to query documents with '" + query +"'", e);
        }
    }
    
    public SoaNodeResult get() throws Exception {
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentModel document = documentManager.getDocument(new PathRef("/default-domain/workspaces"));
        return SoaNodeInformationFactory.create(documentManager, document); // FIXME WorkspaceRoot is not a SoaNode
    }

    public SoaNodeResult[] get(String doctype) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        // Fetch SoaNode list
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE "
                + "ecm:currentLifeCycleState <> 'deleted' AND "
                + "ecm:isCheckedInVersion = 0 AND " + "ecm:isProxy = 0",
                new Object[] { doctype }, false, true);
        DocumentModelList soaNodeModelList = documentManager.query(query);

        // Convert data for marshalling
        List<SoaNodeResult> modelsToMarshall = new LinkedList<SoaNodeResult>();
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            modelsToMarshall.add(new SoaNodeResult(documentService.createSoaNodeId(soaNodeModel),
                    null, null));
        }

        // Write response
        return modelsToMarshall.toArray(new SoaNodeResult[]{});
    }

    public SoaNodeResult get(String doctype, String name) throws Exception {
        SoaNodeId id = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);
    
            // Fetch SoaNode
            DocumentModel foundDocument = documentService.find(documentManager, id);
            if (foundDocument == null) {
                throw new Exception("Document doesnt exist"); // TODO 404
            }
            else {
                return SoaNodeInformationFactory.create(documentManager, foundDocument);
            }
        }
        catch (Exception e) {
        	// TODO 500
            throw new Exception("Failed to fetch document " + id.toString(), e);
        }
    }

    public OperationResult delete(String doctype, String name) throws ClientException {
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Delete SoaNode
            documentService.delete(documentManager, soaNodeId);

            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to delete document " + soaNodeId.toString(), e);
        }
    }
    
    public OperationResult delete(String doctype, String name, String correlatedDoctype,
    		String correlatedName) throws ClientException {
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name),
                correlatedSoaNodeId = new SoaNodeId(correlatedDoctype, correlatedName);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Delete proxy of SoaNode
            DocumentModel correlatedSoaNodeModel = documentService.find(documentManager, correlatedSoaNodeId);
            if (correlatedSoaNodeModel != null) {
                documentService.deleteProxy(documentManager, soaNodeId, correlatedSoaNodeModel.getPathAsString());
            }
            else {
                throw new Exception("Correlated SoaNode does not exist");
            }

            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to delete document " + soaNodeId.toString(), e);
        }
    }

    private Map<String, Object> toObjectProperties(Map<String, Serializable> properties) {
        Map<String, Object> objectProperties = new HashMap<String, Object>();
        objectProperties.putAll(properties);
        return objectProperties;
    }
}
