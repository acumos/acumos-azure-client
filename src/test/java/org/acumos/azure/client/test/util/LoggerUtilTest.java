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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.LoggerUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtilTest {
	private static Logger logger = LoggerFactory.getLogger(LoggerUtilTest.class);
	@Test	
	public void printSingleSolutionImplTest(){
		logger.info("AzureBeanTestObjectparameter Start");
		String output=null;
		LoggerUtil util=new LoggerUtil();
		AzureDeployDataObject deployDataObject=new AzureDeployDataObject();
		ArrayList<String> list=new ArrayList<String>();
		list.add(AzureClientTestConstants.TEST_OBJ);
		output=util.printSingleSolutionImpl(deployDataObject, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ,list, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		assertNotNull(output);
		logger.info("AzureBeanTestObjectparameter End");
	}
	
	@Test	
	public void printExistingVMDetailsTest() {
		logger.info("printExistingVMDetailsTest Start");
		String output=null;
		LoggerUtil util=new LoggerUtil();
		TransportBean tbean=new TransportBean();
		SolutionDeployment bean=new SolutionDeployment();
		output=util.printExistingVMDetails(bean, tbean);
		assertNotNull(output);
		logger.info("printExistingVMDetailsTest end");
	}
	@Test	
	public void printCompositeSolutionDetailsTest() {
		logger.info("printCompositeSolutionDetailsTest Start");
		LoggerUtil util=new LoggerUtil();
		String output=null;
		AzureDeployDataObject authObject=new AzureDeployDataObject();
		output=util.printCompositeSolutionDetails(AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, authObject);
		assertNotNull(output);
		logger.info("printCompositeSolutionDetailsTest End");
	}
	@Test
	public void printCompositeSolutionImplDetailsTest() {
		logger.info("printCompositeSolutionDetailsTest Start");
		LoggerUtil util=new LoggerUtil();
		String output=null;
		AzureDeployDataObject deployDataObject=new AzureDeployDataObject();
		ArrayList<String> list=new ArrayList<String>();
		list.add(AzureClientTestConstants.TEST_OBJ);
		LinkedList<String> sequenceList=new LinkedList<String>(); 
		sequenceList.add(AzureClientTestConstants.TEST_OBJ);
		HashMap<String,String> imageMap=new HashMap<String,String>();
		imageMap.put(AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		TransportBean tbean=new TransportBean();
		HashMap<String,DeploymentBean> nodeTypeContainerMap=new HashMap<String,DeploymentBean>();
		output=util.printCompositeSolutionImplDetails(deployDataObject, AzureClientTestConstants.TEST_OBJ, list, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, sequenceList, imageMap,
				AzureClientTestConstants.TEST_OBJ, nodeTypeContainerMap,AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, tbean);
		assertNotNull(output);
		logger.info("printCompositeSolutionDetailsTest End");
	}

}
