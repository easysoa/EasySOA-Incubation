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

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;

/**
 *
 * @author jguillemotte
 */
@Deploy("org.easysoa.registry.mock.webEngineResourceUpdateTest")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class ResourceUpdateTest extends AbstractWebEngineTest {

	public static final String MOCK_SERVER_ENDPOINT_WSDL_URL = "http://localhost:"
			+ EasySOAWebEngineFeature.PORT + "/mock/wsdl/PureAirFlowers";
	public static final String MOCK_SERVER_ENDPOINT_URL = MOCK_SERVER_ENDPOINT_WSDL_URL + "?wsdl";

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ResourceUpdateTest.class);

    @Inject
    DiscoveryService discoveryService;

    @Inject
    DocumentService documentService;

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
        GetMethod get = new GetMethod(MOCK_SERVER_ENDPOINT_WSDL_URL);
        int statusCode = httpClient.executeMethod(get);
        Assert.assertEquals(200, statusCode);
        String response = get.getResponseBodyAsString();
        Assert.assertTrue(response.contains("PureAirFlowersServiceService"));
    }


    // Resource update test
    @Test
    public void resourceUpdateTest() throws Exception {

        SoaNodeId discoveredEndpointId = new EndpointId("Production", MOCK_SERVER_ENDPOINT_URL);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_TITLE, "My Endpoint");
        properties.put(ResourceDownloadInfo.XPATH_URL, MOCK_SERVER_ENDPOINT_WSDL_URL);

        // Trigger a discovery on a wsdl mock file
        DocumentModel doc = discoveryService.runDiscovery(documentManager, discoveredEndpointId, properties, null);
        Assert.assertNotNull(doc);
        documentManager.save();

        // check if discovered document contains a wsdl
        DocumentModel foundEndpoint = documentService.findSoaNode(documentManager, discoveredEndpointId);
        Assert.assertNotNull(foundEndpoint);

        Assert.assertEquals(MOCK_SERVER_ENDPOINT_WSDL_URL, foundEndpoint.getPropertyValue(ResourceDownloadInfo.XPATH_URL));
        Assert.assertNotNull(foundEndpoint.getProperty("file", "content"));
        Assert.assertNotNull(foundEndpoint.getPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP));

        Blob blob = (Blob) foundEndpoint.getPropertyValue("file:content");

        Assert.assertEquals("PureAirFlowers.wsdl", blob.getFilename());

        //Assert.assertEquals("UTF-8", blob.getEncoding()); // TODO : encoding not set
        //Assert.assertEquals("text/xml", blob.getMimeType()); // TODO : [application/octet-stream] instead of "text/xml" ?

        String blobContent = new String(blob.getByteArray());
        Assert.assertNotNull(blobContent);
        Assert.assertTrue(blobContent.contains("PureAirFlowersServiceService"));

    }

}
