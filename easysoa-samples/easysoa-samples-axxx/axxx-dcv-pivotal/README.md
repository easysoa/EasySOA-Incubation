## AXXX Pivotal

Features :

* login, logout
* allow to list, remove & add Client
* new Client page
* Client details page allowing to modify it (save identifiantClient & balance sheet summary fields), listing ContactClients in tabs, and InformationAVPs below in lift
* in ClientServiceImpl, (like in UserServiceImpl) at init if table empty fill with default data, including the 3 adresses of Uniserv mock and further from unbranded reporting SQL data (lookup tdr_bloca and other field names)
* Client details page : button "Créer précompte" that calls TdrService.creerPrecompte(), displays OK or error message at the end of creerPrecompte, when it is disabled
 * TODO refer to locally stored APV WSDLs, Change localhost by vmapv and configure it int /etc/host file for re-routing
* exposes ContactSvc.asmx.wsdl WS using FraSCAti and implements it on top of ClientService
 * Client only for updates, ContactClient & Information_APV for create & updates
 * updates' key fields : Identifiant_Client, for Information_APV also Bilan_Libelle & Bilan_Annee, OPT for ContactClient also Fonction
* Pivotal's ContactSvc.asmx.wsdl's additionally exposes (required for SLA monitoring) :
 * .getClient(ididentifiantClient) returns Client
 * .getRepartitionTypeStructure() returns [{ typeStructure, clientCount }]

* TODO UI : more MS-y, display partner logos that are in pivotal/images/logos and pivotal/css/images.css, remove unused images & js
* TODO deploy in production (notably doc ; see FStudio)


### About

AXXX Pivotal is a mock-up CRM application deployed at AXXX and managed by the DCV IT department.
See more information about the AXXX use case at https://github.com/easysoa/EasySOA/wiki/Axxx-use-case .

AXXX is a use case of the [EasySOA project](http://www.easysoa.org) and developed by its partners :
* [INRIA labs](http://www.inria.fr)
* [EasiFab](http://easifab.net)
* [Talend](http://www.talend.com)
* [Nuxeo](http://www.nuxeo.org)
* [Bull](http://www.bull.com)
* [Open Wide](http://www.openwide.fr)


### How to install :

Append the contents of the provided ../hosts  file to your system's hosts file
(to be found on Linux at /etc/hosts and on Windows at C:\Windows\System32\drivers\etc\hosts).

Create the "axxx_pivotal" database and "axxx" user in the database server of your choice.
See below how to do it with PostgreSQL (default) or MySQL.

If your choice differs from default configuration (PostgreSQL), you have
* to change accordingly properties named "hibernate.*" in webapp configuration
* and provide the right database driver jar

To do this :
* in development, these properties are in pom.xml and the database driver has to be added
as a maven dependency there also (for MySQL, commented configuration is already provided 
for both and has only to be uncommented)
* in production, these properties can be changed by copying (src/main/resources/)META-INF/persistence.xml
within the webapp to the WEB-INF/classes/META-INF/persistence.xml file and adapting it,
and the database driver has to be added in the webapp's WEB-INF/lib or in your application server's
shared library directory.


### How to develop :

Prerequisites : Java 6, Maven 2+

Compiling :

	mvn clean install -DskipTests

Running :

	mvn -Pexplorer -DskipTests
	
then in your browser open http://localhost:18000/pivotal


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

Create "axxx_pivotal" database :

$> psql -U axxx postgres -h localhost
$postgresql> create database axxx_pivotal encoding 'UTF8';

Now you should be able to log in to the database with the created user :

psql -U axxx axxx_pivotal -h localhost


### Creating database with MySQL :

Get MySQL (5.1) using you favorite package manager or at http://www.mysql.com/ .

	$> mysql -u root -p
	$mysql> create database axxx_pivotal character set 'utf8';
	$mysql> grant all on axxx_pivotal.* to 'axxx'@'localhost' identified by 'axxx';
	$mysql> quit;

Now you should be able to log in to the database with the created user :

	$> mysql -u axxx axxx_pivotal -p


### How to deploy in production :

The compilation step above produces a .war file in target/ that you can deploy in your
favorite application server.

*WARNING* for now it only supports to be deployed at application server root url.

Here is how to do it with Apache Tomcat :

Download Tomcat 6 from http://tomcat.apache.org/download-60.cgi , unzip it and change all
80xx ports to 70xx in conf/server.xml (allows to have a running EasySOA Registry on the 8080 port). 

Now copy axxx-dcv-pivotal/target/pivotal/* in its webapps/ROOT directory :

   rm -rf [TOMCAT_HOME]/webapps/ROOT/*   
   scp -rp axxx-dcv-pivotal/target/pivotal/* [USER]:[REMOTE_HOST]:/home/[USER]/install/apache-tomcat-6.0.36/webapps/ROOT/

   On local machine, you can use this command :
   cp -rf axxx-dps-apv-web/target/pivotal/* [TOMCAT_HOME]/webapps/ROOT/

Then go in bin/ directory and start it :

    cd [TOMCAT_HOME]/bin/
	./catalina.sh run
	
And AXXX Pivotal will be available at http://localhost:7080/ .
	
If the following error message is displayed :

	The BASEDIR environment variable is not defined correctly
	This environment variable is needed to run this program

Then before starting it, just go in bin folder and execute the following command : 

	chmod +x *.sh

Update the appended contents of your system's hosts file to the
actual IPs of the deployment computers.


### How to use :

Some data is filled by default :
* you can log in using "admin"/"admin"

& TODO wiki


### Architecture
Architecture is inspired by the one of INRIA's FraSCAti Studio : see website, source and [some feedbacks](https://github.com/easysoa/EasySOA/wiki/Frascati-studio-feedback).
* JPA EntityManager / Hibernate persistence
* FraSCAti application service layer
* UI :
 * velocity templates (by FraSCAti's implementation.velocity) for views and actions / controllers
 * additionally, AJAX by FraSCAti REST services
* simple login & security layer (User in session, explicitly passed to application services)


### APV to Pivotal mapping
It's pretty straightforward. Some hints :

* blocAdministratif/nomStructure => Raison_Sociale
* blocAdministratif/adresse => Num_et_voie
* tableauDeBord/partenaireDepuis => Anciennete


## FAQ

### Build fails
with error "The POM for org.easysoa.discovery.code:easysoa-discovery-code-mavenplugin:jar:1.0-SNAPSHOT is missing, no dependency information available"

Solution :

You need to first build the EasySOA source discovery plugin once (and the EasySOA registry, but you don't need to start it !) by doing at the EasySOA-Incubation root : mvn clean install -DskipTests

Or you can skip it by adding to the mvn command line : -DskipEasySOA

