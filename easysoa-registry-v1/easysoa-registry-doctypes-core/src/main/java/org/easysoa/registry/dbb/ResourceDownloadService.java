/**
 * EasySOA
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
import java.net.URL;
import org.easysoa.registry.types.ResourceDownloadInfo;

/**
 * Registry-local ResourceDownloadService (but may delegate to remote services)
 *
 * @author jguillemotte
 */
public interface ResourceDownloadService {

    /**
     * Get the file corresponding to the given URL
     * @param url
     * @return
     * @throws Exception If a problem occurs
     */
    public ResourceDownloadInfo get(URL url) throws Exception;

    /**
     * Get the file corresponding to the given URL
     * @param rdi ResourceDownloadInformation
     * @return
     * @throws Exception
     */
    public ResourceDownloadInfo get(ResourceDownloadInfo rdi) throws Exception;

    /**
     * Get the data contained in the given file
     * @param file
     * @return
     * @throws Exception
     */
    public String getData(File file) throws Exception;

}