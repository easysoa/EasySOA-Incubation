
    <#macro escapeUrl path>${path?replace('/', '____')?url?replace('____', '/')}</#macro>
    
    <#--
        see http://doc.nuxeo.com/display/NXDOC/Navigation+URLs , http://answers.nuxeo.com/questions/3203/how-to-buildrequest-a-previewdownload-url-for-a-document
    -->
    <#macro urlToLocalNuxeoDocumentsUi doc>
        <@urlToLocalNuxeoDocumentsUiShort doc['path']/>
    </#macro>
    <#macro urlToLocalNuxeoDocumentsUiShort doc>/nuxeo/nxpath/default<@escapeUrl doc/>@view_documents</#macro>
    <#macro urlToLocalNuxeoDownload doc>/nuxeo/nxfile/default/${doc.id}/blobholder:0/${doc['file:content']['filename']}</#macro>
    <#macro urlToLocalNuxeoDownloadAttachment doc i>/nuxeo/nxfile/default/${doc.id}/files:files/${i}/file/${doc['files:files'][i]['filename']}</#macro>
    <#macro urlToLocalNuxeoPreview doc>/nuxeo/nxdoc/default/${doc.id}/preview_popup</#macro>
    <#macro urlToLocalNuxeoPrint doc>/nuxeo/site/admin/repository<@escapeUrl doc['path']/>/@views/print</#macro>
    