package org.easysoa.registry.types.ids;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.ServiceImplementation;


public class ServiceImplementationSoaNodeId extends SoaNodeId {
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	private String implementationName;

	public ServiceImplementationSoaNodeId(ServiceIdentifierType type, String namespace,
			String interfaceName, String implementationName) {
		super(ServiceImplementation.DOCTYPE, buildName(type, namespace, interfaceName, implementationName));
		this.type = type;
		this.namespace = namespace;
		this.interfaceName = interfaceName;
		this.implementationName = implementationName;
	}
	
	private static String buildName(ServiceIdentifierType type, String namespace,
			String interfaceName, String implementationName) {
		String name;
		switch (type) {
		case WEB_SERVICE: name = InformationServiceSoaNodeId.WS; break;
		case JAVA_INTERFACE: name = InformationServiceSoaNodeId.JAVA; break;
		default: name = "???";
		}
		return name + ":" + namespace + ":" + interfaceName + "=" + implementationName;
	}

	public ServiceImplementationSoaNodeId(String name) {
		super(ServiceImplementation.DOCTYPE, name);
		String[] splitName = name.split("[:=]");
		
		if (splitName.length == 4) {
			// Namespace
			if (InformationServiceSoaNodeId.WS.equals(splitName[0])) {
				this.type = ServiceIdentifierType.WEB_SERVICE;
			}
			else if (InformationServiceSoaNodeId.JAVA.equals(splitName[0])) {
				this.type = ServiceIdentifierType.JAVA_INTERFACE;
			}
			else {
				this.type = ServiceIdentifierType.UNKNOWN;
			}
			
			this.namespace = splitName[1];
			this.interfaceName = splitName[2];
			this.implementationName = splitName[3];
		}
		else {
			this.type = ServiceIdentifierType.UNKNOWN;
			this.namespace = null;
			this.interfaceName = null;
			this.implementationName = name;
		}
	}
	
	public ServiceIdentifierType getServiceIdentifierType() {
		return type;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public String getImplementationName() {
		return implementationName;
	}
	
	public String getInformationServiceSoaName() {
		return this.getName().replaceAll("=.*$", "");
	}
	
}
