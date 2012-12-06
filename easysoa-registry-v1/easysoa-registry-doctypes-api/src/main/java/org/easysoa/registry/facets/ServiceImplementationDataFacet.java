package org.easysoa.registry.facets;

public interface ServiceImplementationDataFacet {

    static final String FACET_SERVICEIMPLEMENTATIONDATA = "ServiceImplementationData";

    static final String XPATH_IMPL_IDE = "impl:ide";
    
    static final String XPATH_IMPL_LANGUAGE = "impl:language";
    
    static final String XPATH_TECHNOLOGY = "impl:technology";

    static final String XPATH_IMPL_BUILD = "impl:build";
    
    static final String XPATH_OPERATIONS = "impl:operations";
    
    static final String XPATH_DOCUMENTATION = "impl:documentation";

    static final String XPATH_ISMOCK = "impl:ismock";

    static final String XPATH_TESTS = "impl:tests";

    static final String XPATH_PROVIDED_INFORMATION_SERVICE = "impl:providedInformationService";

    static final String XPATH_PLATFORM = "impl:linkedPlatform"; // nuxeo id ; this impl's dev platform ; TODO rather candidate also ?

    static final String OPERATION_NAME = "operationName";
    
    static final String OPERATION_PARAMETERS = "operationParameters";
    
    static final String OPERATION_DOCUMENTATION = "operationDocumentation";
    
}
