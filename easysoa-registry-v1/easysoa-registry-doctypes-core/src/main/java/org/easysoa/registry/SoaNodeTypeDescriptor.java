package org.easysoa.registry;

import java.util.ArrayList;
import java.util.List;

import org.easysoa.registry.utils.SimpleELEvaluator;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;
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

    @XNode("managedSoaName")
    public String managedSoaName = null;
    
    @XNodeList(value = "subtype", type = ArrayList.class, componentType=String.class, trim = true)
    public List<String> subtypes;
    
    public String evaluateSoaName(SimpleELEvaluator elEvaluator, DocumentModel model) {
    	elEvaluator.set("document", model);
    	return elEvaluator.evaluate(this.managedSoaName);
    }
    
}