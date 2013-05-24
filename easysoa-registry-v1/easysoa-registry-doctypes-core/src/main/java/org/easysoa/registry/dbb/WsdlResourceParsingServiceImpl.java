/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.dbb;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
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
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfParam;

/**
 * Extracts and sets metadata from WSDL. Tries to do it only if (doc, file name, content) actually changed.
 * 
 * NB. if called on event, can't be on beforeDocumentModification because digest not yet computed at storage level.
 * 
 * About WSDL parsing implementation architecture :
 * see Thumbnail https://github.com/ldoguin/nuxeo-thumbnail http://www.nuxeo.com/blog/development/2012/06/monday-dev-heaven-adding-thumbnail-preview-document-content-nuxeo/
 * 
 * @author jguillemotte
 */
public class WsdlResourceParsingServiceImpl implements ResourceParsingService {

    private static Logger logger = Logger.getLogger(WsdlResourceParsingServiceImpl.class);

    public static final String SOAP_CONTENT_TYPE = "application/soap+xml"; // ; charset=utf-8 http://www.w3schools.com/soap/soap_httpbinding.asp    
    
    private HashSet<String> wsdlDocumentTypes;
    private HashSet<String> wsdlFileDocumentTypes;
    
    public WsdlResourceParsingServiceImpl(){
        wsdlFileDocumentTypes = new HashSet<String>(3);
        wsdlFileDocumentTypes.add(InformationService.DOCTYPE);
        wsdlFileDocumentTypes.add(Endpoint.DOCTYPE);

        wsdlDocumentTypes = new HashSet<String>(3);
        wsdlDocumentTypes.addAll(wsdlFileDocumentTypes);
        wsdlDocumentTypes.add(ServiceImplementation.DOCTYPE);        
    }
    
    /**
     * 
     * @param documentType
     * @return 
     */
    @Override
    public boolean isWsdlInfo(DocumentModel model) { // TODO isWsdlInfo()
        return wsdlDocumentTypes.contains(model.getType()); // TODO getFacets().contains...
        /*Set<String> facets = model.getFacets();
        return facets.contains(InformationService.DOCTYPE) 
                || facets.contains(Endpoint.DOCTYPE)
                || facets.contains(ServiceImplementation.DOCTYPE);*/
    }
    
    /**
     * 
     * @param model
     * @return 
     */
    @Override
    public boolean isWsdlFileResource(DocumentModel model){
        /*if((isRDI(model) && isWsdl(model)) || isWsdlHardType(model)){
            return true;
        }
        return false;*/
        return wsdlFileDocumentTypes.contains(model.getType());
        // isRDI && isWsdl(i.e. isWsdlResource(from type or name of file and / or url) || isWsdlHardType i.e. iserv && endpoint)
    }
    
    /**
     * 
     * @param model
     * @return 
     */
    private boolean isRDI(DocumentModel model){
        return model.getFacets().contains(ResourceDownloadInfo.FACET_RESOURCEDOWNLOADINFO);
    }
    
    /**
     * 
     * @param model
     * @return 
     */
    private boolean isWsdl(DocumentModel model){
        // get the file
        // or get the url
        try {
            String url = (String)model.getPropertyValue(ResourceDownloadInfo.XPATH_URL);
            Blob blob = (Blob)model.getPropertyValue("file:content");
            // Check not null
            if(blob == null){
                return false;
            }
            if(url == null || url.length() == 0){
                return false;
            }
            // Check wsdl
            if(blob.getFilename().toLowerCase().endsWith(".wsdl") || url.toLowerCase().contains("?wsdl")){
                return true;
            }
            return false;
        }
        catch(Exception ex){

        }
        return false;        
    }
    
    /**
     * 
     * @param model
     * @return 
     */
    private boolean isWsdlHardType(DocumentModel model){
        Set<String> facets = model.getFacets();
        return facets.contains(InformationService.DOCTYPE) 
                || facets.contains(Endpoint.DOCTYPE);        
    }
    
    /**
     * 
     * @param sourceDocument
     * @return
     * @throws ClientException 
     */
    @Override
    public boolean extractMetas(DocumentModel sourceDocument) throws ClientException {
        boolean documentModified = false;
        
        // getting new info :
        WsdlBlob wsdlBlob = new WsdlBlob(sourceDocument); // NB. previousDocumentModel can't be used to get old digest
        if (wsdlBlob.hasFileChangedIfAny()) {
            
            if (wsdlBlob.getWsdl() != null) {
                // try extracting from parsing
                documentModified = parseResourceAndSetMetas(wsdlBlob);

            } else {
                // If none anymore, reinit all extracted metadata (or remove facet)
                String oldWsdlFileName = (String) sourceDocument.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
                if (oldWsdlFileName != null) {
                    documentModified = resetMetasIfChanged(sourceDocument);
                }

                // Try extracting metadata from soaname
                if (isWsdlInfo(sourceDocument)) { // NB. additionally also if serviceimpl
                    documentModified = extractMetasFromSoaName(sourceDocument) || documentModified;
                }

            }
        }
        return documentModified;
    }
    
    /**
     * TODO in AbstractResourceService ?!
     * @param wsdlBlob
     * @return
     * @throws ClientException 
     */
    protected boolean parseResourceAndSetMetas(WsdlBlob wsdlBlob) throws ClientException {
        boolean documentModified = false;
        // update filename if required
        if (wsdlBlob.hasFileNameChanged()) {
            wsdlBlob.getSourceDocument().setPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME, wsdlBlob.getBlob().getFilename());
            documentModified = true;
        }

        if (wsdlBlob.hasContentChanged()) { // checking in case just filename changed
            documentModified = extractMetadataFromWsdl(wsdlBlob.getWsdl(), wsdlBlob.getSourceDocument()) || documentModified;
            //sourceDocument.setPropertyValue("wsdl:wsdlFileDigest", newDigest);
        }
        return documentModified;
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

    /**
     * TODO in AbstractResourceService ?!
     * @param sourceDocument
     * @return
     * @throws ClientException 
     */
    protected boolean resetMetasIfChanged(DocumentModel sourceDocument) throws ClientException {
        boolean changed = setWsdlMetadataIfChanged(sourceDocument, null, null, null, null);
        changed = setIfChanged(sourceDocument, WsdlInfoFacet.XPATH_WSDL_FILE_NAME, null) || changed;
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

    /**
     * TODO in AbstractResourceService ?!
     * @param sourceDocument
     * @return
     * @throws ClientException 
     */
    private boolean extractMetasFromSoaName(DocumentModel sourceDocument) throws ClientException {
        boolean documentModified = false;
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
        // NB. not done for endpoint whose SOA name is always "environment:url"
        return documentModified;
    }

}
