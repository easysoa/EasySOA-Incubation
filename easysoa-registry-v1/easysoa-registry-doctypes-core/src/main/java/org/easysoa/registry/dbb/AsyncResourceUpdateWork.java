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

package org.easysoa.registry.dbb;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class AsyncResourceUpdateWork extends AbstractWork {

    private static Logger logger = Logger.getLogger(AsyncResourceUpdateWork.class);

    private DocumentModel newRdi;
    private DocumentModel oldRdi;
    private DocumentModel documentToUpdate;
    private ResourceDownloadService resourceDownloadService;

    AsyncResourceUpdateWork(DocumentModel newRdi, DocumentModel oldRdi, DocumentModel documentToUpdate, ResourceDownloadService resourceDownloadService) {
        this.newRdi = newRdi;
        this.oldRdi = oldRdi;
        this.documentToUpdate = documentToUpdate;
        this.resourceDownloadService = resourceDownloadService;
    }

    @Override
    public String getTitle() {
        return "AsyncResourceUpdateWork";
    }

    @Override
    public void work() throws Exception {
    	String repositoryName = null;
        initSession(repositoryName);
        // if the runtime has shutdown (normally because tests are finished)
        // this can happen, see NXP-4009
        if (session.getPrincipal() == null) {
            return;
        }

        ResourceUpdateService service = Framework.getService(SyncResourceUpdateService.class);
        
        // getting a document with a session for the service below (requires it to be TODO) :
        documentToUpdate = session.getDocument(documentToUpdate.getRef());

        // Update resource & fire event
        service.updateResource(newRdi, oldRdi, documentToUpdate, resourceDownloadService);
    }

}