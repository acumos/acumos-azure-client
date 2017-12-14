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

package org.acumos.azure.client.utils;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6384817901582893495L;

	@JsonProperty("container_name")
	private String container = null;

	@JsonProperty("ip_address")
	private String ipAddress = null;

	@JsonProperty("port")
	private String port = null;

	public DockerInfo(String container, String ipAddress, String port) {
		super();
		this.container = container;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public DockerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "DockerInfo [container=" + container + ", ipAddress=" + ipAddress + ", port=" + port + "]";
	}

}
