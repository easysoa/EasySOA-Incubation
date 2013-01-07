package org.easysoa.registry.matching;

import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

public class MatchingHelper {

    public static boolean isWsdlInfo(DocumentModel wsdlInfo) throws PropertyException, ClientException {
        return wsdlInfo.hasFacet(WsdlInfoFacet.FACET_WSDLINFO) // for now static facet so always
                //&& Platform.SERVICE_LANGUAGE_JAXWS.equals(informationService.getPropertyValue(Platform.XPATH_SERVICE_LANGUAGE))
                && wsdlInfo.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME) != null; // OPT dynamic if possible when setting props in DiscoveryService ??
    }

    public static boolean isRestInfo(DocumentModel restInfo) throws PropertyException, ClientException {
        return restInfo.hasFacet(RestInfoFacet.FACET_RESTINFO) // for now static facet so always
                //&& Platform.SERVICE_LANGUAGE_JAXRS.equals(informationService.getPropertyValue(Platform.XPATH_SERVICE_LANGUAGE))
                && restInfo.getPropertyValue(RestInfoFacet.XPATH_REST_PATH) != null; // OPT dynamic if possible when setting props in DiscoveryService ??
    }
    
}
