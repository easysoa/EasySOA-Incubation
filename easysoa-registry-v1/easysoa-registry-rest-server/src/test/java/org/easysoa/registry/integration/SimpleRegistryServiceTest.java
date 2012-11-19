/**
 * 
 */
package org.easysoa.registry.integration;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author jguillemotte
 *
 */
@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class SimpleRegistryServiceTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(SimpleRegistryServiceTest.class);

    private SimpleRegistryServiceHelper simpleRegistryService = new SimpleRegistryServiceHelper(this);
    
    @Inject
    DocumentService documentService;
    
    @Inject
    DiscoveryService discoveryService;    
    
    private final static int SERVICE_COUNT = 5;
    
    @Before
    public void init() throws Exception {
        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE,
                    "MyService" + i), null, null);
        }
        documentManager.save();
    }
    
    @Test
    public void queryWSDLInterfaceTest(){
        logTestName(logger);

        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces/test/projectID");
        SoaNodeInformation[] soaNodes = discoveryRequest.get(SoaNodeInformation[].class);
        
        Assert.assertNotNull(soaNodes);
        // Check result
        //Assert.assertEquals("All registered services must be found", SERVICE_COUNT, soaNodes.length);
        //SoaNodeId firstSoaNodeId = soaNodes[0].getSoaNodeId();
        //Assert.assertEquals("'type' property must be provided for each document", InformationService.DOCTYPE,
        //        firstSoaNodeId.getType());
        //Assert.assertEquals("'name' property must be provided for each document", "MyService0",
        //        firstSoaNodeId.getName());        
    }
    
}
