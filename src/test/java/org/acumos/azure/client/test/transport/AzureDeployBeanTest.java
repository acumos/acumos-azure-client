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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureDeployBeanTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureContainerBeanTest.class);
	@Test	
	public void testAzureDeployBeanTestparameter(){
		logger.info("<---------Start-------testAzureContainerparameter-------------->");
		 String client="testclient";
		 String tenant="testtenant";
		 String key="testkey";
		 String subscriptionKey="testsubscriptionKey";
		 String rgName="testrg";
		 String acrName="testacr";
		 String storageAccount="teste6";
		 String imagetag="newadder";
		 String solutionId="02eab846-2bd0-4cfe-8470-9fc69";
		 String solutionRevisionId="a9e68bc6-f4b4-41c6-ae8e";
		 String userId="0505e537-ce79-4b1f-bf43";
		AzureDeployBean azBean=new AzureDeployBean();
		azBean.setAcrName("testacr");
		azBean.setClient("testclient");
		azBean.setImagetag("newadder");
		azBean.setKey("testkey");
		azBean.setRgName("testrg");
		azBean.setSolutionId("02eab846-2bd0-4cfe-8470-9fc69");
		azBean.setSolutionRevisionId("a9e68bc6-f4b4-41c6-ae8e-");
		azBean.setStorageAccount("teste6");
		azBean.setSubscriptionKey("testsubscriptionKey");
		azBean.setTenant("testtenant");
		azBean.setUserId("0505e537-ce79-4b1f-bf43");
		
		Assert.assertEquals(client, azBean.getClient());
		Assert.assertEquals(tenant, azBean.getTenant());
		Assert.assertEquals(key, azBean.getKey());
		Assert.assertEquals(subscriptionKey, azBean.getSubscriptionKey());
		Assert.assertEquals(rgName, azBean.getRgName());
		Assert.assertEquals(acrName, azBean.getAcrName());
		Assert.assertEquals(storageAccount, azBean.getStorageAccount());
		Assert.assertEquals(imagetag, azBean.getImagetag());
		Assert.assertEquals(solutionId, azBean.getSolutionId());
		Assert.assertEquals(userId, azBean.getUserId());
	}

}
