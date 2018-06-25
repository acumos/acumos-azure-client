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
package org.acumos.azure.client.transport;

import java.util.Map;

public class TransportBean {
	
	private String nexusUrl;
	private String nexusUserName;
	private String nexusPassword;
	private Map<String,String> protoContainerMap;
	private Map<String,String> protoMap;
	private String nginxWebFolder;
	private String nginxMapFolder;
	private String nginxImageName;
	private String nginxInternalPort;
	private String azureDataFiles;
	
	
   
	public String getAzureDataFiles() {
		return azureDataFiles;
	}

	public void setAzureDataFiles(String azureDataFiles) {
		this.azureDataFiles = azureDataFiles;
	}

	public String getNginxInternalPort() {
		return nginxInternalPort;
	}

	public void setNginxInternalPort(String nginxInternalPort) {
		this.nginxInternalPort = nginxInternalPort;
	}

	public String getNginxImageName() {
		return nginxImageName;
	}

	public void setNginxImageName(String nginxImageName) {
		this.nginxImageName = nginxImageName;
	}

	public String getNginxWebFolder() {
		return nginxWebFolder;
	}

	public void setNginxWebFolder(String nginxWebFolder) {
		this.nginxWebFolder = nginxWebFolder;
	}

	public String getNginxMapFolder() {
		return nginxMapFolder;
	}

	public void setNginxMapFolder(String nginxMapFolder) {
		this.nginxMapFolder = nginxMapFolder;
	}

	public String getNexusUrl() {
		return nexusUrl;
	}

	public void setNexusUrl(String nexusUrl) {
		this.nexusUrl = nexusUrl;
	}

	public String getNexusUserName() {
		return nexusUserName;
	}

	public void setNexusUserName(String nexusUserName) {
		this.nexusUserName = nexusUserName;
	}

	public String getNexusPassword() {
		return nexusPassword;
	}

	public void setNexusPassword(String nexusPassword) {
		this.nexusPassword = nexusPassword;
	}

	public Map<String, String> getProtoContainerMap() {
		return protoContainerMap;
	}

	public void setProtoContainerMap(Map<String, String> protoContainerMap) {
		this.protoContainerMap = protoContainerMap;
	}

	public Map<String, String> getProtoMap() {
		return protoMap;
	}

	public void setProtoMap(Map<String, String> protoMap) {
		this.protoMap = protoMap;
	}
	
	

}
