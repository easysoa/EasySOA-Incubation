
    <#include "/views/EasySOA/docMacros.ftl">
    

    <#macro displayEndpointEnvUrl endpoint subprojectId visibility>
         ${Root.msg("deployedIn")} ${endpoint['env:environment']} ${Root.msg("at")} <a href="${Root.path}/envIndicators/${endpoint.id}/1?subprojectId=${subprojectId}&visibility=${visibility}">${endpoint['endp:url']}</a>
    </#macro>

    <#macro displayEndpointsShort servicePathToEndpoints pathToServices subprojectId visibility>
        <#if servicePathToEndpoints?has_content>
        <div class="accordion" id="accordion2">
            <#list servicePathToEndpoints?keys as servicePath>
                <#assign service=pathToServices[servicePath]/>
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${service.name}"><@displayServiceTitle service false subprojectId visibility/></a>
                    <#-- a href="<@urlToFicheSOA service subprojectId visibility/>" target="_blank"><img src="/nuxeo/icons/fiche.png" alt="${Root.msg("serviceBrowsing")}"/></a>
                    <a href="<@urlToLocalNuxeoDocumentsUiPath servicePath/>" target="_blank"><i class="icon-file-alt"></i></a -->
                </div>
                <div id="${service.name}" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <ul>
                            <#list servicePathToEndpoints[servicePath] as endpoint>
                                <li><@displayEndpointEnvUrl endpoint subprojectId visibility/></li>
                            </#list>
                        </ul>
                    </div>
                </div>
            </div>
            </#list>
        </div>
        <#else>
            ${Root.msg("noDeployedService")}
        </#if>
    </#macro>

    <#macro displayIndicatorShort indicator>
        <tr>
            <td>
                <@linkBootstrapToLocalNuxeoDocumentsUiEdit indicator/>
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
                    <span class="label label-important">${indicator.serviceLevelHealth}</span>
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
            <td>${Root.msg("IndicatorName")}</td>
            <td>${Root.msg("Timestamp")}</td>
            <td>${Root.msg("ServiceLevelHealth")}</td>
            <td>${Root.msg("ServiceLevelViolation")}</td>
            </tr>
            <#list indicators as indicator><@displayIndicatorShort indicator/></#list>
         </table>
    </#macro>