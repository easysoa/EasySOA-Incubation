package org.easysoa.registry.types.adapters;

import java.util.List;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mdutoo
 *
 */
public class ServiceConsumptionAdapter extends SoaNodeAdapter implements ServiceConsumption {

    private final CoreSession documentManager;

    public ServiceConsumptionAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
        this.documentManager = documentModel.getCoreSession();
    }

    @Override
    public String getDoctype() {
        return ServiceConsumption.DOCTYPE;
    }

    /**
     * Not impl'd here
     */
    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
    	throw new UnsupportedOperationException();
    }

	@Override
	public boolean getIsTest() throws Exception {
		return (Boolean) documentModel.getPropertyValue(ServiceConsumption.XPATH_ISTEST);
	}
    
}
