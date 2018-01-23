package org.acumos.azure.client.test.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.azure.client.testcontroller.AzureServiceControllerTest;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureContainerBeanTest {
	private static Logger logger = LoggerFactory.getLogger(AzureContainerBeanTest.class);
	
	@Test	
	public void testAzureContainerparameter(){
		logger.info("<---------Start-------testAzureContainerparameter-------------->");
		String containerName="Adder1";
		String containerIp="11.11.10.90";	
		String containerPort="8557";
		AzureContainerBean containerBean=new AzureContainerBean();
		containerBean.setContainerIp(containerIp);
		containerBean.setContainerName(containerName);
		containerBean.setContainerPort(containerPort);
		Assert.assertEquals(containerName, containerBean.getContainerName());
		Assert.assertEquals(containerIp, containerBean.getContainerIp());
		Assert.assertEquals(containerPort, containerBean.getContainerPort());
		logger.info("<---------End-------testAzureContainerparameter-------------->");
	}

}
