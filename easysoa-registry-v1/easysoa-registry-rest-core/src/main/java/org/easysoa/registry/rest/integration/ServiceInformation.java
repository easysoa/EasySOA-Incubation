/**
 * 
 */
package org.easysoa.registry.rest.integration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author jguillemotte
 *
 */
@XmlRootElement // TODO rm
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ServiceInformation /*extends WSDLInformation*/ {

    @XmlElement
    private EndpointInformations endpoints;
    
    /**
     * Default constructor
     */
    public ServiceInformation(){
        //super();
        endpoints = new EndpointInformations();
        
        ///////////////////
        this.description = "";
        this.name = "";
        this.nuxeoID = "";
        this.objectType = "";
        this.projectID = "";
        this.soaName = "";
        this.wsdlDownloadUrl = "";        
        
        
    }
    
    /**
     * Returns the endpoints for the service
     * @return
     */
    public EndpointInformations getEndpoints(){
        return this.endpoints;
    }
    
    /**
     * Set the endpoints associated with the service
     * @param endpoints
     */
    public void setEndpoints(EndpointInformations endpoints){
        if(endpoints != null){
            this.endpoints = endpoints;
        } else {
            this.endpoints = new EndpointInformations(); 
        }
    }
    
    protected String projectID;
    
    protected String nuxeoID;
    
    protected String name;
   
    protected String description;

    protected String soaName;

    protected String objectType;
    
    protected String wsdlDownloadUrl;

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
    
    /**
     * 
     * @param param The parameter to check
     * @return The param avlue or empty string if the param is null
     */
    protected String checkNotNull(String param){
        if(param != null){
            return param;
        } else {
            return "";
        }        
    }    
    
}
