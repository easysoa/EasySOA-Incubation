package org.easysoa.registry;

import java.io.Serializable;
import java.util.Map;

import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.utils.SimpleELEvaluator;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

public class ModelIntegrityValidator {

	private final Map<String, SoaNodeTypeDescriptor> soaNodeTypes;
	
	private SimpleELEvaluator elEvaluator;

	public ModelIntegrityValidator(Map<String, SoaNodeTypeDescriptor> soaNodeTypes) {
		this.soaNodeTypes = soaNodeTypes;
		this.elEvaluator = new SimpleELEvaluator();
	}
	
	/**
	 * @param model
	 * @throws ModelIntegrityException If the soaname doesn't match the properties.
	 */
	public void validate(DocumentModel model) throws ModelIntegrityException, PropertyException, ClientException {
		SoaNodeTypeDescriptor soaNodeTypeDescriptor = soaNodeTypes.get(model.getType());
		if (soaNodeTypeDescriptor != null) {
			String expectedSoaName = soaNodeTypeDescriptor.evaluateSoaName(elEvaluator, model);
			Serializable actualSoaName = model.getPropertyValue(SoaNode.XPATH_SOANAME);
			if (!expectedSoaName.equals(actualSoaName)) {
				throw new ModelIntegrityException("Invalid SoaName, found '" 
						+ actualSoaName + "' instead of '" + expectedSoaName + "'");
			}
		}
	}
	
}
