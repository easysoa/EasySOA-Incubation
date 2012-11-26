package org.easysoa.registry.rest.marshalling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class SoaNodeAbstractRestModel implements SoaNode {

    @JsonTypeInfo(use = Id.MINIMAL_CLASS, include = As.WRAPPER_OBJECT, property = "type")
    protected Map<String, Serializable> properties = new HashMap<String, Serializable>();
    
    protected List<SoaNodeId> parentIds = new ArrayList<SoaNodeId>();
    
    public Map<String, Serializable> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }
    
    public Object getProperty(String xpath) throws Exception {
        return this.properties.get(xpath);
    }

    public void setProperty(String xpath, Serializable value) throws Exception {
        this.properties.put(xpath, value);
    }

    public List<SoaNodeId> getParentIds() {
        return parentIds;
    }
    
    public void setParentIds(List<SoaNodeId> parentIds) {
        this.parentIds = parentIds;
    }

    public void addParentId(SoaNodeId parentId) {
        this.parentIds.add(parentId);
    }

    @Override
	public String getUuid() throws Exception {
		return (String) properties.get(XPATH_UUID);
	}

	public String getTitle() {
        return (String) properties.get(SoaNode.XPATH_TITLE);
    }
    
    public void setTitle(String title) {
        properties.put(SoaNode.XPATH_TITLE, title);
    }
    
    @JsonIgnore
    public boolean isPlaceholder() throws Exception {
        return (Boolean) properties.get(SoaNode.XPATH_ISPLACEHOLDER);
    }

    @JsonIgnore
    public void setIsPlaceholder(boolean isPlaceholder) throws Exception {
        properties.put(SoaNode.XPATH_ISPLACEHOLDER, isPlaceholder);
    }
    
}
