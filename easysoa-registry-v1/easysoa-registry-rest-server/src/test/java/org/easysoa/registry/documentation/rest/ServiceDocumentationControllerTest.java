package org.easysoa.registry.documentation.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
@Ignore // FIXME Outdated after removal of Service doctype
public class ServiceDocumentationControllerTest extends AbstractRestApiTest {

    public ServiceDocumentationControllerTest() {
        super();
        setLogRepositoryAfterEachTest(true);
    }

    private static Logger logger = Logger.getLogger(ServiceDocumentationControllerTest.class);

    @Test
    public void testServiceDocumentation() throws Exception {
        
        // Fill repository for all tests :
        
        // endpoints
        SoaNodeId endpointId = new SoaNodeId(Endpoint.DOCTYPE, "test:http://localhost:MyEndpoint");
        discoveryService.runDiscovery(documentManager, endpointId, null, null, "strict");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "test:http://localhost:MyEndpoint1"), null, null, "strict");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "test:http://localhost:MyEndpoint2"), null, null, "strict");
        
		// service impls
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
        Map<String, Object> properties = new HashMap<String, Object>();
        /*ListProperty operations = new org.nuxeo.ecm.core.api.model.impl.ListProperty(null, null);
        MapProperty operation1 = new MapProperty(operations, null).setValue(value);
        //operations.add(operations1);
        operation1.put("operationParameters", new StringProperty(operation1, null, 0));
        StringProperty operationName = new StringProperty(operation1, null, 0);
        operationName.setValue(value);
        operation1.put("operationName",  "getOrdersNumber");
        operation1.put("operationDocumentation", "Method: GET, Path: \"/orders/{clientName}\", Description: Returns the orders number for the specified client name.");*/
        ArrayList<Object> operations = new ArrayList<Object>();
        Map<String, Object> operation1 = new HashMap<String, Object>();
        operation1.put("operationParameters", null);
        operation1.put("operationName", "getOrdersNumber");
        operation1.put("operationDocumentation", "Method: GET, Path: \"/orders/{clientName}\", Description: Returns the orders number for the specified client name.");
        operations.add(operation1);
        properties.put(ServiceImplementation.XPATH_OPERATIONS, operations);
        properties.put(ServiceImplementation.XPATH_DOCUMENTATION,
        		"Blah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah");
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        properties.put(ServiceImplementation.XPATH_ISMOCK, "true");
        discoveryService.runDiscovery(documentManager, serviceImplId, properties, null, "strict");
        
        properties.clear();
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImplNotMock"), properties, null, "strict");
        
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyNotMockedImpl"), null, null, "strict");
        documentManager.save();
        
        // development project (as folder, or could be in model TODO, especially since can be discovered from root pom (though it is not only that))

        // (technical component (same ?!?))

        
        // business component (as folder, or could be in model TODO)
        SoaNodeId businessProcessSystem1Id = new SoaNodeId(TaggingFolder.DOCTYPE, "BusinessProcessSystem1");
        discoveryService.runDiscovery(documentManager, businessProcessSystem1Id, null, null, "strict");
        SoaNodeId businessProcess1SoftwareComponent1Id = new SoaNodeId(SoftwareComponent.DOCTYPE, "BusinessProcess1SoftwareComponent1");
        discoveryService.runDiscovery(documentManager, businessProcess1SoftwareComponent1Id, null, Arrays.asList(businessProcessSystem1Id), "strict"); // consists in
        //discoveryService.runDiscovery(documentManager, service0Id, null, Arrays.asList(businessProcess1SoftwareComponent1Id)); // consumes NO rather deliverables
        SoaNodeId deliverable0id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable0");
        discoveryService.runDiscovery(documentManager, deliverable0id, null, Arrays.asList(businessProcess1SoftwareComponent1Id), "strict");
        SoaNodeId serviceImplementation0id = new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation0");
        discoveryService.runDiscovery(documentManager, serviceImplementation0id, null, Arrays.asList(deliverable0id), "strict");
        SoaNodeId deliverable1id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable1");
        discoveryService.runDiscovery(documentManager, deliverable1id, null, null, "strict"); // deliverable in no business process
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, null, "strict");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, Arrays.asList(deliverable1id), "strict");
        
        SoaNodeId noBusinessProcessSoftwareComponentId = new SoaNodeId(SoftwareComponent.DOCTYPE, "NoBusinessProcessSoftwareComponent");
        discoveryService.runDiscovery(documentManager, noBusinessProcessSoftwareComponentId, null, null, "strict");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Deliverable.DOCTYPE, "noBusinessProcessDeliverable"), null, Arrays.asList(noBusinessProcessSoftwareComponentId), "strict");

        // test software component

        // user classified business component
        /*DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, name);
        documentModel.setProperty("dublincore", "title", title);
        documentModel = documentManager.createDocument(documentModel);*/
        documentService.createDocument(documentManager, "Workspace", "Business", DocumentModelHelper
                .getWorkspacesPath(documentManager, defaultSubprojectId), "Business");
        DocumentModel business1Folder = documentService.createDocument(documentManager,
                SystemTreeRoot.DOCTYPE, "Business1", DocumentModelHelper
                .getWorkspacesPath(documentManager, defaultSubprojectId), "Business1");
        // first BP (user created) :
        DocumentModel b1p2 = documentService.create(documentManager, new SoaNodeId(TaggingFolder.DOCTYPE, "Business1Process2"),
                DocumentModelHelper.getWorkspacesPath(documentManager, defaultSubprojectId) + "/Business/Business1"); // will be auto reclassified
        b1p2.setPropertyValue("dc:title", "Business1Process2");
        documentManager.save();
        documentService.createSoaNodeId(b1p2);
        // 2nd BP (reused) :
        documentManager.createProxy(new PathRef(RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId) + "/TaggingFolder/BusinessProcessSystem1"), business1Folder.getRef());
        
        // tag without service : 
        SoaNodeId tagWithoutService = new SoaNodeId(TaggingFolder.DOCTYPE, "tagWithoutService");
        discoveryService.runDiscovery(documentManager, tagWithoutService, null, null, "strict");
        
        documentManager.save();
        logRepository();

        Client client = createAuthenticatedHTTPClient();
        
        // Fetch services page :
        Builder servicesReq = client.resource(this.getURL(ServiceDocumentationController.class)).accept(MediaType.TEXT_HTML);
        String res = servicesReq.get(String.class);
        logger.info(res);

        // Fetch service doc page :
        Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("path/" + "default-domain/repository/Service/MyService0").accept(MediaType.TEXT_HTML); // impl case
        //Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
        //        .path("default-domain/repository/Service/MyService1").accept(MediaType.TEXT_HTML);
        //Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
        //        .path("default-domain/repository/TaggingFolder/Tag0/MyService0").accept(MediaType.TEXT_HTML); // proxy case
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains("MyService0"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("tag/default-domain/repository/TaggingFolder/BusinessProcessSystem1").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains(RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId) + "/Service/BusinessProcessSystem1Service1"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("tag/default-domain/workspaces/Business/Business1/Business1Process2").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains(RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId) + "/Service/Business1Process2Service1"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("default-domain/repository/Service/BusinessProcessSystem1Service1/tags").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains(RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId) + "/TaggingFolder/Business1Process2"));
        
        // validation - internal services (consumption) :
        // for (service : getInternalServices(context))
        
        // validation - services promoted from outside :
        // for (service : application/proxiedServices) if !isCompliantTo(service.itf, application.getReqItf(service) alert("inconsistent!")
        // for (businessService : application/businessServices) if !isCompliantTo(businessService/proxiedTechnicalService.itf, businessService.itf alert("inconsistent!")

        // validation - services promoted to outside vs reqs :
        // for (publishedService : application/publishedServices) if !isCompliantTo(publishedService.itf, application.getPublishedReqItf(publishedService) alert("inconsistent!")
    }
    
}
