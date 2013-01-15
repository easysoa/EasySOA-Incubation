package org.easysoa.registry.types;


public interface SubprojectNode extends Document {
	
    public static final String SCHEMA = "spnode";

    /** copied from subProject TODO */
    public static final String XPATH_SUBPROJECT = SCHEMA + ":subproject";
    //public static final String XPATH_PARENT_SUBPROJECTS = SCHEMA + ":parentSubprojects"; //// only on subProject TODO
    /** subproject + parentSubproject(s)'s visibleSubprojects, copied from subproject, computed once there TODO */
    public static final String XPATH_VISIBLE_SUBPROJECTS = SCHEMA + ":visibleSubprojects"; // for looking for referenced nodes
    public static final String XPATH_VISIBLE_SUBPROJECTS_CSV = SCHEMA + ":visibleSubprojectsCsv"; // for looking for referencing nodes

    public static final String FACET = "SubprojectNode";

}