package org.easysoa.registry.facets;

public interface PlatformDataFacet {

    static final String FACET_PLATFORMDATA = "PlatformData";

    static final String SCHEMA_PLATFORM_PREFIX = "platform:";

    static final String XPATH_IDE = SCHEMA_PLATFORM_PREFIX + "ide";
    static final String XPATH_LANGUAGE = SCHEMA_PLATFORM_PREFIX + "language";
    static final String XPATH_BUILD = SCHEMA_PLATFORM_PREFIX + "build";
    static final String XPATH_SERVICE_LANGUAGE = SCHEMA_PLATFORM_PREFIX + "serviceLanguage";
    
    static final String XPATH_DELIVERABLE_NATURE = SCHEMA_PLATFORM_PREFIX + "deliverableNature";
    static final String XPATH_DELIVERABLE_REPOSITORY_URL = SCHEMA_PLATFORM_PREFIX + "deliverableRepositoryUrl";

    static final String XPATH_SERVICE_PROTOCOL = SCHEMA_PLATFORM_PREFIX + "serviceProtocol";
    static final String XPATH_TRANSPORT_PROTOCOL = SCHEMA_PLATFORM_PREFIX + "transportProtocol";
    static final String XPATH_SERVICE_RUNTIME = SCHEMA_PLATFORM_PREFIX + "serviceRuntime";
    static final String XPATH_APP_SERVER_RUNTIME = SCHEMA_PLATFORM_PREFIX + "appServerRuntime";

    static final String XPATH_SERVICE_SECURITY = SCHEMA_PLATFORM_PREFIX + "serviceSecurity";
    static final String XPATH_SERVICE_SECURITY_MANAGER_URL = SCHEMA_PLATFORM_PREFIX + "serviceSecurityManagerUrl";
    
    static final String XPATH_SERVICE_MONITORING = SCHEMA_PLATFORM_PREFIX + "serviceMonitoring";
    static final String XPATH_SERVICE_MONITORYING_MANAGER_URL = SCHEMA_PLATFORM_PREFIX + "serviceMonitoringManagerUrl";


    static final String LANGUAGE_JAVA = "Java";
    static final String LANGUAGE_JAVASCRIPT = "Javascript";
    
    static final String SERVICE_LANGUAGE_JAXWS = "JAX-WS";
    static final String SERVICE_LANGUAGE_JAXRS = "JAX-RS";
}
