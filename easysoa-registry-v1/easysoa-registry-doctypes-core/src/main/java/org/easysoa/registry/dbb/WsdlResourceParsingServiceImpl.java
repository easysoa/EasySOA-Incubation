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
import org.apache.log4j.Logger;
import org.easysoa.registry.types.listeners.WSDLParsingListener;
import org.easysoa.registry.wsdl.WsdlBlob;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 *
 * @author jguillemotte
 */
public class WsdlResourceParsingServiceImpl implements ResourceParsingService {

    private static Logger logger = Logger.getLogger(WsdlResourceParsingServiceImpl.class);
    
    @Override
    public Description parse(/*DocumentModel documentToUpdate or*/ WsdlBlob wsdlblob) throws Exception {
        Blob blob = wsdlblob.getBlob();
        
        if (blob.getFilename().toLowerCase().endsWith("wsdl")) {
            File file = null;
            try {
                file = File.createTempFile("wsdlCandidate", null);
                blob.transferTo(file);
                WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
                
                try {
                    Description wsdl = wsdlReader.read(file.toURI().toURL());
                    return wsdl;
                }
                catch (WSDLException e) {
                    logger.info("Failed to extract or parse potential WSDL", e);
                    // Not a WSDL, continue to next file
                }
            } catch (Exception e) {
                logger.error("Failed to extract or parse potential WSDL", e);
            } finally {
                if (file != null) {
                    boolean delete = file.delete();
                    if (!delete) {
                        logger.warn("Unable to delete temp WSDL file " + file.getAbsolutePath());
                    }
                }
            }
        }
        return null;        
    }

}
