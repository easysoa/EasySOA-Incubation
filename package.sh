#!/bin/bash

EASYSOA=easysoa-registry-1.0
EASYSOA_PATH=~/easysoa-tmp/$EASYSOA
DOC="EasySOA Registry 1.0 - see user manual at https://github.com/easysoa/EasySOA/wiki/EasySOA-v1.0-doc"

rm -rf $EASYSOA_PATH
mkdir -p $EASYSOA_PATH

echo Getting source of $EASYSOA...
#git clone https://github.com/easysoa/EasySOA-Incubation.git
pushd EasySOA-Incubation
#git checkout $EASYSOA

echo Building...
#mvn clean install -Pmarketplace -DskipTests

echo Copying Registry and DBB...
cp -rf easysoa-registry-v1/easysoa-registry-marketplace/target/nuxeo-cap-* $EASYSOA_PATH/
cp -rf easysoa-discovery-browsing $EASYSOA_PATH/

popd

echo Copying source discovery maven plugin dependencies...
mkdir -p $EASYSOA_PATH/easysoa-discovery-code-mavenplugin/m2_repo
pushd $EASYSOA_PATH/easysoa-discovery-code-mavenplugin/m2_repo

mkdir -p org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/1.0-SNAPSHOT/
cp ~/.m2/repository/org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/1.0-SNAPSHOT/easysoa-discovery-code-mavenplugin-* org/easysoa/discovery/code/easysoa-discovery-code-mavenplugin/1.0-SNAPSHOT/
mkdir -p easysoa/easy-soa-open-wide/0.0.0-SNAPSHOT
cp ~/.m2/repository/easysoa/easy-soa-open-wide/0.0.0-SNAPSHOT/easy-soa-open-wide-* easysoa/easy-soa-open-wide/0.0.0-SNAPSHOT/
mkdir -p org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa
cp ~/.m2/repository/org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa/nuxeo-tree-snapshot-* org/nuxeo/features/nuxeo-tree-snapshot/5.7.easysoa
mkdir -p org/easysoa/registry/easysoa-registry-test/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-test/1.0-SNAPSHOT/easysoa-registry-* org/easysoa/registry/easysoa-registry-test/1.0-SNAPSHOT
mkdir -p org/easysoa/registry/easysoa-registry/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry/1.0-SNAPSHOT/easysoa-registry-* org/easysoa/registry/easysoa-registry/1.0-SNAPSHOT
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-api/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-api/1.0-SNAPSHOT/easysoa-registry-doctypes-api-* org/easysoa/registry/easysoa-registry-doctypes-api/1.0-SNAPSHOT/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-core/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-core/1.0-SNAPSHOT/easysoa-registry-doctypes-core-* org/easysoa/registry/easysoa-registry-doctypes-core/1.0-SNAPSHOT/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-java-core/1.0-SNAPSHOT/
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-java-core/1.0-SNAPSHOT/easysoa-registry-doctypes-java-core-* org/easysoa/registry/easysoa-registry-doctypes-java-core/1.0-SNAPSHOT/
mkdir -p org/easysoa/registry/easysoa-registry-doctypes-java-api/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-doctypes-java-api/1.0-SNAPSHOT/easysoa-registry-doctypes-java-api-* org/easysoa/registry/easysoa-registry-doctypes-java-api/1.0-SNAPSHOT/
mkdir -p org/easysoa/registry/easysoa-registry-rest-core/1.0-SNAPSHOT/
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-rest-core/1.0-SNAPSHOT/easysoa-registry-rest-core-* org/easysoa/registry/easysoa-registry-rest-core/1.0-SNAPSHOT/
mkdir -p org/easysoa/registry/easysoa-registry-rest-client/1.0-SNAPSHOT
cp ~/.m2/repository/org/easysoa/registry/easysoa-registry-rest-client/1.0-SNAPSHOT/easysoa-registry-rest-client-* org/easysoa/registry/easysoa-registry-rest-client/1.0-SNAPSHOT/

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
