package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.PlatformDataFacet;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.ServiceImplementationDataFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.types.ids.ServiceImplementationId;


/**
 * SoaName specs:
 * Either "ws:TARGETNS:NAME=SERVICENAME"
 * Or "java:PROJECT:INTERFACE=NAME"
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceImplementation extends SoaNode, RestInfoFacet, WsdlInfoFacet,
	ArchitectureComponentFacet, PlatformDataFacet, ServiceImplementationDataFacet {
	
    static final String DOCTYPE = "ServiceImplementation";
    
    ServiceImplementationId getParsedSoaName() throws Exception;

    List<OperationInformation> getOperations() throws Exception;
    
    void setOperations(List<OperationInformation> operations) throws Exception;

    List<String> getTests() throws Exception;

    void setTests(List<String> tests) throws Exception;
    
    boolean isMock() throws Exception;
    
}
