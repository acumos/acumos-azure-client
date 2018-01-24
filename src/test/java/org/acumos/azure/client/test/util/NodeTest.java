package org.acumos.azure.client.test.util;

import java.util.ArrayList;
import java.util.List;

import org.acumos.azure.client.utils.Component;
import org.acumos.azure.client.utils.Node;
import org.acumos.azure.client.utils.OperationSignature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class NodeTest {
	
	private static Logger logger = LoggerFactory.getLogger(NodeTest.class);
	@Test	
	public void nodeTestObjectparameter(){
		logger.info("<---------Start-------nodeTestObjectparameter-------------->");
		
		OperationSignature operationSignature = new OperationSignature();
		String containerName = "Adder1";
	    String image = "cognita-nexus01:8001/newadder1:1";
	    Component component=new Component();
	    component.setName("testName");
		component.setOperationSignature(operationSignature);
	   
	    List<Component> dependsOn = new ArrayList<Component>();
	    dependsOn.add(component);
	    Node node =new Node();
	    node.setContainerName(containerName);
	    node.setDependsOn(dependsOn);
	    node.setImage(image);
	    Assert.assertEquals(containerName, node.getContainerName());
	    Assert.assertEquals(image, node.getImage());
	    Assert.assertEquals(dependsOn, node.getDependsOn());
		
	    logger.info("<---------End-------nodeTestObjectparameter-------------->");
   
	}
}
