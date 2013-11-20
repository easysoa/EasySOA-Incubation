/**
 * EasySOA Registry
 * Copyright 2012-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.registry.indicators.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.types.InformationService;
import org.nuxeo.ecm.core.api.CoreSession;


/**
 * Computes governance progress indicators for steering
 * 
 * @author mdutoo
 *
 */
public class PhaseProgressIndicatorProvider extends IndicatorProviderBase {

    private static final String SERVICE_DOCTYPE_INDICATOR = DoctypeCountProvider.buildName(InformationService.DOCTYPE);

    PhaseProgressIndicatorProvider(String category){
        super(category);
    }
    
	@Override
	public List<String> getRequiredIndicators() {
        return Arrays.asList(SERVICE_DOCTYPE_INDICATOR,
        		"serviceWithoutActor", "serviceWithoutInterface", "serviceWithoutComponent",
        		"serviceWithImplementation", "serviceWithoutImplementation",
        		"serviceWithMockImplementation", "serviceWithTestedImplementation",
        		"serviceWithProductionEndpoint", "serviceWithoutProductionEndpoint", "serviceWithEndpoint");
	}

	@Override
	public Map<String, IndicatorValue> computeIndicators(CoreSession session,
			String subprojectId,
			Map<String, IndicatorValue> computedIndicators, String visibility)
			throws Exception {

        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();

        // Count indicators - Service-specific
        long servicesCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        long serviceWithoutActorNb = computedIndicators.get("serviceWithoutActor").getCount();
        long serviceWithoutInterfaceNb = computedIndicators.get("serviceWithoutInterface").getCount();
        long serviceWithoutComponentNb = computedIndicators.get("serviceWithoutComponent").getCount();
        long serviceWithImplementationNb = computedIndicators.get("serviceWithImplementation").getCount();
        long serviceWithoutImplementationNb = computedIndicators.get("serviceWithoutImplementation").getCount();
        long serviceWithMockImplementationNb = computedIndicators.get("serviceWithMockImplementation").getCount();
        long serviceWithTestedImplementationNb = computedIndicators.get("serviceWithTestedImplementation").getCount();
        long serviceWithProductionEndpointNb = computedIndicators.get("serviceWithProductionEndpoint").getCount();
        long serviceWithoutProductionEndpointNb = computedIndicators.get("serviceWithoutProductionEndpoint").getCount();
        long serviceWithEndpointNb = computedIndicators.get("serviceWithEndpoint").getCount();
		
        // specifications can't be complete without all interfaces, provider actors, components)
        double specificationsProgress = 3 * servicesCount
        		- (serviceWithoutActorNb + serviceWithoutInterfaceNb + serviceWithoutComponentNb);
        newIndicator(indicators, "specificationsProgress", specificationsProgress, 3 * servicesCount,
                        		"3 * servicesCount - (serviceWhithoutActorNb + serviceWhithoutInterfaceNb + serviceWhithoutComponentNb");
        /*
                        		"3 * servicesCount[" + servicesCount + "] - (serviceWhithoutActorNb["
                        + serviceWithoutActorNb + "] + serviceWhithoutInterfaceNb[" + serviceWithoutInterfaceNb
                        + "] + serviceWhithoutComponentNb[" + serviceWithoutComponentNb + "]"));
                        */

        // realisation can't be complete without all actual impls, but tested impls & mock impls help closing half the gap
        long realisationProgress = 4 * servicesCount * serviceWithImplementationNb
        		+ serviceWithoutImplementationNb * (serviceWithMockImplementationNb + serviceWithTestedImplementationNb);
        newIndicator(indicators, "realisationProgress", realisationProgress, 4 * servicesCount * servicesCount,
                                "4 * servicesCount * serviceWithImplementationNb + serviceWhithoutImplementationNb * (serviceWhithMockImplementationNb + serviceWhithTestedImplementationNb)");
        /*
                        "4 * servicesCount[" + servicesCount + "] * serviceWithImplementationNb[" + serviceWithImplementationNb
                        + "] + serviceWhithoutImplementationNb[" + serviceWithoutImplementationNb + "] * (serviceWhithMockImplementationNb["
                        		+ serviceWithMockImplementationNb + "] + serviceWhithTestedImplementationNb[" + serviceWithTestedImplementationNb + "])"));
                        		*/
        
        // deploiement can't be complete without all production endpoints, but non-production endpoints help closing half the gap
        long deploiementProgress = 2 * servicesCount * serviceWithProductionEndpointNb
        		+ serviceWithoutProductionEndpointNb * serviceWithEndpointNb;
        newIndicator(indicators, "deploiementProgress", deploiementProgress, 2 * servicesCount * servicesCount,
                        		"2 * servicesCount * serviceWithProductionEndpointNb + serviceWhithoutProductionEndpointNb * serviceWithEndpointNb");
        /*
                        		"2 * servicesCount[" + servicesCount + "] * serviceWithProductionEndpointNb["
                        + serviceWithProductionEndpointNb + "] + serviceWhithoutProductionEndpointNb["
                        				+ serviceWithoutProductionEndpointNb + "] * serviceWithEndpointNb[" + serviceWithEndpointNb + "]"));
                        				*/
        
        return indicators;
	}

}
