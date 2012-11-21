package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.types.names.ServiceImplementationName;


/**
 * SoaName specs:
 * Either "ws:TARGETNS:NAME=SERVICENAME"
 * Or "java:PROJECT:INTERFACE=NAME"
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceImplementation extends SoaNode, RestInfo, WsdlInfo {
	
    static final String DOCTYPE = "ServiceImplementation";

	static final String XPATH_LANGUAGE = "impl:language";
	
    static final String XPATH_TECHNOLOGY = "impl:technology";
    
    static final String XPATH_OPERATIONS = "impl:operations";
    
    static final String XPATH_DOCUMENTATION = "impl:documentation";

    static final String XPATH_ISMOCK = "impl:ismock";

    static final String XPATH_TESTS = "impl:tests";

    static final String XPATH_LINKED_INFORMATION_SERVICE = "impl:linkedInformationService";

    static final String XPATH_COMPONENT = "impl:component"; // nuxeo id ; component that this impl fills ; TODO rather candidate also ?

    static final String XPATH_PLATFORM = "impl:platform"; // nuxeo id ; this impl's dev platform ; TODO rather candidate also ?

    static final String OPERATION_NAME = "operationName";
    
    static final String OPERATION_PARAMETERS = "operationParameters";
    
    static final String OPERATION_DOCUMENTATION = "operationDocumentation";
    
    ServiceImplementationName getParsedSoaName() throws Exception;

    List<OperationImplementation> getOperations() throws Exception;
    
    void setOperations(List<OperationImplementation> operations) throws Exception;

    List<String> getTests() throws Exception;

    void setTests(List<String> tests) throws Exception;
    
    boolean isMock() throws Exception;
    
}
