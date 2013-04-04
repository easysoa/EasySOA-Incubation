<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Matching</title>
        <meta charset="utf-8" />
        <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
        <link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        <style type="text/css">
          .clickable:hover { cursor: pointer; background-color: #FFC; }
          .id { display: none }
          .selected { background-color: #CFC; }
        </style>

        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>
        
        <link rel="stylesheet/less" href="/nuxeo/site/easysoa/skin/css/bootstrap.less">
        <script src="/nuxeo/site/easysoa/skin/js/less.js"></script>
    </head>
    
    <body>

        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"></div>
                EasySOA Réconciliation technique / métier
            </div>
        </div>

        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Point de vue</h3>
                        <#assign visibility=visibility!"">
                        <#assign subprojectId=subprojectId!"">
                        <p>Version de Phase : <@displayCurrentVersion subprojectId visibility/></p>
                    </div>
                </li>
            </ul>
            
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <h3>Réconciliation technique / métier</h3>
                        <p>
                            <ul>
                                <li>
                                    <a href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Matching Dashboard</a>
                                    <small> : EasySOA Registry's Matching Dashboard lists all discovered SOA elements (implementations and endpoints) that its automatic matching algorithm couldn't link with the existing SOA model because of lack of information that could help it decide one way or the other, and further helps the SOA administrator link them by providing suggestions along the specified architecture (Components).</small></small>
                                </li>
                                <li>Gestion des versions
                                    <small></small>
                                </li>
                            </ul>
                        </p>
                        
                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <!-- TODO : Display only the indicators corresponding to the CATEGORY_CARTOGRAPHY category -->
                            <@displayIndicatorsInTable indicators "matching"/>
                        <p>
                    </div>
                </li>
            </ul>
        </div>

        <div id="container">
            <a href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Back to dashboard</a>
        </div>

</body>

</html>
