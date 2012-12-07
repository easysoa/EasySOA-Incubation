package org.easysoa.registry;

import java.util.ArrayList;
import java.util.List;

import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.utils.SimpleELEvaluator;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("soaNodeType")
public class SoaNodeTypeDescriptor {

    @XNode("@name")
    public String name;

    @XNode("soaNameFormat")
    public String soaNameFormat;
    
    @XNodeList(value = "subtype", type = ArrayList.class, componentType=String.class, trim = true)
    public List<String> subtypes;

    @XNodeList(value = "idProperties/property", type = ArrayList.class, componentType=String.class, trim = true)
    public List<String> idProperties;
    
    public String evaluateSoaName(SimpleELEvaluator elEvaluator, DocumentModel model)
    		throws ModelIntegrityException, ClientException {
    	if (soaNameFormat != null) {
    		for (String property : idProperties) {
    			if (model.getPropertyValue(property) == null) {
    				throw new ModelIntegrityException("Can't build SOA name, property " + property + " is not set. " +
    						"Make sure the required properties of this doctype are initialized during document creation.");
    			}
    		}
	    	elEvaluator.set("document", model);
	    	return elEvaluator.evaluate(soaNameFormat);
    	}
    	else {
    		return (String) model.getPropertyValue(SoaNode.XPATH_SOANAME);
    	}
    }
    
}