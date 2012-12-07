package org.easysoa.registry.types.ids;


public class InformationServiceName {

	protected static final String JAVA = "java";
	
	protected static final String WS = "ws";

	private String fullName;
	
	private ServiceNameType type;

	private String namespace;

	private String interfaceName;

	public InformationServiceName(ServiceNameType type, String namespace,
			String interfaceName) {
		switch (type) {
		case WEB_SERVICE: this.fullName = WS; break;
		case JAVA_INTERFACE: this.fullName = JAVA; break;
		default: this.fullName = "???";
		}
		this.fullName += ":" + namespace + ":" + interfaceName;
		this.type = type;
		this.namespace = namespace;
		this.interfaceName = interfaceName;
	}
	
	public static InformationServiceName fromName(String name) {
		String[] splitName = name.split(":");
		
		if (splitName.length == 3) {
			// Namespace
			ServiceNameType type;
			if (WS.equals(splitName[0])) {
				type = ServiceNameType.WEB_SERVICE;
			}
			else if (JAVA.equals(splitName[0])) {
				type = ServiceNameType.JAVA_INTERFACE;
			}
			else {
				type = ServiceNameType.UNKNOWN;
			}
			
			return new InformationServiceName(type, splitName[1], splitName[2]);
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
	
	@Override
	public String toString() {
		return this.fullName;
	}
	
	@Override
	public int hashCode() {
		return this.fullName.hashCode();
	}
	
}
