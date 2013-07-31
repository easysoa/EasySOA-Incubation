/**
 * 
 */
package org.easysoa.registry.integration;

import junit.framework.Assert;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * CXF Client (Spring) for Simple registry Service
 * 
 * @author jguillemotte
 *
 */
public class RegistryApiCXFClientTest {

    private static FileSystemXmlApplicationContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        // Load the CXF Config file
        System.out.println("Launching RegistryApiCXFClientTest");
        context = new FileSystemXmlApplicationContext("src/test/resources/cxf-test.xml");
    }
    
    /**
     * Tests the CXF client with the RegistryApi service
     * @throws Exception If a problem occurs
     */
    @Test
    public void cxfClientTest() throws Exception {
        RegistryApi client = (RegistryApi) context.getBean("registryApiCXFTestClient");
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
    
}
