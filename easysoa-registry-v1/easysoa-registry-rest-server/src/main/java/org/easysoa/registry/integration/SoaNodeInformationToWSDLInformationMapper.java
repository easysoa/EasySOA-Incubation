/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.EndpointInformation;
import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.types.Endpoint;
import org.nuxeo.ecm.core.api.Blob;
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
    public static ServiceInformation mapToServiceInformation(DocumentModel nodeModel, String nuxeoBaseUrl) throws Exception {
   
        ServiceInformation serviceInformation = new ServiceInformation();

        //mapCommonWsdlInformation(nodeModel, serviceInformation, nuxeoBaseUrl);
        // Filling object with document properties
        serviceInformation.setSoaName((String)nodeModel.getPropertyValue("soan:name"));
        serviceInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        serviceInformation.setName((String)nodeModel.getPropertyValue("dc:title"));
        serviceInformation.setDescription((String)nodeModel.getPropertyValue("dc:description"));
        serviceInformation.setNuxeoID(nodeModel.getId()); // the Nuxeo object ID
        serviceInformation.setObjectType(nodeModel.getType());
        
        // WSDL download URL : To be builded by hand
        // http://localhost:8080/nuxeo/nxfile/default/d5bd2a85-a936-4319-8cf9-6e4cd5fd0381/files:files/0/file/WeatherService.wsdl
        try {
            Blob blob = (Blob)nodeModel.getPropertyValue("files/0/file/");
            if (blob != null) {
                serviceInformation.setWsdlDownloadUrl(buildWsdlDownloadUrl(nuxeoBaseUrl, nodeModel.getId(), blob.getFilename()));
            } else {
                serviceInformation.setWsdlDownloadUrl("");
            }
        }
        catch(Exception ex){
            serviceInformation.setWsdlDownloadUrl("");
            //ex.printStackTrace();
        }         
        
        return serviceInformation;        
    }
    
    public static EndpointInformation mapToEndpointInformation(DocumentModel nodeModel, String nuxeoBaseUrl) throws Exception {
        EndpointInformation endpointInformation = new EndpointInformation();
        
        //mapCommonWsdlInformation(nodeModel, endpointInformation, nuxeoBaseUrl);
        // Filling object with document properties
        endpointInformation.setSoaName((String)nodeModel.getPropertyValue("soan:name"));
        endpointInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        endpointInformation.setName((String)nodeModel.getPropertyValue("dc:title"));
        endpointInformation.setDescription((String)nodeModel.getPropertyValue("dc:description"));
        endpointInformation.setNuxeoID(nodeModel.getId()); // the Nuxeo object ID
        endpointInformation.setObjectType(nodeModel.getType());
        
        // WSDL download URL : To be builded by hand
        // http://localhost:8080/nuxeo/nxfile/default/d5bd2a85-a936-4319-8cf9-6e4cd5fd0381/files:files/0/file/WeatherService.wsdl
        try {
            Blob blob = (Blob)nodeModel.getPropertyValue("files/0/file/");
            if (blob != null) {
                endpointInformation.setWsdlDownloadUrl(buildWsdlDownloadUrl(nuxeoBaseUrl, nodeModel.getId(), blob.getFilename()));
            } else {
                endpointInformation.setWsdlDownloadUrl("");
            }
        }
        catch(Exception ex){
            endpointInformation.setWsdlDownloadUrl("");
            //ex.printStackTrace();
        }         
        
        if(Endpoint.DOCTYPE.equalsIgnoreCase(endpointInformation.getObjectType())){
            // Only for endpoint objects
            endpointInformation.setEndpointUrl((String)nodeModel.getPropertyValue("endp:url")); 
            endpointInformation.setEnvironment((String)nodeModel.getPropertyValue("env:environment"));
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
     * @param fileName WSDL file name
     * @return The WSDL download URL
     */
    private static String buildWsdlDownloadUrl(String nuxeoBaseUrl, String objectID, String fileName) throws IllegalArgumentException {
        if(nuxeoBaseUrl == null || "".equals(nuxeoBaseUrl)){
            throw new IllegalArgumentException("nuxeoBaseUrl param must not be null or empty");
        }
        StringBuffer url = new StringBuffer();
        url.append(nuxeoBaseUrl);
        if(!nuxeoBaseUrl.endsWith("/")){
            url.append("/");
        }
        url.append("nxfile/default/");        
        url.append(objectID);
        // TODO : Find a better solution to generate the url, avoid hardcoded substrings
        // Always the first file ??
        url.append("/files:files/0/file/");
        url.append(fileName);
        return url.toString();
    }
    
    
}
