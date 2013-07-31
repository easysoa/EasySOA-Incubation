/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.easysoa.registry.rest.integration.EndpointInformation;
import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.rest.integration.ServiceInformations;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 * 
 */
//@Ignore // until Juliac is fixed
public class SimpleRegistryServiceFraSCAtiTest {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(SimpleRegistryServiceFraSCAtiTest.class);
    
    /** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;

    @BeforeClass
    public static void setUp() throws FrascatiException {
        logger.info("FraSCAti Starting");
        componentList = new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
        componentList.add(frascati.processComposite("simpleRegistryServiceClient", new ProcessingContextImpl()));        
    }

    @Test
    public void restQueryWSDLInformationsService() throws Exception {
        TestClientItf testClient = frascati.getService(componentList.get(0), "simpleRegistryServiceTestClientService", TestClientItf.class);
        ServiceInformations result = testClient.testQueryWSDLInterfaces(null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(3,result.getServiceInformationList().size());
    }

    @Test
    public void restQueryEndpointsService() throws Exception {
        TestClientItf testClient =  frascati.getService(componentList.get(0), "simpleRegistryServiceTestClientService", TestClientItf.class);
        EndpointInformations result = testClient.testQueryEndpoints(null, null);
        Assert.assertNotNull(result);
    }    
    
    @Test
    public void restQueryServicesWithEndpointsService() throws Exception {
        TestClientItf testClient =  frascati.getService(componentList.get(0), "simpleRegistryServiceTestClientService", TestClientItf.class);
        ServiceInformations result = testClient.testQueryServicesWithEndpoints(null, null);
        Assert.assertNotNull(result);
        ServiceInformation service = result.getServiceInformationList().get(2);
        Assert.assertNotNull(service);
        Assert.assertEquals("PureAirFlowersService", service.getName());
        EndpointInformations endpoints = service.getEndpoints();
        Assert.assertNotNull(endpoints);
        EndpointInformation endpoint = endpoints.getEndpointInformationList().get(0);
        Assert.assertEquals("TestEndpoint", endpoint.getName());
    }
    
    /**
     * Tests the FraSCAti client with the RegistryApi service
     * @throws Exception If a problem occurs
     */
    @Test
    public void testRegistryApi() throws Exception {
        RegistryApi client =  frascati.getService(componentList.get(0), "registryApiTestClientService", RegistryApi.class);
        SoaNodeInformations soaNodeInfos = client.get("MyProject/Realisation_v", "Endpoint");
        Assert.assertNotNull(soaNodeInfos);
        Assert.assertEquals(1, soaNodeInfos.getSoaNodeInformationList().size());
        SoaNodeInformation soaNodeInfo = soaNodeInfos.getSoaNodeInformationList().get(0);
        SoaNodeInformation referenceSoaNodeInfo = RegistryApiServerImpl.lastSoaNodeInformation;

        Assert.assertEquals(referenceSoaNodeInfo.getProperties().size(), soaNodeInfo.getProperties().size());
        //Assert.assertEquals("test:http://www.easysoa.org/myService", soaNodeInfo.getTitle()); // NO -
        Assert.assertEquals(1, soaNodeInfo.getProperty("testintnative"));
        Assert.assertEquals(referenceSoaNodeInfo.getProperty("testfloat"), soaNodeInfo.getProperty("testfloat"));
        Assert.assertEquals(referenceSoaNodeInfo.getProperty("testdate"), soaNodeInfo.getProperty("testdate"));
        Assert.assertNotNull(soaNodeInfo.getProperties());
    }
    
    @AfterClass
    public static void endTest() throws FrascatiException {
        for (Component component : componentList) {
            frascati.close(component);
        }
        logger.info("FraSCAti closing");
    }
}
