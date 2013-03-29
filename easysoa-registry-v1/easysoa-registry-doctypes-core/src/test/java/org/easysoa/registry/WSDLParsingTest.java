package org.easysoa.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.common.utils.Base64;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StreamingBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.streaming.ByteArraySource;
import org.nuxeo.runtime.services.streaming.FileSource;
import org.nuxeo.runtime.services.streaming.StringSource;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 * 
 */
@RepositoryConfig(cleanup = Granularity.CLASS)
public class WSDLParsingTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(WSDLParsingTest.class);

    @Inject
    DocumentService documentService;

	@Test
	public void parseWSDL() {
		try {
			// Parse WSDL
			WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
			File wsdlFile = new File("src/test/resources/PureAirFlowers.wsdl");
			Assert.assertTrue("WSDL not found", wsdlFile.exists());
			Description wsdl = wsdlReader.read(wsdlFile.toURI().toURL());

			// Read some data
			Assert.assertEquals("Unexpected binding name",
					"PureAirFlowersServiceITFNAME",
					wsdl.getInterfaces().get(0).getQName().getLocalPart());
			Assert.assertEquals("Unexpected WSDL version",
					"WSDL11",
					wsdl.getVersion().name());
		} catch (Exception e) {
			Assert.fail("Failed to parse WSDL");
		}
	}
	
	// TODO Actually test the parsing listener
	@Test
	public void testListener() throws ClientException, IOException {
        DocumentModel serviceModel = documentService.newSoaNodeDocument(documentManager, 
                new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService"));
        documentManager.createDocument(serviceModel);
        documentManager.save();

        // uploading first wsdl
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();
        
        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        String digest = existingBlob.getDigest();
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertTrue("Unexpected empty digest", digest != null && digest.length() != 0);
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));

        // uploading second, identical wsdl
        blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected changed digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));

        // uploading second, identical wsdl but with different name
        blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService1.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected changed digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService1.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));

        // uploading third, different wsdl
        blob = new StreamingBlob(new FileSource(new File("src/test/resources/PureAirFlowers.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PureAirFlowers.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Flowers", existingBlob.getString().contains("Flowers"));
        Assert.assertEquals("Unexpected port type", "{http://www.pureairflowers.com/servicesTGNS/}PureAirFlowersServiceITFNAME", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PureAirFlowers.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();

        // uploading fourth, different wsdl
        blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();

        // putting bad wsdl
        String badContent = "not a wsdl";
        /*File badWsdlFile = File.createTempFile("WSDLParsingTest", ".wsdl");
        badWsdlFile.deleteOnExit();
        FileWriter badWsdlWriter = new FileWriter(badWsdlFile);
        badWsdlWriter.write(badContent);
        badWsdlWriter.close();
        blob = new StreamingBlob(new FileSource(badWsdlFile));*/
        blob = new StreamingBlob(new StringSource(badContent));
        //blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService1.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireServiceBad.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should be " + badContent, existingBlob.getString().equals(badContent));
        Assert.assertEquals("Unexpected not empty port type", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected not empty filename", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();

        // removing wsdl
        serviceModel.setPropertyValue("file:content", null);
        serviceModel = documentManager.saveDocument(serviceModel);
        // NB. returns same as documentManager.getDocument(serviceModel.getRef()) ; else no extracted metadata
        documentManager.save();

        // getting computed digest from SQLBlob
        Assert.assertNull("Unexpected not null content", documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content"));
        Assert.assertEquals("Unexpected not empty port type", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected not empty filename", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();
	}

}
