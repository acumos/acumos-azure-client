package org.acumos.azure.client.test.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import org.acumos.azure.client.service.impl.AzureServiceImpl;
import org.acumos.azure.client.service.impl.AzureSimpleSolution;
import org.acumos.azure.client.testcontroller.AzureServiceControllerTest;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureSolutionServiceImplTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureSolutionServiceImplTest.class);
	@InjectMocks
	AzureServiceImpl impl;
	
	@Test
	public void getBluePrintNexusTest() {
		try{
			logger.debug("<===Start createDeploymentDataTest========>");
			impl=new AzureServiceImpl();
			String returnStr=null;
			returnStr=impl.getBluePrintNexus("4f87acda-6c25-4434-b562-7d7ebc265c8f", "d451d02a-7c31-4bad-9b46-c418e1b114f8", 
					"http://cognita-dev1-vm01-core:8003/ccds", "ccds_client", "ccds_client", "http://cognita-nexus01:8081/repository/repo_cognita_model_maven/", 
					"cognita_model_rw", "not4you");
			assertNotNull(returnStr);
			/*when(impl.getBluePrintNexus("4f87acda-6c25-4434-b562-7d7ebc265c8f", "d451d02a-7c31-4bad-9b46-c418e1b114f8", 
					"http://cognita-dev1-vm01-core:8003/ccds", "ccds_client", "ccds_client", "http://cognita-nexus01:8081/repository/repo_cognita_model_maven/", 
					"cognita_model_rw", "not4you")).thenReturn("SUCCESS");*/
			
		}catch(Exception e){
			logger.debug("Exception in getBluePrintNexusTest"+e.getMessage());
		}

   }
	@Test
	public void getClientTest() {
		try{
			logger.debug("<===Start getClientTest========>");
			impl=new AzureServiceImpl();
			CommonDataServiceRestClientImpl client=null;
			logger.debug("<===client=======>"+client);
			client=impl.getClient("http://cognita-dev1-vm01-core:8003/ccds","ccds_client","ccds_client");
			assertNotNull(client);
			logger.debug("<===client========>"+client);
			
		}catch(Exception e){
			logger.debug("Exception in getBluePrintNexusTest"+e.getMessage());
		}

   }
	@Test
	public void nexusArtifactClientTest() {
		try{
			logger.debug("<===Start nexusArtifactClientTest========>");
			impl=new AzureServiceImpl();
			/*RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl("http://cognita-nexus01:8081/repository/repo_cognita_model_maven/");
			repositoryLocation.setUsername("cognita_model_rw");
			repositoryLocation.setPassword("not4you");
			NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);*/
			//NexusArtifactClient nexusArtifactClient=null;
			NexusArtifactClient nexusArtifactClient=impl.nexusArtifactClient("http://cognita-nexus01:8081/repository/repo_cognita_model_maven/", "cognita_model_rw", "not4you");
			assertNotNull(nexusArtifactClient);
		}catch(Exception e){
			logger.debug("Exception in nexusArtifactClientTest"+e.getMessage());
		}
	}	
		@Test
		public void iterateImageMapTest() {
			try{
				logger.debug("<===Start iterateImageMapTest========>");
				HashMap<String,String> imageMap=new HashMap<String,String>();
				impl=new AzureServiceImpl();
				imageMap.put("NewAdder11", "cognita-nexus01:8001/newadder1:1");
				ArrayList<String> list=null;
				list=impl.iterateImageMap(imageMap);
				assertNotNull(list);
				logger.debug("<===End iterateImageMapTest========>");
			}catch(Exception e){
				logger.debug("Exception in iterateImageMapTestt"+e.getMessage());
			}
		
   }
}