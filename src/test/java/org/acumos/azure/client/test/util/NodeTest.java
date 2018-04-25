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

import org.acumos.azure.client.utils.Component;
import org.acumos.azure.client.utils.Node;
import org.acumos.azure.client.utils.OperationSignature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class NodeTest {
	
	private static Logger logger = LoggerFactory.getLogger(NodeTest.class);
	@Test	
	public void nodeTestObjectparameter(){
		logger.info("<---------Start-------nodeTestObjectparameter-------------->");
		
		OperationSignature operationSignature = new OperationSignature();
		String containerName = "Adder1";
	    String image = "newadder1:1";
	    Component component=new Component();
	    component.setName("testName");
		component.setOperationSignature(operationSignature);
	   
	    List<Component> dependsOn = new ArrayList<Component>();
	    dependsOn.add(component);
	    Node node =new Node();
	    node.setContainerName(containerName);
	    //node.setDependsOn(dependsOn);
	    node.setImage(image);
	    Assert.assertEquals(containerName, node.getContainerName());
	    Assert.assertEquals(image, node.getImage());
	    //Assert.assertEquals(dependsOn, node.getDependsOn());
		
	    logger.info("<---------End-------nodeTestObjectparameter-------------->");
   
	}
}
