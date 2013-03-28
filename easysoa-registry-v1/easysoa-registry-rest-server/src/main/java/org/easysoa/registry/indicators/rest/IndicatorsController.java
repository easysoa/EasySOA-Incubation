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
import java.util.HashMap;
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
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 * Indicators
 * 
 * @author mdutoo
 * 
 */
//XXX Slightly outdated after removal of Service doctype
@WebObject(type = "EasySOA")
@Path("easysoa/indicators") // TODO move the indicators page in easysoa/indicators
//@Path("easysoa") // TODO move the indicators page in easysoa/indicators
// TODO Add a new controller for index page
public class IndicatorsController extends ModuleRoot {

    private static Logger logger = Logger.getLogger(IndicatorsController.class);
    
    // XXX Categories are currently unused by view
    //public static final String CATEGORY_DOCTYPE_COUNTS = "Doctype counts";
    //public static final String CATEGORY_DOCTYPE_SPECIFIC = "Doctype-specific indicators";
    //public static final String CATEGORY_MISC = "Miscellaneous";

    // New category for indicators
    public static final String CATEGORY_STEERING = "steering";
    public static final String CATEGORY_CARTOGRAPHY = "cartography";
    public static final String CATEGORY_USAGE = "usage";
    public static final String CATEGORY_MATCHING = "matching";
            
    //private Map<String, List<IndicatorProvider>> indicatorProviders = new HashMap<String, List<IndicatorProvider>>();
    private List<IndicatorProvider> indicatorProviders = new ArrayList<IndicatorProvider>();
    
    public IndicatorsController() {
        // Document count by type
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(SoaNode.ABSTRACT_DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(InformationService.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(SoftwareComponent.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(ServiceImplementation.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(Deliverable.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(DeployedDeliverable.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(Endpoint.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(EndpointConsumption.DOCTYPE, CATEGORY_CARTOGRAPHY));
        addIndicator(/*CATEGORY_DOCTYPE_COUNTS,*/ new DoctypeCountProvider(TaggingFolder.DOCTYPE, CATEGORY_CARTOGRAPHY));
        
        // Doctype-specific indicators
        //addIndicator(CATEGORY_DOCTYPE_SPECIFIC, new ServiceStateProvider()); // Disabled because outdated (see Comment in ServiceStateProvider.class)
        addIndicator(/*CATEGORY_DOCTYPE_SPECIFIC,*/ new ServiceImplStateProvider(CATEGORY_CARTOGRAPHY));
        //addIndicator(CATEGORY_DOCTYPE_SPECIFIC, new SoftwareComponentIndicatorProvider()); // Disabled, need to be updated
        addIndicator(/*CATEGORY_DOCTYPE_SPECIFIC,*/ new TagsIndicatorProvider(CATEGORY_STEERING));
        addIndicator(/*CATEGORY_DOCTYPE_SPECIFIC,*/ new ServiceConsumptionIndicatorProvider(CATEGORY_USAGE));

        // Miscellaneous indicators
        addIndicator(/*CATEGORY_MISC,*/ new PlaceholdersIndicatorProvider(CATEGORY_MATCHING));
        
    }
    
    public void addIndicator(/*String category,*/ IndicatorProvider indicator) {
        /*if (!indicatorProviders.containsKey(category)) {
            indicatorProviders.put(category, new ArrayList<IndicatorProvider>());
        }
        indicatorProviders.get(category).add(indicator);*/
        indicatorProviders.add(indicator);
    }
    
    // TODO : add a mthod to compute indicators and to be callable from another controller
    public void computeIndicators(HashMap<String, Integer> nbMap, HashMap<String, Integer> percentMap,  String subprojectId, String visibility) throws Exception{
        
        // using all subprojects or getting default one is let to indicator impls TODO better
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        /*String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            subprojectPathCriteria = " " + IndicatorProvider.NXQL_PATH_STARTSWITH + session.getDocument(new IdRef(subprojectId)).getPathAsString() + "'";
        }*/
        
        if("".equals(subprojectId)){
            subprojectId = null;
        }
        
        //Map<String, Map<String, IndicatorValue>> indicatorsByCategory = computeIndicators(subprojectId, visibility);
        Map<String, IndicatorValue> indicators = computeIndicators(subprojectId, visibility);
        
        // Create and return view
        if(nbMap == null){
            nbMap = new HashMap<String, Integer>();
        }
        if(percentMap == null){
            percentMap = new HashMap<String, Integer>();
        }
        
        //for (Map<String, IndicatorValue> indicatorCategory : indicatorsByCategory.values()) {
            //for (Entry<String, IndicatorValue> indicator : indicatorCategory.entrySet()) {
            for (Entry<String, IndicatorValue> indicator : indicators.entrySet()) {
                if (indicator.getValue().getCount() != -1) {
                    nbMap.put(indicator.getKey(), indicator.getValue().getCount());
                }
                if (indicator.getValue().getPercentage() != -1) {
                    percentMap.put(indicator.getKey(), indicator.getValue().getPercentage());
                }
            }
        //}        
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(/*@DefaultValue(null) */@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);

        // Init maps
        HashMap<String, Integer> nbMap = new HashMap<String, Integer>();
        HashMap<String, Integer> percentMap = new HashMap<String, Integer>();
        
        // Compute the indicators
        this.computeIndicators(nbMap, percentMap, subprojectId, visibility);
        
        // Set args for the template
        return getView("indicators")
        //return getView("index")
                .arg("nbMap", nbMap)
                .arg("percentMap", percentMap)
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        return computeIndicators(subprojectId, visibility);
    }
    
    //private Map<String, Map<String, IndicatorValue>> computeIndicators(String subprojectId, String visibility) throws Exception {
    private Map<String, IndicatorValue> computeIndicators(String subprojectId, String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        List<IndicatorProvider> computedProviders = new ArrayList<IndicatorProvider>();
        List<IndicatorProvider> pendingProviders = new ArrayList<IndicatorProvider>();
        Map<String, IndicatorValue> computedIndicators = new HashMap<String, IndicatorValue>();
        //Map<String, Map<String, IndicatorValue>> indicatorsByCategory = new HashMap<String, Map<String, IndicatorValue>>();
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
                            }
                            catch (Exception e) {
                                logger.warn("Failed to compute indicator '" + indicatorProvider.toString() + "': " + e.getMessage());
                            }
                            if (indicators != null) {
                                //categoryIndicators.putAll(indicators);
                                computedIndicators.putAll(indicators);
                            }
                            
                            computedProviders.add(indicatorProvider);
                            pendingProviders.remove(indicatorProvider);
                        }
                        else {
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
        
        //return indicatorsByCategory;
        return computedIndicators;
    }

}
