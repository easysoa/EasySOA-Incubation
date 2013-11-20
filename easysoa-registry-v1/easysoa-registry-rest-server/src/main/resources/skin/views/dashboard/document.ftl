<div>
    <div class="id">${document.id}</div> <#-- id and title are identical, just display the title and add the phase and the object type !! -->
    <p style="line-height: 13px; margin-top: 5px">
        <u><b>${document.type}</b> <i><@displaySoaNodeShort document subprojectId visibility/></u>
            <br/>
            <#-- span style="color: #444;">${document['soan:name']}</span --><#-- style="font-size: 70%; color: #444;" -->
            <#-- span style="color: #444;">Phase : <@displayPhase document['spnode:subproject']/></span -->
        </i>
    </p>
    <p><#-- style="font-size: 80%" -->
        <#-- Phase : <@displayPhaseIfOutsideContext document['spnode:subproject']/><br / --><#-- displayed in title -->
        <#if document.facets?seq_contains('WsdlInfo')
               && document['wsdl:wsdlPortTypeName']?has_content && document['wsdl:wsdlPortTypeName']?length != 0>
            ${Root.msg("wsdlPortType")}: <span style="color:grey;">${document['wsdl:wsdlPortTypeName']}</span><br />
            <#-- WSDL Service: ${document['wsdl:wsdlServiceName']}<br / -->
        <#elseif document.facets?seq_contains('RestInfo')
               && document['rest:path']?has_content && document['rest:path']?length != 0>
            ${Root.msg("restPath")} : <span style="color:grey;">${document['rest:path']}</span><br />
        </#if>
      <#if document.type == 'Endpoint'>
      <#elseif document.facets?seq_contains('ServiceImplementationData')><#-- better than : -->
      <#-- elseif document.type?ends_with('ServiceImplementation') -->
          ${Root.msg("Language")} : ${document['impl:language']}
          &nbsp;&nbsp;
          ${Root.msg("Build")}: ${document['impl:build']}<br />
          <#-- if document.type == 'JavaServiceImplementation'>
          ${Root.msg("Class")} : ${document['javasi:implementationClass']}<br />
          </#if -->
      <#elseif document.type == 'InformationService'>
          ${Root.msg("Provider")}: <@displayProviderActorOfService document true/>
          &nbsp;&nbsp;
          ${Root.msg("Component")}: <@displayComponentOfService document true/>
   	</#if>
    </p>
</div>