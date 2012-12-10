/**
 * 
 */
package org.easysoa.registry.rest.integration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Container for SlaOrOlaIndicatorList
 * 
 * @author jguillemotte
 *
 */
@XmlRootElement
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class SlaOrOlaIndicators {

    @XmlElement
    private List<SlaOrOlaIndicator> slaOrOlaIndicators;
    
    /**
     * 
     */
    public SlaOrOlaIndicators(){
        slaOrOlaIndicators = new ArrayList<SlaOrOlaIndicator>();
    }
    
    /**
     * 
     * @param indicator
     */
    public void addSlaOrOlaIndicator(SlaOrOlaIndicator indicator){
        this.slaOrOlaIndicators.add(indicator);
    }

    /**
     * 
     * @return
     */
    public List<SlaOrOlaIndicator> getSlaOrOlaIndicatorList() {
        return slaOrOlaIndicators;
    }

    /**
     * 
     * @param slaOrOlaIndicatorList
     */
    public void setSlaOrOlaIndicatorList(List<SlaOrOlaIndicator> slaOrOlaIndicatorList) {
        if(slaOrOlaIndicatorList != null){
            this.slaOrOlaIndicators = slaOrOlaIndicatorList;
        } else {
            this.slaOrOlaIndicators = new ArrayList<SlaOrOlaIndicator>();    
        }
    }
    
}
