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
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
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
	
	
	

	public AzureCompositeSolution(Azure azure,AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String dockerUserName,String dockerPwd,
			String localEnvDockerHost,String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,String probeName,
			String probeUser,String ProbePass,String networkSecurityGroup,HashMap<String,String> imageMap,LinkedList<String> sequenceList,String dockerRegistryName,Blueprint bluePrint,String uidNumStr,
			String dataSource,String dataUserName,String dataPassword,String dockerVMUserName,String dockerVMPassword,String solutionPort,HashMap<String,DeploymentBean> nodeTypeContainerMap,String bluePrintJsonStr) {
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
	    
	   }
	public void run() {
		logger.debug("<-----------------AzureCompositeSolution-----Run Started-------------------------->");
		logger.debug("<-----------------AzureCompositeSolution-----Run Started---*************----------------------->");
		logger.debug("<-------azure-------->"+azure);
		logger.debug("<-------deployDataObject-------->"+deployDataObject);
		logger.debug("<-------dockerContainerPrefix-------->"+dockerContainerPrefix);
		logger.debug("<-------dockerUserName-------->"+dockerUserName);
		logger.debug("<-------dockerPwd-------->"+dockerPwd);
		logger.debug("<-------localEnvDockerHost-------->"+localEnvDockerHost);
		logger.debug("<-------localEnvDockerCertPath-------->"+localEnvDockerCertPath);
		logger.debug("<-------list-------->"+list);
		logger.debug("<-------bluePrintName-------->"+bluePrintName);
		logger.debug("<-------bluePrintUser-------->"+bluePrintUser);
		logger.debug("<-------bluePrintPass-------->"+bluePrintPass);
		logger.debug("<-------networkSecurityGroup-------->"+networkSecurityGroup);
		logger.debug("<-------dockerRegistryName-------->"+dockerRegistryName);
		logger.debug("<-------uidNumStr-------->"+uidNumStr);
		logger.debug("<-------sequenceList-------->"+sequenceList);
		logger.debug("<-------imageMap-------->"+imageMap);
		
		logger.debug("<-------dataSource-------->"+dataSource);
		logger.debug("<-------dataUserName-------->"+dataUserName);
		logger.debug("<-------dataPassword-------->"+dataPassword);
		logger.debug("<-------dockerVMUserName-------->"+dockerVMUserName);
		logger.debug("<-------dockerVMPassword-------->"+dockerVMPassword);
		logger.debug("<-------solutionPort-------->"+solutionPort);
		logger.debug("<-------nodeTypeContainerMap-------->"+nodeTypeContainerMap);
		logger.debug("<-------bluePrintJsonStr-------->"+bluePrintJsonStr);
		
		AzureBean azureBean=new AzureBean();
		ObjectMapper mapper = new ObjectMapper();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		List<ContainerInfo> probeContainerBeanList=new ArrayList<ContainerInfo>();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		
		String probeIP = null;
  	    String probePort = null;
		
		try{
			logger.debug("<-------------start pushCompositeImage------------------------------>");
		    if(dockerVMUserName!=null){
		    	dockerVMUserName=dockerVMUserName.trim();	
		    }
		    if(dockerVMPassword!=null){
		    	dockerVMPassword=dockerVMPassword.trim();	
		    }
		    logger.debug("<-------dockerVMUserName----Trim---->"+dockerVMUserName);
			logger.debug("<-------dockerVMPassword-----Trim--->"+dockerVMPassword);
			HashMap<String,String> containeDetailMap=new HashMap<String,String>();
			DockerInfoList  dockerList=new DockerInfoList();
			//final String saName = SdkContext.randomResourceName("sa", 20);	   
	        final Region region = Region.US_EAST;
	        final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();//"acrsample";
	      
	        String servicePrincipalClientId = deployDataObject.getClient(); // replace with a real service principal client id
	        String servicePrincipalSecret = deployDataObject.getKey(); // and corresponding secret
	        //HashMap<String,String> containerMap=new HashMap<String,String>();
	        String containerInstanceBluePrint="";
	        String containerInstanceprobe="";
	        //String bluePrintContainerId="";
	        
	        logger.debug("<--------------dockerRegistryName--------------------------->"+dockerRegistryName);
	        logger.debug("<--------------list--------------------------->"+list);
	        logger.debug("<--------------sequenceList--------------------------->"+sequenceList);
	        logger.debug("<---------bluePrintName------------->"+bluePrintName);
	        String portArr[]={"8556","8557","8558","8559","8560","8561","8562","8563","8564","8565"};
            if(list!=null && list.size() > 0){
            	
     	            //=============================================================
		            // If service principal client id and secret are not set via the local variables, attempt to read the service
		            //   principal client id and secret from a secondary ".azureauth" file set through an environment variable.
		            //
		            //   If the environment variable was not set then reuse the main service principal set for running this sample.
	
		            if (servicePrincipalClientId.isEmpty() || servicePrincipalSecret.isEmpty()) {
		                String envSecondaryServicePrincipal = System.getenv("AZURE_AUTH_LOCATION_2");
	
		                if (envSecondaryServicePrincipal == null || !envSecondaryServicePrincipal.isEmpty() || !Files.exists(Paths.get(envSecondaryServicePrincipal))) {
		                    envSecondaryServicePrincipal = System.getenv("AZURE_AUTH_LOCATION");
		                }
	
		                servicePrincipalClientId = Utils.getSecondaryServicePrincipalClientID(envSecondaryServicePrincipal);
		                servicePrincipalSecret = Utils.getSecondaryServicePrincipalSecret(envSecondaryServicePrincipal);
		            }
	
	
		            //=============================================================
		            // Create an SSH private/public key pair to be used when creating the container service
	
		            logger.debug("Creating an SSH private and public key pair");
	
		            SSHShell.SshPublicPrivateKey sshKeys = SSHShell.generateSSHKeys("", "ACS");
		            logger.debug("SSH private key value: \n" + sshKeys.getSshPrivateKey());
		            logger.debug("SSH public key value: \n" + sshKeys.getSshPublicKey());
	
	
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
		            logger.debug("azureRegistry.loginServerUrl="+azureRegistry.loginServerUrl()+ ", acrCredentials.username "+ acrCredentials.username()+ ", acrCredentials.passwords" + acrCredentials.passwords().get(0).value());
		            DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath,azureBean,
		                    networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPassword);
		            
		            AuthConfig authConfig = new AuthConfig()
		                    .withUsername(dockerUserName)
		                    .withPassword(dockerPwd);
		            
		            AuthConfig authConfig2 = new AuthConfig()
		                    .withUsername(bluePrintUser)
		                    .withPassword(bluePrintPass);
		            
		            AuthConfig authConfigProb = new AuthConfig()
		                    .withUsername(probeUser)
		                    .withPassword(probePass);
	
		            //=============================================================
		            // Pull a temp image from public Docker repo and create a temporary container from that image
		            // These steps can be replaced and instead build a custom image using a Dockerfile and the app's JAR
		            logger.debug("Start pulling images from nexus::::::::");
		            Iterator itr=list.iterator();
		            while(itr.hasNext()){
		            	 
		            	String imageName=(String)itr.next();
		            	logger.debug("image name in run -->"+imageName);
		            	//logger.debug("Nexus Image Name------------------->"+imageName);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		logger.debug("Pulling image start logging in-->"+imageName);
		            		dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig2)
		                    //.withTag(dockerImageTag)
		                    .exec(new PullImageResultCallback())
		                    .awaitSuccess();
		            		
		            	}else if(imageName!=null && imageName.contains(probeName)) {
		            		logger.debug("Pulling image start logging in and pulling image-->"+imageName);
		            		dockerClient.pullImageCmd(imageName).withAuthConfig(authConfigProb)
		                    //.withTag(dockerImageTag)
		                    .exec(new PullImageResultCallback())
		                    .awaitSuccess();
		            		
		            	}
		            	{
		            	dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
	                    //.withTag(dockerImageTag)
	                    .exec(new PullImageResultCallback())
	                    .awaitSuccess();
		            	}
		            	Thread.sleep(50000);
		            }
		            
		            
		            logger.debug("List local Docker images:");
		            List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();
		            //for (Image image : images) {
		            	//logger.debug("Docker Images \n"+ image.getRepoTags()[0]+"<----image.getId()--->"+ image.getId());
		            //}
		            int dockerCount=1;
		            HashMap<String,CreateContainerResponse> hmap=new HashMap<String,CreateContainerResponse>();
		            HashMap<String,String> containerTagMap=new HashMap<String,String>();
		            HashMap<String,String> containerImageMap=new HashMap<String,String>();
		            //HashMap<String,String> containerInstanceImageMap=new HashMap<String,String>();
		            Iterator itr1=list.iterator();
		            while(itr1.hasNext()){
		            	String imageTagVal="";
		            	String imageName=(String)itr1.next();
		            	CreateContainerResponse dockerContainerInstance = dockerClient.createContainerCmd(imageName)
			                    .withName(dockerContainerName+"_"+dockerCount)
			                    //.withCmd("/hello")
			                    .exec();
		            	/*try{
		            		logger.debug("<----Start Inspection-----imageName---->"+imageName);
		            		InspectContainerResponse inspectContainer=dockerClient.inspectContainerCmd(dockerContainerInstance.getId()).exec();
			            	if(inspectContainer.getConfig()!=null ){
			            		String ipNumberrr=inspectContainer.getNetworkSettings().getIpAddress();
			            		logger.debug("<----ipNumberrr--------->"+ipNumberrr);
			            		//logger.debug("<----remoteDockerContainerInstance------1--->"+inspectContainer.getNetworkSettings().);
			            		logger.debug("<----remoteDockerContainerInstance----2----->"+inspectContainer.getConfig().getExposedPorts());
			            		int len=inspectContainer.getConfig().getExposedPorts().length;
			            		for(int m=0;m<len;m++){
			            			logger.debug("<----m---->"+inspectContainer.getConfig().getExposedPorts()[m]);
			            		}
			            	}
			            	logger.debug("<----end Inspection-----imageName---->"+imageName);
		            	}catch(Exception exce){
		            		logger.debug("Exception in inspection================ "+exce.getMessage());
		            	}*/
		            	
		            	if(imageName!=null && !"".equals(imageName)){
		            		String tag=getTagFromImage(imageName);
		            		if(tag!=null){
		            			imageTagVal=tag;
		            		}
		            	}
		            	logger.debug("===imageName======="+imageName+"========imageTagVal===="+imageTagVal);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		containerInstanceBluePrint=dockerContainerName+"_"+dockerCount;
		            	}
		            	
		            	if(imageName!=null && imageName.contains(probeName)){
		            		logger.debug("containerInstanceprobe===>"+probeName);
		            		containerInstanceprobe=dockerContainerName+"_"+dockerCount;
		            	}
		            	
		            	hmap.put(dockerContainerName+"_"+dockerCount, dockerContainerInstance);
		            	containerTagMap.put(dockerContainerName+"_"+dockerCount, imageTagVal);
		            	containerImageMap.put(dockerContainerName+"_"+dockerCount, imageName);
		            	Thread.sleep(30000);
		            	dockerCount++;
		            }
		            logger.debug("=======containerImageMap====="+containerImageMap);
		            logger.debug("List All Docker containers:");
		            List<Container> dockerContainers = dockerClient.listContainersCmd()
		                    .withShowAll(true)
		                    .exec();
		            for (Container container : dockerContainers) {
		            	logger.debug("All Docker container with images and Name %s (%s)\n"+container.getImage()+"<-----container.getId()----->"+container.getId());
		            }
	
		            //=============================================================
		            // Commit the new container
	               //String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" + dockerContainerName;
		          //logger.debug("privateRepoUrl::::::::::::::::::"+privateRepoUrl);
		            HashMap<String,String> repoUrlMap=new HashMap<String,String>();  
		            Iterator itrContainer=hmap.entrySet().iterator();
		            while(itrContainer.hasNext()){
		            	String imageTagLatest="latest";
		            	Map.Entry pair = (Map.Entry)itrContainer.next();
		            	String containerName=(String)pair.getKey();
		            	CreateContainerResponse dockerContainerInstance=(CreateContainerResponse)pair.getValue();
		            	
		            	String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" + containerName;
		            	logger.debug("dockerContainerInstance.getId():::::::::::::::::"+dockerContainerInstance.getId()+"===privateRepoUrl===="+privateRepoUrl);
		            	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
		            		imageTagLatest=containerTagMap.get(containerName);
		            	}
		            	logger.debug("containerName======"+containerName+"==imageTagLatest======"+imageTagLatest);
			            String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
			                    .withRepository(privateRepoUrl)
			                    .withTag(imageTagLatest).exec();
			            logger.debug("dockerImageId::::::::::::::::::"+dockerImageId);
			            repoUrlMap.put(containerName, privateRepoUrl);
			            // We can now remove the temporary container instance
			            dockerClient.removeContainerCmd(dockerContainerInstance.getId())
			                    .withForce(true)
			                    .exec();
			            Thread.sleep(5000);
		            }
		            //#####################################################################################
		            logger.debug("<----Before Docker remoteDockerClient--------------------------->");
		            DockerClient remoteDockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), null, localEnvDockerCertPath,azureBean
		                    ,networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPassword);
		            logger.debug("<----After Docker remoteDockerClient--------------------------->");
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
		            	Thread.sleep(50000);
		            }
		            
		            logger.debug("<----Pushed Images to privaterepourl and removing imgage from local docker host---------->");
		            // Remove the temp image from the local Docker host
		            /*try {
		            	Iterator itr5=list.iterator();
			            while(itr5.hasNext()){
			            	String imageName=(String)itr5.next();
		                    dockerClient.removeImageCmd(imageName).withForce(true).exec();
			            }
		            } catch (NotFoundException e) {
		            	logger.error("Error in removing images "+e.getMessage());
		            }*/
	
		            //=============================================================
		            // Verify that the image we saved in the Azure Container registry can be pulled and instantiated locally
		            logger.debug("<----pull images from Azure registry to locally--------->");
		             repoContainer=repoUrlMap.entrySet().iterator();
		            while(repoContainer.hasNext()){
		            	Map.Entry pair = (Map.Entry)repoContainer.next();
		            	String containerName=(String)pair.getKey();
		            	String privateRepoUrl=(String)pair.getValue();
		            	 logger.debug("<----pull images from Azure registry to locally-----privateRepoUrl---->"+privateRepoUrl);
		            	dockerClient.pullImageCmd(privateRepoUrl)
	                    .withAuthConfig(dockerClient.authConfig())
	                    .exec(new PullImageResultCallback()).awaitSuccess();
		            	Thread.sleep(50000);
		            }
		            
		            logger.debug("List local Docker images after pulling sample image from the Azure Container Registry:");
		            images = dockerClient.listImagesCmd()
		                    .withShowAll(true)
		                    .exec();
		            /*for (Image image : images) {
		            	logger.debug("List Image after pulling locally \n"+ image.getRepoTags()[0]+"<-------ImageId--------->"+ image.getId());
		            }*/
		            
	                  logger.debug("<----remoteDockerClient with privateRepoUrl--------->");
	                  int imageCount=1;
	                  int remoteCount=1;
	                  int count=0;
	                  List<DockerInfo> dockerInfoList=new ArrayList<DockerInfo>();
	                  if(sequenceList!=null && sequenceList.size() > 0){
			            	Iterator seqItr = sequenceList.iterator();
			                while (seqItr.hasNext()) {
			                    String jsonContainerName=(String)seqItr.next(); 
			                    logger.debug("<----jsonContainerName--------->"+jsonContainerName);
			                    if(jsonContainerName!=null && !"".equals(jsonContainerName)){
			                    	
			                    	repoContainer=repoUrlMap.entrySet().iterator();
			    		            while(repoContainer.hasNext()){
			    		            	Map.Entry pair = (Map.Entry)repoContainer.next();
			    		            	String containerName=(String)pair.getKey();
			    		            	String privateRepoUrl=(String)pair.getValue();
			    		            	String tagImage="latest";
			    		            	String imageName="";
			    		            	DockerInfo dockerinfo=new DockerInfo();
			    		            	String finalContainerName=dockerContainerName + "-private_"+remoteCount;
			    		            	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
			    		            		tagImage=containerTagMap.get(containerName);
			    		            	}
			    		            	logger.debug("<----tagImage------------------>"+tagImage);
			    		            	if(containerImageMap!=null && containerImageMap.get(containerName)!=null){
			    		            		imageName=containerImageMap.get(containerName);
			    		            		logger.debug("<----imageName--------->"+imageName+"====imageMap=="+imageMap);
			    		            		if(imageName!=null && imageMap!=null && imageMap.get(imageName)!=null){
			    		            			finalContainerName=imageMap.get(imageName);
			    		            		}
			    		            	}
			    		            	logger.debug("<--Before--jsonContainerName--------->"+jsonContainerName+"===jsonContainerName==="+jsonContainerName);
			    		            	if(finalContainerName!=null && !finalContainerName.equalsIgnoreCase(jsonContainerName)){
			    		            		 logger.debug("Continue.............................................");
			    		            		continue;
			    		            	}
			    		            	String nodeTypeContainer="";
			    		            	if(nodeTypeContainerMap!=null && nodeTypeContainerMap.size() > 0 && nodeTypeContainerMap.get(finalContainerName)!=null){
			    		            		DeploymentBean dBean=nodeTypeContainerMap.get(finalContainerName);
			    		            		if(dBean!=null && dBean.getScript()!=null){
			    		            			nodeTypeContainer=dBean.getScript();
			    		            		}
			    		            		
			    		            	}
			    		            	String azureVMIP=azureBean.getAzureVMIP();
			    		            	String azureVMName=azureBean.getAzureVMName();
			    		            	//final String vmUserName="dockerUser";
			    		        		//final String vmPassword="12NewPA$$w0rd!";
			    		        		final String vmUserName=dockerVMUserName;
			    		        		final String vmPassword=dockerVMPassword;
			    		        		String repositoryName="";
			    		        		repositoryName=privateRepoUrl+":"+tagImage;
			    		        		String portNumber="";
			    		        		String portNumberString="";
			    		        		logger.debug("====azureVMName======: " + azureVMName);
			    		        		logger.debug("====azureVMIP======: " + azureVMIP);
			    		        		logger.debug("====vmUserName======: " + vmUserName);
			    		        		logger.debug("====vmPassword======: " + vmPassword);
			    		        		logger.debug("====registryServerUrl======: " + azureRegistry.loginServerUrl());
			    		        		logger.debug("====username======: " + acrCredentials.username());
			    		        		logger.debug("====password======: " + acrCredentials.passwords().get(0).value());
			    		        		logger.debug("====repositoryName======: " + repositoryName);
			    		        		logger.debug("====finalContainerName======: " + finalContainerName);
			    		        		logger.debug("====imageCount======: " + imageCount);
			    		        		logger.debug("====nodeTypeContainer======: " + nodeTypeContainer);
			    		        		if(containerInstanceBluePrint!=null && containerInstanceBluePrint.equalsIgnoreCase(containerName)){
			    		        			logger.debug("<--if Part--containerInstanceBluePrint--------->"+containerInstanceBluePrint+"=====containerName==="+containerName);
			    		        			portNumber="8555";
			    		        			azureBean.setBluePrintIp(azureVMIP);
			            			        azureBean.setBluePrintPort(portNumber);
			            			        portNumberString=portNumber+":"+portNumber;
			    		        		}else{
			    		        			portNumber=portArr[count];
			    		        			if(solutionPort!=null && !"".equals(solutionPort)){
			    		        				portNumberString=portNumber+":"+solutionPort;
			    		        			}else{
			    		        				portNumberString=portNumber+":"+portNumber;
			    		        			}
			    		        			count++;
			    		        			
			    		        		}
			    		        		imageCount++;
			    		        		dockerinfo.setIpAddress(azureVMName);
		            		            dockerinfo.setPort(portNumber);
		            		            dockerinfo.setContainer(finalContainerName);
		            		            dockerInfoList.add(dockerinfo);
		            		            logger.debug("====portNumberString======: " + portNumberString);
		            		            logger.debug("====Start Deploying=====================repositoryName=======: "+repositoryName);
			    		        		DockerUtils.deploymentCompositeImageVM(azureVMIP, vmUserName, vmPassword, azureRegistry.loginServerUrl(),  acrCredentials.username(),
			    		        				acrCredentials.passwords().get(0).value(), repositoryName,finalContainerName,imageCount,portNumberString);
			    		        		
			    		        		AzureContainerBean containerBean=new AzureContainerBean();
			    		        		containerBean.setContainerName(finalContainerName);
			    		        		containerBean.setContainerIp(azureVMIP);
			    		        		containerBean.setContainerPort(portNumber);
			    		        		
			    		        		DeploymentBean deploymentBean=new DeploymentBean();
			    		        		deploymentBean.setAzureVMIP(azureVMIP);
			    		        		deploymentBean.setAzureVMName(azureVMName);
			    		        		deploymentBean.setContainerName(finalContainerName);
			    		        		deploymentBean.setContainerPort(portNumber);
			    		        		deploymentBean.setNodeType(nodeTypeContainer);
			    		        		deploymentList.add(deploymentBean);
			    		        		
			    		        		
			    		        		ContainerInfo containerInfo = new ContainerInfo();
			    		        		containerInfo.setContainerName(finalContainerName);
			    		        		containerInfo.setContainerIp(azureVMName);
			    		        		containerInfo.setContainerPort(portNumber);
			    		        		containerInfo.setNodeType(nodeTypeContainer);
			    		        		if(containerInstanceprobe != null && !containerInstanceprobe.equals("")) {
				    		        	   containerInfo.setNodeType("Probe");
				    		        	   probeIP = azureVMIP;
				    		        	   probePort = portNumber;
			    		        		}

			    		        		probeContainerBeanList.add(containerInfo);
			    		        		
			    		        		azureContainerBeanList.add(containerBean);
			    		            }
			                    	
			                    }
			                }
	                  }  
	                  logger.debug("====dockerInfoList======: " + dockerInfoList);
	                  if(dockerInfoList!=null && dockerInfoList.size() > 0){
			            	dockerList.setDockerList(dockerInfoList);
			            }
	                  logger.debug("containeDetailMap==========>"+containeDetailMap+"=====dockerList====="+dockerList);
	  	              azureBean.setDockerinfolist(dockerList);	
		            
			}
          
          String azureDetails=mapper.writeValueAsString(azureBean.getDockerinfolist());  
          setuidHashmapComposite(uidNumStr,azureDetails);
          logger.debug("azureDetails=============="+azureDetails);
          logger.debug("Dockerinfolist=============="+mapper.writeValueAsString(azureBean.getDockerinfolist()));
  		  logger.debug("bluePrint==================="+mapper.writeValueAsString(bluePrint));	
  		  DockerInfoList dockerInfoList=azureBean.getDockerinfolist();
		  String vmIP=azureBean.getAzureVMIP().trim();
		  String bluePrintPort=azureBean.getBluePrintPort().trim();
		  String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
		  String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
		  logger.debug("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
		  String dataBrokerPort=getDataBrokerPort(deploymentList,"DataBroker");
		  String dataBrokerScript=getDataBrokerScript(deploymentList,"DataBroker");
		  String urlDataBroker="http://"+vmIP+":"+dataBrokerPort+"/configDB";
		  
		  
		  // Added for probe
		  if(probeContainerBeanList != null && !probeContainerBeanList.isEmpty()){
			  logger.debug("Inside probeContainerBeanList ==> ");
			  putContainerDetailsJSONProbe(probeContainerBeanList,urlDockerInfo);
			}
		  
		 if(bluePrint!=null){
			 putBluePrintDetailsJSON(bluePrint,urlBluePrint);
		  }
		 if(urlDataBroker!=null){
			  putDataBrokerDetails(deployDataObject,urlDataBroker,dataBrokerScript);
			}
		 
		 // Added notification for probe code
		 if (bluePrint.getProbeIndocator() != null && !bluePrint.getProbeIndocator().equalsIgnoreCase("True"))  {
			 logger.debug("Probe indicator true. Starting generatenotircation==>");
			 generateNotification(probeIP+":"+probePort,deployDataObject.getUserId());
		 }
		 
		
		 if(azureContainerBeanList!=null){
       	  
   			  logger.debug("Start saving data in database=============="); 
   			createDeploymentCompositeData(dataSource,dataUserName,dataPassword,azureContainerBeanList,deployDataObject.getSolutionId(),
   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,"DP");
       		  
         }
		}catch(Exception e){
			logger.error("Error in AzureCompositeSolution===========" +e.getMessage());
			try{
				createDeploymentCompositeData(dataSource,dataUserName,dataPassword,azureContainerBeanList,deployDataObject.getSolutionId(),
	   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,"FA");
			}catch(Exception ex){
				logger.error("Error in saving data===========" +ex.getMessage());
			}
			e.printStackTrace();
		}
		 
		logger.debug("<-----------------AzureCompositeSolution-----Run end-------------------------->");
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
         logger.debug( "createNotification`");
         CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
         MLNotification mlNotification = Utils.convertToMLNotification(client.createNotification(mlpNotification));
         return mlNotification;
	 }
	 
	/**
	  * 
	  * @param msg
	  * @param userId
	  */
	 void generateNotification(String msg, String userId) {
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
                     MLNotification mLNotification = createNotification(notification);
                     client.addUserToNotification(mLNotification.getNotificationId(),userId);
             }
         } catch (Exception e) {
                         logger.error("Exception Occurred while getNotifications", e);
         }
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
	
	/*public void putContainerDetails(DockerInfoList  dockerList,String apiUrl){
		logger.debug("<--------Start---putContainerDetails------->");
		try {
			logger.debug("<----dockerList---------->"+dockerList.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<DockerInfoList> entity = new HttpEntity<DockerInfoList>(dockerList);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.debug("<--------End---putContainerDetails------->");
	}
	public void putBluePrintDetails(Blueprint  bluePrint,String apiUrl){
		logger.debug("<--------Start---putContainerDetails------->");
		try {
			logger.debug("<----bluePrint---------->"+bluePrint.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpEntity<Blueprint> entity = new HttpEntity<Blueprint>(bluePrint);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
             e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.debug("<--------End---putContainerDetails------->");
	}*/
	public void setuidHashmapComposite(String uidNumStr,String azureDetails){
		logger.debug("<---------------setuidHashmap-------Run Start-------------------------->"+azureDetails+"====="+uidNumStr);
		HashMap<String,String> singlatonMap=SingletonMapClass.getInstance();
		singlatonMap.put(uidNumStr, azureDetails);
		logger.debug("<---------------setuidHashmap-------Run End-------------------------->"+singlatonMap);
	}	
	
	public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		return client;
	}
	public void putContainerDetailsJSON(DockerInfoList  dockerList,String apiUrl){
		logger.debug("<--------Start---putContainerDetailsJSON------->");
		try {
			logger.debug("<----dockerList---------->"+dockerList.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString(dockerList);
			logger.debug("<----dockerJson---------->"+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.debug("<--------End---putContainerDetailsJSON------->");
	}
	
	
	public void putContainerDetailsJSONProbe(List<ContainerInfo> dockerList,String apiUrl){
		logger.debug("<--------Start---putContainerDetailsJSON------->");
		try {
			logger.debug("<----dockerList---------->"+dockerList.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString(dockerList);
			logger.debug("<----dockerJson---------->"+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.debug("<--------End---putContainerDetailsJSON------->");
	}
	
	public void putBluePrintDetailsJSON(Blueprint  bluePrint,String apiUrl){
		logger.debug("<--------Start---putBluePrintDetailsJSON------->");
		try {
			logger.debug("<----bluePrint---------->"+bluePrint.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			ObjectMapper mapper = new ObjectMapper();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String blueprintJson=mapper.writeValueAsString(bluePrint); 
			logger.debug("<----blueprintJson---------->"+blueprintJson);
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpEntity<String> entity = new HttpEntity<String>(blueprintJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            logger.error("<---------Exception----------->"+e.getMessage());
            e.printStackTrace();
		 }
		logger.debug("<--------End---putBluePrintDetailsJSON------->");
	}
	
	public void putDataBrokerDetails(AzureDeployDataObject deployDataObject,String apiUrl,String dataBrokerScript){
		logger.debug("<--------Start---putDataBrokerDetails------->");
		try {
			logger.debug("======apiUrl==="+apiUrl);
			logger.debug("====UrlAttribute==="+deployDataObject.getUrlAttribute());
			logger.debug("=====JsonMapping==="+deployDataObject.getJsonMapping());
			logger.debug("=====JsonPosition==="+deployDataObject.getJsonPosition());
			logger.debug("=====dataBrokerScript==="+dataBrokerScript);
			/*final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString("");
			logger.debug("<----dockerJson---------->"+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson,headers);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);*/
		    
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.debug("<--------End---putDataBrokerDetails------->");
	}
	public void createDeploymentCompositeData(String dataSource,String dataUserName,String dataPassword,List<AzureContainerBean> azureContainerBeanList,
			String solutionId,String solutionRevisionId,String userId,String uidNumber,String deploymentStatusCode) throws Exception{
		logger.debug("<---------Start createDeploymentCompositeData ------------------------->");
		logger.debug("<---------dataSource-------->"+dataSource);
		logger.debug("<-------dataUserName-------------->"+dataUserName);
		logger.debug("<--------dataPassword------------->"+dataPassword);
		logger.debug("<---------solutionId------------------->"+solutionId);
		logger.debug("<--------solutionRevisionId-------------------->"+solutionRevisionId);
		logger.debug("<------userId--------------->"+userId);
		logger.debug("<------uidNumber--------------->"+uidNumber);
		logger.debug("<------deploymentStatusCode--------------->"+deploymentStatusCode);
		logger.debug("<------azureContainerBeanList--------------->"+azureContainerBeanList);
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
			logger.debug("<---------azureDetails------------------------->"+azureDetails);
			MLPSolutionDeployment mlpDeployment=client.createSolutionDeployment(mlp);
			logger.debug("<---------mlpDeployment------------------------->"+mlpDeployment);
		}
		logger.debug("<---------End createDeploymentCompositeData ------------------------->");
	}
	
	public String getDataBrokerPort(List<DeploymentBean> deploymentList, String dataBrokerName){
		logger.debug("<---------Start getDataBrokerIP ------------------------->");
		String dataBrokerPort="";
		logger.debug("<---deploymentList---->"+deploymentList);
		logger.debug("<---dataBrokerName---->"+dataBrokerName);
		if(deploymentList!=null && deploymentList.size() > 0  && dataBrokerName!=null && !"".equals(dataBrokerName)){
			for(DeploymentBean bean:deploymentList){
				if(bean!=null && bean.getNodeType()!=null && bean.getNodeType().equalsIgnoreCase(dataBrokerName)){
					dataBrokerPort=bean.getContainerPort();
				}
			}
		}
		logger.debug("<---------End getDataBrokerIP -----------------dataBrokerPort-------->"+dataBrokerPort);
		return dataBrokerPort;
	}
	public String getDataBrokerScript(List<DeploymentBean> deploymentList, String dataBrokerName){
		logger.debug("<---------Start getDataBrokerScript ------------------------->");
		String dataBrokerScript="";
		logger.debug("<---deploymentList---->"+deploymentList);
		logger.debug("<---dataBrokerName---->"+dataBrokerName);
		if(deploymentList!=null && deploymentList.size() > 0  && dataBrokerName!=null && !"".equals(dataBrokerName)){
			for(DeploymentBean bean:deploymentList){
				if(bean!=null && bean.getNodeType()!=null && bean.getNodeType().equalsIgnoreCase(dataBrokerName)){
					dataBrokerScript=bean.getScript();
				}
			}
		}
		logger.debug("<---------End getDataBrokerScript -----------------dataBrokerPort-------->"+dataBrokerScript);
		return dataBrokerScript;
	}

}
