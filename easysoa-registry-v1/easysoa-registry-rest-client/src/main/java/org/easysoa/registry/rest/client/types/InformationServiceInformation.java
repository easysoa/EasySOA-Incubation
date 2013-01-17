package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;

public class InformationServiceInformation extends SoaNodeInformation implements InformationService {

    public InformationServiceInformation(String subprojectId, String name) {
        super(new SoaNodeId(subprojectId, InformationServiceInformation.DOCTYPE, name), null, null);
    }
    
}
