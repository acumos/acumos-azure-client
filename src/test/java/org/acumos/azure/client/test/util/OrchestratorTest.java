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
package org.acumos.azure.client.test.util;

import org.acumos.azure.client.utils.Orchestrator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorTest {

	private static Logger logger = LoggerFactory.getLogger(OrchestratorTest.class);
	@Test	
	public void OperationSignatureTestparameter(){
		logger.info("OperationSignatureTestparameter Start");
		Orchestrator orch=new Orchestrator();
		orch.setImage(AzureClientTestConstants.TEST_IMAGE);
		orch.setName(AzureClientTestConstants.TEST_CONTAINER_NAME_ADDER);
		orch.setVersion(AzureClientTestConstants.TEST_VERSION);
		logger.info("orch "+orch);
		Assert.assertEquals(AzureClientTestConstants.TEST_CONTAINER_NAME_ADDER, orch.getName());
		Assert.assertEquals(AzureClientTestConstants.TEST_VERSION, orch.getVersion());
		Assert.assertEquals(AzureClientTestConstants.TEST_IMAGE, orch.getImage());
		logger.info("OperationSignatureTestparameter End");
	}
}
