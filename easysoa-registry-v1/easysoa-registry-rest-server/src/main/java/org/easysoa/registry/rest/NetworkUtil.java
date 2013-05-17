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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

/**
 * Network Helper
 * 
 * @author mdutoo
 */
public class NetworkUtil {

    // cache
    private static String guessedNetworkHost = null;

    /**
     * Returns first non (Ipv4) loopback host known of network interfaces.
     * TODO LATER move it to wider shared code
     * @return
     */
	public static String guessNetworkHost() {
		synchronized(EasysoaModuleRoot.class) {
			if (guessedNetworkHost == null) {
				// guessing host from network
		    	// NB. not using same code as previously (dbb node,  open a connection to google)
				// because wouldn't work offline
		    	try {
					for (NetworkInterface netItf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
						for (InetAddress inetAddr : Collections.list(netItf.getInetAddresses())) {
							String serverHost = inetAddr.getHostAddress();
							if (!serverHost.equals("localhost") && !serverHost.startsWith("127.")) {
								guessedNetworkHost = serverHost;
								return guessedNetworkHost;
							}
						}
					}
				} catch (SocketException e) {
					// silent failure
				}
		    	
				// else falling back to local host
		    	guessedNetworkHost = "localhost";
			}
		}
		
		return guessedNetworkHost;
	}
    
}
