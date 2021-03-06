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

package org.easysoa.registry.dbb;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class WorkBasedAsyncResourceUpdateServiceImpl extends ResourceUpdateServiceImpl implements AsyncResourceUpdateService {

    /** dedicated Work queue for Resource download */
    public static final String RESOURCE_DOWNLOAD_QUEUE_CATEGORY = "resourceDownload";
    
    public static final String OLD_RDI_EVENT_PROP = "oldRdi";
    public static final String NEW_RDI_EVENT_PROP = "newRdi";
    
    private static Logger logger = Logger.getLogger(WorkBasedAsyncResourceUpdateServiceImpl.class);
    
    // TODO conf
    public boolean manualTransactionAtSaveTimeRatherThanManaged = true;

    @Override
    public void updateResource(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate) throws ClientException {

        // Check if update is needed
        if (!isNewResourceRetrieval(newRdi, oldRdi)) {
            // may happen when document Resource saved at parsing time
            return;
        }
        
        if (documentToUpdate.isDirty()) {
        	// write if dirty (required ex. on documentCreated & async...)
        	documentToUpdate.getCoreSession().saveDocument(documentToUpdate);
        }
        documentToUpdate.getCoreSession().save(); // not required //// TODO TDO
        
        // Get the Nuxeo WorkManager
        WorkManager service = Framework.getLocalService(WorkManager.class);

        // Add a new work in the update queue
        AsyncResourceUpdateWork work = new AsyncResourceUpdateWork(newRdi, oldRdi,
                documentToUpdate, manualTransactionAtSaveTimeRatherThanManaged,
                RESOURCE_DOWNLOAD_QUEUE_CATEGORY);
        service.schedule(work, true); // scheduling it afterCommit
                // else not saved even if isTransactional (unless new transaction manually)
    }
    
}
