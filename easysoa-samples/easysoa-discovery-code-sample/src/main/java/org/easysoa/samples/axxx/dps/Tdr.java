package org.easysoa.samples.axxx.dps;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Tdr {
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
