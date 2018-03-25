package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is a representation of Input Port in blueprint
 * 
 */

public class InputPort implements Serializable {

	private final static long serialVersionUID = 4874295563788554992L;
	@JsonProperty("container_name")
	private String container = null;
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public InputPort() {
		super();
	}

	/**
	 * 
	 * @param operationSignature
	 *            An operation - it further has a name, input message and output
	 *            message
	 * @param container
	 *            Name of the container in the dockerinfo.json
	 */
	public InputPort(String container, OperationSignature operationSignature) {
		super();
		this.container = container;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("container_name")
	public String getContainerName() {
		return container;
	}

	@JsonProperty("container_name")
	public void setContainerName(String container) {
		this.container = container;
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

		return "InputPort [container=" + container + ", operationSignature =" + operationSignature + "]";
	}

}
