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

import static org.easysoa.registry.utils.NuxeoListUtils.getProxiedIdLiteralList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.indicators.rest.IndicatorValue;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.rest.EasysoaModuleRoot;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.ContextData;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

/**
 * Indicators
 * 
 * @author mdutoo
 * 
 */
@WebObject(type = "EasySOA")
@Path("easysoa/services")
public class ServiceDocumentationController extends EasysoaModuleRoot {

    /** properties to be displayed in lists of services */
    //private static final String SERVICE_LIST_PROPS = "*"; // "ecm:title" // TODO is this an optimization worth the hassle ??
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ServiceDocumentationController.class);
    
    public ServiceDocumentationController() {
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(/*@DefaultValue(null) */@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);

        String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        
        // getting (phase-scoped) services
        List<DocumentModel> services = docService.getInformationServices(session, subprojectCriteria);
        // getting (phase-scoped) tagging folders
        List<DocumentModel> tags = docService.getByType(session, TaggingFolder.DOCTYPE, subprojectCriteria);
        // getting (phase-scoped) tagged services
        // WARNING : looking for proxies only work when proxy-containing TaggingFolder are in the same Phase as services
        // (else wrong phase id metadata), and using Path only works for live documents !
        ///DocumentModelList serviceProxies = docService.query(session, DocumentService.NXQL_SELECT_FROM
        ///        + InformationService.DOCTYPE + subprojectCriteria
        ///        + DocumentService.NXQL_AND + DocumentService.NXQL_PATH_STARTSWITH
        ///        + RepositoryHelper.getRepositoryPath(session, subprojectId) + TaggingFolder.DOCTYPE + "'", false, true);
        List<DocumentModel> serviceProxies = new ArrayList<DocumentModel>();
        // so rather getting them using getChildren :
        for (DocumentModel tag : tags) {
            DocumentModelList tagChildrenProxies = session.getChildren(tag.getRef());
            serviceProxies.addAll(tagChildrenProxies);
        }
        //DocumentModelList serviceProxyIds = session.query("SELECT " + "ecm:uuid, ecm:parentid" + " FROM "
        //        + Service.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
        //        + DocumentService.NXQL_PATH_STARTSWITH + RepositoryHelper.getRepositoryPath(session, subprojectId) + TaggingFolder.DOCTYPE + "'", false, true);
        
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
            taggedServices.add(session.getSourceDocument(serviceProxyDoc.getRef())); // TODO or by looking in a services map ?
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
        String proxiedServicesIdLiteralList = getProxiedIdLiteralList(session, serviceProxies);
        String proxiedServicesCriteria = proxiedServicesIdLiteralList.length() == 2 ? "" :
            DocumentService.NXQL_AND + " NOT ecm:uuid IN " + proxiedServicesIdLiteralList;
        DocumentModelList untaggedServices = docService.query(session, DocumentService.NXQL_SELECT_FROM
        		+ InformationService.DOCTYPE + subprojectCriteria + proxiedServicesCriteria, true, false);
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("services")
                .arg("services", services)
                .arg("tags", tags)
                .arg("tagId2Services", tagId2Services)
                //.arg("tagId2ServiceNbs", tagId2ServiceNbs)
                .arg("untaggedServices", untaggedServices)
                .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                // see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html
                // and not "new" else conflicts with Nuxeo's NewMethod helper
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
        // services.get(0).getProperty(schemaName, name)
        // services.get(0).getProperty(xpath)
    }
    
    @GET
    @Path("path{serviceSubprojectId:[^:]+}:{serviceName:.+}")
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByPathHTML(
    		@PathParam("serviceSubprojectId") String serviceSubprojectId, @PathParam("serviceName") String serviceName,
            @QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        serviceSubprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, serviceSubprojectId);

        String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        
        DocumentModel service = docService.findSoaNode(session, new SoaNodeId(serviceSubprojectId, InformationService.DOCTYPE, serviceName));

        Template view = getView("servicedoc");
        if (service != null) {
        	String providerActorId = (String) service.getPropertyValue("iserv:providerActor");
        	DocumentModel providerActor = null;
        	if (providerActorId != null && !providerActorId.isEmpty()) {
        		providerActor = session.getDocument(new IdRef(providerActorId));
        	}
        	String componentId = (String) service.getPropertyValue("acomp:componentId");
        	DocumentModel component = null;
        	if (componentId != null && !componentId.isEmpty()) {
        		component = session.getDocument(new IdRef(componentId));
        	}

        	// Implementations
        	// mock impls :
            List<DocumentModel> mockImpls = docService.query(session, DocumentService.NXQL_SELECT_FROM
            		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "='true'", true, false);
            // WARNING IS NULL DOESN'T WORK IN RELEASE BUT DOES IN JUNIT
        	// old impl using proxy :
            //List<DocumentModel> actualImpls = new java.util.ArrayList<DocumentModel>();
            /* = session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + DocumentService.NXQL_WHERE_NO_PROXY
                    + DocumentService.NXQL_AND + "ecm:uuid IN "
                    + getProxiedIdLiteralList(session,
                            session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + DocumentService.NXQL_WHERE_PROXY + DocumentService.NXQL_AND
                    + DocumentService.NXQL_PATH_STARTSWITH + RepositoryHelper.getRepositoryPath(session, subprojectId) + InformationService.DOCTYPE + "'"
                    + DocumentService.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL")));*/
            List<DocumentModel> actualImpls = docService.query(session, DocumentService.NXQL_SELECT_FROM
            		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "<>'true'", true, false); // WARNING 'true' doesn't work in junit
            if (actualImpls.isEmpty()) {
            	// TODO HACK if empty, try using junit-only alternative query, in case we're in tests :
	            actualImpls = docService.query(session, DocumentService.NXQL_SELECT_FROM
	            		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
	                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
	                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL", true, false); // WARNING IS NULL works in junit only
	            // alternate solution using "not mock impl"
                /*actualImpls = docService.query(session, DocumentService.NXQL_SELECT_FROM
                        + ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                        + DocumentService.NXQL_AND + "ecm:uuid NOT IN " + toLiteral(getIds(mockImpls)), true, false);*/ 
            }
            
            // Endpoints
            List<DocumentModel> endpoints = docService.getEndpointsOfService(service, subprojectCriteria);
            DocumentModel productionEndpoint = docService.getEndpointOfService(service, "Production", subprojectCriteria);
            DocumentModel productionImpl = null;
            if (productionEndpoint != null) {
            	productionImpl = docService.getServiceImplementationFromEndpoint(productionEndpoint);
            }
            
            view = view
                    .arg("subproject", serviceSubprojectId)
                    .arg("service", service)
                    .arg("providerActor", providerActor)
                    .arg("component", component)
                    .arg("actualImpls", actualImpls)
                    .arg("mockImpls", mockImpls)
                    .arg("endpoints", endpoints)
                    .arg("productionEndpoint", productionEndpoint)
                    .arg("productionImpl", productionImpl)
                    .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                    // see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html
                    // and not "new" else conflicts with Nuxeo's NewMethod helper
                    .arg("servicee", service.getAdapter(SoaNodeAdapter.class))
                    .arg("subprojectId", subprojectId)
                    .arg("visibility", visibility)
                    .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
        }
        return view;
    }

    @GET
    @Path("tag/path{tagSubprojectId:[^:]+}:{tagName:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByTagHTML(@PathParam("tagSubprojectId") String tagSubprojectId, @PathParam("tagName") String tagName,
            @QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        tagSubprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, tagSubprojectId);

        String subprojectPathCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        
        String query = DocumentService.NXQL_SELECT_FROM
        		+ InformationService.DOCTYPE + DocumentService.NXQL_WHERE
        		+ InformationService.XPATH_PARENTSIDS + "/* = '" + TaggingFolder.DOCTYPE + ":" + tagName + "'"
        		+ subprojectPathCriteria;
        DocumentModelList tagServices = docService.query(session, query, true, false);
        DocumentModel tag = docService.findSoaNode(session, new SoaNodeId(subprojectId, TaggingFolder.DOCTYPE, tagName));
        
        Template view = getView("tagServices");
        return view
                .arg("tag", tag)
                .arg("tagServices", tagServices)
                .arg("subproject", tagSubprojectId)
                .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                // see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html
                // and not "new" else conflicts with Nuxeo's NewMethod helper
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }
    
    @GET
    @Path("path{serviceSubprojectId:[^:]+}:{serviceName:.+}/tags") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetTagsHTML(@PathParam("serviceSubprojectId") String serviceSubprojectId,
    		@PathParam("serviceName") String serviceName,
            @QueryParam("subprojectId") String subprojectId, 
            @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        serviceSubprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, serviceSubprojectId);

        String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        
        //TODO ?? SubprojectID mandatory to find service ....
        DocumentModel service = docService.findSoaNode(session, new SoaNodeId(serviceSubprojectId, InformationService.DOCTYPE, serviceName));
        List<DocumentModel> tags = docService.getByType(session, TaggingFolder.DOCTYPE, subprojectCriteria);
        
        Template view = getView("servicetags");
        // TODO problem here : A freemarker arg cannot be null or absent
        if (service != null) {
            view.arg("service", service);
        }

        return view
                .arg("tags", tags)
                .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                // see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html
                // and not "new" else conflicts with Nuxeo's NewMethod helper
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }
    
    @POST
    @Path("path{serviceSubprojectId:[^:]+}:{serviceName:.+}/tags") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doPostTagsHTML(@PathParam("serviceSubprojectId") String serviceSubprojectId,
    		@PathParam("serviceName") String serviceName, @FormParam("tagId") String tagId,
            @QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        
        serviceSubprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, serviceSubprojectId);

        subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        
        DocumentModel service = docService.findSoaNode(session, new SoaNodeId(serviceSubprojectId, InformationService.DOCTYPE, serviceName));
        DocumentModel tag = session.getDocument(new IdRef(tagId));
        
        if (service != null && tag != null) {
            DocumentService documentService = Framework.getService(DocumentService.class);
            documentService.create(session, documentService.createSoaNodeId(service), tag.getPathAsString());
            session.save();
        }
        return doGetTagsHTML(serviceSubprojectId, serviceName, subprojectId, visibility);
    }
    
    //@DELETE // doesn't work from browser
    @POST
    @Path("proxy/{documentId:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doDeleteProxyHTML(@PathParam("documentId") String documentId, @FormParam("delete") String delete,
    		@QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel serviceProxy = session.getDocument(new IdRef(documentId));
        
        if (serviceProxy != null) {
            DocumentModel proxyParentDocument = session.getParentDocument(serviceProxy.getRef()); // TODO does it work ???
            DocumentModel proxiedService = session.getSourceDocument(serviceProxy.getRef());
            
            session.removeDocument(serviceProxy.getRef());
            session.save();
            
            String subprojectId = (String) proxyParentDocument.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
            String serviceSubprojectId = (String) proxiedService.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
            String serviceName = (String) proxiedService.getPropertyValue(InformationService.XPATH_SOANAME);
            return doGetTagsHTML(serviceSubprojectId, serviceName, subprojectId, visibility); // TODO removing lead slash
        }
        return doGetHTML(null, visibility); //TODO better
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON() throws Exception {
        return null; // TODO
    }
    
    @GET
    @Path("cartography") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetCartographyFullPageHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);        
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("cartography")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));        
    }

    @GET
    @Path("cartography/sourceDiscovery") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetSourceDiscoveryPageHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);        
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("sourceDiscovery")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));        
    }    

    @GET
    @Path("cartography/runDiscovery") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetRunDiscoveryPageHTML(@QueryParam("subprojectId") String subprojectId, @QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);        
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("runDiscovery")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));        
    }    
    
    @GET
    @Path("matchingFull") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetMatchingFullPageHTML(@QueryParam("subprojectId") String subprojectId,
    		@QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);        
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("matchingFull")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }
    
    @GET
    @Path("usage") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetUsagePageHTML(@QueryParam("subprojectId") String subprojectId,
    		@QueryParam("visibility") String visibility) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);        
        
        // Indicators
        IndicatorsController indicatorsController = new IndicatorsController();
        Map<String, IndicatorValue> indicators = indicatorsController.computeIndicators(session, null, null, subprojectId, visibility);
        
        return getView("usage")
                .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("indicators", indicators)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
    }

    @GET
    @Path("governance") // TODO encoding
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
    @Path("governance/prodPhaseMonitoring") // TODO encoding
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
    @Path("governance/governanceIndicators") // TODO encoding
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
