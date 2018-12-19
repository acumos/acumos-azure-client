package org.acumos.azure.client.transport;

import java.io.InputStream;

public class AzureKubeTransportBean {
	
	private String kubernetesClientUrl;
	private String subnet;
	private String vnet;
	private String networkSecurityGroup;
	private String dockerVMUserName;
	private String dockerVMPd;
	private String sleepTimeFirst;
	private String AzureVMIP;
	private String AzureVMName;
	private InputStream solutionZipStream;
	
	
	
	public InputStream getSolutionZipStream() {
		return solutionZipStream;
	}
	public void setSolutionZipStream(InputStream solutionZipStream) {
		this.solutionZipStream = solutionZipStream;
	}
	public String getAzureVMIP() {
		return AzureVMIP;
	}
	public void setAzureVMIP(String azureVMIP) {
		AzureVMIP = azureVMIP;
	}
	public String getAzureVMName() {
		return AzureVMName;
	}
	public void setAzureVMName(String azureVMName) {
		AzureVMName = azureVMName;
	}
	public String getKubernetesClientUrl() {
		return kubernetesClientUrl;
	}
	public void setKubernetesClientUrl(String kubernetesClientUrl) {
		this.kubernetesClientUrl = kubernetesClientUrl;
	}
	public String getSubnet() {
		return subnet;
	}
	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}
	public String getVnet() {
		return vnet;
	}
	public void setVnet(String vnet) {
		this.vnet = vnet;
	}
	public String getNetworkSecurityGroup() {
		return networkSecurityGroup;
	}
	public void setNetworkSecurityGroup(String networkSecurityGroup) {
		this.networkSecurityGroup = networkSecurityGroup;
	}
	public String getDockerVMUserName() {
		return dockerVMUserName;
	}
	public void setDockerVMUserName(String dockerVMUserName) {
		this.dockerVMUserName = dockerVMUserName;
	}
	public String getDockerVMPd() {
		return dockerVMPd;
	}
	public void setDockerVMPd(String dockerVMPd) {
		this.dockerVMPd = dockerVMPd;
	}
	public String getSleepTimeFirst() {
		return sleepTimeFirst;
	}
	public void setSleepTimeFirst(String sleepTimeFirst) {
		this.sleepTimeFirst = sleepTimeFirst;
	}
	
	

}
