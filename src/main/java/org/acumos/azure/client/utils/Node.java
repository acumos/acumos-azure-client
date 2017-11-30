package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1900236928331958666L;

	public Node() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("container_name")
	private String containerName = null;

	@JsonProperty("image")
	private String image = null;

	@JsonProperty("depends_on")
	private List<Component> dependsOn = null;

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Component> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<Component> dependsOn) {
		this.dependsOn = dependsOn;
	}
	
	public Node addDependsOn(Component component) {
		if (this.dependsOn == null) {
			this.dependsOn =  new ArrayList<Component>();
		}
		this.dependsOn.add(component);
		return this;
	}

	@Override
	public String toString() {
		return "Node [containerName=" + containerName + ", image=" + image + ", dependsOn=" + dependsOn + "]";
	}
}
