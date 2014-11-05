<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/docMacros.ftl">
    
<head>
    <title>EasySOA ${Root.msg("cartography")} - ${Root.msg("serviceBrowsing")}</title>
    <@includeResources/>
    <style type="text/css">
        .clickable:hover { cursor: pointer; background-color: #FFC; }
        .id { display: none }
        .selected { background-color: #CFC; }
    </style>
</head>

<body>

    <div id="header">
      <div id="headerContents">
          <@headerContentsDefault/>
          EasySOA ${Root.msg("cartography")} - ${Root.msg("serviceBrowsing")}
       </div>
   </div>
   <br/>

   <div class="container" id="container">
      <ul class="thumbnails">
         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt="">

               <h3>${Root.msg("servicesBrowsing")}</h3>
      <p/>

      <@displayServicesShort services subprojectId visibility/>
      <p/>
      <p/>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt="">

               <h3>${Root.msg("directAccessToTheir")}</h3>
               <h4>${Root.msg("implementations")}</h4>
               <p/>

               <@displayImplementationsShort impls subprojectId visibility/>
               <p/>
      <p/>
               <h4>${Root.msg("deployments")}</h4>
               <p/>

               <@displayEndpointsShort endpoints subprojectId visibility/>
               <p/>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt="">

      <h3>${Root.msg("byTags")}</h3>
      <p/>

      <#list tags as tag>
      <#if tagId2Services?keys?seq_contains(tag.id)>
      <h4>${Root.msg("soaElements")} (${tagId2Services[tag.id]?size}) ${Root.msg("taggedIn")} <@displayTagShort tag subprojectId visibility/></h4>
        <#-- Inlining to handle non-service cases. TODO LATER more generic ?? -->
          <#-- @displayServicesShort tagId2Services[tag.id] subprojectId visibility/ -->
         <#if services?size = 0>
              ${Root.msg("noServices")}.
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

      <h3>${Root.msg("soaElements")} ${Root.msg("withoutTag")} (${untaggedServices?size})</h3>
      <p/>

      <@displayServicesShort untaggedServices subprojectId visibility/>
      <p/>

      <h3>${Root.msg("unusedTags")} (${tags?size - tagId2Services?keys?size})</h3>
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
