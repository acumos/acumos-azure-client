package org.acumos.azure.client.test.util;

import org.acumos.azure.client.utils.OperationSignature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationSignatureTest {

	private static Logger logger = LoggerFactory.getLogger(OperationSignatureTest.class);
	@Test	
	public void OperationSignatureTestparameter(){
		logger.info("<---------Start-------OperationSignatureTestparameter-------------->");
		String operation="Add";
		OperationSignature opr=new OperationSignature();
		opr.setOperation(operation);
		Assert.assertEquals(operation, opr.getOperation());
		logger.info("<---------End-------OperationSignatureTestparameter-------------->");
	}
}
