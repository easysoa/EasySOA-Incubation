package org.easysoa.registry.types.adapters;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class CoreDoctypesAdapterFactory implements DocumentAdapterFactory {
    
    private static Logger logger = Logger.getLogger(CoreDoctypesAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            if (SoaNode.class.equals(itf)) {
                return new SoaNodeAdapter(doc);
            }
            if (Deliverable.class.equals(itf)) {
                return new DeliverableAdapter(doc);
            }
            if (ServiceImplementation.class.equals(itf)) {
                return new ServiceImplementationAdapter(doc);
            }
            if (EndpointConsumption.class.equals(itf)) {
                return new EndpointConsumptionAdapter(doc);
            }
            if (ServiceConsumption.class.equals(itf)) {
                if (EndpointConsumption.DOCTYPE.equals(doc.getType())) {
                    return new EndpointConsumptionAdapter(doc);
                }
            }
            if (DeployedDeliverable.class.equals(itf)) {
                return new DeployedDeliverableAdapter(doc);
            }
            if (Endpoint.class.equals(itf)) {
                return new EndpointAdapter(doc);
            }
        }
        catch (Exception e) {
            logger.warn("Could not create adapter: " + e.getMessage());
        }
        return null;
    }
    
}
