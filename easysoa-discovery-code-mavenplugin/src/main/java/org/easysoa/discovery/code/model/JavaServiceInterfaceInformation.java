package org.easysoa.discovery.code.model;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;

public class JavaServiceInterfaceInformation {

    private SoaNodeId mavenDeliverableId;
    
    private String interfaceName;

	private String wsNamespace;

	private String wsName;

    public JavaServiceInterfaceInformation(String mavenGroupId, String mavenArtifactId, String interfaceName,
    		String wsNamespace, String wsName) throws Exception {
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
