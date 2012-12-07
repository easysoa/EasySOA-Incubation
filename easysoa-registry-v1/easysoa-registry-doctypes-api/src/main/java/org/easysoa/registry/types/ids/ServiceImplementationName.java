package org.easysoa.registry.types.ids;


public class ServiceImplementationName {
	
	private String fullName;
	
	private ServiceNameType type;

	private String namespace;

	private String interfaceName;

	private String implementationName;

	public ServiceImplementationName(ServiceNameType type, String namespace,
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
	
	public static ServiceImplementationName fromName(String name) {
		String[] splitName = name.split("[:=]");
		
		if (splitName.length == 4) {
			// Namespace
			ServiceNameType type;
			if (InformationServiceName.WS.equals(splitName[0])) {
				type = ServiceNameType.WEB_SERVICE;
			}
			else if (InformationServiceName.JAVA.equals(splitName[0])) {
				type = ServiceNameType.JAVA_INTERFACE;
			}
			else {
				type = ServiceNameType.UNKNOWN;
			}
			
			return new ServiceImplementationName(type, splitName[1], splitName[2], splitName[3]);
		}
		else {
			return null;
		}
	}
	
	public ServiceNameType getType() {
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
