// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #3 
 * Description: Create mock and make it replace the scaffolder client's targeted service
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

// Reproduce Scenario #01: Create Service Scaffolder Client for a given existing service endpoint

var imports = require('./01-scaffolder.js');
testEnv = imports.testEnv;
scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
serviceEndpointToScaffold = imports.serviceEndpointToScaffold;

console.log("------------------------------------");
console.log("[Scenario #3]");

// Create mock of existing endpoint

var serviceMock = new api.JavascriptImpl("MyMock", options={isMock: true}, serviceImplToMock=serviceEndpointToScaffold.getImpl());
var serviceMockEndpoint = testEnv.addServiceImpl(serviceMock);

// Update Scaffolder

scaffolderClientEndpoint.setTargetEndpoint(serviceMockEndpoint);
scaffolderClientEndpoint.display();
console.log("Done.");