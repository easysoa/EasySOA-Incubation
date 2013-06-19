package org.easysoa.registry.types.name;

import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.junit.Assert;
import org.junit.Test;

public class NamesTest {

	@Test
	public void testServiceImplName() {
		
		// Service Implementation name
		
		ServiceImplementationName wsTypeName = ServiceImplementationName.fromName("WS:{namespace}porttypename=servicename");
		Assert.assertEquals(ServiceNameType.WEB_SERVICE, wsTypeName.getType());
		Assert.assertEquals("{namespace}porttypename", wsTypeName.getInterfaceName());
		Assert.assertEquals("servicename", wsTypeName.getImplementationName());
		Assert.assertEquals("WS:{namespace}porttypename", wsTypeName.getInformationServiceSoaName());

		ServiceImplementationName itfTypeName = ServiceImplementationName.fromName("java:interfaceclass=implementationclass");
		Assert.assertEquals(ServiceNameType.JAVA_INTERFACE, itfTypeName.getType());
		Assert.assertEquals("interfaceclass", itfTypeName.getInterfaceName());
		Assert.assertEquals("implementationclass", itfTypeName.getImplementationName());
		Assert.assertEquals("java:interfaceclass", itfTypeName.getInformationServiceSoaName());
		
	}
	
}
