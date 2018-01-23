package org.acumos.azure.client.test.transport;

import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureDeployDataObjectTest {
	
	
	private static Logger logger = LoggerFactory.getLogger(AzureDeployDataObjectTest.class);
	@Test	
	public void testAzureDeployDataObjectparameter(){
		logger.info("<---------Start-------testAzureDeployDataObjectparameter-------------->");
		 String client="c83923c9-73c4-43e2-a47d-2ab700ac";
		 String tenant="412141bb-9e53-4aed-8468-6868c832e618";
		 String key="eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO";
		 String subscriptionKey="81f6511d-7cc6-48f1-a0d1-d30f65fdbe1";
		 String rgName="Cognita";
		 String acrName="Cognita";
		 String storageAccount="cognitae6";
		 //String imagetag="newadder";
		 String solutionId="02eab846-2bd0-4cfe-8470-9fc69";
		 String solutionRevisionId="a9e68bc6-f4b4-41c6-ae8e";
		 String userId="0505e537-ce79-4b1f-bf43";
		 AzureDeployDataObject azBean=new AzureDeployDataObject();
		azBean.setAcrName("Cognita");
		azBean.setClient("c83923c9-73c4-43e2-a47d-2ab700ac");
		//azBean.setImagetag("newadder");
		azBean.setKey("eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO");
		azBean.setRgName("Cognita");
		azBean.setSolutionId("02eab846-2bd0-4cfe-8470-9fc69");
		azBean.setSolutionRevisionId("a9e68bc6-f4b4-41c6-ae8e-");
		azBean.setStorageAccount("cognitae6");
		azBean.setSubscriptionKey("81f6511d-7cc6-48f1-a0d1-d30f65fdbe1");
		azBean.setTenant("412141bb-9e53-4aed-8468-6868c832e618");
		azBean.setUserId("0505e537-ce79-4b1f-bf43");
		
		Assert.assertEquals(client, azBean.getClient());
		Assert.assertEquals(tenant, azBean.getTenant());
		Assert.assertEquals(key, azBean.getKey());
		Assert.assertEquals(subscriptionKey, azBean.getSubscriptionKey());
		Assert.assertEquals(rgName, azBean.getRgName());
		Assert.assertEquals(acrName, azBean.getAcrName());
		Assert.assertEquals(storageAccount, azBean.getStorageAccount());
		//Assert.assertEquals(imagetag, azBean.getImagetag());
		Assert.assertEquals(solutionId, azBean.getSolutionId());
		Assert.assertEquals(userId, azBean.getUserId());
	}

}
