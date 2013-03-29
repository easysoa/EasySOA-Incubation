package org.easysoa.registry.types.adapters;

import java.io.Serializable;
import java.util.List;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaModelSerializationUtil;
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
        return SoaModelSerializationUtil.operationInformationFromPropertyValue(
                documentModel.getPropertyValue(XPATH_OPERATIONS));
    }
    
    @Override
    public void setOperations(List<OperationInformation> operations) throws PropertyException, ClientException {
        documentModel.setPropertyValue(XPATH_OPERATIONS,
                SoaModelSerializationUtil.operationInformationToPropertyValue(operations));
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
