package org.acumos.azure.client.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of Operation Signature list of a Node. IMPORTANT: This itself
 * is NOT an Arraylist.
 */

public class OperationSignatureList implements Serializable {
	private final static long serialVersionUID = -6436344519431883582L;

	@JsonProperty("operation_signature")
	private OperationSignature operationSignature = null;
	@JsonProperty("connected_to")
	private ArrayList<ConnectedTo> connectedTo = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public OperationSignatureList() {
		super();
	}

	/**
	 * Standard POJO constructor initialized with field
	 * 
	 * @param operationSignature
	 *            This is the operation signature
	 * @param connectedTo
	 *            This is the connected to for an operation signature.
	 */
	public OperationSignatureList(OperationSignature operationSignature, ArrayList<ConnectedTo> connectedTo) {
		super();
		this.operationSignature = operationSignature;
		this.connectedTo = connectedTo;
	}

	@JsonProperty("operation_signature")
	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("operation_signature")
	public void setOperationSignature(OperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

	@JsonProperty("connected_to")
	public ArrayList<ConnectedTo> getConnectedTo() {
		return connectedTo;
	}

	@JsonProperty("connected_to")
	public void setConnectedTo(ArrayList<ConnectedTo> connectedTo) {
		this.connectedTo = connectedTo;
	}

	@Override
	public String toString() {

		return "OperationSignatureList [operationSignature=" + operationSignature + ", connectedTo=" + connectedTo
				+ "]";
	}
}
