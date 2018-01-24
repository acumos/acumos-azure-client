package org.acumos.azure.client.test.util;

import org.acumos.azure.client.utils.Orchestrator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorTest {

	private static Logger logger = LoggerFactory.getLogger(OrchestratorTest.class);
	@Test	
	public void OperationSignatureTestparameter(){
		String name = "Adder";
		String version = "1";
		String image = "cognita-nexus01:8001/newadder1";
		Orchestrator orch=new Orchestrator();
		orch.setImage(image);
		orch.setName(name);
		orch.setVersion(version);
		Assert.assertEquals(name, orch.getName());
		Assert.assertEquals(version, orch.getVersion());
		Assert.assertEquals(image, orch.getImage());
	}
}
