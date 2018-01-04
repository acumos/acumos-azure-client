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

package org.acumos.azure.client.api;

/**
 * Constants class to list all the REST API endpoints
 */
public class APINames {

	// charset
	public static final String CHARSET = "application/json;charset=utf-8";
	public static final String SUCCESS_RESPONSE = "SUCCESS";
	public static final String AUTH_FAILED = "AUTHORIZATION FAILED";
	public static final String FAILED = "FAILED";

	// Service APIs
	public static final String AZURE_AUTH_PUSH_IMAGE = "/azure/authAndpushimage";
	public static final String AZURE_AUTH_PUSH_SINGLE_IMAGE = "/azure/singleImageAzureDeployment";

}
