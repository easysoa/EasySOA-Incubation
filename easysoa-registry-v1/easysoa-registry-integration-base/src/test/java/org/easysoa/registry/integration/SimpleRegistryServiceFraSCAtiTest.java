/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.log4j.Logger;
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
        logger.info("FraSCATI Starting");
        componentList = new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
        componentList.add(frascati.processComposite("simpleRegistryServiceClient", new ProcessingContextImpl()));        
    }

    @Test
    public void restQueryWSDLInformationsService() throws Exception {
        TestClientItf testClient = frascati.getService(componentList.get(0), "simpleRegistryServiceTestClient", TestClientItf.class);
        ServiceInformations result = testClient.testQueryWSDLInterfaces(null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(3,result.getServiceInformationList().size());
    }

    @Test
    public void restQueryEndpointsService() throws Exception {
        TestClientItf testClient =  frascati.getService(componentList.get(0), "simpleRegistryServiceTestClient", TestClientItf.class);
        EndpointInformations result = testClient.testQueryEndpoints(null, null);
        Assert.assertNotNull(result);
    }    
    
    @Test
    public void restQueryServicesWithEndpointsService() throws Exception {
        TestClientItf testClient =  frascati.getService(componentList.get(0), "simpleRegistryServiceTestClient", TestClientItf.class);
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
    
    @AfterClass
    public static void endTest() throws FrascatiException {
        for (Component component : componentList) {
            frascati.close(component);
        }
        logger.info("FraSCATI closing");
    }
}
