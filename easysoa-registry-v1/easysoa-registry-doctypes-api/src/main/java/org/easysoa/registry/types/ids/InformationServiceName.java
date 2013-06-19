package org.easysoa.registry.types.ids;


public class InformationServiceName {

	protected static final String JAVA = "java";
	
	protected static final String WS = "WS";
	
	protected static final String REST = "REST";

	private String fullName;
	
	private ServiceNameType type;

	/** {ns}name for WS, path for REST... */
	private String interfaceName;

	public InformationServiceName(ServiceNameType type, String interfaceName) {
		String typeString;
		switch (type) {
		case WEB_SERVICE: typeString = InformationServiceName.WS; break;
		case REST: typeString = InformationServiceName.REST; break;
		case JAVA_INTERFACE: typeString = InformationServiceName.JAVA; break;
		default: typeString = "???";
		}
		this.fullName = typeString + ":" + interfaceName;
		this.type = type;
		this.interfaceName = interfaceName;
	}
	
	/**
	 * 
	 * @param name
	 * @return null if not in format ws/java:ns:name
	 */
	public static InformationServiceName fromName(String name) {
		String[] splitName = name.split(":");
		
		if (splitName.length == 2) {
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
			
			return new InformationServiceName(type, splitName[1]);
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
	
	@Override
	public String toString() {
		return this.fullName;
	}
	
	@Override
	public int hashCode() {
		return this.fullName.hashCode();
	}
	
}
