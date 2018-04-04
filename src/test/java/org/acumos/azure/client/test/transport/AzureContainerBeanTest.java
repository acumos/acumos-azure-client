/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
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
