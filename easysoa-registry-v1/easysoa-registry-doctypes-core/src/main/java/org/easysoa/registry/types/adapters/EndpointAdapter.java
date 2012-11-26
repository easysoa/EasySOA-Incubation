package org.easysoa.registry.types.adapters;

import java.util.HashMap;
import java.util.Map;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ids.SoaNodeIdParser;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class EndpointAdapter extends SoaNodeAdapter implements Endpoint, SoaNodeIdParser {

    public EndpointAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return Endpoint.DOCTYPE;
    }
    
    public String getEnvironment() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_ENVIRONMENT);
    }

	@Override
	public Map<String, Object> parseNameAsProperties(String name) {
		String[] splitName = name.split(":");
		if (splitName.length == 2) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(Endpoint.XPATH_ENVIRONMENT, splitName[0]);
			properties.put(Endpoint.XPATH_URL, splitName[1]);
			return properties;
		}
		else {
			return null;
		}
	}
}
