package org.acumos.azure.client.test.util;



import org.acumos.azure.client.utils.Component;
import org.acumos.azure.client.utils.OperationSignature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentTest {
	
	private static Logger logger = LoggerFactory.getLogger(ComponentTest.class);
	@Test	
	public void ComponentTestObjectparameter(){
		logger.info("<---------Start-------ComponentTest-------------->");
		Component component=new Component();
		String name = "Component";
		OperationSignature operationSignature = new OperationSignature();
		component.setName(name);
		component.setOperationSignature(operationSignature);
		Assert.assertEquals(name, component.getName());
		Assert.assertEquals(operationSignature, component.getOperationSignature());
		logger.info("<---------End-------ComponentTest-------------->");
	}

}
