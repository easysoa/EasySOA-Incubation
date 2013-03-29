package org.easysoa.registry.systems;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;

public interface IntelligentSystemTreeApi {
	
	public boolean intelligentSystemTreeExists(String subprojectId, String name) throws ClientException;

	/**
	 * Creates and saves the given new ITS
	 * @param subprojectId
	 * @param name
	 * @param title
	 * @throws ClientException
	 */
	public void createIntelligentSystemTree(String subprojectId, String name, String title) throws ClientException;

	/**
	 * Creates the required System tree and Proxy if doesn't exist yet, moves the document if exists but at wrong place
	 * (but this doesn't in itself change the document (?))
	 * @param treeName
	 * @param identifier
	 * @param path
	 * @return whether tree changed
	 * @throws ClientException
	 */
	public boolean classifySoaNode(String treeName, SoaNodeId identifier, String path) throws ClientException;
	
	/**
	 * Deletes given SoaNode if in given tree
	 * @param treeName
	 * @param identifier
     * @return whether tree changed
	 * @throws ClientException
	 */
	public boolean deleteSoaNode(String treeName, SoaNodeId identifier) throws ClientException;
	
}
