/**
 * 
 */
package org.easysoa.registry.rest.marshalling;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Informations about WSDL registered in Nuxeo registry
 * 
 * @author jguillemotte
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class WSDLInformation {

    // TODO rename this class SimpleRegistryInformation ? Common name for all objects returned by simpleRegistryInformation
    // TODO what is objectType ???
    
    // Returned by queryWSDLInformation
    //returns : (sub)projectId, (nuxeo id,) name(title), description, soaname, objectType=InformationService(Endpoint), wsdlDownloadUrl=attachedFileUrl
    //OPT EasySOA id stored as component (service ??) name in composite (so it is available at SCA discovery time), OPT EasySOA id is a "pretty" versioned id

    // Returned by queryEndpoints
    //returns : (sub)projectId, (nuxeo id,) name(title), description, soaname, environment, objectType=Endpoint, endpointUrl [, endpointWsdlUrl=deduced, wsdlDownloadUrl=attachedFileUrl]
    //endpointUrl is enough to identify it in EasySOA. OPT store EasySOA id anyway in binding name in composite.
    
    private String projectID;
    
    private String nuxeoID;
    
    private String name;
   
    private String description;

    private String soaName;

    private String objectType;
    
    private String endpoint;
    
    private String wsdlDownloadUrl;

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
        this.projectID = projectID;
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
        this.nuxeoID = nuxeoID;
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
        this.name = name;
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
        this.description = description;
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
        this.soaName = soaName;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
        this.wsdlDownloadUrl = wsdlDownloadUrl;
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
        this.objectType = objectType;
    }
    
}
