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

package org.easysoa.registry.documentation.rest;

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
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.easysoa.registry.types.ids.SoaNodeId;
import static org.easysoa.registry.utils.NuxeoListUtils.*;
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
 * @author mdutoo
 * 
 */
@WebObject(type = "EasySOA")
@Path("easysoa/services")
public class ServiceDocumentationController extends ModuleRoot {

    private static final String SERVICE_LIST_PROPS = "*"; // "ecm:title"
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ServiceDocumentationController.class);
    
    public ServiceDocumentationController() {
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML() throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModelList services = session.query("SELECT " + SERVICE_LIST_PROPS + " FROM " + InformationService.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY);
        DocumentModelList tags = session.query("SELECT " + "*" + " FROM " + TaggingFolder.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY);
        DocumentModelList serviceProxies = session.query("SELECT " + "*" + " FROM " + InformationService.DOCTYPE + IndicatorProvider.NXQL_WHERE_PROXY
                + IndicatorProvider.NXQL_AND + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/" + TaggingFolder.DOCTYPE + "'");
        //DocumentModelList serviceProxyIds = session.query("SELECT " + "ecm:uuid, ecm:parentid" + " FROM " + Service.DOCTYPE + IndicatorProvider.NXQL_WHERE_PROXY
        //        + IndicatorProvider.NXQL_AND + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository" + TaggingFolder.DOCTYPE + "'");
        
        // TODO id to group / aggregate, use... http://stackoverflow.com/questions/5023743/does-guava-have-an-equivalent-to-pythons-reduce-function
        // collection utils :
        // TreeMap(Comparator) http://docs.oracle.com/javase/6/docs/api/java/util/TreeMap.html#TreeMap%28java.util.Comparator%29
        // google collections Maps : uniqueIndex, transformValues (multimaps) http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained
        // custom code in this spirit http://code.google.com/p/guava-libraries/issues/detail?id=546
        // (NB. guava itself brings nothing more)
        // or more functional stuff :
        // jedi http://jedi.codehaus.org/Examples
        // lambdaj : count, group (& filter, join) http://lambdaj.googlecode.com/svn/trunk/html/apidocs/ch/lambdaj/Lambda.html
        // (java)script in java (ex. Rhino)
        // or more SQL-like stuff ex. josql http://josql.sourceforge.net/
        // (or true olap)

        HashMap<String, HashSet<DocumentModel>> tagId2Services = new HashMap<String, HashSet<DocumentModel>>();
        for (DocumentModel serviceProxyDoc : serviceProxies) {
            String serviceProxyParentId = (String) serviceProxyDoc.getParentRef().reference();
            HashSet<DocumentModel> taggedServices = tagId2Services.get(serviceProxyParentId);
            if (taggedServices == null) {
                taggedServices = new HashSet<DocumentModel>();
                tagId2Services.put(serviceProxyParentId, taggedServices);
            }
            // unwrapping proxy
            taggedServices.add(session.getWorkingCopy(serviceProxyDoc.getRef())); // TODO or by looking in a services map ?
        }
        
        /*HashMap<String, Integer> tagId2ServiceNbs = new HashMap<String, Integer>();
        for (DocumentModel serviceProxyIdDoc : serviceProxyIds) {
            String serviceProxyParentId = (String) serviceProxyIdDoc.getParentRef().reference();
            Integer serviceProxyNb = tagId2ServiceNbs.get(serviceProxyParentId);
            if (serviceProxyNb == null) {
                serviceProxyNb = 0;
            } else {
                serviceProxyNb++;
            }
            tagId2ServiceNbs.put(serviceProxyParentId, serviceProxyNb);
        }*/
        DocumentModelList untaggedServices = session.query("SELECT " + SERVICE_LIST_PROPS + " FROM " + InformationService.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY
                + IndicatorProvider.NXQL_AND + " NOT ecm:uuid IN " + getProxiedIdLiteralList(session, serviceProxies));
        
        return getView("services")
                .arg("services", services)
                .arg("tags", tags)
                .arg("tagId2Services", tagId2Services)
                //.arg("tagId2ServiceNbs", tagId2ServiceNbs)
                .arg("untaggedServices", untaggedServices);
        // services.get(0).getProperty(schemaName, name)
        // services.get(0).getProperty(xpath)
    }
    
    @GET
    @Path("path/{serviceName:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByPathHTML(@PathParam("serviceName") String serviceName) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModel service = docService.find(session, new SoaNodeId(InformationService.DOCTYPE, serviceName));
        
        Template view = getView("servicedoc");
        if (service != null) {
            // WARNING IS NULL DOESN'T WORK IN RELEASE BUT IN JUNIT OK
            List<DocumentModel> actualImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY
                    + IndicatorProvider.NXQL_AND + "ecm:uuid IN "
                    + getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/" + InformationService.DOCTYPE + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL"))); // WARNING use IS NULL instead of !='true'
            List<DocumentModel> mockImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid IN "
                    + getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/" + InformationService.DOCTYPE + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "='true'")));
            actualImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY
                    + IndicatorProvider.NXQL_AND + "ecm:uuid NOT IN "
                    + toLiteral(getIds(mockImpls))); // WARNING use IS NULL instead of !='true'
            view = view
                    .arg("service", service)
                    .arg("actualImpls", actualImpls)
                    .arg("mockImpls", mockImpls)
                    .arg("servicee", service.getAdapter(SoaNodeAdapter.class));
        }
        return view; 
    }

    @GET
    @Path("tag/{tagName:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByTagHTML(@PathParam("tagName") String tagName) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        String query = IndicatorProvider.NXQL_SELECT_FROM
        		+ InformationService.DOCTYPE + IndicatorProvider.NXQL_WHERE
        		+ InformationService.XPATH_PARENTSIDS + "/* = '" + TaggingFolder.DOCTYPE + ":" + tagName + "'";
        DocumentModelList tagServices = docService.query(session, query, true, false);
        
        Template view = getView("tagServices");
        return view
                .arg("tag", docService.find(session, new SoaNodeId(TaggingFolder.DOCTYPE, tagName)))
                .arg("tagServices", tagServices); 
    }
    
    @GET
    @Path("{serviceName:.+}/tags") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetTagsHTML(@PathParam("serviceName") String serviceName) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModel service = docService.find(session, new SoaNodeId(InformationService.DOCTYPE, serviceName));
        DocumentModelList tags = session.query("SELECT " + "*" + " FROM " + TaggingFolder.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY);
        
        Template view = getView("servicetags");
        if (service != null) {
            view.arg("service", service);
        }
        return view
                .arg("tags", tags); 
    }
    
    @POST
    @Path("{serviceName:.+}/tags") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doPostTagsHTML(@PathParam("serviceName") String serviceName, @FormParam("tagId") String tagId) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        DocumentModel service = docService.find(session, new SoaNodeId(InformationService.DOCTYPE, serviceName));
        DocumentModel tag = session.getDocument(new IdRef(tagId));
        
        if (service != null && tag != null) {
            DocumentService documentService = Framework.getService(DocumentService.class);
            documentService.create(session, documentService.createSoaNodeId(service), tag.getPathAsString());
            session.save();
        }
        return doGetTagsHTML(serviceName);
    }
    
    //@DELETE // doesn't work from browser
    @POST
    @Path("proxy/{documentId:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doDeleteProxyHTML(@PathParam("documentId") String documentId, @FormParam("delete") String delete) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel serviceProxy = session.getDocument(new IdRef(documentId));
        
        if (serviceProxy != null) {
            DocumentModel proxiedService = session.getWorkingCopy(serviceProxy.getRef());
            session.removeDocument(serviceProxy.getRef());
            session.save();
            return doGetTagsHTML((String) proxiedService.getPropertyValue(InformationService.XPATH_SOANAME)); // removing lead slash
        }
        return doGetHTML(); //TODO better
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON() throws Exception {
        return null; // TODO
    }
    
}
