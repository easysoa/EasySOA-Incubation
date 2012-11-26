/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.integration.WSDLInformations;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;
import org.testng.Assert;

/**
 * @author jguillemotte
 * 
 */
@Ignore // until Juliac is fixed
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
        WSDLInformations result = testClient.testQueryWSDLInterfaces(null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(3,result.getWsdlInformationList().size());
    }

    @Test
    public void restQueryEndpointsService() throws Exception {
        TestClientItf testClient =  frascati.getService(componentList.get(0), "simpleRegistryServiceTestClient", TestClientItf.class);
        WSDLInformations result = testClient.testQueryEndpoints(null, null);
        Assert.assertNotNull(result);
    }    
    
    @AfterClass
    public static void endTest() throws FrascatiException {
        for (Component component : componentList) {
            frascati.close(component);
        }
        logger.info("FraSCATI closing");
    }
}
