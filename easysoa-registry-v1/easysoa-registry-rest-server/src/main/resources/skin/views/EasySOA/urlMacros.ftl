

    <#-- URL Constants -->
    <#assign web_discovery_url = Root.getWebDiscoveryUrl() /><!-- http://host:8083/ ; VM : owsi-vm-easysoa-axxx-registry -->
    <#assign proxy_management_url = 'http://' + Root.getServerHost() + ':9080/easysoa-proxy-web' /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign http_proxy_host = Root.getServerHost() /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign http_proxy_port = '8082' /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign jasmine_url = Root.getJasmineUrl() /><!-- http://host:9100/jasmine/ ; VM : owsi-vm-easysoa-axxx-pivotal -->
    <#assign frascatiStudio_url = 'http://' + Root.getServerHost() + ':7080/easySoa/' /><!-- VM : owsi-vm-easysoa-axxx-registry -->
    <#assign pureAirFlowers_intranet_url = 'http://' + Root.getServerHost() + ':8083/demo-intranet/index.html' />
    <#assign service_scaffolder_url = Root.getServiceScaffolderUrl() /><!-- http://host:8090/ ; VM : owsi-vm-easysoa-axxx-registry -->
    
    <#-- URL Constants - AXXX use case : -->
    <#assign apvPivotal_url = Root.getApvPivotal_url() />
    <#assign axxxDpsApv_url = Root.getAxxxDpsApv_url() />

    <#macro escapeUrl path>${path?replace('/', '____')?url?replace('____', '/')}</#macro>
    <#macro stringEnd s>
        <#-- shortened url display -->
        <#if s?length <= 30>
            <#assign start = 0/>
        <#else>
            <#assign start = s?length - 27/>
        </#if>
        ...${s?substring(start, s?length)}
    </#macro>
    <#macro stringEndWithTooltip s><span title="${s}" style="color:grey;"><@stringEnd s/></span></#macro>
    <#macro linkEnd s><a href="${s}">http://<@stringEnd s/></a></#macro>
    
    <#-- Nuxeo URLs -->
    <#-- see http://doc.nuxeo.com/display/NXDOC/Navigation+URLs , http://answers.nuxeo.com/questions/3203/how-to-buildrequest-a-previewdownload-url-for-a-document -->
    <#macro urlToLocalNuxeoDocumentsUi doc><@urlToLocalNuxeoDocumentsUiPath doc['path']/></#macro>
    <#macro urlToLocalNuxeoDocumentsUiPath path>/nuxeo/nxpath/default<@escapeUrl path/>@view_documents</#macro>
    <#macro urlToLocalNuxeoDocumentsUiEdit doc><@urlToLocalNuxeoDocumentsUiPath doc['path']/>?tabIds=%3ATAB_EDIT</#macro>
    <#macro urlToLocalNuxeoDownload doc>/nuxeo/nxfile/default/${doc.id}/blobholder:0/${doc['file:content']['filename']}</#macro>
    <#macro urlToLocalNuxeoDownloadAttachment doc i>/nuxeo/nxfile/default/${doc.id}/files:files/${i}/file/${doc['files:files'][i]['filename']}</#macro>
    <#macro urlToLocalNuxeoDownloadAny doc i><#if !i?is_number || i < 0><@urlToLocalNuxeoDownload doc/><#else><@urlToLocalNuxeoDownloadAttachment doc i/></#if></#macro>
    <#macro urlToLocalNuxeoPreview doc>/nuxeo/nxdoc/default/${doc.id}/preview_popup</#macro>
    <#macro urlToLocalNuxeoPrint doc>/nuxeo/site/admin/repository<@escapeUrl doc['path']/>/@views/print</#macro>
    <#macro urlToLocalNuxeoPrintMeta doc>/nuxeo/nxpath/default/default<@escapeUrl doc['path']/>@view_documents?theme=galaxy%2Fprint</#macro>
    <#macro urlToLocalNuxeoExportAsPdf doc>/nuxeo/nxpath/default<@escapeUrl doc['path']/>@pdf</#macro>
    <#macro urlToLocalNuxeoDocumentsSoaProject subprojectId>
        <#-- TODO make it work for versions -->
        <#if subprojectId>
            <#assign nuxeoUrl = "/nuxeo/nxdoc/default/"
                + Session.query("select * from Subproject where spnode:subproject='" + subprojectId + "'")[0].id
                + "/view_documents"/>
            <#if !subprojectId?ends_with("_v")>
                <#assign nuxeoUrl = nuxeoUrl + "?version=true" />
            </#if>
        <#else>
            <#assign nuxeoUrl = "/nuxeo/nxpath/default/default-domain@view_documents"/>
        </#if>
        ${nuxeoUrl}
    </#macro>

    <#-- Nuxeo tools with bootstrap icons (used in monitoring etc.) -->
    <#macro linkBootstrapToLocalNuxeoDocumentsUiEdit doc><a href="<@urlToLocalNuxeoDocumentsUiEdit doc/>" target="_blank"><i class="icon-edit"></i></a></#macro>
    
    <#-- Nuxeo tools (used in Fiche Service etc.) -->
    <#macro linkToLocalNuxeoDocumentsUi doc><a href="<@urlToLocalNuxeoDocumentsUi doc/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a></#macro>
    <#macro linkToLocalNuxeoDocumentsUiEdit doc><a href="<@urlToLocalNuxeoDocumentsUiEdit doc/>"><img src="/nuxeo/icons/edition.png" alt="edition"/></a></#macro>
    <#macro linkToLocalNuxeoDownload doc><a href="<@urlToLocalNuxeoDownload doc/>"><img src="/nuxeo/icons/contextual_menu/download.png" alt="download"/></a></#macro>
    <#macro linkToLocalNuxeoDownloadAttachment doc i><a href="<@urlToLocalNuxeoDownloadAttachment doc i/>"><img src="/nuxeo/icons/contextual_menu/download.png" alt="download"/></a></#macro>
    <#macro linkToLocalNuxeoPreview doc><a href="<@urlToLocalNuxeoPreview doc/>"><img src="/nuxeo/icons/contextual_menu/preview.png" alt="preview"/></a></#macro>
    <#macro linkToLocalNuxeoPrint doc><a href="<@urlToLocalNuxeoPrint doc/>"><img src="/nuxeo/icons/printer.gif" alt="print"/></a></#macro>
    <#macro linkToLocalNuxeoPrintMeta doc><a href="<@urlToLocalNuxeoPrintMeta doc/>"><img src="/nuxeo/icons/printer.gif" alt="print metadata"/></a></#macro>
    <#macro linkToLocalNuxeoExportAsPdf doc><a href="<@urlToLocalNuxeoExportAsPdf doc/>"><img src="/nuxeo/icons/contextual_menu/export_pdf.png" alt="export PDF"/></a></#macro>

    <#-- EasySOA tool URLs -->
    <#macro urlToWebDiscovery subprojectId visibility>${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}&serverName=${Root.getServerHost()}&setLng=${Root.getLanguage()}</#macro>
    <#macro urlToFicheSOA soaNode subprojectId visibility>${Root.path}/../services/path${soaNode['spnode:subproject']?xml}:${soaNode.type}:${soaNode['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility}</#macro>
    <#macro urlToEndpointState endpoint subprojectId visibility>${Root.path}/../monitoring/envIndicators/${endpoint.id}/1?subprojectId=${subprojectId}&visibility=${visibility}</#macro>
    <#macro urlToProxyManagementGetInstance subprojectId environment>${proxy_management_url}/management/getInstance.html?projectId=${subprojectId?url('ISO-8859-1')}&userLogin=${Root.currentUser?url('ISO-8859-1')}&environment=${environment?url('ISO-8859-1')}</#macro>
    <#macro urlToServiceScaffolder endpoint>${service_scaffolder_url + 'scaffoldingProxy/?wsdlUrl=' + endpoint['rdi:url']}</#macro>
    <#macro urlToSoapUIProjectConf endpoint>${Root.path}/../soapui/${endpoint.id}.xml</#macro>

    <#macro displayDocumentTools doc>
      &nbsp;<@linkToLocalNuxeoPreview doc/>
      &nbsp;<@linkToLocalNuxeoDownload doc/>
      &nbsp;<@linkToLocalNuxeoDocumentsUi doc/>
      &nbsp;<@linkToLocalNuxeoPrint doc/>
      &nbsp;<@linkToLocalNuxeoExportAsPdf doc/>
    </#macro>

    <#macro displayImplementationTools subprojectId visibility>
      <b>${Root.msg("tools")} : </b><br/>
      <span class="btn-group"><!-- span else additional p -->
         <a class="btn" href="${Root.path}/../cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("sourceDiscovery")}</a> 
         <a class="btn" href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("matchingDashboard")}</a>
      </span>
    </#macro>

    <#macro displayEndpointTools productionEndpoint subprojectId visibility>
      <b>${Root.msg("tools")} :</b><br/>
      ${Root.msg("Discovery")} :
      <span class="btn-group"><!-- span else additional p -->
          <a class="btn" href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}">Web</a> <!-- TODO pass as probe params : Environment (, iserv/endpoint (, user, component)) -->
          <a class="btn" href="<@urlToProxyManagementGetInstance subprojectId 'Production'/>">${Root.msg("monitoring")}</a>  <!-- TODO HTTP Proxy host prop, url to probe IHM, pass as probe params : subproject / Phase, Environment (, iserv/endpoint (, component)) -->
      </span>
      <br/>
      ${Root.msg("monitoring")} :
      <span class="btn-group"><!-- span else additional p -->
          <a class="btn" href="<@urlToEndpointState productionEndpoint subprojectId visibility/>">${Root.msg("Use")}</a> 
          <a class="btn" href="${Root.path}/../monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("statistics")}</a>
      </span>
      <br/>
      ${Root.msg("TestUsing")} :
      <span class="btn-group"><!-- span else additional p -->
          <a class="btn" href="<@urlToSoapUIProjectConf productionEndpoint/>">SOAPUI</a> 
          <a class="btn" href="<@urlToServiceScaffolder productionEndpoint/>">Service Scaffolder</a> <!-- disabled ; TODO light.js, or function, rather endpointUrl?wsdl ?? -->
          <a class="btn" href="${frascatiStudio_url}">FraSCAti Studio</a> 
          <#-- a class="btn" <#if fstudio_enabled>href="${frascatiStudio_url}"<#else>style="color:grey;"</#if>>FraSCAti Studio</a> -->
          <a class="btn" <#if fstudio_enabled>href="${frascatiStudio_url}easySoa/GeneratedAppService"<#else>style="color:grey;"</#if>>${Root.msg("newFraSCAtiStudioApplication")}</a>
          <#-- a class="btn" href="">FraSCAti Studio application A</a -->
      </span>
    </#macro>

    <#-- EasySOA examples URLs -->
    <#macro urlToPureAirFlowers>${pureAirFlowers_intranet_url}</#macro>
    <#macro urlToIntranet>${pureAirFlowers_intranet_url}</#macro>
    <#macro urlToApvPivotal>${apvPivotal_url}</#macro>
    <#macro urlToAxxxDpsApv>${axxxDpsApv_url}</#macro>

