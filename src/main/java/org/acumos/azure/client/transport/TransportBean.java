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
