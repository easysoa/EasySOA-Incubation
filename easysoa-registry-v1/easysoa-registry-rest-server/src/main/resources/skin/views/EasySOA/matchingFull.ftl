<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Matching</title>
        <meta charset="utf-8" />

        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>                
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->        
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"></div>
                EasySOA Conformité
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <h3>Réconciliation technique / métier</h3>
                        <p>
                            <ul>
                                <li>
                                    <a href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Matching Dashboard</a>
                                    <small> : EasySOA Registry's Matching Dashboard lists all discovered SOA elements (implementations and endpoints) that its automatic matching algorithm couldn't link with the existing SOA model because of lack of information that could help it decide one way or the other, and further helps the SOA administrator link them by providing suggestions along the specified architecture (Components).</small>
                                </li>
                                <li>Gestion des versions
                                    <small></small>
                                </li>
                            </ul>
                        </p>
                        
                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "matching"/>
                        <p>
                    </div>
                </li>
            
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Back to dashboard</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>
