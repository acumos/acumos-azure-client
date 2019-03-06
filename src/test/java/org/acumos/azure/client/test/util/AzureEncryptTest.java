package org.acumos.azure.client.test.util;

import static org.junit.Assert.assertNotNull;

import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.AzureEncrypt;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureEncryptTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureEncryptTest.class);
	@Test	
	public void encryptTest()throws Exception{
		logger.info("encryptTest Start");
		AzureEncrypt azureEncrypt=new AzureEncrypt();
		String finalOutput=null;
		finalOutput=azureEncrypt.encrypt(AzureClientTestConstants.TEST_OBJ+AzureClientTestConstants.TEST_OBJ);
		assertNotNull(finalOutput);
		logger.info("encryptTest End"+finalOutput);
	}

}
