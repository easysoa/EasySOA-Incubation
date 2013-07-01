package org.easysoa.registry.types.adapters;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Endpoint;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 *
 * @author mkalam-alami
 *
 */
public class EndpointAdapter extends SoaNodeAdapter implements Endpoint {

    public EndpointAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return Endpoint.DOCTYPE;
    }

    @Override
    public String getEnvironment() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_ENDP_ENVIRONMENT);
    }

    @Override
    public String getHost() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_ENDP_HOST);
    }
}
