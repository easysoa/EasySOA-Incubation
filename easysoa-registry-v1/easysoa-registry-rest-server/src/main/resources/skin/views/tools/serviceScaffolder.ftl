<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">
    <#include "/views/EasySOA/urlMacros.ftl">


    <head>
        <title>EasySOA Tools</title>
        <@includeResources/>
    </head>

    <body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                Service Scaffolder Configuration
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                            <h3>Service Scaffolder : Call simple SOAP services</h3>
                            <p>
Unfortunately, some EasySOA 0.4 features have not been packaged in 2.0, and the Service Scaffolder is one of them.
However, if you've also installed EasySOA 0.4 separately, its Service Scaffolder will still work with EasySOA 2.0 : use the Cartography tool to go to a service's deployment and there click on the "Service Scaffolder" tool (provided both run on the same host,
or serviceScaffolder.url has been configured in nxserver/config/easysoa.properties below the EasySOA Registry 2.0
install path), or directly enter in your browser an URL like <a href="http://localhost:8090/scaffoldingProxy/?wsdlUrl=YOUR_WSDL_URL">http://localhost:8090/scaffoldingProxy/?wsdlUrl=YOUR_WSDL_URL</a>.
                            </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
