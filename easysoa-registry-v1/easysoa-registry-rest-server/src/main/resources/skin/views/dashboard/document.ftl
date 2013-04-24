<div>
    <div class="id">${document.id}</div> <!-- id and title are identical, just display the title and add the phase and the object type !! -->
    <p style="line-height: 13px; margin-top: 5px">
        <i><u>${document['dc:title']}</u>
            <br/>
            <!--<span style="color: #444;">${document['soan:name']}</span>--><!-- style="font-size: 70%; color: #444;" -->
            <!--<span style="color: #444;">Phase : <@displayPhase document['spnode:subproject']/></span>-->
        </i>
    </p>
    <p><!-- style="font-size: 80%" -->
        Phase : <@displayPhase document['spnode:subproject']/><br />
        <#if document.type == 'InformationService' || document.type == 'ServiceImplementation'>
            Port type: ${document['wsdl:wsdlPortTypeName']}<br />
            Service: ${document['wsdl:wsdlServiceName']}<br />
        </#if>
	<#if document.type == 'InformationService'>
	    Provider: ${document['iserv:providerActor']}<br />
	</#if>
            Type : ${document.type}
    </p>
</div>