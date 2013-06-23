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

package org.easysoa.registry.indicators.rest;

import static org.easysoa.registry.utils.NuxeoListUtils.getIds;
import static org.easysoa.registry.utils.NuxeoListUtils.getParentIds;
import static org.easysoa.registry.utils.NuxeoListUtils.getProxiedIdLiteralList;
import static org.easysoa.registry.utils.NuxeoListUtils.getProxiedIds;
import static org.easysoa.registry.utils.NuxeoListUtils.getProxyIds;
import static org.easysoa.registry.utils.NuxeoListUtils.toLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.utils.ContextVisibility;
import org.easysoa.registry.utils.RepositoryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class SoftwareComponentIndicatorProvider extends IndicatorProviderBase {

    public SoftwareComponentIndicatorProvider(String category) {
		super(category);
	}

	@Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(
                DoctypeCountProvider.getName(Deliverable.DOCTYPE),
                DoctypeCountProvider.getName(ServiceImplementation.DOCTYPE),
                DoctypeCountProvider.getName(InformationService.DOCTYPE),
                DoctypeCountProvider.getName(SoftwareComponent.DOCTYPE)
                );
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            if (ContextVisibility.STRICT.getValue().equals(visibility)) {
                subprojectPathCriteria = DocumentService.NXQL_AND
                        + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                        + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));
            }
        }
        
        // not in any software component :
        // TODO WARNING looking for proxies only work when proxy-containing TaggingFolder are in the same Phase as services (else wrong phase id metadata) !
        DocumentModelList deliverableProxyInASoftwareComponent = session.query(DocumentService.NXQL_SELECT_FROM + Deliverable.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY + DocumentService.NXQL_AND + DocumentService.NXQL_PATH_STARTSWITH
                + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/SoftwareComponent" + DocumentService.NXQL_QUOTE + subprojectPathCriteria);
        // so rather getting them using getChildren :
        //TODO
        DocumentModelList deliverableInNoSoftwareComponent = session.query(DocumentService.NXQL_SELECT_FROM + Deliverable.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY
                + getNotInLiteralIfAnyCriteria("ecm:uuid", getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)) + subprojectPathCriteria);
        int deliverableInNoSoftwareComponentCount = deliverableInNoSoftwareComponent.size();
        long deliverableCount = computedIndicators.get(DoctypeCountProvider.getName(Deliverable.DOCTYPE)).getCount();
        ///indicators.put("deliverableInNoSoftwareComponent", 
        ///        new IndicatorValue(deliverableInNoSoftwareComponentCount,
        ///        (deliverableCount > 0) ? 100 * deliverableInNoSoftwareComponentCount / deliverableCount : -1));

        DocumentModelList deliverableInNoSoftwareComponentsImplementations = session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + Deliverable.DOCTYPE + "'"
                +  getNotInLiteralIfAnyCriteria("ecm:parentId", getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)) + subprojectPathCriteria);
        ///indicators.put("deliverableInNoSoftwareComponentsImplementations", 
        ///        new IndicatorValue(deliverableInNoSoftwareComponentsImplementations.size(),
        ///                -1));
        
        DocumentModelList implementationProxyInADeliverable = session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + Deliverable.DOCTYPE + "'");
        DocumentModelList implementationInNoDeliverable = session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY
                + getNotInLiteralIfAnyCriteria("ecm:uuid", getProxiedIdLiteralList(session, implementationProxyInADeliverable)) + subprojectPathCriteria);
        long serviceImplCount = computedIndicators.get(DoctypeCountProvider.getName(ServiceImplementation.DOCTYPE)).getCount();
        super.newIndicator(indicators, "implementationInNoDeliverable", implementationInNoDeliverable.size(), serviceImplCount); // TODO i.e. deliverable is unknown / placeholder
        
        ArrayList<DocumentModel> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent = new ArrayList<DocumentModel>(implementationInNoDeliverable);
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.addAll(deliverableInNoSoftwareComponentsImplementations);
        ///indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent",
        ///        new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size(),
        ///                (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size() / serviceImplCount : -1));
        
        DocumentModelList implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy = session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + InformationService.DOCTYPE + "'"
                // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                + " AND ecm:uuid IN " + toLiteral(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent, null)) + subprojectPathCriteria); // TODO soaid for proxies
        long serviceCount = computedIndicators.get(DoctypeCountProvider.getName(ServiceImplementation.DOCTYPE)).getCount();
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet = new HashSet<String>(
                getParentIds(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy));
        ///indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService",
        ///        new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size(),
        ///                (serviceCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size() / serviceCount : -1));
       
        HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>((int) Math.min(serviceCount, Integer.MAX_VALUE));
        DocumentModelList serviceList = session.query(DocumentService.NXQL_SELECT_FROM + InformationService.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectPathCriteria);
        DocumentService documentService = Framework.getService(DocumentService.class);
        for (DocumentModel service : serviceList) {
            DocumentModelList serviceImpls = documentService.getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationIdSet.add(service.getId());
            }
        }
        HashSet<String> serviceInNoSoftwareComponentIdSet = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet);
        serviceInNoSoftwareComponentIdSet.addAll(serviceWhithoutImplementationIdSet);
        super.newIndicator(indicators, "serviceInNoSoftwareComponent", serviceInNoSoftwareComponentIdSet.size(), serviceCount);
        
        
        // whose software component if any is not in any Tagging Folder ("Business Process") :
        HashSet<String> serviceIdSet = new HashSet<String>(getIds(serviceList));
        HashSet<String> serviceInASoftwareComponentIdSet = new HashSet<String>(serviceIdSet);
        serviceInASoftwareComponentIdSet.removeAll(serviceInNoSoftwareComponentIdSet);
        ///indicators.put("serviceInASoftwareComponent",
        ///        new IndicatorValue(serviceInASoftwareComponentIdSet.size(),
        ///        (serviceCount > 0) ? 100 * serviceInASoftwareComponentIdSet.size() / serviceCount : -1)); // TODO bof
        
        DocumentModelList softwareComponentProxyInATaggingFolder = session.query(DocumentService.NXQL_SELECT_FROM + "SoftwareComponent"
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + TaggingFolder.DOCTYPE + "'" + subprojectPathCriteria);
        DocumentModelList softwareComponentInNoTaggingFolder = session.query(DocumentService.NXQL_SELECT_FROM + "SoftwareComponent" + DocumentService.NXQL_WHERE_NO_PROXY
                + getNotInLiteralIfAnyCriteria("ecm:uuid", getProxiedIdLiteralList(session, softwareComponentProxyInATaggingFolder)) + subprojectPathCriteria); // TODO bof
        long softwareComponentCount = computedIndicators.get(DoctypeCountProvider.getName(SoftwareComponent.DOCTYPE)).getCount();
        ///indicators.put("softwareComponentInNoTaggingFolder",
        ///        new IndicatorValue(softwareComponentInNoTaggingFolder.size(),
        ///                (softwareComponentCount > 0) ? 100 * softwareComponentInNoTaggingFolder.size() / softwareComponentCount : -1));
        HashSet<String> softwareComponentInNoTaggingFoldersDeliverableIds = new HashSet<String>(getProxiedIds(session, session.query(DocumentService.NXQL_SELECT_FROM + Deliverable.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/SoftwareComponent" + "'"
                + getNotInLiteralIfAnyCriteria("ecm:parentId", getProxiedIdLiteralList(session, softwareComponentProxyInATaggingFolder)) + subprojectPathCriteria)));
        ///indicators.put("softwareComponentInNoTaggingFoldersDeliverables", 
        ///        new IndicatorValue(softwareComponentInNoTaggingFoldersDeliverableIds.size(),
        ///                -1));
        HashSet<String> deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds = new HashSet<String>(getIds(deliverableInNoSoftwareComponent));
        deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.addAll(softwareComponentInNoTaggingFoldersDeliverableIds);
        ///indicators.put("deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolder",
        ///        new IndicatorValue(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size(),
        ///                (deliverableCount > 0) ? 100 * deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size() / deliverableCount : -1));

        HashSet<String> deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds = new HashSet<String>(getProxiedIds(session,
                session.query(DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                + DocumentService.NXQL_WHERE_PROXY
                + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + Deliverable.DOCTYPE
                + "' AND ecm:parentId IN " + toLiteral(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds) + subprojectPathCriteria)));
        ///indicators.put("deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementation",
        ///        new IndicatorValue(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds.size(),
        ///                (serviceCount > 0) ? 100 * deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds.size() / serviceCount : -1));
        
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds = new HashSet<String>(getIds(implementationInNoDeliverable));
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.addAll(deliverableInNoSoftwareComponentOrWhoseIsInNoTaggingFolderImplementationIds);
        ///indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolder",
        ///        new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size(),
        ///                (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds.size() / serviceImplCount : -1));
        
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds = new HashSet<String>(getProxiedIds(session,
                session.query(DocumentService.NXQL_SELECT_FROM  + ServiceImplementation.DOCTYPE
                        + DocumentService.NXQL_WHERE_PROXY
                        + " AND ecm:path STARTSWITH '" + RepositoryHelper.getRepositoryPath(session, subprojectId) + "/" + InformationService.DOCTYPE + "'"
                        // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                        + " AND ecm:uuid IN " + toLiteral(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderIds, null)) + subprojectPathCriteria))); // TODO soaid for proxies
        ///indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderService",
        ///        new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds.size(),
        ///                (serviceImplCount > 0) ? 100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds.size() / serviceCount : -1));
        
        HashSet<String> serviceInNoTaggingFolderIds = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentOrWhoseIsInNoTaggingFolderServiceIds);
        serviceInNoTaggingFolderIds.addAll(serviceWhithoutImplementationIdSet);
        super.newIndicator(indicators, "serviceInNoTaggingFolder", serviceInNoTaggingFolderIds.size(), serviceCount);
        
        return indicators;
    }
    
    private String getNotInLiteralIfAnyCriteria(String propXpath, String literalList) {
        return (literalList.length() == 2) ? "" : " AND " + propXpath + " NOT IN " + literalList;
    }
    
}
