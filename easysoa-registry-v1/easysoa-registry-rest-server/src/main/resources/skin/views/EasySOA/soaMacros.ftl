
        <#include "/views/EasySOA/macros.ftl">
        <#include "/views/EasySOA/docMacros.ftl">
    
		
		<#macro displayOperationParameters params>
		   <#if params?has_content && params?length != 0>
		      <br/>
		      <div style="margin-left:10px; font-size:90%; color:grey;">${params?replace(', ', ')</span><br/>')?replace('=', ' <span style="font-size:90%">(')})</div
		      <br/>
		   </#if>
		</#macro>
        
        <#macro displayOperations iserv_operations impl_operations>
    		<table style="width: 600px;">
                <#if iserv_operations?size != 0>
                    <#-- title (for RPC style operation display) NOT REQUIRED -->
    		        <#-- tr>
    		            <th style="font-weight: bold;">Name</th>
    		            <th style="font-weight: bold;">Parameters</th>
    		            <th style="font-weight: bold;">Returns</th>
    		            <th style="font-weight: bold;">Documentation</th>
    		        </tr -->
                    <#list iserv_operations as iserv_operation>
                    <tr><td>
                    <table style="border-spacing: 0px 10px;">
                        <#-- RPC style operation display : -->
                        <#-- tr>
                            <td><h:outputText value="${entry.get('operationName').getValue()}" /></td>
                            <td><h:outputText value="${entry.get('operationParameters').getValue()}/>" /></td>
                            <td><h:outputText value="${entry.get('operationReturnParameters').getValue()}" /></td>
                            <td><h:outputText value="${entry.get('operationDocumentation').getValue()}" /></td>
                        </tr -->
                        <#-- in/out (message) style operation display : -->
                        <#-- finding corresponding operation in (first) non mock impl : -->
                        <#if impl_operations?is_sequence>
                            <#list impl_operations as impl_operation_cur>
                                <#if impl_operation_cur['operationName'] = iserv_operation['operationName']>
                                    <#assign impl_operation = impl_operation_cur>
                                </#if>
                            </#list>
                        </#if><#-- else no impl_operations -->
                        <#-- displaying mashupped iserv & impl operation, with pretty display of enum values : -->
                        <tr>
                            <td colspan="2" style="font-weight: bold; text-decoration: underline;">${iserv_operation['operationName']}</td>
                            <td style="font-style: italic;">${iserv_operation['operationDocumentation']}<#if impl_operation?has_content> (${impl_operation['operationDocumentation']})</#if></td>
                        </tr>
                        <tr>
                            <td title="${iserv_operation['operationInContentType']}">  <b>In</b> <span style="color:grey"><@displayMimeType iserv_operation['operationInContentType']/></span></td>
                            <td colspan="2">${iserv_operation['operationParameters']}<#if impl_operation?has_content> [<@displayOperationParameters impl_operation['operationParameters']/>]</#if></td>
                        </tr>
                        <tr>
                            <td title="${iserv_operation['operationOutContentType']}">  <b>Out</b> <span style="color:grey"><@displayMimeType iserv_operation['operationOutContentType']/></span></td>
                            <td colspan="2">${iserv_operation['operationReturnParameters']}<#if impl_operation?has_content> [<@displayOperationParameters impl_operation['operationReturnParameters']/>]</#if></td>
                        </tr>
                    </table>
                    </td></tr>
                    </#list>
                <#else>
                    <tr>
                        <td colspan="3" style="text-align: center">No operations.</td>
                    </tr>
                </#if>
            </table>
        </#macro>

		
		<#macro displayMimeType mimeType>
		   <#assign mimeTypePrettyName = mimeTypePrettyNames[mimeType]/>
		   <#if !mimeTypePrettyName?has_content>
		      <#assign mimeTypePrettyName = mimeType/>
		   </#if>
		   <span title="${mimeType}">${mimeTypePrettyName}</span>
		</#macro>

        <#macro displayFile service_files_file downloadUrlPrefix>
                <tr>
                    <#-- <@urlToLocalNuxeoDownloadAttachment service service_files_file_index/>" -->
                    <td colspan="3"><a href="${downloadUrlPrefix}${service_files_file['filename']}">${service_files_file['filename']}
                    <#if service_files_file['file']['mimeType']?has_content && service_files_file['file']['mimeType']?starts_with('image/')>
                       <img src="${downloadUrlPrefix}${service_files_file['filename']}" height="35" width="35"/>
                    </#if>
                    </a></td>
                    <td><@displayMimeType service_files_file['file']['mimeType']/></td>
                    <td>${service_files_file['file']['length']}</td>
                    <td><span style="color:grey" title="${service_files_file['file']['digest']}">digest</td>
                </tr>
        </#macro>

		<#macro displayFiles service_files_files>
            <#-- assign service_files_files=service['files:files']>
            <#assign service_children=service['children'] --><#-- TODO LATER docs, Resources ?! -->
            <table style="width: 600px;">
            <#if service_files_files?size != 0>
                <tr><td>
                <table style="border-spacing: 0px 10px;">
                <#list service_files_files as service_files_file>
                    <@displayFile service_files_file '/nuxeo/nxfile/default/${service.id}/files:files/${service_files_file_index}/file/'/>
                </#list>
                </table>
                </td></tr>
            <#else>
                <tr>
                    <td colspan="3" style="text-align: center">Pas de fichiers joints</td>
                </tr>
            </#if>
            <#-- TODO LATER also children documents -->
            </table>
		</#macro>


        <#macro displayTested serviceimpl>
           <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
           <#list Root.getDocumentService().getSoaNodeChildren(deliverable, 'ServiceConsumption') as servicecons>
              <#-- ${servicecons['javasc:consumedInterface']} ?= ${serviceimpl['javasi:implementedInterface']}; -->
              <#if servicecons['javasc:consumedInterface'] = serviceimpl['javasi:implementedInterface']
                    && servicecons['javasc:consumerClass']?ends_with('Test')>
                 <#-- && serviceimpl['serviceimpl:ismock'] = 'false' -->
                 <#assign foundTestServiceCons = servicecons/>
                 <!-- TODO + location -->
              </#if>
           </#list>
           <#if foundTestServiceCons?has_content>
              <li><span title="${deliverable['title']} / ${foundTestServiceCons['javasc:consumerClass']}">TESTED
              (${foundTestServiceCons['javasc:consumerClass']?substring(foundTestServiceCons['javasc:consumerClass']?last_index_of('.') + 1, foundTestServiceCons['javasc:consumerClass']?length)})</span></li> 
           <#else>
              <li>not tested</li>
           </#if>
        </#macro>
		