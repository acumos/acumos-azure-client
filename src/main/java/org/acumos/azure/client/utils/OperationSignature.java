package org.acumos.azure.client.utils;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperationSignature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8176878378145971860L;

	public OperationSignature() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public OperationSignature(String operation) {
		super();
		this.operation = operation;
	}

	@JsonProperty("operation")
	private String operation = null;

	public String getOperation() {
		return operation;
	}
    
	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "OperationSignature [operation=" + operation + "]";
	}

}
