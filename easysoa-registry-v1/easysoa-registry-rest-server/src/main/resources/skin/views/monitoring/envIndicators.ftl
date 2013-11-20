<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("monitoring")} - ${Root.msg("serviceLevel")}</title>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/EasySOA/docMacros.ftl">
        <#include "/views/monitoring/macros.ftl">
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("monitoring")} - ${Root.msg("serviceLevel")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("indicatorsForService")} <@displayServiceTitleWithoutPhase service true subprojectId visibility/>
                        <a href="<@urlToLocalNuxeoDocumentsUiPath servicePath/>" target="_blank"><i class="icon-file-alt"></i></a></h3>
                        <span><#-- to solve img pb just below Bootstrap .thumbnail (actually display:block) -->
                        <#-- OLD Déployé en <@displayEnvironment endpoint['env:environment']/> à ${endpoint['endp:url']} -->
                        ${Root.msg("deployedAt")} <@displayEndpointTitleWithoutPhase endpoint subprojectId visibility/>
                        </span>
                        <p>
                        <@displayIndicatorsShort indicators/>
                        </p>

                        <p>
                            <table class="table-hidden">
                                <tr>
                                    <td style="text-align: left;width: 40%">
                                        <#if pagination.hasPreviousPage()>
                                            <a href="${Root.path}/envIndicators/${endpoint.id}/1?subprojectId=${subprojectId}&visibility=${visibility}"><span class="icon-circle-arrow-left" style="color: grey"></span></a>
                                        <#else>
                                            <span class="icon-circle-arrow-left" style="color: grey"></span>
                                        </#if>
                                    </td>
                                    <td style="text-align: left;width: 5%">
                                        <#if pagination.hasPreviousPage()>
                                            <a href="${Root.path}/envIndicators/${endpoint.id}/${pagination.currentPage - 1}?subprojectId=${subprojectId}&visibility=${visibility}"><span class="icon-chevron-sign-left" style="color: grey"></span></a>
                                        <#else>
                                            <span class="icon-chevron-sign-left" style="color: grey"></span>
                                        </#if>
                                    </td>
                                    <td style="text-align: center;width: 10%"><span>Page ${pagination.currentPage} / ${pagination.totalPageNumber}</span></td>
                                    <td style="text-align: right;width: 5%">
                                        <#if pagination.hasNextPage()>
                                            <a href="${Root.path}/envIndicators/${endpoint.id}/${pagination.currentPage + 1}?subprojectId=${subprojectId}&visibility=${visibility}"><span class="icon-chevron-sign-right" style="color: grey"></span></a>
                                        <#else>
                                            <span class="icon-chevron-sign-right" style="color: grey"></span>
                                        </#if>
                                    </td>
                                    <td style="text-align: right;width: 40%">
                                        <#if pagination.hasNextPage()>
                                            <a href="${Root.path}/envIndicators/${endpoint.id}/${pagination.totalPageNumber}?subprojectId=${subprojectId}&visibility=${visibility}"><span class="icon-circle-arrow-right" style="color: grey"></span></a>
                                        <#else>
                                            <span class="icon-circle-arrow-right" style="color: grey"></span>
                                        </#if>
                                    </td>
                                </tr>
                            </table>
                        </p>
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("backToDeployedServiceList")}</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>
