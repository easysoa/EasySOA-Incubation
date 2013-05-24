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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author jguillemotte
 */
public interface ResourceParsingService {  

    /**
     * 
     * @param soaNodeDocModel an SOA node
     * @return whether this document can store WsdlInfo metas (i.e. wsdlPortTypeName...)
     * NB. WsdlInfo can be filled from various sources :
     * * WSDL files (ex. provided as content of the same Resource SOA node document) from various disco
     * * JAXWS implementations from source probe
     * 
     * TODO LATER maybe store or copy WsdlInfo in separate metas across types, so
     * an iserv's WsdlInfo can coexist on one of its endpoint with this endpoint's wsdlInfo ??
     */
    public boolean isWsdlInfo(DocumentModel soaNodeDocModel);
    
    /**
     * 
     * @param soaNodeDocModel an SOA node
     * @return whether this document is a Resource that holds a WSDL XML file
     */
    public boolean isWsdlFileResource(DocumentModel soaNodeDocModel);
    
    /**
     * Parses Resource file, extracts and sets metas
     * @param sourceDocument
     * @return
     * @throws ClientException 
     */
    public boolean extractMetas(DocumentModel sourceDocument) throws ClientException;
    
}
