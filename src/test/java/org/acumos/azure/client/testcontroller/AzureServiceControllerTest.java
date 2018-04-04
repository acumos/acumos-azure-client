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
package org.acumos.azure.client.testcontroller;

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.AppProperties;
import org.acumos.azure.client.utils.JsonRequest;
import org.acumos.azure.client.utils.JsonResponse;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Assert;
import org.junit.Test;


@RunWith(MockitoJUnitRunner.class)
public class AzureServiceControllerTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureServiceControllerTest.class);
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	private AppProperties app;

	@Autowired
	public void setApp(AppProperties app) {
		this.app = app;
	}

	@Mock
	private Environment env;
	
	private MockMvc mockMvc;
	
	@InjectMocks
	AzureServiceController azureController;
	
	/*@Mock
	AzureServiceController azureController=new AzureServiceController();*/
	
	@Test
	public void singleImageAzureDeploymentTest() {
		String result = "";
		try {
			AzureDeployBean azBean=new AzureDeployBean();
			azBean.setAcrName("CognitaE6Reg");
			azBean.setClient("c83923c9-73c4-43e2-a47d-2ab700ac9353");
			azBean.setImagetag("cognita-nexus01:8001/newadder1:1");
			azBean.setKey("eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO/dpvw6CXwpc=");
			azBean.setRgName("Cognita-OAM");
			azBean.setSolutionId("02eab846-2bd0-4cfe-8470-9fc69fa0d877");
			azBean.setSolutionRevisionId("a9e68bc6-f4b4-41c6-ae8e-4e97ec3916a6");
			azBean.setStorageAccount("cognitae6storage");
			azBean.setSubscriptionKey("81f6511d-7cc6-48f1-a0d1-d30f65fdbe1a");
			azBean.setTenant("412141bb-9e53-4aed-8468-6868c832e618");
			azBean.setUserId("0505e537-ce79-4b1f-bf43-68d88933c369");
			
			response.setStatus(400);
			logger.info("====="+response.getStatus());
			when(azureController.singleImageAzureDeployment(request,azBean,response)).thenReturn("{\"UIDNumber\":\"\",\"status\":\"SUCCESS\"}");
			logger.info("==Test Status ==="+response.getStatus());
			Assert.assertEquals(200, response.getStatus());
		}catch (Exception e) {
			logger.info("failed tot execute the above test case of singleImageAzureDeploymentTest"+e.getMessage());
		}
	}	
		@Test
		public void compositeImageAzureDeploymentTest() {
			String result = "";
			try {
				AzureDeployDataObject azBean=new AzureDeployDataObject();
				azBean.setAcrName("CognitaE6Reg");
				azBean.setClient("c83923c9-73c4-43e2-a47d-2ab700ac9353");
				//azBean.setImagetag("cognita-nexus01:8001/newadder1:1");
				azBean.setKey("eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO/dpvw6CXwpc=");
				azBean.setRgName("Cognita-OAM");
				azBean.setSolutionId("4f87acda-6c25-4434-b562-7d7ebc265c8f");
				azBean.setSolutionRevisionId("d451d02a-7c31-4bad-9b46-c418e1b114f8");
				azBean.setStorageAccount("cognitae6storage");
				azBean.setSubscriptionKey("81f6511d-7cc6-48f1-a0d1-d30f65fdbe1a");
				azBean.setTenant("412141bb-9e53-4aed-8468-6868c832e618");
				azBean.setUserId("7cd47ca4-1c5d-4cdc-909c-f7c17367b4d4");
				
				response.setStatus(400);
				logger.info("====="+response.getStatus());
				when(azureController.compositeSolutionAzureDeployment(request,azBean,response)).thenReturn("{\"UIDNumber\":\"\",\"status\":\"SUCCESS\"}");
				logger.info("==Test Status ==="+response.getStatus());
				//Assert.assertEquals(200, response.getStatus());
			}catch (Exception e) {
				logger.info("failed tot execute the above test case of singleImageAzureDeploymentTest"+e.getMessage());
			}
			
		
	}
		
		@Test
		public void getUIDDetailsTest() {
			String result = "";
			/*try {
				
			}catch (Exception e) {
				logger.info("failed tot execute the above test case of singleImageAzureDeploymentTest"+e.getMessage());
			}*/
			}

}
