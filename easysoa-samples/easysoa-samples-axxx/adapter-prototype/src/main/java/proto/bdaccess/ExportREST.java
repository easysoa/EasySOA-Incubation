package proto.bdaccess;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.integration.EndpointStateService;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * A class that can connect to EasySOA core and export data. 
 * see: https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-registry-v1/easysoa-registry-rest-server/src/test/java/org/easysoa/registry/integration/EndpointStateServiceTest.java
 */
public class ExportREST {
	
	// todo: not resolved
    private String url = "http://localhost:8080/nuxeo";
    private String username = "Administrator";
    private String password = "Administrator";

    private String endpointStateServiceUrl; // = url + "/site/easysoa/endpointStateService";
    
    
    /**
     * endpointId : url of the service: the provider field of the json config file.
     * 
     * exp: "provider":"owsi-vm-easysoa-axxx-pivotal.accelance.net".
     */
    public final static String ENDPOINT_ID = "test";
    
    public final static String INDICATOR_NAME = "testSlaIndicator";
    
    
    
	/**
	 * Export data to EasySOA (REST).
	 * 
	 * 
	 */
	public Boolean exportData(Date dateFrom, Date dateTo, String endPointId, String slaOrOlaName, String level) throws Exception {
		
		if (this.client == null) {
		    initClients(url, username, password);
		    this.endpointStateServiceUrl = url + "/site/easysoa/endpointStateService";
		}
	     	    	    
	    // Run update test request
        WebResource createUpdateRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators");
        
        SlaOrOlaIndicators slaOrOlaIndicatorsCreate = new SlaOrOlaIndicators();
        SlaOrOlaIndicator indicatorCreate = new SlaOrOlaIndicator();
        
        indicatorCreate.setEndpointId(endPointId);
        
        indicatorCreate.setSlaOrOlaName(slaOrOlaName);
        
        ServiceLevelHealth srvLevelHealth=ServiceLevelHealth.bronze;
        boolean serviceLevelViolation = false;

        if(level.equalsIgnoreCase("silver")) {
        	srvLevelHealth=ServiceLevelHealth.silver;
        } else if(level.equalsIgnoreCase("gold")) { 
        	srvLevelHealth=ServiceLevelHealth.gold;
        } else { //  if(level.equalsIgnoreCase("bronze")) {
        	//srvLevelHealth=ServiceLevelHealth.bronze;
            serviceLevelViolation = true;
        }
        
        indicatorCreate.setServiceLevelHealth(srvLevelHealth);
        indicatorCreate.setServiceLevelViolation(serviceLevelViolation);
        
        GregorianCalendar calendar = new GregorianCalendar(); // now
        indicatorCreate.setTimestamp(calendar.getTime());
        slaOrOlaIndicatorsCreate.getSlaOrOlaIndicatorList().add(indicatorCreate);
        createUpdateRequest.post(slaOrOlaIndicatorsCreate);
                        
		return true;			
	}
	
	
	

    

    
    
    private RegistryApi registryApi;
    private Session session;
    private SimpleRegistryService simpleRegistryService;
    private EndpointStateService endpointStateService;

    private ClientConfig clientConfig;
    private Client client;

