/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
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

package org.easysoa.preview;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.impl.blob.StreamingBlob;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.core.convert.cache.SimpleCachableBlobHolder;
import org.nuxeo.ecm.core.convert.extension.Converter;
import org.nuxeo.ecm.core.convert.extension.ConverterDescriptor;
import org.nuxeo.runtime.services.streaming.InputStreamSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converter that uses XSLT to generate HTML view from a WSDL Because WSDL files
 * do not have a specific Mime-Type this converter needs to be called by it's
 * name ...
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
public class WSDLToHtmlConverter implements Converter {

    private static final Log log = LogFactory.getLog(WSDLToHtmlConverter.class);

    @Override
    public BlobHolder convert(BlobHolder blobHolder, Map<String, Serializable> params) throws ConversionException {
        List<Blob> blobHolderBlobs = null;
        try {
            blobHolderBlobs = blobHolder.getBlobs();

            if (blobHolderBlobs == null || blobHolderBlobs.isEmpty()) {
                return null;
            }

            List<Blob> blobs = new ArrayList<Blob>();
            for (Blob blob : blobHolderBlobs) {
                Blob e = generateHtml(blob);
                if (e == null) {
                    continue;
                }

                blobs.add(e);
            }

            if (blobs.isEmpty()) {
                throw new ClientException("Any successful wsdl conversion.");
            }

            // Put JS file
            Blob jsBlob = new StreamingBlob(new InputStreamSource(
                    WSDLToHtmlConverter.class
                            .getResourceAsStream("/XSLT/wsdl-viewer.js")));
            jsBlob.setFilename("wsdl-viewer.js");
            blobs.add(jsBlob);
            return new SimpleCachableBlobHolder(blobs);
        } catch (ClientException e) {
            throw new ConversionException("Unable to generate HTML preview", e);
        }
    }

    protected Blob generateHtml(Blob blob) {
        File out = null;

        try {
            out = File.createTempFile("wsdlToHtml", ".html");
            WSDLTransformer.generateHtmlView(blob.getStream(),
                    new FileOutputStream(out));

            Blob mainBlob = new FileBlob(new FileInputStream(out), "text/html",
                    "UTF-8");
            mainBlob.setFilename("index-" + blob.getDigest() + ".html");

            return mainBlob;
        } catch (Exception e) {
            log.info("Unable to generato HTML preview for blob: " + blob.getFilename());
            log.debug(e, e);

            return null;
        } finally {
            if (out != null) {
                boolean delete = out.delete();
                if (!delete) {
                    log.warn("Unable to delete output file " + out.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void init(ConverterDescriptor desc) {
        // NOP
    }
}
