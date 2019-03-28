package org.acumos.azure.client.test.logging;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.acumos.azure.client.logging.LogConfig;
import org.acumos.azure.client.logging.ACUMOSLogConstants.MDCs;
import org.acumos.azure.client.test.util.AzureClientTestConstants;
import org.acumos.azure.client.test.util.ParseJsonTest;
import org.acumos.azure.client.utils.ParseJSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogConfigTest {
	
	private static Logger logger = LoggerFactory.getLogger(LogConfigTest.class);
	@Test	
	public void setEnteringMDCsTest()throws Exception{
		logger.info("setEnteringMDCsTest Start");
		LogConfig.setEnteringMDCs(AzureClientTestConstants.TEST_CLIENT, AzureClientTestConstants.TEST_CLIENT);
		String requestId=MDC.get(MDCs.REQUEST_ID);
		assertNotNull(MDC.get(MDCs.REQUEST_ID));
		logger.info("setEnteringMDCsTest End");
	}

}
