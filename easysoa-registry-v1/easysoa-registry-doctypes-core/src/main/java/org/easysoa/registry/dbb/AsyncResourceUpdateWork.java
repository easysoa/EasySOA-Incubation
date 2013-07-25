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
import org.easysoa.registry.types.ResourceDownloadInfo;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Does the work of downloading and triggering parsing of Resource in an async manner.
 * Does so in its own configurable work queue to allow scalability without impeding
 * other Nuxeo async features.
 *
 * @author jguillemotte, mdutoo
 */
public class AsyncResourceUpdateWork extends AbstractWork {

    private static Logger logger = Logger.getLogger(AsyncResourceUpdateWork.class);

    private DocumentModel newRdi;
    private DocumentModel oldRdi;
    private DocumentModel documentToUpdate;
    private boolean manualTransactionAtSaveTimeRatherThanManaged;
    private String category;

    /**
     * 
     * @param newRdi
     * @param oldRdi
     * @param documentToUpdate
     * @param manualTransactionAtSaveTimeRatherThanManaged
     * @param category controls the queue it goes to if it exists (else default queue)
     */
    AsyncResourceUpdateWork(DocumentModel newRdi, DocumentModel oldRdi, DocumentModel documentToUpdate,
            boolean manualTransactionAtSaveTimeRatherThanManaged, String category) {
        this.newRdi = newRdi;
        this.oldRdi = oldRdi;
        this.documentToUpdate = documentToUpdate;
        this.manualTransactionAtSaveTimeRatherThanManaged = manualTransactionAtSaveTimeRatherThanManaged;
        this.category = category;
    }

    @Override
    public String getTitle() {
        return "AsyncResourceUpdateWork";
    }

    /**
     * NB. it is better if transaction is done (manually) only
     * around save time, to avoid clogging too much resources while downloading
     * @return !manualTransactionAtSaveTimeRatherThanManaged
     */
    @Override
    protected boolean isTransactional() {
        return !manualTransactionAtSaveTimeRatherThanManaged;
    }

    @Override
    public String getCategory() {
        return category;
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

        ResourceUpdateService service = Framework.getLocalService(SyncResourceUpdateService.class);
        
        // getting a document with a session for the service below (requires it to be TODO) :
        documentToUpdate = session.getDocument(documentToUpdate.getRef());

        // Update resource & fire event
        service.updateResource(newRdi, oldRdi, documentToUpdate);
        
        // if isTransactional and scheduled postCommit, transaction is managed
        // (started and committed or rollbacked) automatically by AbstractWork
        
        if (manualTransactionAtSaveTimeRatherThanManaged) {
            // if not transactional, we can do a transaction only around save time,
            // to avoid clogging too much resources while downloading
            
            // NB. isTransactional only works if scheduled postCommit. However, if isTransactional
            // and not postCommit, it can be make to work by such a manual transaction,
            // else it wouldn't be saved when session closed
            try {
                TransactionHelper.startTransaction();
                session.saveDocument(documentToUpdate);
                session.save();
            } catch (Exception e) {
                logger.error("Error while updating resource " + documentToUpdate
                        + " to " + newRdi.getPropertyValue(ResourceDownloadInfo.XPATH_URL), e);
                if (TransactionHelper.isTransactionActive()) {
                    TransactionHelper.setTransactionRollbackOnly(); // marks for rollback
                } // else already ended or marked rollback
            } finally {
                if (TransactionHelper.isTransactionActive()) {
                    TransactionHelper.commitOrRollbackTransaction();
                } // else already ended
            }
        }
    }

}
