package com.axxx.dps.demo;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;


/**
 * 
 * WARNING: Requires the Nuxeo Studio project "EasySOA" to be deployed on a launched Nuxeo DM (port 8080) 
 * 
 * @author mkalam-alami
 *
 */
public class RemoteRepositoryInit {

	public static final String ACTOR_DOCTYPE = "Actor";
	public static final String BUSINESS_SERVICE_DOCTYPE = "BusinessService";
	public static final String BUSINESS_SERVICE_PROVIDER_ACTOR = "businessservice:providerActor";
	public static final String BUSINESS_SERVICE_CONSUMER_ACTOR = "businessservice:consumerActor";
	public static final String SLA_DOCTYPE = "SLA";
	public static final String INFORMATION_SERVICE_DOCTYPE = "InformationService";
	public static final String INFORMATION_SERVICE_PROVIDER_ACTOR = "informationservice:providerActor";
	public static final String INFORMATION_SERVICE_CONSUMER_ACTOR = "informationservice:consumerActor";
	public static final String INFORMATION_SERVICE_BUSINESS_SERVICE = "informationservice:linkedBusinessService";
	public static final String OLA_DOCTYPE = "OLA";
	public static final String COMPONENT_DOCTYPE = "Component";
	public static final String COMPONENT_TYPE = "component_schema:componentCategory";
	public static final String COMPONENT_INFORMATION_SERVICE = "component_schema:linkedInformationService";
	
	private static RegistryApi registryApi;
	private static Session session;

	public static final void main(String[] args) throws Exception {
		initClients();
		
		// Reset repository and root
		String rootName = "Projets collaboratifs";
		delete("SELECT * FROM " + SystemTreeRoot.DOCTYPE + " WHERE dc:title = '" + rootName + "'");
		delete("SELECT * FROM " + Repository.DOCTYPE);
		
		// Root
		delete("SELECT * FROM " + SystemTreeRoot.DOCTYPE + " WHERE dc:title = '" + rootName + "'");
		String rootPath = createDocument(SystemTreeRoot.DOCTYPE, rootName, "/default-domain/workspaces");
		
		// Project and its folders
		String projectPath = createSoaNode(new SoaNodeId(TaggingFolder.DOCTYPE, "Intégration APV Web - Pivotal"), rootPath);
		String specificationsPath = createDocument("Folder", "1. Spécifications", projectPath);
		String realizationPath = createDocument("Folder", "2. Réalisation", projectPath);
		createDocument("Folder", "3. Déploiements", projectPath);
		
		// PHASE 1: Specs
		
		// Specifications sub-folders
		String businessArchitecturePath = createDocument("Folder", "1. Business architecture", specificationsPath);
		String infoArchitecturePath = createDocument("Folder", "2. Information architecture", specificationsPath);
		String compArchitecturePath = createDocument("Folder", "3. Component architecture", specificationsPath);
		
		// Actors
		SoaNodeId actUniId = new SoaNodeId(ACTOR_DOCTYPE, "Uniserv"); createSoaNode(actUniId);
		SoaNodeId actCommId = new SoaNodeId(ACTOR_DOCTYPE, "Commercial AXXX"); createSoaNode(actCommId);
		SoaNodeId actCRMId = new SoaNodeId(ACTOR_DOCTYPE, "CRM DCV"); createSoaNode(actCRMId);
		SoaNodeId actSIDPSId = new SoaNodeId(ACTOR_DOCTYPE, "SI DPS"); createSoaNode(actSIDPSId);
		SoaNodeId actSIDCVId = new SoaNodeId(ACTOR_DOCTYPE, "SI DCV"); createSoaNode(actSIDCVId);
		
		// Business services
		String bsPreCptPath = createSoaNode(new SoaNodeId(BUSINESS_SERVICE_DOCTYPE, "Demande précompte (Dde_Pré_Cpt)"),
				businessArchitecturePath,
				BUSINESS_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actCRMId),
				BUSINESS_SERVICE_CONSUMER_ACTOR + "=" + getIdRef(actCommId)
				);
		createSoaNode(new SoaNodeId(BUSINESS_SERVICE_DOCTYPE, "Demande de fonds (Dde_Fonds)"),
				businessArchitecturePath);
		createDocument("File", "BusinessArchitecture.doc", businessArchitecturePath);
		createDocument("File", "SpecificationsExternes (=DocUtilisateur).doc", businessArchitecturePath);
		createSoaNode(new SoaNodeId(ACTOR_DOCTYPE, "Commercial AXXX"), bsPreCptPath);
		createSoaNode(new SoaNodeId(ACTOR_DOCTYPE, "CRM DCV"), bsPreCptPath);
		SoaNodeId slaDdePreCptId = new SoaNodeId(SLA_DOCTYPE, "SLA Dde_Pré_Cpt");
		String slaPreCptPath = createSoaNode(slaDdePreCptId, bsPreCptPath);
 
