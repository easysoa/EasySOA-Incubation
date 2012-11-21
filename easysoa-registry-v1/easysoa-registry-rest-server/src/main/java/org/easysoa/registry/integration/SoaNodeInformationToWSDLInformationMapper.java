/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.marshalling.WSDLInformation;
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
    public static WSDLInformation mapToWSDLInformation(DocumentModel nodeModel, String nuxeoBaseUrl) throws Exception {
   
        WSDLInformation wsdlInformation = new WSDLInformation();

        // Filling object with document properties
        wsdlInformation.setSoaName((String)nodeModel.getPropertyValue("soan:name"));
        wsdlInformation.setProjectID(""); // Tag corresponding to project ID ??? Not implemented yet
        wsdlInformation.setName((String)nodeModel.getPropertyValue("dc:title"));  
        wsdlInformation.setDescription((String)nodeModel.getPropertyValue("dc:description"));            
        wsdlInformation.setNuxeoID(nodeModel.getId()); // the Nuxeo object ID
        wsdlInformation.setObjectType(nodeModel.getType());
        
        // Only for endpoint objects
        wsdlInformation.setEndpoint(""); 
        
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
        return wsdlInformation;        
    }
    
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
