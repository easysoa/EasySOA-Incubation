package org.easysoa.registry.types.listeners;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.easysoa.registry.dbb.ProbeConfUtil;
import org.easysoa.registry.dbb.ResourceDownloadService;
import org.easysoa.registry.dbb.ResourceUpdateService;
import org.easysoa.registry.dbb.SynchronousResourceUpdateServiceImpl;
import org.easysoa.registry.facets.InformationServiceDataFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaModelSerializationUtil;
import org.easysoa.registry.types.ids.InformationServiceName;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.wsdl.WsdlBlob;
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
import org.nuxeo.runtime.api.Framework;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfParam;


/**
 * Extracts metadata from WSDL if changed.
 * 
 * Should be registered on events : documentCreated, documentModified
 * (and not beforeDocumentModification because digest not yet computed at storage level)
 * 
 * About implementation architecture :
 * see Thumbnail https://github.com/ldoguin/nuxeo-thumbnail http://www.nuxeo.com/blog/development/2012/06/monday-dev-heaven-adding-thumbnail-preview-document-content-nuxeo/
 * 
 * TODO : refactor in a WsdlService, use a given field for wsdl file
 * 
 * @author mdutoo
 *
 */
public class WSDLParsingListener implements EventListener {

    public static final String SOAP_CONTENT_TYPE = "application/soap+xml"; // ; charset=utf-8 http://www.w3schools.com/soap/soap_httpbinding.asp
    
    private static Logger logger = Logger.getLogger(WSDLParsingListener.class);
    
    private HashSet<String> wsdlDocumentTypes;
    private HashSet<String> wsdlFileDocumentTypes;
    
    public WSDLParsingListener() {
        wsdlFileDocumentTypes = new HashSet<String>(3);
        wsdlFileDocumentTypes.add(InformationService.DOCTYPE);
        wsdlFileDocumentTypes.add(Endpoint.DOCTYPE);

        wsdlDocumentTypes = new HashSet<String>(3);
        wsdlDocumentTypes.addAll(wsdlFileDocumentTypes);
        wsdlDocumentTypes.add(ServiceImplementation.DOCTYPE);
    }
    
    public boolean isWsdlDocumentType(String documentType) {
        return wsdlDocumentTypes.contains(documentType);
    }
    
    
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
        
        //if (DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()) && !sourceDocument.isDirty()) {
        //    return;
        //}
        
        //if (!sourceDocument.hasSchema(SoaNode.SCHEMA)
        //		|| sourceDocument.getPropertyValue(InformationService.XPATH_SOANAME) == null) {
		//	return;
        //}
        
        if (!isWsdlDocumentType(sourceDocument.getType())) {
            return;
        }

        // NB. available if beforeDocumentModification event
        DocumentModel previousDocumentModel = (DocumentModel) context.getProperty("previousDocumentModel");
        
        if (sourceDocument.getFacets().contains(ResourceDownloadInfo.FACET_RESOURCEDOWNLOADINFO)) { // TODO extract to isResourceDocument()
        
            String probeType = "";//TODO
            String probeInstanceId = "";//TODO
            
            // exit if document change but probe conf says it triggers using dedicated event
            if (ProbeConfUtil.isResourceProbeEventCustom(probeType, probeInstanceId)) {
                return;
            }
            
            ResourceDownloadService probeResourceDownloadService = ProbeConfUtil.getResourceDownloadService(probeType, probeInstanceId);
        
            // Starting update :
            try {
                ResourceUpdateService resourceUpdateService = Framework.getService(ResourceUpdateService.class);
                resourceUpdateService.updateResource(sourceDocument, previousDocumentModel,
                        sourceDocument, probeResourceDownloadService);
            }
            catch(Exception ex){
                logger.error("Error during the update", ex);
                throw new ClientException("Error during the update", ex);
            }
        }
        
        // TODO test it
        // TODO copy this file to ResourceListener & replace it in *listener.xml conf,
        // remove the following code from it, hook WsdlParsingListener rather on resourceDownloaded event in xml conf,
        // trigger resourceDownloaded event in ResourceUpdateService impl
        
        boolean documentModified = false;
        
        // getting old info :
        String oldWsdlFileName = (String) sourceDocument.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
        
        // getting new info :
        WsdlBlob wsdlBlob = new WsdlBlob(sourceDocument, previousDocumentModel);
        if (!wsdlBlob.hasWsdlFileChangedIfAny()) {
            return;
        }

        //WsdlBlob wsdlBlob = getWsdlBlob(sourceDocument);
        if (wsdlBlob.getWsdl() != null) {
            // update filename if required
            if (wsdlBlob.hasWsdlFileNameChanged()) {
                sourceDocument.setPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME, wsdlBlob.getBlob().getFilename());
                documentModified = true;
            }

