package org.easysoa.discovery.code.model;

import java.util.Map;

import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ids.SoaNodeId;


// TODO change wsName(space) to serviceItfName (for REST also), or make it WS only
public class JavaServiceInterfaceInformation {

    private SoaNodeId mavenDeliverableId;
    
    private String interfaceName;

	private String wsNamespace;

	private String wsName;

	private Map<String, OperationInformation> operations;

    public JavaServiceInterfaceInformation(String subprojectId,
            String mavenGroupId, String mavenArtifactId, String interfaceName,
    		String wsNamespace, String wsName, Map<String, OperationInformation> operations) throws Exception {
        this.operations = operations;
        this.mavenDeliverableId = new MavenDeliverableInformation(subprojectId, mavenGroupId, mavenArtifactId).getSoaNodeId();
        this.interfaceName = interfaceName;
		this.wsNamespace = wsNamespace;
		this.wsName = wsName;
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
    
    public SoaNodeId getMavenDeliverableId() {
        return mavenDeliverableId;
    }
    
    public String getWsPortTypeLocalName() {
		return wsName;
	}
    
    public String getWsPortTypeNS() {
		return wsNamespace;
	}
    
    public String getWsPortTypeName() {
		return JaxWSSourcesHandler.toShortNsName(this.getWsPortTypeNS(), this.getWsPortTypeLocalName());
	}
    
    /**
     * key = methodName, value = operation
     * @return
     */
    public Map<String, OperationInformation> getOperations() {
		return operations;
	}
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(interfaceName);
        }
        else {
            return super.equals(obj);
        }
    }
    
}
