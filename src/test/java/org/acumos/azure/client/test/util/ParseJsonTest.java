package org.acumos.azure.client.test.util;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedList;

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
		imageMap=parse.parseJsonFile();
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
		blueprint=parse.jsonFileToObject();
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
		linkedList=parse.getSequenceFromJSON();
		assertNotNull(linkedList);
		}catch(Exception e){
			logger.debug("Exception in jsonFileToObjectTest"+e.getMessage());
		}
		logger.info("<---------End-------getSequenceFromJSONTest-------------->");
	}
	
	
}
