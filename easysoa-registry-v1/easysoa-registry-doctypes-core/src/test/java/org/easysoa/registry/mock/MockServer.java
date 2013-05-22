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

package org.easysoa.registry.mock;

import java.io.File;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.io.FileUtils;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 * Mock server to serve static wsdl files
 * 
 * @author jguillemotte
 */
//@Path("/hello")
@Path("/mock")
//@Produces("text/html;charset=UTF-8")
@Produces("text/xml;charset=UTF-8")
@WebObject(type = "MockServer")
public class MockServer extends ModuleRoot {
 
    /*@GET
    public Object doGet() {
        return "Hello WebEngine";
    }*/

    @GET
    @Path("wsdl/{wsdlResource}")
    public Object doGet(@PathParam("wsdlResource") String wsdlResource) throws Exception{
        
        if(wsdlResource == null || "".equals(wsdlResource)){
            throw new IllegalArgumentException("The name of the wsdl must be specified");
        }
        
        // Get the wsdl file list
        String[] extensions = new String[1];
        extensions[0] = "wsdl";
        Collection<File> fileList = FileUtils.listFiles(new File("src/test/resources/"), extensions, false);
        
        for(File file : fileList){
            // If the requested file is in the list return it
            if(file.getName().toLowerCase().contains(wsdlResource.toLowerCase())){
                return FileUtils.readFileToString(file);
            }
        }
        throw new Exception("Resource '" + wsdlResource + "' not found");
     }
    
}
