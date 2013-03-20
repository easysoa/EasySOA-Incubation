package org.easysoa.registry;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

@RepositoryConfig(cleanup = Granularity.CLASS)
public class ModelIntegrityTests extends AbstractRegistryTest {

    private static Logger logger = Logger.getLogger(DiscoveryServiceTest.class);
	
	@Inject
	private DocumentService docService;

    @Inject
    DiscoveryService discoveryService;
    
	private EndpointId endpointId = new EndpointId("Production", "http://www.myservice.com");

    private static Map<String, Object> properties;
    
	@Test
	public void testEndpointsIntegrity() throws ClientException {
		// Create endpoint
		DocumentModel createdEndpoint = docService.create(documentManager, endpointId);
		documentManager.save();
		
		// Check default values
		Assert.assertEquals(endpointId.getEnvironment(), createdEndpoint.getPropertyValue(Endpoint.XPATH_ENDP_ENVIRONMENT));
		Assert.assertEquals(endpointId.getUrl(), createdEndpoint.getPropertyValue(Endpoint.XPATH_URL));
	}

    @Test
    public void testSoaNameGeneration() throws ClientException {
    	// Create endpoint without SOA name & make sure it has been generated
    	DocumentModel endpointModel = documentManager.createDocumentModel(
                DocumentModelHelper.getWorkspacesPath(documentManager, defaultSubprojectId),
    			"endpoint", Endpoint.DOCTYPE);
    	endpointModel.setPropertyValue(Endpoint.XPATH_ENDP_ENVIRONMENT, "Production");
    	endpointModel.setPropertyValue(Endpoint.XPATH_URL, "MyEndpointURL");
    	endpointModel = documentManager.createDocument(endpointModel);
    	Assert.assertEquals("Production:MyEndpointURL", endpointModel.getPropertyValue(Endpoint.XPATH_SOANAME));

    	// Create endpoint without SOA name nor sufficient info to generate it & make sure it has failed
    	endpointModel = documentManager.createDocumentModel(DocumentModelHelper
                .getWorkspacesPath(documentManager, defaultSubprojectId), "endpoint2", Endpoint.DOCTYPE);
    	try {
    		documentManager.createDocument(endpointModel);
    		Assert.fail("Creation of an incomplete SoaNode must not work");
    	}
    	catch (Exception e) {
    		logger.info("Document creation exception message: " + e.getCause().getMessage());
    	}

    	// Create Software Component without SOA name: in that case, there's no rule to manage the SOA name,
    	// so it must be created using the document title
    	DocumentModel softCompModel = documentManager.createDocumentModel(DocumentModelHelper
                .getWorkspacesPath(documentManager, defaultSubprojectId),
    			"softwarecomponent", SoftwareComponent.DOCTYPE);
    	softCompModel.setPropertyValue(SoftwareComponent.XPATH_TITLE, "MySoftwareComponent");
    	softCompModel = documentManager.createDocument(softCompModel); // XXX SoaName is not set on the createDocument() result, but is be saved eventually
    	//softCompModel = documentManager.getDocument(softCompModel.getRef());
    	Assert.assertEquals("MySoftwareComponent", softCompModel.getPropertyValue(SoftwareComponent.XPATH_SOANAME));
    }
    
    @Test
    public void testInvalidDiscovery() throws Exception {
        // Try to override Endpoint URL
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_URL, "Other URL");
        
        try {
        	discoveryService.runDiscovery(documentManager, endpointId, properties, null, "strict");
			Assert.fail("Update of an Endpoint URL must fail");
		} catch (ModelIntegrityException e) {
			logger.info("Discovery exception success");
        }
        
        // Try to override Endpoint SOA name
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_SOANAME, "Other name");
        
        try {
        	discoveryService.runDiscovery(documentManager, endpointId, properties, null, "strict");
			Assert.fail("Update of an SOA name must fail");
		} catch (ModelIntegrityException e) {
			logger.info("Discovery exception message " + e.getMessage());
        }
    }
}
