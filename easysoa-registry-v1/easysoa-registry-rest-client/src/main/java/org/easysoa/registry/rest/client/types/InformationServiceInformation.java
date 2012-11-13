package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;

public class InformationServiceInformation extends SoaNodeInformation implements InformationService {

    public InformationServiceInformation(String name) {
        super(new SoaNodeId(InformationServiceInformation.DOCTYPE, name), null, null);
    }
    
}
