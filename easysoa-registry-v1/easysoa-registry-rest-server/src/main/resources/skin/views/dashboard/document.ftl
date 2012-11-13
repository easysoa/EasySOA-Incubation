<div>
  <div class="id">${document.id}</div>
	<p style="line-height: 13px; margin-top: 5px"><i>${document['dc:title']}<br />
	<span style="font-size: 70%; color: #444;">${document['soan:name']}</span></i></p>
	<p style="font-size: 80%">
	  <#if document.type == 'InformationService' || document.type == 'ServiceImplementation'>
	    Port type: ${document['wsdl:wsdlPortTypeName']}<br />
	    Service: ${document['wsdl:wsdlServiceName']}<br />
	  </#if>
	  <#if document.type == 'InformationService'>
	    Provider: ${document['iserv:providerActor']}<br />
	  </#if>
	</p>
</div>