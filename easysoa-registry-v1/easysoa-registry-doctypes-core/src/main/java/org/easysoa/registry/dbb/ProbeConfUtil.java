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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * @author jguillemotte
 */
public class ProbeConfUtil {

    /**
     * 
     * @param probe
     * @param event
     * @return 
     */
    public static boolean isResourceProbeEventCustom(String probeType, String probeInstanceId) {
        if ("myCustomProbeTypeUsingACustomEvent".equals(probeType)) { // example
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public static boolean isSelfUpdated(){
        return false;
    }
    
    /**
     * 
     * @return A ResourceUpdaterService
     */
    public static ResourceDownloadService getResourceDownloadService(String probeType, String probeInstanceId) {
        if ("myCustomProbeTypeUsingACustomEvent".equals(probeType)) { // example
            return null; // return remote ResourceDownloadService exposed by probe
        }
        try {
            // else default : use Registry's default ResourceDownloadService
            return Framework.getService(ResourceDownloadService.class);
        } catch (Exception ex) {
            throw new RuntimeException("Can't get ResourceDownloadService", ex);
        }
    }
    
}
