/**
 * EasySOA Proxy
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

package org.easysoa.registry.types;

import java.io.File;

/**
 * TODO move to facets package
 * @author jguillemotte
 */
public interface ResourceDownloadInfo {

    static final String FACET_RESOURCEDOWNLOADINFO = "ResourceDownloadInfo";

    static final String XPATH_URL = "rdi:url";

    /**
     * the URL used by the probe to download the Resource file if it differs
     * from rdi:url (may happen if there is routing), else null
     */
    static final String XPATH_DOWNLOADABLE_URL = "rdi:downloadableUrl";

    static final String XPATH_TIMESTAMP = "rdi:timestamp";

    static final String TIMESTAMP_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /** probeType if any, none means Registry UI itself */
    static final String XPATH_PROBE_TYPE = "rdi:probeType";

    /** probe instance id, none means the default one */
    static final String XPATH_PROBE_INSTANCEID = "rdi:probeInstanceId";

    /**
     * Setter for File
     * @param file
     */
    public void setFile(File file);

    /**
     * Setter for timestamp
     * @param timestamp
     */
    public void setTimestamp(String timestamp);

    /**
     * Return the file (can be null)
     * @return A File
     */
    public File getFile();

    /**
     * Return the download timestamp
     * @return A String representing the download timestamp
     */
    public String getTimestamp();

    /**
     * Return the URL
     * @return A String representing the URL
     */
    public String getUrl();

    /**
     * Setter for URL
     * @param url
     */
    public void setUrl(String url);

    /**
     * Return the downloadable URL i.e. the URL used by the probe to download
     * the Resource file if it differs from url (may happen if there is routing),
     * else null
     * @return A String representing the URL
     */
    public String getDownloadableUrl();

    /**
     * Setter for downloadable URL
     * @param url
     */
    public void setDownloadableUrl(String url);


    /**
     * @return probeType if any, none means Registry UI itself
     */
    public String getProbeType();

    public void setProbeType(String probeType);

    /**
     * @return probe instance id, none means the default one
     */
    public String getProbeInstanceId();

    public void setProbeInstanceId(String probeInstanceId);
    
}
