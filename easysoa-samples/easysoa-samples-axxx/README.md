AXXX Use Case

See introduction at https://github.com/easysoa/EasySOA/wiki/Axxx-use-case 

The AXXX scenario is meant to be played like this :
* 1. model Specifications in registry
* 1.1 auto fill registry by RemoteRepositoryInit (afterwards you can show a little bit how to work on Specifications)
* 1.2 have a look at dashboard(s)
* 1.3 approval workflow : see changes & publish
* 2. develop Realisation
* 2.1 do code disco in AXXX projects by 'mvn clean install' (afterwards you can show a little bit how to work on code)
* 1.2 have a look at dashboard(s)
* 3. deploy
* 3.1 by starting AXXX applications or mocks
* 3.2 do web disco

TODO : (AXXX impl,) web disco, monitored messages disco, environments & mock vs actual impl, publish impl & depl, CIO dashboard, integrated platforms (Jasmine SOA monitoring, FStudio call & mocking, Talend service development)

Additional samples :
in rest-server, DashboardMatchingSamples fills registry with data that helps showcase matching


## Deploying AXXX Use case on VMs

* build each application (EasySOA Registry v1, AXXX Pivotal & APV, but also the proxy and its UI, and optionally FraSCAti Studio & Cloud). See in their READMEs. If you build them on their VMs (not advised !),
 * first remove the Nuxeo Studio jar from the Maven repository to make sure it'll be updated : rm -rf ~/.m2/repository/easysoa/easy-soa-open-wide/
 * make sure you're using java jdk7 (ex. source ~/.bashrc_jdk7) for all EasySOA & EasySOA-Incubation projects
* deploy applications
 * for the Registry, it's easier to take a full build that includes the Nuxeo server base (NB. version nuxeo-cap-5.7-I20130610_0116-tomcat), which can be found once built in easysoa-registry-v1/easysoa-registry-marketplace/target .
 * put up Apache Tomcat 7 servers for the others : on vmregistry in ~/install/apache-tomcat7 for FraSCAti Studio & Cloud and in ~/install/apache-tomcat7-proxy for the proxy, on vmpivotal in ~/install/apache-tomcat7 for Pivotal, on vmapv in ~/install/apache-tomcat7 for AVP, and put theirs respective wars in their webapps/ folders.
* install each application on its respective VM (besides what's already done in the vmregistry, vmpivotal & vmapv folders below axxx-deploy/ . See in their READMEs). This includes
 * for the registry, installing a Nuxeo
 * databases (create user, databases & allow connection ; see in their READMEs),
 * unix services (install each of the provided /etc/init.d files , ex. on Debian using update-rc.d FILE defaults)
 * create a "nuxeo" link to the nuxeo install folder to make things easier : ln -fs ~/install/nuxeo-XXXVERSION ~/install/nuxeo
* copy the deployment files from the vmregistry, vmpivotal & vmapv folders below axxx-deploy/ to their respective VM. First update those files if their original versions have changed :
 * vmregistry/home/axxx/install/apache-tomcat7-proxy/webapps/easysoa-proxy/WEB-INF/classes/httpDiscoveryProxy.composite
 * vmregistry/home/axxx/install/nuxeo/nxserver/config/easysoa.properties
* also install JASMINe & its EasySOA probe (see JASMINEe source) & adapter (sources here) extensions, Talend ESB & its AXXX orchestration (sources here).
* on all VMs, add to the /etc/hosts file hostname definitions for vmregistry, vmpivotal & vmapv targeting their real IP (see example in the the axxx-deploy/hosts file)
* do the remaining host configuration work. If you only want it to work on your own computer, the simplest is to put in your own /etc/hosts file the vmregistry, vmpivotal & vmapv hosts with their real IPs. But if you want it to work for all out of the box, you have to :
 * on vmregistry, in ~/install/nuxeo/nxserver/config/easysoa.properties , change the JASMINe URL to the REAL vmpivotal host : jasmine.url=http://REALHOST.NET:9100/jasmine/
 * on vmregistry, in ~/install/apache-tomcat7-proxy/webapps/easysoa-proxy/WEB-INF/classes/httpDiscoveryProxy.properties , change the password (nuxeo.auth.password) to the real one
 * NOT MANDATORY on vmregistry, in ~/install/apache-tomcat7-proxy/webapps/easysoa-proxy/WEB-INF/classes/httpDiscoveryProxy.composite, change the Proxy host to the REAL vmregistry host : ```<tuscany:binding.http uri="http://REALHOST.NET:8082/" />```. To do that, you have to first unarchive the Proxy WAR (ex. by starting Tomcat once) and remove it (else FraSCAti won't start up because of doubled definitions).
* start & test everything everything : check that processes are up, web UIs, web services URLs, click on all features from the http://vmregistry:8080/nuxeo/easysoa/site to test them.
 

