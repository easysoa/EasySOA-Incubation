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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mkalam-alami
 */
public class IndicatorValue {

    private String name;
    private String description;
    private Set<String> categories = new HashSet<String>(); // TODO : several categories ??
    private long count;
    private int percentage;
    private long percentageTotal;
    private Date date = null;
    private List<String> args = new ArrayList<String>();

    public IndicatorValue(String name, String category, String description) {
        this.name= name;
        this.description = description;
        addCategory(category);
    }

    /**
     * Creates a count indicator and computes its percentage
     * @param count The indicator value, or -1 if not relevant
     * @param percentageTotal The total count allowing to compute the percentage
     * (the indicator value in percents), or -1 if not relevant
     */
    public IndicatorValue(String name, String category, double count, long percentageTotal, String description) {
    	this(name, category, description);
        this.count = Math.round(count);
        this.percentageTotal = percentageTotal;
        if(this.percentageTotal <= 0) { // -1 or 0
        	this.percentage = -1;
        } else {
            // Compute the percentage value
        	this.percentage = (int) Math.round(100 * count / percentageTotal);
        }
    }

    /**
     * Creates a percentage-only indicator
     * @param name
     * @param category
     * @param percentage
     * @param description
     */
    public IndicatorValue(String name, String category, int percentage, String description) {
    	this(name, category, description);
        this.count = -1;
        this.percentageTotal = -1;
    	this.percentage = percentage;
	}

    /**
     * Creates a percentage-only indicator
     * @param name
     * @param category
     * @param percentage
     */
    public IndicatorValue(String name, String category, int percentage) {
		this(name, category, percentage, null);
    }

    /**
     * Creates a count indicator and computes its percentage
     * @param name
     * @param category
     * @param count
     * @param percentageTotal
     */
    public IndicatorValue(String name, String category, double count, long percentageTotal) {
		this(name, category, count, percentageTotal, null);
	}

	/**
     * @return The indicator name
     */
    public String getName(){
        return name;
    }

    /**
     * @return The indicator description
     */
    public String getDescription(){
        return this.description;
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
    public long getCount() {
        return count;
    }

    /**
     *
     * @return The indicator value in percents, or -1 if not relevant
     */
    public int getPercentage() {
        return percentage;
    }

    public long getPercentageTotal() {
        return percentageTotal;
    }

    @Override
    public String toString() {
        String countString = (count != -1) ? Long.toString(count) : "N.A.";
        String percentageTotalString = (percentageTotal != -1) ? Long.toString(percentageTotal) + "%" : "N.A.";
        String percentageString = (percentage != -1) ? Integer.toString(percentage) + "%" : "N.A.";
        return "[" + countString + " / " + percentageTotalString + " : " + percentageString + "%]";
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Add an argument in the argument list
     * @param arg
     */
    public void addArg(String arg){
        if(arg != null && !"".equals(arg)){
            args.add(arg);
        }
    }

    /**
     * @return the args
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Set the argument list (to be used for message translate for example)
     * @param args the args to set
     */
    public void setArgs(List<String> args) {
        this.args = args;
    }

}
