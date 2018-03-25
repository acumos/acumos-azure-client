package org.acumos.azure.client.utils;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dependent of each Node in the blueprint.json. IMPORTANT: This is not a List by itself.
 */

public class ConnectedTo implements Serializable {
	private static final long serialVersionUID = 5749775315078650369L;

	@JsonProperty("container_name")
	private String containerName = null;
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public ConnectedTo() {
		super();
	}

	/**
	 * ConnectedTo Constructor
	 * 
	 * @param containerName
	 *            Name of the container
	 * @param operationSignature
	 *            Operation signature
	 */

	public ConnectedTo(String containerName, OperationSignature operationSignature) {
		super();
		this.containerName = containerName;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("container_name")
	public String getContainerName() {
		return containerName;
	}

	@JsonProperty("container_name")
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	@JsonProperty("operation_signature")
	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("operation_signature")
	public void setOperationSignature(OperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

	@Override
	public String toString() {
		return "Component [containerName=" + containerName + ", operationSignature=" + operationSignature + "]";
	}

}
