package org.easysoa.registry.rest.integration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Simple registry service interface
 * 
 * Architecture : see https://github.com/easysoa/EasySOA/wiki/Easysoa-integration-architecture---development-platform
 * 
 * @author jguillemotte
 *
 */
@Path("easysoa/simpleRegistryService")
public interface SimpleRegistryService {

    /**
     * Returns the Information services registered in Nuxeo
     * 
     * {
     *   "wsdlInformations":[
     *     {
     *       "projectID":"",
     *       "nuxeoID":"61739c9d-6308-4618-a547-769f20c8f51b",
     *       "name":"TdrService",
     *       "description":null,
     *       "soaName":"\"http://www.pureairflowers.com/services/\":TdrService",
     *       "objectType":"InformationService",
     *       "environment":"",
     *       "endpointUrl":"",
     *       "wsdlDownloadUrl":""
     *     },{
     *       "projectID":"",
     *       "nuxeoID":"f20fa784-7ece-4b20-abe0-a28b3c73bb1e",
     *       "name":"PureAirFlowersService",
     *       "description":null,
     *       "soaName":"http://www.pureairflowers.com/services/:PureAirFlowersService",
     *       "objectType":"InformationService",
     *       "environment":"",
     *       "endpointUrl":"",
     *       "wsdlDownloadUrl":""
     *     }
     *   ]
     * }
     * 
     * @param search looked up in name, description, in interface extracted metas (portType)
     * OPT in interface fulltext
     * @param subProjectId if null, looks up in all subprojects
     * OPT in the configured entreprise-wide environment(s)
     * & version strategy (ex. "staging : latest published, integration : latest")
     * OPT may be provided from FraSCAtiStudio app (name ?)
     * @return WSDLInformation array
     * @throws Exception If a problem occurs
     */
    //@param platformServiceStandard (ex : SOAP[1.1]) (LATER)
    //@param wsBindingTransport (ex : http://schemas.xmlsoap.org/soap/http) (LATER)
    //@param platform (ex : fraSCAtiStudioPlatformId) (LATER)
    @GET
    @Path("/queryWSDLInterfaces")
    @Produces(MediaType.APPLICATION_JSON)
    public WSDLInformations queryWSDLInterfaces(
            @QueryParam("search") String search, 
            @QueryParam("subProjectId") String subProjectId/*,
            @QueryParam("platformServiceStandard") String platformServiceStandard,
            @QueryParam("wsBindingTransport") String wsBindingTransport,
            @QueryParam("platform") String platform*/ ) throws Exception;
    
    /**
     * Returns the Endpoints registered in Nuxeo
     * @param search looked up in name, description, in interface extracted metas (portType)
     * @param subProjectId if null, looks up in all subprojects
     * @return WSDLInformation array
     * @throws Exception If a problem occurs
     */
    @GET
    @Path("/queryEndpoints")
    @Produces(MediaType.APPLICATION_JSON)
    // TODO : change return type for EndpointInformation or common type
    public WSDLInformations queryEndpoints(@QueryParam("search") String search, 
            @QueryParam("subProjectId") String subProjectId/*,
            @QueryParam("platformServiceStandard") String platformServiceStandard,
            @QueryParam("wsBindingTransport") String wsBindingTransport,
            @QueryParam("platform") String platform,
            @QueryParam("environment") String environment*/ ) throws Exception;
    
    /**
     * TODO jguillemotte
     * 
     * Returns services of the given subProject that match the search, for each
     * along with the array of all its endpoints :
     * [environmentName, endpointUrl, (seen)wsdlDownloadUrl]
     * 
     * This is a convenience method to avoid to do a lot of queries. It is
     * especially useful to external platforms that want to synchronize all endpoints.
     * 
     * Its implementation does a first query on services as usual, then does a query
     * that finds Endpoints whose serviceId is among those (reuse & refactor in a util
     * SoftwareComponentIndicatorProvider.getIdLiteralList()) and fills the result
     * array.
     *  
     * @search if null, returns all of them
     */
    /*@GET
    @Path("/queryServicesWithEndpoints")
    @Produces(MediaType.APPLICATION_JSON)
    // TODO : change return type for EndpointInformation or common type
    public WSDLInformations queryServicesWithEndpoints(@QueryParam("search") String search, 
            @QueryParam("subProjectId") String subProjectId ) throws Exception;*/
   
}
