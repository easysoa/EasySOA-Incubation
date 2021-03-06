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

/**
 *
 * @author jguillemotte
 */
public class SynchronousResourceUpdateServiceImpl extends ResourceUpdateServiceImpl
		implements SyncResourceUpdateService {

    private static Logger logger = Logger.getLogger(SynchronousResourceUpdateServiceImpl.class);

    // Default constructor
    public SynchronousResourceUpdateServiceImpl(){

    }

    @Override
    public void updateResource(DocumentModel newRdi, DocumentModel oldRdi,
            DocumentModel documentToUpdate) throws ClientException {

        // Update resource & fire event
        super.doUpdateResourceAndFireEvent(newRdi, oldRdi, documentToUpdate);
    }

}