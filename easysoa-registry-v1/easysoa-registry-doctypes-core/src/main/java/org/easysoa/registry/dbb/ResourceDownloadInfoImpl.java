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

package org.easysoa.registry.dbb;

import java.io.File;
import org.easysoa.registry.types.ResourceDownloadInfo;

/**
 *
 * @author jguillemotte
 */
public class ResourceDownloadInfoImpl implements ResourceDownloadInfo {

    private File file;
    private String timestamp;
    private String url;
    private String downloadableURL;
    private String probeType;
    private String probeInstanceId;

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getDownloadableUrl() {
        return this.downloadableURL;
    }

    @Override
    public void setDownloadableUrl(String downloadableURL) {
        this.downloadableURL = downloadableURL;
    }

    @Override
    public String getProbeType() {
        return probeType;
    }

    @Override
    public void setProbeType(String probeType) {
        this.probeType = probeType;
    }

    @Override
    public String getProbeInstanceId() {
        return probeInstanceId;
    }

    @Override
    public void setProbeInstanceId(String probeInstanceId) {
        this.probeInstanceId = probeInstanceId;
    }

}
