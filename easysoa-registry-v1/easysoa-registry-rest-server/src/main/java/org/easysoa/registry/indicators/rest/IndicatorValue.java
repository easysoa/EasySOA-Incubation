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

/**
 * 
 * @author jguillemotte
 */
public class IndicatorValue {
    
    private int count;
    private int percentage;

    /**
     * 
     * @param count The indicator value, or -1 if not relevant
     * @param percentage The indicator value in percents, or -1 if not relevant
     */
    public IndicatorValue(int count, int percentage) {
        this.count = count;
        this.percentage = percentage;
    }
    
    /**
     * 
     * @return The indicator value, or -1 if not relevant
     */
    public int getCount() {
        return count;
    }
    
    /**
     * 
     * @return The indicator value in percents, or -1 if not relevant
     */
    public int getPercentage() {
        return percentage;
    }
    
    @Override
    public String toString() {
        String countString = (count != -1) ? Integer.toString(count) : "N.A.";
        String percentageString = (percentage != -1) ? Integer.toString(percentage) + "%" : "N.A.";
        return "[" + countString + " / " + percentageString + "]";
    }

}
