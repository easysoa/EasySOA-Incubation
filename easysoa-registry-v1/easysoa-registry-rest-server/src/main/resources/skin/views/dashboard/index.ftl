<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("matchingDashboard")}</title>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/EasySOA/docMacros.ftl"><#-- for displayPhase(subprojectId) -->
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("matchingDashboard")}
                <form style="position:absolute; right:80px; top:110px" action="/nuxeo/site/easysoa/dashboard/samples?subprojectId=${subprojectId}&visibility=${visibility}" method="post" style="position: absolute; right: 20px; top: 20px">
                    <input type="submit" value="${Root.msg("fillWithSamples")}" />
                </form>
            </div>
        </div>

        <br/>

        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">

                        <div id="selectedServiceImpl" style="display: none">${selectedServiceImpl}</div>

                        <#assign doctypeTitle = Root.msg("Endpoints")>
                        <#assign targetDoctypeTitle = Root.msg("implementation")>
                        <#assign unmatched = endpointWithoutImpl>
                        <#include "/views/dashboard/matching.ftl">

                        <#assign doctypeTitle = Root.msg("Implementations")>
                        <#assign targetDoctypeTitle = Root.msg("service")>
                        <#assign unmatched = servWithoutSpecs>
                        <#include "/views/dashboard/matching.ftl">

                        <h2>${Root.msg("myServices")}</h2>

                        <table class="table table-bordered" style="border:1px solid black">
                            <tr>
                                <th>${Root.msg("Definitions")}</th>
                                <th></th>
                                <th>${Root.msg("Implementations")}</th>
                                <th>${Root.msg("Matching")}</th>
                            </tr>
                            <#list matchedImpls as matchedImpl>
                            <tr>
                                <td>
                                  <#assign document = infoServicesById[matchedImpl['impl:providedInformationService']]>
                                  <#include "/views/dashboard/document.ftl">
                                </td>
                                <td><b>&gt;</b></td>
                                <td>
                                  <#-- Displaying the button to break matched link always (and not only when incompatible) : -->
                                  <#-- if document['wsdl:wsdlPortTypeName'] != matchedImpl['wsdl:wsdlPortTypeName'] -->
                                  <form style="float: right" method="post" action="/nuxeo/site/easysoa/dashboard?subprojectId=${subprojectId}&visibility=${visibility}">
                                        <input type="submit" value="X" style="width: 30px" />
                                        <input type="hidden" name="unmatchedModelId" value="${matchedImpl.id}" />
                                    <input type="hidden" name="targetId" value="" />
                                  </form>
                                  <#-- /#if -->
                                  <#assign document = matchedImpl>
                                  <#include "/views/dashboard/document.ftl">
                                </td>
                                <td><span class="icon-ok" style="color:green"></span></td>
                            </tr>
                            </#list>
                            <#list unimplementedServs as unimplementedServ>
                            <tr>
                                <td class="clickable infoService">
                                  <#assign document = unimplementedServ>
                                  <#include "/views/dashboard/document.ftl">
                                </td>
                                <td>x</td>
                                <td></td>
                                <td><span class="icon-remove" style="color:red"></span></td>
                            </tr>
                            </#list>
                        </table>

                        <h2>${Root.msg("myImplementedAndDeployedServices")}</h2>

                        <table class="table table-bordered" style="border:1px solid black">
                            <tr>
                                <th>${Root.msg("Implementations")}</th>
                                <th></th>
                                <th>${Root.msg("Deployments")}</th>
                                <th>${Root.msg("Matching")}</th>
                            </tr>
                            <#list matchedEndpoints as matchedEndpoint>
                            <tr>
                                <td>
                                  <#assign document = Root.getDocumentService().getServiceImplementationFromEndpoint(matchedEndpoint)/>
                                  <#include "/views/dashboard/document.ftl">
                                </td>
                                <td><b>&gt;</b></td>
                                <td>
                                  <#-- Displaying the button to break matched link always (and not only when incompatible) : -->
                                  <#-- if document['wsdl:wsdlPortTypeName'] != matchedEndpoint['wsdl:wsdlPortTypeName'] -->
                                  <form style="float: right" method="post" action="/nuxeo/site/easysoa/dashboard?subprojectId=${subprojectId}&visibility=${visibility}">
                                        <input type="submit" value="X" style="width: 30px" />
                                        <input type="hidden" name="unmatchedModelId" value="${matchedEndpoint.id}" />
                                    <input type="hidden" name="targetId" value="" />
                                  </form>
                                  <#-- /#if -->
                                  <#assign document = matchedEndpoint>
                                  <#include "/views/dashboard/document.ftl">
                                </td>
                                <td><span class="icon-ok" style="color:green"></span></td>
                            </tr>
                            </#list>
                            <#-- TODO list undeployed services ? NO TODO rather list iserv, impl & endpoint on a single line -->
                        </table>

                        <script>
                        $(document).ready(function() {

                        function getId($el) {
                        return $('.id', $el).html();
                        }

                        // To allow to have simultaneously selections of various kinds (endpoint_to_impl, impl_to_iserv...)
                        function getSelectedFormInputName($el) {
                          return $el.attr('selectedFormInputName');
                        }

                        $('.clickable').click(function() {
                        var $el = $(this);
                        if ($el.hasClass('target')) {
                          $('.target').removeClass('selected');
                          $el.addClass('selected');
                          var selectedFormInputName = getSelectedFormInputName($el);
                          $('#' + selectedFormInputName).attr('value', getId($el));
                        }
                        else {
                          $('.unmatchedModel').removeClass('selected');
                          $el.addClass('selected');
                          var selectedFormInputName = getSelectedFormInputName($el);
                          $('#' + selectedFormInputName).attr('value', getId($el));
                        }
                        });

                        });
                        </script>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
