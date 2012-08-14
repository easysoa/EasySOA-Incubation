package org.easysoa.registry.types.adapters;

import org.apache.log4j.Logger;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class CoreDoctypesAdapterFactory implements DocumentAdapterFactory {
    
    private static Logger logger = Logger.getLogger(CoreDoctypesAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            if (Deliverable.class.equals(itf)) {
                return new DeliverableAdapter(doc);
            }
            if (DeployedDeliverable.class.equals(itf)) {
                return new DeployedDeliverableAdapter(doc);
            }
            if (Endpoint.class.equals(itf)) {
                return new EndpointAdapter(doc);
            }
        }
        catch (InvalidDoctypeException e) {
            logger.warn("Could not create adapter: " + e.getMessage());
        }
        return null;
    }
    
}