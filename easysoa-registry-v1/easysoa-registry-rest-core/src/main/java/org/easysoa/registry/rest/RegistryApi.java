package org.easysoa.registry.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformations;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RegistryApi {

    /** discovers the given SOA node */
    @POST
    OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception;

    /** @return all non-proxy, not deleted SOA nodes in the given subproject returned by the given guery */
    @POST
    @Path("query")
    @Consumes(MediaType.TEXT_PLAIN)
    SoaNodeInformations query(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId,
            String query) throws Exception;

    /** Returns the subproject root. FIXME WorkspaceRoot is not a SoaNode */
    @GET
    SoaNodeInformation get(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId) throws Exception;

    /** @return all SOA nodes of the given type (TODO or facet) in the given subproject */
    @GET
    @Path("{doctype}")
    SoaNodeInformations get(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype) throws Exception;

    /** @return the given SOA node */
    @GET
    @Path("{doctype}/{name:.+}")
    SoaNodeInformation get(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    /** Deletes the given SOA node */
    @DELETE
    @Path("{doctype}/{name:.+}")
    OperationResult delete(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    /** deletes the proxy of the given SOA node that is below the given correlated SOA node */
    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    OperationResult delete(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name,
            @QueryParam("correlatedSubprojectId") String correlatedSubprojectId,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws Exception;


}