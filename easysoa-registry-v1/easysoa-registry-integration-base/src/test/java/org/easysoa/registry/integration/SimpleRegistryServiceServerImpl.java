/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.EndpointInformation;
import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.ServiceInformations;

/**
 * Simple registry service mock server implementation
 * 
 * @author jguillemotte
 *
 */
public class SimpleRegistryServiceServerImpl implements SimpleRegistryService {

    @Override
    public ServiceInformations queryWSDLInterfaces(String search, String subProjectId, String visibility) throws Exception {

        ServiceInformations serviceInformations = new ServiceInformations();
        ServiceInformation serviceInformation1 = new ServiceInformation();
        ServiceInformation serviceInformation2 = new ServiceInformation();
        ServiceInformation serviceInformation3 = new ServiceInformation();
        
        serviceInformation1.setSoaName("\"http://www.pureairflowers.com/services/\":TdrService");
        serviceInformation1.setProjectID("");
        serviceInformation1.setName("TdrService");  
        serviceInformation1.setDescription("");            
        serviceInformation1.setNuxeoID("61739c9d-6308-4618-a547-769f20c8f51b");
        serviceInformation1.setObjectType("InformationService");
        serviceInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        serviceInformations.addServiceInformation(serviceInformation1);
        
        serviceInformation2.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        serviceInformation2.setProjectID("");
        serviceInformation2.setName("PureAirFlowersService");  
        serviceInformation2.setDescription("Pure Air Flowers service description");            
        serviceInformation2.setNuxeoID("f20fa784-7ece-4b20-abe0-a28b3c73bb1e");
        serviceInformation2.setObjectType("InformationService");        
        serviceInformations.addServiceInformation(serviceInformation2);

        serviceInformation3.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        serviceInformation3.setProjectID("");
        serviceInformation3.setName("PureAirFlowersService");  
        serviceInformation3.setDescription("Pure Air Flowers service description");            
        serviceInformation3.setNuxeoID("cc7ac06a-08fe-484f-b1cf-542506b90beb");
        serviceInformation3.setObjectType("InformationService");        
        serviceInformations.addServiceInformation(serviceInformation3);        

        return serviceInformations;
    }

    @Override
    public EndpointInformations queryEndpoints(String search, String subProjectId, String visibility) throws Exception {
        EndpointInformations endpointInformations = new EndpointInformations();
        EndpointInformation endpointInformation1 = new EndpointInformation();

        endpointInformation1.setSoaName("Test:http://localhost:8785/Test");
        endpointInformation1.setProjectID("");
        endpointInformation1.setName("TestEndpoint");  
        endpointInformation1.setDescription("");            
        endpointInformation1.setNuxeoID("d634451a-be48-4162-9a91-ad69ac69d25a");
        endpointInformation1.setObjectType("Endpoint");
        endpointInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        endpointInformation1.setEnvironment("Test");
        endpointInformation1.setEndpointUrl("http://localhost:8785/Test");
        endpointInformations.addEndpointInformation(endpointInformation1);        

        return endpointInformations;
    }

    @Override
    public ServiceInformations queryServicesWithEndpoints(String search, String subProjectId, String visibility) throws Exception {

        ServiceInformations serviceInformations = new ServiceInformations();
        ServiceInformation serviceInformation1 = new ServiceInformation();
        ServiceInformation serviceInformation2 = new ServiceInformation();
        ServiceInformation serviceInformation3 = new ServiceInformation();
        
        serviceInformation1.setSoaName("\"http://www.pureairflowers.com/services/\":TdrService");
        serviceInformation1.setProjectID("");
        serviceInformation1.setName("TdrService");  
        serviceInformation1.setDescription("");            
        serviceInformation1.setNuxeoID("61739c9d-6308-4618-a547-769f20c8f51b");
        serviceInformation1.setObjectType("InformationService");
        serviceInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        serviceInformations.addServiceInformation(serviceInformation1);
        
        serviceInformation2.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        serviceInformation2.setProjectID("");
        serviceInformation2.setName("PureAirFlowersService");  
        serviceInformation2.setDescription("Pure Air Flowers service description");            
        serviceInformation2.setNuxeoID("f20fa784-7ece-4b20-abe0-a28b3c73bb1e");
        serviceInformation2.setObjectType("InformationService");        
        serviceInformations.addServiceInformation(serviceInformation2);

        serviceInformation3.setSoaName("http://www.pureairflowers.com/services/:PureAirFlowersService");
        serviceInformation3.setProjectID("");
        serviceInformation3.setName("PureAirFlowersService");  
        serviceInformation3.setDescription("Pure Air Flowers service description");            
        serviceInformation3.setNuxeoID("cc7ac06a-08fe-484f-b1cf-542506b90beb");
        serviceInformation3.setObjectType("InformationService");        
        serviceInformations.addServiceInformation(serviceInformation3);        

        EndpointInformations endpointInformations = new EndpointInformations();
        EndpointInformation endpointInformation1 = new EndpointInformation();

        endpointInformation1.setSoaName("Test:http://localhost:8785/Test");
        endpointInformation1.setProjectID("");
        endpointInformation1.setName("TestEndpoint");  
        endpointInformation1.setDescription("");            
        endpointInformation1.setNuxeoID("d634451a-be48-4162-9a91-ad69ac69d25a");
        endpointInformation1.setObjectType("Endpoint");
        endpointInformation1.setWsdlDownloadUrl("http://localhost:8080/nuxeo/nxfile/default/61739c9d-6308-4618-a547-769f20c8f51b/files:files/0/file/WeatherService.wsdl");
        endpointInformation1.setEnvironment("Test");
        endpointInformation1.setEndpointUrl("http://localhost:8785/Test");
        endpointInformations.addEndpointInformation(endpointInformation1);        
        
        // Associate endpoint with service information 3
        serviceInformation3.setEndpoints(endpointInformations);
        
        return serviceInformations;
    }

}
