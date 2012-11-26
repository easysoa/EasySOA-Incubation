package org.easysoa.registry;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ids.EndpointId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
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
		Assert.assertEquals(endpointId.getEnvironment(), createdEndpoint.getPropertyValue(Endpoint.XPATH_ENVIRONMENT));
		Assert.assertEquals(endpointId.getUrl(), createdEndpoint.getPropertyValue(Endpoint.XPATH_URL));
		
	}

    @Test
    public void testInvalidDiscovery() throws Exception {
        // Try to override Endpoint URL
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_URL, "Other URL");
        
        try {
        	discoveryService.runDiscovery(documentManager, endpointId, properties, null);
			Assert.fail("Update of an Endpoint URL must fail");
		} catch (ModelIntegrityException e) {
			logger.info("Discovery exception message: " + e.getMessage());
        }
        
        // Try to override Endpoint SOA name
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_SOANAME, "Other name");
        
        try {
        	discoveryService.runDiscovery(documentManager, endpointId, properties, null);
			Assert.fail("Update of an SOA name must fail");
		} catch (ModelIntegrityException e) {
			logger.info("Discovery exception message " + e.getMessage());
        }
    }
}
