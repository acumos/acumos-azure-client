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

import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.DockerInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerinfoTest {
	
	
	private static Logger logger = LoggerFactory.getLogger(DockerinfoTest.class);
	@Test	
	public void dockerinfoTestparameter(){
		logger.info("<---------Start-------ComponentTest-------------->");
        DockerInfo dockerInfo=new DockerInfo();
        dockerInfo.setContainer(AzureClientConstants.TEST_COMPONENT_NAME);
        dockerInfo.setIpAddress(AzureClientConstants.TEST_IP_ADDER);
        dockerInfo.setPort(AzureClientConstants.TEST_PORT);
        Assert.assertEquals(AzureClientConstants.TEST_COMPONENT_NAME, dockerInfo.getContainer());
        Assert.assertEquals(AzureClientConstants.TEST_IP_ADDER, dockerInfo.getIpAddress());
        Assert.assertEquals(AzureClientConstants.TEST_PORT, dockerInfo.getPort());

	}

}
