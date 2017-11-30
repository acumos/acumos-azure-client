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

package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.HashMap;

public class AzureBean implements Serializable {

	private static final long serialVersionUID = 8150999661600938895L;

	public AzureBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	private DockerInfoList dockerinfolist = null;
	private HashMap<String, String> bluePrintMap = null;

	private String bluePrintIp = null;
	private String bluePrintPort = null;
	private String azureVMIP = null;

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
