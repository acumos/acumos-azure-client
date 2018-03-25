package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSource {
	private final static long serialVersionUID = -4657840573214474196L;
	@JsonProperty("name")
	private String name = null;
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public DataSource() {
		super();
	}

	/**
	 * 
	 * @param operationSignature
	 * 		This is the operation signature
	 * @param name
	 * 		This is the name of the source
	 */
	public DataSource(String name, OperationSignature operationSignature) {
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
