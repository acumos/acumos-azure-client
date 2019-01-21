/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.acumos.azure.client.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.SingletonMapClass;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.AzureEncrypt;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;

public class AzureSimpleSolution implements Runnable {
	Logger logger = LoggerFactory.getLogger(AzureSimpleSolution.class);
	private Azure azure;
	private AzureDeployDataObject deployDataObject;
	private String dockerContainerPrefix;
	private String dockerUserName;
	private String dockerPd;
	private String localEnvDockerHost;
	private String localEnvDockerCertPath;
	private ArrayList<String> list = new ArrayList<String>();
	private String dockerRegistryName;
	private String bluePrintName;
	private String bluePrintUser;
	private String bluePrintPass;
	private String networkSecurityGroup;
	private String dockerRegistryPort;
	private String uidNumStr;

	private String dataSource;
	private String dataUserName;
	private String dataPd;
	private String dockerVMUserName;
	private String dockerVMPd;
	private String solutionPort;
	private String subnet;
	private String vnet;
	private String sleepTimeFirst;
	private String sleepTimeSecond;
	private String nexusRegistyUserName;
	private String nexusRegistyPd;
	private String nexusRegistyName;
	private String otherRegistyName;
	
	public AzureSimpleSolution(){
		
	}

	public AzureSimpleSolution(Azure azure, AzureDeployDataObject deployDataObject, String dockerContainerPrefix,
			String dockerUserName, String dockerPd, String localEnvDockerHost, String localEnvDockerCertPath,
			ArrayList<String> list, String bluePrintName, String bluePrintUser, String bluePrintPass,
			String networkSecurityGroup, String dockerRegistryName, String uidNumStr, String dataSource,
			String dataUserName, String dataPd, String dockerVMUserName, String dockerVMPd,String solutionPort,
			String subnet,String vnet,String sleepTimeFirst,String sleepTimeSecond,String nexusRegistyUserName,String nexusRegistyPd,
			String nexusRegistyName,String otherRegistyName) {
		this.azure = azure;
		this.deployDataObject = deployDataObject;
		this.dockerContainerPrefix = dockerContainerPrefix;
		this.dockerUserName = dockerUserName;
		this.dockerPd = dockerPd;
		this.localEnvDockerHost = localEnvDockerHost;
		this.localEnvDockerCertPath = localEnvDockerCertPath;
		this.list = list;
		this.bluePrintName = bluePrintName;
		this.bluePrintUser = bluePrintUser;
		this.bluePrintPass = bluePrintPass;

		this.networkSecurityGroup = networkSecurityGroup;
		this.dockerRegistryName = dockerRegistryName;
		this.uidNumStr = uidNumStr;
		this.dataSource = dataSource;
		this.dataUserName = dataUserName;
		this.dataPd = dataPd;
		this.dockerVMUserName = dockerVMUserName;
		this.dockerVMPd = dockerVMPd;
		this.solutionPort = solutionPort;
		this.subnet = subnet;
		this.vnet = vnet;
		this.sleepTimeFirst = sleepTimeFirst;
		this.sleepTimeSecond = sleepTimeSecond;
		this.nexusRegistyUserName = nexusRegistyUserName;
		this.nexusRegistyPd = nexusRegistyPd;
		this.nexusRegistyName = nexusRegistyName;
		this.otherRegistyName = otherRegistyName;

	}

