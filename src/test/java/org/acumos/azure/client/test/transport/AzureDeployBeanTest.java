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
import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureDeployBeanTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureContainerBeanTest.class);
	@Test	
	public void testAzureDeployBeanTestparameter(){
		logger.info("testAzureContainerparameter Start");
		AzureDeployBean azBean=new AzureDeployBean();
		azBean.setAcrName(AzureClientConstants.TEST_ACR_NAME);
		azBean.setClient(AzureClientConstants.TEST_CLIENT);
		azBean.setImagetag(AzureClientConstants.TEST_IMAGETAG);
		azBean.setKey(AzureClientConstants.TEST_KEY);
		azBean.setRgName(AzureClientConstants.TEST_RG_NAME);
		azBean.setSolutionId(AzureClientConstants.TEST_SOLUTION_ID);
		azBean.setSolutionRevisionId(AzureClientConstants.TEST_SOLUTIONREVISION_ID);
		azBean.setStorageAccount(AzureClientConstants.TEST_STORAGE_ACCOUNT);
		azBean.setSubscriptionKey(AzureClientConstants.TEST_SUBSCRIPTION_KEY);
		azBean.setTenant(AzureClientConstants.TEST_TENANT);
		azBean.setUserId(AzureClientConstants.TEST_USER_ID);
		
		Assert.assertEquals(AzureClientConstants.TEST_CLIENT, azBean.getClient());
		Assert.assertEquals(AzureClientConstants.TEST_TENANT, azBean.getTenant());
		Assert.assertEquals(AzureClientConstants.TEST_KEY, azBean.getKey());
		Assert.assertEquals(AzureClientConstants.TEST_SUBSCRIPTION_KEY, azBean.getSubscriptionKey());
		Assert.assertEquals(AzureClientConstants.TEST_RG_NAME, azBean.getRgName());
		Assert.assertEquals(AzureClientConstants.TEST_ACR_NAME, azBean.getAcrName());
		Assert.assertEquals(AzureClientConstants.TEST_STORAGE_ACCOUNT, azBean.getStorageAccount());
		Assert.assertEquals(AzureClientConstants.TEST_IMAGETAG, azBean.getImagetag());
		Assert.assertEquals(AzureClientConstants.TEST_SOLUTION_ID, azBean.getSolutionId());
		Assert.assertEquals(AzureClientConstants.TEST_USER_ID, azBean.getUserId());
		logger.info("testAzureContainerparameter End");
	}

}
