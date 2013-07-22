<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <head>
        <title>EasySOA Gouvernance</title>
        <meta charset="utf-8"/>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->

        <link href="/nuxeo/site/easysoa/skin/css/prettify.css" type="text/css" rel="stylesheet"/>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/prettify/prettify._js"></script>

        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen"/>
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all"/>
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico"/>
    </head>

    <body>
        <#include "/views/EasySOA/macros.ftl">

        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true"/>
                </div>
                EasySOA - Indicateurs de complétion et gouvernance
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h2>Retrouvez ici vos indicateurs :</h2>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">BusinessServices sans interface (InformationService) :</td>
                                    <td width="10%">0</td>
                                    <td width="10%">0 %</td>
                                </tr>
                                <tr>
                                    <#assign indicator = indicators["serviceWithoutComponent"]/>
                                    <td width="80%" title="${indicator.description}">InformationServices sans architecture (sans composant) :</td>
                                    <td width="10%" title="${indicator.description}">${indicator.count}</td>
                                    <td width="10%" title="${indicator.description}">${indicator.percentage} %</td>
                                </tr>
                                <tr>
                                    <#assign indicator = indicators["serviceWithDocumentation"]/>
                                    <td width="80%" title="${indicator.description}">Service documentés :</td>
                                    <td width="10%" title="${indicator.description}">${indicator.count}</td>
                                    <td width="10%" title="${indicator.description}">${indicator.percentage} %</td>
                                </tr>
                                <tr>
                                    <td width="80%">Composant sans contrainte de Plateforme :</td>
                                    <td width="10%">5</td>
                                    <td width="10%">12 %</td>
                                </tr>


                            </table>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
