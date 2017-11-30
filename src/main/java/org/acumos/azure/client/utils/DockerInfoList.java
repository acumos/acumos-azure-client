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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerInfoList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8959582621635414829L;

	@JsonProperty("docker_info_list")
	private List<DockerInfo> dockerList = null;

	public DockerInfoList(List<DockerInfo> dockerList) {
		super();
		this.dockerList = dockerList;
	}

	public DockerInfoList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<DockerInfo> getDockerList() {
		return dockerList;
	}

	public DockerInfo findDockerInfoByContainer(String container) {
		for (DockerInfo d : dockerList) {
			if (d.getContainer().equals(container))
				return d;
		}
		return null;

	}

	public void setDockerList(List<DockerInfo> dockerList) {
		this.dockerList = dockerList;
	}

	@Override
	public String toString() {
		return "DockerInfos [dockerList=" + dockerList + "]";
	}

}
