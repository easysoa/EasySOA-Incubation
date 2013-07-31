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

import org.easysoa.registry.DocumentService;

/**
 * Provider for Doctype counter indicators
 * 
 * @author jguillemotte
 */
public class DoctypeCountProvider extends QueryCountIndicator {

    private final String docType;
    private final String category;
    private final String description;
    private final String name;

    /**
     * 
     * @param name
     * @param description
     * @param doctype
     * @param category 
     */
    public DoctypeCountProvider(String name, String description, String docType, String category) {
        super(DocumentService.NXQL_SELECT_FROM + docType + DocumentService.NXQL_WHERE_NO_PROXY);
        this.docType = docType;
        this.name = name;
        this.description = description;
        this.category = category;
    }    
    
    /**
     * 
     * @param name
     * @param doctype
     * @param category 
     */
    public DoctypeCountProvider(String name, String docType, String category) {
        this(name, null, docType, category);
    }

    /**
     * 
     * @param doctype
     * @param category 
     */
    public DoctypeCountProvider(String docType, String category) {
        this(docType, null, docType, category);
    }
    
    @Override
    public String getName() {
        //return getName(doctype);
        return this.name;
    }

    @Override
    public String getDocType(){
        return this.docType;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public String getCategory(){
        return this.category;
    }

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }
    
    public static String getName(String docType) {
        return docType;
    }
    
}
