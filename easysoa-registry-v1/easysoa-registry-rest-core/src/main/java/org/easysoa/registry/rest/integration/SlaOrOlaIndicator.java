/**
 * 
 */
package org.easysoa.registry.rest.integration;

import java.util.Date;

/**
 * @author jguillemotte
 *
 */
public class SlaOrOlaIndicator {
    
    //{endpointId, slaOrOlaName, timestamp, serviceLevelHealth=gold/silver/bronze, serviceLevelViolation=true/false})

    private String endpointId = "";
    
    // Indicator type : SLA or OLA
    private String type = "";
    
    private String slaOrOlaName = "";
    
    private String description = "";
    
    private Date timestamp;
    
    private ServiceLevelHealth serviceLevelHealth;
    
    private Boolean serviceLevelViolation;
    
    private String path = "";
    
    /**
     * @return the endpointId
     */
    public String getEndpointId() {
        return endpointId;
    }

    /**
     * @param endpointId the endpointId to set
     */
    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    /**
     * @return the slaOrOlaName
     */
    public String getSlaOrOlaName() {
        return slaOrOlaName;
    }

    /**
     * @param slaOrOlaName the slaOrOlaName to set
     */
    public void setSlaOrOlaName(String slaOrOlaName) {
        this.slaOrOlaName = slaOrOlaName;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the serviceLavelHealth
     */
    public ServiceLevelHealth getServiceLevelHealth() {
        return serviceLevelHealth;
    }

    /**
     * @param serviceLavelHealth the serviceLavelHealth to set
     */
    public void setServiceLevelHealth(ServiceLevelHealth serviceLevelHealth) {
        this.serviceLevelHealth = serviceLevelHealth;
    }

    /**
     * @return the serviceLevelViolation
     */
    public boolean isServiceLevelViolation() {
        return serviceLevelViolation;
    }

    /**
     * @param serviceLevelViolation the serviceLevelViolation to set
     */
    public void setServiceLevelViolation(boolean serviceLevelViolation) {
        this.serviceLevelViolation = serviceLevelViolation;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    
}
