<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Cartography</title>
        <meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        
        <link href="/nuxeo/site/easysoa/skin/css/prettify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/prettify/prettify._js"></script>
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"></div>
                EasySOA - Decouverte sources
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                            <h3>The easysoa discovery code maven plugin</h3>
                            <p>
                            Sources Discovery allows to automatically extract known services, implementations and consumptions from source code (for now, Java) by analyzing it at compilation time.
                            <br/>
                            The only thing to do is to add and configure the easysoa discovery code maven plugin in your project pom.xml file.
                            <br/>
                            There are five parameters to configure :
                            <ul>
                                <li>nuxeoSitesUrl : URL of the Easysoa Nuxeo system</li>
                                <li>username : username to be used for the connection</li>
                                <li>password : password to be used for the connection</li>
                                <li>subproject : subproject where to register the discovered informations</li>
                                <li>application : application where to register the discovered informations</li>
                            </ul>
                            </p>
                            <h3>Code source</h3>
                            Le code source est disponible <a href="https://github.com/easysoa/EasySOA-Incubation/tree/master/easysoa-discovery-code-mavenplugin">ici</a>.
                        
                            <H3>Exemple de configuration :</h3>
                            Le code ci dessous est à ajouter au fichier pom.xml de votre projet.
                            <pre class="prettyprint">
                                ...
                                &lt;!-- EasySOA Code discovery plugin -->
                                &lt;plugin/&gt;
                                  &lt;groupId/&gt;org.easysoa.discovery.code&lt;/groupId/&gt;
                                    &lt;artifactId/&gt;easysoa-discovery-code-mavenplugin&lt;/artifactId/&gt;
                                    &lt;version/&gt;1.0-SNAPSHOT&lt;/version/&gt;
                                    &lt;executions/&gt;
                                      &lt;execution/&gt;
                                        &lt;id>test&lt;/id/&gt;
                                        &lt;phase/&gt;compile&lt;/phase/&gt;
                                        &lt;goals/&gt;
                                          &lt;goal/&gt;discover&lt;/goal/&gt;
                                        &lt;/goals/&gt;
                                        &lt;configuration/&gt;
                                          &lt;nuxeoSitesUrl/&gt;http://localhost:8080/nuxeo/site&lt;/nuxeoSitesUrl/&gt;
                                          &lt;username/&gt;Administrator&lt;/username/&gt;
                                          &lt;password/&gt;Administrator&lt;/password/&gt;
                                          &lt;application/&gt;/default-domain/Intégration DPS - DCV/Realisation_v&lt;/application/&gt;
                                          &lt;subproject/&gt;AXXX DPS APV&lt;/subproject/&gt;
                                        &lt;/configuration/&gt;
                                      &lt;/execution/&gt;
                                    &lt;/executions/&gt;
                                &lt;/plugin/&gt;
                                ...
                            </pre>
                            
                            <a href="https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-samples/easysoa-samples-axxx/axxx-dps-apv/pom.xml">Here</a> you can find a complete pom.xml exemple.
                            
                    </div>
                </li>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'acceuil</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

    <script>
        !function ($) {
            $(function(){
                window.prettyPrint && prettyPrint()   
            })
        }(window.jQuery)
    </script>    
    
</html> 