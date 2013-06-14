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

import org.nuxeo.common.collections.ScopeType;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

/**
 * 
 * @author mdutoo
 *
 */
public abstract class EventListenerBase implements EventListener {
	
	public static final String CONTEXT_REQUEST_EASYSOA_DISABLED = "easysoaEventDisabled";

	/**
	 * Disables EasySOA event listeners
	 * @param sourceDocument
	 */
	public static void disableListeners(DocumentModel sourceDocument) {
        sourceDocument.getContextData().putScopedValue(ScopeType.REQUEST,
        		CONTEXT_REQUEST_EASYSOA_DISABLED, true);
	}
	
	/**
	 * An alternative is to get a fresh Document instance from the session :
	 * coreSession.getDocument(sourceDocument.getRef())
	 * (but NOT to use the one returned by coreSession.saveDocument(), because
	 * contextData is copied over to it)
	 * @param sourceDocument
	 */
	public static void enableListeners(DocumentModel sourceDocument) {
        sourceDocument.getContextData().remove(ScopeType.REQUEST
        		.getScopedKey(CONTEXT_REQUEST_EASYSOA_DISABLED));
	}
	
	/**
	 * To be checked by all EasySOA listeners
	 * @param context
	 * @return
	 */
	protected boolean areListenersDisabled(EventContext context) {
		return context.hasProperty(CONTEXT_REQUEST_EASYSOA_DISABLED);
	}
	
}
