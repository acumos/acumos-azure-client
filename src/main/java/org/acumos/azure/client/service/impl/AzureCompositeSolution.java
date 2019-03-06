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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.logging.ONAPLogDetails;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.ContainerInfo;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.AzureEncrypt;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.LoggerUtil;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

//import org.acumos.p

public class AzureCompositeSolution implements Runnable {
	
	
	Logger logger =LoggerFactory.getLogger(AzureCompositeSolution.class);	
	private Azure azure;
	private AzureDeployDataObject deployDataObject;
	private String dockerContainerPrefix;
	private String dockerUserName;
	private String dockerPd;
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
	private String dataPd;
	private String dockerVMUserName;
	private String dockerVMPd;
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
	private String nexusRegistyPd;
	private String nexusRegistyName;
	private String otherRegistyName;
	private String exposeDataBrokerPort;
	private String internalDataBrokerPort;
	private TransportBean tbean;
	
    public AzureCompositeSolution(){
    	
    }
	
    // Constructor of AzureCompositeSolution
	public AzureCompositeSolution(Azure azure,AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String dockerUserName,String dockerPd,
			String localEnvDockerHost,String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,String probeInternalPort,String probeName,
			String probeUser,String probePass,String networkSecurityGroup,HashMap<String,String> imageMap,LinkedList<String> sequenceList,String dockerRegistryName,Blueprint bluePrint,String uidNumStr,
			String dataSource,String dataUserName,String dataPd,String dockerVMUserName,String dockerVMPd,String solutionPort,HashMap<String,DeploymentBean> nodeTypeContainerMap,
			String bluePrintJsonStr, String probeNexusEndPoint,String subnet,String vnet,DataBrokerBean dataBrokerBean,
			String sleepTimeFirst,String sleepTimeSecond,String nexusRegistyUserName,String nexusRegistyPd,String nexusRegistyName,
			String otherRegistyName,String exposeDataBrokerPort,String internalDataBrokerPort,TransportBean tbean) {
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
	    this.imageMap=imageMap;
	    this.sequenceList=sequenceList;
	    this.bluePrint=bluePrint;
	    this.uidNumStr=uidNumStr;
	    
	    this.dataSource=dataSource;
	    this.dataUserName=dataUserName;
	    this.dataPd=dataPd;
	    this.dockerVMUserName=dockerVMUserName;
	    this.dockerVMPd=dockerVMPd;
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
		this.nexusRegistyPd = nexusRegistyPd;
		this.nexusRegistyName = nexusRegistyName;
		this.otherRegistyName = otherRegistyName;
		this.exposeDataBrokerPort = exposeDataBrokerPort;
		this.internalDataBrokerPort = internalDataBrokerPort;
		this.tbean = tbean;
	   }
	public void run() {
		
		logger.debug("AzureCompositeSolution Run Started ");
		logger.debug("azure "+azure);
		AzureBean azureBean=new AzureBean();
		ObjectMapper mapper = new ObjectMapper();
		LoggerUtil loggerUtil=new LoggerUtil();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		List<ContainerInfo> probeContainerBeanList=new ArrayList<ContainerInfo>();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		
		String probeIP = null;
  	    String probePort = null;
  	    AzureCommonUtil azureUtil=new AzureCommonUtil();
  	    AzureEncrypt azEncrypt=new AzureEncrypt();
  	    String azureEncPD="";
  	    String vmIP="";
		try{
			ONAPLogDetails.setMDCDetails(tbean.getRequestId(), tbean.getUserDetail());
			loggerUtil.printCompositeSolutionImplDetails(deployDataObject,dockerContainerPrefix,list,
					bluePrintName,uidNumStr,sequenceList,imageMap,solutionPort,nodeTypeContainerMap,
					bluePrintJsonStr,probeName,probeInternalPort,probeNexusEndPoint,sleepTimeFirst,
					sleepTimeSecond,nexusRegistyName,otherRegistyName,exposeDataBrokerPort,internalDataBrokerPort,tbean);
			dockerVMPd=azureUtil.getRandomPassword(10).toString();
			azureEncPD=azEncrypt.encrypt(dockerVMPd);
			logger.debug("azureEncPD "+azureEncPD);
			int sleepTimeFirstInt=Integer.parseInt(sleepTimeFirst);
			int sleepTimeSecondInt=Integer.parseInt(sleepTimeSecond);
			logger.debug("pushCompositeImage start");
			HashMap<String,String> containeDetailMap=new HashMap<String,String>();
			DockerInfoList  dockerList=new DockerInfoList();
	        final Region region = Region.US_EAST;// US_EAST is coming from Azure sdk libraries
	        final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();//"acrsample";
	        
	        Map<String,String> protoMap=new HashMap<String,String>();
	        if(tbean!=null && tbean.getProtoContainerMap()!=null && tbean.getProtoContainerMap().size() > 0){
	        	Iterator protoItr = tbean.getProtoContainerMap().entrySet().iterator();
			    while (protoItr.hasNext()) {
			        Map.Entry protoPair = (Map.Entry)protoItr.next();
			        if(protoPair!=null && protoPair.getKey()!=null && protoPair.getValue()!=null){
			        	logger.debug(protoPair.getKey() + " = " + protoPair.getValue());
			        	String containerName=(String)protoPair.getKey();
			        	String protoPath=(String)protoPair.getValue();
			        	ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(tbean.getNexusUrl(), tbean.getNexusUserName(),
			        			tbean.getNexusPd(), protoPath);
						logger.debug(protoPair.getKey() +"byteArrayOutputStream "+byteArrayOutputStream);
						protoMap.put(protoPath, byteArrayOutputStream.toString());
			        }
			    }
	        }
	        logger.debug("protoMap "+protoMap);
	        tbean.setProtoMap(protoMap);
	        String containerInstanceBluePrint="";
	        String containerInstanceprobe="";
	        logger.debug("list "+list);
	        logger.debug("sequenceList "+sequenceList);
	        logger.debug("bluePrintName "+bluePrintName);
	        int portIncrement=8557;
            if(list!=null && list.size() > 0){
            	
		            //Get the existing Azure registry using resourceGroupName and Acr Name
		            Registry azureRegistry = azure.containerRegistries().getByResourceGroup(deployDataObject.getRgName(), deployDataObject.getAcrName());
		            //Utils.print(azureRegistry);
	                //=============================================================
		            // Create a Docker client that will be used to push/pull images to/from the Azure Container Registry
		            RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
		            DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath,azureBean,
		                    networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPd,subnet,vnet,sleepTimeFirstInt);
		            
		            //=============================================================
		            // Pull a temp image from public Docker repo and create a temporary container from that image
		            // These steps can be replaced and instead build a custom image using a Dockerfile and the app's JAR
		            logger.debug("Start pulling images from nexus ");
		            Iterator itr=list.iterator();
		            while(itr.hasNext()){
		            	 
		            	String imageName=(String)itr.next();
		            	logger.debug("image name in run "+imageName);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		logger.debug("Inside BluePrint ");
		            		azureUtil.pullImageFromRepository(bluePrintUser,bluePrintPass,imageName,dockerClient);
		            	}else if(imageName!=null && imageName.contains(probeName)) {
		            		logger.debug("Inside Probe ");
		            		azureUtil.pullImageFromRepository(probeUser,probePass,imageName,dockerClient);
		            	}else{
		            	    logger.debug("image name in run else "+imageName);
	                        if(azureUtil.getRepositryStatus(imageName, nexusRegistyName)){
	                        	logger.debug("Repository Nexus");
	                        	azureUtil.pullImageFromRepository(nexusRegistyUserName,nexusRegistyUserName,imageName,dockerClient);
	                        }else if(imageName!=null && imageName.contains(AzureClientConstants.NGINX_IMAGE)){
	                        	logger.debug("Nginx images "+imageName);
	                        	azureUtil.pullImageFromRepository("","",imageName,dockerClient);
			            	}else{
			            		 logger.debug("other Registry");
			            		 azureUtil.pullImageFromRepository(dockerUserName,dockerPd,imageName,dockerClient);
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
		            		String tag=azureUtil.getTagFromImage(imageName);
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
		                    ,networkSecurityGroup,dockerRegistryPort,dockerRegistryName,dockerVMUserName,dockerVMPd,subnet,vnet,sleepTimeFirstInt);
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
	                  //Nginx mapping folder
	                  if(containerInstanceprobe!=null && !"".equals(containerInstanceprobe)){
	                	  DockerUtils.protoFileVM(azureBean.getAzureVMIP(), dockerVMUserName, dockerVMPd,tbean);
	                  }
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
			    		        		tbean.setVmIP(azureVMIP);
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
			    		        				probeNexusEndPoint="http://"+azureVMName+":"+tbean.getNginxPort();
			    		        			}else if(nodeTypeContainer!=null && !"".equals(nodeTypeContainer) && nodeTypeContainer.equalsIgnoreCase(AzureClientConstants.DATABROKER_NAME)
			    		        					&& nodeTypeName!=null && !"".equals(nodeTypeName) && nodeTypeName.equalsIgnoreCase(AzureClientConstants.DATA_BROKER_CSV_FILE)){
			    		        				portNumberString=exposeDataBrokerPort+":"+internalDataBrokerPort;
			    		        				portNumber=exposeDataBrokerPort;
			    		        			}else if(finalContainerName.equalsIgnoreCase(AzureClientConstants.NGINX_CONTAINER)){
			    		        				portNumber=String.valueOf(portIncrement);
			    		        				portNumberString=portNumber+":"+tbean.getNginxInternalPort();
			    		        				tbean.setNginxPort(portNumber);
			    		        				count++;
			    		        				portIncrement++;
			    		        			}else{
			    		        				portNumber=String.valueOf(portIncrement);
				    		        			if(solutionPort!=null && !"".equals(solutionPort)){
				    		        				portNumberString=portNumber+":"+solutionPort;
				    		        			}else{
				    		        				portNumberString=portNumber+":"+portNumber;
				    		        			}
				    		        			count++;
				    		        			portIncrement++;
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
			    		        		DockerUtils.deploymentCompositeImageVM(azureVMIP, dockerVMUserName, dockerVMPd, azureRegistry.loginServerUrl(),  acrCredentials.username(),
			    		        				acrCredentials.passwords().get(0).value(), repositoryName,finalContainerName,imageCount,
			    		        				portNumberString,probeNexusEndPoint,sleepTimeFirstInt,tbean);
			    		        		
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
          logger.debug(" JSON FILE FROM DESIGN STUDIO "+bluePrintJsonStr);
          logger.debug("azureDetails "+azureDetails);
          logger.debug("Dockerinfolist "+mapper.writeValueAsString(azureBean.getDockerinfolist()));
  		  logger.debug("bluePrint "+mapper.writeValueAsString(bluePrint));	
  		  DockerInfoList dockerInfoList=azureBean.getDockerinfolist();
		  vmIP=azureBean.getAzureVMIP().trim();
		  String bluePrintPort=azureBean.getBluePrintPort().trim();
		  String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/"+AzureClientConstants.PUT_DOCKER_INFO_URL;  
		  String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/"+AzureClientConstants.PUT_BLUEPRINT_INFO_URL;
		  logger.debug("urlDockerInfo "+urlDockerInfo+" urlBluePrint "+urlBluePrint);
		  String dataBrokerPort=azureUtil.getDataBrokerPort(deploymentList,AzureClientConstants.DATABROKER_NAME);
		  String urlDataBroker="http://"+vmIP+":"+dataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
		  String csvDataBrokerPort="";
		  String csvDataBrokerUrl="";
		  if(dataBrokerBean!=null){
			  csvDataBrokerPort=azureUtil.getDataBrokerPortCSV(deploymentList,AzureClientConstants.DATABROKER_NAME);
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
			  azureUtil.callCsvConfigDB(deployDataObject.getUsername(),deployDataObject.getUserPd(),deployDataObject.getHost(),deployDataObject.getPort(),csvDataBrokerUrl,dataBrokerBean);
			 }
		// putBlueprint
		 if(bluePrint!=null){
			 azureUtil.putBluePrintDetailsJSON(bluePrintJsonStr,urlBluePrint);
		  }
		// putDockerInfo
		 if(dockerList != null){
			  logger.debug("Inside probeContainerBeanList ");
			  azureUtil.putContainerDetailsJSONProbe(dockerList,urlDockerInfo);
			}
		 // configDB
		 if(dataBrokerPort!=null &&  !"".equals(dataBrokerPort)){
			 logger.debug("Inside putDataBrokerDetails ");
			 azureUtil.putDataBrokerDetails(deployDataObject.getUrlAttribute(),deployDataObject.getJsonMapping(),
					 deployDataObject.getJsonPosition(),urlDataBroker);
			}
		
		 
		 // Added notification for probe code
		 ArrayList<ProbeIndicator> probeIndicatorList = bluePrint.getProbeIndicator();
		 ProbeIndicator prbIndicator = null;
		 if(probeIndicatorList != null && probeIndicatorList.size() >0) {
				prbIndicator = probeIndicatorList.get(0);
		 }	
		 
		 if(vmIP!=null && !"".equals(vmIP)){
			 azureUtil.generateNotification("Composite Solution VM is created, IP is: "+vmIP+" Password: "+dockerVMPd, deployDataObject.getUserId(),
						dataSource, dataUserName, dataPd);
		 }
		 //if (bluePrint.getProbeIndocator() != null && bluePrint.getProbeIndocator().equalsIgnoreCase("True"))  {
		 if (bluePrint.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True"))  {
			 logger.debug("Probe indicator true. Starting generatenotircation deployDataObject.getUserId() "+deployDataObject.getUserId());
			 logger.debug(" probeIP "+probeIP+" probePort "+probePort);
			 azureUtil.generateNotification("Probe IP and Port: "+probeIP+":"+probePort,deployDataObject.getUserId(),dataSource, dataUserName, dataPd);
		 }
		 
		 if(azureContainerBeanList!=null){
   			logger.debug("Start saving data in database "); 
   			azureUtil.createDeploymentCompositeData(dataSource,dataUserName,dataPd,azureContainerBeanList,deployDataObject.getSolutionId(),
   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,AzureClientConstants.DEPLOYMENT_PROCESS);
         }
		}catch(Exception e){
			MDC.put("ClassName", "AzureCompositeSolution");
			logger.error("AzureCompositeSolution failed", e);
			MDC.remove("ClassName");
			if(vmIP!=null && !"".equals(vmIP)) {
				 logger.error("Azure VM IP is:"+vmIP+" Password: "+azureEncPD);
			 }
			try{
				azureUtil.generateNotification("Error in vm creation", deployDataObject.getUserId(),
						dataSource, dataUserName, dataPd);
				azureUtil.createDeploymentCompositeData(dataSource,dataUserName,dataPd,azureContainerBeanList,deployDataObject.getSolutionId(),
	   					  deployDataObject.getSolutionRevisionId(),deployDataObject.getUserId(),uidNumStr,AzureClientConstants.DEPLOYMENT_FAILED);
			}catch(Exception ex){
				logger.error("createDeploymentCompositeData failed", e);
			}
		}
		ONAPLogDetails.clearMDCDetails();
		logger.debug("AzureCompositeSolution Run End");
	}
	
 
}
