package org.easysoa.registry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 */
@RepositoryConfig(cleanup = Granularity.CLASS)
public class DiscoveryServiceTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DiscoveryServiceTest.class);

    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    @Inject
    SoaMetamodelService soaMetamodelService;

    private static SoaNodeId discoveredEndpointId;

    private static Map<String, Object> properties;
    
    private static DocumentModel foundEndpoint;
    
    @Test
    public void testSimpleDiscovery() throws Exception {
        // Gather discovery information
        discoveredEndpointId = new EndpointId("Production", "http://www.services.com/endpoint");
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_TITLE, "My Endpoint");
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredEndpointId, properties, null);
        documentManager.save();
        
        // Check results
        foundEndpoint = documentService.find(documentManager, discoveredEndpointId);
        Assert.assertNotNull("An endpoint must be created by the discovery processing", foundEndpoint);
        properties.put(Endpoint.XPATH_URL, "http://www.services.com/endpoint"); // URL must be set automatically
        for (Entry<String, Object> property : properties.entrySet()) {
            Assert.assertEquals("Property " + property.getKey() + " must match value from discovery",
                    property.getValue(), foundEndpoint.getPropertyValue(property.getKey()));
        }
    }
    
    @Test
    public void testSoaMetamodelService() throws Exception {
        // Check a few random values of the default contribution
        Assert.assertTrue("The default contributions must be loaded",
                soaMetamodelService.getChildren(Deliverable.DOCTYPE).contains(ServiceImplementation.DOCTYPE));
        Assert.assertTrue("The default contributions must be loaded",
                soaMetamodelService.getChildren(SoftwareComponent.DOCTYPE).contains(Deliverable.DOCTYPE));

        // Test a random path
        Assert.assertArrayEquals("Subtypes chain must be valid", 
                new Object[]{ Deliverable.DOCTYPE, ServiceImplementation.DOCTYPE },
                soaMetamodelService.getPath(SoftwareComponent.DOCTYPE, ServiceImplementation.DOCTYPE).toArray());
    }
    
    @Test
    public void testCorrelationDiscovery() throws Exception {
        
        // Add correlation information
        List<SoaNodeId> parentDocuments = new LinkedList<SoaNodeId>();
        SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa.services:myservices");
        parentDocuments.add(deliverableId);
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "myserviceimpl");
        parentDocuments.add(serviceImplId);
        SoaNodeId softwareCompId = new SoaNodeId(SoftwareComponent.DOCTYPE, "mysoftwarecomponent");
        parentDocuments.add(softwareCompId);
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredEndpointId, null, parentDocuments);
        documentManager.save();
        
        // Check results
        DocumentModel foundSoftComp = documentService.find(documentManager, softwareCompId);
        Assert.assertTrue(softwareCompId + " must be linked to " + deliverableId, 
                documentService.hasChild(documentManager, foundSoftComp, deliverableId));
        
        DocumentModel foundDeliverable = documentService.find(documentManager, deliverableId);
        Assert.assertTrue(deliverableId + " must be linked to " + serviceImplId, 
                documentService.hasChild(documentManager, foundDeliverable, serviceImplId));
        
        DocumentModel foundServiceImpl = documentService.find(documentManager, serviceImplId);
        Assert.assertTrue(serviceImplId + " must be linked to " + discoveredEndpointId, 
                documentService.hasChild(documentManager, foundServiceImpl, discoveredEndpointId));

        Assert.assertFalse(foundSoftComp + " must not be linked to " + discoveredEndpointId, 
                documentService.hasChild(documentManager, foundSoftComp, discoveredEndpointId));
    }

    @Test
    public void testMerge() throws Exception {
        // Rediscover the same endpoint with new info
        properties = new HashMap<String, Object>();
        properties.put("dc:description", "Blahblah");
        discoveryService.runDiscovery(documentManager, discoveredEndpointId, properties, null);
        documentManager.save();
        
        // Check that the endpoint has properties from both discoveries
        DocumentModel foundEndpoint = documentService.find(documentManager, discoveredEndpointId);
        
        Assert.assertEquals("http://www.services.com/endpoint", foundEndpoint.getPropertyValue(Endpoint.XPATH_URL));
        Assert.assertEquals("Blahblah", foundEndpoint.getPropertyValue("dc:description"));
    }

}
