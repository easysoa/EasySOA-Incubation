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

package org.easysoa.registry.systems;

import java.util.Map;
import org.easysoa.registry.types.EndpointConsumption;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author jguillemotte
 */
public class HostClassifier implements IntelligentSystemTreeClassifier {

    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(CoreSession documentManager, DocumentModel model) throws Exception {
        // Filter non-Maven documents
        if (!EndpointConsumption.DOCTYPE.equals(model.getType())) {
            return null;
        }

        // Return application name
        EndpointConsumption endpointConsumption = model.getAdapter(EndpointConsumption.class);
        return endpointConsumption.getHost();
    }

}
