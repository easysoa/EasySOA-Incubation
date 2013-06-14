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

package org.easysoa.registry.governance.rest;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.easysoa.registry.indicators.rest.IndicatorValue;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.rest.EasysoaModuleRoot;
import org.easysoa.registry.utils.ContextData;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;

/**
 * Governance controller
 *
 * @author jguillemotte
 */
@WebObject(type = "governance")
@Path("easysoa/governance")
public class GovernanceController extends EasysoaModuleRoot {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetGovernancePageHTML(@QueryParam("subprojectId") String subprojectId,
    		@QueryParam("visibility") String visibility) throws Exception {

        CoreSession session = SessionFactory.getSession(request);

        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);

        return getView("governance")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    @GET
    @Path("prodPhaseMonitoring") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetProdPhaseMonitoringPageHTML(@QueryParam("subprojectId") String subprojectId,
    		@QueryParam("visibility") String visibility) throws Exception {

        CoreSession session = SessionFactory.getSession(request);

        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);

        return getView("prodPhaseMonitoring")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    @GET
    @Path("governanceIndicators") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetGovernanceIndicatorsPageHTML(@QueryParam("subprojectId") String subprojectId,
    		@QueryParam("visibility") String visibility) throws Exception {

        CoreSession session = SessionFactory.getSession(request);

        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);

        return getView("governanceIndicators")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

}
