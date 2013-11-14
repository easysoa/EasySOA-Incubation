#!/bin/bash

EASYSOA=easysoa-registry-2.0
EASYSOA_REGISTRY_MV=1.0-SNAPSHOT
EASYSOA_STUDIO_MV=0.0.0-SNAPSHOT
EASYSOA_DISCOCODE_MV=$EASYSOA_REGISTRY_MV
EASYSOA_PROXY_MV=$EASYSOA_REGISTRY_MV
EASYSOA_PATH=~/easysoa-tmp/$EASYSOA
DOC="EasySOA Registry 2.0 - see user manual at https://github.com/easysoa/EasySOA/wiki/EasySOA-v2.0-doc"
TOMCAT_VERSION_NAME=apache-tomcat-7.0.42
TOMCAT_ARCHIVE=~/dl/dev/server/$TOMCAT_VERSION_NAME.tar.gz

rm -rf $EASYSOA_PATH
mkdir -p $EASYSOA_PATH

echo Getting source of $EASYSOA...
## alt 1 : clone & checkout
#git clone https://github.com/easysoa/EasySOA-Incubation.git
#pushd EasySOA-Incubation
#git checkout $EASYSOA
## alt 2 : reuse current local checkout
pushd EasySOA-Incubation
## in this case, source tag can be created and shared :
# git tag $EASYSOA
# git push origin $EASYSOA

echo Building Registry...
#mvn clean install -Pmarketplace -DskipTests
pushd easysoa-registry-v1
#./nuxeo.rb deploy
popd

echo Copying Registry...
cp -rf easysoa-registry-v1/easysoa-registry-marketplace/target/nuxeo-cap-* $EASYSOA_PATH/

echo Copying DBB...
cp -rf easysoa-discovery-browsing $EASYSOA_PATH/

popd

echo Copying source discovery maven plugin dependencies...
mkdir -p $EASYSOA_PATH/easysoa-discovery-code-mavenplugin/m2_repo
pushd $EASYSOA_PATH/easysoa-discovery-code-mavenplugin/m2_repo

mkdir -p org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/$EASYSOA_DISCOCODE_MV/
cp ~/.m2/repository/org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/$EASYSOA_DISCOCODE_MV/easysoa-discovery-code-mavenplugin-* org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/$EASYSOA_DISCOCODE_MV/
mkdir -p easysoa/easy-soa-open-wide/$EASYSOA_STUDIO_MV
cp ~/.m2/repository/easysoa/easy-soa-open-wide/$EASYSOA_STUDIO_MV/easy-soa-open-wide-* easysoa/easy-soa-open-wide/$EASYSOA_STUDIO_MV/
mkdir -p org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa
cp ~/.m2/repository/org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa/nuxeo-tree-snapshot-* org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa
mkdir -p org/easysoa/registry/easysoa-registry-test/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-test/$EASYSOA_REGISTRY_MV/easysoa-registry-* org/easysoa/registry/easysoa-registry-test/$EASYSOA_REGISTRY_MV
mkdir -p org/easysoa/registry/easysoa-registry/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry/$EASYSOA_REGISTRY_MV/easysoa-registry-* org/easysoa/registry/easysoa-registry/$EASYSOA_REGISTRY_MV
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-api/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-api/$EASYSOA_REGISTRY_MV/easysoa-registry-doctypes-api-* org/easysoa/registry/easysoa-registry-doctypes-api/$EASYSOA_REGISTRY_MV/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-core/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-core/$EASYSOA_REGISTRY_MV/easysoa-registry-doctypes-core-* org/easysoa/registry/easysoa-registry-doctypes-core/$EASYSOA_REGISTRY_MV/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-java-core/$EASYSOA_REGISTRY_MV/
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-java-core/$EASYSOA_REGISTRY_MV/easysoa-registry-doctypes-java-core-* org/easysoa/registry/easysoa-registry-doctypes-java-core/$EASYSOA_REGISTRY_MV/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-java-api/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-java-api/$EASYSOA_REGISTRY_MV/easysoa-registry-doctypes-java-api-* org/easysoa/registry/easysoa-registry-doctypes-java-api/$EASYSOA_REGISTRY_MV/
mkdir -p org/easysoa/registry/easysoa-registry-rest-core/$EASYSOA_REGISTRY_MV/
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-rest-core/$EASYSOA_REGISTRY_MV/easysoa-registry-rest-core-* org/easysoa/registry/easysoa-registry-rest-core/$EASYSOA_REGISTRY_MV/
mkdir -p org/easysoa/registry/easysoa-registry-rest-client/$EASYSOA_REGISTRY_MV
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-rest-client/$EASYSOA_REGISTRY_MV/easysoa-registry-rest-client-* org/easysoa/registry/easysoa-registry-rest-client/$EASYSOA_REGISTRY_MV/

popd

pushd EasySOA

echo Building Proxy...
#mvn clean install -DskipTests

echo Getting source of $EASYSOA Proxy...
## alt 1 : clone & checkout
#git clone https://github.com/easysoa/EasySOA.git
#pushd EasySOA
#git checkout $EASYSOA
## alt 2 : reuse current local checkout
pushd EasySOA
## in this case, source tag can be created and shared :
# git tag $EASYSOA
# git push origin $EASYSOA

echo Copying Proxy...
tar xfz $TOMCAT_ARCHIVE -C $EASYSOA_PATH/
mv -f $EASYSOA_PATH/$TOMCAT_VERSION_NAME $EASYSOA_PATH/apache-tomcat7-proxy
cp -rf easysoa-proxy/easysoa-proxy-core/easysoa-proxy-web/src/main/resources/tomcat7/* $EASYSOA_PATH/apache-tomcat7-proxy
rm -rf $EASYSOA_PATH/apache-tomcat7-proxy/webapps/easysoa-proxy*
cp -rf easysoa-proxy/easysoa-proxy-core/easysoa-proxy-war/target/easysoa-proxy $EASYSOA_PATH/apache-tomcat7-proxy/webapps
rm -rf $EASYSOA_PATH/apache-tomcat7-proxy/webapps/ROOT/*
cp -rf easysoa-proxy/easysoa-proxy-core/easysoa-proxy-web/target/easysoa-proxy-web/* $EASYSOA_PATH/apache-tomcat7-proxy/webapps/ROOT

popd

echo Archiving...
pushd $EASYSOA_PATH
echo $DOC >> README.txt
cd ..
tar cfz $EASYSOA.tar.gz $EASYSOA

echo Uploading...
scp $EASYSOA.tar.gz root@openwide-vm-easysoa.accelance.net:/var/www/download

popd
#rm -rf $EASYSOA_PATH

echo Done !
