/**
 * EasySOA Registry
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

package org.easysoa.registry.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Clients can use this sub-interface to tell their JAXRS engine to use RegistryAPI through JSON.
 * See RegistryAPI.
 * 
 * Defined in Jackson 1.8.1.
 * (see how to migrate to 2.0 at http://www.cowtowncoder.com/blog/archives/2012/04/entry_469.html )
 */
@Path("easysoa/registry") // even if already in itf
//because Jersey doesn't (mostly) inherit @Path, see
//http://jersey.576304.n2.nabble.com/Inheritance-of-Annotations-and-Jersey-td3225397.html
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RegistryJsonApi extends RegistryApi {

}
