
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
                            <td style="font-style: italic;"><@formatTextToHtml iserv_operation['operationDocumentation']/><#if impl_operation?has_content> (<@formatTextToHtml impl_operation['operationDocumentation']/>)</#if></td>
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
                        <td colspan="3" style="text-align: center">${Root.msg("NoOperations")}.</td>
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

        <#macro displayFileFileContent file_content doc i>
            <#if file_content?has_content>
                <#assign hasAttachedOrChildFile=true/>
                <tr>
                    <td colspan="3"><a href="<@urlToLocalNuxeoDownloadAny doc i/>">${file_content['filename']}
                    <#if file_content['mimeType']?has_content && file_content['mimeType']?starts_with('image/')>
                       <img src="<@urlToLocalNuxeoDownloadAny doc i/>" height="35" width="35"/>
                    </#if>
                    </a></td>
                    <td><@displayMimeType file_content['mimeType']/></td>
                    <td>${file_content['length']} MB</td>
                    <td><span style="color:grey" title="${file_content['digest']}">digest</span></td><!-- TODO better : copiable !! -->
                    <td>
                        <#if !i?is_number>
                            <@displayDocumentTools doc/>
                        <#else>
                            <!-- TODO attached file document tools -->
                        </#if>
                    </td>
                </tr>
            </#if>
        </#macro>

        <#macro displayFileContent doc>
            <#assign doc_file_content=doc['file:content']/>
            <#if doc_file_content?has_content>
                <@displayFileFileContent doc_file_content doc ""/>
            </#if>
        </#macro>
        
        <#macro displayFileAttachment doc_files_file i doc>
            <@displayFileFileContent doc_files_file['file'] doc i/>
        </#macro>
        
    <#macro displayFileAttachments doc>
        <#assign doc_files_files=doc['files:files']/>
        <#if doc_files_files?has_content>
            <#list doc_files_files as doc_files_file>
                <@displayFileAttachment doc_files_file doc_files_file_index doc/>
            </#list>
        </#if>
    </#macro>
        
    <#macro displayFiles doc>
        <@displayFileContent doc/>
        <@displayFileAttachments doc/>
    </#macro>

		<#macro displayNonSoaFilesAndDocuments service>
            <#assign service_children=service['children']/>
            <#assign hasAttachedOrChildFile=false/>
            <table style="width: 600px;">
                <tr><td>
                <table style="border-spacing: 0px 10px;">
            <#if !service.facets?seq_contains('SoaNode')>
                <@displayFileContent service/>
            </#if>
            <@displayFileAttachments service/><#-- TODO better : only if not resource, by checking car filled by displayResource --> 
            <#if service_children?has_content>
                <#list service_children as child>
                    <#if !service.facets?seq_contains('SoaNode')>
                        <@displayFiles child/>
                    </#if>
                </#list>
            </#if>
                </table>
                </td></tr>
            
            <#if !hasAttachedOrChildFile>
                <tr>
                    <td colspan="3" style="text-align: center">${Root.msg("NoFileAttachedOrChildren")}</td>
                </tr>
            </#if>
            </table>
		</#macro>
		
    <#macro displayResourceInfo resourceInfo>
        ${Root.msg("Resource")} 
        <#if resourceInfo['rdi:url']?has_content>
           ${Root.msg("thatIsExternal")} 
           <span title="par <#if resourceInfo['rdi:probeType']?has_content>${resourceInfo['rdi:probeType']}<#else>${Root.msg("GUI")}</#if>
           (<#if resourceInfo['rdi:timestamp']?has_content>${resourceInfo['rdi:timestamp']}</#if>)" style="color:grey;">${Root.msg("taken")}</span>
           ${Root.msg("from")} <@linkEnd resourceInfo['rdi:url']/>
        <#else>
           <span title="${Root.msg("updatedByAnUserFromGUI")}" style="color:grey;">${Root.msg("embedded")}</span>. 
        </#if>
    </#macro>
    
    <#macro displayResources service>
        <span style="font-weight: bold;">${Root.msg("SOAResources")} :</span> (${Root.msg("embeddedOrExternal")}) :<br/>
        <#assign hasInterfaceResource=false/>
        <#-- assign hasSecurityPolicyResource=false/ -->
        
        &nbsp;&nbsp;- <@displayResource service/>
        
        <#-- TODO do this in Java, & using WsdlBlob ! -->
        <#-- if service['files:files']?has_content>
            <br/>
            <#list service['files:files'] as child>
                &nbsp;&nbsp;- <@displayResource child/>
                <br/>
            </#list>
        </#if -->
        <#assign soaNodeResourceChildren = Root.getDocumentService().getSoaNodeChildren(soaNode, 'Resource')/>
        <#list soaNodeResourceChildren as soaNodeResourceChild>
            <#-- @displaySoaNodeLink soaNodeChild 'Resource'/ -->
            &nbsp;&nbsp;- <@displayResource soaNodeResourceChild/>
        </#list>
        <#if !hasInterfaceResource>
            ${Root.msg("NoInterfaceDefinition")} <span title="SOAP, REST JAXRS" style="color:grey;">${Root.msg("thatIsUnderstood")}</span>.
        </#if>
    </#macro>
    
    <#macro displayResource resource>
        <#-- TODO do this in Java, & using using WsdlBlob ! -->
        <#assign resourceFile=resource['file:content']/>
        <#if !resourceFile?has_content || resource.facets?seq_contains('ResourceDownloadInfo')>
        
        <#if resource.facets?seq_contains('WsdlInfo') && resource['wsdlinfo:wsdlPortTypeName']?has_content>
            <#assign hasInterfaceResource=true/>
            <span style="font-weight: bold;">${Root.msg("Interface")} :</span> ${Root.msg("specifiesAService")}
            <b>SOAP</b> ${resource['wsdlinfo:transport']} ${Root.msg("whoseInterfaceIs")}
            <span title="${Root.msg("name")} : ${resource['wsdlinfo:wsdlServiceName']}" style="color:grey;">${resource['wsdlinfo:wsdlPortTypeName']}</span>
            <br/>&nbsp;&nbsp;&nbsp;${Root.msg("inADefinitionThatIs")}
            <#if resourceFile?has_content && resourceFile['filename']?has_content
                    && resourceFile['filename']?ends_with('.wsdl')>
                <b>WSDL</b> (${Root.msg("XMLFile")} <@stringEndWithTooltip resourceFile['filename']/> <@displayDocumentTools resource/>)
            <#else>
                ${Root.msg("ofUnknownType")} (JAR JAXWS...)
            </#if>
        </#if>
        <#if resource.facets?seq_contains('RestInfo') && resource['restinfo:path']?has_content>
            <#assign hasInterfaceResource=true/>
            <span style="font-weight: bold;">${Root.msg("Interface")} :</span> ${Root.msg("specifiesAService")}
            <b>REST</b> ${Root.msg("withPath")}
            <span title="${resource['restinfo:accepts']} => ${resource['restinfo:contentType']}" style="color:grey;">${resource['restinfo:path']}</span>
            <br/>&nbsp;&nbsp;&nbsp;${Root.msg("inADefinitionThatIs")}
            <#if resourceFile?has_content && resourceFile['filename']?has_content
                    && resourceFile['filename']?ends_with('.jar')>
                <b>JAXRS</b> (${Root.msg("JARFile")} <@stringEndWithTooltip resourceFile['filename']/> <@displayDocumentTools resource/>)
            <#else>
                ${Root.msg("ofUnknownType")} (Swagger...)
            </#if>
        </#if>
        <#-- @displayResourceTools resource/>
        <#if fstudio_enabled>
            - Outils : <a href="">Mock impl client in FraSCAti Studio</a>
        </#if -->
        ${Root.msg("in")} <@displayResourceInfo resource/>
        
        <#elseif resource['rdi:url']?has_content>
            ${Root.msg("NoInterfaceDefinition")} <span title="SOAP, REST JAXRS" style="color:grey;">${Root.msg("thatIsUnderstood")}</span>, ${Root.msg("butA")} <@displayResourceInfo resource/>
        <#else>
            ${Root.msg("NoInterfaceDefinition")}.
        </#if>
    </#macro>


        <#macro displayTested serviceimpl>
           <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
           <#if deliverable?has_content>
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
              <span title="${deliverable['title']} / ${foundTestServiceCons['javasc:consumerClass']}" style="color:green;"><b>${Root.msg("tested")}</b></span>
              <span style="color:gray;">(${foundTestServiceCons['javasc:consumerClass']?substring(foundTestServiceCons['javasc:consumerClass']?last_index_of('.') + 1, foundTestServiceCons['javasc:consumerClass']?length)})</span> 
           <#else>
              <b><span style="color:red;">${Root.msg("NOTTESTED")}</span></b>
           </#if>
           </#if><#-- else no deliverable, ex. Compliance > Matching Dashboard samples -->
        </#macro>
		