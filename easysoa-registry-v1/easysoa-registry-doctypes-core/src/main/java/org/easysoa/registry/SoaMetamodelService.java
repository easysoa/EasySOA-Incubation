package org.easysoa.registry;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface SoaMetamodelService {

    public static final String EXTENSIONPOINT_TYPES = "types";

    public static final String EXTENSIONPOINT_INHERITEDFACETS = "inheritedFacets";

	SoaNodeTypeDescriptor getSoaNodeType(String name);
	
    Collection<String> getChildren(String type);
    
    /**
     * Computes the minimal path needed to link two document types.
     * The returned list is empty if <i>toType</i> can be directly stored under <i>fromType</i>.
     * 
     * @param fromType The parent type
     * @param toType The child type
     * @return The path (without <i>fromType</i>, or null if it's not possible
     * to store <i>toType</i> under <i>fromType</i>)
     */
    List<String> getPath(String fromType, String toType);
    
	Set<String> getInheritedFacets(Set<String> filter);

	void applyFacetInheritance(CoreSession documentManager, DocumentModel model,
			boolean isFacetSource) throws Exception;

	void resetInheritedFacets(DocumentModel model) throws Exception;

	
}