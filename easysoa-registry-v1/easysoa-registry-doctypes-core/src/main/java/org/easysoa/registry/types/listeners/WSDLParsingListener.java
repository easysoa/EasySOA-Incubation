package org.easysoa.registry.types.listeners;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.registry.dbb.ResourceParsingService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * Extracts metadata from WSDL if changed.
 * 
 * Should be registered on events : documentCreated, documentModified
 * (and not beforeDocumentModification because digest not yet computed at storage level)
 * 
 * @author mdutoo
 *
 */
public class WSDLParsingListener implements EventListener {

    public static final String SOAP_CONTENT_TYPE = "application/soap+xml"; // ; charset=utf-8 http://www.w3schools.com/soap/soap_httpbinding.asp
    
    private static Logger logger = Logger.getLogger(WSDLParsingListener.class);
    
    public WSDLParsingListener() {}
    
    @Override
    public void handleEvent(Event event) throws ClientException {
		// Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        CoreSession documentManager = documentContext.getCoreSession();
        
        //if (DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()) && !sourceDocument.isDirty()) {
        //    return;
        //}
        
        //if (!sourceDocument.hasSchema(SoaNode.SCHEMA)
        //		|| sourceDocument.getPropertyValue(InformationService.XPATH_SOANAME) == null) {
		//	return;
        //}

        ResourceParsingService resourceParsingService;
        try {
            resourceParsingService = Framework.getService(ResourceParsingService.class);
        }
        catch(Exception ex){
            throw new ClientException(ex);
        }        
        
        if (!resourceParsingService.isWsdlFileResource(sourceDocument)) {
            return;
        }

        // NB. available if beforeDocumentModification event (so useless for digest which comes on documentModified)
        //DocumentModel previousDocumentModel = (DocumentModel) context.getProperty("previousDocumentModel");
        
        // TODO test it
        // TODO copy this file to ResourceListener & replace it in *listener.xml conf,
        // remove the following code from it, hook WsdlParsingListener rather on resourceDownloaded event in xml conf,
        // trigger resourceDownloaded event in ResourceUpdateService impl
        boolean documentModified = resourceParsingService.extractMetas(sourceDocument);
		
        // Save according to event type
        if (documentModified && !DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
                documentManager.saveDocument(sourceDocument);
                documentManager.save();
        } // else DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()), therefore save will be automatically done http://doc.nuxeo.com/display/NXDOC/Events+and+Listeners
    }


    /**
     * @obsolete rather written inline in WsdlBlob (or listener), but shows how it is meant to work
     * @param sourceDocument
     * @return
     * @throws ClientException
     */
    /*private static WsdlBlob getWsdlBlob(DocumentModel sourceDocument) throws ClientException {
        Blob blob = null;
        String oldWsdlFileName = (String) sourceDocument.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
        if (oldWsdlFileName != null) {
            blob = getBlob(sourceDocument, oldWsdlFileName);
        }
        WsdlBlob wsdlBlob = null;
        if (blob != null) {
            wsdlBlob = tryParsingWsdlFromBlob(blob);
        }
        if (wsdlBlob == null) {
            wsdlBlob = findWsdlBlob(sourceDocument);   
        }
        return wsdlBlob;
    }*/
	
	/**
	 * TODO LATER maybe rather sourceDocument.getAdapter(BlobHolder.class) but doesn't handle file:files
	 * 
	 * @param sourceDocument
	 * @param fileName file name of the blob to be returned, must no be null
	 * @return
	 * @throws ClientException
	 */
    public static Blob getBlob(DocumentModel sourceDocument, String fileName) throws ClientException {
        // Look for first WSDL
        Blob fileBlob = null;
        Description wsdl = null;
        
        // look in attached files
        List<?> filesInfos = (List<?>) sourceDocument.getPropertyValue("files:files");
        if (filesInfos != null && !filesInfos.isEmpty()) {
            for (Object fileInfoObject : filesInfos) {
                Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                fileBlob = (Blob) fileInfoMap.get("file");
                if (fileName.equals(fileBlob.getFilename())) {
                    return fileBlob;
                }
            }
        }
        
        if (wsdl == null) {
            // look in document content
            fileBlob = (Blob) sourceDocument.getPropertyValue("file:content");
            if (fileBlob != null && fileName.equals(fileBlob.getFilename())) {
                return fileBlob;
            }
        }
        return null;
    }
    
    public static Description tryParsingWsdlFromBlob(Blob blob) {
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
