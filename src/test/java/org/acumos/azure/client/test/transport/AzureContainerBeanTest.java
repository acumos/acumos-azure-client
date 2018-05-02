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
import org.acumos.azure.client.utils.AzureClientConstants;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureContainerBeanTest {
	private static Logger logger = LoggerFactory.getLogger(AzureContainerBeanTest.class);
	
	@Test	
	public void testAzureContainerparameter(){
		logger.info("testAzureContainerparameter Start");
		
		AzureContainerBean containerBean=new AzureContainerBean();
		containerBean.setContainerIp(AzureClientConstants.TEST_IP_ADDER);
		containerBean.setContainerName(AzureClientConstants.TEST_CONTAINER_NAME_ADDER);
		containerBean.setContainerPort(AzureClientConstants.TEST_PORT_ADDER);
		Assert.assertEquals(AzureClientConstants.TEST_CONTAINER_NAME_ADDER, containerBean.getContainerName());
		Assert.assertEquals(AzureClientConstants.TEST_IP_ADDER, containerBean.getContainerIp());
		Assert.assertEquals(AzureClientConstants.TEST_PORT_ADDER, containerBean.getContainerPort());
		logger.info("testAzureContainerparameter End");
	}

}
