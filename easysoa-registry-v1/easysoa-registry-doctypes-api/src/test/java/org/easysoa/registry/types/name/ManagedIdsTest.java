package org.easysoa.registry.types.name;

import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationSoaNodeId;
import org.junit.Assert;
import org.junit.Test;

public class ManagedIdsTest {

	@Test
	public void testServiceImplSoaName() {
		
		// Service Implementation name
		
		ServiceImplementationSoaNodeId wsTypeId = new ServiceImplementationSoaNodeId("ws:namespace:porttypename=servicename");
		Assert.assertEquals(ServiceIdentifierType.WEB_SERVICE, wsTypeId.getServiceIdentifierType());
		Assert.assertEquals("namespace", wsTypeId.getNamespace());
		Assert.assertEquals("porttypename", wsTypeId.getInterfaceName());
		Assert.assertEquals("servicename", wsTypeId.getImplementationName());
		Assert.assertEquals("ws:namespace:porttypename", wsTypeId.getInformationServiceSoaName());

		ServiceImplementationSoaNodeId itfTypeId = new ServiceImplementationSoaNodeId("java:project:interfaceclass=implementationclass");
		Assert.assertEquals(ServiceIdentifierType.JAVA_INTERFACE, itfTypeId.getServiceIdentifierType());
		Assert.assertEquals("project", itfTypeId.getNamespace());
		Assert.assertEquals("interfaceclass", itfTypeId.getInterfaceName());
		Assert.assertEquals("implementationclass", itfTypeId.getImplementationName());
		Assert.assertEquals("java:project:interfaceclass", itfTypeId.getInformationServiceSoaName());
		
	}
	
}
