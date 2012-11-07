package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.types.names.ServiceImplementationName;


/**
 * SoaName specs:
 * Either "ws:TARGETNS:NAME=SERVICENAME"
 * Or "itf:PROJECT:INTERFACE=NAME"
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceImplementation extends SoaNode {
	
    static final String DOCTYPE = "ServiceImplementation";
    
    static final String XPATH_TECHNOLOGY = "impl:technology";
    
    static final String XPATH_OPERATIONS = "impl:operations";
    
    static final String XPATH_DOCUMENTATION = "impl:documentation";

    static final String XPATH_ISMOCK = "impl:ismock";

    static final String XPATH_TESTS = "impl:tests";

    static final String XPATH_LINKED_INFORMATION_SERVICE = "impl:linkedInformationService";

    static final String XPATH_WSDL_PORTTYPE_NAME = "impl:wsdlPortTypeName";

    static final String XPATH_WSDL_SERVICE_NAME = "impl:wsdlServiceName";
    
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