    public String getIdRef(SoaNodeId soaNodeId) throws Exception {
        Documents info = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + soaNodeId.getType() + 
                        " WHERE soan:name = '" + soaNodeId.getName() + "'" +
                        " AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0").execute();
        if (info != null && !info.isEmpty()) {
            return info.get(0).getId();
        }
        return null;
    }

    public String getPath(SoaNodeId soaNodeId) throws Exception {
        Documents info = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + soaNodeId.getType() + 
                        " WHERE soan:name = '" + soaNodeId.getName() + "'" +
                        " AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0").execute();
        if (info != null && !info.isEmpty()) {
            return info.get(0).getPath();
        }
        return null;
    }
    
    public String publish(String fromPath, String toParentPath) throws Exception {
        Document document = (Document) session.newRequest("Document.Publish")
             .setInput(getDocByPath(fromPath))
             .set("target", getDocByPath(toParentPath))
             .execute();
        return document.getPath();
    }
    
    private Document getDocByPath(String path) throws Exception {
        Documents docs = (Documents) session
                .newRequest("Document.Query").setHeader("X-NXDocumentProperties", "*")
                .set("query", "SELECT * FROM Document WHERE ecm:path = '" + path + "'").execute();
        if (docs != null && !docs.isEmpty()) {
            return docs.get(0);
        }
        else {
            return null;
        }
    }
    
    private Document getSubproject(String subprojectId) throws Exception {
        Documents docs = (Documents) session
                .newRequest("Document.Query").setHeader("X-NXDocumentProperties", "*")
                .set("query", "SELECT * FROM Subproject WHERE spnode:subproject = '" + subprojectId + "'").execute();
        if (docs != null && !docs.isEmpty()) {
            return docs.get(0);
        }
        else {
            return null;
        }
    }

    public String createDocument(String doctype, String title, String path, String... properties) throws Exception {
        if (path == null) {
            throw new Exception("Path is null");
        }
        
        // returns existing one if already exists (to allow for several steps)
        Document parentDocument = getDocByPath(path);
        Documents existingDocuments = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + doctype + " WHERE ecm:parentId = '" + parentDocument.getId()
                        + "' AND dc:title='" + title + "'").execute();
        if (existingDocuments != null && !existingDocuments.isEmpty()) {
            return existingDocuments.get(0).getPath();
        }
        
        OperationRequest request = session.newRequest("Document.Create")
                .setInput(parentDocument).set("type", doctype)
                .set("name", title);
        StringBuilder propertiesString = new StringBuilder("dc:title=" + title);
        for (String property : properties) {
            propertiesString.append('\n' + property);
        }
        request.set("properties", propertiesString);
        Document createdDocument = (Document) request.execute();
        return createdDocument.getPath();
    }

    private String createSoaNode(SoaNodeId soaNodeId) throws Exception {
        return createSoaNode(soaNodeId, (String) null);
    }
    
    public String createSoaNode(SoaNodeId soaNodeId, String parentPath, String... properties) throws Exception {
        SoaNodeInformation soaNode = new SoaNodeInformation(soaNodeId, null, null);
        if (parentPath != null) {
            soaNode.addParentDocument(new SoaNodeId(null, parentPath));
        }
        for (String property : properties) {
            String[] splitProperty = property.split("=");
            soaNode.setProperty(splitProperty[0], splitProperty[1]);
        }
        registryApi.post(soaNode);
        return getPath(soaNodeId);
    }
    
    public String createSoaNode(SoaNodeId soaNodeId, SoaNodeId parentSoaNode, String... properties) throws Exception {
        SoaNodeInformation soaNode = new SoaNodeInformation(soaNodeId, null, null);
        if (parentSoaNode != null) {
            soaNode.addParentDocument(parentSoaNode);
        }
        for (String property : properties) {
            String[] splitProperty = property.split("=");
            soaNode.setProperty(splitProperty[0], splitProperty[1]);
        }
        registryApi.post(soaNode);
        return getPath(soaNodeId);
    }

    public void uploadWsdl(String path, String wsdlFilePath) throws Exception {
        // see http://doc.nuxeo.com/display/NXDOC/Using+Nuxeo+Automation+Client
        FileBlob wsdlBlob = new FileBlob(new File(wsdlFilePath));
        wsdlBlob.setMimeType("text/xml");
        session.newRequest("Blob.Attach").setHeader(
                Constants.HEADER_NX_VOIDOP, "true").setInput(wsdlBlob) // HEADER_NX_VOIDOP => will return null
                .set("document", path).execute();
    }

    public void delete(String query) throws Exception {
        Documents docs = (Documents) session.newRequest("Document.Query").set("query", query).execute();
        for (Document doc : docs) {
            session.newRequest("Document.Delete").setInput(doc).execute();
        }
    }
    
    public void initClients(String url, String username, String password) {
                System.out.println("Logging in " + username + "/" + password);
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(url);
        clientBuilder.setCredentials(username, password);
        registryApi = clientBuilder.constructRegistryApi();
        simpleRegistryService = clientBuilder.constructSimpleRegistryService();
        endpointStateService = clientBuilder.constructEndpointStateService();

        this.clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
        client = Client.create(this.clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        
        HttpAutomationClient client = new HttpAutomationClient(url);
        session = client.getSession(username, password);
    }
    
    public static String getSourceFolderPath(String doctype) {
        return Subproject.DEFAULT_SUBPROJECT_PATH + '/' + Repository.REPOSITORY_NAME + '/' + doctype; 
    }
    
}
