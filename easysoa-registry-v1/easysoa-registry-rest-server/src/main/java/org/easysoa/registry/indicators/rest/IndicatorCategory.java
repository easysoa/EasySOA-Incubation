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
 * 
 */
public enum IndicatorCategory {

    // Not used at the moment to avoid a too rigid system
    
    STEERING("steering"),
    CARTOGRAPHY("cartography"),
    USAGE("usage"),
    MATCHING("matching");
    
    private String name;

    private IndicatorCategory(String name){
        this.name = name;
    }
    
    /**
     * @return The enum name
     */
    public String getName(){
        return this.name;
    }
    
}
