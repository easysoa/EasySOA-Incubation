package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.types.ids.SoaNodeId;


public interface SoaNode extends Document {

	public static final String ABSTRACT_DOCTYPE = "SoaNode";
	
    public static final String SCHEMA = "soanode";
    
    public static final String XPATH_SOANAME = "soan:name";

    /** copied from subProject TODO */
    ///public static final String XPATH_SUBPROJECT = "subproject:subproject";
    ////public static final String XPATH_PARENT_SUBPROJECTS = "subproject:parentSubprojects"; //// only on subProject TODO
    /** subproject + parentSubproject(s)'s visibleSubprojects, copied from subproject, computed once there TODO */
    ///public static final String XPATH_VISIBLE_SUBPROJECTS = "subproject:visibleSubprojects";
    
    public static final String XPATH_ISPLACEHOLDER = "soan:isplaceholder";

    public static final String XPATH_PARENTSIDS = "soan:parentIds";
    
    public static final String XPATH_UUID = "ecm:uuid";

    public static final String FACET = "SoaNode";

    String getUuid() throws Exception;
    
    SoaNodeId getSoaNodeId() throws Exception;

    String getSoaName() throws Exception;

    boolean isPlaceholder() throws Exception;
    
    void setIsPlaceholder(boolean isPlaceholder) throws Exception;

    List<SoaNodeId> getParentIds() throws Exception;

	void setParentIds(List<SoaNodeId> parentIds) throws Exception;
	
	SoaNodeId getParentOfType(String doctype) throws Exception;
	
	List<SoaNodeId> getParentsOfType(String doctype) throws Exception;
}