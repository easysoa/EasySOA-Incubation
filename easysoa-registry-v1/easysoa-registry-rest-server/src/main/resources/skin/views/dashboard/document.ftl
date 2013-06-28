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
        Phase : <@displayPhase document['spnode:subproject']/><br />
        <#if document.type == 'InformationService' || document.type == 'ServiceImplementation'>
            Port type: ${document['wsdl:wsdlPortTypeName']}<br />
            Service: ${document['wsdl:wsdlServiceName']}<br />
        </#if>
   	<#if document.type == 'InformationService'>
   	    Fournisseur: <@displayComponentOfService document/>
          Composant: <@displayComponentOfService document/>
      <#elseif document.type?ends_with('ServiceImplementation')>
          Langage : ${document['impl:language']}<br />
          <#if document['impl:ismock'] = 'true'>
              de test<br />
          </#if>
          <#if document.type == 'JavaServiceImplementation'>
          Classe : ${document['javasi:implementationClass']}<br />
          </#if>
   	</#if>
    </p>
</div>