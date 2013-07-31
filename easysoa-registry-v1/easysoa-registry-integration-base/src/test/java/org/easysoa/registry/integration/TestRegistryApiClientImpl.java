package org.easysoa.registry.integration;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.RegistryJsonApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.osoa.sca.annotations.Reference;

public class TestRegistryApiClientImpl implements RegistryApi {
    
    /**
     * Tells the client JAXRS stack to use JSON and ask the server to do the same
     */
    @Reference
    private RegistryJsonApi registryApi;

    public RegistryJsonApi getRegistryApi() {
        return registryApi;
    }

    public void setRegistryApi(RegistryJsonApi registryApi) {
        this.registryApi = registryApi;
    }

    @Override
    @POST
    public OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception {
        return registryApi.post(soaNodeInfo);
    }

    @Override
    @POST
    @Path("query")
    @Consumes("text/plain")
    public SoaNodeInformations query(@QueryParam("subproject") String subprojectId,
            String query) throws Exception {
        return registryApi.query(subprojectId, query);
    }

    @Override
    @GET
    public SoaNodeInformation get(@QueryParam("subproject") String subprojectId)
            throws Exception {
        return registryApi.get(subprojectId);
    }

    @Override
    @GET
    @Path("{doctype}")
    public SoaNodeInformations get(@QueryParam("subproject") String subprojectId,
            @PathParam("doctype") String doctype) throws Exception {
        return registryApi.get(subprojectId, doctype);
    }

    @Override
    @GET
    @Path("{doctype}/{name:.+}")
    public SoaNodeInformation get(@QueryParam("subproject") String subprojectId,
            @PathParam("doctype") String doctype, @PathParam("name") String name)
            throws Exception {
        return registryApi.get(subprojectId, doctype, name);
    }

    @Override
    @DELETE
    @Path("{doctype}/{name:.+}")
    public OperationResult delete(@QueryParam("subproject") String subprojectId,
            @PathParam("doctype") String doctype, @PathParam("name") String name)
            throws Exception {
        return registryApi.delete(subprojectId, doctype, name);
    }

    @Override
    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    public OperationResult delete(
            @QueryParam("subproject") String subprojectId,
            @PathParam("doctype") String doctype,
            @PathParam("name") String name,
            @QueryParam("correlatedSubprojectId") String correlatedSubprojectId,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName)
            throws Exception {
        return registryApi.delete(subprojectId, doctype, name, correlatedSubprojectId,
                correlatedDoctype, correlatedName);
    }

}
