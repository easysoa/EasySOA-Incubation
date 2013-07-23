
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
        <strong>Perspective :</strong>&nbsp<@displayCurrentVersion subprojectId contextInfo visibility/>&nbsp</td>
        <#if button == "true">
            (&nbsp;<a href="/nuxeo/site/easysoa/context?subprojectId=${subprojectId}&visibility=${visibility}">Changer</a>&nbsp;)
        </#if>
    </#macro>
