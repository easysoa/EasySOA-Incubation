package org.easysoa.registry.indicators.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import static org.easysoa.registry.utils.NuxeoListUtils.*;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class SoftwareComponentIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(
                DoctypeCountIndicator.getName(Deliverable.DOCTYPE),
                DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE),
                DoctypeCountIndicator.getName(InformationService.DOCTYPE),
                DoctypeCountIndicator.getName(SoftwareComponent.DOCTYPE)
                );
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        
        // not in any software component :
        DocumentModelList deliverableProxyInASoftwareComponent = session.query(NXQL_SELECT_FROM + Deliverable.DOCTYPE
                + NXQL_WHERE_PROXY + NXQL_AND + NXQL_PATH_STARTSWITH
                + "/default-domain/repository/SoftwareComponent" + NXQL_QUOTE);
        DocumentModelList deliverableInNoSoftwareComponent = session.query(NXQL_SELECT_FROM + Deliverable.DOCTYPE + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent));
        int deliverableInNoSoftwareComponentCount = deliverableInNoSoftwareComponent.size();
        int deliverableCount = computedIndicators.get(DoctypeCountIndicator.getName(Deliverable.DOCTYPE)).getCount();
        indicators.put("deliverableInNoSoftwareComponent", 
                new IndicatorValue(deliverableInNoSoftwareComponentCount,
                (deliverableCount > 0) ? 100 * deliverableInNoSoftwareComponentCount / deliverableCount : -1));
        
        DocumentModelList deliverableInNoSoftwareComponentsImplementations = session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + Deliverable.DOCTYPE + "'"
                + " AND ecm:parentId NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent));
        indicators.put("deliverableInNoSoftwareComponentsImplementations", 
                new IndicatorValue(deliverableInNoSoftwareComponentsImplementations.size(),
                        -1));
        
        DocumentModelList implementationProxyInADeliverable = session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + Deliverable.DOCTYPE + "'");
        DocumentModelList implementationInNoDeliverable = session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, implementationProxyInADeliverable));
        int serviceImplCount = computedIndicators.get(DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE)).getCount();
        indicators.put("implementationInNoDeliverable", 
                new IndicatorValue(implementationInNoDeliverable.size(),
                        (serviceImplCount > 0) ? 100 * implementationInNoDeliverable.size() / serviceImplCount : -1)); // TODO i.e. deliverable is unknown / placeholder
        
        ArrayList<DocumentModel> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent = new ArrayList<DocumentModel>(implementationInNoDeliverable);
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.addAll(deliverableInNoSoftwareComponentsImplementations);
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size(),
                        (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size() / serviceImplCount : -1));
        
        DocumentModelList implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy = session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + InformationService.DOCTYPE + "'"
                // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                + " AND ecm:uuid IN " + toLiteral(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent, null))); // TODO soaid for proxies
        int serviceCount = computedIndicators.get(DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE)).getCount();
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet = new HashSet<String>(
                getParentIds(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy));
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size(),
                        (serviceCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size() / serviceCount : -1));
       
        HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>(serviceCount);
        DocumentModelList serviceList = session.query(NXQL_SELECT_FROM + InformationService.DOCTYPE + NXQL_WHERE_NO_PROXY);
        DocumentService documentService = Framework.getService(DocumentService.class);
        for (DocumentModel service : serviceList) {
            DocumentModelList serviceImpls = documentService.getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationIdSet.add(service.getId());
            }
        }
        HashSet<String> serviceInNoSoftwareComponentIdSet = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet);
        serviceInNoSoftwareComponentIdSet.addAll(serviceWhithoutImplementationIdSet);
        indicators.put("serviceInNoSoftwareComponent", 
                new IndicatorValue(serviceInNoSoftwareComponentIdSet.size(),
                (serviceCount > 0) ? 100 * serviceInNoSoftwareComponentIdSet.size() / serviceCount : -1));
        
        
        // whose software component if any is not in any Tagging Folder ("Business Process") :
        HashSet<String> serviceIdSet = new HashSet<String>(getIds(serviceList));
        HashSet<String> serviceInASoftwareComponentIdSet = new HashSet<String>(serviceIdSet);
        serviceInASoftwareComponentIdSet.removeAll(serviceInNoSoftwareComponentIdSet);
        indicators.put("serviceInASoftwareComponent",
                new IndicatorValue(serviceInASoftwareComponentIdSet.size(),
                (serviceCount > 0) ? 100 * serviceInASoftwareComponentIdSet.size() / serviceCount : -1)); // TODO bof
        
        DocumentModelList softwareComponentProxyInATaggingFolder = session.query(NXQL_SELECT_FROM + "SoftwareComponent"
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + TaggingFolder.DOCTYPE + "'");
        DocumentModelList softwareComponentInNoTaggingFolder = session.query(NXQL_SELECT_FROM + "SoftwareComponent" + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, softwareComponentProxyInATaggingFolder)); // TODO bof
        int softwareComponentCount = computedIndicators.get(DoctypeCountIndicator.getName(SoftwareComponent.DOCTYPE)).getCount();
        indicators.put("softwareComponentInNoTaggingFolder",
                new IndicatorValue(softwareComponentInNoTaggingFolder.size(),
                        (softwareComponentCount > 0) ? 100 * softwareComponentInNoTaggingFolder.size() / softwareComponentCount : -1));
        HashSet<String> softwareComponentInNoTaggingFoldersDeliverableIds = new HashSet<String>(getProxiedIds(session, session.query(NXQL_SELECT_FROM + Deliverable.DOCTYPE
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/SoftwareComponent" + "'"
                + " AND ecm:parentId NOT IN " + getProxiedIdLiteralList(session, softwareComponentProxyInATaggingFolder))));
        indicators.put("softwareComponentInNoTaggingFoldersDeliverables", 
                new IndicatorValue(softwareComponentInNoTaggingFoldersDeliverableIds.size(),
                        -1));
        HashSet<String> deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds = new HashSet<String>(getIds(deliverableInNoSoftwareComponent));
        deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.addAll(softwareComponentInNoTaggingFoldersDeliverableIds);
        indicators.put("deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolder",
                new IndicatorValue(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size(),
                        (deliverableCount > 0) ? 100 * deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size() / deliverableCount : -1));

        HashSet<String> deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds = new HashSet<String>(getProxiedIds(session,
                session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + Deliverable.DOCTYPE
                + " AND ecm:parentId IN " + toLiteral(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds))));
        indicators.put("deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementation",
                new IndicatorValue(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds.size(),
                        (serviceCount > 0) ? 100 * deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds.size() / serviceCount : -1));
        
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds = new HashSet<String>(getIds(implementationInNoDeliverable));
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.addAll(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds);
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolder",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size(),
                        (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size() / serviceImplCount : -1));
        
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds = new HashSet<String>(getProxiedIds(session,
                session.query(NXQL_SELECT_FROM  + ServiceImplementation.DOCTYPE
                        + NXQL_WHERE_PROXY
                        + " AND ecm:path STARTSWITH '" + "/default-domain/repository/" + InformationService.DOCTYPE + "'"
                        // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                        + " AND ecm:uuid IN " + toLiteral(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds, null))))); // TODO soaid for proxies
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderService",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds.size(),
                        (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds.size() / serviceCount : -1));
        
        HashSet<String> serviceInNoTaggingFolderIds = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds);
        serviceInNoTaggingFolderIds.addAll(serviceWhithoutImplementationIdSet);
        indicators.put("serviceInNoTaggingFolder", 
                new IndicatorValue(serviceInNoTaggingFolderIds.size(),
                (serviceCount > 0) ? 100 * serviceInNoTaggingFolderIds.size() / serviceCount : -1));
        
        return indicators;
    }
    
}
