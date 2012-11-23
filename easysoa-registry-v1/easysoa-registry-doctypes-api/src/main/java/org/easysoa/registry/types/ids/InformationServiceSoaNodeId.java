package org.easysoa.registry.types.ids;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.InformationService;


public class InformationServiceSoaNodeId extends SoaNodeId {

	protected static final String JAVA = "java";
	
	protected static final String WS = "ws";

	private ServiceIdentifierType type;

	private String namespace;

	private String interfaceName;

	public InformationServiceSoaNodeId(ServiceIdentifierType type, String namespace,
			String interfaceName) {
		super(InformationService.DOCTYPE, buildName(type, namespace, interfaceName));
		this.type = type;
		this.namespace = namespace;
		this.interfaceName = interfaceName;
	}

	public InformationServiceSoaNodeId(String name) {
		super(InformationService.DOCTYPE, name);
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

	private static String buildName(ServiceIdentifierType type, String namespace,
			String interfaceName) {
		String fullName;
		switch (type) {
		case WEB_SERVICE: fullName = WS; break;
		case JAVA_INTERFACE: fullName = JAVA; break;
		default: fullName = "???";
		}
		fullName += ":" + namespace + ":" + interfaceName;
		return fullName;
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
	
}
