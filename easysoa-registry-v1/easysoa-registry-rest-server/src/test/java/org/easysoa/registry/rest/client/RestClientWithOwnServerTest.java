package org.easysoa.registry.rest.client;

import java.io.File;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;


/**
 * Full unit tests for the REST client library.
 * Can't be done in -rest module because uses the whole, actual EasySOA / Registry Nuxeo server.
 *
 * @author mdutoo
 *
 */
@Deploy("org.easysoa.registry.rest.server")
public class RestClientWithOwnServerTest extends AbstractWebEngineTest {

    @Inject
    DiscoveryService discoveryService; // only for initDefaultSubprojectId()

    @Inject
    DocumentService documentService;

    private ClientBuilder clientBuilder;

    private RegistryApi registryApi;

    public RestClientWithOwnServerTest() {
        this.clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(EasySOAWebEngineFeature.NUXEO_SITES_URL); // test URL
        this.registryApi = clientBuilder.constructRegistryApi();
    }


    @Before
    public void initDefaultSubprojectId() throws Exception {
        SoaNodeInformation subprojectInfo = registryApi.get(Subproject.DEFAULT_SUBPROJECT_ID);

        defaultSubprojectId = subprojectInfo.getSubprojectId();
    }

    @Test
    public void testClientCreation() throws Exception {
        Assert.assertNotNull("RegistryApi instanciation must be successful", registryApi);

        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService");
        OperationResult result = registryApi.post(new SoaNodeInformation(myServiceId, null, null));
        Assert.assertTrue("Creation must be successful", result.isSuccessful());

        // Fetch it
        SoaNodeInformation foundSoaNode = registryApi.get(myServiceId.getSubprojectId(),
                myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getSoaNodeId());

        // Create another document
        SoaNodeId myOtherServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyOtherService");
        result = registryApi.post(new SoaNodeInformation(myOtherServiceId, null, null));
        Assert.assertTrue("Creation must be successful", result.isSuccessful());

        // Get the all the documents
        SoaNodeInformations soaNodeInformations = registryApi.get(defaultSubprojectId, InformationService.DOCTYPE);
        Assert.assertNotNull(soaNodeInformations);
        Assert.assertEquals(2, soaNodeInformations.getSoaNodeInformationList().size());
        Assert.assertNotNull(soaNodeInformations.getSoaNodeInformationList().get(0));
        Assert.assertNotNull(soaNodeInformations.getSoaNodeInformationList().get(1));

    }

    @Test
    public void testClientGetDocumentWithBlob() throws Exception {

        Assert.assertNotNull("RegistryApi instanciation must be successful", registryApi);

        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService");
        OperationResult result = registryApi.post(new SoaNodeInformation(myServiceId, null, null));
        Assert.assertTrue("Creation must be successful", result.isSuccessful());

        // Fetch it
        SoaNodeInformation foundSoaNode = registryApi.get(myServiceId.getSubprojectId(),
                myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getSoaNodeId());

        // Set a blob file
        // server side solution :
        DocumentModel docModel =documentService.findSoaNode(documentManager, myServiceId, true);
        Assert.assertNotNull(docModel);
        // Update registry with new resource
        File testFile = new File("../easysoa-registry-rest-client/src/test/resources/InternationalPostalValidation_nourl.wsdl");
        Assert.assertNotNull(testFile);
        StringBlob blob = new StringBlob(FileUtils.readFile(testFile));
        blob.setFilename("InternationalPostalValidation_nourl.wsdl");
        docModel.setPropertyValue("file:content", blob);
        docModel = documentManager.saveDocument(docModel);
        Assert.assertNotNull(docModel);
        // client side solution :
        /*
        Map<String, Serializable> myServiceProperties = new HashMap<String, Serializable>();
        String filePath = "../easysoa-registry-rest-client/src/test/resources/InternationalPostalValidation_nourl.wsdl";
        URL wsdlUrl = new URL("file://" + new File(filePath).getAbsolutePath());
        myServiceProperties.put(ResourceDownloadInfo.XPATH_URL, wsdlUrl.toString());
        SoaNodeInformation myServiceInfo = new SoaNodeInformation(myServiceId, myServiceProperties, null);
        result = registryApi.post(myServiceInfo);
        Assert.assertTrue("Creation must be successful", result.isSuccessful());
        */

        // Get again the document
        foundSoaNode = registryApi.get(myServiceId.getSubprojectId(),
                myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getSoaNodeId());

        // Check that the blob file is not included in the response
        Assert.assertNull(foundSoaNode.getProperty("file:content"));

    }
}
