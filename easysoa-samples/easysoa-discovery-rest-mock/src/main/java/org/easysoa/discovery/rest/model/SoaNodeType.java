package org.easysoa.discovery.rest.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

@JsonSerialize(using = ToStringSerializer.class)
public enum SoaNodeType {

    SoaSystem,
    Service,
    Software,
    Deliverable,
    ServiceImpl,
    Environment,
    DeployedDeliverable,
    Endpoint;
    
}
