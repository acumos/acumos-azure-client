package org.acumos.azure.client.service.test.impl;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.acumos.azure.client.service.impl.AzureServiceImpl;
import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureServiceImplTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureServiceImplTest.class);
	@Test	
	public void iterateImageMapTest()throws Exception{
		logger.info("getRepositryNameTest Start");
		HashMap<String,String> hmap=new HashMap<String,String>();
		AzureServiceImpl impl=new AzureServiceImpl();
		ArrayList<String> list=null;
		hmap.put(AzureClientTestConstants.TEST_KEY, AzureClientTestConstants.TEST_OBJ);
		list=impl.iterateImageMap(hmap);
		assertNotNull(list);
		logger.info("getRepositryNameTest End");
	}
	@Test	
	public void getSequenceTest()throws Exception{
		logger.info("getSequenceTest Start");
		HashMap<String,String> hmap=new HashMap<String,String>();
		AzureServiceImpl impl=new AzureServiceImpl();
		LinkedList<String> sequenceList=null;
		hmap.put(AzureClientTestConstants.TEST_KEY, AzureClientTestConstants.TEST_OBJ);
		sequenceList=impl.getSequence(hmap);
		assertNotNull(sequenceList);
		logger.info("getSequenceTest End");
	}

}
