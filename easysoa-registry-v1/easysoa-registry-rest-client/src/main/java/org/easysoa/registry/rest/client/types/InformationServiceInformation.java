package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.rest.marshalling.SoaNodeResult;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;

public class InformationServiceInformation extends SoaNodeResult implements InformationService {

    public InformationServiceInformation(String name) {
        super(new SoaNodeId(InformationServiceInformation.DOCTYPE, name), null, null);
    }
    
}
