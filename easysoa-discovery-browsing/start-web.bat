@echo off

set LINE=----------------------------------------------------

echo %LINE%
echo HTTP Web Server
echo (Default static server configuration: Host=127.0.0.1, Port=8083
echo  Default proxy configuration: Host=127.0.0.1, Port=8081)
echo DEPENDENCIES: Service registry (to log in)
echo %LINE%

rem Default port: 8083
cd js
"node.exe" -v
"node.exe" easysoa.js
