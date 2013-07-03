<!DOCTYPE html>
<html>

<head>
   <title>EasySOA Cartographie - Fiche Service</title>
   <meta charset="utf-8" />
   
   <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
   
   <!-- custom style and scripts -->
   <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
   <link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
   
   <style type="text/css">
     .clickable:hover { cursor: pointer; background-color: #FFC; }
     .id { display: none }
     .selected { background-color: #CFC; }
   </style>
    
    <!-- Bootstrap default style and scripts -->
    <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
    <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
</head>

<body>

        <#include "/views/EasySOA/macros.ftl">
        <#include "/views/EasySOA/docMacros.ftl">
    
   <div id="header">
      <div id="headerContents">
          <div id="logoLink">&nbsp;</div>
          <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
          EasySOA Cartographie - Documentation
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
               
               <h3>Fiches Service</h3>
      <p/>
      
      <@displayServicesShort services subprojectId visibility/>
      <p/>
      <p/>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt="">
               
               <h3>Accès direct à leurs...</h3>
               <h4>implémentations</h4>
               <p/>
               
               <@displayImplementationsShort impls subprojectId visibility/>
               <p/>
      <p/>
               <h4>déploiements</h4>
               <p/>
               
               <@displayEndpointsShort endpoints subprojectId visibility/>
               <p/>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt="">
               
      <h3>Par tags</h3>
      <p/>

      <#list tags as tag>
      <#if tagId2Services?keys?seq_contains(tag.id)>
      <h4>Services (${tagId2Services[tag.id]?size}) du tag <@displayTagShort tag subprojectId visibility/></h4>
        <#-- Inlining to handle non-service cases. TODO LATER more generic ?? -->
          <#-- @displayServicesShort tagId2Services[tag.id] subprojectId visibility/ -->
         <#if services?size = 0>
              Pas de services.
          <#else>
          <ul>
              <#list tagId2Services[tag.id] as service>
                  <li>
                  <#-- handling non-SoaNode tagged things. TODO or less, or wider ?? -->
                  <#if service.facets?seq_contains('SoaNode')>
                    <@displaySoaNodeShort service subprojectId visibility/>
                <#else>
                    <@displayDocShort service/>
                </#if>
                  </li>
             </#list>
          </ul>
          </#if>
      </#if>
      </#list>
      <p/>

      <h3>Services sans tag (${untaggedServices?size})</h3>
      <p/>

      <@displayServicesShort untaggedServices subprojectId visibility/>
      <p/>

      <h3>Tags non utilisés (${tags?size - tagId2Services?keys?size})</h3>
      <p/>

      <ul>
         <#list tags as tag>
         <#if !tagId2Services?keys?seq_contains(tag.id)>
         <li><@displayTagShort tag subprojectId visibility/></li>
         </#if>
         </#list>
      </ul>
      <p/>
      
            </div>
         </li>
   
         <@displayReturnToIndexButtonBar/>
   
      </ul>
   </div>

</body>

</html>
