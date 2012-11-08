package org.easysoa.registry.types.name;

import org.easysoa.registry.types.names.ServiceIdentifierType;
import org.easysoa.registry.types.names.ServiceImplementationName;
import org.junit.Assert;
import org.junit.Test;

public class NamesTest {

	@Test
	public void testServiceImplName() {
		
		// Service Implementation name
		
		ServiceImplementationName wsTypeName = new ServiceImplementationName("ws:namespace:porttypename=servicename");
		Assert.assertEquals(ServiceIdentifierType.WEB_SERVICE, wsTypeName.getType());
		Assert.assertEquals("namespace", wsTypeName.getNamespace());
		Assert.assertEquals("porttypename", wsTypeName.getInterfaceName());
		Assert.assertEquals("servicename", wsTypeName.getImplementationName());
		Assert.assertEquals("ws:namespace:porttypename", wsTypeName.getInformationServiceSoaName());

		ServiceImplementationName itfTypeName = new ServiceImplementationName("java:project:interfaceclass=implementationclass");
		Assert.assertEquals(ServiceIdentifierType.JAVA_INTERFACE, itfTypeName.getType());
		Assert.assertEquals("project", itfTypeName.getNamespace());
		Assert.assertEquals("interfaceclass", itfTypeName.getInterfaceName());
		Assert.assertEquals("implementationclass", itfTypeName.getImplementationName());
		Assert.assertEquals("java:project:interfaceclass", itfTypeName.getInformationServiceSoaName());
		
	}
	
}
