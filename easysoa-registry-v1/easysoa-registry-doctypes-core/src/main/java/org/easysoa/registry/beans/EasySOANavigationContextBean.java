/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.easysoa.registry.beans;

import static org.jboss.seam.ScopeType.CONVERSATION;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.webapp.context.NavigationContextBean;

/**
 * Overriden to find the doc to navigate to under its actual parent
 * rather than under its proxy parent.
 * This is a hack for SoaNode created below a proxy to an SoaNode
 * (typically a TaggingFolder accessed outside of the Repository).
 * TODO or redirect any navigationt o a (SOA node) proxy to its actual doc ??
 * NB. UI (JSF components) will still throw NoSuchDocumentException in SQLSession.getDocumentByUUID("")
 * but they won't be displayed.
 */
@Name("navigationContext")
@Scope(CONVERSATION)
@Install(precedence = Install.APPLICATION) // overrides through higher precedence than default @Install(precedence = FRAMEWORK)
// see http://answers.nuxeo.com/questions/411/how-configuring-the-default-page-after-the-user-connexion
public class EasySOANavigationContextBean extends NavigationContextBean {

	private static final long serialVersionUID = -99857267499249083L;

	public String navigateToRef(DocumentRef docRef) throws ClientException {
        if (!documentManager.exists(docRef)) {
	        if (docRef instanceof PathRef) {
	        	// building parent path and doc name out of path
	        	String path = ((PathRef) docRef).value;
	        	int slashLastIndex = path.lastIndexOf('/');
	        	String parentPath = path.substring(0, slashLastIndex);
	        	String docName = path.substring(slashLastIndex + 1, path.length());
	        	PathRef parentPathRef = new PathRef(parentPath);
	        	DocumentModel parent = documentManager.getDocument(parentPathRef);
	        	if (parent.isProxy()) {
	        		docRef = getDocumentBelowProxyParent(parent, docName).getRef();
	        		// NB. this throws NoSuchDocumentException if none, but would have happened anyway
	        	}
	        }
        }

        return super.navigateToRef(docRef);
    }
	
    public String navigateToDocument(DocumentModel doc, String viewId)
            throws ClientException {
        if (documentManager == null) {
            throw new IllegalStateException("documentManager not initialized");
        }

        if (!documentManager.exists(doc.getRef())) {
        	DocumentModel parent = documentManager.getDocument(doc.getParentRef());
        	if (parent.isProxy()) {
        		doc = getDocumentBelowProxyParent(parent, doc.getName());
        	}
        }
        return super.navigateToDocument(doc, "view");
    }
    
    protected DocumentModel getDocumentBelowProxyParent(DocumentModel parentDoc,
    		String docName) throws ClientException {
    	DocumentModel actualParentDoc = documentManager.getSourceDocument(parentDoc.getRef());
    	DocumentModel doc = documentManager.getChild(actualParentDoc.getRef(), docName);
		// NB. this throws NoSuchDocumentException if none, but would have happened anyway
    	return doc;
    }
}
