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

package org.easysoa.registry.dbb;

import java.io.FileInputStream;
import java.net.URL;

import org.easysoa.registry.dbb.HttpDownloader;
import org.easysoa.registry.dbb.HttpDownloaderService;
import org.easysoa.registry.dbb.HttpDownloaderServiceImpl;

public class BrowsingContext {

	private URL url = null;
	
	private String data = null;

	/**
	 * 
	 * @param data The response produced when the user browsed to the URL.
	 */
	public BrowsingContext(URL url, String data) throws Exception {
		this.url = url;
		this.data = data;
	}
	
	public BrowsingContext(URL url) throws Exception {
		if (url != null) {
			this.url = url;
			
			// Download the file at the given URL
	    	HttpDownloaderService httpDownloaderService = new HttpDownloaderServiceImpl();
	        HttpDownloader fileDownloader = httpDownloaderService.createHttpDownloader(url);
	        FileInputStream fis = null;
	        try {
	        	fileDownloader.download();
	        	java.io.File file = fileDownloader.getFile();
		        if (file == null) {
		        	// ex. if site root url returns something else than 200 (ex. 403)
		        	return; // else cleaner.clean(siteRootFile) throws NullPointerException 
		        }
		        fis = new FileInputStream(file);
		        StringBuffer dataBuffer = new StringBuffer();
		        int c;
		        while ((c = fis.read()) != -1) {
		        	dataBuffer.append((char) c);
		        }
		        data = dataBuffer.toString();
	        }
	        catch (Exception e) {
	        	data = null;
	        }
	        finally {
	            if (fis != null) {
	                fis.close();
	            }
	        	fileDownloader.delete();
	        }
		}
	}
	
	/**
	 * @return The browsed URL
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * @return The response produced when the user browsed to the URL.
	 */
	public String getData() {
		return data;
	}
	
}
