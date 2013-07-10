

    <#-- URL Constants -->
    <#assign web_discovery_url = Root.getWebDiscoveryUrl() /><!-- VM : owsi-vm-easysoa-axxx-registry -->
    <#assign proxy_management_url = 'http://' + Root.getServerHost() + ':9080/easysoa-proxy-web' /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign http_proxy_host = Root.getServerHost() /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign http_proxy_port = '8082' /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign jasmine_url = Root.getJasmineUrl() /><!-- VM : owsi-vm-easysoa-axxx-pivotal -->
    <#assign frascatiStudio_url = 'http://' + Root.getServerHost() + ':7080/easySoa/' /><!-- VM : owsi-vm-easysoa-axxx-registry -->


    <#macro escapeUrl path>${path?replace('/', '____')?url?replace('____', '/')}</#macro>

    <#-- Nuxeo URLs -->
    <#-- see http://doc.nuxeo.com/display/NXDOC/Navigation+URLs , http://answers.nuxeo.com/questions/3203/how-to-buildrequest-a-previewdownload-url-for-a-document -->
    <#macro urlToLocalNuxeoDocumentsUi doc>
        <@urlToLocalNuxeoDocumentsUiShort doc['path']/>
    </#macro>
    <#macro urlToLocalNuxeoDocumentsUiShort doc>/nuxeo/nxpath/default<@escapeUrl doc/>@view_documents</#macro>
    <#macro urlToLocalNuxeoDocumentsUiEdit doc><@urlToLocalNuxeoDocumentsUiShort doc/>?tabIds=%3ATAB_EDIT</#macro>
    <#macro urlToLocalNuxeoDownload doc>/nuxeo/nxfile/default/${doc.id}/blobholder:0/${doc['file:content']['filename']}</#macro>
    <#macro urlToLocalNuxeoDownloadAttachment doc i>/nuxeo/nxfile/default/${doc.id}/files:files/${i}/file/${doc['files:files'][i]['filename']}</#macro>
    <#macro urlToLocalNuxeoPreview doc>/nuxeo/nxdoc/default/${doc.id}/preview_popup</#macro>
    <#macro urlToLocalNuxeoPrint doc>/nuxeo/site/admin/repository<@escapeUrl doc['path']/>/@views/print</#macro>

    <#-- EasySOA tool URLs -->
    <#macro urlToFicheSOA soaNode subprojectId visibility>${Root.path}/../services/path${soaNode['spnode:subproject']?xml}:${soaNode.type}:${soaNode['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility}</#macro>
    <#macro urlToEndpointState endpoint subprojectId visibility>${Root.path}/../monitoring/envIndicators/${endpoint.id}?subprojectId=${subprojectId}&visibility=${visibility}</#macro>
    <#macro urlToProxyManagementGetInstance subprojectId environment>${proxy_management_url}/management/getInstance.html?projectId=${subprojectId?url('ISO-8859-1')}&userLogin=${Root.currentUser?url('ISO-8859-1')}&environment=${environment?url('ISO-8859-1')}</#macro>
    <#macro urlToServiceScaffolder endpoint>${web_discovery_url + '/scaffoldingProxy/?wsdlUrl=' + endpoint['rdi:url']}</#macro>
    <#macro urlToSoapUIProjectConf endpoint>${Root.path}/../soapui/${endpoint.id}.xml</#macro>

    <#macro displayImplementationTools subprojectId visibility>
      <b>Outils : </b> Découverte
      <a href="${Root.path}/../cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Source</a>,
      <a href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Mise en correspondance</a>
    </#macro>

    <#macro displayEndpointTools productionEndpoint subprojectId visibility>
      <b>Outils :</b><br/>
      Découverte :
      <a href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}">Web</a>, <!-- TODO pass as probe params : Environment (, iserv/endpoint (, user, component)) -->
      <a href="<@urlToProxyManagementGetInstance subprojectId 'Production'/>">Monitoring</a>,  <!-- TODO HTTP Proxy host prop, url to probe IHM, pass as probe params : subproject / Phase, Environment (, iserv/endpoint (, component)) -->
      <br/>
      Monitoring :
      <a href="<@urlToEndpointState productionEndpoint subprojectId visibility/>">Usage</a>,
      <a href="${Root.path}/../monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}">Statistiques</a>
      <br/>
      Tester avec :
        <a href="<@urlToServiceScaffolder productionEndpoint/>">Service Scaffolder</a>, <!-- TODO light.js, or function, rather endpointUrl?wsdl ?? -->
        <a href="<@urlToSoapUIProjectConf productionEndpoint/>">SOAPUI</a>,
        <a href="${frascatiStudio_url}">FraSCAti Studio</a>,
        <a href="${frascatiStudio_url}easySoa/GeneratedAppService">FraSCAti Studio new application</a>
        <!-- a href="">FraSCAti Studio application A</a -->
    </#macro>
