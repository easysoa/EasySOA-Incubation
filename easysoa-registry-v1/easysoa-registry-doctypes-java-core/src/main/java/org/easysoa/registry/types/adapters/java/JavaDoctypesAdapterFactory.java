package org.easysoa.registry.types.adapters.java;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.adapters.EndpointConsumptionAdapter;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class JavaDoctypesAdapterFactory implements DocumentAdapterFactory {
    
    private static Logger logger = Logger.getLogger(JavaDoctypesAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            if (MavenDeliverable.class.equals(itf)) {
                return new MavenDeliverableAdapter(doc);
            }
            if (JavaServiceConsumption.class.equals(itf)) {
                return new JavaServiceConsumptionAdapter(doc);
            }
            if (ServiceConsumption.class.equals(itf)) {
                if (JavaServiceConsumption.DOCTYPE.equals(doc.getType())) {
                    return new JavaServiceConsumptionAdapter(doc);
                } else if (EndpointConsumption.DOCTYPE.equals(doc.getType())) {
                	// taken from CoreDoctypesAdapterFactory, TODO using extends / super ??
                    return new EndpointConsumptionAdapter(doc);
                }
                // NB. can't support more than one ServiceConsumption subclassing project !! TODO LATER better
            }
        }
        catch (Exception e) {
            logger.warn("Could not create adapter: " + e.getMessage());
        }
        return null;
    }
    
}
