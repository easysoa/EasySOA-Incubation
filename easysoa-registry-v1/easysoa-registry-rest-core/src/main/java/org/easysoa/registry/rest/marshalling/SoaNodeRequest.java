package org.easysoa.registry.rest.marshalling;


public abstract class SoaNodeRequest extends SoaNodeAbstractRestModel {

    protected String type;
    
    public SoaNodeRequest(String type) {
    	this.type = type;
    }
    
}
