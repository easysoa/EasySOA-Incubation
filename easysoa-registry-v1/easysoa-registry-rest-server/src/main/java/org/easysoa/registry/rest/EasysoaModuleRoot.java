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
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.EndpointMatchingService;
import org.easysoa.registry.ServiceMatchingService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.subproject.SubprojectId;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.webengine.model.Messages;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.ResourceType;
import org.nuxeo.ecm.webengine.model.WebContext;
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
    
    public final static String LANGUAGE_SESSION_ATTR_NAME = "org.easysoa.registry.rest.language";
    
    private String language = null;
 
    @Override
    public Resource initialize(WebContext ctx, ResourceType type, Object... args) {
        Resource resource = super.initialize(ctx, type, args);
        if (this.language == null) {
            this.language = computeLanguage();
        }
        return resource;
    }

    /**
     * TODO when upgrading beyond 5.7.3 replace by https://jira.nuxeo.com/browse/NXP-9887
     * @return
     */
    protected String computeLanguage() {
        HttpSession session = request.getSession(false);
        String sessionLanguage = null;
        if (session != null) {
            sessionLanguage = (String) session.getAttribute(LANGUAGE_SESSION_ATTR_NAME);
        }

        String language = request.getParameter("language");
        if (language == null || language.length() == 0) {
            language = sessionLanguage;
            if (language == null) {
                language = ctx.getLocale().getLanguage();
            }
        } else {
            if (session != null && !language.equals(sessionLanguage)) {
                session.setAttribute(LANGUAGE_SESSION_ATTR_NAME, language);
            }
        }
        return language;
    }

    /**
     * TODO when upgrading beyond 5.7.3 replace by https://jira.nuxeo.com/browse/NXP-9887
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Shortcut inspired by AbstractWebContext.getMessage(key, args), but :
     * * only wraps missing ones with '!' in dev mode
     * * allows to override client (request) locale language by language request parameter 
     * @param args
     * @param key 
     * @return
     * @throws Exception
     */
    public String msg(String key, Object ... args) throws Exception {
        //return ctx.getMessage(key, args);
        Messages messages = ctx.getModule().getMessages();
        
        try {
            String msg = messages.getString(key, this.getLanguage()); // NB. language inited in initialize()
            if (args != null && args.length > 0) {
                // format the string using given args
                msg = MessageFormat.format(msg, args);
            }
            return msg;
        } catch (MissingResourceException e) {
            if (!Framework.isDevModeSet()) {
                return key; // not dev mode : don't display warning
            }
            return '!' + key + '!';
        }
    }
    
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
     * @returns (LATER nuxeo.conf's nuxeo.url else) VirtualHostHelper's host if not localhost
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

    /**
     * Provides it to Freemarker templates.
     * TODO LATER do it in a nuxeo service's init
     * @returns (LATER nuxeo.conf's nuxeo.url else) VirtualHostHelper's url if not localhost
     * else returns it but with guessed server hostname
     * (as known locally) to provide to non-local clients
     */
    public String getServerUrl() {
    	// NB. how to get nuxeo.conf's nuxeo.url ?? not from org.nuxeo.common.Environment.getDefault()...

    	// Trying nuxeo's VirtualHostHelper
    	// NB. handles proxying but doesn't guess locally known hostname/IP
    	String serverUrlString = VirtualHostHelper.getServerURL(request);
    	try {
			URL serverUrl = new URL(serverUrlString);
			String serverHost = serverUrl.getHost();
			if (!serverHost.equals("localhost") && !serverHost.startsWith("127.")) {
				return serverUrlString;
			}

            // else falling back to guessing host from network
            return serverUrl.getProtocol() + "://" + NetworkUtil.guessNetworkHost() + ':' + serverUrl.getPort() + '/';

		} catch (MalformedURLException e) {
			// silent failure
		}
        return "[failed to guess server url]";
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

    public String getServiceScaffolderUrl() {
    	return getProperties().getProperty("serviceScaffolder.url", "http://" + getServerHost() + ":8090/");
    }
    
    //////////////////////////////
    // AXXX use case URLs :

    public String getApvPivotal_url(){
        return getProperties().getProperty("apvPivotal.url", "http://" + getServerHost() + ":7080/pivotal/index.html");
    }

    public String getAxxxDpsApv_url(){
        return getProperties().getProperty("AxxxDpsApv.url", "http://" + getServerHost() + ":7080/apv/index.jsp");
    }


    //protected String subprojectCriteria = // TODO cache

    public DocumentService getDocumentService() throws Exception {
    	return Framework.getService(DocumentService.class);
    }
    public ServiceMatchingService getServiceMatchingService() throws Exception {
        return Framework.getService(ServiceMatchingService.class);
    }
    public EndpointMatchingService getEndpointMatchingService() throws Exception {
        return Framework.getService(EndpointMatchingService.class);
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
