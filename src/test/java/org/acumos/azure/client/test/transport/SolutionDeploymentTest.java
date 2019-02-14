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

import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolutionDeploymentTest {
	
	private static Logger logger = LoggerFactory.getLogger(SolutionDeploymentTest.class);
	@Test	
	public void solutionDeploymentTestparameter(){
		logger.info("solutionDeploymentTestparameter Start");
		SolutionDeployment dep=new SolutionDeployment();
		
		dep.setClient(AzureClientTestConstants.TEST_OBJ);
		dep.setTenant(AzureClientTestConstants.TEST_OBJ);
		dep.setKey(AzureClientTestConstants.TEST_OBJ);
		dep.setSubscriptionKey(AzureClientTestConstants.TEST_OBJ);
		dep.setRgName(AzureClientTestConstants.TEST_OBJ);
		dep.setAcrName(AzureClientTestConstants.TEST_OBJ);
		dep.setUserId(AzureClientTestConstants.TEST_OBJ);
		dep.setUrlAttribute(AzureClientTestConstants.TEST_OBJ);
		dep.setJsonPosition(AzureClientTestConstants.TEST_OBJ);
		dep.setJsonMapping(AzureClientTestConstants.TEST_OBJ);
		dep.setUsername(AzureClientTestConstants.TEST_OBJ);
		dep.setUserPd(AzureClientTestConstants.TEST_OBJ);
		dep.setHost(AzureClientTestConstants.TEST_OBJ);
		dep.setPort(AzureClientTestConstants.TEST_OBJ);
		dep.setVmHostName(AzureClientTestConstants.TEST_OBJ);
		dep.setVmHostIP(AzureClientTestConstants.TEST_OBJ);
		dep.setSolutionId(AzureClientTestConstants.TEST_OBJ);
		dep.setSolutionRevisionId(AzureClientTestConstants.TEST_OBJ);
		dep.setVmUserName(AzureClientTestConstants.TEST_OBJ);
		dep.setVmUserPd(AzureClientTestConstants.TEST_OBJ);
		
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getClient());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getTenant());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getKey());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getSubscriptionKey() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getRgName());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getAcrName());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getUserId());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getUrlAttribute());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getJsonPosition() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getJsonMapping());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getUsername());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getUserPd());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getHost() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getPort());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getVmHostName());
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getVmHostIP() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getSolutionId() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getSolutionRevisionId() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getVmUserName() );
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,dep.getVmUserPd() );
		
		logger.info("solutionDeploymentTestparameter End");
		
	}

}
