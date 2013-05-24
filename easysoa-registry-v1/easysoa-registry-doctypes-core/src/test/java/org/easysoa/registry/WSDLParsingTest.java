package org.easysoa.registry;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.blob.StreamingBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
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


	private static DocumentRef serviceRef;
	private static SoaNodeId serviceSoaId;
	private static String digest;

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
	
	/**
	 * Actually testing the parsing listener, goes on in following tests
	 * @throws ClientException
	 * @throws IOException
	 */
	@Test
	public void testListenerCreate() throws ClientException, IOException {
		serviceSoaId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService");
        DocumentModel serviceModel = documentService.newSoaNodeDocument(documentManager, serviceSoaId);
        documentManager.createDocument(serviceModel);
        documentManager.save();
        serviceRef = serviceModel.getRef(); // storing ref to re-get clean (no contextData) document after
	}
	
	@Test
	public void testListenerUploadFirstWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // uploading first wsdl
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();
        
        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        digest = existingBlob.getDigest();
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertTrue("Unexpected empty digest", digest != null && digest.length() != 0);
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
	}
	
	@Test
	public void testListenerUploadSecondIdenticalWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // uploading second, identical wsdl
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();

        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected changed digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
	}
	
	@Test
	public void testListenerUploadSecondIdenticalButDifferentNameWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // uploading second, identical wsdl but with different name
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService1.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();

        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected changed digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService1.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
	}
	
	@Test
	public void testListenerUploadThirdDifferentWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);


        // uploading third, different wsdl
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PureAirFlowers.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PureAirFlowers.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();

        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Flowers", existingBlob.getString().contains("Flowers"));
        Assert.assertEquals("Unexpected port type", "{http://www.pureairflowers.com/servicesTGNS/}PureAirFlowersServiceITFNAME", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PureAirFlowers.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();
	}
	
	@Test
	public void testListenerUploadFourthDifferentWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // uploading fourth, different wsdl
        StreamingBlob blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireService.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();

        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should contain " + "Precompte", existingBlob.getString().contains("Precompte"));
        Assert.assertEquals("Unexpected port type", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected filename", "PrecomptePartenaireService.wsdl", serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();
	}
	
	@Test
	public void testListenerUploadBadWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // putting bad wsdl
        String badContent = "not a wsdl";
        /*File badWsdlFile = File.createTempFile("WSDLParsingTest", ".wsdl");
        badWsdlFile.deleteOnExit();
        FileWriter badWsdlWriter = new FileWriter(badWsdlFile);
        badWsdlWriter.write(badContent);
        badWsdlWriter.close();
        blob = new StreamingBlob(new FileSource(badWsdlFile));*/
        StreamingBlob blob = new StreamingBlob(new StringSource(badContent));
        //blob = new StreamingBlob(new FileSource(new File("src/test/resources/PrecomptePartenaireService1.wsdl")));
        blob.setMimeType("text/xml");
        blob.setEncoding("UTF-8");
        blob.setFilename("PrecomptePartenaireServiceBad.wsdl");// TODO or auto ?
        serviceModel.setPropertyValue("file:content", blob);
        
        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();
        
        // getting computed digest from SQLBlob
        Blob existingBlob = (Blob) documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content");
        Assert.assertTrue("Content should be " + badContent, existingBlob.getString().equals(badContent));
        Assert.assertEquals("Unexpected not empty port type", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertNotSame("Unexpected identical digest", digest, existingBlob.getDigest());
        Assert.assertEquals("Unexpected not empty filename", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
        digest = existingBlob.getDigest();
	}
	
	@Test
	public void testListenerRemoveWsdl() throws ClientException, IOException {
        // getting clean document (with extracted metadata and no contextData guards) :
        // NB. reusing previous serviceModel = documentManager.saveDocument() is not enough because it
        // would have extracted metadata, but also contextData guards explicitly recopied
        DocumentModel serviceModel = documentManager.getDocument(serviceRef);

        // removing wsdl
        serviceModel.setPropertyValue("file:content", null);

        serviceModel = documentManager.saveDocument(serviceModel); // else if not re-assigned, no extracted metadatas for asserts below
        documentManager.save();

        // getting computed digest from SQLBlob
        Assert.assertNull("Unexpected not null content", documentManager.getDocument(serviceModel.getRef()).getPropertyValue("file:content"));
        Assert.assertEquals("Unexpected not empty port type", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
        Assert.assertEquals("Unexpected not empty filename", null, serviceModel.getPropertyValue(InformationService.XPATH_WSDL_FILE_NAME));
	}

}
