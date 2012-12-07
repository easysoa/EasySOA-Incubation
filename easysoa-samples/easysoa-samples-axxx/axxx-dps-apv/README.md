## AXXX APV Web

* TODO develop the APV Java model (beyond Precompte) 
* TODO persist it in Hibernate / PostgreSQL (see axxx-dcv-pivotal/README.md)
* TODO develop webapp in Tomcat and UI in Spring MVC, using session in view pattern to handle transactions
* TODO expose it as WS (beyond Precompte), using CXF Interceptors & sessions in view pattern to handle transactions
* TODO OW when a Projet Vacances is validated in APV, develop in Java a call of ContactSvc.Client and one call of ContactSvc.Information_APV per public (Information_APV.Bilan_Libelle) : accompagnateurs, enfants, jeunes, familles, adultesisoles, seniors, aidantsfamiliaux

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
TODO

### How to develop :

Prerequisites : Java 6, Maven 2+

Compiling :

        mvn clean install -DskipTests

Running :

FOR NOW ONLY PrecomptePartenaireWebService !!
        in Eclipse, on PrecomptePartenaireWebServiceTestStarter do right-click > Run as Java application

TODO then in your browser open http://

Debugging :

Either
* from Eclipse : right-click > Debug as Java application
* for tests (or code discovery maven plugin), from Maven : add ```-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=y``` in the MAVEN_OPTS environment variable (```export MAVEN_OPTS="..."``` in command line shell or in your $HOME/.bashrc )
* for Tomcat webapp : 


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
