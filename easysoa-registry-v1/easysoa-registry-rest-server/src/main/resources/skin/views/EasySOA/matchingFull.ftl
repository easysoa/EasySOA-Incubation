<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Conformité</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
                </div>
                EasySOA Conformité
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Réconciliation technique / métier</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Réconciliation</a>
                                <!--<small> : EasySOA Registry's Matching Dashboard lists all discovered SOA elements (implementations and endpoints) that its automatic matching algorithm couldn't link with the existing SOA model because of lack of information that could help it decide one way or the other, and further helps the SOA administrator link them by providing suggestions along the specified architecture (Components).</small>-->
                                <small> : L'outil de réconciliation EasySOA liste tous les éléments SOA découverts (Implémentations et service déployés), que l'algorithme de découverte automatique n'a pas pu lier avec le model SOA existant pour cause de manque d'informations, peut aider l'administrateur SOA à établir les liens en présentant des suggestions en conforminté avec l'architecture spécifiée (Composants).</small>
                            </p>
                        </p>

                        <h3>Indicateurs <@displayIndicatorsExport subprojectId visibility "matching"/></h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "matching"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
