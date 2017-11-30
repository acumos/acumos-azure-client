package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Component implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5749775315078650369L;

	public Component() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Component(String name, OperationSignature operationSignature) {
		super();
		this.name = name;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("name")
	private String name = null;
	/*
	 * @JsonProperty("operation_signatures") private List<OperationSignature>
	 * operationSignatures = null;
	 */
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	public void setOperationSignature(OperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

	@Override
	public String toString() {
		return "Component [name=" + name + ", operationSignature=" + operationSignature + "]";
	}

}
