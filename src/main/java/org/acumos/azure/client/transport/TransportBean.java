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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TransportBean {
	
	private String nexusUrl;
	private String nexusUserName;
	private String nexusPd;
	private Map<String,String> protoContainerMap;
	private Map<String,String> protoMap;
	private String nginxWebFolder;
	private String nginxMapFolder;
	private String nginxImageName;
	private String nginxInternalPort;
	private String azureDataFiles;
	private String nginxPort;
	private String vmIP;
	private String bluePrintName;
	private String probeName;
	private String nexusRegistyName;
	private String dataSourceUrl;
	private String dataSourceUserName;
	private String dataSourcePd;
	private String probePrintImage;
	private String bluePrintImage;
	private ArrayList<String> imageList;
	private HashMap<String,String> imageMap;
	private LinkedList<String> sequenceList;
	
	
	public String getProbePrintImage() {
		return probePrintImage;
	}

	public void setProbePrintImage(String probePrintImage) {
		this.probePrintImage = probePrintImage;
	}

	public String getBluePrintImage() {
		return bluePrintImage;
	}

	public void setBluePrintImage(String bluePrintImage) {
		this.bluePrintImage = bluePrintImage;
	}

	public String getDataSourceUrl() {
		return dataSourceUrl;
	}

	public void setDataSourceUrl(String dataSourceUrl) {
		this.dataSourceUrl = dataSourceUrl;
	}

	public String getDataSourceUserName() {
		return dataSourceUserName;
	}

	public void setDataSourceUserName(String dataSourceUserName) {
		this.dataSourceUserName = dataSourceUserName;
	}

	public String getDataSourcePd() {
		return dataSourcePd;
	}

	public void setDataSourcePd(String dataSourcePd) {
		this.dataSourcePd = dataSourcePd;
	}

	public String getNexusRegistyName() {
		return nexusRegistyName;
	}

	public void setNexusRegistyName(String nexusRegistyName) {
		this.nexusRegistyName = nexusRegistyName;
	}

	public String getVmIP() {
		return vmIP;
	}

	public void setVmIP(String vmIP) {
		this.vmIP = vmIP;
	}

	public String getNginxPort() {
		return nginxPort;
	}

	public void setNginxPort(String nginxPort) {
		this.nginxPort = nginxPort;
	}

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
   
	public String getNexusPd() {
		return nexusPd;
	}

	public void setNexusPd(String nexusPd) {
		this.nexusPd = nexusPd;
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


	public ArrayList<String> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}

	public HashMap<String, String> getImageMap() {
		return imageMap;
	}

	public void setImageMap(HashMap<String, String> imageMap) {
		this.imageMap = imageMap;
	}

	public LinkedList<String> getSequenceList() {
		return sequenceList;
	}

	public void setSequenceList(LinkedList<String> sequenceList) {
		this.sequenceList = sequenceList;
	}

	public String getBluePrintName() {
		return bluePrintName;
	}

	public void setBluePrintName(String bluePrintName) {
		this.bluePrintName = bluePrintName;
	}

	public String getProbeName() {
		return probeName;
	}

	public void setProbeName(String probeName) {
		this.probeName = probeName;
	}
	
	

}
