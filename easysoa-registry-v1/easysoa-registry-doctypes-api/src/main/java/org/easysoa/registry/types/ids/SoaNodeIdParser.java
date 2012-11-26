package org.easysoa.registry.types.ids;

import java.util.Map;

import org.easysoa.registry.types.SoaNode;

public interface SoaNodeIdParser extends SoaNode {
	
	public Map<String, Object> parseNameAsProperties(String name);
	
}
