package org.acumos.azure.client.test.util;

import java.util.ArrayList;
import java.util.List;

import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.Node;
import org.acumos.azure.client.utils.OperationSignature;
import org.acumos.azure.client.utils.Orchestrator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluePrintTest {
	
	private static Logger logger = LoggerFactory.getLogger(BluePrintTest.class);
	@Test	
	public void BluePrintTestObjectparameter(){
		logger.info("<---------Start-------BluePrintTestObjectparameter-------------->");
		String name = "bluePrint";
		String version = "1";
		//String nodes = "3";
		//String inputs = "Adder";
		//String orchestrator = "BluePrintOrchestrator";
		
		
        List<OperationSignature> inputs=(List)new ArrayList<OperationSignature>();
		Orchestrator orchestrator=new Orchestrator();
		List<Node> nodes=(List)new ArrayList<Node>();
		
		Blueprint blueprint=new Blueprint();
		//blueprint.setInputs(inputs);
		blueprint.setName(name);
		blueprint.setNodes(nodes);
		//blueprint.setOrchestrator(orchestrator);
		blueprint.setVersion(version);
		
		Assert.assertEquals(name, blueprint.getName());
		//Assert.assertEquals(inputs, blueprint.getInputs());
		Assert.assertEquals(nodes, blueprint.getNodes());
		//.assertEquals(orchestrator, blueprint.getOrchestrator());
		Assert.assertEquals(version, blueprint.getVersion());
		
		logger.info("<---------End-------BluePrintTestObjectparameter-------------->");
	}

}
