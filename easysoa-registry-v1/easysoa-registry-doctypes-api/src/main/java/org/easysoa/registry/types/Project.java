package org.easysoa.registry.types;


public interface Project extends Document {

	public static final String DOCTYPE = "Project";

    public static final String DEFAULT_PROJECT_NAME = "MyProject";

    public static final String DEFAULT_PROJECT_PATH = Repository.DEFAULT_DOMAIN_PATH + '/' + Project.DEFAULT_PROJECT_NAME;

}