package org.acumos.azure.client.transport;

public class SolutionDeployment {
 
	private String solutionId;
	private String solutionRevisionId;
	private String vmUserName;
	private String vmUserPd;
	private String vmHost;
	
	
	public String getVmHost() {
		return vmHost;
	}
	public void setVmHost(String vmHost) {
		this.vmHost = vmHost;
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
