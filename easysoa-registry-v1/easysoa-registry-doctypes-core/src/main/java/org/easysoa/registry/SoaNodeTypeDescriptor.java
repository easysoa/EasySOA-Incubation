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

    @XNodeList(value = "immutableProperties/property", type = ArrayList.class, componentType=String.class, trim = true)
    public List<String> immutableProperties;
    
    public String evaluateSoaName(SimpleELEvaluator elEvaluator, DocumentModel model) throws ClientException {
    	if (soaNameFormat != null) {
	    	elEvaluator.set("document", model);
	    	return elEvaluator.evaluate(soaNameFormat);
    	}
    	else {
    		return (String) model.getPropertyValue(SoaNode.XPATH_SOANAME);
    	}
    }
    
}