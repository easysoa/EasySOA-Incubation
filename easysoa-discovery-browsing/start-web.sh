#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "EasySOA Web Servers"
echo "(Default static server configuration: Host=127.0.0.1, Port=8083"
echo " Default proxy configuration: Host=127.0.0.1, Port=8081)"
echo "DEPENDENCIES: Service registry (to log in)"
echo path : `pwd`
echo $LINE

# Checking 32 or 64 bits
# NB. not using uname which could be wrong in chrooted envs
# see http://stackoverflow.com/questions/106387/is-it-possible-to-detect-32-bit-vs-64-bit-in-a-bash-script
if [ `getconf LONG_BIT` = "64" ]
then
    echo "Using 64-bit node"
    NODE=../node64
else
    echo "Using 32-bit node"
    NODE=../node
fi
cd js
$NODE -v
$NODE --debug easysoa.js

