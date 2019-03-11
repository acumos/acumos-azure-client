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

import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployBean;
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
		azBean.setAcrName(AzureClientTestConstants.TEST_ACR_NAME);
		azBean.setClient(AzureClientTestConstants.TEST_CLIENT);
		azBean.setImagetag(AzureClientTestConstants.TEST_IMAGETAG);
		azBean.setKey(AzureClientTestConstants.TEST_KEY);
		azBean.setRgName(AzureClientTestConstants.TEST_RG_NAME);
		azBean.setSolutionId(AzureClientTestConstants.TEST_SOLUTION_ID);
		azBean.setSolutionRevisionId(AzureClientTestConstants.TEST_SOLUTIONREVISION_ID);
		azBean.setStorageAccount(AzureClientTestConstants.TEST_STORAGE_ACCOUNT);
		azBean.setSubscriptionKey(AzureClientTestConstants.TEST_SUBSCRIPTION_KEY);
		azBean.setTenant(AzureClientTestConstants.TEST_TENANT);
		azBean.setUserId(AzureClientTestConstants.TEST_USER_ID);
		logger.info("azBean "+azBean);
		Assert.assertEquals(AzureClientTestConstants.TEST_CLIENT, azBean.getClient());
		Assert.assertEquals(AzureClientTestConstants.TEST_TENANT, azBean.getTenant());
		Assert.assertEquals(AzureClientTestConstants.TEST_KEY, azBean.getKey());
		Assert.assertEquals(AzureClientTestConstants.TEST_SUBSCRIPTION_KEY, azBean.getSubscriptionKey());
		Assert.assertEquals(AzureClientTestConstants.TEST_RG_NAME, azBean.getRgName());
		Assert.assertEquals(AzureClientTestConstants.TEST_ACR_NAME, azBean.getAcrName());
		Assert.assertEquals(AzureClientTestConstants.TEST_STORAGE_ACCOUNT, azBean.getStorageAccount());
		Assert.assertEquals(AzureClientTestConstants.TEST_IMAGETAG, azBean.getImagetag());
		Assert.assertEquals(AzureClientTestConstants.TEST_SOLUTION_ID, azBean.getSolutionId());
		Assert.assertEquals(AzureClientTestConstants.TEST_USER_ID, azBean.getUserId());
		logger.info("testAzureContainerparameter End");
	}

}
