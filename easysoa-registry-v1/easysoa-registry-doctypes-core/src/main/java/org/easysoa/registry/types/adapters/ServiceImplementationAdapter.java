package org.easysoa.registry.types.adapters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.utils.ListUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class ServiceImplementationAdapter extends SoaNodeAdapter implements ServiceImplementation {

    public ServiceImplementationAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }
    
    public String getDoctype() {
        return ServiceImplementation.DOCTYPE;
    }
    
    @Override
    public ServiceImplementationName getParsedSoaName() throws Exception {
    	return ServiceImplementationName.fromName(this.getSoaName());
    }

    @Override
    public List<OperationInformation> getOperations() throws PropertyException, ClientException {
        // Proper-ish conversion from List<Map<String, Serializable>> hidden behind Serializable, to List<OperationImplementation>
        List<?> operationUnknowns = (List<?>) documentModel.getPropertyValue(XPATH_OPERATIONS);
        List<OperationInformation> operations = new ArrayList<OperationInformation>();
        for (Object operationUnknown : operationUnknowns) {
            Map<?, ?> operationMap = (Map<?, ?>) operationUnknown;
            operations.add(new OperationInformation(
                    (String) operationMap.get(OPERATION_NAME),
                    (String) operationMap.get(OPERATION_PARAMETERS),
                    (String) operationMap.get(OPERATION_DOCUMENTATION)));
        }
        return operations;
    }
    
    @Override
    public void setOperations(List<OperationInformation> operations) throws PropertyException, ClientException {
        // Conversion from List<OperationImplementation> to List<Map<String, Serializable>>
        List<Map<String, Serializable>> operationsSerializable = new ArrayList<Map<String, Serializable>>();
        for (OperationInformation operation : operations) {
            Map<String, Serializable> operationSerializable = new HashMap<String, Serializable>();
            operationSerializable.put(OPERATION_NAME, operation.getName());
            operationSerializable.put(OPERATION_DOCUMENTATION, operation.getDocumentation());
            operationSerializable.put(OPERATION_PARAMETERS, operation.getParameters());
            operationsSerializable.add(operationSerializable);
        }
        documentModel.setPropertyValue(XPATH_OPERATIONS, (Serializable) operationsSerializable);
        
    }

	@Override
	public List<String> getTests() throws Exception {
		Serializable[] testsArray = (Serializable[]) documentModel.getPropertyValue(XPATH_TESTS);
		return ListUtils.toStringList(testsArray);
	}

	@Override
	public void setTests(List<String> tests) throws Exception {
		documentModel.setPropertyValue(XPATH_TESTS, tests.toArray());
	}
	
	@Override
	public boolean isMock() throws Exception {
		String isMock = (String) documentModel.getPropertyValue(XPATH_ISMOCK);
		return isMock != null && Boolean.parseBoolean(isMock);
	}

}
