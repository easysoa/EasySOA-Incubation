package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;

public class SoaNodeInformationFactory {
    
    @SuppressWarnings("serial")
    private static Set<String> marshalledSchemaSet = new HashSet<String>() {{
        add("dublincore");

        //marshalledSchemaSet.add("subproject"); // TODO why not ?? because no subproject returned ?
        add("spnode"); // TODO or directly serialized in SoaNodeId ??
        add("istr");
        add("environment");
        add("soanode"); // TODO or directly serialized in SoaNodeId ??
        //add("soaversionable"); // TODO why not ?? not used ??
        add("deliverable");
        add("deliverabletype");
        add("informationservice");
        add("serviceimpl");
        add("servicecons");
        add("javaserviceimplementation"); // TODO move to -java- project ??
        add("javaserviceconsumption"); // TODO move to -java- project ??
        add("endpoint");
        add("wsdlinfo");
        add("restinfo");
        add("resourcedownloadinfo"); // TODO test
        add("platform");
        add("architecturecomponent");
    }};

    @SuppressWarnings("serial")
    private static Set<String> skippedPropertySet = new HashSet<String>() {{
        add("files");
        add("content"); // else JsonMappingException no mapper for FileDescriptor ; value is org.nuxeo.ecm.core.storage.sql.coremodel.SQLBlob
    }};

    private SoaNodeInformationFactory() {
    }
    
    public static SoaNodeInformation create(
            CoreSession documentManager, DocumentModel model) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        // Properties
        HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
        Map<String, Object> schemaProperties;
        // TODO FIXME [SoaNodeInformationFactory] else (un)serialization problems for ex. file props but also others
        for (String schema : model.getSchemas()) {
            if (/*!"common".equals(schema)*/marshalledSchemaSet.contains(schema)) {
                schemaProperties = model.getProperties(schema);
                for (Entry<String, Object> entry : schemaProperties.entrySet()) {
                    String entryKey = entry.getKey();
                    if (!skippedPropertySet.contains(entryKey)
                            && entry.getValue() instanceof Serializable) {
                        properties.put(entry.getKey(), (Serializable) entry.getValue());
                    }
                }
            }
        }
        properties.put(NXQL.ECM_UUID, model.getId());

        // allowing for non SOA nodes ex. Subproject :
        if (!documentService.isSoaNode(documentManager, model.getType())) {
            return new SoaNodeInformation(null, properties, null);
        }
        
        // completing SOA node handling :
        
        // ID
        SoaNodeId id = documentService.createSoaNodeId(model);
        
        // Parent SoaNodes
        List<SoaNodeId> parentDocuments = new LinkedList<SoaNodeId>();
        String[] parentIds = (String[]) model.getPropertyValue(SoaNode.XPATH_PARENTSIDS);
        for (String parentId : parentIds) {
        	parentDocuments.add(SoaNodeId.fromString(parentId));
        }
        
        return new SoaNodeInformation(id, properties, parentDocuments);
    }
    
}
