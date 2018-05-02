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
package org.acumos.azure.client.test.transport;

import java.util.HashMap;

import org.acumos.azure.client.transport.SingletonMapClass;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingltonMapTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureDeployDataObjectTest.class);
	
	@Test	
	public void singltonMapTestparameter(){
		logger.info("singltonMapTestparameter Start");
		HashMap hmap=SingletonMapClass.getInstance();
		Assert.assertEquals(hmap, SingletonMapClass.getInstance());
		logger.info("singltonMapTestparameter End");
	}

}
