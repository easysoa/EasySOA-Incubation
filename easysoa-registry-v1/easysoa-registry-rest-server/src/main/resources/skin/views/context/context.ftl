<!DOCTYPE html>
<html>

<head>
	<title>Choose a project</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
	<style type="text/css">
	  .clickable:hover { cursor: pointer; background-color: #FFC; }
	  .id { display: none }
	  .selected { background-color: #CFC; }
	</style>
</head>

<body>
    <div id="header">
        <div id="headerContents">
            <div id="logoLink">&nbsp;</div>
	    <div id="headerUserBar"></div>
            Projects
	</div>
    </div>

    <div id="container">
        Projects and subprojects :
        <ul>
        <#list projectIdToSubproject?keys as project>
            <li>${project}
                <ul>
                <#list projectIdToSubproject[project] as subProject>
                    <!--<li><a href="${Root.path}/../?subprojectId=${subProject['spnode:subproject']}">${subProject['dc:title']} - ${subProject.versionLabel}</a></li>-->
                    <li> 
                        ${subProject['dc:title']} - ${subProject.versionLabel} (<a href="${Root.path}/../?subprojectId=${subProject['spnode:subproject']}&visibility=strict">Strict</a>, <a href="${Root.path}/../?subprojectId=${subProject['spnode:subproject']}&visibility=depth">Depth</a>)
                    </li>
                </#list>
                </ul>
           </li>
        </#list>
        </ul>
    </div>

    <div id="container">
        <a href="${Root.path}/../">Return to global context</a>
    </div>

</body>