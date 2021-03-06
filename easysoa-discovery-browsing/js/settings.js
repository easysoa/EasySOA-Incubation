// EasySOA Web
// Copyright (c) 2011 Open Wide and others
//
// MIT licensed
//
// Contact : easysoa-dev@googlegroups.com

/* ==============
 * Proxy settings
 * ==============
 */

exports.PROXY_PORT = '8081';

// to enable forward proxying to your company's proxy, uncomment and adapt :
//exports.FORWARD_PROXY_HOST = 'myCompanyProxy.com'
//exports.FORWARD_PROXY_PORT = 80

/* ===================
 * Web server settings
 * ===================
 */

exports.WEB_PORT = '8083';

exports.WWW_PATH = __dirname + '/../www';

exports.NO_AUTH_NEEDED = [
  '^/favicon.ico',
  '^/css/*',
  '^/js/*',
  '^/lib/*',
  '^/img/*',
  '^/js/bookmarklet/*',
  '^/demo-intranet/*'
];

exports.SCAFFOLDING_SERVER_URL = "http://127.0.0.1:18000";

/* ==============
 * Nuxeo settings
 * ==============
 */

exports.NUXEO_URL = NUXEO_URL               = 'http://127.0.0.1:8080/nuxeo';
exports.NUXEO_REST_URL                      = NUXEO_URL + '/site';
exports.EASYSOA_ROOT_URL = EASYSOA_ROOT_URL = NUXEO_URL + '/site/easysoa';
exports.EASYSOA_DISCOVERY_PATH              = 'easysoa/registry';
exports.EASYSOA_SERVICE_FINDER_PATH         = 'easysoa/servicefinder';

exports.SERVICE_FINDER_IGNORE = [
  '\\.css',
  '\\.jpg',
  '\\.gif',
  '\\.png',
  '\\.js$', // and not .jsp !
  '\\.ico',
  'localhost:7001', // FraSCAti (part of EasySOA Light)
  '127.0.0.1:7001',
  'jQuery',
  '/socket\\.io/',
  'google',
  '8080', // nuxeo registry
  '8083',
  NUXEO_URL
];
