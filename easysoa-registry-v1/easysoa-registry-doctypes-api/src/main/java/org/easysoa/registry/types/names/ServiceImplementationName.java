package org.easysoa.registry.types.names;


public class ServiceImplementationName {
	
	private String fullName;
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	private String implementationName;

	public ServiceImplementationName(ServiceIdentifierType type, String namespace,
			String interfaceName, String implementationName) {
		switch (type) {
		case WEB_SERVICE: this.fullName = InformationServiceName.WS; break;
		case JAVA_INTERFACE: this.fullName = InformationServiceName.JAVA; break;
		default: this.fullName = "???";
		}
		this.fullName += ":" + namespace + ":" + interfaceName + "=" + implementationName;
		this.type = type;
		this.namespace = namespace;
		this.interfaceName = interfaceName;
		this.implementationName = implementationName;
	}
	
	public ServiceImplementationName(String name) {
		this.fullName = name;
		String[] splitName = name.split("[:=]");
		
		// Namespace
		if (InformationServiceName.WS.equals(splitName[0])) {
			this.type = ServiceIdentifierType.WEB_SERVICE;
		}
		else if (InformationServiceName.JAVA.equals(splitName[0])) {
			this.type = ServiceIdentifierType.JAVA_INTERFACE;
		}
		else {
			this.type = ServiceIdentifierType.UNKNOWN;
		}
		
		this.namespace = splitName[1];
		this.interfaceName = splitName[2];
		this.implementationName = splitName[3];
	}
	
	public ServiceIdentifierType getType() {
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
		return this.fullName.replaceAll("=.*$", "");
	}
	
	@Override
	public String toString() {
		return this.fullName;
	}
	
	@Override
	public int hashCode() {
		return this.fullName.hashCode();
	}
	
}
