
        
    <#macro displayEndpointEnvUrl endpoint subprojectId visibility>
         Déployé en ${endpoint['env:environment']} à <a href="${Root.path}/envIndicators/${endpoint.id}?subprojectId=${subprojectId}&visibility=${visibility}">${endpoint['endp:url']}</a>
    </#macro>
        
    <#macro displayEndpointsShort endpoints subprojectId visibility>
        <#if endpoints?has_content>
        <div class="accordion" id="accordion2">
            <#list endpoints?keys as service>
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${service}">${service}</a>
                </div>
                <div id="${service}" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <ul>
                            <#list endpoints[service] as endpoint>
                                <li><@displayEndpointEnvUrl endpoint subprojectId visibility/></li>
                            </#list>
                        </ul>
                    </div>
                </div>            
            </div>
            </#list>
        </div>
        <#else>
            Aucun service déployé
        </#if>
        
        <#--<ul>
            <#list endpoints?keys as service>
            ${service.name}
                <#list endpoints[service] as endpoint>
                    <@displayEndpointShort endpoint subprojectId visibility/>
                </#list>
            </#list>
            </ul>-->

    </#macro>       
        
    <#macro displayIndicatorShort indicator>
        <tr>
            <td>
                <a href="<@urlToLocalNuxeoDocumentsUiEdit indicator.path/>" target="_blank"><i class="icon-edit"></i></a>
                <a href="#" data-toggle="tooltip" data-placement="top" title="${indicator.description}">${indicator.slaOrOlaName}</a>
            </td>
            <td>${indicator.timestamp?datetime?string.long}</td>
            <td>
                <#if indicator.serviceLevelHealth=="gold">
                    <span class="label label-success">${indicator.serviceLevelHealth}</span>
                <#else>
                    <#if indicator.serviceLevelHealth=="silver">
                    <span class="label label-warning">${indicator.serviceLevelHealth}</span>
                    <#else> 
                    <span class="label label-important">{indicator.serviceLevelHealth}</span>
                    </#if>
                </#if>
            </td>
            <td>
                <#if indicator.isServiceLevelViolation()>
                    <#--${indicator.serviceLevelViolation}-->
                    <#-- TODO : how to display the lvl violation ?? round icon with red an d green color ?? -->
                    <span class="icon-circle" style="color: red"></span>
                <#else>
                <span class="icon-circle" style="color: green"></span>
                </#if>
            </td>
        </tr>
    </#macro>
    
    <#macro displayIndicatorsShort indicators>
        <table class="table table-bordered">
            <tr>
                <!--
                <td>Indicator name</td>
            <td>Timestamp</td>
            <td>Service level health</td>
            <td>Service level violation</td>
                -->
                <td>Indicateur</td>
            <td>Horodatage</td>
            <td>Niveau de santé du service</td>
            <td>Niveau de violation du service</td>
            </tr>
            <#list indicators as indicator><@displayIndicatorShort indicator/></#list>
         </table>
    </#macro>