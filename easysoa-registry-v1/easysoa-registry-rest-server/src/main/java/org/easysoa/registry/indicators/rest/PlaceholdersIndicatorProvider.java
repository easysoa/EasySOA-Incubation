package org.easysoa.registry.indicators.rest;

import java.util.List;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.SoaNode;

public class PlaceholdersIndicatorProvider extends QueryCountIndicator {

    public PlaceholdersIndicatorProvider() {
        super(DocumentService.NXQL_SELECT_FROM + SoaNode.ABSTRACT_DOCTYPE
                + DocumentService.NXQL_WHERE_NO_PROXY
                + DocumentService.NXQL_AND + DocumentService.NXQL_IS_NOT_DELETED
                + DocumentService.NXQL_AND + SoaNode.XPATH_ISPLACEHOLDER + " = 1",
              DocumentService.NXQL_SELECT_FROM + SoaNode.ABSTRACT_DOCTYPE
                + DocumentService.NXQL_WHERE_NO_PROXY
                + DocumentService.NXQL_AND + DocumentService.NXQL_IS_NOT_DELETED);
    }

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public String getName() {
        return "Placeholders count";
    }

}
