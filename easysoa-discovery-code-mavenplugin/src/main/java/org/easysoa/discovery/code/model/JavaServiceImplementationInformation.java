package org.easysoa.discovery.code.model;

import javax.xml.namespace.QName;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.client.types.ServiceImplementationInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationSoaNodeId;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceImplementationInformation extends ServiceImplementationInformation implements JavaServiceImplementation {

    public JavaServiceImplementationInformation(SoaNodeId deliverable, String implementationClass,
            String implementedInterface, String implementedInterfaceLocation) {
        this(new ServiceImplementationSoaNodeId(ServiceIdentifierType.JAVA_INTERFACE,
        		deliverable.getName(), implementedInterface, implementationClass).toString());
        this.properties.put(XPATH_IMPLEMENTATIONCLASS, implementationClass);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACE, implementedInterface);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACELOCATION, implementedInterfaceLocation);
    }
    
    public JavaServiceImplementationInformation(String namespace, String name, String servicename) {
    	this(new ServiceImplementationSoaNodeId(ServiceIdentifierType.WEB_SERVICE, namespace, name, servicename).toString());
        this.properties.put(XPATH_WSDL_PORTTYPE_NAME, new QName(namespace, name).toString());
        this.properties.put(XPATH_WSDL_SERVICE_NAME, new QName(namespace, servicename).toString());
    }
    
    public JavaServiceImplementationInformation(String soaname) {
        super(soaname);
        setDoctype(JavaServiceImplementation.DOCTYPE);
    }
    
    
    public static JavaServiceImplementationInformation create(SoaNodeInformation soaNodeInfo) {
        // TODO Convertor service, or anything cleaner that this?
        JavaServiceImplementationInformation result = new JavaServiceImplementationInformation(
                soaNodeInfo.getSoaNodeId().getName());
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
