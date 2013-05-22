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
import org.easysoa.registry.types.Project;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 *
 * @author jguillemotte
 */
public class ContextData {

    /**
     * 
     * @param session
     * @param subprojectId
     * @return 
     */
    public final static ContextData getVersionData(CoreSession session, String subprojectId) throws ClientException {
        ContextData versionData = new ContextData();
        // Get the project, phase and version with the subproject ID
        DocumentModel contextInfo = SubprojectServiceImpl.getSubprojectById(session, subprojectId);
        DocumentModel liveVersion = session.getWorkingCopy(contextInfo.getRef());
        String versionPath = liveVersion.getPathAsString();
        
        // find the project
        DocumentModelList documentList = session.query(DocumentService.NXQL_SELECT_FROM + Project.DOCTYPE);
        for(DocumentModel project : documentList){
            if(versionPath.startsWith(project.getPathAsString())){
                versionData.setProject(project.getName());
                break;
            }
        }
        
        versionData.setPhase(contextInfo.getName());
        if(contextInfo.isVersion()){
            versionData.setVersion(contextInfo.getProperty("major_version").getValue() + "." + contextInfo.getProperty("minor_version").getValue());
        }/* else {
            versionData.setVersion("version courante");
        }*/
        
        return versionData;
    }    
    
    private String project;
    private String phase;
    private String version;
    //private String projectId;
    
    public ContextData(){
        this.project = "";
        this.phase = "";
        this.version = "";
    }

    public ContextData(String project, String phase, String version){
        setProject(project);
        setPhase(phase);
        setVersion(version);
    }
    
    /**
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public final void setProject(String project) {
        if(project != null){
            this.project = project;
        } else {
            this.project = "";
        }
    }

    /**
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @param phase the phase to set
     */
    public final void setPhase(String phase) {
        if(phase != null){
            this.phase = phase;
        } else {
            this.phase = "";
        }
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public final void setVersion(String version) {
        if(version != null){
            this.version = version;
        } else {
            this.version = "";
        }
    }    
    
}
