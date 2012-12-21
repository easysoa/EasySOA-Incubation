## AXXX APV Web


### Features

* no user & security for now
* APV POJO model : PrecomptePartenaire, Tdr, Projet
* Hibernate / PostgreSQL persistence, session in view filter (rather than session = transaction)
 * TODO doc, see axxx-dcv-pivotal/README.md
* Spring MVC UI / Tomcat webapp
 * field error handling : required, binding (ex. String instead of int), business errors (numbers <= 0...) 
 => by Spring MVC JSR303 / Hibernate Validator annotations : defined in POJO, checked in controller, displayed in view
 (see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/ ) 
 beyond : by controller returning "errors" or by js like Pivotal
* WS :
 * exposes : PrecomptePartenaire
 * TODO TdrService getters ex. for monitoring business indicators ??
 * calls : TODO Pivotal.ContactSvc
* OPT user & security (admin vs tdr user & POJO...)

Business processes :
* TODO home page with tab links to pages : "tdrPrecomptes", "tdrs", "projets"
* TODO in "tdrPrecomptes" page, list Tdr (nomStructure, ville) with "created" status (see "tdrs" page)
 * TODO for each allow to delete them, allow to go to details page
 * TODO button "TEST SEULEMENT Créer une nouvelle Tdr" that goes to a "new tdr" page listing only tdr fields
 that correspond to PrecomptePartenaire fields, with the right validation annotations (@NotEmpty...).
 Clicking on it computes computed fields and creates a new tdr.
 * TODO in details page, list all fields but allow to edit only non-identifiantClientPivotal or computed ones
 (dotationglobale), with validation. Clicking on "sauver" button computes them and saves the tdr.
 * TODO also there, enable "approuver" button only if : TdrTdb fields filled (save for computed fields)
 & valid (dotationannuelle > 0), especially "conventionnement" ones. Clicking on it does a "sauver", then
 sets status to "approved", saves, OPT and redirects to approved tdr details page.
 * OPT also require tdr user information to be provided
* TODO implement CreerPrecompteService on top of TdrService, with "created" status
 * TODO map PrecomptePartenaire to Tdr
* TODO in "tdrs" page, list Tdr with "approved" status (copy or reuse previous ones)
 * TODO for each allow to delete them, allow to go to details page with "sauver" button & validated fields
 * TODO for each list item & in details page, "projets" link to "projets" page listing the tdr's projets
 & "nouveau projet" button linking to empty projet details page
