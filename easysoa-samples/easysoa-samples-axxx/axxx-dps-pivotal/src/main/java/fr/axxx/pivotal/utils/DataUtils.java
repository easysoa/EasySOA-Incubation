/**
 * EasySOA AXXX Pivotal
 * 
 * Copyright (C) 2011-2012 Open Wide
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: http://www.easysoa.org
 *
 * Author: Marc Dutoo
 *
 * Contributor(s): 
 *
 */

package fr.axxx.pivotal.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data storage helpers
 */
public class DataUtils {

    private final static Logger LOG = Logger.getLogger(DataUtils.class.getCanonicalName());
    
    /**
     * Encrypts a string based on MD5 algorithm
     * 
     * @param password
     * @return
     */
    public static String crypt(String password){
                
        byte[] defaultBytes = password.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();
                    
            StringBuffer hexString = new StringBuffer();
            for (byte messageDigestElt : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & messageDigestElt));
            }
            return hexString.toString();
        } catch(NoSuchAlgorithmException nsae) {
            LOG.log(Level.SEVERE, "Encryption configuration error", nsae);
        }
        return null;
    }

}
