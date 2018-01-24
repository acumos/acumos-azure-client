package org.acumos.azure.client.test.util;

import org.acumos.azure.client.utils.AppProperties;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppPropertiesTest {
	
	 
	 
	 private static Logger logger = LoggerFactory.getLogger(AppPropertiesTest.class);
		@Test	
		public void appPropertiesTestparameter(){
			logger.info("<---------Start-------AppPropertiesTest-------------->");
			String host="8557";
			 String port="10.21.13.63";
			 String config="config";
			 AppProperties app=new AppProperties();
			 app.setHost(host);
			 app.setConfig(config);
			 app.setPort(port);
			 Assert.assertEquals(host, app.getHost());
			 Assert.assertEquals(config, app.getConfig());
			 Assert.assertEquals(port, app.getPort());
			logger.info("<---------End -------AppPropertiesTest-------------->");
		}
}
