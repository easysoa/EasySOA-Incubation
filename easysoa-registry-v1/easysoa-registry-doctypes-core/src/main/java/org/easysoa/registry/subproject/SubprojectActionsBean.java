/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
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

package org.easysoa.registry.subproject;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.platform.publisher.api.PublisherService;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.snapshot.Snapshot;
import org.nuxeo.snapshot.Snapshotable;

/**
 * Subproject actions : create version...
 * 
 * @author mdutoo
 * 
 */
@Name("subprojectActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class SubprojectActionsBean implements Serializable {
    
    private static final long serialVersionUID = 4640555715744062388L;

    private static Log log = LogFactory.getLog(SubprojectActionsBean.class);

    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;

    DocumentService docService;
    
    @In
    FacesContext facesContext;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    // won't inject this because of seam problem after activation
    // ::protected Map<String, String> messages;
    protected ResourcesAccessor resourcesAccessor;

    private String outputMessage = null;
    
    @Create
    //@Observer(value = { AppliImplListener.APPLI_IMPL_CHANGED })
    public void init() throws Exception {
        // Create session if needed
        if (documentManager == null) {
            documentManager = navigationContext.getOrCreateDocumentManager();
        }
        if (docService == null) {
            docService = Framework.getService(DocumentService.class);
        }
    }
    
    public String createSubprojectVersion() throws ClientException {
        DocumentModel subproject = navigationContext.getCurrentDocument();
        if (subproject == null || !subproject.getType().equals(Subproject.DOCTYPE)) {
            outputMessage = "Unable to create version, current document is not a subproject !";
            return null;
        }
        
        DocumentModel versionedSubprojectModel = SubprojectServiceImpl
                .createSubprojectVersion(subproject, VersioningOption.MINOR); // TODO also MAJOR
        // newly created version can then be accessed through
        // Historique > Versions archivées > Consulter la version archivée
        // which appears in the breadcrumb beneath MySubproject/Suivi de version/MySubproject
        
        /*
        REMOVING CUSTOM PUBLICATION CODE
        because the new version may already be accessed (as said above)
        and when browsing it from publication section, it would use the regular pageProvider
        and therefore display no children (because they are not accessible anymore through
        session.getChildren() but using nuxeo-tree-snapshot's own adapter and pageProvider) 
        
        CoreSession coreSession = subproject.getCoreSession();
        String publishedSectionName = coreSession.getDocument(versionedSubprojectModel.getParentRef()).getName()
                + "_" + versionedSubprojectModel.getName() + "_v" + versionedSubprojectModel.getVersionLabel();
           // TODO or latest version only, like regular publishing ?? (anyway older versions still accessible)
        DocumentModel publishedSection = coreSession.createDocumentModel("Section");
        publishedSection.setPathInfo("/default-domain/sections", publishedSectionName);
        publishedSection.setPropertyValue("dc:title", publishedSectionName);
        publishedSection = coreSession.createDocument(publishedSection);
        coreSession.save();
        
        DocumentModel versionedSubprojectProxy = coreSession.createProxy(versionedSubprojectModel.getRef(), publishedSection.getRef());
        // (NB. versionedSubprojectProxy is not a version, even though its target is)
        */

        outputMessage = "Successfully created new version " + versionedSubprojectModel.getVersionLabel(); // TODO ??
        Object[] params = { versionedSubprojectModel.getVersionLabel() };
        facesMessages.add(StatusMessage.Severity.INFO,
                resourcesAccessor.getMessages().get("message.easysoa.newVersionCreated"),
                params); // or FacesMessages.instance().add(...)

        // Doesn't work, current document is not refreshed ....
        /*try{
            DocumentModelList parentList = docService.findAllParents(documentManager, subproject);
            if(parentList != null && parentList.size() > 0){
                EventManager.raiseEventsOnDocumentChildrenChange(parentList.get(0));
                Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, parentList.get(0));
            }
        } catch(Exception ex){
            log.warn("Unable to get the parent document models", ex);
        }*/
        
        //EventManager.raiseEventsOnDocumentChildrenChange(subproject);
        //EventManager.raiseEventsOnDocumentSelected(subproject);
        //Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, subproject); // no effect
        //Events.instance().raiseEvent(EventNames.DOCUMENT_CHANGED, subproject); // no effect
        
        navigationContext.invalidateCurrentDocument(); // Else the current document is not refreshed and another clic on the 'create new version' button create a version on the previous 'live' document.
            
        DocumentModel publishedVersion = versionedSubprojectModel;// goes at MySubproject/Suivi de version/MySubproject
        //DocumentModel publishedVersion = versionedSubprojectProxy;// goes at Publications/MySubproject_vx.y (but if create version again, error "can't set prop")
        ///DocumentModel publishedVersion = subproject;// goes to live (but that's not what we'd want)
        if (publishedVersion != null) {
            return navigationContext.navigateToDocument(publishedVersion, "after-edit");
        }
        else {
            return null;
        }
    }

    public String getOutputMessage() {
        return outputMessage; // TODO ??
    }
    
}
