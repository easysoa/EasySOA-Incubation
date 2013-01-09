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
 * mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit"
 * 
 * The following arguments may be given (in maven by -Dexec.args="<space separated arguments>") to
 * specify which step to play (if none are given, all of them will be played) :
 * * clean : deletes the existing model
 * * (Specifications are always (updated or) created)
 * * (Realisation must be done by source discovery)
 * * Deploiement : creates a Prod endpoint for TdrWebService
 * * Exploitation : creates an SOA monitoring indicator for it (by calling EndpointStateService)
 * 
 * WARNING: Requires a running EasySOA Registry on port 8080 (or at least a launched Nuxeo DM
 * with the Nuxeo Studio project "EasySOA" deployed)