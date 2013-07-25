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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;


/**
 * Interface allowing to get sync implementation of ResourceUpdateService
 *
 * @author mdutoo
 */
public class ConfigurableResourceUpdateServiceImpl implements ResourceUpdateService, ConfigurableResourceUpdateService {

    // Using asynchronous update by default
    private boolean synchronous = true;

    /**
     * If set to true, the synchronous update service will be used, else the asynchronous service
     * @param synchronous
     */
    @Override
    public void setSynchronousUpdateService(boolean synchronous) {
        this.synchronous = synchronous;
    }
	
	private ResourceUpdateService getResourceUpdateService() throws ClientException {
        try {
            if(synchronous){
                return Framework.getService(SyncResourceUpdateService.class);
            } else {
                return Framework.getService(AsyncResourceUpdateService.class);
                // NB. default is async, TODO rather AsyncResourceUpdateService itf and this code in ConfbleResourceUpdateServiceImpl
            }
        }
        catch(Exception ex){
            throw new ClientException("Can't get ResourceUpdateService", ex);
        }
	}

    @Override
	public boolean isNewResourceRetrieval(DocumentModel newRdi,
			DocumentModel oldRdi) throws ClientException {
		return getResourceUpdateService().isNewResourceRetrieval(newRdi, oldRdi);
	}

    @Override
	public void updateResource(DocumentModel sourceDocument,
			DocumentModel previousDocumentModel,
			DocumentModel documentToUpdate)
			throws ClientException {
		getResourceUpdateService().updateResource(sourceDocument,
				previousDocumentModel, documentToUpdate);
	}

    @Override
	public void fireResourceDownloadedEvent(DocumentModel document)
			throws ClientException {
		getResourceUpdateService().fireResourceDownloadedEvent(document);
	}

    @Override
    public ResourceDownloadService getProbeResourceDownloadService(
            DocumentModel newRdi) throws ClientException {
        return getResourceUpdateService().getProbeResourceDownloadService(newRdi);
    }
	
}
