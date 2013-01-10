package org.easysoa.registry.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Platform;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.model.PropertyException;

public class MatchingHelper {

    private static Logger logger = Logger.getLogger(MatchingHelper.class);
    
    public static List<String> implPlatformPropsToMatch = Collections.unmodifiableList(Arrays.asList(
            Platform.XPATH_LANGUAGE, Platform.XPATH_BUILD, Platform.XPATH_SERVICE_LANGUAGE,
            Platform.XPATH_DELIVERABLE_NATURE, Platform.XPATH_DELIVERABLE_REPOSITORY_URL));
    public static List<String> endpointPlatformPropsToMatch = Collections.unmodifiableList(Arrays.asList(
            Platform.XPATH_SERVICE_PROTOCOL, Platform.XPATH_TRANSPORT_PROTOCOL, Platform.XPATH_SERVICE_RUNTIME,
            Platform.XPATH_APP_SERVER_RUNTIME, Platform.XPATH_SERVICE_SECURITY, Platform.XPATH_SERVICE_SECURITY_MANAGER_URL,
            Platform.XPATH_SERVICE_MONITORING, Platform.XPATH_SERVICE_MONITORYING_MANAGER_URL));
    public static List<String> allPlatformPropsToMatch;
    static {
        allPlatformPropsToMatch = new ArrayList<String>();
        allPlatformPropsToMatch.addAll(implPlatformPropsToMatch);
        allPlatformPropsToMatch.addAll(endpointPlatformPropsToMatch);
        allPlatformPropsToMatch = Collections.unmodifiableList(allPlatformPropsToMatch);
    }

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


    /**
     * TODO move
     * @param documentManager
     * @param query
     * @param filterComponentId
     * @param endpoint
     * @return the applied filterComponentId else null
     * @throws ClientException
     */
    public static String appendComponentFilterToQuery(CoreSession documentManager,
            MatchingQuery query, String filterComponentId, DocumentModel soaNodeToMatch) throws ClientException {
        String filterComponentIdToUse = filterComponentId;
        if (filterComponentId == null && soaNodeToMatch.getPropertyValue(Endpoint.XPATH_COMPONENT_ID) != null) {
            filterComponentIdToUse = (String) soaNodeToMatch.getPropertyValue(Endpoint.XPATH_COMPONENT_ID);
        }
        if (filterComponentIdToUse != null) {
            if (logger.isDebugEnabled() && !documentManager.exists(new IdRef(filterComponentIdToUse))) {
                logger.warn("While matching SOA node " + soaNodeToMatch + ", Component ID doesn't exist : "
                        + filterComponentIdToUse + ((filterComponentId != null) ? "provided along" : "else stored in it"));
            }
            query.addCriteria(Component.XPATH_COMPONENT_ID + " = '" + filterComponentIdToUse + "'");
            return filterComponentIdToUse;
        }
        return null;
    }
    
}
