package org.acumos.azure.client.utils;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Orchestrator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 964690784032387784L;
	
	public Orchestrator(String name, String version, String image) {
		super();
		this.name = name;
		this.version = version;
		this.image = image;
	}

	public Orchestrator() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("name")
	private String name = null;
	
	@JsonProperty("version")
	private String version = null;
	
	@JsonProperty("image")
	private String image = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Orchestrator [name=" + name + ", version=" + version + ", image=" + image + "]";
	}
	
}
