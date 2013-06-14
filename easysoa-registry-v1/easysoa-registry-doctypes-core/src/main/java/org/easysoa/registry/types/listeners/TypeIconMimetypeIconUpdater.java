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

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.filemanager.core.listener.MimetypeIconUpdater;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeEntry;


/**
 * Overriding so document icons will always stay set to their type icon and never become their mimetype
 * icon (otherwise, WSDL etc. mimetypes should be given pretty service + XML / graffle.png icons)
 * @author mdutoo
 *
 */
public class TypeIconMimetypeIconUpdater extends MimetypeIconUpdater {

	@Override
	public void updateIconField(MimetypeEntry mimetypeEntry, DocumentModel doc) throws Exception {
		super.updateIconField(null, doc);
	}

}
