// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

require('longjohn');
var http = require('http');
var express = require('express');

var settings = require('./settings');
var authComponent = require('./auth');
var proxyComponent = require('./proxy');
var dbbComponent = require('./dbb');
var lightComponent = require('./light');
var nuxeoComponent = require('./nuxeo');

/**
 * Application entry point.
 * Serves both an EasySOA Web server & a discovery by browsing proxy
 *
 * Author: Marwane Kalam-Alami
 */

/*
 * Set up web server
 */
var app = express();
var webServer = http.createServer(app);
app.configure(function(){

  // Request formatting
  app.use(express.cookieParser());
  app.use(express.session({ secret: 'easysoa-web' }));
  app.use(express.bodyParser());
  
  // Components routing & middleware configuration
  authComponent.configure(app);
  dbbComponent.configure(app, webServer);
  lightComponent.configure(app);
  nuxeoComponent.configure(app);
  
  // Router
  app.use(app.router);
  
  // Static file server
  app.use(express.favicon(settings.WWW_PATH + '/favicon.ico'));
  app.use(express.static(settings.WWW_PATH));
  app.use(express.directory(settings.WWW_PATH));
    
});
webServer.listen(settings.WEB_PORT);

nuxeoComponent.startConnectionChecker();

/*
 * Set up proxy server
 */
var proxyServer = http.createServer(function(request, response) {
  
  // Service finder
  dbbComponent.handleProxyRequest(request, response);
  
  // Proxy
  proxyComponent.handleProxyRequest(request, response);
  
});
proxyServer.listen(settings.PROXY_PORT);
