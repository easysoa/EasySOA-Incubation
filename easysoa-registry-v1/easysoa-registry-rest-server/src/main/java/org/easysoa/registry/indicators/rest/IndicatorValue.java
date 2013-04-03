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

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author mkalam-alami
 */
public class IndicatorValue {
    
    private String name; // TODO : good idea to put the name in the indicator value ???
    private Set<String> categories = new HashSet<String>(); // TODO : several categories ??
    private int count;
    private int percentage;

    /**
     * 
     * @param count The indicator value, or -1 if not relevant
     * @param percentage The indicator value in percents, or -1 if not relevant
     */
    public IndicatorValue(String name, String category, int count, int percentage) {
        this.name= name;
        addCategory(category);
        this.count = count;
        this.percentage = percentage;
    }
    
    /**
     * 
     * @return The indicator name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Add a category
     * @param category 
     */
    public void addCategory(String category){
        if(!categories.contains(category)){
            categories.add(category);
        }
    }
    
    /**
     * 
     * @return The indicator categories
     */
    public Set<String> getCategories(){
        return categories;
    }
    
    /**
     * 
     * @param category
     * @return 
     */
    public boolean containsCategory(String category){
        if(categories.contains(category)){
            return true;
        } else {
            return false;
        }
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
