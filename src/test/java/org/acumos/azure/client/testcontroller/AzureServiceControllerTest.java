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
			azBean.setAcrName("testAcr");
			azBean.setClient("testclient");
			azBean.setImagetag("newadder1:1");
			azBean.setKey("test key");
			azBean.setRgName("test-OAM");
			azBean.setSolutionId("testSolutionId");
			azBean.setSolutionRevisionId("testRevision");
			azBean.setStorageAccount("teststorage");
			azBean.setSubscriptionKey("testSubscribtionId");
			azBean.setTenant("testtenat");
			azBean.setUserId("testUser");
			
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
				azBean.setAcrName("testAcr");
				azBean.setClient("testclient");
				
				azBean.setKey("test key");
				azBean.setRgName("test-OAM");
				azBean.setSolutionId("testSolutionId");
				azBean.setSolutionRevisionId("testRevision");
				azBean.setStorageAccount("teststorage");
				azBean.setSubscriptionKey("testSubscribtionId");
				azBean.setTenant("testtenat");
				azBean.setUserId("testUser");
				
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
