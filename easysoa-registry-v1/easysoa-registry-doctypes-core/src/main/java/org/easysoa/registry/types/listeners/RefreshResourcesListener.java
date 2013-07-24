/**
 * EasySOA Registry
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

package org.easysoa.registry.types.listeners;

import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.runtime.api.Framework;


/**
 * Listens to periodic custom refreshResources event (to be scheduled) to trigger
 * refresh of (obviously live) Resources that are encompassed within the NXQL
 * criteria provided as eventCategory of said schedule.
 * 
 * To use it, configure such a schedule in a contribution, ex. :
 * 
  <extension point="schedule" target="org.nuxeo.ecm.core.scheduler.SchedulerService">
    <schedule id="refreshResources_MyProject/Deploiement">
      <event>refreshResources</event>
      <!-- refresh all Resources of said subproject -->
      <!-- eventCategory>spnode:subproject='/default-domain/MyProject/Realisation_v'</eventCategory -->
      <!-- refresh all Resources of said Project's Subprojects -->
      <eventCategory>spnode:subproject LIKE '/default-domain/MyProject/%_v'</eventCategory>
      <!-- refresh every night at 3 am -->
      <cronExpression>0 3 * * * ?</cronExpression>
    </schedule>
  </extension>
 * 
 * Must be triggered in async (which requires to implement PostCommitEventListener
 * and postCommit="true", see EventListenerDescriptor.java) manner by Nuxeo's event system, see
 * http://doc.nuxeo.com/display/NXDOC/Events+and+Listeners
 * (though it might also be called synchronously)
 *
 * Should be registered with async & postCommit on event : readyToDownload fired by AsyncResourceUpdateServiceImpl
 *
 * @author mdutoo
 */
public class RefreshResourcesListener extends EventListenerBase
        implements PostCommitEventListener {
    
    public static final String REFRESH_RESOURCES_EVENT = "refreshResources";

    private static Logger logger = Logger.getLogger(RefreshResourcesListener.class);

    
    /**
     * Simplistic async / postcommit impl that dispatches to handleEvent(event)
     * @param events
     * @throws ClientException
     */
    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            try {
                this.handleEvent(event);
            } catch (Exception e) {
                logger.error("Error", e);
            }
        }
        
    }

    @Override
    public void handleEvent(Event event) throws ClientException {

        // NB. not checking event because only registered on refreshResources
        
        EventContext context = event.getContext();
        // NB. context is not an instanceof DocumentEventContext

        //String subprojectIdLikePattern = (String) context.getProperty("eventCategory");
        final String nxqlCriteria = (String) context.getProperty("eventCategory");

        RepositoryManager mgr;
        try {
            mgr = Framework.getService(RepositoryManager.class);
        } catch (Exception ex) {
            throw new ClientException("Cannot get RepositoryManager", ex);
        }
        
        new UnrestrictedSessionRunner(mgr.getDefaultRepository().getName()) {
            @Override
            public void run() throws ClientException {
                doRefreshResources(this.session, nxqlCriteria);
            }
        }.runUnrestricted();
    }


    protected void doRefreshResources(CoreSession session, String nxqlCriteria) throws ClientException {
        DocumentService documentService;
        try {
            documentService = Framework.getService(DocumentService.class);
        } catch (Exception ex) {
            throw new ClientException("Cannot get DocumentService", ex);
        }
        
        List<DocumentModel> resourceModels = documentService.query(session, DocumentService.NXQL_SELECT_FROM
                + "Document" + DocumentService.NXQL_WHERE + "ecm:mixinType='"
                + ResourceDownloadInfo.FACET_RESOURCEDOWNLOADINFO + "'" + DocumentService.NXQL_AND
                //+ SubprojectNode.XPATH_SUBPROJECT + " LIKE '" + subprojetIdPattern + "'",
                + nxqlCriteria,
                true, false);
        
        for (DocumentModel resourceModel : resourceModels) {
            if (resourceModel.isVersion()) {
                continue; // obviously can't work on versions
            }
            
            // reset timestamp, which will trigger redownload when ResourceListener listens to change :
            resourceModel.setPropertyValue(ResourceDownloadInfo.XPATH_TIMESTAMP, null);
            session.saveDocument(resourceModel);
        }
        session.save();
    }

}
