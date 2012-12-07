/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.ServiceInformations;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.Assert;

/**
 * CXF Client (Spring) for Simple registry Service
 * 
 * @author jguillemotte
 *
 */
public class SimpleRegistryServiceCXFClientTest {

    private static FileSystemXmlApplicationContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        // Load the CXF Config file
        System.out.println("Launching SimpleRegistryServiceCXFClientTest");
        context = new FileSystemXmlApplicationContext("src/test/resources/cxf.xml");
    }
    
    /**
     * Test the CXF client with the Simple Registry Service
     * @throws Exception If a problem occurs
     */
    @Test
    public void cxfClientTest() throws Exception {
        SimpleRegistryService client = (SimpleRegistryService) context.getBean("simpleRegistryServiceCXFTestClient");
        ServiceInformations result = client.queryWSDLInterfaces(null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getServiceInformationList().size());
        ServiceInformation information = result.getServiceInformationList().get(0);
        Assert.assertEquals("TdrService",information.getName());
        Assert.assertEquals("InformationService", information.getObjectType());
    }
    
}
