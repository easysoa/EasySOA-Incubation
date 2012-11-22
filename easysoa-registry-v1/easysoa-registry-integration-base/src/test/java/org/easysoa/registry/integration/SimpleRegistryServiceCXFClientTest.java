/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.WSDLInformations;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.Assert;

/**
 * @author jguillemotte
 *
 */
public class SimpleRegistryServiceCXFClientTest {

    private static FileSystemXmlApplicationContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        //System.setProperty("cxf.config.file", "src/test/resources/cxf.xml");
        System.out.println("Launching SimpleRegistryServiceCXFClientTest");
        context = new FileSystemXmlApplicationContext("src/test/resources/cxf.xml");
    }
    
    @Test
    @Ignore
    public void cxfClientTest() throws Exception{
        SimpleRegistryService client = (SimpleRegistryService) context.getBean("simpleRegistryServiceCXFTestClient");
        WSDLInformations result = client.queryWSDLInterfaces(null, null);
        Assert.assertNotNull(result);        
    }
    
}
