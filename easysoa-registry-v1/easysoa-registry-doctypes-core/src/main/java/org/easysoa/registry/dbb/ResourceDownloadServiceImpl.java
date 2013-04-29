/**
 * 
 */
package org.easysoa.registry.dbb;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

/**
 * @author jguillemotte
 *
 */
public class ResourceDownloadServiceImpl implements ResourceDownloadService {

    private String resourceDownloaderAddress = "http://localhost:7080/get";
    
    @Override
    public File get(URL url) throws Exception {

        // First try : Connect to FraSCAti studio download service (TODO : this service must be exposed as REST service ...)
        try {
            Client client = Client.create();
            client.setConnectTimeout(3000); // Set timeout to 3 seconds
            WebResource webResource = client.resource(resourceDownloaderAddress);
            
            // TODO for frascati service side : Add a parameter to pass the url
            webResource.setProperty("fileURL", url.toURI().toString());
            
            // Get the resource
            File resource = webResource.get(File.class);
            return resource;
        }
        catch(Exception ex){
            // Error or timeout, try the second donwload method
        }
        
        // If no response : Use local downloader service
        return download(url);
    }

    /**
     * 
     * @param url
     * @return
     * @throws Exception 
     */
    private File download(URL url) throws Exception{
        HttpDownloaderService httpDownloaderService = new HttpDownloaderServiceImpl();
	HttpDownloader fileDownloader = httpDownloaderService.createHttpDownloader(url);
        fileDownloader.download();
        return fileDownloader.getFile();
    }

    /**
     * Read data from File
     * @param file
     * @return 
     */
    @Override
    public String getData(File file) throws Exception {
        String data;
        FileInputStream fis = null;
        try {
            if (file == null) {
                // ex. if site root url returns something else than 200 (ex. 403)
                return ""; // else cleaner.clean(siteRootFile) throws NullPointerException 
            }
            fis = new FileInputStream(file);
            StringBuilder dataBuffer = new StringBuilder();
            int c;
            while ((c = fis.read()) != -1) {
                dataBuffer.append((char) c);
            }
            data = dataBuffer.toString();
        }
        catch (Exception e) {
                data = null;
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return data;
    }
    
    //@Reference
    //protected List<UrlPatternWebResourceDownloadService> downloadServices;
    
    /* (non-Javadoc)
     * @see org.easysoa.registry.rest.integration.resources.RoutingWebResourceDownloadService#get(java.net.URL)
     */
    /*@Override
    @Path("/get/")
    @GET
    public File get(URL url, PreferencesManagerItf preferences) throws Exception {

        // - Sort the list, to have the webResourceDownloadServices in the specified order
        // Need to be sorted manually because FraSCAti doesn't provide a native way to specify an execution order
        Collections.sort(downloadServices, new DownloadServicesComparator());
        
        for(UrlPatternWebResourceDownloadService service : downloadServices){
            if(service.matchUrl(url)){
                // file type is always WSDL ??
                File localTempFolder = new File(tempFolder + "tempWsdl");
                if(!localTempFolder.exists()){
                    localTempFolder.mkdir();
                }
                File localWsdlFile = new File(tempFolder + "tempWsdl/temp_" + System.currentTimeMillis() + ".wsdl");
                if (!localWsdlFile.createNewFile()) {
                    throw new IOException("Unable to create local WSDL file at " + localWsdlFile.getAbsolutePath());
                }
                // TODO closes ?
                IOUtils.write(service.get(url), new FileOutputStream(localWsdlFile));
                return localWsdlFile;
                //return Response.ok(service.get(url)).type("text/xml").build();
            }
        }
        //return Response.serverError().build();
        return null;
    }
*/    
}
