package org.acumos.azure.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 */
public abstract class AbstractController {

	protected static final String APPLICATION_JSON = "application/json";

	protected final ObjectMapper mapper;

	public AbstractController() {
		mapper = new ObjectMapper();
	}

}
