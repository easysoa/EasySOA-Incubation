package org.easysoa.registry.indicators.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.utils.ContextVisibility;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public class TagsIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        UserManager userManager = Framework.getService(UserManager.class);

        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            if(ContextVisibility.DEEP.getValue().equals(visibility)){
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));                                
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);                
            }
        }
        
        // Count users
        int userCount = userManager.getUserIds().size();
        
        // Count services without tagging folders
        int notTaggedServices = 0;
        DocumentModelList serviceModels = session.query(DocumentService.NXQL_SELECT_FROM + InformationService.DOCTYPE
                + DocumentService.NXQL_WHERE_NO_PROXY + subprojectPathCriteria);
        for (DocumentModel serviceModel : serviceModels) {
            DocumentModelList serviceParents = documentService.findAllParents(session, serviceModel);
            notTaggedServices++;
            for (DocumentModel serviceParent : serviceParents) {
                if (TaggingFolder.DOCTYPE.equals(serviceParent.getType())) {
                    notTaggedServices--;
                    break;
                }
            }
        }
        
        // Count tagging folders
        DoctypeCountIndicator taggingFolderCount = new DoctypeCountIndicator(TaggingFolder.DOCTYPE);
        IndicatorValue taggingFolderCountValue = taggingFolderCount.compute(session, subprojectId, computedIndicators, visibility);
        
        // Register indicators
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put("Services without at least one user tag",
                new IndicatorValue(notTaggedServices,
                        (serviceModels.size() > 0) ? 100 * notTaggedServices / serviceModels.size() : -1));
        indicators.put("Average tag count per user",
                new IndicatorValue(
                        (userCount > 0) ? taggingFolderCountValue.getCount() / userCount : -1, -1));
        
        return indicators;
    }

}
