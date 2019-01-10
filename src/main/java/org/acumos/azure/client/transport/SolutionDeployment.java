package org.acumos.azure.client.transport;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolutionDeployment {
 
	private String solutionId;
	private String solutionRevisionId;
	private String vmUserName;
	private String vmUserPd;
	private String vmHostIP;
	private String vmHostName;
	private String userId;
	
	private String jsonPosition;
	private String jsonMapping;
	
	private String username;
	@JsonProperty("password")
	private String userPd;
	private String host;
	private String port;
	private String urlAttribute;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUrlAttribute() {
		return urlAttribute;
	}
	public void setUrlAttribute(String urlAttribute) {
		this.urlAttribute = urlAttribute;
	}
	public String getJsonPosition() {
		return jsonPosition;
	}
	public void setJsonPosition(String jsonPosition) {
		this.jsonPosition = jsonPosition;
	}
	public String getJsonMapping() {
		return jsonMapping;
	}
	public void setJsonMapping(String jsonMapping) {
		this.jsonMapping = jsonMapping;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserPd() {
		return userPd;
	}
	public void setUserPd(String userPd) {
		this.userPd = userPd;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getVmHostName() {
		return vmHostName;
	}
	public void setVmHostName(String vmHostName) {
		this.vmHostName = vmHostName;
	}
	public String getVmHostIP() {
		return vmHostIP;
	}
	public void setVmHostIP(String vmHostIP) {
		this.vmHostIP = vmHostIP;
	}
	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	public String getSolutionRevisionId() {
		return solutionRevisionId;
	}
	public void setSolutionRevisionId(String solutionRevisionId) {
		this.solutionRevisionId = solutionRevisionId;
	}
	public String getVmUserName() {
		return vmUserName;
	}
	public void setVmUserName(String vmUserName) {
		this.vmUserName = vmUserName;
	}
	public String getVmUserPd() {
		return vmUserPd;
	}
	public void setVmUserPd(String vmUserPd) {
		this.vmUserPd = vmUserPd;
	}
	
	
}
