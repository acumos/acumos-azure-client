package org.acumos.openstack.client.transport;

public class OpenstackDeployBean {
	
	private String identityEndpoint;
	private String userName;
	private String password;
	private String identifierName;
	private String projectScopeId;
	private String keyName;
	private String vmName;
	
	
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getIdentityEndpoint() {
		return identityEndpoint;
	}
	public void setIdentityEndpoint(String identityEndpoint) {
		this.identityEndpoint = identityEndpoint;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIdentifierName() {
		return identifierName;
	}
	public void setIdentifierName(String identifierName) {
		this.identifierName = identifierName;
	}
	public String getProjectScopeId() {
		return projectScopeId;
	}
	public void setProjectScopeId(String projectScopeId) {
		this.projectScopeId = projectScopeId;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	

}
