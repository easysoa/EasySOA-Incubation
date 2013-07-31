package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * REST discovery API impl.
 *
 * About implementation :
 * For now try to put info discovered in source code the simplest way.
 * Later (once indicators are computed...) soanodeinformation may need to be be detailed
 * (add correlation direction, full model...)
 *
 * @author mkalam-alami
 *
 */
@Path("easysoa/registry") // even if already in itf else won't be deployed
// (because at root there's already the default Nuxeo sites web UI)
// because Jersey doesn't (mostly) inherit @Path, see
// http://jersey.576304.n2.nabble.com/Inheritance-of-Annotations-and-Jersey-td3225397.html
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RegistryApiImpl implements RegistryApi {

    @Context HttpServletRequest request;

    public OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception {
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);

            // Run discovery
            discoveryService.runDiscovery(documentManager, soaNodeInfo.getSoaNodeId(),
                    toObjectProperties(soaNodeInfo.getProperties()), soaNodeInfo.getParentDocuments());
            documentManager.save();
            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Error while discovering " + soaNodeInfo
                    + " with properties " + soaNodeInfo.getProperties()
                    + " and parents " + soaNodeInfo.getParentIds(), e);
        }
    }

    public SoaNodeInformations query(String subprojectId, String query) throws Exception {
        try {
            CoreSession documentManager = SessionFactory.getSession(request);
        	DocumentService docService = Framework.getService(DocumentService.class);

            //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
            // TODO default or not ??
            String subprojectPathCriteria;
            if (subprojectId == null || subprojectId.length() == 0) {
                subprojectPathCriteria = "";
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            }

            DocumentModelList modelList = docService.query(documentManager, query
                    + DocumentService.NXQL_NO_DELETED_DOCUMENTS_CRITERIA + subprojectPathCriteria, true, false);
            //SoaNodeInformation soaNodes[] = new SoaNodeInformation[modelList.size()];
            SoaNodeInformations soaNodes = new SoaNodeInformations();
            int i = 0;
            for (DocumentModel model : modelList) {
                //soaNodes[i++] = SoaNodeInformationFactory.create(documentManager, model);
                soaNodes.addSoaNodeInformation(SoaNodeInformationFactory.create(documentManager, model));
            }
            return soaNodes;
        } catch (Exception e) {
            throw new Exception("Failed to query documents with '" + query +"'", e);
        }
    }

    public SoaNodeInformation get(String subprojectId) throws Exception {
        CoreSession documentManager = SessionFactory.getSession(request);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        // TODO default or not ??
        /*String subprojectPathCriteria;
        if (subprojectId == null || subprojectId.length() == 0) {
            subprojectPathCriteria = "";
        } else {
            subprojectPathCriteria = " " + IndicatorProvider.NXQL_PATH_STARTSWITH
                    + subprojectId.split("_v")[0] + "'";
        }*/

        DocumentModel document = documentManager.getDocument(new PathRef(
                DocumentModelHelper.getWorkspacesPath(documentManager, subprojectId)));
        return SoaNodeInformationFactory.create(documentManager, document);
    }

    public SoaNodeInformations get(String subprojectId, String doctype) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null || subprojectId.length() == 0) {
            subprojectPathCriteria = "";
        } else {
            subprojectPathCriteria = DocumentService.NXQL_AND + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
        }

        // Fetch SoaNode list
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE "
                + "ecm:currentLifeCycleState <> 'deleted' AND "
                + "ecm:isCheckedInVersion = 0 AND " + "ecm:isProxy = 0" + subprojectPathCriteria,
                new Object[] { doctype }, false, true);
        DocumentModelList soaNodeModelList = documentManager.query(query);

        // Convert data for marshalling
        List<SoaNodeInformation> modelsToMarshall = new LinkedList<SoaNodeInformation>();
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            modelsToMarshall.add(new SoaNodeInformation(documentService.createSoaNodeId(soaNodeModel),
                    null, null));
        }

        // Write response
        //return modelsToMarshall.toArray(new SoaNodeInformation[]{});
        SoaNodeInformations soaNodes = new SoaNodeInformations();
        soaNodes.setSoaNodeInformationList(modelsToMarshall);
        return soaNodes;
    }

    public SoaNodeInformation get(String subprojectId, String doctype, String name) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        SoaNodeId soaNodeId = new SoaNodeId(subprojectId, doctype, name);

        try {
            // Fetch SoaNode
            DocumentModel foundDocument = documentService.findSoaNode(documentManager, soaNodeId);
            if (foundDocument == null) {
                throw new Exception("Document doesnt exist"); // TODO 404
            }
            else {
                return SoaNodeInformationFactory.create(documentManager, foundDocument);
            }
        }
        catch (Exception e) {
        	// TODO 500
            throw new Exception("Failed to fetch document " + soaNodeId, e);
        }
    }

    public OperationResult delete(String subprojectId, String doctype, String name) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        SoaNodeId soaNodeId = new SoaNodeId(subprojectId, doctype, name);

        try {
            // Delete SoaNode
            documentService.delete(documentManager, soaNodeId);

            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to delete document " + soaNodeId, e);
        }
    }

    public OperationResult delete(String subprojectId, String doctype, String name,
            String correlatedSubprojectId, String correlatedDoctype, String correlatedName) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId);
        SoaNodeId soaNodeId = new SoaNodeId(subprojectId, doctype, name);
        // TODO findCorrelated() in visibleSubprojects ??
        correlatedSubprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, correlatedSubprojectId);
        SoaNodeId correlatedSoaNodeId = new SoaNodeId(correlatedSubprojectId, correlatedDoctype, correlatedName);

        try {
            // Delete proxy of SoaNode
            DocumentModel correlatedSoaNodeModel = documentService.findSoaNode(documentManager, correlatedSoaNodeId);
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
