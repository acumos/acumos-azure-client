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

package org.acumos.azure.client.service;

import java.io.IOException;
import java.util.LinkedList;

import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfoList;

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.azure.management.Azure;

public interface AzureService {

	/*
	 * Authorize with microsoft Azure active directory
	 */
	Azure authorize(AzureDeployDataObject authObject);
	
}
