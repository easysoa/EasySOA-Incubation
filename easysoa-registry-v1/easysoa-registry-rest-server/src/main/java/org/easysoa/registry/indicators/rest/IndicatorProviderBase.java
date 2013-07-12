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

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class IndicatorProviderBase implements IndicatorProvider {

    protected String category;

    public IndicatorProviderBase(String category){
        if(category == null){
            this.category = "";
        } else {
            this.category = category;
        }
    }

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public List<String> getRequiredIndicators() {
		return null;
	}

	/**
	 * Creates and adds a count indicator and computes its percentage
	 * @param indicators
	 * @param name
	 * @param count
	 * @param percentageTotal
	 * @param description
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, double count, long percentageTotal, String description) {
		IndicatorValue indicator = new IndicatorValue(name, category, count, percentageTotal, description);
		indicators.put(name, indicator);
		return indicator;
	}

	/**
	 * Creates and adds a percentage-only indicator
	 * @param indicators
	 * @param name
	 * @param percentage
	 * @param description
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, int percentage, String description) {
		IndicatorValue indicator = new IndicatorValue(name, category, percentage, description);
		indicators.put(name, indicator);
		return indicator;
	}

	/**
	 * Creates and adds a date-only indicator
	 * @param indicators
	 * @param name
	 * @param percentage
	 * @param description
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, Date date, String description) {
		IndicatorValue indicator = new IndicatorValue(name, category, -1, description);
		indicator.setDate(date);
		indicators.put(name, indicator);
		return indicator;
	}

	/**
	 * Creates and adds a count indicator and computes its percentage
	 * @param indicators
	 * @param name
	 * @param count
	 * @param percentageTotal
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, double count, long percentageTotal) {
		return newIndicator(indicators, name, count, percentageTotal, null);
	}

	/**
	 * Creates and adds a percentage-only indicator
	 * @param indicators
	 * @param name
	 * @param percentage
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, int percentage) {
		return newIndicator(indicators, name, percentage, null);
	}

	/**
	 * Creates and adds a date-only indicator
	 * @param indicators
	 * @param name
	 * @param date
	 * @return
	 */
	protected IndicatorValue newIndicator( Map<String, IndicatorValue> indicators,
			String name, Date date) {
		return newIndicator(indicators, name, date, null);
	}

}
