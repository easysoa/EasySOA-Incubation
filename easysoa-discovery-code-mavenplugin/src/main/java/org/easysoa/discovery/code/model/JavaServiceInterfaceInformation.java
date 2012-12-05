package org.easysoa.discovery.code.model;

import java.util.List;

import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ids.SoaNodeId;

public class JavaServiceInterfaceInformation {

    private SoaNodeId mavenDeliverableId;
    
    private String interfaceName;

	private String wsNamespace;

	private String wsName;

	private List<OperationInformation> operations;

    public JavaServiceInterfaceInformation(String mavenGroupId, String mavenArtifactId, String interfaceName,
    		String wsNamespace, String wsName, List<OperationInformation> operations) throws Exception {
        this.operations = operations;
		this.mavenDeliverableId = new MavenDeliverableInformation(mavenGroupId, mavenArtifactId).getSoaNodeId();
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
    
    public String getWsName() {
		return wsName;
	}
    
    public String getWsNamespace() {
		return wsNamespace;
	}
    
    public List<OperationInformation> getOperations() {
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
