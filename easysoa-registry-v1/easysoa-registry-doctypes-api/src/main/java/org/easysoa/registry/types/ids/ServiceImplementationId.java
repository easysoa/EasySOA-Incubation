package org.easysoa.registry.types.ids;


public class ServiceImplementationId {
	
	private String fullName;
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	private String implementationName;

	public ServiceImplementationId(ServiceIdentifierType type, String namespace,
			String interfaceName, String implementationName) {
		switch (type) {
		case WEB_SERVICE: this.fullName = InformationServiceId.WS; break;
		case JAVA_INTERFACE: this.fullName = InformationServiceId.JAVA; break;
		default: this.fullName = "???";
		}
		this.fullName += ":" + namespace + ":" + interfaceName + "=" + implementationName;
		this.type = type;
		this.namespace = namespace;
		this.interfaceName = interfaceName;
		this.implementationName = implementationName;
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
