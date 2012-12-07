/**
 * 
 */
package org.easysoa.registry.rest.integration;

/**
 * @author jguillemotte
 *
 */
public class SlaOrOlaIndicator {
    
    //{endpointId, slaOrOlaName, timestamp, serviceLevelHealth=gold/silver/bronze, serviceLevelViolation=true/false})

    private String endpointId;
    
    private String slaOrOlaName;
    
    private long timestamp;
    
    private ServiceLevelHealth serviceLevelHealth;
    
    private boolean serviceLevelViolation;    
    
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
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
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
    
}
