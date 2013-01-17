package org.easysoa.registry.indicators.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class IndicatorsControllerTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(IndicatorsControllerTest.class);

    @Test
    public void testIndicators() throws Exception {
        
        // Fill repository for all tests :
        
        // endpoints
        SoaNodeId endpointId = new EndpointId("Production", "MyEndpoint");
        discoveryService.runDiscovery(documentManager, endpointId, null, null);
        discoveryService.runDiscovery(documentManager, new EndpointId("Production", "MyEndpoint1"),
                null, null);
        discoveryService.runDiscovery(documentManager, new EndpointId("Production", "MyEndpoint2"),
                null, null);
        
		// service impls
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ServiceImplementation.XPATH_DOCUMENTATION,
        		"Blah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah");
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        properties.put(ServiceImplementation.XPATH_ISMOCK, "true");
        discoveryService.runDiscovery(documentManager, serviceImplId, properties, null);
        
        properties.clear();
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImplNotMock"), properties, null);
        
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyNotMockedImpl"), null, null);
        documentManager.save();
        
        // development project (as folder, or could be in model TODO, especially since can be discovered from root pom (though it is not only that))

        // (technical component (same ?!?))

        
        // business component (as folder, or could be in model TODO)
        SoaNodeId businessProcessSystem1Id = new SoaNodeId(TaggingFolder.DOCTYPE, "BusinessProcessSystem1");
        discoveryService.runDiscovery(documentManager, businessProcessSystem1Id, null, null);
        SoaNodeId businessProcess1SoftwareComponent1Id = new SoaNodeId(SoftwareComponent.DOCTYPE, "BusinessProcess1SoftwareComponent1");
        discoveryService.runDiscovery(documentManager, businessProcess1SoftwareComponent1Id, null, Arrays.asList(businessProcessSystem1Id)); // consists in
        //discoveryService.runDiscovery(documentManager, service0Id, null, Arrays.asList(businessProcess1SoftwareComponent1Id)); // consumes NO rather deliverables
        SoaNodeId deliverable0id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable0");
        discoveryService.runDiscovery(documentManager, deliverable0id, null, Arrays.asList(businessProcess1SoftwareComponent1Id));
        SoaNodeId serviceImplementation0id = new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation0");
        discoveryService.runDiscovery(documentManager, serviceImplementation0id, null, Arrays.asList(deliverable0id));
        discoveryService.runDiscovery(documentManager, serviceImplementation0id, null, null);
        SoaNodeId deliverable1id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable1");
        discoveryService.runDiscovery(documentManager, deliverable1id, null, null); // deliverable in no business process
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, null);
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, Arrays.asList(deliverable1id));
        
        SoaNodeId noBusinessProcessSoftwareComponentId = new SoaNodeId(SoftwareComponent.DOCTYPE, "NoBusinessProcessSoftwareComponent");
        discoveryService.runDiscovery(documentManager, noBusinessProcessSoftwareComponentId, null, null);
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Deliverable.DOCTYPE, "noBusinessProcessDeliverable"), null, Arrays.asList(noBusinessProcessSoftwareComponentId));

        // test software component

        documentManager.save();
        logRepository();

        // Fetch indicators page :
        Client client = createAuthenticatedHTTPClient();
        Builder indicatorsReq = client.resource(this.getURL(IndicatorsController.class)).accept(MediaType.APPLICATION_JSON);
        String res = indicatorsReq.get(String.class);
       /*JsonNode indicators =*/ new ObjectMapper().readValue(res, JsonNode.class);
        
       /* Assert.assertEquals("Indicators must be computed and return the expected values",
                1, indicators.get(IndicatorsController.CATEGORY_DOCTYPE_SPECIFIC)
                    .get("softwareComponentInNoTaggingFolder").get("count").getIntValue());*/

        logger.info(res);
    }
    
}
