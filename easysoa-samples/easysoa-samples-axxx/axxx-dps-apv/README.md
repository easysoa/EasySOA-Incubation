## AXXX APV Web


## Features
* APV POJO model : PrecomptePartenaire, Tdr, Projet
 * TODO unify PrecomptePartenaire & Tdr
* Hibernate / PostgreSQL persistence, Spring annotated transactions & sessions (rather than session in view)
 * TODO doc, see axxx-dcv-pivotal/README.md
* Spring MVC UI / Tomcat webapp
* Exposes WS : PrecomptePartenaire
 * TODO TdrService getters ex. for monitoring business indicators ??

TODO admin (vs tdr) user & POJO ?!

Business processes :
* TODO Tdr precompte filling : fill & approve precompte to available tdr ; allow only if complete (montant > 0, user info provided...)
* TODO Projet Vacances :
 * fill "goal" fields
 * fill beneficiary aggregated fields
 * OPT add beneficiaries
 * OPT with accounting
 * OPT fill post holiday fields
* TODO Projet Vacances indicators update : on each click on "save",
 * update accounting for each benef & globally,
 * AS LESS AS POSSIBLE (prefer ex. computing read-only "Autres" field from other accounting fields) fill "errorMessages" field
 * set "approvable" state if OK (nbBenefs > 0, balanced accounting, goals provided...)
* TODO Projet Vacances publication process : when clicking on "publish" (only if "approvable"),
 * set "published" status
 * update tdr indicators TODO Q only through queries or stored also ??
 * call in Java ContactSvc.Client once, and call ContactSvc.Information_APV once per public (Information_APV.Bilan_Libelle) : enfants, jeunes, adultesisoles, seniors (NO accompagnateurs, familles, aidantsfamiliaux)

* TODO UI : prettify, more AXXX-y (see www.axxx.fr)

TODO pouvoir se débarasser de tout s'il le faut : bilan comptable, voire benefs...
TODO Benef
TODO store bilan in other POJOs in same table
TODO Q only through queries or stored also ??


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
TODO create database as below


### How to develop :

Prerequisites : Java 6, Maven 2+

Compiling :

        mvn clean install -DskipTests

Running :

FOR NOW ONLY PrecomptePartenaireWebService !!
        in Eclipse, on PrecomptePartenaireWebServiceTestStarter do right-click > Run as Java application

TODO then in your browser open http://

Download Tomcat 6 from http://tomcat.apache.org/download-60.cgi
unzip it, then copy target/*war in its webapps directory and start it : ./catalina.sh run

Debugging :

Either
* from Eclipse : right-click > Debug as Java application
* for tests (or code discovery maven plugin), from Maven : add ```-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=y``` in the MAVEN_OPTS environment variable (```export MAVEN_OPTS="..."``` in command line shell or in your $HOME/.bashrc )
* for Tomcat webapp : TODO enable debugging in Tomcat


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
TODO




## FAQ

### Build fails
with error "The POM for org.easysoa.discovery.code:easysoa-discovery-code-mavenplugin:jar:1.0-SNAPSHOT is missing, no dependency information available" 

Solution :

You need to first build the EasySOA source discovery plugin once (and the EasySOA registry, but you don't need to start it !) by doing at the EasySOA-Incubation root : mvn clean install -DskipTests

Or you can skip it by adding to the mvn command line : -DskipEasySOA

