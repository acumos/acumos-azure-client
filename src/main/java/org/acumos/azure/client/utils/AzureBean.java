package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.HashMap;

public class AzureBean implements Serializable{
	
	public AzureBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
 private DockerInfoList dockerinfolist=null;
 private HashMap<String,String> bluePrintMap=null;
 
 private String bluePrintIp=null;
 private String bluePrintPort=null;
 private String azureVMIP=null;

public String getBluePrintIp() {
	return bluePrintIp;
}

public void setBluePrintIp(String bluePrintIp) {
	this.bluePrintIp = bluePrintIp;
}

public String getBluePrintPort() {
	return bluePrintPort;
}

public void setBluePrintPort(String bluePrintPort) {
	this.bluePrintPort = bluePrintPort;
}

public DockerInfoList getDockerinfolist() {
	return dockerinfolist;
}

public void setDockerinfolist(DockerInfoList dockerinfolist) {
	this.dockerinfolist = dockerinfolist;
}

public HashMap<String, String> getBluePrintMap() {
	return bluePrintMap;
}

public void setBluePrintMap(HashMap<String, String> bluePrintMap) {
	this.bluePrintMap = bluePrintMap;
}

public String getAzureVMIP() {
	return azureVMIP;
}

public void setAzureVMIP(String azureVMIP) {
	this.azureVMIP = azureVMIP;
}
 
 

}
