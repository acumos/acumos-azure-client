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
		for (DockerInfo d: dockerList) {
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
