package org.easysoa.registry.integration;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.easysoa.registry.rest.marshalling.WSDLInformation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.WsdlInfo;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;

/**
 * Simple service registry implementation
 * 
 * @author jguillemotte
 *
 */
@Path("easysoa/simpleRegistryService")
public class SimpleRegistryServiceImpl implements SimpleRegistryService {

    //TODO : How to get nuxeo base url  
    public final static String NUXEO_BASE_URL = "http://localhost:8080/nuxeo/";
    
    /**
     * 
     */
    @Context HttpServletRequest request;    
    
    /**
     * 
     */
    @Override
    public WSDLInformation[] queryWSDLInterfaces(String search, String subProjectId, String platformServiceStandard, String wsBindingTransport, String platformId) throws Exception {
        
        CoreSession documentManager = SessionFactory.getSession(request);

        ArrayList<String> parameters = new ArrayList<String>();
        StringBuffer query = new StringBuffer(); 
        query.append("SELECT * FROM InformationService ");
        // Search parameter
        if(search != null && !"".equals(search)){
            // TODO ecm:name (NOT FOUND !!), SoaNode.XPATH_SOANAME (soan:name), WsdlInfo.XPATH_WSDL_PORTTYPE_NAME
            String searchParam = "%"+search+"%";
            query.append("WHERE dc:title like '?' " +
                    "OR " + SoaNode.XPATH_SOANAME + " like '?' " +
                            "OR dc:description like '?' " +
                                    "OR " + WsdlInfo.XPATH_WSDL_PORTTYPE_NAME + " like '?'");
            parameters.add(searchParam);
            parameters.add(searchParam);
            parameters.add(searchParam);
            parameters.add(searchParam);
        }
        // Project ID parameter
        if(subProjectId != null && !"".equals(subProjectId)){
            // TODO : not implemented yet in Nuxeo registry
            //[and projectId=appGivenProjectId || projectId in (allProjectIdsReferredByFraSCAtiStudioProject)]
        }
        
        // Platform parameter
        if(platformId != null && !"".equals(platformId)){
            // TODO Add implementation for condition, would allow to search only in its own services
            // what acts as platformId for : FraSCAti Studio (base url and not Cloud(s) url), Talend (talend repository url ?)
            // for now only platform:deliverableRepositoryUrl exists that could act as platform id, later maybe :
            // [and is/impl:platformId='fraSCAtiStudioPlatformId']
        }

        // Platform service standard parameter
        if(platformServiceStandard != null && !"".equals(platformServiceStandard)){
            // LATER Add doc model & implementation for condition (FraSCAti & CXF only support WSDL11), something like :
            // platform:wsdlVersion / WsdlInfo.XPATH_WSDL_VERSION='1.1'            
        }        
        
        // WS binding transport(_type) parameter
        if(wsBindingTransport != null && !"".equals(wsBindingTransport)){
            // LATER Add doc model & implementation for condition (for now only HTTP), something like :
            // [and platform:serviceTransport or endpointServiceTransport='http://schemas.xmlsoap.org/soap/http']
        }
        
        // LATER other platform parameters (see ServiceMatchingListener.findIServicesForImpl()) ?

        // Execute query
        String nxqlQuery = NXQLQueryBuilder.getQuery(query.toString(), parameters.toArray(), false, true);
        DocumentModelList soaNodeModelList = documentManager.query(nxqlQuery);        
        
        // Write response
        WSDLInformation[] wsdlInformations = new WSDLInformation[soaNodeModelList.size()];
        int index = 0;
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            wsdlInformations[index] = SoaNodeInformationToWSDLInformationMapper.mapToWSDLInformation(soaNodeModel, NUXEO_BASE_URL);
            index++;
        }
        return wsdlInformations;
    }

    /**
     * 
     */
    @Override
    public WSDLInformation[] queryEndpoints(String search, String subProjectId) throws Exception {

        CoreSession documentManager = SessionFactory.getSession(request);

        // Fetch SoaNode list
        ArrayList<String> parameters = new ArrayList<String>(); 
        StringBuffer query = new StringBuffer(); 
        query.append("SELECT * FROM Endpoint ");
 
        // Search parameter
        if(search != null && !"".equals(search)){
            String searchParam = "%"+search+"%";
            query.append("WHERE dc:title like '?' " +
                    "OR " + SoaNode.XPATH_SOANAME + " like '?' " +
                            "OR dc:description like '?' " +
                                    "OR " + WsdlInfo.XPATH_WSDL_PORTTYPE_NAME + " like '?'");
            parameters.add(searchParam);
            parameters.add(searchParam);
            parameters.add(searchParam);
            parameters.add(searchParam);
        }
        
        String nxqlQuery = NXQLQueryBuilder.getQuery(query.toString(), parameters.toArray(), false, true);
        DocumentModelList soaNodeModelList = documentManager.query(nxqlQuery);        
        
        // Write response
        WSDLInformation[] wsdlInformations = new WSDLInformation[soaNodeModelList.size()];
        int index = 0;
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            wsdlInformations[index] = SoaNodeInformationToWSDLInformationMapper.mapToWSDLInformation(soaNodeModel, NUXEO_BASE_URL);
            index++;
        }
        return wsdlInformations;
    }

}
