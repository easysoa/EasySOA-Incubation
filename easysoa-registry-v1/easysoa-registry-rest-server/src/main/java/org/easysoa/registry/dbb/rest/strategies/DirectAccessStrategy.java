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

package org.easysoa.registry.dbb.rest.strategies;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.registry.dbb.rest.BrowsingContext;
import org.easysoa.registry.dbb.rest.FoundService;
import org.easysoa.registry.dbb.rest.ServiceFinderStrategy;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * 
 * Finds a service when its WSDL URL is given.
 * 
 * @author mkalam-alami
 *
 */
public class DirectAccessStrategy extends DefaultAbstractStrategy implements ServiceFinderStrategy {
    
    @Override
    public List<FoundService> findFromContext(BrowsingContext context) throws Exception {

        List<FoundService> foundServices = new LinkedList<FoundService>();
        
        URL url = context.getURL();
        String urlString = url.toString();
        if (urlString.toLowerCase().endsWith("?wsdl")) {
            
            // Guess service name
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            Description desc = reader.read(url); // TODO use context response
            
            String serviceName = null;
            try {
                serviceName = desc.getQName().getLocalPart();
            }
            catch (Exception e) {
                String[] splitUrl = urlString.toLowerCase().split("[/.]");
                serviceName = (splitUrl[splitUrl.length - 1].contains("wsdl"))
                        ? splitUrl[splitUrl.length - 2]
                        : splitUrl[splitUrl.length - 1];
            }

            foundServices.add(new FoundService(serviceName, urlString));
            
        }
        
        return foundServices;
    }

}
