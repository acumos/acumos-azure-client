package org.acumos.azure.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.logging.LogConfig;
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.acumos.azure.client.utils.SSHShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;

public class AzureSolutionDeployment implements Runnable{
	Logger logger = LoggerFactory.getLogger(AzureSolutionDeployment.class);
	
	private SolutionDeployment solutionBean;
	private TransportBean tbean;
	private Azure azure;
	public AzureSolutionDeployment() {
			}
	public AzureSolutionDeployment(SolutionDeployment solutionBean,TransportBean tbean,Azure azure) {
		this.solutionBean=solutionBean;
		this.tbean=tbean;
		this.azure=azure;
	}
	public void run() {
		logger.debug("AzureSolutionDeployment run start ");
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			String solutionToolKitType=azureUtil.getSolutionCode(solutionBean.getSolutionId(), 
					tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
			logger.debug("solutionToolKitType "+solutionToolKitType);
			String checkSoftware=checkPrerequisites(solutionBean,azureUtil);
			logger.debug("checkSoftware "+checkSoftware);
			if(checkSoftware!=null && "success".equalsIgnoreCase(checkSoftware)) {
				String removeContainer=removeAllContainer(solutionBean);
				logger.debug("removeContainer "+removeContainer);
				if(removeContainer!=null && "success".equalsIgnoreCase(removeContainer) ) {
					if(solutionToolKitType!=null && !"".equals(solutionToolKitType) && "CP".equalsIgnoreCase(solutionToolKitType)){
				   		logger.debug("Composite Solution Details Start");
				   		compositeSolutionDetails(solutionBean,tbean);
				   		logger.debug("Composite Solution Details End");
				   	 }else{
				   		logger.debug("Single Solution Details Start");
				   		singleSolutionDetails(solutionBean, tbean);
				   		logger.debug("Single Solution Details End");
				   	 }
				}
			}
		   	
		 }catch(Exception e) {
			 logger.error("AzureSolutionDeployment failed", e);
			 LogConfig.clearMDCDetails();
		}
		LogConfig.clearMDCDetails();
		logger.debug("AzureSolutionDeployment run End ");
	}
	public void singleSolutionDetails(SolutionDeployment solutionBean,TransportBean tbean)throws Exception{
		 logger.debug("singleSolutionDetails Start");
		 AzureCommonUtil azureUtil=new AzureCommonUtil();
		 AzureContainerBean containerBean = new AzureContainerBean();
		 String regUserName="";
		 String regPass="";
		 ArrayList<String> list=new ArrayList<String>();
		 String privateRepoUrl="";
		 String imageTag="";
		 try {
			 final String dockerContainerName = AzureClientConstants.DOCKER_CONTAINER_PREFIX + System.currentTimeMillis();//"acrsample";
			 logger.debug("dockerContainerName "+dockerContainerName);
			 String image=azureUtil.getSingleImageData(solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					 tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
	         logger.debug("imageName "+image);
	         list.add(image);
			 //Get the existing Azure registry using resourceGroupName and Acr Name
			 Registry azureRegistry = azure.containerRegistries().getByResourceGroup(solutionBean.getRgName(), solutionBean.getAcrName());
			 RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
			 DockerClient dockerClient = DockerUtils.createDockerClient(azure, solutionBean.getRgName(), null,
	                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), 
	                    tbean.getLocalHostEnv(), "",null,"","","","","","","",0);
			 logger.debug("Local Docker client object created");
			 if(dockerClient == null) {
				 logger.debug("Docker client null");
			  }
			 logger.debug("list "+list);
			 Iterator imageItr=list.iterator();
	         CreateContainerResponse dockerContainerInstance=null;
	         String containerCountName="";
	         imageTag=AzureClientConstants.IMAGE_TAG_LATEST;
	         if(image!=null && !"".equals(image)){
         		String tag=azureUtil.getTagFromImage(image);
         		if(tag!=null){
         			imageTag=tag;
         		}
         	}
         	logger.debug("imageTag "+imageTag);
         	
	         int dockerCount=0;
	         while(imageItr.hasNext()){
	        	 String imageName = (String) imageItr.next();
				if(azureUtil.getRepositryStatus(imageName, tbean.getNexusRegistyName())){
            		logger.debug(" Other Nexus Registry ");
            		azureUtil.pullImageFromRepository(tbean.getNexusRegistyUserName(),tbean.getNexusRegistyPd(),imageName,dockerClient);
        		  }else {
        		   logger.debug(" Other Registry ");
		           azureUtil.pullImageFromRepository(tbean.getRegistryUserName(),tbean.getRegistryPd(),imageName,dockerClient);
        		 }
				logger.debug("imageTag "+imageTag);
            	dockerCount=dockerCount+1;
            	containerCountName=dockerContainerName+"_"+dockerCount;
            	logger.debug("containerCountName "+containerCountName);
            	dockerContainerInstance = dockerClient.createContainerCmd(imageName).withName(containerCountName).exec();
            	privateRepoUrl = azureRegistry.loginServerUrl() + AzureClientConstants.PRIVATE_REPO_PREFIX + containerCountName;
            	logger.debug("privateRepoUrl "+privateRepoUrl);
            	String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId()).withRepository(privateRepoUrl)
	                                   .withTag(imageTag).exec();
            	logger.debug("dockerImageId "+dockerImageId);
            	dockerClient.removeContainerCmd(dockerContainerInstance.getId()).withForce(true).exec();
            	Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
            	//Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
            	logger.debug("Start pushing image to private repo ");
            	dockerClient.pushImageCmd(privateRepoUrl).withAuthConfig(dockerClient.authConfig())
                    .exec(new PushImageResultCallback()).awaitSuccess();
            	Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
			  }
			 
	         String repositoryName = "";
			 repositoryName = privateRepoUrl + ":" + imageTag;
			 logger.debug("repositoryName "+repositoryName);
			 logger.debug("regUserName "+regUserName+ " regPass "+regPass); 
			 String portNumberString="8557"+":"+tbean.getSolutionPort();
			 logger.debug("portNumberString "+portNumberString); 
			 DockerUtils.deploymentImageVM(solutionBean.getVmHostIP(),solutionBean.getVmUserName(), solutionBean.getVmUserPd(), 
					 azureRegistry.loginServerUrl(),acrCredentials.username(),acrCredentials.passwords().get(0).value(), 
					 repositoryName,portNumberString,Integer.parseInt(tbean.getSleepTimeFirst()));
			 
			 containerBean.setContainerIp(solutionBean.getVmHostIP());
			 containerBean.setContainerPort("8557");
			 containerBean.setContainerName("ContainerOne");
			 azureUtil.generateNotification("Single Solution Deployed, IP is: "+solutionBean.getVmHostIP(), solutionBean.getUserId(),
					 tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
			 azureUtil.createDeploymentData(tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(), containerBean,
					 solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					 solutionBean.getUserId(), tbean.getUidNumStr(), AzureClientConstants.DEPLOYMENT_PROCESS);
		 } catch (Exception e) {
			 logger.error("AzureSimpleSolution  for existing vm failed", e.getMessage());
			 try{
					azureUtil.generateNotification("Error in vm creation", solutionBean.getUserId(),
							tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
					azureUtil.createDeploymentData(tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(), containerBean,
							solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
							solutionBean.getUserId(), tbean.getUidNumStr(), AzureClientConstants.DEPLOYMENT_FAILED);
				}catch(Exception ex){
					logger.error("createDeploymentData failed", e.getMessage());
				}
		 }
		logger.debug("singleSolutionDetails End");
	}
	public void compositeSolutionDetails(SolutionDeployment solutionBean,TransportBean tbean)throws Exception{
		logger.debug("compositeSolutionDetails Start");
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		ParseJSON parseJson=new ParseJSON();
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		DockerInfoList  dockerList=new DockerInfoList();
		ArrayList<DockerInfo> dockerInfoList=new ArrayList<DockerInfo>();
		Blueprint bluePrintProbe=null;
		HashMap<String,String> repoUrlContainer=new HashMap<String,String>();
		HashMap<String,String> tagContainer=new HashMap<String,String>();
		String containerInstanceprobe=null;
		try {
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					        tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(),
					        tbean.getNexusUrl(),tbean.getNexusUserName(),tbean.getNexusPd());
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
			boolean probeIndicator=parseJson.checkProbeIndicator(AzureClientConstants.JSON_FILE_NAME);
			
			HashMap<String,String> imageMap=null;
			HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
			ArrayList<String> list=null;
			LinkedList<String> sequenceList=null;
			DataBrokerBean dataBrokerBean=null;
			logger.debug("probeIndicator "+probeIndicator);
			     
			 if(probeIndicator){
					tbean.setProtoContainerMap(parseJson.getProtoDetails(AzureClientConstants.JSON_FILE_NAME));
					bluePrintProbe =parseJson.jsonFileToObjectProbe(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
					
				}else{
					bluePrintProbe=parseJson.jsonFileToObject(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
				}
			 imageMap=parseJson.parseJsonFileImageMap(AzureClientConstants.JSON_FILE_NAME);
		     list=azureImpl.iterateImageMap(imageMap);
		     nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(AzureClientConstants.JSON_FILE_NAME);
		     sequenceList=parseJson.getSequenceListFromJSON(AzureClientConstants.JSON_FILE_NAME);
		     dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
				if(dataBrokerBean!=null){
					if(dataBrokerBean!=null){
						ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(tbean.getNexusUrl(),tbean.getNexusUserName(),
								            tbean.getNexusPd(), dataBrokerBean.getProtobufFile());
						logger.debug("byteArrayOutputStream "+byteArrayOutputStream);
						if(byteArrayOutputStream!=null){
							dataBrokerBean.setProtobufFile(byteArrayOutputStream.toString());
						}else{
							dataBrokerBean.setProtobufFile("");
							
						}
						
					 }
				}
				logger.debug("bluePrintProbe.ProbeIndocator"+bluePrintProbe.getProbeIndicator());
				ArrayList<ProbeIndicator> probeIndicatorList = bluePrintProbe.getProbeIndicator();
				ProbeIndicator prbIndicator = null;
				if(probeIndicatorList != null && probeIndicatorList.size() >0) {
					prbIndicator = probeIndicatorList.get(0);
				}			
			    if (bluePrintProbe.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True") ) {
			    	list.add(tbean.getNginxImageName());
					imageMap.put(tbean.getNginxImageName(), AzureClientConstants.NGINX_CONTAINER);
					sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.NGINX_CONTAINER);
					if (tbean.getProbePrintImage() != null && !"".equals(tbean.getProbePrintImage())) {
						list.add(tbean.getProbePrintImage());
						imageMap.put(tbean.getProbePrintImage(), AzureClientConstants.PROBE_CONTAINER_NAME);
						sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.PROBE_CONTAINER_NAME);
					}
				}	

				if (tbean.getBluePrintImage() != null && !"".equals(tbean.getBluePrintImage())) {
					list.add(tbean.getBluePrintImage());
					imageMap.put(tbean.getBluePrintImage(), AzureClientConstants.BLUEPRINT_CONTAINER);
				}
				logger.debug("list "+list);
				logger.debug("imageMap "+imageMap);
				logger.debug("sequenceList "+sequenceList);
				
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
		        
		        
				final String dockerContainerName = AzureClientConstants.DOCKER_CONTAINER_PREFIX + System.currentTimeMillis();//"acrsample";
				logger.debug("dockerContainerName "+dockerContainerName);
				//Get the existing Azure registry using resourceGroupName and Acr Name
				Registry azureRegistry = azure.containerRegistries().getByResourceGroup(solutionBean.getRgName(), solutionBean.getAcrName());
				RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
				
				DockerClient dockerClient = DockerUtils.createDockerClient(azure, solutionBean.getRgName(), null,
	                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), 
	                    tbean.getLocalHostEnv(), "",null,"","","","","","","",0);
				logger.debug("Local Docker client object created");
				if(dockerClient == null) {
					logger.debug("Docker client null");
				}
	            
				  
	            Iterator imageItr=imageMap.entrySet().iterator();
	            CreateContainerResponse dockerContainerInstance=null;
	            String containerCountName="";
	            String imageTag=AzureClientConstants.IMAGE_TAG_LATEST;
	            int dockerCount=0;
	            while(imageItr.hasNext()){
	            	Map.Entry pair = (Map.Entry)imageItr.next();
	            	String imageNameVal=(String)pair.getKey();
	            	String imageContainerNameVal=(String)pair.getValue();
	            	logger.debug("imageNameVal "+imageNameVal +" imageContainerNameVal "+imageContainerNameVal);
	            	if(imageContainerNameVal.equalsIgnoreCase(AzureClientConstants.BLUEPRINT_CONTAINER_NAME)){
	            		logger.debug(" BluePrint Container ");
	            		azureUtil.pullImageFromRepository(tbean.getBluePrintUser(),tbean.getBluePrintPass(),imageNameVal,dockerClient);
		            }else if(imageContainerNameVal.equalsIgnoreCase(AzureClientConstants.PROBE_CONTAINER_NAME)) {
		            	logger.debug(" Probe Container ");
	            		containerInstanceprobe=AzureClientConstants.PROBE_CONTAINER_NAME;
	            		azureUtil.pullImageFromRepository(tbean.getProbUser(),tbean.getProbePass(),imageNameVal,dockerClient);
		            }else if(imageContainerNameVal.equalsIgnoreCase(AzureClientConstants.NGINX_CONTAINER)) {
		                logger.debug(" NGINX Container ");
		                azureUtil.pullImageFromRepository("","",imageNameVal,dockerClient);
		            }else{
		            	if(azureUtil.getRepositryStatus(imageNameVal, tbean.getNexusRegistyName())){
		            		logger.debug(" Other Nexus Registry ");
		            		azureUtil.pullImageFromRepository(tbean.getNexusRegistyUserName(),tbean.getNexusRegistyPd(),imageNameVal,
		            				dockerClient);
           			    }else {
           			    	logger.debug(" Other Registry ");
		            		azureUtil.pullImageFromRepository(tbean.getRegistryUserName(),tbean.getRegistryPd(),imageNameVal,dockerClient);
           			    }
		            	
		            }
	            	if(imageNameVal!=null && !"".equals(imageNameVal)){
	            		String tag=azureUtil.getTagFromImage(imageNameVal);
	            		if(tag!=null){
	            			imageTag=tag;
	            		}
	            	}
	            	logger.debug("imageTag "+imageTag);
	            	dockerCount=dockerCount+1;
	            	containerCountName=dockerContainerName+"_"+dockerCount;
	            	logger.debug("containerCountName "+containerCountName);
	            	dockerContainerInstance = dockerClient.createContainerCmd(imageNameVal)
		                    .withName(containerCountName).exec();
	            	String privateRepoUrl = azureRegistry.loginServerUrl() + AzureClientConstants.PRIVATE_REPO_PREFIX + containerCountName;
	            	logger.debug("privateRepoUrl "+privateRepoUrl);
	            	String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
		                    .withRepository(privateRepoUrl)
		                    .withTag(imageTag).exec();
	            	logger.debug("dockerImageId "+dockerImageId);
	            	repoUrlContainer.put(imageContainerNameVal, privateRepoUrl);
	            	tagContainer.put(imageContainerNameVal, imageTag);
	            	dockerClient.removeContainerCmd(dockerContainerInstance.getId())
                    .withForce(true).exec();
	            	Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
	            	//Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
	            	logger.debug("Start pushing image to private repo ");
	            	dockerClient.pushImageCmd(privateRepoUrl)
                    .withAuthConfig(dockerClient.authConfig())
                    .exec(new PushImageResultCallback()).awaitSuccess();
	            	Thread.sleep(Integer.parseInt(tbean.getSleepTimeFirst()));
	            }
	            
				
				int portNoIncrement=8557;
				String portNumberString="";
				String probeNexusEndPoint="";
				int imageCount=0;
				String bluePrintPort="";
				String probeIP="";
				String probePort="";
				if(containerInstanceprobe!=null && !"".equals(containerInstanceprobe)){
	              	  DockerUtils.protoFileVM(solutionBean.getVmHostIP(), solutionBean.getVmUserName(), solutionBean.getVmUserPd(),tbean);
	               }
				if(sequenceList!=null && sequenceList.size() > 0){
	            	Iterator seqItr = sequenceList.iterator();
	                while (seqItr.hasNext()) {
	                    String jsonContainerName=(String)seqItr.next(); 
	                    logger.debug(" jsonContainerName "+jsonContainerName);
	                    Iterator imageMapItr=imageMap.entrySet().iterator();
			            while(imageMapItr.hasNext()){
			            	Map.Entry pair = (Map.Entry)imageMapItr.next();
			            	String imageName=(String)pair.getKey();
			            	String imageContainerName=(String)pair.getValue();
			            	 logger.debug(" imageName "+imageName);
			            	 logger.debug(" imageContainerName "+imageContainerName);
			            	 if(imageContainerName!=null && imageContainerName.equalsIgnoreCase(jsonContainerName)){
			            		 logger.debug(" Inside processing of Images ");
			            		 String portNumber="";
			            		 String regUserName="";
			            		 String regPass="";
			            		 String nodeTypeContainer="";
			            		 String nodeTypeName="";
			            		 String tagImageName=AzureClientConstants.IMAGE_TAG_LATEST;
			            		 AzureContainerBean containerBean=new AzureContainerBean();
			            		 DeploymentBean deployment=new DeploymentBean();
			            		 DockerInfo dockerinfo=new DockerInfo();
			            		 //String repositoryName=azureUtil.getRepositoryName(imageName);
			            		 String repositoryName=repoUrlContainer.get(imageContainerName);
			            		 String tag=tagContainer.get(imageContainerName);
			            		 if(tag!=null && !"".equals(tag)) {
			            			 repositoryName=repositoryName+":"+tag; 
			            		 }else {
			            			 repositoryName=repositoryName+":"+tagImageName; 
			            		 }
			            		 logger.debug(" imageName "+imageName+" repositoryName "+repositoryName);
			            		 if(nodeTypeContainerMap!=null && nodeTypeContainerMap.size() > 0 && nodeTypeContainerMap.get(jsonContainerName)!=null){
		    		            		DeploymentBean dBean=nodeTypeContainerMap.get(jsonContainerName);
		    		            		if(dBean!=null){
		    		            			nodeTypeContainer=dBean.getNodeType();
		    		            			nodeTypeName=dBean.getDataBrokerType();
		    		            		}
		    		            		
		    		            	}
			            		 if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.BLUEPRINT_CONTAINER_NAME)){
			            			 logger.debug(" Blueprint Section ");
			            			 portNumber="8555"; 
			            			 bluePrintPort=portNumber;
			            			 portNumberString=portNumber+":"+portNumber;
			            			 regUserName=tbean.getBluePrintUser();
			            			 regPass=tbean.getBluePrintPass();
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            		 }else if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.PROBE_CONTAINER_NAME)) {
			            			 logger.debug(" Probe Section ");
			            			 portNumberString=tbean.getProbeInternalPort()+":"+tbean.getProbeInternalPort();
	    		        			 probeNexusEndPoint="http://"+solutionBean.getVmHostName()+":"+tbean.getNginxPort();
	    		        			 probePort=tbean.getProbeInternalPort();
	    		        			 portNumber=tbean.getProbeInternalPort();
	    		        			 regUserName=tbean.getProbUser();
			            			 regPass=tbean.getProbePass();
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.PROBE_NODE_TYPE);
			            		 }else if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.NGINX_CONTAINER)) {
			            			 logger.debug(" NGINX Section ");
			            			 portNumber=String.valueOf(portNoIncrement);
			            			 portNoIncrement=portNoIncrement+1;
			            			 portNumberString=portNumber+":"+tbean.getNginxInternalPort();
	    		        			 tbean.setNginxPort(portNumber);
	    		        			 regUserName="";
			            			 regPass="";
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            		 }else{
			            			 logger.debug(" Other Section ");
			            			 portNumber=String.valueOf(portNoIncrement);
			            			 portNoIncrement=portNoIncrement+1;
			            			 logger.debug(" portNumber "+portNumber);
			            			 if(tbean.getSolutionPort()!=null && !"".equals(tbean.getSolutionPort())){
		    		        			 portNumberString=portNumber+":"+tbean.getSolutionPort();
		    		        			}else{
		    		        			 portNumberString=portNumber+":"+portNumber;
		    		        		  }
			            			 
			            			 if(azureUtil.getRepositryStatus(imageName, tbean.getNexusRegistyName())){
			            				 regUserName=tbean.getNexusRegistyUserName();
				            			 regPass=tbean.getNexusRegistyPd(); 
			            			 }else {
			            				 regUserName=tbean.getRegistryUserName();
				            			 regPass=tbean.getNexusRegistyPd(); 
			            			 }
			            			 if(nodeTypeContainer!=null && !"".equals(nodeTypeContainer)){
			            				    deployment.setNodeType(nodeTypeContainer);
			    		        			deployment.setDataBrokerType(nodeTypeName);
			            			 }else {
			            				 deployment.setDataBrokerType(""); 
				            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            			 }
			            			 
			            		 }
			            		 imageCount=imageCount+1;
			            		 logger.debug(" portNumber "+portNumber);
			            		 logger.debug(" probePort "+probePort);
			            		 logger.debug(" portNumberString "+portNumberString);
			            		 logger.debug(" regUserName "+azureRegistry.loginServerUrl()+" regPass "+acrCredentials.passwords().get(0).value());
			            		 logger.debug(" repositoryName "+azureRegistry.loginServerUrl());
			            		 logger.debug(" imageName "+imageName);
			            		 logger.debug(" jsonContainerName "+jsonContainerName);
			            		 logger.debug(" imageCount "+imageCount);
			            		 logger.debug(" portNumberString "+portNumberString);
			            		 logger.debug(" probeNexusEndPoint "+probeNexusEndPoint);
			            		 logger.debug(" Start Deploying solution in vm ");
			            		 DockerUtils.deploymentCompositeImageVM(solutionBean.getVmHostIP(), solutionBean.getVmUserName(),
			            				 solutionBean.getVmUserPd(),azureRegistry.loginServerUrl(), acrCredentials.username(),acrCredentials.passwords().get(0).value(), 
			            				 repositoryName,jsonContainerName,imageCount,portNumberString,probeNexusEndPoint,Integer.parseInt(tbean.getSleepTimeFirst()),tbean);
			            		 
			            		 logger.debug(" End Deploying solution in vm ");
			            		    dockerinfo.setIpAddress(solutionBean.getVmHostName());
	            		            dockerinfo.setPort(portNumber);
	            		            dockerinfo.setContainer(jsonContainerName);
	            		            
		    		        		containerBean.setContainerName(jsonContainerName);
		    		        		containerBean.setContainerIp(solutionBean.getVmHostIP());
		    		        		containerBean.setContainerPort(portNumber);
		    		        		
		    		        		deployment.setAzureVMIP(solutionBean.getVmHostIP());
		    		        		deployment.setAzureVMName(solutionBean.getVmHostName());
		    		        		deployment.setContainerName(jsonContainerName);
		    		        		deployment.setContainerPort(portNumber);
		    		        		
		    		        		dockerInfoList.add(dockerinfo);
		    		        		azureContainerBeanList.add(containerBean);
		    		        		deploymentList.add(deployment);
			            	 }
			            }
	                }
				}
				if(dockerInfoList!=null && dockerInfoList.size() > 0){
	            	dockerList.setDockerList(dockerInfoList);
	            }
				String urlDockerInfo="http://"+solutionBean.getVmHostIP()+":"+bluePrintPort+"/"+AzureClientConstants.PUT_DOCKER_INFO_URL;  
				String urlBluePrint="http://"+solutionBean.getVmHostIP()+":"+bluePrintPort+"/"+AzureClientConstants.PUT_BLUEPRINT_INFO_URL;
				logger.debug("urlDockerInfo "+urlDockerInfo+" urlBluePrint "+urlBluePrint);
				String dataBrokerPort=azureUtil.getDataBrokerPort(deploymentList,AzureClientConstants.DATABROKER_NAME);
				String urlDataBroker="http://"+solutionBean.getVmHostIP()+":"+dataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
				String csvDataBrokerPort="";
				String csvDataBrokerUrl="";
				if(dataBrokerBean!=null){
					  csvDataBrokerPort=azureUtil.getDataBrokerPortCSV(deploymentList,AzureClientConstants.DATABROKER_NAME);
				 }
				if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
					  csvDataBrokerUrl="http://"+solutionBean.getVmHostIP()+":"+csvDataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
				  }
				 logger.debug("csvDataBrokerUrl "+csvDataBrokerUrl);
				 logger.debug("csvDataBrokerPort "+csvDataBrokerPort);
				 logger.debug("urlDataBroker "+urlDataBroker);
				 logger.debug("dataBrokerPort "+dataBrokerPort);
				// Added for probe
				  if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
					  logger.debug("Inside csv Data Broker ConfigDB  "); 
					  azureUtil.callCsvConfigDB(solutionBean.getUsername(),solutionBean.getUserPd(),solutionBean.getHost(),solutionBean.getPort(),
							  csvDataBrokerUrl,dataBrokerBean);
					 }
				// putBlueprint
				 if(bluePrintProbe!=null){
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
					 azureUtil.putDataBrokerDetails(solutionBean.getUrlAttribute(),solutionBean.getJsonMapping(),
							 solutionBean.getJsonPosition(),urlDataBroker);
					}
				
				// Added notification for probe code
				 probeIndicatorList = bluePrintProbe.getProbeIndicator();
				 prbIndicator = null;
				 if(probeIndicatorList != null && probeIndicatorList.size() >0) {
						prbIndicator = probeIndicatorList.get(0);
				 }	
				 
				 if(solutionBean.getVmHostIP()!=null && !"".equals(solutionBean.getVmHostIP())){
					 azureUtil.generateNotification("Composite Solution is deployed, IP is: "+solutionBean.getVmHostIP(), solutionBean.getUserId(),
							 tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
				 }
				 //if (bluePrint.getProbeIndocator() != null && bluePrint.getProbeIndocator().equalsIgnoreCase("True"))  {
				 if (bluePrintProbe.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True"))  {
					 logger.debug("Probe indicator true. Starting generatenotircation deployDataObject.getUserId() "+solutionBean.getUserId());
					 logger.debug(" probeIP "+solutionBean.getVmHostIP()+" probePort "+probePort);
					 azureUtil.generateNotification("Probe IP and Port: "+solutionBean.getVmHostIP()+":"+probePort,solutionBean.getUserId(),
							 tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
				 }
				 if(azureContainerBeanList!=null){
			   			logger.debug("Start saving data in database "); 
			   			azureUtil.createDeploymentCompositeData(tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd(),azureContainerBeanList,solutionBean.getSolutionId(),
			   					solutionBean.getSolutionRevisionId(),solutionBean.getUserId(),tbean.getUidNumStr(),AzureClientConstants.DEPLOYMENT_PROCESS);
			      }
		 	
        }catch(Exception e) {
        	logger.error("compositeSolutionDetails for existing failed", e.getMessage());
        	try{
	        	azureUtil.generateNotification("Error in vm creation", solutionBean.getUserId(),
	        			tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
	        	azureUtil.createDeploymentCompositeData(tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd(),azureContainerBeanList,solutionBean.getSolutionId(),
						solutionBean.getSolutionRevisionId(),solutionBean.getUserId(),tbean.getUidNumStr(),AzureClientConstants.DEPLOYMENT_FAILED);
        	}catch(Exception ex){
				logger.error("compositeSolutionDetails for existing failed  in saving data",e.getMessage());
			}
		}
	  logger.debug("compositeSolutionDetails End");
	}
	
	public String checkPrerequisites(SolutionDeployment deploymentBean,AzureCommonUtil azureUtil)throws Exception{
		 logger.debug("checkPrerequisites Start");
		 SSHShell sshShell = null;
		 String returnStr="success";
		 String scriptOutput="";
		 try {
			 String installScript=azureUtil.getFileDetails(AzureClientConstants.SETUP_SCRIPT_NAME);
			 logger.debug("installScript "+installScript);
			 sshShell = SSHShell.open(deploymentBean.getVmHostIP(),22,deploymentBean.getVmUserName(),deploymentBean.getVmUserPd());
			 sshShell.upload(new ByteArrayInputStream(installScript.getBytes()),
						"setup-dockert.sh", "dockerscript", true, "4095");
			 logger.debug("Start executing script ");
			 scriptOutput = sshShell.executeCommand("bash -c ~/dockerscript/setup-dockert.sh", true, true);
			 logger.debug("scriptOutput "+scriptOutput);
		 }catch (Exception exception) {
				logger.error("Exception in checkPrerequisites "+exception);
				returnStr="fail";
				throw exception;
			} finally {
				if (sshShell != null) {
					sshShell.close();
					sshShell = null;
				}
			}
		 logger.debug("checkPrerequisites End returnStr"+returnStr);
		return returnStr;
	}
	public String removeAllContainer(SolutionDeployment deploymentBean)throws Exception{
		logger.debug("removeAllContainer Start ");
		SSHShell sshShell = null;
		String output="";
		String returnStr="success";
		try {
			String removeDockerScript=""
					                 + "docker stop $(docker ps -a -q) \n"
						             +"docker rm $(docker ps -a -q) ";
			sshShell = SSHShell.open(deploymentBean.getVmHostIP(),22,deploymentBean.getVmUserName(),deploymentBean.getVmUserPd());
			logger.debug("Upload docker install script  ");
			
			 sshShell.upload(new ByteArrayInputStream(removeDockerScript.getBytes()),
						"removeDockerScript.sh", "azuredockerscript", true, "4095");
			 logger.debug("Start executing script ");
			 output = sshShell
						.executeCommand("bash -c ~/azuredockerscript/removeDockerScript.sh", true, true);
			 logger.debug("output "+output);
			
		} catch (Exception exception) {
			logger.error("Exception in checkInstallDocker "+exception);
			returnStr="fail";
			throw exception;
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		
		logger.debug("removeAllContainer End returnStr "+returnStr);
		return returnStr;
	}
	
}
