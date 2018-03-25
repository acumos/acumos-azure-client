package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of data brokers object under training clients. This is NOT a
 * representation or Node type Data Broker.
 */

public class DataBroker implements Serializable {
	private final static long serialVersionUID = -5917310849172760370L;

	@JsonProperty("name")
	private String name = null;
	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public DataBroker() {
		super();
	}

	/**
	 * 
	 * @param operationSignature
	 *            This is the operation signature.
	 * @param name
	 *            Name of the data source
	 */
	public DataBroker(String name, OperationSignature operationSignature) {
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
