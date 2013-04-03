﻿<!DOCTYPE html>
<html>
<head>
<title>EasySOA REST Services Documentation</title>
<style rel="stylesheet" type="text/css">
<!--
* {
	margin: 0;
	padding: 0;
}

body {
	width: 100%;
	height: 820px;
	background-color: #EEF5FA;
	font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
	font-size: 10pt;
}

h1 {
	margin-bottom: 10px;
	border-bottom: 1px solid grey;
	color: #446;
	font-size: 15pt;
}

h2 {
	font: 12pt Arial;
	font-weight: bold;
	padding-top: 10px;
	padding-bottom: 5px;
	color: #248;
}

li {
	margin-left: 20px;
	margin-bottom: 10px;
}

#headerLogo {
	padding: 15px;
}

#header {
	width: 100%;
	font-size: 14pt;
	font-weight: bold;
	height: 80px;
	background: white;
	border-bottom: 2px solid #444;
}

#container {
	padding: 20px;
}

p {
	font-size: 10pt;
	margin-bottom: 10px;
}

table {
	border-spacing: 0;
	border: 1px solid black;
	margin: 10px;
	width: 800px;
}

td {
	border: 1px solid grey;
	padding: 5px;
	background-color: white;
}

th {
	border: 1px solid grey;
	padding: 5px;
	background-color: #cde;
}

a {
	color: #036;
	text-decoration: none;
	font-weight: bold;
}

.url {
	font-family: monospace;
	background-color: #DDD;
	border: 1px solid #AAA;
}

.param {
	font-family: monospace;
	background-color: #DDF;
	border: 1px solid #AAF;
	margin-right: 5px;
}

.code {
	font-family: monospace;
	background-color: #EEE;
}

.exampleRequest {
	font-weight: bold;
	padding-bottom: 10px;
	margin-bottom: 10px;
	border-bottom: 1px solid #AAA;
}

.exampleResponse {
	color: #222;
}

li {
	margin-bottom: 0;
}

th {
	font-size: 12pt;
}

td:first-child {
	font-size: 12pt;
}
-->
</style>

        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>

</head>
</html>
<body>

        <#include "/views/EasySOA/macros.ftl">    
    
	<div id="header">
		<img id="headerLogo" src="/nuxeo/site/easysoa/skin/img/logo50px.png" />
	</div>
	<div id="container">

		<h1>Indicateurs sur votre SOA</h1>

                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <p>Version de Phase : <@displayCurrentVersion subprojectId visibility/></p>

		<h2>Nbs</h2>
		<ul>
			<#list nbMap?keys as nbMapKey>
			<li>Nombre de ${nbMapKey} : <b>${nbMap[nbMapKey]}</b>
				<#if percentMap[nbMapKey]> | 
					<#if percentMap[nbMapKey] != -1>
						Pourcentage : <b>${percentMap[nbMapKey]}%</b>
					<#else>
						Pourcentage : <b>N.A.</b>
					</#if>
				</#if>
			</li>
			</#list>
		</ul>

		<h2>Percent</h2>
		<ul>
			<#list percentMap?keys as percentMapKey>
			<#if percentMap[percentMapKey]>
			<li>
				<#if percentMap[percentMapKey] != -1>
					${percentMapKey} : <b>${percentMap[percentMapKey]}%</b>
				<#else>
					${percentMapKey} : <b>N.A.</b>
				</#if>
			</li>
			</#if>
			</#list>
		</ul>

	</div>
        <div id="container">
            <h1>Context</h1>
            
            <#if subprojectId>
            Current context : ${subprojectId}, visibility ${visibility}
            <#else>
            Current context : no selected version
            </#if>

            <ul>
                <li>
                    <a href="${Root.path}/context/">Change context</a>
                </li>
            </ul>
        </div>
        <div id="container">
		<h1>Dashboards</h1>

		<ul>
			<li>
                            <a href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Easysoa Matching dashboard</a>
                        </li>
			<li>
                            <a href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">Easysoa Service documentation dashboard</a>
                        </li>
			<li>
                            <a href="${Root.path}/monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">Easysoa Endpoint indicators dashboard</a>
                        </li>
		</ul>

	</div>

</body>