* TODO in "projets" page, list Projet (typeLieu, periode, departement, status), only of current tdr if any
 * TODO for each allow to delete them, allow to go to details page with "sauver" button & validated fields.
 Clicking on "sauver" computes computed fields (OPT xxxBenefs from Benef lists & their accounting fields) and saves the projet.
 * TODO also there, enable "approuver" button only if : Benefs fields filled (save for computed fields)
 & valid (benefs > 0, montant > 0). Clicking on it does a "sauver", then sets status to "approved",
 saves projet, calls TdrService.updateTdb(), and redirects to (tdr's) "projets" page.
  * OPT list & display details of Benef, compute Benefs fields from them
  * LATER manage accounting fields (BenefFinancement) in Benef
* TODO in TdrService :
 * updateTdb() recomputes its tdr's impacted computed fields (by queries on all approved projets),
 saves tdr, then calls TdrService.publish()
 * which calls (in Java) ContactSvc.Client once, and ContactSvc.Information_APV once per public
 (Information_APV.Bilan_Libelle) : enfants, jeunes, adultesisoles, seniors
 * for this, generate pivotal/ContactSvc.wsdl to Java, inject it in Spring, OPT write mock & test
 * LATER display approved Projets in readonly & list them separately

* TODO UI : prettify, more AXXX-y (see www.axxx.fr)


### About
AXXX APV is a mock-up business application managing requests for funded holidays, deployed at AXXX and managed by the DPS IT department.
See more information about the AXXX use case at https://github.com/easysoa/EasySOA/wiki/Axxx-use-case .

AXXX is a use case of the [EasySOA project](http://www.easysoa.org) and developed by its partners :
* [INRIA labs](http://www.inria.fr)
* [EasiFab](http://easifab.net)
* [Talend](http://www.talend.com)
* [Nuxeo](http://www.nuxeo.org)
* [Bull](http://www.bull.com)
* [Open Wide](http://www.openwide.fr)


### How to install :

Create the "axxx_apv" database and "axxx" user in the database server of your choice.
See below how to do it with PostgreSQL (default) or MySQL.

If different from default configuration (PostgreSQL), configure it in the AXXX APV webapp.
In development, this is done in (axxx-dps-apv-core/src/main/resources/)axxx-dps-apv.properties .
In production, this is done in webapp in WEB-INF/classes/axxx-dps-apv-deploy.properties .


### How to develop :

Prerequisites : Java 6, Maven 2+

#### Compiling :

        mvn clean install -DskipTests

#### Running :

to expose WS from Eclipse, on PrecomptePartenaireWebServiceTestStarter do right-click > Run as Java application
        and it'll be at http://localhost:8076/services/ProjetService?wsdl


#### Deploying :

Download Tomcat 6 from http://tomcat.apache.org/download-60.cgi , unzip it and change all
80xx ports to 70xx in conf/server.xml (allows to have a running EasySOA Registry on the 8080 port). 

Now copy axxx-dps-apv-web/target/*war in its webapps directory, go in bin/ directory and start it :

	./catalina.sh run
	
If the following error message is displayed :

	The BASEDIR environment variable is not defined correctly
	This environment variable is needed to run this program

Just go in bin folder and execute the following command : 

	chmod +x *.sh

Then start Tomcat with the command ./catalina.sh run or ./startup.sh

#### Debugging :

Either
* from Eclipse : right-click > Debug as Java application
* for tests (or code discovery maven plugin), from Maven : add ```-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=y``` in the MAVEN_OPTS environment variable (```export MAVEN_OPTS="..."``` in command line shell or in your $HOME/.bashrc )
* for Tomcat webapp : change JPDA_ADDRESS in catalina.sh if you want (defaults to 8000) and start it using instead

	./catalina.sh jpda run


### Creating database with PostgreSQL :

Get PostgreSQL (9.1) using you favorite package manager or at http://www.postgresql.org/ .

Create "axxx" user :

        $> su - postgres
        $> psql
        $postgresql> create user axxx with password 'axxx' createdb;
        $postgresql> \q

Allow him to log in locally using password :

        $> vi /etc/postgresql/9.1/main/pg_hba.conf

        # [AXXX] local (for Unix domain sockets) & IPv4 connections
        local   all             axxx                                    md5
        host    all             axxx            127.0.0.1/32            md5

        $> /etc/init.d/postgresql reload

Create "axxx_apv" database :

$> psql -U axxx postgres -h localhost
$postgresql> create database axxx_apv encoding 'UTF8';

Now you should be able to log in to the database with the created user :

psql -U axxx axxx_apv -h localhost


### Creating database with MySQL :

Get MySQL (5.1) using you favorite package manager or at http://www.mysql.com/ .

        $> mysql -u root -p
        $mysql> create database axxx_apv character set 'utf8';
        $mysql> grant all on axxx_apv.* to 'axxx'@'localhost' identified by 'axxx';
        $mysql> quit;

Now you should be able to log in to the database with the created user :

        $> mysql -u axxx axxx_apv -p


### How to use :
See Features


## FAQ

### Build fails
with error "The POM for org.easysoa.discovery.code:easysoa-discovery-code-mavenplugin:jar:1.0-SNAPSHOT is missing, no dependency information available" 

Solution :

You need to first build the EasySOA source discovery plugin once (and the EasySOA registry, but you don't need to start it !) by doing at the EasySOA-Incubation root : mvn clean install -DskipTests

Or you can skip it by adding to the mvn command line : -DskipEasySOA

### OutOfMemoryError - PermGen space error
May happen with other webapps in the same tomcat. To solve it, add to catalina.sh :
	
	JAVA_OPTS=-XX:MaxPermSize=128m
