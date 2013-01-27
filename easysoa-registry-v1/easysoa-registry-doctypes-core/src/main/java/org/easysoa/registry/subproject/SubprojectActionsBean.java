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
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.platform.publisher.api.PublisherService;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
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

    private Blob file = null;

    private String importType = "sca";

    private String targetWorkspace;

    private String targetAppliImpl;

    private String outputMessage = null;
    
    private DocumentModel targetWorkspaceModel;
    
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
        
        CoreSession coreSession = subproject.getCoreSession();
        String publishedSectionName = coreSession.getDocument(versionedSubprojectModel.getParentRef()).getName()
                + "_" + versionedSubprojectModel.getName() + "_v" + versionedSubprojectModel.getVersionLabel();
        DocumentModel publishedSection = coreSession.createDocumentModel("Section");
        publishedSection.setPathInfo("/default-domain/sections", publishedSectionName);
        publishedSection.setPropertyValue("dc:title", publishedSectionName);
        publishedSection = coreSession.createDocument(publishedSection);
        coreSession.save();
        
        coreSession.createProxy(versionedSubprojectModel.getRef(), publishedSection.getRef());

        outputMessage = "Successfully created new version " + versionedSubprojectModel.getVersionLabel();

        DocumentModel publishedVersion = null;// TODO
        if (publishedVersion != null) {
            return navigationContext.navigateToDocument(publishedVersion, "after-edit");
        }
        else {
            return null;
        }
    }

    public String getOutputMessage() {
        return outputMessage;
    }
    
}
