package org.easysoa.discovery.code;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.SoaNodeResult;
import org.easysoa.registry.types.java.JavaServiceImplementation;

public class CodeDiscoveryRegistryClient {

    private final RegistryApi registryApi;

    CodeDiscoveryRegistryClient(RegistryApi registryApi) {
        this.registryApi = registryApi;
    }

    public SoaNodeResult[] findImplsByInterface(String wsInterface) throws Exception {
        return registryApi.query("SELECT * FROM " 
        + JavaServiceImplementation.DOCTYPE + " WHERE "
        + JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE + " = '" + wsInterface + "'"
        + " AND " + JavaServiceImplementation.XPATH_ISMOCK + " != 'true'");
    }
    
}
