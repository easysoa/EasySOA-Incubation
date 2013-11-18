package org.easysoa.registry.wsdl;

import java.util.List;
import java.util.Map;

import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.types.listeners.WSDLParsingListener;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.ow2.easywsdl.wsdl.api.Description;


/**
 * Helps finding and getting info from WSDL file (Blob)
 * 
 * TODO LATER maybe refactor it to an adapter ?
 * 
 * @author mdutoo
 *
 */
public class WsdlBlob { // TODO implements ResourceBlob ? or better as ResourceAdapter ??
    private Blob blob = null;
    private Description wsdl = null;
    private DocumentModel sourceDocument;
    private boolean hasWsdlFileChangedIfAny = true; // computed after constructor
    private boolean hasWsdlFileNameChanged = true; // computed after constructor
    private boolean hasWsdlContentChanged = true; // computed after constructor if filename has not changed
    private boolean wsdlAlreadyComputed = false;
    
    /**
     * When it's finished, blob is null if and only if none or changed
     * @param sourceDocument
     * @param previousDocumentModel
     * @throws ClientException
     */
    public WsdlBlob(DocumentModel sourceDocument) throws ClientException {
        this.sourceDocument = sourceDocument;
        ///this.previousDocumentModel = previousDocumentModel;
        
        computeHasFileChanged();
    }
    
    /**
     * When it's finished, blob is null if and only if none or changed
     * @throws ClientException
     */
    private void computeHasFileChanged() throws ClientException {
        String oldWsdlFileName = (String) getSourceDocument().getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
        if (oldWsdlFileName != null) {
            blob = WSDLParsingListener.getBlob(getSourceDocument(), oldWsdlFileName);
            if (blob != null) {
                hasWsdlFileNameChanged = false;

                // blob is not null, therefore newDigest is not null either
                hasWsdlContentChanged = isNewFileDifferent();
                hasWsdlFileChangedIfAny = hasWsdlContentChanged;
            }
        }
    }
    
    /**
     * Precondition : blob is not null (there is actually a new file)
     * @throws ClientException
     */
    private boolean isNewFileDifferent() throws ClientException {
        // comparing digests
        String oldDigest = getOldDigest();
        String newDigest = getContentDigest();
        // blob is not null, therefore newDigest is not null either
        return !newDigest.equals(oldDigest);
    }

    public boolean hasFileChangedIfAny() {
        return hasWsdlFileChangedIfAny;
    }

    public boolean hasFileNameChanged() {
        return hasWsdlFileNameChanged;
    }

    public boolean hasContentChanged() throws ClientException {
        if (hasWsdlFileNameChanged) {
            getWsdl(); // make sure it's computed
        }
        return hasWsdlContentChanged;
    }
    
    /**
     * (Computes if not yet done and) returns WSDL Blob, if any
     * @return null if none
     */
    public Blob getBlob() throws ClientException {
        if (blob == null) {
            getWsdl(); // make sure it's computed
        }
        return blob;
    }
    public Description getWsdl() throws ClientException {
        if (wsdlAlreadyComputed) {
            return wsdl;
        }

        if (blob != null) {
            wsdl = WSDLParsingListener.tryParsingWsdlFromBlob(blob);
        }
        if (wsdl == null) {
            findWsdlBlob(getSourceDocument());
        }
        
        wsdlAlreadyComputed = true;
        return wsdl;
    }
    
    /**
     * Looks for first WSDL
     * @param sourceDocument
     * @param wsdlFileName guide to find it easier
     * @return
     * @throws ClientException
     */
    private void findWsdlBlob(DocumentModel sourceDocument) throws ClientException {
        // look in attached files
        List<?> filesInfos = (List<?>) sourceDocument.getPropertyValue("files:files");
        if (filesInfos != null && !filesInfos.isEmpty()) {
            for (Object fileInfoObject : filesInfos) {
                Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                blob = (Blob) fileInfoMap.get("file");
                wsdl = WSDLParsingListener.tryParsingWsdlFromBlob(blob);
            }
        }
        
        if (wsdl == null) {
            // look in document content
            blob = (Blob) sourceDocument.getPropertyValue("file:content");
            if (blob != null) {
                wsdl = WSDLParsingListener.tryParsingWsdlFromBlob(blob);
            }
        }
        
        if (wsdl != null) {
            hasWsdlContentChanged = isNewFileDifferent();
        } else {
            blob = null; // no wsdl file has been found
        }
    }
    

    private String getOldDigest() throws ClientException {
        
        // alternative 1 - using the one auto computed & stored by VCS :
        /*Blob oldBlob = null;
        if (previousDocumentModel != null && oldWsdlFileName != null) {
            String oldWsdlFileName = (String) sourceDocument.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_FILE_NAME);
            oldBlob = getBlob(previousDocumentModel, oldWsdlFileName);
            oldDigest = oldBlob.getDigest();
        } // else new document or was without wsdl so null*/
        
        // however documentUpdated.previousDocumentModel doesn't give access to the previous blob's VCS Digest
        // so alternative 2 - store it in a business metadata or our own :
        String oldDigest = (String) getSourceDocument().getPropertyValue(WsdlInfoFacet.XPATH_WSDL_DIGEST);
        
        return oldDigest;
    }

    public String getContentDigest() {
        String newDigest = null;
        
        // getting VCS' digest since already computed (in documentUpdated, but not beforeDocumentModification event)
        if (blob != null) {
            newDigest = blob.getDigest(); // NB. digest is auto computed at storage time (i.e. SQLBlob)
        }
        
        // NB. this is the same as :
        //String oldDigest = ((org.nuxeo.ecm.core.storage.sql.coremodel.SQLBlob) sourceDocument.getPropertyValue("file:content")).getBinary().getDigest();
        
        // alternative 2 - computing new digest using Nuxeo VCS algo
        // NB. alternative 1 - compute it explicitly, using FileManager's algo :
        /*try {
            FileManager fm = Framework.getService(FileManager.class); // requires org.nuxeo.ecm.platform.filemanager.core & api
            newDigest = fm.computeDigest(blob);
            //blob.setDigest(newDigest); // NO changes SQLBlob's digest, but rather makes it loose the binary -_-
        } catch (Exception e) {
            logger.warn("Error computing WSDL digest", e);
        }*/
        
        // alternative 3 : compute it explicitly, using an algo that is specific
        // to XML, WSDL (signature) or only on our extracted metadata
        
        return newDigest;
    }

    /**
     * @return the sourceDocument
     */
    public DocumentModel getSourceDocument() {
        return sourceDocument;
    }

    /**
     * @param sourceDocument the sourceDocument to set
     */
    public void setSourceDocument(DocumentModel sourceDocument) {
        this.sourceDocument = sourceDocument;
    }
    
}
