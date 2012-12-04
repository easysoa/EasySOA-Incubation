## AXXX Pivotal

* TODO allow to list, remove & add Client
* TODO new Client page
* TODO Client details page allowing to modify it BUT NOT IDENTIFIANT_CLIENT OR , also listing ContactClients and InformationAVPs
* TODO in ClientServiceImpl, (like in UserServiceImpl) at init if table empty fill with default data (ask MDU)
* TODO Client details page : button "Créer précompte" that calls TdrService.creerPrecompte() (ask MDU)
* TODO expose ContactSvc.asmx.wsdl WS using FraSCAti (and already gen'd source) and implement it on top of ClientService
* TODO OW enrich Pivotal's ContactSvc.asmx.wsdl's XML with :
.getClient(id) returns Client
.getRepartitionTypeStructure() returns [{ typeStructure, clientCount }]
then regenerate the Java classes, and implement them

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

Create the "axxx_pivotal" database and "axxx" user in the database server of your choice.
See below how to do it with PostgreSQL (default) or MySQL.

If different from default configuration (PostgreSQL), configure it in the AXXX Pivotal webapp.
In development, this is done in pom.xml . In production, this is done in
(src/main/resources/)META-INF/persistence.xml .


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

TODO from FStudio
TODO (database) properties in root composite (or property file ?)


### How to use :

Some data is filled by default :
* you can log in using "admin"/"admin"

& TODO wiki


### Architecture
Architecture is inspired by the one of INRIA's FraSCAti Studio : see website, source and [some feedbacks](https://github.com/easysoa/EasySOA/wiki/Frascati-studio-feedback).


### APV to Pivotal mapping
It's pretty straightforward. Some hints :

blocAdministratif/nomStructure => Raison_Sociale
blocAdministratif/adresse => Num_et_voie
tableauDeBord/partenaireDepuis => Anciennete
