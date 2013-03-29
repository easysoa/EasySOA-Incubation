package org.easysoa.samples.paf;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Allows for orders management of the PureAirFlowers clients.
 * - All clients are specified by a unique client name
 * - Orders count are represented by a integer
 * This documentation is not 40 lines long, but should be enough
 * to reach a moderately satisfying result for the services documentation
 * indicator.
 * 
 * @author mdutoo
 */
@Consumes({ "application/json", "application/json" })
@Produces("application/json")
@Path("/rest")
public interface PureAirFlowersRestItf {

    /**
     * Returns the orders number for the specified client name.
     */
    @GET
    @Path("/orders/{clientName}")
    public int getOrdersNumber(@PathParam("clientName") String clientName);

    /**
     * Adds an order to the specified client
     */
    @POST
    @Consumes({ "application/json", "application/xml" })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/orders")
    public void addOrder(@FormParam("clientName") String clientName, @FormParam("orderNb") Integer orderNb);

}
