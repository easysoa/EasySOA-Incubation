/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry;

import com.google.inject.Inject;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.easysoa.registry.dbb.ResourceUpdateService;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.ecm.automation.test.RestFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.LocalDeploy;

/**
 *
 * @author jguillemotte
 */
@RunWith(FeaturesRunner.class)
@Features(WebEngineFeature.class)
@Jetty(port = 18080)
@Deploy("org.easysoa.registry.mock.webEngineHelloTest")
//@Deploy("org.easysoa.registry.doctypes.core")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class ResourceUpdateTest /*extends AbstractWebEngineTest*/ /*extends AbstractRegistryTest*/ {
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ResourceUpdateTest.class);

    //@Inject
    //DiscoveryService discoveryService;
    
    // TODO :
    // Add a mock to serve a sample WSDL file to download => done
    // Check that the doc has a blob with correct name and mimetype set
 
    /*@Test
    public void testHello() throws HttpException, IOException {
        HttpClient httpClient = new HttpClient();
        //GetMethod get = new GetMethod("http://localhost:" + EasySOAWebEngineFeature.PORT + "/hello");
        GetMethod get = new GetMethod("http://localhost:18080/hello");
        int statusCode = httpClient.executeMethod(get);
        Assert.assertEquals(200, statusCode);
        String response = get.getResponseBodyAsString();
        Assert.assertEquals(response, "Hello WebEngine");
    }*/
    
    @Test
    public void testMock() throws HttpException, IOException {
        HttpClient httpClient = new HttpClient();
        GetMethod get = new GetMethod("http://localhost:18080/mock/wsdl/PureAirFlowers?wsdl");
        int statusCode = httpClient.executeMethod(get);
        Assert.assertEquals(200, statusCode);
        String response = get.getResponseBodyAsString();
        Assert.assertTrue(response.contains("PureAirFlowersServiceService"));
    }
    
    
    // Resource update test
    @Test
    public void resourceUpdateTest() throws Exception {
        
        // Trigger a discovery on a wsdl mock file
        //discoveryService.runDiscovery(documentManager, null, null, null);
        //documentManager.save();
        
        // Get the service
        //ResourceUpdateService resourceUpdateService = Framework.getService(ResourceUpdateService.class);
        // Test update
        //resourceUpdateService.updateResource(null, null, null, null);
        
    }

}
