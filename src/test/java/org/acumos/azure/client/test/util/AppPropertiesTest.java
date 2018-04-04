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
