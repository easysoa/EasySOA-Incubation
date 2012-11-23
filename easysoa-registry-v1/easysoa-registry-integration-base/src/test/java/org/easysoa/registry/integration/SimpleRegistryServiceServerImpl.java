/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.WSDLInformation;
import org.easysoa.registry.rest.integration.WSDLInformations;

/**
 * Simple registry service mock server implementation
 * 
 * @author jguillemotte
 *
 */
public class SimpleRegistryServiceServerImpl implements SimpleRegistryService {

    @Override
    public WSDLInformations queryWSDLInterfaces(String search, String subProjectId) throws Exception {

        WSDLInformations wsdlInformations = new WSDLInformations();
        WSDLInformation wsdlInformation1 = new WSDLInformation();
        WSDLInformation wsdlInformation2 = new WSDLInformation();
        WSDLInformation wsdlInformation3 = new WSDLInformation();
        
        wsdlInformation1.setSoaName("\"http://www.pureairflowers.com/services/\":TdrService");
        wsdlInformation1.setProjectID("");
        wsdlInformation1.setName("TdrService");  
        wsdlInformation1.setDescription("");            
        wsdlInformation1.setNuxeoID("61739c9d-6308-4618-a547-769f20c8f51b");
        wsdlInformation1.setObjectType("InformationService");
        wsdlInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        wsdlInformations.addWsdlInformation(wsdlInformation1);
        
        wsdlInformation2.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        wsdlInformation2.setProjectID("");
        wsdlInformation2.setName("PureAirFlowersService");  
        wsdlInformation2.setDescription("Pure Air Flowers service description");            
        wsdlInformation2.setNuxeoID("f20fa784-7ece-4b20-abe0-a28b3c73bb1e");
        wsdlInformation2.setObjectType("InformationService");        
        wsdlInformations.addWsdlInformation(wsdlInformation2);

        wsdlInformation3.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        wsdlInformation3.setProjectID("");
        wsdlInformation3.setName("PureAirFlowersService");  
        wsdlInformation3.setDescription("Pure Air Flowers service description");            
        wsdlInformation3.setNuxeoID("cc7ac06a-08fe-484f-b1cf-542506b90beb");
        wsdlInformation3.setObjectType("InformationService");        
        wsdlInformations.addWsdlInformation(wsdlInformation3);        

        return wsdlInformations;
    }

    @Override
    public WSDLInformations queryEndpoints(String search, String subProjectId) throws Exception {
        WSDLInformations wsdlInformations = new WSDLInformations();
        WSDLInformation wsdlInformation1 = new WSDLInformation();

        wsdlInformation1.setSoaName("TestEndpoint");
        wsdlInformation1.setProjectID("");
        wsdlInformation1.setName("TestEndpoint");  
        wsdlInformation1.setDescription("");            
        wsdlInformation1.setNuxeoID("d634451a-be48-4162-9a91-ad69ac69d25a");
        wsdlInformation1.setObjectType("Endpoint");
        wsdlInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        wsdlInformation1.setEnvironment("Test");
        wsdlInformation1.setEndpointUrl("http://localhost:8785/Test");
        wsdlInformations.addWsdlInformation(wsdlInformation1);        

        return wsdlInformations;
    }

}
