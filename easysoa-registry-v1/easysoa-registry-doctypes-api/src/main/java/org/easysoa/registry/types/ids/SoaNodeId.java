package org.easysoa.registry.types.ids;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class SoaNodeId {

    /** may be null, then assumed as default subproject */
    private String subprojectId;
    private String name;
    private String type;

    protected SoaNodeId() {
        // Empty constructor required to be compatible with JAXB serialization
    }

    /**
     * With default subproject. Convenience for testing.
     * @param doctype
     * @param name
     */
    public SoaNodeId(String doctype, String name) {
        this.subprojectId = null;
        this.type = doctype;
        this.name = name;
    }
    
    /**
     * <i>Warning</i>: Use doctype-specific classes instead when available (eg.: {@link EndpointId})
     * to avoid the creation of illegal names. Otherwise, using the wrong <code>name</code> format, 
     * or not initializing required properties will induce errors upon document creation/update.
     * 
     * @param subprojectId the subproject's nuxeo ID
     * @param doctype The document type
     * @param name The SOA name of the document
     */
    public SoaNodeId(String subprojectId, String doctype, String name) {
        this.subprojectId = subprojectId;
        this.type = doctype;
    	this.name = name;
    }
    
    public String getSubprojectId() {
        return this.subprojectId;
    }

    public void setSubprojectId(String subprojectId) {
        this.subprojectId = subprojectId;
    }

    public String getName() {
        return this.name;
    }
    
    protected void setName(String name) {
		this.name = name;
	}
    
    public String getType() {
        return this.type;
    }
    
    protected void setType(String type) {
		this.type = type;
	}

	public Map<String, Serializable> getDefaultPropertyValues() {
		return new HashMap<String, Serializable>();
	}

	@Override
    public String toString() {
        return ((this.subprojectId == null) ? "" : this.subprojectId)
                + ':' + this.type + ':' + this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoaNodeId) {
            SoaNodeId otherId = (SoaNodeId) obj;
            return (this.subprojectId == null && otherId.getSubprojectId() == null
                    || this.subprojectId != null && this.subprojectId.equals(otherId.getSubprojectId()))
                    && this.type.equals(otherId.getType()) && this.name.equals(otherId.getName());
        }
        else {
            return false;
        }
    }
    
    public static SoaNodeId fromString(String string) {
		String[] splitParent = string.split("\\:", 3); // TODO 4 if spname.version
		if (splitParent.length == 3) {
		    String subprojectId = splitParent[0];
		    if (subprojectId != null && subprojectId.trim().length() == 0) {
		        subprojectId = null;
		    }
			return new SoaNodeId(subprojectId, splitParent[1], splitParent[2]);
		}
		else {
			return null;
		}
    }
    
}
