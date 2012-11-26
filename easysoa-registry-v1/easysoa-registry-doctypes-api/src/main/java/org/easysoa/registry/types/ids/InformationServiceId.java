package org.easysoa.registry.types.ids;


public class InformationServiceId {

	protected static final String JAVA = "java";
	
	protected static final String WS = "ws";

	private String fullName;
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	public InformationServiceId(ServiceIdentifierType type, String namespace,
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
	
	public static InformationServiceId fromName(String name) {
		String[] splitName = name.split(":");
		
		if (splitName.length == 3) {
			// Namespace
			ServiceIdentifierType type;
			if (WS.equals(splitName[0])) {
				type = ServiceIdentifierType.WEB_SERVICE;
			}
			else if (JAVA.equals(splitName[0])) {
				type = ServiceIdentifierType.JAVA_INTERFACE;
			}
			else {
				type = ServiceIdentifierType.UNKNOWN;
			}
			
			return new InformationServiceId(type, splitName[1], splitName[2]);
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
	
	@Override
	public String toString() {
		return this.fullName;
	}
	
	@Override
	public int hashCode() {
		return this.fullName.hashCode();
	}
	
}
