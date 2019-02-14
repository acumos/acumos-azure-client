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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportBeanTest {

	private static Logger logger = LoggerFactory.getLogger(TransportBeanTest.class);
	@Test	
	public void transportBeanTestparameter(){
		logger.info("AzureBeanTestObjectparameter Start");
		TransportBean tbean=new TransportBean();

		tbean.setLocalHostEnv(AzureClientTestConstants.TEST_OBJ);
        tbean.setSolutionPort(AzureClientTestConstants.TEST_OBJ);
        tbean.setUidNumStr(AzureClientTestConstants.TEST_OBJ);
        tbean.setSleepTimeFirst(AzureClientTestConstants.TEST_OBJ);
        tbean.setBluePrintUser(AzureClientTestConstants.TEST_OBJ);
        tbean.setBluePrintPass(AzureClientTestConstants.TEST_OBJ);
        tbean.setProbUser(AzureClientTestConstants.TEST_OBJ);
        tbean.setProbePass(AzureClientTestConstants.TEST_OBJ);
        tbean.setNexusRegistyUserName(AzureClientTestConstants.TEST_OBJ);
        tbean.setNexusRegistyPd(AzureClientTestConstants.TEST_OBJ);
        tbean.setRegistryUserName(AzureClientTestConstants.TEST_OBJ);
        tbean.setRegistryPd(AzureClientTestConstants.TEST_OBJ);
        tbean.setProbeInternalPort(AzureClientTestConstants.TEST_OBJ);
        tbean.setExposeDataBrokerPort(AzureClientTestConstants.TEST_OBJ);
		tbean.setInternalDataBrokerPort(AzureClientTestConstants.TEST_OBJ);
        tbean.setProbePrintImage(AzureClientTestConstants.TEST_OBJ);
        tbean.setBluePrintImage(AzureClientTestConstants.TEST_OBJ);
        tbean.setDataSourceUrl(AzureClientTestConstants.TEST_OBJ);
        tbean.setDataSourceUserName(AzureClientTestConstants.TEST_OBJ);
        tbean.setDataSourcePd(AzureClientTestConstants.TEST_OBJ);
		tbean.setNexusRegistyName(AzureClientTestConstants.TEST_OBJ);
        tbean.setVmIP(AzureClientTestConstants.TEST_OBJ);
        tbean.setNginxPort(AzureClientTestConstants.TEST_OBJ);
        tbean.setAzureDataFiles(AzureClientTestConstants.TEST_OBJ);
        tbean.setNginxInternalPort(AzureClientTestConstants.TEST_OBJ);
        tbean.setNginxImageName(AzureClientTestConstants.TEST_OBJ);
        tbean.setNginxWebFolder(AzureClientTestConstants.TEST_OBJ);
        tbean.setNginxMapFolder(AzureClientTestConstants.TEST_OBJ);
        tbean.setNexusUrl(AzureClientTestConstants.TEST_OBJ);
        tbean.setNexusUserName(AzureClientTestConstants.TEST_OBJ);
	    tbean.setNexusPd(AzureClientTestConstants.TEST_OBJ);
        tbean.setBluePrintName(AzureClientTestConstants.TEST_OBJ);
		tbean.setProbeName(AzureClientTestConstants.TEST_OBJ);
		
		Map<String,String> protoContainerMap=new HashMap<String,String>();
		protoContainerMap.put(AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		Map<String,String> protoMap=new HashMap<String,String>();
		protoMap.put(AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		ArrayList<String> imageList=new ArrayList<String>();
		imageList.add(AzureClientTestConstants.TEST_OBJ);
		HashMap<String,String> imageMap=new HashMap<String,String>();
		imageMap.put(AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		LinkedList<String> sequenceList=new LinkedList<String>();
		sequenceList.add(AzureClientTestConstants.TEST_OBJ);
		
        tbean.setProtoContainerMap(protoContainerMap);
        tbean.setProtoMap(protoMap);
        tbean.setImageList(imageList);
        tbean.setImageMap(imageMap);
        tbean.setSequenceList(sequenceList);
        
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getLocalHostEnv()); 
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getSolutionPort()); 
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getUidNumStr());
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getSleepTimeFirst()); 
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintUser());
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintPass());
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbUser()); 
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbePass());
         Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyUserName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyPd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyUserName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyPd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getRegistryUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getRegistryPd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbeInternalPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getExposeDataBrokerPort()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getInternalDataBrokerPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbePrintImage()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintImage());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourceUrl()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourceUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourcePd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getVmIP()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxPort()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getAzureDataFiles());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxInternalPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxImageName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxWebFolder());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxMapFolder());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusUrl());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusPd()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbeName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getRegistryUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getRegistryPd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbeInternalPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getExposeDataBrokerPort()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getInternalDataBrokerPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbePrintImage()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintImage());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourceUrl()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourceUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getDataSourcePd());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusRegistyName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getVmIP()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxPort()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getAzureDataFiles());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxInternalPort());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxImageName()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxWebFolder());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNginxMapFolder());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusUrl());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusUserName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getNexusPd()); 
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getBluePrintName());
		 Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, tbean.getProbeName());
		 
		 Assert.assertEquals(protoContainerMap, tbean.getProtoContainerMap());
		 Assert.assertEquals(protoMap, tbean.getProtoMap());
		 Assert.assertEquals(imageList, tbean.getImageList());
		 Assert.assertEquals(imageMap, tbean.getImageMap());
		 Assert.assertEquals(sequenceList, tbean.getSequenceList());
		 
		
        
		logger.info("AzureBeanTestObjectparameter End");
		
	}
}
