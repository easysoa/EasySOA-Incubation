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
