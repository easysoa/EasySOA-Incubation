/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.List;
import java.util.Map;

import org.easysoa.registry.matching.MatchingHelper;
import org.easysoa.registry.rest.integration.EndpointInformation;
import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.types.Document;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author jguillemotte
 *
 */
public class SoaNodeInformationToWSDLInformationMapper {

    /**
     * Transform a SoaNodeInformation object to a WSDLInformation object
     * @param nodeInformation
     * @return
     */
    public static ServiceInformation mapToServiceInformation(DocumentModel docModel, String nuxeoBaseUrl, CoreSession documentManager) throws Exception {
   
        ServiceInformation serviceInformation = new ServiceInformation();

        //mapCommonWsdlInformation(nodeModel, serviceInformation, nuxeoBaseUrl);
        // Filling object with document properties
        serviceInformation.setSoaName((String)docModel.getPropertyValue("soan:name"));
        serviceInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        serviceInformation.setName((String)docModel.getPropertyValue("dc:title"));
        serviceInformation.setDescription((String)docModel.getPropertyValue("dc:description"));
        serviceInformation.setNuxeoID(docModel.getId()); // the Nuxeo object ID
        serviceInformation.setObjectType(docModel.getType());

        if (MatchingHelper.isWsdlInfo(docModel)) {
            serviceInformation.setWsdlPortType((String)docModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
            serviceInformation.setWsdlServiceName((String)docModel.getPropertyValue(InformationService.XPATH_WSDL_SERVICE_NAME));
        } else if (MatchingHelper.isRestInfo(docModel)) {
            serviceInformation.setRestPath((String)docModel.getPropertyValue(InformationService.XPATH_REST_PATH));
            serviceInformation.setRestAccepts((String)docModel.getPropertyValue(InformationService.XPATH_REST_ACCEPTS));
            serviceInformation.setRestContentType((String)docModel.getPropertyValue(InformationService.XPATH_REST_CONTENT_TYPE));
        }
        
        // WSDL download URL : To be builded by hand
        // http://localhost:8080/nuxeo/nxfile/default/d5bd2a85-a936-4319-8cf9-6e4cd5fd0381/files:files/0/file/WeatherService.wsdl
        // TODO :if there is no attached WSDL file, no wsdl url is returned
        serviceInformation.setWsdlDownloadUrl("");
        String nuxeoFilePath = "";
        try {
            Blob fileBlob = null;
            // look in attached files
            List<?> filesInfos = (List<?>) docModel.getPropertyValue("files:files");
            if (filesInfos != null && !filesInfos.isEmpty()) {
                for (Object fileInfoObject : filesInfos) {
                    Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                    fileBlob = (Blob) fileInfoMap.get("file");
                    nuxeoFilePath = "files:files/0/file";
                }
            }
            
            if (fileBlob == null) {
                // look in document content
                fileBlob = (Blob) docModel.getPropertyValue("file:content");
                //nuxeoFilePath = "file:content";
                nuxeoFilePath = "blobholder:0";
            }            
            
            /*Blob blob = (Blob)nodeModel.getPropertyValue("files/0/file/");*/
            if (fileBlob != null) {
                serviceInformation.setWsdlDownloadUrl(buildWsdlDownloadUrl(nuxeoBaseUrl, docModel.getId(), nuxeoFilePath , fileBlob.getFilename()));
            } else {
                serviceInformation.setWsdlDownloadUrl("");
            }
        }
        catch(Exception ex){
            //serviceInformation.setWsdlDownloadUrl("");
            ex.printStackTrace();
        }         
        
        return serviceInformation;        
    }
    
    public static EndpointInformation mapToEndpointInformation(DocumentModel docModel, String nuxeoBaseUrl) throws Exception {
        EndpointInformation endpointInformation = new EndpointInformation();
        
        //mapCommonWsdlInformation(nodeModel, endpointInformation, nuxeoBaseUrl);
        // Filling object with document properties
        endpointInformation.setSoaName((String)docModel.getPropertyValue(SoaNode.XPATH_SOANAME));
        endpointInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        endpointInformation.setName((String)docModel.getPropertyValue(Document.XPATH_TITLE));
        // NB. WARNING name actually stores the dc:title
        // (which is not ecm:name, which is not soan:name from which it is deduced)
        // TODO LATER & also in FraSCAti Studio, refactor to getTitle() & add getName() for ecm:name
        endpointInformation.setDescription((String)docModel.getPropertyValue(Document.XPATH_DESCRIPTION));
        endpointInformation.setNuxeoID(docModel.getId()); // the Nuxeo object ID
        endpointInformation.setObjectType(docModel.getType());
        
        // WSDL download URL : To be builded by hand
        // http://localhost:8080/nuxeo/nxfile/default/d5bd2a85-a936-4319-8cf9-6e4cd5fd0381/files:files/0/file/WeatherService.wsdl
        endpointInformation.setWsdlDownloadUrl("");
        String nuxeoFilePath = "";
        try {
            Blob fileBlob = null;
            // look in attached files
            List<?> filesInfos = (List<?>) docModel.getPropertyValue("files:files");
            if (filesInfos != null && !filesInfos.isEmpty()) {
                for (Object fileInfoObject : filesInfos) {
                    Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                    fileBlob = (Blob) fileInfoMap.get("file");
                    nuxeoFilePath = "files:files/0/file";
                }
            }
            
            if (fileBlob == null) {
                // look in document content
                fileBlob = (Blob) docModel.getPropertyValue("file:content");
                //nuxeoFilePath = "file:content";
                nuxeoFilePath = "blobholder:0";
            }            
            
            /*Blob blob = (Blob)nodeModel.getPropertyValue("files/0/file/");*/
            if (fileBlob != null) {
                endpointInformation.setWsdlDownloadUrl(buildWsdlDownloadUrl(nuxeoBaseUrl, docModel.getId(), nuxeoFilePath , fileBlob.getFilename()));
            } else {
                endpointInformation.setWsdlDownloadUrl("");
            }
        }
        catch(Exception ex){
            endpointInformation.setWsdlDownloadUrl("");
            //ex.printStackTrace();
        }

        if (MatchingHelper.isWsdlInfo(docModel)) {
            endpointInformation.setWsdlPortType((String)docModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME));
            endpointInformation.setWsdlServiceName((String)docModel.getPropertyValue(InformationService.XPATH_WSDL_SERVICE_NAME));
        } else if (MatchingHelper.isRestInfo(docModel)) {
            endpointInformation.setRestPath((String)docModel.getPropertyValue(InformationService.XPATH_REST_PATH));
            endpointInformation.setRestAccepts((String)docModel.getPropertyValue(InformationService.XPATH_REST_ACCEPTS));
            endpointInformation.setRestContentType((String)docModel.getPropertyValue(InformationService.XPATH_REST_CONTENT_TYPE));
        }  
        
        if(Endpoint.DOCTYPE.equalsIgnoreCase(endpointInformation.getObjectType())){
            // Only for endpoint objects
            endpointInformation.setEndpointUrl((String)docModel.getPropertyValue("endp:url")); 
            endpointInformation.setEnvironment((String)docModel.getPropertyValue("env:environment"));
        }

        return endpointInformation;      
    }
    
