/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
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

package org.easysoa.registry.dbb.strategies;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.dbb.BrowsingContext;
import org.easysoa.registry.dbb.ResourceDownloadService;
import org.easysoa.registry.dbb.ServiceFinderStrategy;
import org.easysoa.registry.dbb.HttpDownloader;
import org.easysoa.registry.dbb.HttpDownloaderService;
import org.easysoa.registry.dbb.HttpDownloaderServiceImpl;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * Abstract strategy which provides a way to retrieve the application name.
 * 
 * @author mkalam-alami
 *
 */
public abstract class DefaultAbstractStrategy implements ServiceFinderStrategy {

    private static final Log log = LogFactory.getLog(DefaultAbstractStrategy.class);
    
    private static HtmlCleaner cleaner = new HtmlCleaner();
    
    /**
     * Guesses an application name from the given URL, by retrieving
     * the site's root title, else the given page title. 
     * @param url
     * @return The application name or null if neither of the pages has a title tag.
     * @throws Exception 
     */
    protected static String guessApplicationName(BrowsingContext browsingContext) throws Exception  {
        URL siteRootUrl = new URL(browsingContext.getURL().getProtocol() + "://" + browsingContext.getURL().getHost()
                + ":" + ((browsingContext.getURL().getPort() == -1) ? 80 : browsingContext.getURL().getPort()));
        String applicationName = extractApplicationNameFromUrl(siteRootUrl);
        if (applicationName == null) {
            applicationName = extractApplicationNameFromUrl(browsingContext.getURL());
        }
        return applicationName;
    }
    
    private static String extractApplicationNameFromUrl(URL url) throws Exception {
        ResourceDownloadService resourceDownloadService = Framework.getService(ResourceDownloadService.class); 
        java.io.File siteRootFile = resourceDownloadService.get(url);
        if (siteRootFile == null) {
        	// ex. if site root url returns something else than 200 (ex. 403)
        	return null; // else cleaner.clean(siteRootFile) throws NullPointerException 
        }
        try {
            TagNode siteRootCleanHtml = cleaner.clean(siteRootFile);
            return extractApplicationName(siteRootCleanHtml);
        }
        catch (StackOverflowError e) {
            log.warn("HtmlCleaner stack overflow while parsing " + url + ", cannot fetch app name");
            return null;
        }
    }

    private static String extractApplicationName(TagNode html) {
        TagNode[] titles = html.getElementsByName("title", true);
        if (titles.length > 0) {
            return titles[0].getText().toString().trim();
        }
        else {
            return null;
        }
    }

}
