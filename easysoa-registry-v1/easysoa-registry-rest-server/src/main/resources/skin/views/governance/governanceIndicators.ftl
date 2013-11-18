<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Gouvernance</title>
        <@includeResources/>
    </head>

    <body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
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
                                    <@displayIndicatorInTable indicators["serviceWithoutComponent"]/>
                                </tr>
                                <tr>
                                    <@displayIndicatorInTable indicators["serviceWithDocumentation"]/>
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
