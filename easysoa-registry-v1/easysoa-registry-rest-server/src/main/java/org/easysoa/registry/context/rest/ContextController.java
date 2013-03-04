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

package org.easysoa.registry.context.rest;

import java.util.HashMap;
import java.util.HashSet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Project;
import org.easysoa.registry.types.Subproject;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 *
 * @author jguillemotte
 */
@WebObject(type = "context")
@Path("easysoa/context")
public class ContextController extends ModuleRoot {

    // Logger
    private static Logger logger = Logger.getLogger(ContextController.class);
    
    @GET
    @Produces(MediaType.TEXT_HTML)    
    public Template doGetHtml() throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        Template view = getView("context");

        // Get the projects
        DocumentModelList projectsList = session.query("SELECT * FROM " + Project.DOCTYPE);

        HashMap<String, HashSet<DocumentModel>> projectIdToSubproject = new HashMap<String, HashSet<DocumentModel>>();

        for(DocumentModel project : projectsList){
            HashSet<DocumentModel> subProjects = new HashSet<DocumentModel>();
            DocumentModelList subProjectsList = session.query("SELECT * FROM " + Subproject.DOCTYPE + " WHERE " + DocumentService.NXQL_PATH_STARTSWITH + project.getPathAsString() + "'");
            for(DocumentModel subProject : subProjectsList){
                subProjects.add(subProject);
            }
            projectIdToSubproject.put((String)project.getPropertyValue(Project.XPATH_TITLE), subProjects);
        }
        
        // Pass projects map in the view
        view.arg("projectIdToSubproject", projectIdToSubproject);
        
        return view;
    }

}
