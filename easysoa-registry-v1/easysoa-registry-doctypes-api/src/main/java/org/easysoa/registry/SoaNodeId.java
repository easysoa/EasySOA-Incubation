package org.easysoa.registry;

import org.easysoa.registry.types.Endpoint;


public class SoaNodeId {

    private String name;
    private String type;

    protected SoaNodeId() {
        // Empty constructor required to be compatible with JAXB serialization
    }
    
    public SoaNodeId(String doctype, String name) {
        this.setType(doctype);
        this.setName(name);
    }
    
    public String getName() {
        return name;
    }
    
    protected void setName(String name) {
        // Remove eventual suffix added by Nuxeo when some proxies conflict
        // XXX Side effect is that no document should end its "real" name with a dot followed by numbers
        this.name = name.replaceAll("\\.[0-9]+$", "");
        if (Endpoint.DOCTYPE.equals(this.type)) {
        	this.name = this.name.replace('/', '-');
        }
    }
    
    public String getType() {
        return type;
    }
    
    protected void setType(String doctype) {
        this.type = doctype;
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
