package org.easysoa.registry.types;

import org.easysoa.registry.types.ids.SoaNodeId;

/**
 *
 * @author mkalam-alami
 *
 */
public interface EndpointConsumption extends ServiceConsumption {

    static final String DOCTYPE = "EndpointConsumption";

    static final String PREDICATE_CONSUMES = "consumes";

    static final String XPATH_CONSUMER_HOST = "ec:consumerHost";

    static final String XPATH_CONSUMED_URL = "ec:consumedUrl";

    static final String XPATH_CONSUMED_ENVIRONMENT = "ec:consumedEnvironment";

    static final String XPATH_CONSUMER_IP = "ec:consumerIp";

    SoaNodeId getConsumedEndpoint() throws Exception;

    void setConsumedEndpoint(SoaNodeId consumedEndpoint) throws Exception;

    String getHost() throws Exception;

}