	public void run() {
		logger.debug("AzureSimpleSolution Run Started ");
		logger.debug("azure" + azure);
		logger.debug("deployDataObject " + deployDataObject);
		logger.debug("dockerContainerPrefix " + dockerContainerPrefix);
		logger.debug("localEnvDockerHost " + localEnvDockerHost);
		logger.debug("localEnvDockerCertPath " + localEnvDockerCertPath);
		logger.debug("list " + list);
		logger.debug("uidNumStr " + uidNumStr);
		logger.debug("solutionPort " + solutionPort);
		logger.debug("solutionId " + deployDataObject.getSolutionId());
		logger.debug("solutionRevisionId " + deployDataObject.getSolutionRevisionId());
		logger.debug("userId " + deployDataObject.getUserId());
		logger.debug("sleepTimeFirst " + sleepTimeFirst);
		logger.debug("sleepTimeSecond " + sleepTimeSecond);
		logger.debug("nexusRegistyName "+nexusRegistyName);
		logger.debug("otherRegistyName "+otherRegistyName);
        
		AzureBean azureBean = new AzureBean();
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		AzureContainerBean containerBean = new AzureContainerBean();
		AzureEncrypt azEncrypt=new AzureEncrypt();
		try {
			 
			//dockerVMPd=azureUtil.getRandomPassword(10).toString();
			//logger.debug("VM PD "+azEncrypt.encrypt(dockerVMPd));
			int sleepTimeFirstInt=Integer.parseInt(sleepTimeFirst);
			int sleepTimeSecondInt=Integer.parseInt(sleepTimeSecond);
			final String saName = SdkContext.randomResourceName("sa", 20);
			final Region region = Region.US_EAST; // US_EAST is coming from Azure sdk libraries
			final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();// "acrsample";
			
			

			// Get the existing Azure registry using resourceGroupName and Acr Name
			Registry azureRegistry = azure.containerRegistries().getByResourceGroup(deployDataObject.getRgName(),
					deployDataObject.getAcrName());

			Utils.print(azureRegistry);

			RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
			DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
					azureRegistry.loginServerUrl(), acrCredentials.username(),
					acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath, azureBean,
					networkSecurityGroup, dockerRegistryPort, dockerRegistryName,dockerVMUserName,dockerVMPd,subnet,vnet,sleepTimeFirstInt);

			AuthConfig authConfig = new AuthConfig().withUsername(dockerUserName).withPassword(dockerPd);
			AuthConfig authConfigNexus = new AuthConfig().withUsername(nexusRegistyUserName).withPassword(nexusRegistyPd);
			logger.debug("Start pulling images from nexus::::::::");
			Iterator itr = list.iterator();
			while (itr.hasNext()) {
				String imageName = (String) itr.next();
				logger.debug("imageName "+imageName);
				if(azureUtil.getRepositryStatus(imageName, nexusRegistyName)){
					logger.debug("In nexus username ");
					dockerClient.pullImageCmd(imageName).withAuthConfig(authConfigNexus)
					// .withTag(dockerImageTag)
					.exec(new PullImageResultCallback()).awaitSuccess();
				}else{
					logger.debug("In other username ");
					dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
					// .withTag(dockerImageTag)
					.exec(new PullImageResultCallback()).awaitSuccess();
				}
				
				Thread.sleep(sleepTimeFirstInt);
			}

			int dockerCount = 1;
			HashMap<String, CreateContainerResponse> hmap = new HashMap<String, CreateContainerResponse>();
			HashMap<String, String> containerTagMap = new HashMap<String, String>();
			HashMap<String, String> containerImageMap = new HashMap<String, String>();
			HashMap<String, String> containerInstanceImageMap = new HashMap<String, String>();
			Iterator itr1 = list.iterator();
			while (itr1.hasNext()) {
				String imageTagVal = "";
				String imageName = (String) itr1.next();
				CreateContainerResponse dockerContainerInstance = dockerClient.createContainerCmd(imageName)
						.withName(dockerContainerName + "_" + dockerCount)
						// .withCmd("/hello")
						.exec();

				if (imageName != null && !"".equals(imageName)) {
					String tag = azureUtil.getTagFromImage(imageName);
					if (tag != null) {
						imageTagVal = tag;
					}
				}
				logger.debug("imageName " + imageName + " imageTagVal " + imageTagVal);
				hmap.put(dockerContainerName + "_" + dockerCount, dockerContainerInstance);
				containerTagMap.put(dockerContainerName + "_" + dockerCount, imageTagVal);
				containerImageMap.put(dockerContainerName + "_" + dockerCount, imageName);
				Thread.sleep(sleepTimeFirstInt);
				dockerCount++;
			}
			logger.debug("List All Docker containers:");
			List<Container> dockerContainers = dockerClient.listContainersCmd().withShowAll(true).exec();

			for (Container container : dockerContainers) {
				logger.debug("All Docker container with images and Name %s (%s)\n" + container.getImage()
						+ "container.getId() " + container.getId());
			}

			// =============================================================
			// Commit the new container
			HashMap<String, String> repoUrlMap = new HashMap<String, String>();
			Iterator itrContainer = hmap.entrySet().iterator();
			while (itrContainer.hasNext()) {
				String imageTagLatest = AzureClientConstants.IMAGE_TAG_LATEST;
				Map.Entry pair = (Map.Entry) itrContainer.next();
				String containerName = (String) pair.getKey();
				CreateContainerResponse dockerContainerInstance = (CreateContainerResponse) pair.getValue();

				String privateRepoUrl = azureRegistry.loginServerUrl() + AzureClientConstants.PRIVATE_REPO_PREFIX + containerName;
				logger.debug("dockerContainerInstance.getId() " + dockerContainerInstance.getId()
						+ " privateRepoUrl " + privateRepoUrl);
				if (containerTagMap != null && containerTagMap.get(containerName) != null) {
					imageTagLatest = containerTagMap.get(containerName);
				}
				logger.debug("containerName " + containerName + "imageTagLatest " + imageTagLatest);
				String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
						.withRepository(privateRepoUrl).withTag(imageTagLatest).exec();
				logger.debug("dockerImageId " + dockerImageId);
				repoUrlMap.put(containerName, privateRepoUrl);
				// We can now remove the temporary container instance
				dockerClient.removeContainerCmd(dockerContainerInstance.getId()).withForce(true).exec();
				Thread.sleep(sleepTimeFirstInt);
			}
			
			Iterator repoContainer = repoUrlMap.entrySet().iterator();
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();

				dockerClient.pushImageCmd(privateRepoUrl).withAuthConfig(dockerClient.authConfig())
						.exec(new PushImageResultCallback()).awaitSuccess();
				Thread.sleep(sleepTimeSecondInt);
			}

