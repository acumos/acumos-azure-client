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

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.cds.domain.MLPNotification;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureCommonUtilTest {
	
	private static Logger logger = LoggerFactory.getLogger(BluePrintTest.class);
	@Test	
	public void getRepositryNameTest()throws Exception{
		logger.info("getRepositryNameTest Start");
		String repo=null;
		AzureCommonUtil cutil=new AzureCommonUtil();
		repo=cutil.getRepositoryName(AzureClientTestConstants.TEST_NEXUS_IMAGE);
		assertNotNull(repo);
		logger.info("getRepositryNameTest End"+repo);
	}
	
	@Test	
	public void getFileDetailsTest()throws Exception{
		logger.info("readBytesFromFileTest Start");
		AzureCommonUtil cutil=new AzureCommonUtil();
		String fileOutput=null;
		fileOutput=cutil.getFileDetails(AzureClientTestConstants.TEST_BLUEPRINT_FILE);
		assertNotNull(fileOutput);
		logger.info("getFileDetailsTest End"+fileOutput);
	}
	
	@Test	
	public void getTagFromImageTest()throws Exception{
		logger.info("getTagFromImageTest Start");
		AzureCommonUtil cutil=new AzureCommonUtil();
		String fileOutput=null;
		fileOutput=cutil.getTagFromImage(AzureClientTestConstants.TEST_NEXUS_IMAGE);
		assertNotNull(fileOutput);
		logger.info("getTagFromImageTest End"+fileOutput);
	}
	@Test	
	public void getRandomPasswordTest()throws Exception{
		logger.info("getRandomPasswordTest Start");
		AzureCommonUtil cutil=new AzureCommonUtil();
		StringBuffer pass=null;
		pass=cutil.getRandomPassword(10);
		assertNotNull(pass);
		logger.info("getRandomPasswordTest Start");
	}
	@Test	
	public void getRepositryStatusTest()throws Exception{
		logger.info("getRepositryStatusTest Start");
		AzureCommonUtil cutil=new AzureCommonUtil();
		boolean check;
		check=cutil.getRepositryStatus(AzureClientTestConstants.TEST_NEXUS_IMAGE, AzureClientTestConstants.TEST_REPO);
		assertNotNull(check);
		logger.info("getRepositryStatusTest Start");
	}
	@Test
	public void convertToAzureDeployDataObjectTest()throws Exception{
		logger.info("convertToAzureDeployDataObjectTest Start");
		AzureCommonUtil cutil=new AzureCommonUtil();
		AzureKubeBean kubeBean=new AzureKubeBean();
		AzureDeployDataObject authObject=null;
		kubeBean.setAcrName(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setClient(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setKey(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setRgName(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setSolutionId(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setSolutionRevisionId(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setStorageAccount(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setSubscriptionKey(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setTenant(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setUserId(AzureClientTestConstants.TEST_OBJ);
		authObject=cutil.convertToAzureDeployDataObject(kubeBean);
		assertNotNull(authObject);
		logger.info("convertToAzureDeployDataObjectTest End");
		
	}
	@Test
	public void convertToMLNotificationTest() {
		logger.info("convertToMLNotificationTest Start");
		MLNotification mlNotification =null;
		AzureCommonUtil cutil=new AzureCommonUtil();
		MLPNotification mlpNotification=new MLPNotification();
		mlpNotification.setNotificationId(AzureClientTestConstants.TEST_OBJ);
		mlpNotification.setTitle(AzureClientTestConstants.TEST_OBJ);
		mlpNotification.setMessage(AzureClientTestConstants.TEST_OBJ);
		mlpNotification.setUrl(AzureClientTestConstants.TEST_OBJ);
		Instant startDate = Instant.now();
		Instant endDate = startDate.plus(Period.ofDays(365));
		mlpNotification.setStart(startDate);
		mlpNotification.setEnd(endDate);
		mlNotification=cutil.convertToMLNotification(mlpNotification);
		assertNotNull(mlNotification);
		logger.info("convertToMLNotificationTest End"+mlNotification.toString());
	}
	
	@Test	
	public void getDataBrokerPortTest()throws Exception{
		logger.info("getDataBrokerTunnelCSVTest Start");
		String dataBrokerPort=null;
		AzureCommonUtil cutil=new AzureCommonUtil();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		DeploymentBean db=new DeploymentBean();
		db.setDataBrokerType("");
		db.setNodeType("DataBroker");
		db.setContainerPort("8000");
		deploymentList.add(db);
		dataBrokerPort=cutil.getDataBrokerPort(deploymentList, "DataBroker");
		assertNotNull(dataBrokerPort);
		logger.info("getDataBrokerTunnelCSVTest End"+dataBrokerPort);
	}
	
	
	@Test	
	public void getDataBrokerPortCSVTest()throws Exception{
		logger.info("getDataBrokerPortCSVTest Start");
		String dataBrokerPort=null;
		AzureCommonUtil cutil=new AzureCommonUtil();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		DeploymentBean db=new DeploymentBean();
		db.setDataBrokerType(AzureClientConstants.DATA_BROKER_CSV_FILE);
		db.setNodeType("DataBroker");
		db.setContainerPort("8000");
		deploymentList.add(db);
		dataBrokerPort=cutil.getDataBrokerPortCSV(deploymentList, "DataBroker");
		assertNotNull(dataBrokerPort);
		logger.info("getDataBrokerPortCSVTest End"+dataBrokerPort);
	}
	
	@Test	
	public void getDataBrokerScriptTest(){
		logger.info("getDataBrokerScriptTest Start");
		String dataBrokerPort=null;
		AzureCommonUtil cutil=new AzureCommonUtil();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		DeploymentBean db=new DeploymentBean();
		db.setDataBrokerType(AzureClientConstants.DATA_BROKER_CSV_FILE);
		db.setNodeType("DataBroker");
		db.setContainerPort("8000");
		db.setScript(AzureClientTestConstants.TEST_OBJ);
		deploymentList.add(db);
		dataBrokerPort=cutil.getDataBrokerScript(deploymentList, "DataBroker");
		assertNotNull(dataBrokerPort);
		logger.info("getDataBrokerScriptTest End"+dataBrokerPort);
	}
	@Test	
	public void setTransportValuesTest() {
		logger.info("setTransportValuesTest Start");
		TransportBean tbean=new TransportBean();
		AzureCommonUtil cutil=new AzureCommonUtil();
		tbean=cutil.setTransportValues(tbean, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ,AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ,
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ, 
				AzureClientTestConstants.TEST_OBJ, AzureClientTestConstants.TEST_OBJ);
		assertNotNull(tbean.getUidNumStr());
		logger.info("setTransportValuesTest Start");
	}

}
