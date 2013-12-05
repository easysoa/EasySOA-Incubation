package org.easysoa.registry.types;


public interface Subproject extends Document, SubprojectNode {

	public static final String DOCTYPE = "Subproject";
	
    public static final String SCHEMA = "subproject";

    public static final String SPECIFICATIONS_SUBPROJECT_NAME = "Specifications";
    public static final String REALISATION_SUBPROJECT_NAME = "Realisation";
    public static final String DEPLOIEMENT_SUBPROJECT_NAME = "Deploiement";
    
    public static final String DEFAULT_SUBPROJECT_NAME = REALISATION_SUBPROJECT_NAME;
    public static final String DEFAULT_SUBPROJECT_PATH = Project.DEFAULT_PROJECT_PATH + '/' + Subproject.DEFAULT_SUBPROJECT_NAME;

    public static final String SUBPROJECT_ID_VERSION_SEPARATOR = "_v";
    public static final String DEFAULT_SUBPROJECT_ID = DEFAULT_SUBPROJECT_PATH + SUBPROJECT_ID_VERSION_SEPARATOR;

    public static final String XPATH_PARENT_SUBPROJECTS = SCHEMA + ":parentSubprojects"; //// only on subProject TODO
    // NB. others are in spnode

}