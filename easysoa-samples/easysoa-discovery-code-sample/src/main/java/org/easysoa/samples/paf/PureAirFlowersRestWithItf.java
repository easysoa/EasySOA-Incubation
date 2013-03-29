package org.easysoa.samples.paf;


/**
 * Allows for orders management of the PureAirFlowers clients.
 * - All clients are specified by a unique client name
 * - Orders count are represented by a integer
 * This documentation is not 40 lines long, but should be enough
 * to reach a moderately satisfying result for the services documentation
 * indicator.
 * 
 * NOT SUPPORTED FOR NOW
 * 
 * @author mdutoo
 */
public class PureAirFlowersRestWithItf implements PureAirFlowersRestItf {

    private PureAirFlowersServiceImpl pureAirFlowersServiceImpl;
    
    public PureAirFlowersRestWithItf() {
        pureAirFlowersServiceImpl = new PureAirFlowersServiceImpl();
    }

    /**
     * Returns the orders number for the specified client name.
     */
    public int getOrdersNumber(String clientName) {
        return pureAirFlowersServiceImpl.getOrdersNumber(clientName);
    }

    /**
     * Adds an order to the specified client
     */
    public void addOrder(String clientName, Integer orderNb) {
        pureAirFlowersServiceImpl.addOrder(orderNb, clientName);
    }

}
