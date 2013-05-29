package org.easysoa.registry.types.listeners;

import org.apache.log4j.Logger;
import org.easysoa.registry.SoaNodeMatchingListener;
import org.easysoa.registry.types.Component;
import org.nuxeo.common.collections.ScopeType;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

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
