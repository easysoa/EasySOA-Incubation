package org.easysoa.registry.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeRequest;
import org.easysoa.registry.rest.marshalling.SoaNodeResult;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RegistryApi {

    @POST
    OperationResult post(SoaNodeRequest soaNodeInfo) throws Exception;

    @POST
    @Path("query")
    @Consumes(MediaType.TEXT_PLAIN)
    SoaNodeResult[] query(String query) throws Exception;
    
    @GET
    SoaNodeResult get() throws Exception;

    @GET
    @Path("{doctype}")
    SoaNodeResult[] get(@PathParam("doctype") String doctype) throws Exception;

    @GET
    @Path("{doctype}/{name}")
    SoaNodeResult get(@PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    @DELETE
    @Path("{doctype}/{name}")
    OperationResult delete(@PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    OperationResult delete(@PathParam("doctype") String doctype, @PathParam("name") String name,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws Exception;


}