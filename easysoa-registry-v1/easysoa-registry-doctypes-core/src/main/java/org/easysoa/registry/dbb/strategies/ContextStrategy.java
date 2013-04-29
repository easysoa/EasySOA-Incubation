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
import java.util.LinkedList;
import java.util.List;

import org.easysoa.registry.dbb.ServiceFinderStrategy;
import org.easysoa.registry.dbb.BrowsingContext;
import org.easysoa.registry.dbb.FoundService;

/**
 * 
 * Scraper based on the knowledge of various services stacks,
 * to retrieve web-services by trying various URL patterns.
 * 
 * XXX: Currently highly mocked, works with the demo only.
 * 
 * @author mkalam-alami
 *
 */
public class ContextStrategy extends DefaultAbstractStrategy implements ServiceFinderStrategy {
    
    @Override
    public List<FoundService> findFromContext(BrowsingContext context) throws Exception {
        
        List<FoundService> foundServices = new LinkedList<FoundService>();
        
        // XXX: Hard-coded matching of the PAF demo services
        URL url = context.getURL();
        if (url.getPath().contains("crm")) {
            foundServices.add(new FoundService(
                    "Orders service",
                    "http://localhost:9010/PureAirFlowers?wsdl",
                    guessApplicationName(context)));
        }
        
        return foundServices;
    }

}
