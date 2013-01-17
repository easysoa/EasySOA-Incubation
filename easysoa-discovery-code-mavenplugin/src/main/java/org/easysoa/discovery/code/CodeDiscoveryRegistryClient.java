package org.easysoa.discovery.code;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

public class CodeDiscoveryRegistryClient {

    private final RegistryApi registryApi;

    CodeDiscoveryRegistryClient(RegistryApi registryApi) {
        this.registryApi = registryApi;
    }

    public SoaNodeInformation[] findImplsByInterface(String subprojectId, String wsInterface) throws Exception {
        return registryApi.query(subprojectId, "SELECT * FROM " 
        + JavaServiceImplementation.DOCTYPE + " WHERE "
        + JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE + " = '" + wsInterface + "'"
        + " AND " + JavaServiceImplementation.XPATH_ISMOCK + " != 'true'");
    }
    
}
