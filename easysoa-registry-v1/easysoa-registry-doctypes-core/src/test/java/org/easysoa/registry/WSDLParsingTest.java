package org.easysoa.registry;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class WSDLParsingTest {

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
		} catch (Exception e) {
			Assert.fail("Failed to parse WSDL");
		}
	}

}