		// Information services
		SoaNodeId isCheckAddressId = new SoaNodeId(INFORMATION_SERVICE_DOCTYPE, "checkAddress");
		createSoaNode(isCheckAddressId,
				infoArchitecturePath, INFORMATION_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actUniId));
		
		createSoaNode(new SoaNodeId(INFORMATION_SERVICE_DOCTYPE, "Cré_Précpte"), infoArchitecturePath,
				INFORMATION_SERVICE_PROVIDER_ACTOR + "=" + actSIDCVId);
		
		createSoaNode(new SoaNodeId(INFORMATION_SERVICE_DOCTYPE, "TdrWebService"), infoArchitecturePath);
		createSoaNode(new SoaNodeId(OLA_DOCTYPE, "OLA TdrWebService"), slaPreCptPath);
		
		createSoaNode(new SoaNodeId(INFORMATION_SERVICE_DOCTYPE, "Information_APV"), infoArchitecturePath);
		
		createSoaNode(new SoaNodeId(INFORMATION_SERVICE_DOCTYPE, "Cré_Maj_Dde_Fonds"), infoArchitecturePath);

		// Components
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "checkAddress"), compArchitecturePath,
				COMPONENT_TYPE + "=Application", COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isCheckAddressId));
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "TdrWebService"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Information_APV"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Orchestration_DCV"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Orchestration_DPS"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Pivotal"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "APV_Web"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Talend ESB DCV"), compArchitecturePath,
				COMPONENT_TYPE + "=Technical");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Talend ESB DPS"), compArchitecturePath,
				COMPONENT_TYPE + "=Technical");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Embedded CXF or FraSCAti"), compArchitecturePath,
				COMPONENT_TYPE + "=Technical");
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Embedded CXF or FraSCAti with Talend exts"), compArchitecturePath,
				COMPONENT_TYPE + "=Technical");

		// PHASE 1: Realization
		//publish(specificationsPath, realizationPath);
		createSoaNode(new SoaNodeId(Service.DOCTYPE, "checkAddress"), realizationPath);
		createSoaNode(new SoaNodeId(Service.DOCTYPE, "TdrWebService"), realizationPath,
				Service.XPATH_LINKED_COMPONENT + "=" + getIdRef(new SoaNodeId(COMPONENT_DOCTYPE, "TdrWebService")));
		createSoaNode(new SoaNodeId(Service.DOCTYPE, "Information_APV"), realizationPath);
		createSoaNode(new SoaNodeId(Service.DOCTYPE, "Orchestration_DCV"), realizationPath);
		createSoaNode(new SoaNodeId(Service.DOCTYPE, "Orchestration_DPS"), realizationPath);
		
	}

	public static String getIdRef(SoaNodeId soaNodeId) throws Exception {
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
	
	public static String publish(String fromPath, String toParentPath) throws Exception {
		Document document = (Document) session.newRequest("Document.Publish")
			 .setInput(getDocByPath(fromPath))
			 .set("target", getDocByPath(toParentPath))
			 .execute();
		return document.getPath();
	}
	
	private static Document getDocByPath(String path) throws Exception {
		Documents docs = (Documents) session
				.newRequest("Document.Query")
				.set("query", "SELECT * FROM Document WHERE ecm:path = '" + path + "'").execute();
		if (docs != null && !docs.isEmpty()) {
			return docs.get(0);
		}
		else {
			return null;
		}
	}

	public static String createDocument(String doctype, String title, String path, String... properties) throws Exception {
		if (path == null) {
			throw new Exception("Path is null");
		}
		OperationRequest request = session.newRequest("Document.Create")
				.setInput(getDocByPath(path)).set("type", doctype)
				.set("name", title);
		StringBuilder propertiesString = new StringBuilder("dc:title=" + title);
		for (String property : properties) {
			propertiesString.append('\n' + property);
		}
		request.set("properties", propertiesString);
		Document createdDocument = (Document) request.execute();
		return createdDocument.getPath();
	}

	
	

	private static String createSoaNode(SoaNodeId soaNodeId) throws Exception {
		return createSoaNode(soaNodeId, (String) null);
	}
	
	public static String createSoaNode(SoaNodeId soaNodeId, String parentPath, String... properties) throws Exception {
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
	
	public static String createSoaNode(SoaNodeId soaNodeId, SoaNodeId parentSoaNode, String... properties) throws Exception {
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
	
	public static void delete(String query) throws Exception {
		Documents docs = (Documents) session.newRequest("Document.Query").set("query", query).execute();
		for (Document doc : docs) {
			session.newRequest("Document.Delete").setInput(doc).execute();
		}
	}
	
	public static void initClients() {
		ClientBuilder clientBuilder = new ClientBuilder();
		registryApi = clientBuilder.constructRegistryApi();
		
		HttpAutomationClient client = new HttpAutomationClient(
		           "http://localhost:8080/nuxeo/site/automation");
		session = client.getSession("Administrator", "Administrator");
	}
	
	public static String getSourceFolderPath(String doctype) {
        return Repository.REPOSITORY_PATH + '/' + doctype; 
    }
    
	public static String getPath(SoaNodeId identifier) {
        return getSourceFolderPath(identifier.getType()) + '/' + identifier.getName();
    }

}
