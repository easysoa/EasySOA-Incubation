/**
 * 
 */
package org.easysoa.registry.rest.integration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author jguillemotte
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class EndpointInformation/* extends WSDLInformation */{

    private String environment;
    
    private String endpointUrl;    
    
    public EndpointInformation(){
        //super();
        this.environment = "";
        this.endpointUrl = "";
        
        ////////////////////
        this.description = "";
        this.name = "";
        this.nuxeoID = "";
        this.objectType = "";
        this.projectID = "";
        this.soaName = "";
        this.wsdlDownloadUrl = "";
        
    }

    /**
     * @return the endpoint
     */
    public String getEndpointUrl() {
        return endpointUrl;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = checkNotNull(endpointUrl);
    }
    
    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = checkNotNull(environment);
    }    
    
    
    // copied from InformationService (to avoid inheriting from it to simplify mapping to REST)
    private String projectID;
    
    private String nuxeoID;
    
    private String name;
   
    private String description;

    private String soaName;

    private String objectType;
    
    private String wsdlDownloadUrl;
    private String wsdlPortType;
    private String wsdlServiceName;
    
    private String restPath;
    private String restAccepts;
    private String restContentType;

    /**
     * Default constructor
     */
    /*public WSDLInformation(){
        this.description = "";
        this.name = "";
        this.nuxeoID = "";
        this.objectType = "";
        this.projectID = "";
        this.soaName = "";
        this.wsdlDownloadUrl = "";
    }*/
    
    /**
     * @return the projectID
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * @param projectID the projectID to set
     */
    public void setProjectID(String projectID) {
        this.projectID = checkNotNull(projectID);
    }

    /**
     * @return the nuxeoID
     */
    public String getNuxeoID() {
        return nuxeoID;
    }

    /**
     * @param nuxeoID the nuxeoID to set
     */
    public void setNuxeoID(String nuxeoID) {
        this.nuxeoID = checkNotNull(nuxeoID);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = checkNotNull(name);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = checkNotNull(description);
    }

    /**
     * @return the soaName
     */
    public String getSoaName() {
        return soaName;
    }

    /**
     * @param soaName the soaName to set
     */
    public void setSoaName(String soaName) {
        this.soaName = checkNotNull(soaName);
    }

    /**
     * @return the wsdlDownloadUrl
     */
    public String getWsdlDownloadUrl() {
        return wsdlDownloadUrl;
    }

    /**
     * @param wsdlDownloadUrl the wsdlDownloadUrl to set
     */
    public void setWsdlDownloadUrl(String wsdlDownloadUrl) {
        this.wsdlDownloadUrl = checkNotNull(wsdlDownloadUrl);
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = checkNotNull(objectType);
    }
    
    public String getWsdlPortType() {
        return wsdlPortType;
    }

    public void setWsdlPortType(String wsdlPortType) {
        this.wsdlPortType = wsdlPortType;
    }

    public String getWsdlServiceName() {
        return wsdlServiceName;
    }

    public void setWsdlServiceName(String wsdlServiceName) {
        this.wsdlServiceName = wsdlServiceName;
    }

    public String getRestPath() {
        return restPath;
    }

    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    public String getRestAccepts() {
        return restAccepts;
    }

    public void setRestAccepts(String restAccepts) {
        this.restAccepts = restAccepts;
    }

    public String getRestContentType() {
        return restContentType;
    }

    public void setRestContentType(String restContentType) {
        this.restContentType = restContentType;
    }
    
    /**
     * 
     * @param param The parameter to check
     * @return The param avlue or empty string if the param is null
     */
    private String checkNotNull(String param){
        if(param != null){
            return param;
        } else {
            return "";
        }        
    }
    
}
