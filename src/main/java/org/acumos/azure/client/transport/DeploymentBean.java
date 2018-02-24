package org.acumos.azure.client.transport;

public class DeploymentBean {
	
	private String azureVMName;
	private String azureVMIP;
	private String containerPort;
	private String containerName;
	private String nodeType;
	private String script;
	
	
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
