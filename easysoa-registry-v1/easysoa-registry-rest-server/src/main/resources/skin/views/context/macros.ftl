
    <#macro displayProjectsPhasesAndVersionsShort projectVersionsList>
        <div class="accordion" id="accordion2">
            <#list projectVersionsList?keys as project>
            <#if project != "Project Template">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <#if project == "MyProject" >
                        <#assign projectTag="Projet par défaut">
                    <#else>
                        <#assign projectTag="Projet">
                    </#if>
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${projectTag} ${project}</a>
                </div>
                <div id="${project?replace(' ', '_')}" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <ul>
                            <li><@displayLiveShort projectVersionsList project/></li>
                            <li><@displayVersionsShort projectVersionsList project/></li>
                        </ul>
                    </div>
                </div>
            </div>
            </#if>
            </#list>
        </div>
    </#macro>
    
    <#macro displayLiveShort projectVersionsList project>
        Versions courantes
        <ul>
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["live"] as live>
            <li> 
                ${live['dc:title']} - ${live.versionLabel} (<a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayVersionsShort projectVersionsList project>
        Anciennes versions
        <ul>        
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["versions"] as version>
            <li> 
                ${version['dc:title']} - ${version.versionLabel} (<a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayCurrentVersion subprojectId contextInfo visibility>
        <#if subprojectId>
            ${contextInfo.project} / ${contextInfo.phase}&nbsp;
            <#if contextInfo.version != "">
            (version ${contextInfo.version})
            <#else>
            (version courante)
            </#if>
            <span class="label">Visibilité : <#if visibility == "deep">Avec phases parentes<#else>Sans phases parentes</#if></span>
        <#else>
            Perspective globale
        </#if>
    </#macro>

    <#-- Display the context bar as a Bootstrap full width thumbnail -->
    <#macro displayContextBar subprojectId contextInfo visibility button>
        <li class="span12">
            <div class="thumbnail">
                <img data-src="holder.js/300x200" alt="">
                <table class="table-hidden">
                    <tr>
                        <td class="td-hidden"><strong>Perspective :</strong>&nbsp<@displayCurrentVersion subprojectId contextInfo visibility/>&nbsp;&nbsp;</td>
                        <td class="td-hidden" style="text-align:right">
                            <#if button == "true">
                            <a class="btn btn-primary" href="/nuxeo/site/easysoa/context?subprojectId=${subprojectId}&visibility=${visibility}">Changer la perspective</a>
                            </#if>
                        </td>
                    </tr>
                </table>
            </div>
        </li>
    </#macro>
