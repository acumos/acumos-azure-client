package org.acumos.azure.client.test.util;

import org.acumos.azure.client.utils.DockerInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerinfoTest {
	
	
	private static Logger logger = LoggerFactory.getLogger(DockerinfoTest.class);
	@Test	
	public void dockerinfoTestparameter(){
		logger.info("<---------Start-------ComponentTest-------------->");
		String container = "Adder1";
        String ipAddress ="10.21.13.63";
        String port = "8557";
        DockerInfo dockerInfo=new DockerInfo();
        dockerInfo.setContainer(container);
        dockerInfo.setIpAddress(ipAddress);
        dockerInfo.setPort(port);
        Assert.assertEquals(container, dockerInfo.getContainer());
        Assert.assertEquals(ipAddress, dockerInfo.getIpAddress());
        Assert.assertEquals(port, dockerInfo.getPort());

	}

}
