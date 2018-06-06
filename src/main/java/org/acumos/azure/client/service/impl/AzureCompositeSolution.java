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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.SingletonMapClass;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import org.acumos.azure.client.transport.ContainerInfo;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.MLNotification;

//import org.acumos.p

public class AzureCompositeSolution implements Runnable {
	
	
	Logger logger =LoggerFactory.getLogger(AzureCompositeSolution.class);	
	private Azure azure;
	private AzureDeployDataObject deployDataObject;
	private String dockerContainerPrefix;
	private String dockerUserName;
	private String dockerPwd;
	private String localEnvDockerHost;
	private String localEnvDockerCertPath;
	private ArrayList<String> list=new ArrayList<String>();
	private String dockerRegistryName;
	private String bluePrintName;
	private String bluePrintUser;
	private String bluePrintPass;
	private String probeInternalPort;
	private String probeName;
	private String probeUser;
	private String probePass;
	private String networkSecurityGroup;
	private String dockerRegistryPort;
	private HashMap<String,String> imageMap;
	private LinkedList<String> sequenceList;
	private Blueprint bluePrint;
	private String uidNumStr;
	private String dataSource;
	private String dataUserName;
	private String dataPassword;
	private String dockerVMUserName;
	private String dockerVMPassword;
	private String solutionPort;
	private HashMap<String,DeploymentBean> nodeTypeContainerMap;
    private String bluePrintJsonStr;
    private String probeNexusEndPoint;
    private String subnet;
    private String vnet;
    private DataBrokerBean dataBrokerBean;
    private String sleepTimeFirst;
	private String sleepTimeSecond;
	private String nexusRegistyUserName;
	private String nexusRegistyPwd;
	private String nexusRegistyName;
	private String otherRegistyName;
	private String exposeDataBrokerPort;
	private String internalDataBrokerPort;
	
    public AzureCompositeSolution(){
    	
    }
	
