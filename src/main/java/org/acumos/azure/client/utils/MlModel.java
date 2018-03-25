package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MlModel implements Serializable {
	private final static long serialVersionUID = 4186812809255376373L;

	@JsonProperty("name")
	private String name = null;
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public MlModel() {
		super();
	}

	/**
	 * 
	 * @param operationSignature
	 *            This is the operation signature
	 * @param name
	 *            This is the model name
	 * 
	 */
	public MlModel(String name, OperationSignature operationSignature) {
		super();
		this.name = name;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("operation_signature")
	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("operation_signature")
	public void setOperationSignature(OperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

}
