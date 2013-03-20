package org.easysoa.registry.indicators.rest;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Provider of one or several indicators.
 * Can depend on other indicators that are then passed as parameters for computing. 
 * 
 * @author mkalam-alami
 *
 */
public interface IndicatorProvider {
    
    /**
     * @return The required indicator names
     */
    List<String> getRequiredIndicators();
    
    /**
     * @param session
     * @param computedIndicators A map of the previously computed indicators.
     * Will always contain values of the required indicators.
     * @return The new indicators to register
     * @throws Exception
     */
    Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception;
    
}
