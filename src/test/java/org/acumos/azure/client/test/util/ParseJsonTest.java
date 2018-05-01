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

import java.util.HashMap;
import java.util.LinkedList;

import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.ParseJSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJsonTest {
	
	private static Logger logger = LoggerFactory.getLogger(ParseJsonTest.class);
	@Test	
	public void parseJsonFileTest(){
		logger.info("<---------Start-------parseJsonFileTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		imageMap=parse.parseJsonFile(AzureClientConstants.TEST_BLUEPRINT_OLD_FILE);
		assertNotNull(imageMap);
		}catch(Exception e){
			logger.debug("Exception in parseJsonFileTest"+e.getMessage());
		}
		logger.info("<---------End-------parseJsonFileTest-------------->");
	}
   
	@Test	
	public void jsonFileToObjectTest(){
		logger.info("<---------Start-------parseJsonFileTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		Blueprint blueprint=null;
		DataBrokerBean dataBrokerbean=parse.getDataBrokerContainer(AzureClientConstants.TEST_BLUEPRINT_OLD_FILE);
		blueprint=parse.jsonFileToObject(AzureClientConstants.TEST_BLUEPRINT_OLD_FILE,dataBrokerbean);
		assertNotNull(blueprint);
		}catch(Exception e){
			logger.debug("Exception in jsonFileToObjectTest"+e.getMessage());
		}
		logger.info("<---------End-------parseJsonFileTest-------------->");
	}
	
	@Test	
	public void getSequenceFromJSONTest(){
		logger.info("<---------Start-------getSequenceFromJSONTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		
		LinkedList<String> linkedList=null;
		linkedList=parse.getSequenceFromJSON(AzureClientConstants.TEST_BLUEPRINT_OLD_FILE);
		assertNotNull(linkedList);
		}catch(Exception e){
			logger.debug("Exception in jsonFileToObjectTest"+e.getMessage());
		}
		logger.info("<---------End-------getSequenceFromJSONTest-------------->");
	}
	
	@Test	
	public void jsonFileToObjectProbeTest(){
		logger.info("<---------Start-------jsonFileToObjectProbeTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		ObjectMapper mapper = new ObjectMapper();
		Blueprint blueprint=null;
		DataBrokerBean dataBrokerbean=parse.getDataBrokerContainer(AzureClientConstants.TEST_BLUEPRINT_FILE);
		blueprint=parse.jsonFileToObjectProbe(AzureClientConstants.TEST_BLUEPRINT_FILE,dataBrokerbean);
		String blueprintJson=mapper.writeValueAsString(blueprint); 
		logger.debug("<----blueprintJson---------->"+blueprintJson);
		assertNotNull(blueprint);
		}catch(Exception e){
			logger.debug("Exception in jsonFileToObjectProbeTest"+e.getMessage());
		}
		logger.info("<---------End-------jsonFileToObjectProbeTest-------------->");
	}
	
	@Test
	public void parseJsonFileProbeTest(){
		logger.info("<---------Start-------parseJsonFileProbeTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		imageMap=parse.parseJsonFileProbe(AzureClientConstants.TEST_BLUEPRINT_FILE);
		assertNotNull(imageMap);
		}catch(Exception e){
			logger.debug("Exception in parseJsonFileProbeTest"+e.getMessage());
		}
		logger.info("<---------End-------parseJsonFileProbeTest-------------->");
	}
	
	@Test	
	public void getSequenceFromJSONProbeTest(){
		logger.info("<---------Start-------getSequenceFromJSONProbeTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		
		LinkedList<String> linkedList=null;
		linkedList=parse.getSequenceFromJSONProbe(AzureClientConstants.TEST_BLUEPRINT_FILE);
		assertNotNull(linkedList);
		}catch(Exception e){
			logger.debug("Exception in getSequenceFromJSONProbeTest"+e.getMessage());
		}
		logger.info("<---------End-------getSequenceFromJSONProbeTest-------------->");
	}
	
	@Test
	public void nodeTypeContainerMapTest(){
		logger.info("<---------Start-------nodeTypeContainerMapTest-------------->");
		try{
		HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
		ParseJSON parse=new ParseJSON();
		nodeTypeContainerMap=parse.getNodeTypeContainerMap(AzureClientConstants.TEST_BLUEPRINT_FILE);
		assertNotNull(nodeTypeContainerMap);
		}catch(Exception e){
			logger.debug("Exception in nodeTypeContainerMapTest"+e.getMessage());
		}
		logger.info("<---------End-------nodeTypeContainerMapTest-------------->");
	}
	
	@Test
	public void getDataBrokerContainerTest(){
		logger.info("<---------Start-------getDataBrokerContainerTest-------------->");
		try{
		DataBrokerBean	dataBrokerbean=null;	
		HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
		ParseJSON parse=new ParseJSON();
		ObjectMapper mapper = new ObjectMapper();
		dataBrokerbean=parse.getDataBrokerContainer(AzureClientConstants.TEST_BLUEPRINT_FILE);
		String dataBrokerbeanjson=mapper.writeValueAsString(dataBrokerbean); 
		System.out.println("<----dataBrokerbeanjson---------->"+dataBrokerbeanjson);
		assertNotNull(dataBrokerbean);
		}catch(Exception e){
			logger.debug("Exception in nodeTypeContainerMapTest"+e.getMessage());
		}
		logger.info("<---------End-------getDataBrokerContainerTest-------------->");
	}
	
}
