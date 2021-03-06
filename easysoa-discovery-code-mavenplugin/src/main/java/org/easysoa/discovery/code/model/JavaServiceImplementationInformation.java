package org.easysoa.discovery.code.model;

import javax.xml.namespace.QName;

import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.client.types.ServiceImplementationInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceImplementationInformation extends ServiceImplementationInformation implements JavaServiceImplementation {

    /*
    // java impls should have at least those props
    public JavaServiceImplementationInformation(SoaNodeId deliverable, String implementationClass,
            String implementedInterface, String implementedInterfaceLocation) {
        this(deliverable.getSubprojectId(), new ServiceImplementationName(ServiceNameType.JAVA_INTERFACE,
        		deliverable.getName(), implementedInterface, implementationClass).toString());
        this.properties.put(XPATH_IMPLEMENTATIONCLASS, implementationClass);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACE, implementedInterface);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACELOCATION, implementedInterfaceLocation);
    }
    
    // WS impls should have at least those props (and can also have java impl props if it's java)
    public JavaServiceImplementationInformation(String subprojectId,
            String namespace, String name, String servicename) {
    	this(subprojectId, new ServiceImplementationName(ServiceNameType.WEB_SERVICE, namespace, name, servicename).toString());
        this.properties.put(XPATH_WSDL_PORTTYPE_NAME, new QName(namespace, name).toString());
        this.properties.put(XPATH_WSDL_SERVICE_NAME, new QName(namespace, servicename).toString());
    }
    */
    
    public JavaServiceImplementationInformation(String subprojectId, String soaname) {
        super(subprojectId, JavaServiceImplementation.DOCTYPE, soaname);
    }
    
    public JavaServiceImplementationInformation(String subprojectId, ServiceImplementationName soaname) {
        super(subprojectId, JavaServiceImplementation.DOCTYPE, soaname.toString());
    }
    
    public JavaServiceImplementationInformation(SoaNodeId deliverableSoaId, ServiceImplementationName soaname) {
        super(deliverableSoaId.getSubprojectId(), JavaServiceImplementation.DOCTYPE, soaname.toString());
    }

	public static JavaServiceImplementationInformation create(SoaNodeInformation soaNodeInfo) {
        // TODO Convertor service, or anything cleaner that this?
        JavaServiceImplementationInformation result = new JavaServiceImplementationInformation(
                soaNodeInfo.getSoaNodeId().getSubprojectId(), soaNodeInfo.getSoaNodeId().getName());
        result.setProperties(soaNodeInfo.getProperties());
        result.setParentDocuments(soaNodeInfo.getParentDocuments());
        return result;
    }
    
    public SoaNodeId getDeliverable() {
        for (SoaNodeId parentDocument : parentDocuments) {
            if (Deliverable.DOCTYPE.equals(parentDocument)) {
                return parentDocument;
            }
        }
        return null;
    }

}
