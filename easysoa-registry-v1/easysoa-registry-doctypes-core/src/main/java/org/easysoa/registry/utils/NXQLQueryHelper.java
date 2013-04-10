
/**
 * EasySOA Proxy
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

package org.easysoa.registry.utils;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.SubprojectNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 *
 * @author jguillemotte
 */
public class NXQLQueryHelper {

    /**
     * Build a nxql request criteria with the subproject ID.
     * if the context visibility is STRICT the following request criteria will be returned : "AND spnode:subproject = 'subprojectId'"
     * otherwise for a DEEP visibility, the following request criteria will be returned : "AND spnode:subproject IN ('subprojectId', 'subprojectId', ...)"
     * 
     * The subprojectid's used in the DEEP request are the parent subprojects ID's
     * 
     * @param session Nuxeo session
     * @param subprojectId Subproject ID
     * @param deepVisibility True if the context visibility must be DEEP, false if it must be STRICT
     * @return
     * @throws PropertyException
     * @throws ClientException 
     */
    public static String buildSubprojectPathCriteria(CoreSession session, String subprojectId, boolean deepVisibility) throws PropertyException, ClientException {
        if(deepVisibility){
            return buildSubprojectPathCriteria(session, subprojectId, ContextVisibility.DEEP.getValue());
        } else {
            return buildSubprojectPathCriteria(session, subprojectId, ContextVisibility.STRICT.getValue());
        }
    }
    
    /**
     * Build a nxql request criteria with the subproject ID.
     * if the context visibility is STRICT the following request criteria will be returned : "AND spnode:subproject = 'subprojectId'"
     * otherwise for a DEEP visibility, the following request criteria will be returned : "AND spnode:subproject IN ('subprojectId', 'subprojectId', ...)"
     * 
     * The subprojectid's used in the DEEP request are the parent subprojects ID's
     * 
     * @param session Nuxeo session
     * @param subprojectId Subproject ID
     * @param visibility Context visibility : deep or strict
     * @return
     * @throws PropertyException
     * @throws ClientException 
     */    
    public static String buildSubprojectPathCriteria(CoreSession session, String subprojectId, String visibility) throws PropertyException, ClientException {
    
        String subprojectPathCriteria;
        if (subprojectId == null || subprojectId.length() == 0) {
            subprojectPathCriteria = "";
        } else {
            if(ContextVisibility.DEEP.getValue().equalsIgnoreCase(visibility)){
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));                                
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + buildCriteriaInSubproject(subprojectId);                
            }
        }    
        return subprojectPathCriteria;
    }
    
    /**
     * 
     * @param subprojectId
     * @return 
     */
    public static String buildCriteriaInSubproject(String subprojectId) {
        return SubprojectNode.XPATH_SUBPROJECT + "='" + subprojectId + "'";
    }
    
    /**
     * 
     * @param subprojectNode
     * @return
     * @throws PropertyException
     * @throws ClientException 
     */
    public static String buildCriteriaSeenFromSubproject(DocumentModel subprojectNode)
            throws PropertyException, ClientException {
        // Filter by subproject
        String implVisibleSubprojectIds = null;
        if (subprojectNode.hasFacet(SubprojectNode.FACET)) {
            implVisibleSubprojectIds = (String) subprojectNode
                    .getPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV);
        }
        if (implVisibleSubprojectIds == null) {
            throw new ClientException("visibleSubprojects should not be null on " + subprojectNode);
        }
        
        // ex. "AXXXSpecifications"; // or in 2 pass & get it from subProject ??
        return SubprojectNode.XPATH_SUBPROJECT + " IN (" + implVisibleSubprojectIds + ")";
    }    
    
}
