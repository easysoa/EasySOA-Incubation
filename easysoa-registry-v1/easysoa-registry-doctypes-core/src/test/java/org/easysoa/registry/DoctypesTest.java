package org.easysoa.registry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.SchemaManager;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
public class DoctypesTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DoctypesTest.class);

    @Inject
    SchemaManager schemaManager;

    @Inject
    DocumentService documentService;
    
    @Test
    public void testDoctypesAvailability() throws ClientException {
        
        String[] doctypes = new String[] {
                Deliverable.DOCTYPE,
                DeployedDeliverable.DOCTYPE,
                Endpoint.DOCTYPE,
                IntelligentSystem.DOCTYPE,
                IntelligentSystemTreeRoot.DOCTYPE,
                Repository.DOCTYPE,
                InformationService.DOCTYPE,
                ServiceImplementation.DOCTYPE,
                SoftwareComponent.DOCTYPE,
                SystemTreeRoot.DOCTYPE,
                TaggingFolder.DOCTYPE
        };
        
        for (String doctype : doctypes) {
            Assert.assertNotNull("Doctype " + doctype + " must exist", schemaManager.getDocumentType(doctype));
        }
        
    }

    @Test
    public void testDocumentAdapters() throws ClientException {
     
        // Create a deliverable
        DocumentModel deliverableModel = documentService.create(documentManager, 
                new SoaNodeId(Deliverable.DOCTYPE, "MyDeliverable"),
                DocumentModelHelper.getWorkspacesPath(documentManager, defaultSubprojectId));
        
        // Use its adapter
        Deliverable deliverable = deliverableModel.getAdapter(Deliverable.class);
        Assert.assertNotNull("Document model must be adapted as a deliverable", deliverable);
        
    }
    
    @Test
    public void testServiceImplComplexProps() throws Exception {

        // Create ServiceImpl
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
        DocumentModel serviceImplModel = documentService.create(documentManager, 
                serviceImplId);
        
        String opName = "Yo";
        String params = "Param1:java.lang.int, Param2:my.Class";
        String returnParams = "YoResponse:java.lang.String";
        String opDoc = "This does something";
        String inContentType = "text/xml";
        String outContentType = "application/json"; // http://stackoverflow.com/questions/477816/what-is-the-correct-json-content-type
        
        // Use adapter to manipulate operations
        ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
        List<OperationInformation> operations = serviceImpl.getOperations();
        operations.add(new OperationInformation(opName, params, returnParams, opDoc, inContentType, outContentType));
        serviceImpl.setOperations(operations);
        List<String> tests = new ArrayList<String>();
        tests.add("org.easysoa.TestClass1");
        tests.add("org.easysoa.TestClass2");
        serviceImpl.setTests(tests);
        
        // Save
        documentManager.saveDocument(serviceImplModel);
        documentManager.save();
        
        // Fetch document again, check operations update
        serviceImplModel = documentService.findSoaNode(documentManager, serviceImplId);
        serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
        operations = serviceImpl.getOperations();
        Assert.assertEquals(1, operations.size());
        Assert.assertEquals(opName, operations.get(0).getName());
        Assert.assertEquals(params, operations.get(0).getParameters());
        Assert.assertEquals(returnParams, operations.get(0).getReturnParameters());
        Assert.assertEquals(opDoc, operations.get(0).getDocumentation());
        Assert.assertEquals(inContentType, operations.get(0).getInContentType());
        Assert.assertEquals(outContentType, operations.get(0).getOutContentType());
        Assert.assertEquals(opDoc, operations.get(0).getDocumentation());
        Assert.assertEquals(2, serviceImpl.getTests().size());
    }
    
    @Test
    public void testEndpointConsumptionRelations() throws Exception {
        // Create endpoint consumption
        SoaNodeId endpointConsumptionId = new SoaNodeId(EndpointConsumption.DOCTYPE, "MyConsumption");
        DocumentModel endpointConsumptionModel = documentService.create(documentManager, endpointConsumptionId);
        EndpointConsumption endpointConsumption = endpointConsumptionModel.getAdapter(EndpointConsumption.class);
        Assert.assertNotNull("EndpointConsumption adapter must be available", endpointConsumption);
        documentManager.save();
        
        // Manipulate and test it
        Assert.assertNull("EndpointConsumption must not initially consume endpoints",
                endpointConsumption.getConsumedEndpoint());
        SoaNodeId consumedEndpoint = new EndpointId("myenv", "myurl");
        endpointConsumption.setConsumedEndpoint(consumedEndpoint);
        Assert.assertEquals("EndpointConsumption must be set as expected", consumedEndpoint,
                endpointConsumption.getConsumedEndpoint());
        DocumentModel foundEndpointModel = documentService.findSoaNode(documentManager, consumedEndpoint);
        Assert.assertNotNull("Consumed endpoint must be created", foundEndpointModel);
        endpointConsumption.setConsumedEndpoint(null);
        Assert.assertNull("EndpointConsumption must be removed",
                endpointConsumption.getConsumedEndpoint());
    }
}
