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

import java.util.HashMap;

import org.acumos.azure.client.test.transport.AzureDeployDataObjectTest;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.DockerInfoList;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureBeanTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureBeanTest.class);
	@Test	
	public void AzureBeanTestObjectparameter(){
		logger.info("<---------Start-------AzureBeanTestObjectparameter-------------->");
		 DockerInfoList dockerinfolist = new DockerInfoList();
		 HashMap<String, String> bluePrintMap = new HashMap<String, String>();
		 bluePrintMap.put("test", "test");
		 AzureBean azbean=new AzureBean();
		 azbean.setAzureVMIP(AzureClientConstants.TEST_AZUREVMIP);
		 azbean.setBluePrintIp(AzureClientConstants.TEST_BLUEPRINTIP);
		 azbean.setBluePrintMap(bluePrintMap);
		 azbean.setBluePrintPort(AzureClientConstants.TEST_BLUEPRINTPORT);
		 azbean.setDockerinfolist(dockerinfolist);
		 
	    Assert.assertEquals(dockerinfolist, azbean.getDockerinfolist());
		Assert.assertEquals(bluePrintMap, azbean.getBluePrintMap());
		Assert.assertEquals(AzureClientConstants.TEST_BLUEPRINTIP, azbean.getBluePrintIp());
		Assert.assertEquals(AzureClientConstants.TEST_BLUEPRINTPORT, azbean.getBluePrintPort());
		Assert.assertEquals(AzureClientConstants.TEST_AZUREVMIP, azbean.getAzureVMIP());
		logger.info("<---------End-------AzureBeanTestObjectparameter-------------->");
	}

}
