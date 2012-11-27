package org.easysoa.registry.types.listeners;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.InformationServiceId;
import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationId;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
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
        		InformationServiceId parsedSoaName = InformationServiceId.fromName(
        				(String) sourceDocument.getPropertyValue(InformationService.XPATH_SOANAME));
            	if (parsedSoaName != null) {
	        		String portTypeName = "{" + parsedSoaName.getNamespace() + "}" + parsedSoaName.getInterfaceName();
	        		sourceDocument.setPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME, portTypeName);
	        		documentModified = true;
            	}
        	}
        }
        else if (ServiceImplementation.DOCTYPE.equals(sourceDocument.getType())) {
        	ServiceImplementationId parsedSoaName = ServiceImplementationId.fromName(
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
	        // Extract metadata from WSDL
	        Object filesInfoValue = sourceDocument.getPropertyValue("files:files");
			if (filesInfoValue != null) {
	
				try {
					// Look for first WSDL
					Description wsdl = null;
					List<?> filesInfoList = (List<?>) filesInfoValue;
					for (Object fileInfoObject : filesInfoList) {
						Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
						Blob fileBlob = (Blob) fileInfoMap.get("file");
						try {
							File file = File.createTempFile("wsdlCandidate", null);
							fileBlob.transferTo(file);
							WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
							
							try {
								wsdl = wsdlReader.read(file.toURI().toURL());
								break;
							}
							catch (WSDLException e) {
								// Not a WSDL, continue to next file
							}
						} catch (Exception e) {
							logger.error("Failed to extract or parse potential WSDL", e);
						}
					}
					
					// Extract relevant metadata
					if (wsdl != null) {
						QName portTypeName = wsdl.getInterfaces().get(0).getQName();
						QName serviceName = wsdl.getServices().get(0).getQName();
						String wsdlVersion = wsdl.getVersion().name();
						if (InformationService.DOCTYPE.equals(sourceDocument.getType())) {
							sourceDocument.setPropertyValue(InformationService.XPATH_SOANAME,
									new InformationServiceId(ServiceIdentifierType.WEB_SERVICE,
											portTypeName.getNamespaceURI(), portTypeName.getLocalPart()).toString());
							sourceDocument.setPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME, portTypeName.toString());
							sourceDocument.setPropertyValue(InformationService.XPATH_WSDL_SERVICE_NAME, serviceName.toString());
							sourceDocument.setPropertyValue(InformationService.XPATH_WSDL_VERSION, wsdlVersion);
						}
						else {
							sourceDocument.setPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME, portTypeName.toString());
							sourceDocument.setPropertyValue(Endpoint.XPATH_WSDL_SERVICE_NAME, serviceName.toString());
							sourceDocument.setPropertyValue(Endpoint.XPATH_WSDL_VERSION, wsdlVersion);
						}

						documentModified = true;
					}
					
				
				} catch (Exception e) {
					logger.error("Failed to parse WSDL", e);
					return;
				}
	        }
        }
		
		// Save according to event type
		if (documentModified && DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
			documentManager.saveDocument(sourceDocument);
			documentManager.save();
		}
	}

}
