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

package org.easysoa.registry.tools.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.easysoa.registry.rest.EasysoaModuleRoot;
import org.easysoa.registry.utils.ContextData;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;

/**
 *
 * @author jguillemotte
 */
@WebObject(type = "tools")
@Path("easysoa/tools")
public class ToolsController extends EasysoaModuleRoot {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ToolsController.class);

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {

        CoreSession session = SessionFactory.getSession(request);
        //DocumentService docService = Framework.getService(DocumentService.class);
        //String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);

        return getView("tools")
            .arg("subprojectId", subprojectId)
            .arg("visibility", visibility)
            .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    @GET
    @Path("apis") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetApisPageHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {

        CoreSession session = SessionFactory.getSession(request);
        //DocumentService docService = Framework.getService(DocumentService.class);
        //String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);

        return getView("apis")
            .arg("subprojectId", subprojectId)
            .arg("visibility", visibility)
            .arg("contextInfo", ContextData.getVersionData(session, subprojectId));

    }

}
