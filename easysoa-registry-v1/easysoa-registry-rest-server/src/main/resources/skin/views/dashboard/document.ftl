<div>
    <div class="id">${document.id}</div> <!-- id and title are identical, just display the title and add the phase and the object type !! -->
    <p style="line-height: 13px; margin-top: 5px">
        <u><b>${document.type}</b> <i><@displaySoaNodeShort document subprojectId visibility/></u>
            <br/>
            <!--<span style="color: #444;">${document['soan:name']}</span>--><!-- style="font-size: 70%; color: #444;" -->
            <!--<span style="color: #444;">Phase : <@displayPhase document['spnode:subproject']/></span>-->
        </i>
    </p>
    <p><!-- style="font-size: 80%" -->
        <#-- Phase : <@displayPhaseIfOutsideContext document['spnode:subproject']/><br / --><#-- displayed in title -->
        <#if document.facets?seq_contains('WsdlInfo')
               && document['wsdl:wsdlPortTypeName']?has_content && document['wsdl:wsdlPortTypeName']?length != 0>
            WSDL Port type: <span style="color:grey;">${document['wsdl:wsdlPortTypeName']}</span><br />
            <#-- WSDL Service: ${document['wsdl:wsdlServiceName']}<br / -->
        <#elseif document.facets?seq_contains('RestInfo')
               && document['rest:path']?has_content && document['rest:path']?length != 0>
            REST path : <span style="color:grey;">${document['rest:path']}</span><br />
        </#if>
   	<#if document.type == 'InformationService'>
   	    Fournisseur: <@displayProviderActorOfService document true/>
   	    &nbsp;&nbsp;
          Composant: <@displayComponentOfService document true/>
      <#elseif document.type?ends_with('ServiceImplementation')><#-- TODO better -->
          Langage : ${document['impl:language']}
          &nbsp;&nbsp;
          Build : ${document['impl:build']}<br />
          <#-- if document.type == 'JavaServiceImplementation'>
          Classe : ${document['javasi:implementationClass']}<br />
          </#if -->
   	</#if>
    </p>
</div>