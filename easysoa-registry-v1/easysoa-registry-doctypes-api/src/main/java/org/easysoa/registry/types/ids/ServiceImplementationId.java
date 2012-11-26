package org.easysoa.registry.types.ids;

import org.easysoa.registry.types.ServiceImplementation;


public class ServiceImplementationId extends SoaNodeId {
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	private String implementationName;

	public ServiceImplementationId(ServiceIdentifierType type, String namespace,
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
		case WEB_SERVICE: name = InformationServiceId.WS; break;
		case JAVA_INTERFACE: name = InformationServiceId.JAVA; break;
		default: name = "???";
		}
		return name + ":" + namespace + ":" + interfaceName + "=" + implementationName;
	}

	public static ServiceImplementationId fromName(String name) {
		String[] splitName = name.split("[:=]");

		if (splitName.length == 4) {
			// Namespace
			ServiceIdentifierType type;
			if (InformationServiceId.WS.equals(splitName[0])) {
				type = ServiceIdentifierType.WEB_SERVICE;
			}
			else if (InformationServiceId.JAVA.equals(splitName[0])) {
				type = ServiceIdentifierType.JAVA_INTERFACE;
			}
			else {
				type = ServiceIdentifierType.UNKNOWN;
			}
			
			return new ServiceImplementationId(type, splitName[1], splitName[2], splitName[3]);
		}
		else {
			return null;
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
