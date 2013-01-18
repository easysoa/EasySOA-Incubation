package org.easysoa.registry.types.listeners;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.InformationServiceName;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

public class WSDLParsingListener implements EventListener {

    private static Logger logger = Logger.getLogger(WSDLParsingListener.class);
    
	@Override
	public void handleEvent(Event event) throws ClientException {
		// Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        CoreSession documentManager = documentContext.getCoreSession();
        boolean documentModified = false;
        
        if (!sourceDocument.hasSchema(SoaNode.SCHEMA)
        		|| sourceDocument.getPropertyValue(InformationService.XPATH_SOANAME) == null) {
			return;
        }
        
        // Extract metadata from soaname
        if (InformationService.DOCTYPE.equals(sourceDocument.getType())) {
        	if (sourceDocument.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME) == null) {
        		InformationServiceName parsedSoaName = InformationServiceName.fromName(
        				(String) sourceDocument.getPropertyValue(InformationService.XPATH_SOANAME)); 
            	if (parsedSoaName != null) { // not null only if in format ws/java:ns:name
	        		String portTypeName = "{" + parsedSoaName.getNamespace() + "}" + parsedSoaName.getInterfaceName();
	        		sourceDocument.setPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME, portTypeName);
	        		documentModified = true;
            	}
        	}
        }
        else if (ServiceImplementation.DOCTYPE.equals(sourceDocument.getType())) {
        	ServiceImplementationName parsedSoaName = ServiceImplementationName.fromName(
    				(String) sourceDocument.getPropertyValue(ServiceImplementation.XPATH_SOANAME));
        	if (parsedSoaName != null) {
	        	if (sourceDocument.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME) == null) {
	        		String portTypeName = "{" + parsedSoaName.getNamespace() + "}" + parsedSoaName.getInterfaceName();
	        		sourceDocument.setPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, portTypeName);
	        		documentModified = true;
	        	}
	        	if (sourceDocument.getPropertyValue(ServiceImplementation.XPATH_WSDL_SERVICE_NAME) == null) {
	        		String serviceName = "{" + parsedSoaName.getNamespace() + "}" + parsedSoaName.getImplementationName();
	        		sourceDocument.setPropertyValue(ServiceImplementation.XPATH_WSDL_SERVICE_NAME, serviceName);
	        		documentModified = true;
	        	}
        	}
        }

        if (InformationService.DOCTYPE.equals(sourceDocument.getType())
        		|| Endpoint.DOCTYPE.equals(sourceDocument.getType())) {
			try {
			    documentModified = extractMetadataFromWsdl(sourceDocument);
			} catch (Exception e) {
				logger.error("Failed to extract from WSDL", e);
				return;
			}
        }
		
		// Save according to event type
		if (documentModified && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
			documentManager.saveDocument(sourceDocument);
			documentManager.save();
		}
	}

    private boolean extractMetadataFromWsdl(DocumentModel sourceDocument) throws Exception {
        // Look for first WSDL
        Description wsdl = null;
        
        // look in attached files
        List<?> filesInfos = (List<?>) sourceDocument.getPropertyValue("files:files");
        if (filesInfos != null && !filesInfos.isEmpty()) {
            for (Object fileInfoObject : filesInfos) {
                Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                Blob fileBlob = (Blob) fileInfoMap.get("file");
                wsdl = tryParsingWsdlFromBlob(fileBlob);
            }
        }
        
        if (wsdl == null) {
            // look in document content
            Blob fileBlob = (Blob) sourceDocument.getPropertyValue("file:content");
            if (fileBlob != null) {
                wsdl = tryParsingWsdlFromBlob(fileBlob);
            }
        }
        
        // Extract relevant metadata
        if (wsdl != null) {
            QName portTypeName = wsdl.getInterfaces().get(0).getQName();
            QName serviceName = wsdl.getServices().get(0).getQName();
            String wsdlVersion = wsdl.getVersion().name();
            boolean changed = false;
            if (InformationService.DOCTYPE.equals(sourceDocument.getType())) {
                if (false) { // TODO [soaname change]
                sourceDocument.setPropertyValue(InformationService.XPATH_SOANAME,
                        new InformationServiceName(ServiceNameType.WEB_SERVICE,
                                portTypeName.getNamespaceURI(), portTypeName.getLocalPart()).toString());
                }
                changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_PORTTYPE_NAME, portTypeName.toString()) || changed;
                changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_SERVICE_NAME, serviceName.toString()) || changed;
                changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_VERSION, wsdlVersion) || changed;
            }
            else {
                changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_PORTTYPE_NAME, portTypeName.toString()) || changed;
                changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_SERVICE_NAME, serviceName.toString()) || changed;
                changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_VERSION, wsdlVersion) || changed;
            }

            return changed;
        }
        return false;
    }

    private boolean setIfChanged(DocumentModel doc, String prop, Serializable newValue)
            throws PropertyException, ClientException {
        Serializable oldValue = doc.getPropertyValue(prop);
        if (newValue == null && oldValue != null
                || newValue != null && !newValue.equals(oldValue)) {
            doc.setPropertyValue(prop, newValue);
            return true;
        }
        return false;
    }

    private Description tryParsingWsdlFromBlob(Blob fileBlob) {
        if (fileBlob.getFilename().toLowerCase().endsWith("wsdl")) {
            try {
                File file = File.createTempFile("wsdlCandidate", null);
                fileBlob.transferTo(file);
                WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
                
                try {
                    return wsdlReader.read(file.toURI().toURL());
                }
                catch (WSDLException e) {
                    logger.info("Failed to extract or parse potential WSDL", e);
                    // Not a WSDL, continue to next file
                }
            } catch (Exception e) {
                logger.error("Failed to extract or parse potential WSDL", e);
            }
        }
        return null;
    }

}
