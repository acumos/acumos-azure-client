package org.acumos.azure.client.test.util;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedList;

import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.ParseJSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseJsonTest {
	
	private static Logger logger = LoggerFactory.getLogger(ParseJsonTest.class);
	@Test	
	public void parseJsonFileTest(){
		logger.info("<---------Start-------parseJsonFileTest-------------->");
		try{
		HashMap<String,String> imageMap=null;
		ParseJSON parse=new ParseJSON();
		imageMap=parse.parseJsonFile("blueprint2.json");
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
		blueprint=parse.jsonFileToObject("blueprint2.json");
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
		linkedList=parse.getSequenceFromJSON("blueprint2.json");
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
		Blueprint blueprint=null;
		blueprint=parse.jsonFileToObjectProbe("blueprint.json");
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
		imageMap=parse.parseJsonFileProbe("blueprint.json");
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
		linkedList=parse.getSequenceFromJSONProbe("blueprint.json");
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
		nodeTypeContainerMap=parse.getNodeTypeContainerMap("blueprint.json");
		assertNotNull(nodeTypeContainerMap);
		}catch(Exception e){
			logger.debug("Exception in nodeTypeContainerMapTest"+e.getMessage());
		}
		logger.info("<---------End-------nodeTypeContainerMapTest-------------->");
	}
	
}
