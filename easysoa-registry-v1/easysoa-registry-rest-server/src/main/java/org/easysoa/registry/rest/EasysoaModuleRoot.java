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

package org.easysoa.registry.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Properties;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.subproject.SubprojectId;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * Base class for EasySOA controllers.
 * 
 * Provides helper methods & props to all Freemarker templates & controllers.
 * 
 * @author jguillemotte, mdutoo
 */
public class EasysoaModuleRoot extends ModuleRoot {

    /**
     * 
     * @return The current user name
     * @throws Exception 
     */
    public String getCurrentUser() throws Exception {
        String user = "";
        Principal currentUser = ctx.getPrincipal(); // SessionFactory.getSession(request).getPrincipal()
        
        if(currentUser != null){
            user = currentUser.getName();
        }
        return user;
    }

    /**
     * Provides it to Freemarker templates.
     * TODO LATER do it in a nuxeo service's init
     * @returns (LATER nuxeo.conf's nuxeo.url else) VirtualHostHelper's host
     * else returns guessed server hostname
     * (as known locally) to provide to non-local clients
     */
    public String getServerHost() {
    	// NB. how to get nuxeo.conf's nuxeo.url ?? not from org.nuxeo.common.Environment.getDefault()...
    	
    	// Trying nuxeo's VirtualHostHelper
    	// NB. handles proxying but doesn't guess locally known hostname/IP 
    	String serverUrlString = VirtualHostHelper.getServerURL(request);
    	try {
			URL serverUrl = new URL(serverUrlString);
			String serverHost = serverUrl.getHost();
			if (!serverHost.equals("localhost") && !serverHost.startsWith("127.")) {
				return serverHost;
			}
		} catch (MalformedURLException e) {
			// silent failure
		}

    	// else falling back to guessing host from network
    	return NetworkUtil.guessNetworkHost();
    }
    
    public boolean isDevModeSet() {
    	return Framework.isDevModeSet();
    }
    
    /**
     * Provides EasySOA properties to Freemarker templates (though better use dedicated getters)
     * @return
     */
    public Properties getProperties() {
    	try {
	    	DocumentService documentService = Framework.getService(DocumentService.class);
	    	return documentService.getProperties();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get DocumentService", e);
		}
    }
    
    public String getWebDiscoveryUrl() {
    	return getProperties().getProperty("webDiscovery.url", "http://" + getServerHost() + ":8083/");
    }
    
    public String getJasmineUrl() {
    	return getProperties().getProperty("jasmine.url", "http://" + getServerHost() + ":9100/jasmine/");
    }
    
    //protected String subprojectCriteria = // TODO cache
    
    public DocumentService getDocumentService() throws Exception {
    	return Framework.getService(DocumentService.class);
    }
    public UserManager getUserManager() throws Exception {
    	return Framework.getService(UserManager.class);
    }
    
    public String buildSubprojectCriteria(DocumentModel soaNodeDocModel,
    		String subprojectId) throws PropertyException, ClientException {
    	return NXQLQueryHelper.buildSubprojectCriteria(
    			soaNodeDocModel.getCoreSession(), subprojectId, true);
    }
    
    public SubprojectId parseSubprojectId(String subprojectId) throws Exception {
    	return SubprojectServiceImpl.parseSubprojectId(subprojectId);
    }
    
}
