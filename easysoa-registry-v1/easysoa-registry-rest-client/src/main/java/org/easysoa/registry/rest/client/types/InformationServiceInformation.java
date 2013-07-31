package org.easysoa.registry.rest.client.types;

import java.util.List;

import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.SoaModelSerializationUtil;
import org.easysoa.registry.types.ids.SoaNodeId;

public class InformationServiceInformation extends SoaNodeInformation implements InformationService {

    public InformationServiceInformation(String subprojectId, String name) {
        super(new SoaNodeId(subprojectId, InformationServiceInformation.DOCTYPE, name), null, null);
    }

    public List<OperationInformation> getOperations() {
        return SoaModelSerializationUtil.operationInformationFromPropertyValue(
                properties.get(XPATH_OPERATIONS));
    }
    
    public void setOperations(List<OperationInformation> operations) {
        properties.put(XPATH_OPERATIONS,
                SoaModelSerializationUtil.operationInformationToPropertyValue(operations));
    }
    
}
