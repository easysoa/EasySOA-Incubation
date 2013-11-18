/**
 * EasySOA Registry
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

package org.easysoa.registry.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.soapui.SoapUIWSDL;
import org.easysoa.registry.soapui.SoapUIWSDLFactory;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.utils.ContextData;
import org.easysoa.registry.wsdl.WsdlBlob;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author mkalam-alami
 */
@WebObject(type = "soapui")
@Path("easysoa/soapui")
public class SoapUIConfRest extends EasysoaModuleRoot {

    private static final Log log = LogFactory.getLog(SoapUIConfRest.class);

    /**
     *
     * @param request
     * @param docId
     * @return
     * @throws Exception
     */
    @GET
    @Path("/{id}")
    //@Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Produces("application/xml+soapui")
    public Object doGetSoapUIConf(@Context HttpServletRequest request,
            @PathParam("id") String docId) throws Exception {

        docId = docId.replace(".xml", "");

        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        List<SoapUIWSDL> wsdls = new ArrayList<SoapUIWSDL>();
        DocumentModel requestedDocumentModel = session.getDocument(new IdRef(docId)); // Get an InteligentSystem

        // Get context
        ContextData data = ContextData.getVersionData(session, (String)requestedDocumentModel.getPropertyValue(Subproject.XPATH_SUBPROJECT));
        // Get the environment
        String environment = getEnvironment(requestedDocumentModel);

        // Set project name
        StringBuilder soapUIProjectName = new StringBuilder();
        soapUIProjectName.append(data.getProject());
        soapUIProjectName.append(" / ");
        soapUIProjectName.append(data.getPhase());
        if(!"".equals(environment)){
            soapUIProjectName.append(" / ");
            soapUIProjectName.append(environment);
        }

        // Set up params (= WSDLs)
        SoapUIWSDLFactory wsdlFactory = new SoapUIWSDLFactory();
        // TODO : add a test to check if we are on an endpoint or on an its
        // If endpoint, get the parent its for the environment
        if(Endpoint.DOCTYPE.equals(requestedDocumentModel.getType())){
            addWdsl(requestedDocumentModel, wsdlFactory, wsdls);
        }
        // Assuming the requested document is a InteligentSystemTree
        else {
            DocumentModelList serviceModels = docService.getChildren(session, requestedDocumentModel.getRef(), Endpoint.DOCTYPE);
            for (DocumentModel documentModel : serviceModels) {
                addWdsl(documentModel, wsdlFactory, wsdls);
            }
        }

        // Render
        Template view = getView("soapuiconf");
        view.arg("wsdls", wsdls);
        view.arg("projectName", soapUIProjectName.toString());
        return view;
    }

    /**
     *
     * @param document
     * @return
     * @throws Exception
     */
    private String getEnvironment(DocumentModel document) throws Exception {
        // Environement is only available for Endpoints and EndpointConsumptions
        if(Endpoint.DOCTYPE.equals(document.getType())){
            return (String)document.getPropertyValue(Endpoint.XPATH_ENDP_ENVIRONMENT);
        } else if(EndpointConsumption.DOCTYPE.equals(document.getType())) {
            return (String)document.getPropertyValue(EndpointConsumption.XPATH_CONSUMED_ENVIRONMENT);
        }
        return "";
    }

    /**
     *
     * @param documentModel
     * @param wsdlFactory
     * @param wsdls
     * @throws Exception
     */
    private void addWdsl(DocumentModel documentModel, SoapUIWSDLFactory wsdlFactory, List<SoapUIWSDL> wsdls) throws Exception {
        WsdlBlob wsdlBlob = new WsdlBlob(documentModel);
        File tmpWsdlFile = File.createTempFile(documentModel.getTitle().replaceAll("/", "_"), "wsdl");
        Blob blob = wsdlBlob.getBlob();
        if (blob != null) {
            blob.transferTo(tmpWsdlFile);
            SoapUIWSDL newWSDL = wsdlFactory.create(tmpWsdlFile, (String) documentModel.getPropertyValue(Endpoint.XPATH_URL));
            wsdls.add(newWSDL);
        } // else no attached WSDL
    }

}