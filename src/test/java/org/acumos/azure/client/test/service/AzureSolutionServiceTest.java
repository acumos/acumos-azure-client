package org.acumos.azure.client.test.service;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.service.impl.AzureSimpleSolution;
import org.acumos.azure.client.testcontroller.AzureServiceControllerTest;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class AzureSolutionServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureServiceControllerTest.class);
	@Mock
	private Environment env;
	
	private MockMvc mockMvc;
	
	@InjectMocks
	AzureSimpleSolution azureSimpleSolution;
	
	@Test
	public void createDeploymentDataTest() {
		try{
			logger.debug("<===Start createDeploymentDataTest========>");
			AzureContainerBean az=new AzureContainerBean();
			az.setContainerIp("11.11.10.90");
			az.setContainerName(null);
			az.setContainerPort("8557");
			UUID uidNumber = UUID.randomUUID();
			String uidNumStr=uidNumber.toString();
			MLPSolutionDeployment mlpDeployment=new MLPSolutionDeployment();
			mlpDeployment.setDeploymentStatusCode("DP");
			mlpDeployment.setDeploymentId(uidNumStr);
			mlpDeployment.setRevisionId("a9e68bc6-f4b4-41c6-ae8e-4e97ec3916a6");
			mlpDeployment.setSolutionId("02eab846-2bd0-4cfe-8470-9fc69fa0d877");
			mlpDeployment.setUserId("0505e537-ce79-4b1f-bf43-68d88933c36");
			//String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			//String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
			//String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
			
			when(azureSimpleSolution.createDeploymentData("http://cognita-dev1-vm01-core:8002/ccds", "ccds_client", "ccds_client", az,
					"02eab846-2bd0-4cfe-8470-9fc69fa0d877", "a9e68bc6-f4b4-41c6-ae8e-4e97ec3916a6", 
					"0505e537-ce79-4b1f-bf43-68d88933c36", uidNumStr, "DP")).thenReturn(mlpDeployment);
			logger.debug("<===End createDeploymentDataTest========>"+mlpDeployment);
		}catch(Exception e){
			logger.debug("Error in junit test ofcreateDeploymentDataTest "+e.getMessage());
		}
		
	}

}
