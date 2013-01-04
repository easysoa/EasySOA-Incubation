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

package org.easysoa.registry.monitoring.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.indicators.rest.IndicatorProvider;
import org.easysoa.registry.indicators.rest.SoftwareComponentIndicatorProvider;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * Indicators
 * 
 * impl : TODO see ServiceDocumentationController, MatchingDashboard...
 * 
 * @author mdutoo, jguillemotte
 * 
 */
@WebObject(type = "EasySOA")
@Path("easysoa/monitoring")
public class ServiceDocumentationController extends ModuleRoot {

    private static final String SERVICE_LIST_PROPS = "*"; // "ecm:title"
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ServiceDocumentationController.class);
    
    /**
     * Default constructor
     */
    public ServiceDocumentationController() {
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML() throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        List<String> envs = new ArrayList<String>();

        // Get the environments
        DocumentModelList environments = session.query("SELECT DISTINCT " + Endpoint.XPATH_ENDP_ENVIRONMENT + " FROM " + Endpoint.DOCTYPE);
        // Fill the envs list
        for(DocumentModel model : environments){
            envs.add((String)model.getPropertyValue(Endpoint.XPATH_ENDP_ENVIRONMENT));
        }
        return getView("dashboard") // TODO see services.ftl, dashboard/*.ftl...
                .arg("envs", envs); // TODO later by (sub)project
    }
    
    @GET
    @Path("env/{envName:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByPathHTML(@PathParam("envName") String envName) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        // Check there is at least on environment
        if(envName == null || "".equals(envName)){
            throw new IllegalArgumentException("At least one environment must be specified");
        }
        
        Template view = getView("envIndicators"); // TODO see services.ftl, dashboard/*.ftl...
        
        // Get the enpoints associated with the environment
        DocumentModelList endpointsModel = session.query("SELECT * FROM " + Endpoint.DOCTYPE + " WHERE " + Endpoint.XPATH_ENDP_ENVIRONMENT + " = " + envName);
        
        for(DocumentModel endpointModel : endpointsModel){
            
        }
        
        List<DocumentModel> endpoints = null;

        
        if (endpoints != null) {
            view = view.arg("endpoints", endpoints);
        }
        return view; 
    }
    
}