package org.easysoa.registry.types.ids;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class SoaNodeId {

    private String name;
    private String type;

    protected SoaNodeId() {
        // Empty constructor required to be compatible with JAXB serialization
    }
    
    /**
     * <i>Warning</i>: Use doctype-specific classes instead when available (eg.: {@link EndpointId})
     * to avoid the creation of illegal names. Otherwise, using the wrong <code>name</code> format, 
     * or not initializing required properties will induce errors upon document creation/update.
     * 
     * @param doctype The document type
     * @param name The SOA name of the document
     */
    public SoaNodeId(String doctype, String name) {
        this.type = doctype;
    	this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    protected void setName(String name) {
		this.name = name;
	}
    
    public String getType() {
        return type;
    }
    
    protected void setType(String type) {
		this.type = type;
	}

	public Map<String, Serializable> getDefaultPropertyValues() {
		return new HashMap<String, Serializable>();
	}

	@Override
    public String toString() {
        return type + ":" + name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoaNodeId) {
            SoaNodeId otherId = (SoaNodeId) obj;
            return this.type.equals(otherId.getType()) && this.name.equals(otherId.getName());
        }
        else {
            return false;
        }
    }
    
    public static SoaNodeId fromString(String string) {
		String[] splitParent = string.split("\\:", 2);
		if (splitParent.length == 2) {
			return new SoaNodeId(splitParent[0], splitParent[1]);
		}
		else {
			return null;
		}
    }
    
}
