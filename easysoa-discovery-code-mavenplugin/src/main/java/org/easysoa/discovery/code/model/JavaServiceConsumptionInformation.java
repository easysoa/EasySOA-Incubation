package org.easysoa.discovery.code.model;

import java.util.List;

import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceConsumptionInformation extends SoaNodeInformation implements JavaServiceConsumption {

    public JavaServiceConsumptionInformation(String fromClass,
            JavaServiceInterfaceInformation javaSCInformation, boolean isTest) {
        super(new SoaNodeId(javaSCInformation.getMavenDeliverableId().getSubprojectId(), JavaServiceConsumption.DOCTYPE, 
        		javaSCInformation.getMavenDeliverableId().getName() + ":" + fromClass + ">"
        		+ javaSCInformation.getInterfaceName() + ":" + javaSCInformation.getWsPortTypeName()), null, null);
        SoaNodeId fromDeliverable = javaSCInformation.getMavenDeliverableId();
        String interfaceLocation = fromDeliverable.getName();
        this.properties.put(XPATH_CONSUMERCLASS, fromClass);
        this.properties.put(XPATH_CONSUMEDINTERFACE, javaSCInformation.getInterfaceName());
        this.properties.put(XPATH_WSDL_PORTTYPE_NAME, javaSCInformation.getWsPortTypeName());
        this.properties.put(XPATH_CONSUMEDINTERFACELOCATION, interfaceLocation);
        this.properties.put(XPATH_ISTEST, isTest);
		this.parentDocuments.add(fromDeliverable );

		// (TODO Cleaner porttype/servicename discovery + revert soanodeid)
        this.properties.put(XPATH_WSDL_PORTTYPE_NAME, javaSCInformation.getWsPortTypeName());
    }
    
    @Override
    public String getConsumedInterface() throws Exception {
        return (String) properties.get(JavaServiceConsumption.XPATH_CONSUMEDINTERFACE);
    }

    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConsumerClass() throws Exception {
        return (String) properties.get(JavaServiceConsumption.XPATH_CONSUMERCLASS);
    }

    @Override
    public String getConsumedInterfaceLocation() throws Exception {
        return (String) properties.get(JavaServiceConsumption.XPATH_CONSUMEDINTERFACELOCATION);
    }

    @Override
    public boolean getIsTest() throws Exception {
        return (Boolean) properties.get(JavaServiceConsumption.XPATH_ISTEST);
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
