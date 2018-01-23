package org.acumos.azure.client.test.transport;

import java.util.HashMap;

import org.acumos.azure.client.transport.SingletonMapClass;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingltonMapTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureDeployDataObjectTest.class);
	
	@Test	
	public void singltonMapTestparameter(){
		logger.info("<---------Start-------singltonMapTestparameter-------------->");
		HashMap hmap=SingletonMapClass.getInstance();
		Assert.assertEquals(hmap, SingletonMapClass.getInstance());
		logger.info("<---------End-------singltonMapTestparameter-------------->");
	}

}
