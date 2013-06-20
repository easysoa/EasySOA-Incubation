/**
 * EasySOA Registry
 * Copyright 2012 Open Wide
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
     * To allow to find it among others.
     * Is its class name if not an indicator instancfe itself.
     * @return its name
     */
    String getName();
    
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