    // Constructor of AzureCompositeSolution
	public AzureCompositeSolution(Azure azure,AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String dockerUserName,String dockerPwd,
			String localEnvDockerHost,String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,String probeInternalPort,String probeName,
			String probeUser,String probePass,String networkSecurityGroup,HashMap<String,String> imageMap,LinkedList<String> sequenceList,String dockerRegistryName,Blueprint bluePrint,String uidNumStr,
			String dataSource,String dataUserName,String dataPassword,String dockerVMUserName,String dockerVMPassword,String solutionPort,HashMap<String,DeploymentBean> nodeTypeContainerMap,
			String bluePrintJsonStr, String probeNexusEndPoint,String subnet,String vnet,DataBrokerBean dataBrokerBean,
			String sleepTimeFirst,String sleepTimeSecond,String nexusRegistyUserName,String nexusRegistyPwd,String nexusRegistyName,
			String otherRegistyName,String exposeDataBrokerPort,String internalDataBrokerPort) {
	    this.azure = azure;
	    this.deployDataObject = deployDataObject;
	    this.dockerContainerPrefix = dockerContainerPrefix;
	    this.dockerUserName = dockerUserName;
	    this.dockerPwd = dockerPwd;
	    this.localEnvDockerHost = localEnvDockerHost;
	    this.localEnvDockerCertPath = localEnvDockerCertPath;
	    this.list = list;
	    this.bluePrintName = bluePrintName;
	    this.bluePrintUser = bluePrintUser;
	    this.bluePrintPass = bluePrintPass;
	    
	    this.networkSecurityGroup = networkSecurityGroup;
	    this.dockerRegistryName = dockerRegistryName;
	    this.imageMap=imageMap;
	    this.sequenceList=sequenceList;
	    this.bluePrint=bluePrint;
	    this.uidNumStr=uidNumStr;
	    
	    this.dataSource=dataSource;
	    this.dataUserName=dataUserName;
	    this.dataPassword=dataPassword;
	    this.dockerVMUserName=dockerVMUserName;
	    this.dockerVMPassword=dockerVMPassword;
	    this.solutionPort=solutionPort;
	    this.nodeTypeContainerMap=nodeTypeContainerMap;
	    this.bluePrintJsonStr=bluePrintJsonStr;
	    this.probeName=probeName;
	    this.probeUser=probeUser;
	    this.probePass=probePass;
	    this.probeInternalPort=probeInternalPort;
	    this.probeNexusEndPoint=probeNexusEndPoint;
	    this.subnet=subnet;
	    this.vnet=vnet;
	    this.dataBrokerBean=dataBrokerBean;
	    this.sleepTimeFirst = sleepTimeFirst;
		this.sleepTimeSecond = sleepTimeSecond;
		this.nexusRegistyUserName = nexusRegistyUserName;
		this.nexusRegistyPwd = nexusRegistyPwd;
		this.nexusRegistyName = nexusRegistyName;
		this.otherRegistyName = otherRegistyName;
		this.exposeDataBrokerPort = exposeDataBrokerPort;
		this.internalDataBrokerPort = internalDataBrokerPort;
	    
	   }
	public void run() {
		logger.debug("AzureCompositeSolution Run Started ");
		logger.debug("azure "+azure);
		logger.debug("deployDataObject "+deployDataObject);
		logger.debug("dockerContainerPrefix "+dockerContainerPrefix);
		logger.debug("list "+list);
		logger.debug("bluePrintName "+bluePrintName);
		logger.debug("uidNumStr "+uidNumStr);
		logger.debug("sequenceList "+sequenceList);
		logger.debug("imageMap "+imageMap);
		logger.debug("solutionPort "+solutionPort);
		logger.debug("nodeTypeContainerMap "+nodeTypeContainerMap);
		logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
		logger.debug("probeName "+probeName);
		logger.debug("probeInternalPort "+probeInternalPort);
		logger.debug("probeNexusEndPoint "+probeNexusEndPoint);
		logger.debug("sleepTimeFirst " + sleepTimeFirst);
		logger.debug("sleepTimeSecond " + sleepTimeSecond);
		logger.debug("nexusRegistyName "+nexusRegistyName);
		logger.debug("otherRegistyName "+otherRegistyName);
		logger.debug("exposeDataBrokerPort "+exposeDataBrokerPort);
		logger.debug("internalDataBrokerPort "+internalDataBrokerPort);
		
		
		AzureBean azureBean=new AzureBean();
		ObjectMapper mapper = new ObjectMapper();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		List<ContainerInfo> probeContainerBeanList=new ArrayList<ContainerInfo>();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		
		String probeIP = null;
  	    String probePort = null;
  	    AzureCommonUtil azureUtil=new AzureCommonUtil();
		try{
			int sleepTimeFirstInt=Integer.parseInt(sleepTimeFirst);
			int sleepTimeSecondInt=Integer.parseInt(sleepTimeSecond);
			logger.debug("pushCompositeImage start");
		    if(dockerVMUserName!=null){
		    	dockerVMUserName=dockerVMUserName.trim();	
		    }
		    if(dockerVMPassword!=null){
		    	dockerVMPassword=dockerVMPassword.trim();	
		    }
			HashMap<String,String> containeDetailMap=new HashMap<String,String>();
			DockerInfoList  dockerList=new DockerInfoList();
	        final Region region = Region.US_EAST;// US_EAST is coming from Azure sdk libraries
	        final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();//"acrsample";
	      
	        String servicePrincipalClientId = deployDataObject.getClient(); // replace with a real service principal client id
	        String servicePrincipalSecret = deployDataObject.getKey(); // and corresponding secret
	        String containerInstanceBluePrint="";
	        String containerInstanceprobe="";
	        
	        logger.debug("list "+list);
	        logger.debug("sequenceList "+sequenceList);
	        logger.debug("bluePrintName "+bluePrintName);
	        String portArr[]={"8557","8558","8559","8560","8561","8562","8563","8564","8565","8566"};
            if(list!=null && list.size() > 0){
            	
     	            //=============================================================
		            // If service principal client id and secret are not set via the local variables, attempt to read the service
		            //   principal client id and secret from a secondary ".azureauth" file set through an environment variable.
		            //
		            //   If the environment variable was not set then reuse the main service principal set for running this sample.
	
		            if (servicePrincipalClientId.isEmpty() || servicePrincipalSecret.isEmpty()) {
		                String envSecondaryServicePrincipal = System.getenv(AzureClientConstants.AZURE_AUTH_LOCATION_NEXT);
	
		                if (envSecondaryServicePrincipal == null || !envSecondaryServicePrincipal.isEmpty() || !Files.exists(Paths.get(envSecondaryServicePrincipal))) {
		                    envSecondaryServicePrincipal = System.getenv(AzureClientConstants.AZURE_AUTH_LOCATION);
		                }
	
		                servicePrincipalClientId = Utils.getSecondaryServicePrincipalClientID(envSecondaryServicePrincipal);
		                servicePrincipalSecret = Utils.getSecondaryServicePrincipalSecret(envSecondaryServicePrincipal);
		            }
	
	
		            //=============================================================
		            // Create an SSH private/public key pair to be used when creating the container service
	
		            logger.debug("Creating an SSH private and public key pair");
	
		            SSHShell.SshPublicPrivateKey sshKeys = SSHShell.generateSSHKeys("", AzureClientConstants.SSH_ACS);
	
	
		            //=============================================================
		            // Create an Azure Container Service with Kubernetes orchestration
	
		            logger.debug("Creating an Azure Container Service with Kubernetes ochestration and one agent (virtual machine)");
	
		            Date t1 = new Date();        
	
	
		            //=============================================================
		            // Create an Azure Container Registry to store and manage private Docker container images
	
		            logger.debug("Creating an Azure Container Registry");
		            Date t2 = new Date();
		            
		            t1 = new Date();
	                
		            //Get the existing Azure registry using resourceGroupName and Acr Name
		            Registry azureRegistry = azure.containerRegistries().getByResourceGroup(deployDataObject.getRgName(), deployDataObject.getAcrName());
		            
		            t2 = new Date();
		            logger.debug("Created Azure Container Registry: (took " + ((t2.getTime() - t1.getTime()) / 1000) + " seconds) " + azureRegistry.id());
		            Utils.print(azureRegistry);
	
	
		            //=============================================================
		            // Create a Docker client that will be used to push/pull images to/from the Azure Container Registry
	
		            RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
		            DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath,azureBean,
		                    networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPassword,subnet,vnet,sleepTimeFirstInt);
		            
		            AuthConfig authConfig = new AuthConfig()
		                    .withUsername(dockerUserName)
		                    .withPassword(dockerPwd);
		            
		            AuthConfig authConfig2 = new AuthConfig()
		                    .withUsername(bluePrintUser)
		                    .withPassword(bluePrintPass);
		            
		            AuthConfig authConfigProb = new AuthConfig()
		                    .withUsername(probeUser)
		                    .withPassword(probePass);
		            
		            AuthConfig authConfigNexus = new AuthConfig()
		                    .withUsername(nexusRegistyUserName)
		                    .withPassword(nexusRegistyPwd);
	
		            //=============================================================
		            // Pull a temp image from public Docker repo and create a temporary container from that image
		            // These steps can be replaced and instead build a custom image using a Dockerfile and the app's JAR
		            logger.debug("Start pulling images from nexus ");
		            Iterator itr=list.iterator();
		            while(itr.hasNext()){
		            	 
		            	String imageName=(String)itr.next();
		            	logger.debug("image name in run "+imageName);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		logger.debug("Pulling image start logging in "+imageName);
		            		dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig2)
		                    //.withTag(dockerImageTag)
		                    .exec(new PullImageResultCallback())
		                    .awaitSuccess();
		            		
		            	}else if(imageName!=null && imageName.contains(probeName)) {
		            		logger.debug("Pulling image start logging in and pulling image "+imageName);
		            		dockerClient.pullImageCmd(imageName).withAuthConfig(authConfigProb)
		                    //.withTag(dockerImageTag)
		                    .exec(new PullImageResultCallback())
		                    .awaitSuccess();
		            		
		            	}else{
		            	
		            	logger.debug("image name in run else "+imageName);
                        if(azureUtil.getRepositryStatus(imageName, nexusRegistyName)){
                        	logger.debug("Repository Nexus");
                        	dockerClient.pullImageCmd(imageName).withAuthConfig(authConfigNexus)
    	                    //.withTag(dockerImageTag)
    	                    .exec(new PullImageResultCallback())
    	                    .awaitSuccess();
		            	 }else{
		            		 logger.debug("other Registry");
		            		 dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
			                    //.withTag(dockerImageTag)
			                    .exec(new PullImageResultCallback())
			                    .awaitSuccess();
		            	  }
		            	
		            	}
		            	Thread.sleep(sleepTimeSecondInt);
		            }
		            
		            
		            logger.debug("List local Docker images:");
		            List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();
		            int dockerCount=1;
		            HashMap<String,CreateContainerResponse> hmap=new HashMap<String,CreateContainerResponse>();
		            HashMap<String,String> containerTagMap=new HashMap<String,String>();
		            HashMap<String,String> containerImageMap=new HashMap<String,String>();
		            Iterator itr1=list.iterator();
		            while(itr1.hasNext()){
		            	String imageTagVal="";
		            	String imageName=(String)itr1.next();
		            	CreateContainerResponse dockerContainerInstance = dockerClient.createContainerCmd(imageName)
			                    .withName(dockerContainerName+"_"+dockerCount)
			                    //.withCmd("/hello")
			                    .exec();
		            	
		            	if(imageName!=null && !"".equals(imageName)){
		            		String tag=getTagFromImage(imageName);
		            		if(tag!=null){
		            			imageTagVal=tag;
		            		}
		            	}
		            	logger.debug("imageName "+imageName+" imageTagVal "+imageTagVal);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		containerInstanceBluePrint=dockerContainerName+"_"+dockerCount;
		            	}
		            	
