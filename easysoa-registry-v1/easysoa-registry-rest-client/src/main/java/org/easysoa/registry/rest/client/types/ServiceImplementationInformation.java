package org.easysoa.registry.rest.client.types;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaModelSerializationUtil;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.ListUtils;

public class ServiceImplementationInformation extends SoaNodeInformation implements ServiceImplementation {
    
    public ServiceImplementationInformation(String subprojectId, String name) {
        super(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, name), null, null);
    }

    protected ServiceImplementationInformation(String subprojectId, String doctype, String name) {
        super(new SoaNodeId(subprojectId, doctype, name), null, null);
    }
    
    @Override
    public ServiceImplementationName getParsedSoaName() throws Exception {
    	return ServiceImplementationName.fromName(this.getSoaName());
    }
    
    public List<OperationInformation> getOperations() {
        return SoaModelSerializationUtil.operationInformationFromPropertyValue(
                properties.get(XPATH_OPERATIONS));
    }
    
    public void setOperations(List<OperationInformation> operations) {
        properties.put(XPATH_OPERATIONS,
                SoaModelSerializationUtil.operationInformationToPropertyValue(operations));
    }

	public List<String> getTests() throws Exception {
		Serializable[] testsArray = (Serializable[]) properties.get(XPATH_TESTS);
		return ListUtils.toStringList(testsArray);
	}

	public void setTests(List<String> tests) throws Exception {
		properties.put(XPATH_TESTS, (Serializable) tests);
	}

	@JsonIgnore
	public boolean isMock() {
		return properties.containsKey(XPATH_ISMOCK)
				&& Boolean.parseBoolean((String) properties.get(XPATH_ISMOCK));
	}
	
}
