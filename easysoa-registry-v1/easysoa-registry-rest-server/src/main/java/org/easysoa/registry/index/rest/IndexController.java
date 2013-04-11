/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.registry.index.rest;

import java.security.Principal;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.easysoa.registry.indicators.rest.IndicatorValue;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.utils.ContextData;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * Controller for index view
 * @author jguillemotte
 */
@WebObject(type = "EasySOA")
@Path("easysoa")
public class IndexController extends ModuleRoot {

    private static Logger logger = Logger.getLogger(IndexController.class);
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);

        // Compute indicators
        IndicatorsController indicatorController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorController.computeIndicators(session, null, null, subprojectId, visibility);
        
        // Return the index view
        return getView("index")
           .arg("indicators", indicators)
           .arg("subprojectId", subprojectId)
           .arg("visibility", visibility)
           .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    /**
     * 
     * @return The current user name
     * @throws Exception 
     */
    public String getCurrentUser() throws Exception {
        String user = "";
        
        CoreSession session = SessionFactory.getSession(request);
        Principal currentUser = session.getPrincipal();
        
        if(currentUser != null){
            user = currentUser.getName();
        }
        return user;
    }

    
}
