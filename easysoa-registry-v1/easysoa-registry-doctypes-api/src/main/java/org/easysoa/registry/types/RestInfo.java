package org.easysoa.registry.types;

/**
 * Facet
 * 
 * @author mdutoo
 *
 */
public interface RestInfo {
	
	static final String FACET_RESTINFO = "RestInfo";

    //static final String XPATH_REST_PATH = "rest:enginePathRoot"; // OPT copied from platform if any
    //static final String XPATH_REST_PATH = "rest:absolutePath"; // OPT computed
	
    static final String XPATH_REST_PATH = "rest:path";
    static final String XPATH_REST_ACCEPTS = "rest:accepts";
    static final String XPATH_REST_CONTENT_TYPE = "rest:content_type";
}
