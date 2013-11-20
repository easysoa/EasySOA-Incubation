<#if unmatched?has_content>
    <h3 style="color: red; border-color: red">${doctypeTitle} ${Root.msg("toClassify")}</h3>
    <#assign matchingPrefix = doctypeTitle?replace(" ", "_") + "_to_" + targetDoctypeTitle?replace(" ", "_") + "_">

    <table class="table table-bordered" style="width: 500px; border: 1px solid black">
        <tr>
            <th style="background-color: #FDC">${doctypeTitle}</th>
        </tr>
        <#list unmatched as unmatched>
        <tr>
            <td class="clickable unmatchedModel" id="${unmatched.id}" selectedFormInputName="${matchingPrefix}unmatchedModelId">
                <div style="float: right">
                    <#-- Select component -->
                    <#-- Get suggestions -->
                    <input class="components" type="button" value="${Root.msg("select")}" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/components/${unmatched.id}?subprojectId=${subprojectId}&visibility=${visibility}'" />
                    <input class="suggestions" type="button" value="${Root.msg("getSuggestions")}" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${unmatched.id}?subprojectId=${subprojectId}&visibility=${visibility}'" />
                </div>
                <#assign document = unmatched>
                <#include "/views/dashboard/document.ftl">

                <#if suggestions?? && selectedModel == unmatched.id>
                <table class="table table-bordered" style="width: 500px; border: 1px solid black">
                    <tr>
                        <th style="background-color: #FFA">${Root.msg("suggestions")} <#-- ${targetDoctypeTitle}s NO would be wrong for iserv through placeholder -->
                        <#if selectedComponentTitle??>${Root.msg("from")} <i>${selectedComponentTitle}</i></#if></th>
                    </tr>
                    <#if suggestions?has_content>
                    <#list suggestions as suggestion>
                    <tr>
                        <td class="clickable target" selectedFormInputName="${matchingPrefix}targetId">
                            <#assign document = suggestion>
                            <#include "/views/dashboard/document.ftl">
                        </td>
                    </tr>
                    </#list>
                    <#else>
                        <td style="text-align: center; font-style: italic">
                        ${Root.msg("noMatches")}
                        </td>
                    </#if>
                    <tr>
                        <th style="background-color: #FFA">${Root.msg("suggestions")} <#-- ${targetDoctypeTitle}s NO would be wrong for iserv through placeholder -->
                        <#if selectedComponentTitle??>${Root.msg("from")} <i>${selectedComponentTitle}</i></#if> (${Root.msg("anyPlatform")})</th>
                    </tr>
                    <#if anyPlatformSuggestions?has_content>
                    <#list anyPlatformSuggestions as suggestion>
                    <tr>
                        <td class="clickable target" selectedFormInputName="${matchingPrefix}targetId">
                            <#assign document = suggestion>
                            <#include "/views/dashboard/document.ftl">
                        </td>
                    </tr>
                    </#list>
                    <#elseif anyPlatformSuggestionsCount??>
                        <td style="text-align: center">
                            ${anyPlatformSuggestionsCount} {Root.msg("additionalMatches")}
                        </td>
                    <#else>
                        <td style="text-align: center; font-style: italic">
                        ${Root.msg("noMatches")}
                        </td>
                    </#if>
                    <#if allFromComponent?? && allFromComponent?has_content>
                    <tr>
                        <th style="background-color: #FFA">${Root.msg("all")} <#-- Tous les ${targetDoctypeTitle}s NO would be wrong for iserv through placeholder -->
                        ${Root.msg("from")} <i>${selectedComponentTitle}</i></th>
                    </tr>
                    <#list allFromComponent as suggestion>
                    <tr>
                        <td class="clickable target" selectedFormInputName="${matchingPrefix}targetId">
                            <#assign document = suggestion>
                            <#include "/views/dashboard/document.ftl">
                        </td>
                    </tr>
                    </#list>
                </#if>
            </table>
            </#if>

            <#if components?has_content && selectedModel == unmatched.id>
            <table class="table table-bordered" style="width: 500px; border: 1px solid black">
                <th style="background-color: #DDD">Components list</th>
                <#list components as component>
                <tr>
                    <td class="clickable component" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${selectedModel}/${component.id}?subprojectId=${subprojectId}&visibility=${visibility}'">
                        <#assign document = component>
                        <#include "/views/dashboard/document.ftl">
                    </td>
                </tr>
                </#list>
            </table>
            </#if>
            </td>
        </tr>
        </#list>
    </table>

    <form action="/nuxeo/site/easysoa/dashboard?subprojectId=${subprojectId}&visibility=${visibility}" method="post" style="float: left; width: 100%; margin-top: 10px">
        <fieldset style="width: 400px; padding: 10px;">
            ${Root.msg("clickOnAAndASuggestionThenClickOn", [doctypeTitle])} :<br />
            <#-- un(e) ${targetDoctypeTitle} NO would be wrong for iserv through placeholder -->
            <input type="submit" value="${Root.msg("createALink")}" />
            <input id="${matchingPrefix}unmatchedModelId" name="unmatchedModelId" type="hidden" />
            <input id="${matchingPrefix}targetId" name="targetId" type="hidden" />
        </fieldset>    
    </form>
  
</#if>