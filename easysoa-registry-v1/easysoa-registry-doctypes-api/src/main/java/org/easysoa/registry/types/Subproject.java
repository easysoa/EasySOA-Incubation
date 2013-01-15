package org.easysoa.registry.types;


public interface Subproject extends Document, SubprojectNode {

	public static final String DOCTYPE = "Subproject";
	
    public static final String SCHEMA = "subproject";

    public static final String XPATH_PARENT_SUBPROJECTS = SCHEMA + ":parentSubprojects"; //// only on subProject TODO
    // NB. others are in spnode

}