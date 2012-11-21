package org.easysoa.registry.integration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.easysoa.registry.rest.marshalling.WSDLInformation;

/**
 * 
 * @author jguillemotte
 *
 */
@Path("easysoa/simpleRegistryService")
public interface SimpleRegistryService {

    /**
     * Returns the Information services registered in Nuxeo
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
    public WSDLInformation[] queryWSDLInterfaces(
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
    public WSDLInformation[] queryEndpoints(@QueryParam("search") String search, 
            @QueryParam("subProjectId") String subProjectId ) throws Exception;
    
    //OPT public SoaNodeInformation[] queryJAXRSInterfaces(String search, String subProjectId);
    
    //private List<Document> queryInterfaces(String type, String name, String desc, String metas, String fulltext, String subProjectId); TODO ??
    
    /*
     SimpleServiceRegistryService.query(WSDL)Interfaces(...) : select * from InformationService where (name like '%search%' or desc like '%search%') [and projectId=appGivenProjectId || projectId in (allProjectIdsReferredByFraSCAtiStudioProject)] and platform_service_standard='SOAP[1.1]' [and ws_binding_transport(_type)='http://schemas.xmlsoap.org/soap/http'] [and platform='fraSCAtiStudioPlatformId']

    NO ON ITS OWN UNION SOAP Endpoints WSDLs ?? not if its IS has a WSDL
    LATER OPT UNION (re)source WSDLs ??
    OPT platformId allows notably a development platform (FStudio, Talend...) to look only in its own services i.e. "EasySOA as WSDL indexer"
    
    returns : (sub)projectId, (nuxeo id,) name(title), description, soaname, objectType=InformationService(Endpoint), wsdlDownloadUrl=attachedFileUrl
    OPT EasySOA id stored as component (service ??) name in composite (so it is available at SCA discovery time), OPT EasySOA id is a "pretty" versioned id
    
    to download WSDL : in a first time use hardcoded BasicAuth (Administrator:Administrator) in FraSCAti client (or Apache HTTPClient)
    
    TODO :
    1. define REST interface & implement it returning empty
    2. write unit test like RegistryApiTest
    3. complete impl simply, to see returned values
    4. design returned model
    (5. improve impl)
    6. do it for querySOAPEndpoints
    7. extract REST interface in easysoa-registry-integration-api with FraSCAti only in test dependencies and unit test them
    */
    
    /*
    SimpleServiceRegistryService.query(SOAP)Endpoints(...) : select * from Endpoint where ...

    returns : (sub)projectId, (nuxeo id,) name(title), description, soaname, objectType=Endpoint, endpointUrl [, wsdlUrl=deduced, wsdlDownloadUrl=attachedFileUrl]
    endpointUrl is enough to identify it in EasySOA. OPT store EasySOA id anyway in binding name in composite.


     */
    
    
    /*
     private (Light)RegistryService.getAllProjectIdsPublishedIn(Light)Environment(ex. "staging") 
     */
}
