package org.easysoa.registry.types.name;

import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationId;
import org.junit.Assert;
import org.junit.Test;

public class NamesTest {

	@Test
	public void testServiceImplName() {
		
		// Service Implementation name
		
		ServiceImplementationId wsTypeName = ServiceImplementationId.fromName("ws:namespace:porttypename=servicename");
		Assert.assertEquals(ServiceIdentifierType.WEB_SERVICE, wsTypeName.getType());
		Assert.assertEquals("namespace", wsTypeName.getNamespace());
		Assert.assertEquals("porttypename", wsTypeName.getInterfaceName());
		Assert.assertEquals("servicename", wsTypeName.getImplementationName());
		Assert.assertEquals("ws:namespace:porttypename", wsTypeName.getInformationServiceSoaName());

		ServiceImplementationId itfTypeName = ServiceImplementationId.fromName("java:project:interfaceclass=implementationclass");
		Assert.assertEquals(ServiceIdentifierType.JAVA_INTERFACE, itfTypeName.getType());
		Assert.assertEquals("project", itfTypeName.getNamespace());
		Assert.assertEquals("interfaceclass", itfTypeName.getInterfaceName());
		Assert.assertEquals("implementationclass", itfTypeName.getImplementationName());
		Assert.assertEquals("java:project:interfaceclass", itfTypeName.getInformationServiceSoaName());
		
	}
	
}
