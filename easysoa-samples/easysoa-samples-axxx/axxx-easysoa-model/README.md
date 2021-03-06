## AXXX EasySOA SOA Model

Features :

* AXXX Specifications :
 * Business Services & their SLA and OLA
 * Information Services & their SLA and OLAs
 * Business & Technical Components and their platform constraints 


### About

AXXX EasySOA SOA Model allows to get a complete SOA model of AXXX use case in a running EasySOA Registry.
See more information about the AXXX use case at https://github.com/easysoa/EasySOA/wiki/Axxx-use-case .

AXXX is a use case of the [EasySOA project](http://www.easysoa.org) and developed by its partners :
* [INRIA labs](http://www.inria.fr)
* [EasiFab](http://easifab.net)
* [Talend](http://www.talend.com)
* [Nuxeo](http://www.nuxeo.org)
* [Bull](http://www.bull.com)
* [Open Wide](http://www.openwide.fr)


### How to develop :

Running :

(from the Javadoc)

 * * from Eclipse : right-click > Run as Java application) to init full
 * EasySOA model of AXXX use case in running EasySOA Registry.
 * * from Maven command line :
 * mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit" -Dexec.args="username=Administrator password=Administrator"
 * 
 * In arguments may be given (in maven by -Dexec.args="[space separated arguments]") :
 * 
 * A. which step to play (if none are given, all of them will be played) :
 * * clean : deletes the existing model
 * * (Specifications are always (updated or) created)
 * * (Realisation must be done by source discovery)
 * * Deploiement : creates a Prod endpoint for TdrWebService
 * * Exploitation : creates an SOA monitoring indicator for it (by calling EndpointStateService)
 * 
 * B. properties, in [key]=[value] syntax
 * * username (default : Administrator)
 * * password (default : Administrator)
 * * apvHost (default : localhost)
 * * pivotalHost (default : localhost)
 * * registryHost (default : localhost)
 * * hostMode
 * * uploadUsingResourceUpdate (default : true) : uploads using rdi:url, else using automation
 * 
 * For instance, if you which to wipe the registry out, then fill Specifications and
 * create Realisation subproject, do :
 * 
 * mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit" -Dexec.args="clean Specifications Realisation username=Administrator password=Administrator"
 * 
 * then building (in the upper folder) all AXXX applications will discover their service
 * implementations, consumptions and their deliverables, register them in the Registry and
 * match them against the previously filled Specifications :
 * pushd ..
 * mvn clean install
 *
 * simulating deployment and operational exploitation can then be done by going back here and:
 *  mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit" -Dexec.args="Deploiement Exploitation username=Administrator password=Administrator"
 * 
 * WARNING: Requires a running EasySOA Registry on port 8080 (or at least a launched Nuxeo DM
 * with the Nuxeo Studio project "EasySOA" deployed)
