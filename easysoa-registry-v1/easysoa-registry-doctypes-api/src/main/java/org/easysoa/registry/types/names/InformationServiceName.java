package org.easysoa.registry.types.names;


public class InformationServiceName {

	protected static final String JAVA = "java";
	
	protected static final String WS = "ws";

	private String fullName;
	
	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	public InformationServiceName(ServiceIdentifierType type, String namespace,
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
	
	public InformationServiceName(String name) {
		this.fullName = name;
		String[] splitName = name.split(":");
		
		if (splitName.length == 3) {
			// Namespace
			if (WS.equals(splitName[0])) {
				this.type = ServiceIdentifierType.WEB_SERVICE;
			}
			else if (JAVA.equals(splitName[0])) {
				this.type = ServiceIdentifierType.JAVA_INTERFACE;
			}
			else {
				this.type = ServiceIdentifierType.UNKNOWN;
			}
			
			this.namespace = splitName[1];
			this.interfaceName = splitName[2];
		}
		else {
			this.type = ServiceIdentifierType.UNKNOWN;
			this.namespace = null;
			this.interfaceName = name;
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
