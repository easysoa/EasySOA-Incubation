<!DOCTYPE html>
<html>

    <head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />

	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->        
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>

        <!-- font-awesome style for icons -->
        <link rel="stylesheet" href="/nuxeo/site/easysoa/skin/css/font-awesome.css">

        <!-- To solve temporarily the conflict between CSS styles -->
        <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>

    <body>
        <#include "/views/EasySOA/macros.ftl">
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                EasySOA Matching dashboard
                <form action="/nuxeo/site/easysoa/dashboard/samples?subprojectId=${subprojectId}&visibility=${visibility}" method="post"><!-- style="position: absolute; right: 20px; top: 20px"-->
                    <input type="submit" value="Fill with samples" />
                </form>                
            </div>
        </div>
            
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">        

                        <div id="selectedServiceImpl" style="display: none">${selectedServiceImpl}</div>

                        <#assign doctypeTitle = "Endpoint">
                        <#assign targetDoctypeTitle = "implementation">
                        <#assign unmatched = endpointWithoutImpl>
                        <#include "/views/dashboard/matching.ftl">

                        <#assign doctypeTitle = "Service implementation">
                        <#assign targetDoctypeTitle = "service">
                        <#assign unmatched = servWithoutSpecs>
                        <#include "/views/dashboard/matching.ftl">

                        <h2>My services</h2>

                        <table class="table table-bordered" style="border:1px solid black">
                            <tr>
                                <th>Information service</th>
                                <th></th>
                                <th>Service implementation</th>
                                <th>Matching</th>
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

                        <script>
                        $(document).ready(function() {

                        function getId($el) {
                        return $('.id', $el).html();
                        }

                        function getSelectedFormInputName($el) {
                        return $('.selectedFormInputName', $el).html();
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
                <!--<div id="container">
                <a href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'acceuil</a>
                </div>-->
                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