			logger.debug("Pushed Images to privaterepourl and removing imgage from local docker host");
			
			logger.debug("Start deployment of Image  ");

			repoContainer = repoUrlMap.entrySet().iterator();
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();
				String tagImage = AzureClientConstants.IMAGE_TAG_LATEST;
				if (containerTagMap != null && containerTagMap.get(containerName) != null) {
					tagImage = containerTagMap.get(containerName);
				}

				String azureVMIP = azureBean.getAzureVMIP();
				String repositoryName = "";
				repositoryName = privateRepoUrl + ":" + tagImage;
			
				logger.debug("azureBean VM " + azureBean.getAzureVMIP());
				String portNumberString="8557"+":"+solutionPort;
				DockerUtils.deploymentImageVM(azureVMIP, dockerVMUserName, dockerVMPd, azureRegistry.loginServerUrl(),
						acrCredentials.username(), acrCredentials.passwords().get(0).value(), repositoryName,
						portNumberString,sleepTimeFirstInt);
				containerBean.setContainerIp(azureBean.getAzureVMIP());
				containerBean.setContainerPort("8557");
				containerBean.setContainerName("ContainerOne");
				azureUtil.generateNotification("Single Solution VM is created, IP is: "+azureVMIP, deployDataObject.getUserId(),
						dataSource, dataUserName, dataPd);
			}
			createDeploymentData(dataSource, dataUserName, dataPd, containerBean,
					deployDataObject.getSolutionId(), deployDataObject.getSolutionRevisionId(),
					deployDataObject.getUserId(), uidNumStr, AzureClientConstants.DEPLOYMENT_PROCESS,azureUtil);
		} catch (Exception e) {
			 logger.error("AzureSimpleSolution failed", e);
			try{
				azureUtil.generateNotification("Error in vm creation", deployDataObject.getUserId(),
						dataSource, dataUserName, dataPd);
				createDeploymentData(dataSource, dataUserName, dataPd, containerBean,
						deployDataObject.getSolutionId(), deployDataObject.getSolutionRevisionId(),
						deployDataObject.getUserId(), uidNumStr, AzureClientConstants.DEPLOYMENT_FAILED,azureUtil);
			}catch(Exception ex){
				logger.error("createDeploymentData failed", e);
			}
		}
		logger.debug("AzureSimpleSolution Run End");
	}
	
    public MLPSolutionDeployment createDeploymentData(String dataSource, String dataUserName, String dataPd,
			AzureContainerBean containerBean, String solutionId, String solutionRevisionId, String userId,
			String uidNumber, String deploymentStatusCode,AzureCommonUtil azureUtil) throws Exception {
			logger.debug(" createDeploymentData Start");
			logger.debug("solutionId " + solutionId);
			logger.debug("solutionRevisionId " + solutionRevisionId);
			logger.debug("userId " + userId);
			logger.debug("uidNumber " + uidNumber);
			logger.debug("deploymentStatusCode " + deploymentStatusCode);
			MLPSolutionDeployment mlpDeployment=null;
			ObjectMapper mapper = new ObjectMapper();
			CommonDataServiceRestClientImpl client = azureUtil.getClient(dataSource, dataUserName, dataPd);
			if (solutionId != null && solutionRevisionId != null && userId != null && uidNumber != null) {
				MLPSolutionDeployment mlp = new MLPSolutionDeployment();
				mlp.setSolutionId(solutionId);
				mlp.setUserId(userId);
				mlp.setRevisionId(solutionRevisionId);
				mlp.setDeploymentId(uidNumber);
				mlp.setDeploymentStatusCode(deploymentStatusCode);
				String azureDetails = mapper.writeValueAsString(containerBean);
				mlp.setDetail(azureDetails);
				logger.debug("azureDetails " + azureDetails);
				mlpDeployment = client.createSolutionDeployment(mlp);
				logger.debug("mlpDeployment " + mlpDeployment);
			}
			logger.debug("createDeploymentData End");
			return mlpDeployment;
	}

}
