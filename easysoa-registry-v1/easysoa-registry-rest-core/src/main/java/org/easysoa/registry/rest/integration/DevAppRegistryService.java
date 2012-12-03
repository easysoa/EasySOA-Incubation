package org.easysoa.registry.rest.integration;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Development-time application registry service interface.
 * Meant to be called by development platforms, such as
 * FraSCAti Studio and Talend Studio.
 * 
 * Architecture : see https://github.com/easysoa/EasySOA/wiki/Easysoa-integration-architecture---development-platform
 * 
 * TODO jguillemotte
 * 
 * @author mdutoo, jguillemotte
 *
 */
@Path("easysoa/devAppRegistryService")
public interface DevAppRegistryService {

    /**
     * Registers given development-time application.
     * 
     * NB. to register services, use discovery while refering to a registered dev app
     * 
     * Its implementation will create a
     * TaggingFolder / top-level Deliverable / Application System / Component
     * in the given subproject with the given properties, that service discovery
     * will be able to refer to.
     * 
     * @param name
     * @param description also documentation : how it's done & meant to be used
     * @param subProjectId where it is registered ; required
     * @return created application(Id)
     * @throws Exception If a problem occurs
     */
    //@param platformServiceStandard (ex : SOAP[1.1]) (LATER)
    //@param wsBindingTransport (ex : http://schemas.xmlsoap.org/soap/http) (LATER)
    //@param platform (ex : fraSCAtiStudioPlatformId) (LATER)
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
	public Object register(
			String subProjectId,
			String name,
			String description,
			String platformId/*TODO or criteria*/) throws Exception;

    /**
     * Returns registered development applications that match
     * 
     * @param search looked up in name, description, TODO
     * @param subProjectId if null, looks up in all subprojects
     * OPT in the configured entreprise-wide environment(s)
     * & version strategy (ex. "staging : latest published, integration : latest")
     * @return found applications
     * @throws Exception If a problem occurs
     */
    //@param platformServiceStandard (ex : SOAP[1.1]) (LATER)
    //@param wsBindingTransport (ex : http://schemas.xmlsoap.org/soap/http) (LATER)
    //@param platform (ex : fraSCAtiStudioPlatformId) (LATER)
    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Object query(
            @QueryParam("search") String search, 
            @QueryParam("subProjectId") String subProjectId/*,
            @QueryParam("platformServiceStandard") String platformServiceStandard,
            @QueryParam("wsBindingTransport") String wsBindingTransport,
            @QueryParam("platform") String platform*/ ) throws Exception;
    
}
