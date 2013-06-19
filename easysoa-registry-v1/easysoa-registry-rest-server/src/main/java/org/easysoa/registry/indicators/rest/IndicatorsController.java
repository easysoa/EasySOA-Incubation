/**
 * EasySOA Registry Copyright 2012 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.registry.indicators.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.rest.EasysoaModuleRoot;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.utils.ContextData;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;

/**
 * Indicators
 * 
 * TODO refactor init of providers & computing to dedicated IndicatorsService
 *
 * @author mdutoo
 *
 */
//XXX Slightly outdated after removal of Service doctype
@WebObject(type = "EasySOA")
@Path("easysoa/indicators")
public class IndicatorsController extends EasysoaModuleRoot {

    // New category for indicators
    public static final String CATEGORY_STEERING = "steering"; // Or Governance ?
    public static final String CATEGORY_CARTOGRAPHY = "cartography";
    public static final String CATEGORY_USAGE = "usage";
    public static final String CATEGORY_MATCHING = "matching";

    private static Logger logger = Logger.getLogger(IndicatorsController.class);

    private List<IndicatorProvider> indicatorProviders = new ArrayList<IndicatorProvider>();

    /**
     * Init the indicator controller
     */
    public IndicatorsController() {
        // Document count by type
        addIndicator(new DoctypeCountProvider("Nombre de " + SoaNode.ABSTRACT_DOCTYPE, "", SoaNode.ABSTRACT_DOCTYPE, CATEGORY_CARTOGRAPHY)); // TODO : replace all these providers by a unique provider
        addIndicator(new DoctypeCountProvider("Nombre de " + InformationService.DOCTYPE, "", InformationService.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + SoftwareComponent.DOCTYPE, "", SoftwareComponent.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + ServiceImplementation.DOCTYPE, "", ServiceImplementation.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + Deliverable.DOCTYPE, "", Deliverable.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + DeployedDeliverable.DOCTYPE, "", DeployedDeliverable.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + Endpoint.DOCTYPE, "", Endpoint.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + EndpointConsumption.DOCTYPE, "", EndpointConsumption.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(new DoctypeCountProvider("Nombre de " + TaggingFolder.DOCTYPE, "", TaggingFolder.DOCTYPE, CATEGORY_CARTOGRAPHY));

        // Doctype-specific indicators
        addIndicator(new ServiceStateProvider(CATEGORY_STEERING));
        addIndicator(new ServiceImplStateProvider(CATEGORY_CARTOGRAPHY));
        //addIndicator(new SoftwareComponentIndicatorProvider()); // Disabled, need to be updated
        addIndicator(new TagsIndicatorProvider(CATEGORY_STEERING));
        addIndicator(new ServiceConsumptionIndicatorProvider(CATEGORY_USAGE));

        // Miscellaneous indicators
        addIndicator(new PlaceholdersIndicatorProvider(CATEGORY_MATCHING));

        //
        addIndicator(new LastCodeDiscoveryIndicatorProvider(CATEGORY_CARTOGRAPHY));
        //
        addIndicator(new ServiceDefaultCountIndicatorProvider(CATEGORY_STEERING));
        //
        addIndicator(new PhaseProgressIndicatorProvider(CATEGORY_STEERING));
    }

    /**
     * Add an indicator in the indicator list
     *
     * @param indicator
     */
    public void addIndicator(IndicatorProvider indicator) {
        indicatorProviders.add(indicator);
    }

    // TODO : add a method to compute indicators and to be callable from another controller
    // TODO : return a single map instead of 2 separed maps for percents and count values
    /**
     *
     * @param nbMap
     * @param percentMap
     * @param subprojectId The subproject (or version) ID
     * @param visibility Visibility used when computing the indicators
     * @return A map containing the indicators values
     * @throws Exception If a problem occurs
     */
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, HashMap<String, Integer> nbMap, HashMap<String, Integer> percentMap, String subprojectId, String visibility) throws Exception {

        // using all subprojects or getting default one is let to indicator impls TODO better
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        /*String subprojectPathCriteria;
         if (subprojectId == null) {
         subprojectPathCriteria = "";
         } else {
         subprojectPathCriteria = " " + IndicatorProvider.NXQL_PATH_STARTSWITH + session.getDocument(new IdRef(subprojectId)).getPathAsString() + "'";
         }*/

        if ("".equals(subprojectId)) {
            subprojectId = null;
        }

        Map<String, IndicatorValue> indicators = computeIndicators(session, subprojectId, visibility);

        // Create and return view
        if (nbMap == null) {
            nbMap = new HashMap<String, Integer>();
        }
        if (percentMap == null) {
            percentMap = new HashMap<String, Integer>();
        }

        for (Entry<String, IndicatorValue> indicator : indicators.entrySet()) {
            if (indicator.getValue().getCount() != -1) {
                nbMap.put(indicator.getKey(), indicator.getValue().getCount());
            }
            if (indicator.getValue().getPercentage() != -1) {
                percentMap.put(indicator.getKey(), indicator.getValue().getPercentage());
            }
        }
        return indicators;
    }

    /**
     *
     * @param subprojectId
     * @param visibility
     * @return The indicators view
     * @throws Exception
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);

        // Init maps
        HashMap<String, Integer> nbMap = new HashMap<String, Integer>();
        HashMap<String, Integer> percentMap = new HashMap<String, Integer>();

        // Compute the indicators
        this.computeIndicators(session, nbMap, percentMap, subprojectId, visibility);

        // Set args for the template
        return getView("indicators")
                .arg("nbMap", nbMap) // TODO : only one map containing the indicatorValue
                .arg("percentMap", percentMap) // TODO : only one map
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    /**
     *
     * @param subprojectId
     * @param visibility
     * @return The indicators packaged in a json structure
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON(@QueryParam("subproject") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        return computeIndicators(session, subprojectId, visibility);
    }

    //private Map<String, Map<String, IndicatorValue>> computeIndicators(String subprojectId, String visibility) throws Exception {
    private Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId, String visibility) throws Exception {
        //CoreSession session = SessionFactory.getSession(request);

        List<IndicatorProvider> computedProviders = new ArrayList<IndicatorProvider>();
        List<IndicatorProvider> pendingProviders = new ArrayList<IndicatorProvider>();
        Map<String, IndicatorValue> computedIndicators = new HashMap<String, IndicatorValue>();
        //Map<String, Map<String, IndicatorValue>> indicatorsByCategory = new HashMap<String, Map<String, IndicatorValue>>();
        HashSet<String> pendingRequiredIndicators = new HashSet<String>(indicatorProviders.size());
        int previousComputedProvidersCount = -1;

        // Compute indicators in several passes, with respect to dependencies
        while (computedProviders.size() != previousComputedProvidersCount) {
            previousComputedProvidersCount = computedProviders.size();

            /*for (Entry<String, List<IndicatorProvider>> indicatorProviderCategory : indicatorProviders.entrySet()) {*/
            // Start or continue indicator category
                /*Map<String, IndicatorValue> categoryIndicators = indicatorsByCategory.get(indicatorProviderCategory.getKey());
             if (categoryIndicators == null) {
             categoryIndicators = new HashMap<String, IndicatorValue>();
             }*/

            // Browse all providers
            //for (IndicatorProvider indicatorProvider : indicatorProviderCategory.getValue()) {
            for (IndicatorProvider indicatorProvider : indicatorProviders) {
                if (!computedProviders.contains(indicatorProvider)) {
                    // Compute indicator only if the dependencies are already computed
                    List<String> requiredIndicators = indicatorProvider.getRequiredIndicators();
                    boolean allRequirementsSatisfied = true;
                    if (requiredIndicators != null) {
                        for (String requiredIndicator : requiredIndicators) {
                            if (!computedIndicators.containsKey(requiredIndicator)) {
                                allRequirementsSatisfied = false;
                                pendingRequiredIndicators.add(requiredIndicator);
                                break;
                            }
                        }
                    }

                    // Actual indicator calculation
                    if (allRequirementsSatisfied) {
                        Map<String, IndicatorValue> indicators = null;
                        try {
                            indicators = indicatorProvider
                                    .computeIndicators(session, subprojectId, computedIndicators, visibility);
                        } catch (Exception e) {
                            logger.warn("Failed to compute indicator '" + indicatorProvider.toString() + "': " + e.getMessage());
                        }
                        if (indicators != null) {
                            //categoryIndicators.putAll(indicators);
                            computedIndicators.putAll(indicators);
                            pendingRequiredIndicators.removeAll(indicators.entrySet()); // just in case there had been required
                        }

                        computedProviders.add(indicatorProvider);
                        pendingProviders.remove(indicatorProvider);
                    } else {
                        pendingProviders.add(indicatorProvider);
                    }
                }
            }
            //indicatorsByCategory.put(indicatorProviderCategory.getKey(), categoryIndicators);
        }
        /*}*/

        // Warn if some indicators have been left pending
        for (IndicatorProvider pendingProvider : pendingProviders) {
            logger.warn(pendingProvider.getClass().getName()
                    + " provider dependencies could not be satisfied ("
                    + pendingProvider.getRequiredIndicators() + ")");
        }
        if (!pendingRequiredIndicators.isEmpty()) {
	        logger.warn("Pending required indicators : " + pendingRequiredIndicators);
        }
        
        //return indicatorsByCategory;
        return computedIndicators;
    }
}
