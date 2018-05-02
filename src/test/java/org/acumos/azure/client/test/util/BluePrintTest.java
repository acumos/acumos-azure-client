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

import java.util.ArrayList;
import java.util.List;

import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.Node;
import org.acumos.azure.client.utils.OperationSignature;
import org.acumos.azure.client.utils.Orchestrator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluePrintTest {
	
	private static Logger logger = LoggerFactory.getLogger(BluePrintTest.class);
	@Test	
	public void BluePrintTestObjectparameter(){
		logger.info("BluePrintTestObjectparameter Start");
		
        List<OperationSignature> inputs=(List)new ArrayList<OperationSignature>();
		Orchestrator orchestrator=new Orchestrator();
		List<Node> nodes=(List)new ArrayList<Node>();
		
		Blueprint blueprint=new Blueprint();
		blueprint.setName(AzureClientTestConstants.TEST_BLUEPRINT_NAME);
		blueprint.setNodes(nodes);
		blueprint.setVersion(AzureClientTestConstants.TEST_BLUEPRINT_VERSION);
		
		Assert.assertEquals(AzureClientTestConstants.TEST_BLUEPRINT_NAME, blueprint.getName());
		Assert.assertEquals(nodes, blueprint.getNodes());
		Assert.assertEquals(AzureClientTestConstants.TEST_BLUEPRINT_VERSION, blueprint.getVersion());
		
		logger.info("BluePrintTestObjectparameter End");
	}

}
