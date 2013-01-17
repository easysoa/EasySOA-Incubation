package org.easysoa.registry.systems;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;

public interface IntelligentSystemTreeApi {
	
	public boolean intelligentSystemTreeExists(String subprojectId, String name) throws ClientException;

	public void createIntelligentSystemTree(String subprojectId, String name, String title) throws ClientException;

	public void classifySoaNode(String treeName, SoaNodeId identifier, String path) throws ClientException;
	
	public void deleteSoaNode(String treeName, SoaNodeId identifier) throws ClientException;
	
}
