
    <#macro displayProjectsPhasesAndVersionsShort projectVersionsList>
        <div class="accordion" id="accordion2">
            <#list projectVersionsList?keys as project>
            <#if project != "Project Template">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <#assign liveAndVersions = projectVersionsList[project]/>
                    <#assign lastPhase = liveAndVersions["live"][liveAndVersions["live"]?size-1]/>

                    <#if project == "MyProject" >
                        <#assign projectTag=Root.msg("defaultProject")/>
                    <#else>
                        <#assign projectTag=Root.msg("project")/>
                    </#if>
                    <#-- a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${projectTag} ${project}</a -->
                    <div class="accordion-toggle">
                    <a href="${Root.path}/../?subprojectId=${lastPhase['spnode:subproject']}&visibility=deep">${projectTag} <Strong>${project}</Strong>, 
                    phase ${lastPhase['dc:title']} - ${lastPhase.versionLabel}</a>&nbsp;<span class="icon-eye-open" style="color:grey" title="${Root.msg("withParentPhases")}"></span>
                    &nbsp;(<a data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${Root.msg("moreChoice")}</a>)
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
        ${Root.msg("currentVersions")}
        <ul>
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["live"] as live>
            <li>
                ${live['dc:title']} - ${live.versionLabel} (<span class="icon-eye-open" style="color:grey" title="${Root.msg("withParentPhases")}"></span>
                <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">${Root.msg("with")}</a>, 
                <span class="icon-eye-close" style="color:grey" title="${Root.msg("withoutParentPhases")}"></span>
                <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">${Root.msg("withoutParentPhasesWithLinkEnding", ["</a>"])})
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayVersionsShort projectVersionsList project>
        ${Root.msg("previousVersions")}
        <ul>
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["versions"] as version>
            <li>
                ${version['dc:title']} - ${version.versionLabel} (<span class="icon-eye-open" style="color:grey" title="${Root.msg("withParentPhases")}"></span>
                <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">${Root.msg("with")}</a>, 
                <span class="icon-eye-close" style="color:grey" title="S${Root.msg("withoutParentPhases")}"></span>
                <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">${Root.msg("withoutParentPhasesWithLinkEnding", ["</a>"])})
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
            <#-- span class="label">Visibilité : <#if visibility == "deep">${Root.msg("withParentPhases")}<#else>${Root.msg("withoutParentPhases")}</#if></span -->
            <#if visibility == "deep">
                <span class="icon-eye-open" style="color:grey" title="${Root.msg("withParentPhases")}"></span>
                <#if longDisplay == "true">&nbsp;(${Root.msg("withParentPhases")})</#if>
            <#else>
                <span class="icon-eye-close" style="color:grey" title="${Root.msg("withoutParentPhases")}"></span>
                <#if longDisplay == "true">&nbsp;(${Root.msg("withoutParentPhases")})</#if>
            </#if>
        <#else>
            ${Root.msg("globalPerspective")}
        </#if>
    </#macro>

    <#-- Display the context bar as a Bootstrap full width thumbnail -->
    <#macro displayContextBar subprojectId contextInfo visibility button longDisplay>
        <#assign currentMsg = ""/>
        <#if longDisplay == "true">
            <#assign currentMsg = Root.msg("currentPerspectiveCurrent")/>
        </#if>
        <strong>${Root.msg("currentPerspectiveLongDisplay", [currentMsg])}:</strong>&nbsp<@displayCurrentVersion subprojectId contextInfo visibility longDisplay/>
        <#if button == "true">
            (<a href="/nuxeo/site/easysoa/context?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("change")}</a>)
        </#if>
    </#macro>