    /*private static void mapCommonWsdlInformation(DocumentModel nodeModel, WSDLInformation wsdlInformation, String nuxeoBaseUrl) throws PropertyException, ClientException {
        // Filling object with document properties
        wsdlInformation.setSoaName((String)nodeModel.getPropertyValue("soan:name"));
        wsdlInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        wsdlInformation.setName((String)nodeModel.getPropertyValue("dc:title"));
        wsdlInformation.setDescription((String)nodeModel.getPropertyValue("dc:description"));
        wsdlInformation.setNuxeoID(nodeModel.getId()); // the Nuxeo object ID
        wsdlInformation.setObjectType(nodeModel.getType());
        
        // WSDL download URL : To be builded by hand
        // http://localhost:8080/nuxeo/nxfile/default/d5bd2a85-a936-4319-8cf9-6e4cd5fd0381/files:files/0/file/WeatherService.wsdl
        try {
            Blob blob = (Blob)nodeModel.getPropertyValue("files/0/file/");
            if (blob != null) {
                wsdlInformation.setWsdlDownloadUrl(buildWsdlDownloadUrl(nuxeoBaseUrl, nodeModel.getId(), blob.getFilename()));
            } else {
                wsdlInformation.setWsdlDownloadUrl("");
            }
        }
        catch(Exception ex){
            wsdlInformation.setWsdlDownloadUrl("");
            //ex.printStackTrace();
        }        
    }*/
    
        
    /**
     * Builds a WSDL download URL
     * @param nuxeoBaseUrl Nuxeo base address, eg : http://localhost:8080/nuxeo/
     * @param objectID Nuxeo object ID
     * @param nuxeoFilePath Nuxeo file path where the document is stored (eg : file:content or files:files/0/file)
     * @param fileName WSDL file name
     * @return The WSDL download URL
     */
    private static String buildWsdlDownloadUrl(String nuxeoBaseUrl, String objectID, String nuxeoFilePath, String fileName) throws IllegalArgumentException {
        if(nuxeoBaseUrl == null || "".equals(nuxeoBaseUrl)){
            throw new IllegalArgumentException("nuxeoBaseUrl param must not be null or empty");
        }
        StringBuilder url = new StringBuilder();
        url.append(nuxeoBaseUrl);
        if(!nuxeoBaseUrl.endsWith("/")){
            url.append("/");
        }
        url.append("nxfile/default/");        
        url.append(objectID);
        // TODO : Find a better solution to generate the url, avoid hardcoded substrings
        // Always the first file ??
        //url.append("/files:files/0/file/");
        url.append("/").append(nuxeoFilePath).append("/");
        url.append(fileName);
        return url.toString();
    }
    
}
