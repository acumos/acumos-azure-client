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

import java.io.FileInputStream;
import java.io.InputStream;

import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.acumos.azure.client.transport.AzureKubeTransportBean;
import org.acumos.azure.client.transport.TransportBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureKubeTransportBeanTest {
	
	
	private static Logger logger = LoggerFactory.getLogger(TransportBeanTest.class);
	@Test	
	public void azureKubeTransportBeanTestparameter() throws Exception{
		logger.info("azureKubeTransportBeanTestparameter Start");
		InputStream is=null;
		is=new FileInputStream(AzureClientTestConstants.TEST_BLUEPRINT_FILE);
		AzureKubeTransportBean kubeBean=new AzureKubeTransportBean();
		kubeBean.setAzureVMIP(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setAzureVMName(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setCmnDataPd(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setCmnDataUrl(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setCmnDataUser(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setDockerVMPd(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setDockerVMUserName(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setKubernetesClientUrl(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setNetworkSecurityGroup(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setSleepTimeFirst(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setSolutionZipStream(is);
		kubeBean.setSubnet(AzureClientTestConstants.TEST_OBJ);
		kubeBean.setVnet(AzureClientTestConstants.TEST_OBJ);
		
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getAzureVMIP()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getAzureVMName()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ,kubeBean.getCmnDataPd()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getCmnDataUrl()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getCmnDataUser()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getDockerVMPd()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getDockerVMUserName()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getKubernetesClientUrl()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getNetworkSecurityGroup()); 
		Assert.assertEquals(is, kubeBean.getSolutionZipStream()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getSleepTimeFirst()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getSubnet()); 
		Assert.assertEquals(AzureClientTestConstants.TEST_OBJ, kubeBean.getVnet()); 
		
		logger.info("azureKubeTransportBeanTestparameter End");
	}

}
