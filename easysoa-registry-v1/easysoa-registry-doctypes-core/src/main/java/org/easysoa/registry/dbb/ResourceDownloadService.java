package org.easysoa.registry.dbb;

import java.io.File;
import java.net.URL;

/**
 * 
 * @author jguillemotte
 */
public interface ResourceDownloadService {

    /**
     * Get the file corresponding to the given URL
     * @param url
     * @return
     * @throws Exception If a problem occurs
     */
    public File get(URL url) throws Exception;

    /**
     * Get the data contained in the given file
     * @param file
     * @return
     * @throws Exception 
     */
    public String getData(File file) throws Exception;
    
}