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


public class DoctypeCountIndicator extends QueryCountIndicator {

    private final String doctype;

    public DoctypeCountIndicator(String doctype) {
        super(DocumentService.NXQL_SELECT_FROM + doctype + DocumentService.NXQL_WHERE_NO_PROXY);
        this.doctype = doctype;
    }

    @Override
    public String getName() {
        return getName(doctype);
    }

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }
    
    public static String getName(String doctype) {
        return doctype + " count";
    }
    
}
