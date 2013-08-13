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

package org.easysoa.registry.cxf.client;

import com.google.inject.Inject;
import org.apache.cxf.jaxrs.client.WebClient;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.runtime.test.runner.Deploy;

/**
 *
 * @author jguillemotte
 */
@Deploy("org.easysoa.registry.rest.server")
public class CxfClientTest extends AbstractWebEngineTest {

    @Inject
    DiscoveryService discoveryService; // only for initDefaultSubprojectId()

    @Inject
    DocumentService documentService;

    private String username = "Administrator";
    private String password = "Administrator";

    @Test
    public void testClientCreation() throws Exception {

        // Create client with authentification
        WebClient client = WebClient.create(EasySOAWebEngineFeature.NUXEO_SITES_URL, username, password, null);

        Assert.assertNotNull("Client instanciation must be successful", client);

        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService");

        client.path("easysoa/registry");
        //client.type("text/xml").accept("text/xml");
        OperationResult result = client.post(new SoaNodeInformation(myServiceId, null, null), OperationResult.class);
        Assert.assertNotNull(result);
        Assert.assertTrue("Creation must be successful", result.isSuccessful());

        // Fetch it
        //get(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;
        /*
        client = client.path("/" + myServiceId.getType() + "/" + myServiceId.getName());
        client = client.replaceQueryParam("subproject", myServiceId.getSubprojectId());
        SoaNodeInformation foundSoaNode = client.get(SoaNodeInformation.class);
        //SoaNodeInformation foundSoaNode = registryApi.get(myServiceId.getSubprojectId(), myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getSoaNodeId());
        */

        // Create another document
        SoaNodeId myOtherServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyOtherService");
        result = client.post(new SoaNodeInformation(myOtherServiceId, null, null), OperationResult.class);
        Assert.assertNotNull(result);
        Assert.assertTrue("Creation must be successful", result.isSuccessful());

        // Get the all the documents
        client = client.path("/" + InformationService.DOCTYPE);
        client = client.replaceQueryParam("subproject", defaultSubprojectId);
        SoaNodeInformations soaNodeInformations = client.get(SoaNodeInformations.class);
        Assert.assertNotNull(soaNodeInformations);
        Assert.assertEquals(2, soaNodeInformations.getSoaNodeInformationList().size());
        Assert.assertNotNull(soaNodeInformations.getSoaNodeInformationList().get(0));
        Assert.assertNotNull(soaNodeInformations.getSoaNodeInformationList().get(1));

    }

}
