package org.easysoa.registry.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SoaModelSerializationUtil {

    public static final List<OperationInformation> operationInformationFromPropertyValue(Serializable value) {
        List<OperationInformation> operations = new ArrayList<OperationInformation>();
        if (value == null) {
            return operations; 
        }
        // Proper-ish conversion from List<Map<String, Serializable>> hidden behind Serializable, to List<OperationImplementation>
        List<?> operationUnknowns = (List<?>) value;
        for (Object operationUnknown : operationUnknowns) {
            Map<?, ?> operationMap = (Map<?, ?>) operationUnknown;
            operations.add(new OperationInformation(
                    (String) operationMap.get(OperationInformation.OPERATION_NAME),
                    (String) operationMap.get(OperationInformation.OPERATION_PARAMETERS),
                    (String) operationMap.get(OperationInformation.OPERATION_RETURN_PARAMETERS),
                    (String) operationMap.get(OperationInformation.OPERATION_DOCUMENTATION),
                    (String) operationMap.get(OperationInformation.OPERATION_IN_CONTENT_TYPE),
                    (String) operationMap.get(OperationInformation.OPERATION_OUT_CONTENT_TYPE)));
        }
        return operations;
    }
    
    public static final Serializable operationInformationToPropertyValue(List<OperationInformation> operations) {
        List<Map<String, Serializable>> operationsSerializable = new ArrayList<Map<String, Serializable>>();
        if (operations == null) {
            return (Serializable) operationsSerializable; 
        }
        // Conversion from List<OperationImplementation> to List<Map<String, Serializable>>
        for (OperationInformation operation : operations) {
            Map<String, Serializable> operationSerializable = new HashMap<String, Serializable>();
            operationSerializable.put(OperationInformation.OPERATION_NAME, operation.getName());
            operationSerializable.put(OperationInformation.OPERATION_DOCUMENTATION, operation.getDocumentation());
            operationSerializable.put(OperationInformation.OPERATION_PARAMETERS, operation.getParameters());
            operationSerializable.put(OperationInformation.OPERATION_RETURN_PARAMETERS, operation.getReturnParameters());
            operationSerializable.put(OperationInformation.OPERATION_IN_CONTENT_TYPE, operation.getInContentType());
            operationSerializable.put(OperationInformation.OPERATION_OUT_CONTENT_TYPE, operation.getOutContentType());
            operationsSerializable.add(operationSerializable);
        }
        return (Serializable) operationsSerializable;
        
    }
    
}
