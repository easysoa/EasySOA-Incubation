
    <#macro displayProjectsPhasesAndVersionsShort projectVersionsList>
        <div class="accordion" id="accordion2">
            <#list projectVersionsList?keys as project>
            <#if project != "Project Template">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <#assign liveAndVersions = projectVersionsList[project]/>
                    <#assign lastPhase = liveAndVersions["live"][liveAndVersions["live"]?size-1]/>

                    <#if project == "MyProject" >
                        <#assign projectTag="Projet par défaut">
                    <#else>
                        <#assign projectTag="Projet">
                    </#if>
                    <!--<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${projectTag} ${project}</a>-->
                    <div class="accordion-toggle">
                    <a href="${Root.path}/../?subprojectId=${lastPhase['spnode:subproject']}&visibility=deep">${projectTag} <Strong>${project}</Strong>, phase ${lastPhase['dc:title']} - ${lastPhase.versionLabel}</a>&nbsp;<span class="icon-eye-open" style="color:grey" title="Avec phases parentes"></span>
                    &nbsp;(<a data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">Plus de choix...</a>)
                    </div>
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
                ${live['dc:title']} - ${live.versionLabel} (<span class="icon-eye-open" style="color:grey" title="Avec phases parentes"></span><a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">Avec</a>, <span class="icon-eye-close" style="color:grey" title="Sans phases parentes"></span><a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
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
                ${version['dc:title']} - ${version.versionLabel} (<span class="icon-eye-open" style="color:grey" title="Avec phases parentes"></span><a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">Avec</a>, <span class="icon-eye-close" style="color:grey" title="Sans phases parentes"></span><a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayCurrentVersion subprojectId contextInfo visibility longDisplay>
        <#if subprojectId>
            ${contextInfo.project} / ${contextInfo.phase}&nbsp;
            <#if contextInfo.version != "">
            (version ${contextInfo.version})
            <#else>
            (version courante)
            </#if>
            <!--<span class="label">Visibilité : <#if visibility == "deep">Avec phases parentes<#else>Sans phases parentes</#if></span>-->
            <#if visibility == "deep">
                <span class="icon-eye-open" style="color:grey" title="Avec phases parentes"></span><#if longDisplay == "true">&nbsp;(Avec phases parente)</#if>
            <#else>
                <span class="icon-eye-close" style="color:grey" title="Sans phases parentes"></span><#if longDisplay == "true">&nbsp;(Sans phases parente)</#if>
            </#if>
        <#else>
            Perspective globale
        </#if>
    </#macro>

    <#-- Display the context bar as a Bootstrap full width thumbnail -->
    <#macro displayContextBar subprojectId contextInfo visibility button longDisplay>
        <strong>Perspective <#if longDisplay == "true">courante </#if>:</strong>&nbsp<@displayCurrentVersion subprojectId contextInfo visibility longDisplay/>
        <#if button == "true">
            (<a href="/nuxeo/site/easysoa/context?subprojectId=${subprojectId}&visibility=${visibility}">Changer</a>)
        </#if>
    </#macro>