		            	if(imageName!=null && imageName.contains(probeName)){
		            		logger.debug("containerInstanceprobe "+probeName);
		            		containerInstanceprobe=dockerContainerName+"_"+dockerCount;
		            	}
		            	
		            	hmap.put(dockerContainerName+"_"+dockerCount, dockerContainerInstance);
		            	containerTagMap.put(dockerContainerName+"_"+dockerCount, imageTagVal);
		            	containerImageMap.put(dockerContainerName+"_"+dockerCount, imageName);
		            	Thread.sleep(sleepTimeFirstInt);
		            	dockerCount++;
		            }
		            logger.debug("containerImageMap "+containerImageMap);
		            logger.debug("List All Docker containers:");
		            List<Container> dockerContainers = dockerClient.listContainersCmd()
		                    .withShowAll(true)
		                    .exec();
		            for (Container container : dockerContainers) {
		            	logger.debug("All Docker container with images and Name %s (%s)\n"+container.getImage()+" container.getId() "+container.getId());
		            }
	
		            //=============================================================
		            // Commit the new container
		            HashMap<String,String> repoUrlMap=new HashMap<String,String>();  
		            Iterator itrContainer=hmap.entrySet().iterator();
		            while(itrContainer.hasNext()){
		            	String imageTagLatest=AzureClientConstants.IMAGE_TAG_LATEST;
		            	Map.Entry pair = (Map.Entry)itrContainer.next();
		            	String containerName=(String)pair.getKey();
		            	CreateContainerResponse dockerContainerInstance=(CreateContainerResponse)pair.getValue();
		            	
		            	String privateRepoUrl = azureRegistry.loginServerUrl() + AzureClientConstants.PRIVATE_REPO_PREFIX + containerName;
		            	logger.debug("dockerContainerInstance.getId() "+dockerContainerInstance.getId()+"privateRepoUrl "+privateRepoUrl);
		            	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
		            		imageTagLatest=containerTagMap.get(containerName);
		            	}
		            	logger.debug("containerName "+containerName+" imageTagLatest "+imageTagLatest);
			            String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
			                    .withRepository(privateRepoUrl)
			                    .withTag(imageTagLatest).exec();
			            logger.debug("dockerImageId "+dockerImageId);
			            repoUrlMap.put(containerName, privateRepoUrl);
			            // We can now remove the temporary container instance
			            dockerClient.removeContainerCmd(dockerContainerInstance.getId())
			                    .withForce(true)
			                    .exec();
			            Thread.sleep(sleepTimeFirstInt);
		            }
		            //#####################################################################################
		            logger.debug("Before Docker remoteDockerClient ");
		            DockerClient remoteDockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), null, localEnvDockerCertPath,azureBean
		                    ,networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPassword,subnet,vnet,sleepTimeFirstInt);
		            logger.debug("After Docker remoteDockerClient ");
		            //=============================================================
		            // Push the new Docker image to the Azure Container Registry
		            Iterator repoContainer=repoUrlMap.entrySet().iterator();
		            while(repoContainer.hasNext()){
		            	Map.Entry pair = (Map.Entry)repoContainer.next();
		            	String containerName=(String)pair.getKey();
		            	String privateRepoUrl=(String)pair.getValue();
		            	
		            	dockerClient.pushImageCmd(privateRepoUrl)
	                    .withAuthConfig(dockerClient.authConfig())
	                    .exec(new PushImageResultCallback()).awaitSuccess();
		            	Thread.sleep(sleepTimeSecondInt);
		            }
		            
		            logger.debug("Pushed Images to privaterepourl and removing imgage from local docker host ");
		            // Remove the temp image from the local Docker host
		            //=============================================================
		            // Verify that the image we saved in the Azure Container registry can be pulled and instantiated locally
		            logger.debug(" pull images from Azure registry to locally ");
		             repoContainer=repoUrlMap.entrySet().iterator();
		            while(repoContainer.hasNext()){
		            	Map.Entry pair = (Map.Entry)repoContainer.next();
		            	String containerName=(String)pair.getKey();
		            	String privateRepoUrl=(String)pair.getValue();
		            	 logger.debug(" pull images from Azure registry to locally privateRepoUrl "+privateRepoUrl);
		            	dockerClient.pullImageCmd(privateRepoUrl)
	                    .withAuthConfig(dockerClient.authConfig())
	                    .exec(new PullImageResultCallback()).awaitSuccess();
		            	Thread.sleep(sleepTimeSecondInt);
		            }
		            
		            logger.debug("List local Docker images after pulling sample image from the Azure Container Registry:");
		            images = dockerClient.listImagesCmd()
		                    .withShowAll(true)
		                    .exec();
	                  logger.debug("remoteDockerClient with privateRepoUrl ");
	                  int imageCount=1;
	                  int remoteCount=1;
	                  int count=0;
	                  ArrayList<DockerInfo> dockerInfoList=new ArrayList<DockerInfo>();
	                  if(sequenceList!=null && sequenceList.size() > 0){
			            	Iterator seqItr = sequenceList.iterator();
			                while (seqItr.hasNext()) {
			                    String jsonContainerName=(String)seqItr.next(); 
			                    logger.debug(" jsonContainerName "+jsonContainerName);
			                    if(jsonContainerName!=null && !"".equals(jsonContainerName)){
			                    	
			                    	repoContainer=repoUrlMap.entrySet().iterator();
			    		            while(repoContainer.hasNext()){
			    		            	Map.Entry pair = (Map.Entry)repoContainer.next();
			    		            	String containerName=(String)pair.getKey();
			    		            	String privateRepoUrl=(String)pair.getValue();
			    		            	String tagImage=AzureClientConstants.IMAGE_TAG_LATEST;
			    		            	String imageName="";
			    		            	DockerInfo dockerinfo=new DockerInfo();
			    		            	String finalContainerName=dockerContainerName + "-private_"+remoteCount;
			    		            	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
			    		            		tagImage=containerTagMap.get(containerName);
			    		            	}
			    		            	logger.debug("tagImage "+tagImage);
			    		            	if(containerImageMap!=null && containerImageMap.get(containerName)!=null){
			    		            		imageName=containerImageMap.get(containerName);
			    		            		logger.debug("imageName "+imageName+" imageMap "+imageMap);
			    		            		if(imageName!=null && imageMap!=null && imageMap.get(imageName)!=null){
			    		            			finalContainerName=imageMap.get(imageName);
			    		            		}
			    		            	}
			    		            	logger.debug(" Before jsonContainerName "+jsonContainerName+" finalContainerName "+finalContainerName);
			    		            	if(finalContainerName!=null && !finalContainerName.equalsIgnoreCase(jsonContainerName)){
			    		            		 logger.debug("Continue.............................................");
			    		            		continue;
			    		            	}
			    		            	String nodeTypeContainer="";
			    		            	String nodeTypeName="";
			    		            	if(nodeTypeContainerMap!=null && nodeTypeContainerMap.size() > 0 && nodeTypeContainerMap.get(finalContainerName)!=null){
			    		            		DeploymentBean dBean=nodeTypeContainerMap.get(finalContainerName);
			    		            		if(dBean!=null){
			    		            			nodeTypeContainer=dBean.getNodeType();
			    		            			nodeTypeName=dBean.getDataBrokerType();
			    		            		}
			    		            		
			    		            	}
			    		            	String azureVMIP=azureBean.getAzureVMIP();
			    		            	String azureVMName=azureBean.getAzureVMName();
			    		        		final String vmUserName=dockerVMUserName;
			    		        		final String vmPassword=dockerVMPassword;
			    		        		String repositoryName="";
			    		        		repositoryName=privateRepoUrl+":"+tagImage;
			    		        		String portNumber="";
			    		        		String portNumberString="";
			    		        		logger.debug("azureVMName " + azureVMName);
			    		        		logger.debug("azureVMIP " + azureVMIP);
			    		        		logger.debug("finalContainerName " + finalContainerName);
			    		        		logger.debug("imageCount " + imageCount);
			    		        		logger.debug("nodeTypeContainer " + nodeTypeContainer);
			    		        		logger.debug("nodeTypeName " + nodeTypeName);
			    		        		logger.debug("containerInstanceprobe " + containerInstanceprobe);
			    		        		logger.debug("Deploying containerName "+containerName);
			    		        		if(containerInstanceBluePrint!=null && containerInstanceBluePrint.equalsIgnoreCase(containerName)){
			    		        			logger.debug("if Part of containerInstanceBluePrint "+containerInstanceBluePrint+" containerName "+containerName);
			    		        			portNumber="8555";
			    		        			azureBean.setBluePrintIp(azureVMIP);
			            			        azureBean.setBluePrintPort(portNumber);
			            			        portNumberString=portNumber+":"+portNumber;
			    		        		}else{
			    		        			portNumber=probeInternalPort;
			    		        			if(containerInstanceprobe != null && !containerInstanceprobe.equals("") && containerName!=null 
				    		        				&& containerInstanceprobe.equalsIgnoreCase(containerName)) {
			    		        				portNumberString=probeInternalPort+":"+probeInternalPort;
			    		        			}else if(nodeTypeContainer!=null && !"".equals(nodeTypeContainer) && nodeTypeContainer.equalsIgnoreCase(AzureClientConstants.DATABROKER_NAME)
			    		        					&& nodeTypeName!=null && !"".equals(nodeTypeName) && nodeTypeName.equalsIgnoreCase(AzureClientConstants.DATA_BROKER_CSV_FILE)){
			    		        				portNumberString=exposeDataBrokerPort+":"+internalDataBrokerPort;
			    		        				portNumber=exposeDataBrokerPort;
			    		        				
			    		        			}else{
			    		        				portNumber=portArr[count];
				    		        			if(solutionPort!=null && !"".equals(solutionPort)){
				    		        				portNumberString=portNumber+":"+solutionPort;
				    		        			}else{
				    		        				portNumberString=portNumber+":"+portNumber;
				    		        			}
				    		        			count++;
			    		        			}
			    		        			
			    		        			
			    		        		}
			    		        		
			    		        		imageCount++;
			    		        		dockerinfo.setIpAddress(azureVMName);
		            		            dockerinfo.setPort(portNumber);
		            		            dockerinfo.setContainer(finalContainerName);
		            		            logger.debug("azureVMName " + azureVMName+" portNumber "+portNumber+" finalContainerName "+finalContainerName);
		            		            logger.debug(" portNumberString " + portNumberString);
		            		            logger.debug(" containerName " + containerName);
		            		            logger.debug("Start Deploying repositoryName "+repositoryName);
			    		        		DockerUtils.deploymentCompositeImageVM(azureVMIP, vmUserName, vmPassword, azureRegistry.loginServerUrl(),  acrCredentials.username(),
			    		        				acrCredentials.passwords().get(0).value(), repositoryName,finalContainerName,imageCount,
			    		        				portNumberString,probeNexusEndPoint,sleepTimeFirstInt);
			    		        		
			    		        		AzureContainerBean containerBean=new AzureContainerBean();
			    		        		containerBean.setContainerName(finalContainerName);
			    		        		containerBean.setContainerIp(azureVMIP);
			    		        		containerBean.setContainerPort(portNumber);
			    		        		
			    		        		DeploymentBean deploymentBean=new DeploymentBean();
			    		        		deploymentBean.setAzureVMIP(azureVMIP);
			    		        		deploymentBean.setAzureVMName(azureVMName);
			    		        		deploymentBean.setContainerName(finalContainerName);
			    		        		deploymentBean.setContainerPort(portNumber);
			    		        		
			    		        		
			    		        		
			    		        		
			    		        		ContainerInfo containerInfo = new ContainerInfo();
			    		        		containerInfo.setContainerName(finalContainerName);
			    		        		containerInfo.setContainerIp(azureVMName);
			    		        		containerInfo.setContainerPort(portNumber);
			    		        		
			    		        		logger.debug(" Before Probe containerInstanceprobe "+containerInstanceprobe+" containerName "+containerName);
			    		        		if(containerInstanceprobe != null && !containerInstanceprobe.equals("") && containerName!=null 
			    		        				&& containerInstanceprobe.equalsIgnoreCase(containerName)) {
			    		        			logger.debug("After Probe containerInstanceprobe "+containerInstanceprobe+" containerName "+containerName);
				    		        	   
				    		        	   probeIP = azureVMIP;
				    		        	   probePort = portNumber;
				    		        	   containerInfo.setNodeType(AzureClientConstants.PROBE_NODE_TYPE);
				    		        	   deploymentBean.setNodeType(AzureClientConstants.PROBE_NODE_TYPE);
			    		        		}else{
			    		        			logger.debug("nodeTypeContainer start for setting " + nodeTypeContainer+" nodeTypeName "+nodeTypeName);
			    		        			if(nodeTypeContainer!=null && !"".equals(nodeTypeContainer)){
				    		        			containerInfo.setNodeType(nodeTypeContainer);
				    		        			deploymentBean.setNodeType(nodeTypeContainer);
				    		        			deploymentBean.setDataBrokerType(nodeTypeName);
			    		        			}else{
			    		        			containerInfo.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			    		        			deploymentBean.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			    		        			deploymentBean.setDataBrokerType("");
			    		        			}
			    		        			
			    		        		}
			    		        		logger.debug("deploymentBean " + deploymentBean.getNodeType()+" nodeTypeName "+deploymentBean.getDataBrokerType());
			    		        		dockerInfoList.add(dockerinfo);
			    		        		deploymentList.add(deploymentBean);
			    		        		probeContainerBeanList.add(containerInfo);
			    		        		azureContainerBeanList.add(containerBean);
			    		            }
			                    	
			                    }
			                }
	                  }  
	                  logger.debug(" dockerInfoList " + dockerInfoList);
	                  if(dockerInfoList!=null && dockerInfoList.size() > 0){
			            	dockerList.setDockerList(dockerInfoList);
			            }
	                  logger.debug("containeDetailMap "+containeDetailMap+" dockerList "+dockerList);
	  	              azureBean.setDockerinfolist(dockerList);	
		            
			}
          
          String azureDetails=mapper.writeValueAsString(azureBean.getDockerinfolist());  
          setuidHashmapComposite(uidNumStr,azureDetails);
          logger.debug(" JSON FILE FROM DESIGN STUDIO "+bluePrintJsonStr);
          logger.debug("azureDetails "+azureDetails);
          logger.debug("Dockerinfolist "+mapper.writeValueAsString(azureBean.getDockerinfolist()));
  		  logger.debug("bluePrint "+mapper.writeValueAsString(bluePrint));	
  		  DockerInfoList dockerInfoList=azureBean.getDockerinfolist();
		  String vmIP=azureBean.getAzureVMIP().trim();
		  String bluePrintPort=azureBean.getBluePrintPort().trim();
		  String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/"+AzureClientConstants.PUT_DOCKER_INFO_URL;  
		  String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/"+AzureClientConstants.PUT_BLUEPRINT_INFO_URL;
		  logger.debug("urlDockerInfo "+urlDockerInfo+" urlBluePrint "+urlBluePrint);
		  String dataBrokerPort=getDataBrokerPort(deploymentList,AzureClientConstants.DATABROKER_NAME);
		  String urlDataBroker="http://"+vmIP+":"+dataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
		  String csvDataBrokerPort="";
		  String csvDataBrokerUrl="";
		  if(dataBrokerBean!=null){
			  csvDataBrokerPort=getDataBrokerPortCSV(deploymentList,AzureClientConstants.DATABROKER_NAME);
		  }
		  if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
			  csvDataBrokerUrl="http://"+vmIP+":"+csvDataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
		  }
		  logger.debug("csvDataBrokerUrl "+csvDataBrokerUrl);
		  logger.debug("csvDataBrokerPort "+csvDataBrokerPort);
		  logger.debug("urlDataBroker "+urlDataBroker);
		  logger.debug("dataBrokerPort "+dataBrokerPort);
		  // Added for probe
		  
		  if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
			  logger.debug("Inside csv Data Broker ConfigDB  "); 
			  callCsvConfigDB(deployDataObject,csvDataBrokerUrl,dataBrokerBean);
			 }
		// putBlueprint
		 if(bluePrint!=null){
			 putBluePrintDetailsJSON(bluePrintJsonStr,urlBluePrint);
		  }
		// putDockerInfo
		 if(dockerList != null){
			  logger.debug("Inside probeContainerBeanList ");
			  putContainerDetailsJSONProbe(dockerList,urlDockerInfo);
			}
		 // configDB
		 if(dataBrokerPort!=null &&  !"".equals(dataBrokerPort)){
			 logger.debug("Inside putDataBrokerDetails ");
			  putDataBrokerDetails(deployDataObject,urlDataBroker);
			}
		
		 
		 // Added notification for probe code
		 ArrayList<ProbeIndicator> probeIndicatorList = bluePrint.getProbeIndicator();
		 ProbeIndicator prbIndicator = null;
		 if(probeIndicatorList != null && probeIndicatorList.size() >0) {
				prbIndicator = probeIndicatorList.get(0);
		 }	
		 
		 if(vmIP!=null && !"".equals(vmIP)){
			 azureUtil.generateNotification("Composite Solution VM is created, IP is:"+vmIP, deployDataObject.getUserId(),
						dataSource, dataUserName, dataPassword);
		 }
		 //if (bluePrint.getProbeIndocator() != null && bluePrint.getProbeIndocator().equalsIgnoreCase("True"))  {
		 if (bluePrint.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True"))  {
			 logger.debug("Probe indicator true. Starting generatenotircation deployDataObject.getUserId() "+deployDataObject.getUserId());
			 logger.debug(" probeIP "+probeIP+" probePort "+probePort);
			 generateNotification(probeIP+":"+probePort,deployDataObject.getUserId());
		 }
		 
		
		 if(azureContainerBeanList!=null){
       	  
   			logger.debug("Start saving data in database "); 
   			createDeploymentCompositeData(dataSource,dataUserName,dataPassword,azureContainerBeanList,deployDataObject.getSolutionId(),
   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,AzureClientConstants.DEPLOYMENT_PROCESS);
       		  
         }
		}catch(Exception e){
			logger.error("AzureCompositeSolution failed", e);
			try{
				azureUtil.generateNotification("Error in vm creation", deployDataObject.getUserId(),
						dataSource, dataUserName, dataPassword);
				createDeploymentCompositeData(dataSource,dataUserName,dataPassword,azureContainerBeanList,deployDataObject.getSolutionId(),
	   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,AzureClientConstants.DEPLOYMENT_FAILED);
			}catch(Exception ex){
				logger.error("createDeploymentCompositeData failed", e);
			}
		}
		 
		logger.debug("AzureCompositeSolution Run End");
	}
	
	/**
	 * 
	 * @param notificationId
	 * @param userId
	 */
	public void addNotificationUser(String notificationId, String userId) {
        logger.debug("addNotificationUser");
    	CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
    	client.addUserToNotification(notificationId,userId);
     }
	
	
	/**
	 * 
	 * @param mlpNotification
	 * @return
	 */
	 public org.acumos.azure.client.transport.MLNotification createNotification(MLPNotification mlpNotification) {
		 logger.debug("createNotification Start");
         CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
         MLNotification mlNotification = Utils.convertToMLNotification(client.createNotification(mlpNotification));
         logger.debug("createNotification End");
         return mlNotification;
	 }
	 
	/**
	  * 
	  * @param msg
	  * @param userId
	  */
	 void generateNotification(String msg, String userId) throws Exception{
		 logger.debug("generateNotification Start");
		 logger.debug("userId "+userId+" msg "+msg);
         MLPNotification notification = new MLPNotification();
         try {
                 if (msg != null) {
                     notification.setTitle(msg);
                     // Provide the IP address and port of the probe Instance
                     notification.setMessage(msg);
                     Date startDate = new Date();
                     Date endDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24));
                     notification.setStart(startDate);
                     notification.setEnd(endDate);
                     CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
                     notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
                     MLNotification mLNotification = createNotification(notification);
                     logger.debug("mLNotification.getNotificationId() "+mLNotification.getNotificationId());
                     client.addUserToNotification(mLNotification.getNotificationId(),userId);
             }
         } catch (Exception e) {
        	 logger.error("generateNotification failed", e);
        	 throw e;
         }
         logger.debug("generateNotification End"); 
	 }

	
	public String getTagFromImage(String imageName){
		String imageTag=null;
		final int endColon = imageName.lastIndexOf(':');
		if (endColon < 0) {
			imageTag=null;
		}else{
		  final String tag = imageName.substring(endColon + 1);
		  if (tag.indexOf('/') < 0) {
			  imageTag = tag;
		  }else{
			  imageTag = null;
		   }
		}
		return imageTag;
	}
	public void setuidHashmapComposite(String uidNumStr,String azureDetails){
		logger.debug("setuidHashmap Start");
		HashMap<String,String> singlatonMap=SingletonMapClass.getInstance();
		singlatonMap.put(uidNumStr, azureDetails);
		logger.debug("setuidHashmap End");
	}	
	
	public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		return client;
	}
	public void putContainerDetailsJSON(DockerInfoList  dockerList,String apiUrl) throws Exception{
		logger.debug("putContainerDetailsJSON Start");
		try {
			logger.debug("dockerList "+dockerList.toString()+"apiUrl "+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString(dockerList);
			logger.debug("dockerJson "+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
			  logger.error("putContainerDetailsJSON failed", e);
	          throw e;
		 }
		logger.debug("putContainerDetailsJSON End");
	}
	
	
	public void putContainerDetailsJSONProbe(DockerInfoList dockerList,String apiUrl)throws Exception{
		logger.debug("putContainerDetailsJSON Start");
		try {
			logger.debug("dockerList "+dockerList.toString()+"apiUrl "+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString(dockerList);
			logger.debug("dockerJson "+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
			  logger.error("putContainerDetailsJSONProbe failed", e);
	          throw e;
		 }
		logger.debug("putContainerDetailsJSON  End");
	}
	
	public void putBluePrintDetailsJSON(String  blueprintJson,String apiUrl)throws Exception{
		logger.debug("putBluePrintDetailsJSON Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			final String url = apiUrl;
			ObjectMapper mapper = new ObjectMapper();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			logger.debug("blueprintJson "+blueprintJson);
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpEntity<String> entity = new HttpEntity<String>(blueprintJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
			  logger.error("putBluePrintDetailsJSON failed", e);
			  throw e;
		 }
		logger.debug("putBluePrintDetailsJSON End");
	}
	
	public void putDataBrokerDetails(AzureDeployDataObject deployDataObject,String apiUrl)throws Exception{
		logger.debug("putDataBrokerDetails Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			logger.debug("UrlAttribute "+deployDataObject.getUrlAttribute());
			logger.debug("JsonMapping "+deployDataObject.getJsonMapping());
			logger.debug("JsonPosition "+deployDataObject.getJsonPosition());
			//logger.debug("=====dataBrokerScript==="+dataBrokerScript);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("jsonUrl", deployDataObject.getUrlAttribute());
			//map.add("jsonScript", dataBrokerScript);
			map.add("jsonMapping", deployDataObject.getJsonMapping());
			map.add("jsonPosition", deployDataObject.getJsonPosition());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			restTemplate.exchange(apiUrl, HttpMethod.PUT, request, String.class);
		    
		  } catch (Exception e) {
			  logger.error("generateNotification failed", e);
			  throw e;
		 }
		logger.debug("putDataBrokerDetails End");
	}
	public void createDeploymentCompositeData(String dataSource,String dataUserName,String dataPassword,List<AzureContainerBean> azureContainerBeanList,
			String solutionId,String solutionRevisionId,String userId,String uidNumber,String deploymentStatusCode) throws Exception{
		logger.debug("createDeploymentCompositeData start");
		logger.debug("solutionId "+solutionId);
		logger.debug("solutionRevisionId "+solutionRevisionId);
		logger.debug("userId "+userId);
		logger.debug("uidNumber "+uidNumber);
		logger.debug("deploymentStatusCode "+deploymentStatusCode);
		logger.debug("azureContainerBeanList "+azureContainerBeanList);
		ObjectMapper mapper = new ObjectMapper();
		CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
		if(solutionId!=null && solutionRevisionId!=null && userId!=null && uidNumber!=null){
			MLPSolutionDeployment mlp=new MLPSolutionDeployment();
			mlp.setSolutionId(solutionId);
			mlp.setUserId(userId);
			mlp.setRevisionId(solutionRevisionId);
			mlp.setDeploymentId(uidNumber);
			mlp.setDeploymentStatusCode(deploymentStatusCode);
			String azureDetails=mapper.writeValueAsString(azureContainerBeanList);
			mlp.setDetail(azureDetails);
			logger.debug("azureDetails "+azureDetails);
			MLPSolutionDeployment mlpDeployment=client.createSolutionDeployment(mlp);
			logger.debug("mlpDeployment "+mlpDeployment);
		}
		logger.debug("createDeploymentCompositeData End");
	}
	
	public String getDataBrokerPort(List<DeploymentBean> deploymentList, String dataBrokerName){
		logger.debug("getDataBrokerIP Start");
		String dataBrokerPort="";
		logger.debug("deploymentList "+deploymentList);
		logger.debug("dataBrokerName "+dataBrokerName);
		if(deploymentList!=null && deploymentList.size() > 0  && dataBrokerName!=null && !"".equals(dataBrokerName)){
			for(DeploymentBean bean:deploymentList){
				logger.debug("bean.NodeType() "+bean.getNodeType());
				logger.debug("bean.DataBrokerType() "+bean.getDataBrokerType());
				if(bean!=null && bean.getNodeType()!=null && bean.getNodeType().equalsIgnoreCase(dataBrokerName)
						&& !bean.getDataBrokerType().equalsIgnoreCase(AzureClientConstants.DATA_BROKER_CSV_FILE)){
					dataBrokerPort=bean.getContainerPort();
				}
			}
		}
		logger.debug("dataBrokerPort "+dataBrokerPort);
		logger.debug("End getDataBrokerIP");
		return dataBrokerPort;
	}
	public String getDataBrokerPortCSV(List<DeploymentBean> deploymentList, String dataBrokerName){
		logger.debug("getDataBrokerPortCSV Start");
		String dataBrokerPort="";
		logger.debug("deploymentList "+deploymentList);
		logger.debug("dataBrokerName"+dataBrokerName);
		if(deploymentList!=null && deploymentList.size() > 0  && dataBrokerName!=null && !"".equals(dataBrokerName)){
			for(DeploymentBean bean:deploymentList){
				logger.debug("bean.NodeType() "+bean.getNodeType());
				logger.debug("bean.DataBrokerType() "+bean.getDataBrokerType());
				if(bean!=null && bean.getNodeType()!=null && bean.getNodeType().equalsIgnoreCase(dataBrokerName)
						&& bean.getDataBrokerType()!=null && bean.getDataBrokerType().equalsIgnoreCase(AzureClientConstants.DATA_BROKER_CSV_FILE)){
					dataBrokerPort=bean.getContainerPort();
				}
			}
		}
		logger.debug("dataBrokerPort "+dataBrokerPort);
		logger.debug("getDataBrokerPortCSV End");
		return dataBrokerPort;
	}
	public String getDataBrokerScript(List<DeploymentBean> deploymentList, String dataBrokerName){
		logger.debug("getDataBrokerScript Start");
		String dataBrokerScript="";
		logger.debug("deploymentList "+deploymentList);
		logger.debug("dataBrokerName "+dataBrokerName);
		if(deploymentList!=null && deploymentList.size() > 0  && dataBrokerName!=null && !"".equals(dataBrokerName)){
			for(DeploymentBean bean:deploymentList){
				if(bean!=null && bean.getNodeType()!=null && bean.getNodeType().equalsIgnoreCase(dataBrokerName)){
					dataBrokerScript=bean.getScript();
				}
			}
		}
		logger.debug("dataBrokerScript "+dataBrokerScript);
		logger.debug("getDataBrokerScript End");
		return dataBrokerScript;
	}
	public void callCsvConfigDB(AzureDeployDataObject deployDataObject,String apiUrl,DataBrokerBean dataBrokerBean)throws Exception{
		logger.debug("callCsvConfigDB Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			final String url = apiUrl;
			if(deployDataObject!=null){
				dataBrokerBean.setUserName(deployDataObject.getUsername());
				dataBrokerBean.setPassword(deployDataObject.getPassword());
				dataBrokerBean.setHost(deployDataObject.getHost());
				dataBrokerBean.setPort(deployDataObject.getPort());
			}
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dataBrokerBeanJson=mapper.writeValueAsString(dataBrokerBean);
			logger.debug("dataBrokerBeanJson "+dataBrokerBeanJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dataBrokerBeanJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
			  logger.error("callCsvConfigDB failed", e);
			  throw e;
		 }
		logger.debug("callCsvConfigDB End");
	}

}
