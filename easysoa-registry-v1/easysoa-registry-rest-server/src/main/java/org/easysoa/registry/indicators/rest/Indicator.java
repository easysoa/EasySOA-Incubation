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

import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Simple indicator provider, that only computes 1 indicator.
 * 
 * @author mkalam-alami
 *
 */
public abstract class Indicator implements IndicatorProvider {

    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility ) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put(getName(), compute(session, subprojectId, computedIndicators, visibility));
        return indicators;
    }
    
    public abstract String getName();
    
    //public abstract String getDescription();
    
    public abstract String getCategory();
    
    public abstract IndicatorValue compute(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception;
    
}
