package org.easysoa.registry.types.ids;


public class ServiceImplementationName {
	
	private String fullName;
	
	private ServiceNameType type;

	/** {ns}name for WS, path for REST... */
	private String interfaceName;

	/** fully qualified impl name as known */
	private String implementationName;

	public ServiceImplementationName(ServiceNameType type,
			String interfaceName, String implementationName) {
		String typeString;
		switch (type) {
		case WEB_SERVICE: typeString = InformationServiceName.WS; break;
		case REST: typeString = InformationServiceName.REST; break;
		case JAVA_INTERFACE: typeString = InformationServiceName.JAVA; break;
		default: typeString = "???";
		}
		this.fullName = typeString + ":" + interfaceName + "=" + implementationName;
		this.type = type;
		this.interfaceName = interfaceName;
		this.implementationName = implementationName;
	}
	
	public static ServiceImplementationName fromName(String name) {
		String[] splitName = name.split("[:=]");
		
		if (splitName.length == 3) {
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
			
			return new ServiceImplementationName(type, splitName[1], splitName[2]);
		}
		else {
			return null;
		}
	}
	
	public ServiceNameType getType() {
		return type;
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
