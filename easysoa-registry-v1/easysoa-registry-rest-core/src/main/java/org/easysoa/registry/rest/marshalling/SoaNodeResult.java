package org.easysoa.registry.rest.marshalling;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.types.ids.SoaNodeId;

public class SoaNodeResult extends SoaNodeAbstractRestModel {

    protected SoaNodeId id;
    
    public SoaNodeResult(SoaNodeId id, Map<String, Serializable> properties, List<SoaNodeId> parentIds) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Serializable>() : properties;
        this.parentIds = (parentIds == null) ? new LinkedList<SoaNodeId>() : parentIds;
    }

	@Override
	public SoaNodeId getSoaNodeId() throws Exception {
		return this.id;
	}

	@Override
	public String getSoaName() throws Exception {
		return this.id.getName();
	}
    
}
