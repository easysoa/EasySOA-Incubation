
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

import org.easysoa.registry.SubprojectServiceImpl;
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
     * TODO call getSubprojectIdOrCreateDefault and rename to buildSubprojectCriteriaElseDefault
     * 
     * Build a nxql request criteria with the subproject ID.
     * if the context visibility is STRICT the following request criteria will be returned : "spnode:subproject = 'subprojectId'"
     * otherwise for a DEEP visibility, the following request criteria will be returned : "spnode:subproject IN ('subprojectId', 'subprojectId', ...)"
     * 
     * The subprojectid's used in the DEEP request are the parent subprojects ID's
     * 
     * @param session Nuxeo session
     * @param subprojectId Subproject ID required (at worse should be the default subproject) 
     * @param deepVisibility True if the context visibility must be DEEP, false if it must be STRICT
     * @return
     * @throws PropertyException
     * @throws ClientException 
     */
    public static String buildSubprojectCriteria(CoreSession session, String subprojectId, boolean deepVisibility) throws PropertyException, ClientException {
        if(deepVisibility){
            return buildSubprojectCriteria(session, subprojectId, ContextVisibility.DEEP.getValue());
        } else {
            return buildSubprojectCriteria(session, subprojectId, ContextVisibility.STRICT.getValue());
        }
    }
    
    /**
     * TODO rename to buildSubprojectCriteria
     * 
     * Build a nxql request criteria with the subproject ID.
     * If no subproject id, returns an empty criteria
     * else if the context visibility is STRICT the following request criteria will be returned : "spnode:subproject = 'subprojectId'"
     * otherwise for a DEEP visibility, the following request criteria will be returned : "spnode:subproject IN ('subprojectId', 'subprojectId', ...)"
     * 
     * The subprojectid's used in the DEEP request are the parent subprojects ID's
     * 
     * @param session Nuxeo session
     * @param subprojectId Subproject ID if null or empty, an empty criteria is returned
     * @param visibility Context visibility : deep or strict
     * @return
     * @throws PropertyException
     * @throws ClientException 
     */    
    public static String buildSubprojectCriteria(CoreSession session, String subprojectId, String visibility) throws PropertyException, ClientException {
    
        String subprojectPathCriteria;
        if (subprojectId == null || subprojectId.length() == 0) {
            subprojectPathCriteria = "";
        } else {
            if(ContextVisibility.DEEP.getValue().equalsIgnoreCase(visibility)){
            	DocumentModel subproject = SubprojectServiceImpl.getSubprojectById(session, subprojectId);
            	if (subproject == null) {
            		throw new ClientException("No subproject with id " + subprojectId);
            	}
                subprojectPathCriteria = SubprojectServiceImpl.buildCriteriaSeenFromSubproject(subproject);
            } else {
                subprojectPathCriteria = SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);                
            }
        }    
        return subprojectPathCriteria;
    }
    
}