            if (wsdlBlob.hasWsdlContentChanged()) { // checking in case just filename changed
                documentModified = extractMetadataFromWsdl(wsdlBlob.getWsdl(), sourceDocument);   
                //sourceDocument.setPropertyValue("wsdl:wsdlFileDigest", newDigest);
            }
            
        } else {
            if (oldWsdlFileName != null) {
                // reinit all extracted metadata (or remove facet)
                documentModified = setWsdlMetadataIfChanged(sourceDocument, null, null, null, null);
                sourceDocument.setPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME, null);
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

        }
		
		// Save according to event type
		if (documentModified && !DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
			documentManager.saveDocument(sourceDocument);
			documentManager.save();
		} // else DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()), therefore save will be automatically done http://doc.nuxeo.com/display/NXDOC/Events+and+Listeners
	}

    /**
     * @obsolete rather written inline in WsdlBlob (or listener), but shows how it is meant to work
     * @param sourceDocument
     * @return
     * @throws ClientException
     */
    /*private static WsdlBlob getWsdlBlob(DocumentModel sourceDocument) throws ClientException {
        Blob blob = null;
        String oldWsdlFileName = (String) sourceDocument.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
        if (oldWsdlFileName != null) {
            blob = getBlob(sourceDocument, oldWsdlFileName);
        }
        WsdlBlob wsdlBlob = null;
        if (blob != null) {
            wsdlBlob = tryParsingWsdlFromBlob(blob);
        }
        if (wsdlBlob == null) {
            wsdlBlob = findWsdlBlob(sourceDocument);   
        }
        return wsdlBlob;
    }*/
	
	/**
	 * TODO LATER maybe rather sourceDocument.getAdapter(BlobHolder.class) but doesn't handle file:files
	 * 
	 * @param sourceDocument
	 * @param fileName file name of the blob to be returned, must no be null
	 * @return
	 * @throws ClientException
	 */
    public static Blob getBlob(DocumentModel sourceDocument, String fileName) throws ClientException {
        // Look for first WSDL
        Blob fileBlob = null;
        Description wsdl = null;
        
        // look in attached files
        List<?> filesInfos = (List<?>) sourceDocument.getPropertyValue("files:files");
        if (filesInfos != null && !filesInfos.isEmpty()) {
            for (Object fileInfoObject : filesInfos) {
                Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                fileBlob = (Blob) fileInfoMap.get("file");
                if (fileName.equals(fileBlob.getFilename())) {
                    return fileBlob;
                }
            }
        }
        
        if (wsdl == null) {
            // look in document content
            fileBlob = (Blob) sourceDocument.getPropertyValue("file:content");
            if (fileBlob != null && fileName.equals(fileBlob.getFilename())) {
                return fileBlob;
            }
        }
        return null;
    }
    
    /**
     * Extracts relevant metadata
     * @param wsdl
     * @param sourceDocument
     * @return
     * @throws ClientException
     */
    private static boolean extractMetadataFromWsdl(Description wsdl,
            DocumentModel sourceDocument) throws ClientException {
        if (wsdl != null) {
            // for now, only taking the first Interface & Service of the WSDL.
            // TODO LATER develop WSDL import that creates as many services as there are in the WSDL
            // and / or allow to select the right one
            //String wsdlDoc = wsdl.getDocumentation().getContent();//TODO ?!?
            QName portTypeName = wsdl.getInterfaces().get(0).getQName();
            QName serviceName = wsdl.getServices().get(0).getQName(); // TODO better : make sure is is of the interface above
            String wsdlVersion = wsdl.getVersion().name();

            List<OperationInformation> operationInfos = new ArrayList<OperationInformation>(); 
            for (Operation wsdlOperation : wsdl.getInterfaces().get(0).getOperations()) {
                StringBuffer docBuf = new StringBuffer();
                if (wsdlOperation.getDocumentation() != null) {
                    docBuf.append("\n");
                    docBuf.append(wsdlOperation.getDocumentation().getContent());
                }
                
                String inParameters = getParameters(wsdlOperation.getInput(), docBuf);
                String outParameters = getParameters(wsdlOperation.getOutput(), docBuf); 
                //TODO LATER also error message
                
                OperationInformation operationInfo = new OperationInformation(
                        wsdlOperation.getQName().getLocalPart(),//TODO LATER rather toString() and only display localPart
                        inParameters, outParameters,
                        docBuf.toString(), SOAP_CONTENT_TYPE, SOAP_CONTENT_TYPE);
                operationInfos.add(operationInfo);
            }
            
            boolean changed = setWsdlMetadataIfChanged(sourceDocument, wsdlVersion, portTypeName, serviceName, operationInfos);
            return changed;
        }
        return false;
    }

    /**
     * 
     * @param itfParam input, output or fault
     * @return
     */
    private static String getParameters(AbsItfParam itfParam, StringBuffer docBuf) {
        if (itfParam != null) {
            if (!itfParam.getParts().isEmpty()) {
                Part part = itfParam.getParts().get(0); //TODO LATER multipart
                if (part != null) {
                    if (part.getElement() != null) {
                        Element element = part.getElement();
                        if (element.getDocumentation() != null) {
                            docBuf.append("\n");
                            docBuf.append(element.getDocumentation().getContent());
                        }
                        if (element.getType().getDocumentation() != null) {
                            docBuf.append("\n");
                            docBuf.append(element.getType().getDocumentation().getContent());
                        }
                        //TODO also of all inner elements...
                        // NB. inputElement and not Type, because if anonymous Type its QName will be null
                        return element.getQName().getLocalPart(); //TODO LATER rather toString() and only display localPart;
                    }
                }
            } // else no part, seen in ContactSvc.asmx.wsdl :
//    <wsdl:operation name="getRepartitionTypeStructure">
//      <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Get repartition TypeStructure</wsdl:documentation>
//      <wsdl:input message="" />
//      <wsdl:output message="tns:getRepartitionTypeStructureSoapOut" />
//    </wsdl:operation>
        }
        return "";
    }

    private static boolean setWsdlMetadataIfChanged(DocumentModel sourceDocument,
            String wsdlVersion, QName portTypeName, QName serviceName,
            List<OperationInformation> operationInfos) throws ClientException {
        boolean changed = false;
        if (InformationService.DOCTYPE.equals(sourceDocument.getType())) {
            if (false) { // TODO [soaname change] ?!?
                sourceDocument.setPropertyValue(InformationService.XPATH_SOANAME,
                        new InformationServiceName(ServiceNameType.WEB_SERVICE,
                                portTypeName.getNamespaceURI(), portTypeName.getLocalPart()).toString());
                changed = true;
            }
            changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_SERVICE_NAME, toStringIfNotNull(serviceName)) || changed;
            changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_VERSION, wsdlVersion) || changed;
            changed = setIfChanged(sourceDocument, InformationService.XPATH_WSDL_PORTTYPE_NAME, toStringIfNotNull(portTypeName)) || changed;
            changed = setIfChanged(sourceDocument, InformationServiceDataFacet.XPATH_OPERATIONS,
                    SoaModelSerializationUtil.operationInformationToPropertyValue(operationInfos)) || changed;
        }
        else { // Endpoint.DOCTYPE.equals(sourceDocument.getType())
            // NB. for now WSDL metas of all model elements (InformationService, (ServiceImplementation), Enndpoint) are the same
            changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_PORTTYPE_NAME, toStringIfNotNull(portTypeName)) || changed;
            changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_SERVICE_NAME, toStringIfNotNull(serviceName)) || changed;
            changed = setIfChanged(sourceDocument, Endpoint.XPATH_WSDL_VERSION, wsdlVersion) || changed;
            //changed = setIfChanged(sourceDocument, "iserv:operations", operationInfos) || changed; //TODO LATER also operations on Endpoint ??
        }
        return changed;
    }

    private static Serializable toStringIfNotNull(Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    private static boolean setIfChanged(DocumentModel doc, String prop, Serializable newValue)
            throws PropertyException, ClientException {
        Serializable oldValue = doc.getPropertyValue(prop);
        if (newValue == null && oldValue != null
                || newValue != null && !newValue.equals(oldValue)) {
            doc.setPropertyValue(prop, newValue);
            return true;
        }
        return false;
    }

    public static Description tryParsingWsdlFromBlob(Blob blob) {
        if (blob.getFilename().toLowerCase().endsWith("wsdl")) {
            File file = null;
            try {
                file = File.createTempFile("wsdlCandidate", null);
                blob.transferTo(file);
                WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
                
                try {
                    Description wsdl = wsdlReader.read(file.toURI().toURL());
                    return wsdl;
                }
                catch (WSDLException e) {
                    logger.info("Failed to extract or parse potential WSDL", e);
                    // Not a WSDL, continue to next file
                }
            } catch (Exception e) {
                logger.error("Failed to extract or parse potential WSDL", e);
            } finally {
                if (file != null) {
                    boolean delete = file.delete();
                    if (!delete) {
                        logger.warn("Unable to delete temp WSDL file " + file.getAbsolutePath());
                    }
                }
            }
        }
        return null;
    }

}
