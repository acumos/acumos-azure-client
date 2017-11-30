package org.acumos.azure.client.service;

import java.io.IOException;
import java.util.LinkedList;

import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfoList;

import java.util.ArrayList;

import com.microsoft.azure.management.Azure;

public interface AzureService {
	
	/*
	 *  Authorize with microsoft Azure active directory
	 */
	Azure authorize(AzureDeployDataObject authObject);
	
	/*
	 * Push/Deploy the image to application
	 */	

	/*boolean pushImage(Azure azure, String clientId, String secret, String rgName, String acrName,String solutionId,String imagetag) throws IOException, Exception;*/
	
	/*
	 * Push/Deploy the image to application
	 */	

	AzureBean pushImage(Azure azure, AzureDeployDataObject deployDataObject, String dockerContainerPrefix, String dockerUserName, String dockerPwd, 
			String localEnvDockerHost, String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,
			String networkSecurityGroup,String dockerRegistryPort) throws IOException, Exception;
	
	public void putContainerDetails(DockerInfoList  dockerList,String apiUrl);
	public void putBluePrintDetails(Blueprint  bluePrint,String apiUrl);
	
}
