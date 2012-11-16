package org.easysoa.registry.integration;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * Simple service registry implementation
 * 
 * @author jguillemotte
 *
 */
@Path("easysoa/simpleRegistryService")
public class SimpleRegistryServiceImpl implements SimpleRegistryService {

    @Context HttpServletRequest request;    
    
    @Override
    public SoaNodeInformation[] queryWSDLInterfaces(String search, String subProjectId) throws Exception {

        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        // Fetch SoaNode list
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM InformationService ",
                //+ "WHERE ecm:currentLifeCycleState <> 'deleted' AND "
                //+ "ecm:isCheckedInVersion = 0 AND " + "ecm:isProxy = 0",
                new Object[0], false, true);
        //{ "informationservice" }
        DocumentModelList soaNodeModelList = documentManager.query(query);        
        
        // Convert data for marshalling
        List<SoaNodeInformation> modelsToMarshall = new LinkedList<SoaNodeInformation>();
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            modelsToMarshall.add(new SoaNodeInformation(documentService.createSoaNodeId(soaNodeModel),
                    null, null));
        }

        // Write response
        return modelsToMarshall.toArray(new SoaNodeInformation[]{});        
    }

}
