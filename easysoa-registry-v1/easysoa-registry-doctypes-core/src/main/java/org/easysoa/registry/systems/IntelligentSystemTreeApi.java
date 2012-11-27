package org.easysoa.registry.systems;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;

public interface IntelligentSystemTreeApi {
	
	public boolean intelligentSystemTreeExists(String name) throws ClientException;

	public void createIntelligentSystemTree(String name, String title) throws ClientException;

	public void setSoaNode(String treeName, SoaNodeId identifier, String path) throws ClientException;
	
	public void removeSoaNode(String treeName, SoaNodeId identifier) throws ClientException;
	
}
