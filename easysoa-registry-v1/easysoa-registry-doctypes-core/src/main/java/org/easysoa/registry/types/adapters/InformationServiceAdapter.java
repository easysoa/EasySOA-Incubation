package org.easysoa.registry.types.adapters;

import java.util.List;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.SoaModelSerializationUtil;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mdutoo
 *
 */
public class InformationServiceAdapter extends SoaNodeAdapter implements InformationService {

    public InformationServiceAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }
    
    public String getDoctype() {
        return InformationService.DOCTYPE;
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
    
}
