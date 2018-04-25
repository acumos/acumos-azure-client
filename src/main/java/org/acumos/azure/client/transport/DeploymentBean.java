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

public class DeploymentBean {
	
	private String azureVMName;
	private String azureVMIP;
	private String containerPort;
	private String containerName;
	private String nodeType;
	private String script;
    private String dataBrokerType;
	
	
	
	
	public String getDataBrokerType() {
		return dataBrokerType;
	}
	public void setDataBrokerType(String dataBrokerType) {
		this.dataBrokerType = dataBrokerType;
	}
	
	
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getAzureVMName() {
		return azureVMName;
	}
	public void setAzureVMName(String azureVMName) {
		this.azureVMName = azureVMName;
	}
	public String getAzureVMIP() {
		return azureVMIP;
	}
	public void setAzureVMIP(String azureVMIP) {
		this.azureVMIP = azureVMIP;
	}
	public String getContainerPort() {
		return containerPort;
	}
	public void setContainerPort(String containerPort) {
		this.containerPort = containerPort;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	

}
