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
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

public class AzureCompositeSolution implements Runnable {
	
	
	Logger logger =LoggerFactory.getLogger(AzureSimpleSolution.class);	
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


	public AzureCompositeSolution(Azure azure,AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String dockerUserName,String dockerPwd,
			String localEnvDockerHost,String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,
			String networkSecurityGroup,HashMap<String,String> imageMap,LinkedList<String> sequenceList,String dockerRegistryName,Blueprint bluePrint,String uidNumStr,
			String dataSource,String dataUserName,String dataPassword,String dockerVMUserName,String dockerVMPassword) {
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
	    
	   }
	public void run() {
		logger.info("<-----------------AzureCompositeSolution-----Run Started-------------------------->");
		logger.info("<-------azure-------->"+azure);
		logger.info("<-------deployDataObject-------->"+deployDataObject);
		logger.info("<-------dockerContainerPrefix-------->"+dockerContainerPrefix);
		logger.info("<-------dockerUserName-------->"+dockerUserName);
		logger.info("<-------dockerPwd-------->"+dockerPwd);
		logger.info("<-------localEnvDockerHost-------->"+localEnvDockerHost);
		logger.info("<-------localEnvDockerCertPath-------->"+localEnvDockerCertPath);
		logger.info("<-------list-------->"+list);
		logger.info("<-------bluePrintName-------->"+bluePrintName);
		logger.info("<-------bluePrintUser-------->"+bluePrintUser);
		logger.info("<-------bluePrintPass-------->"+bluePrintPass);
		logger.info("<-------networkSecurityGroup-------->"+networkSecurityGroup);
		logger.info("<-------dockerRegistryName-------->"+dockerRegistryName);
		logger.info("<-------uidNumStr-------->"+uidNumStr);
		logger.info("<-------sequenceList-------->"+sequenceList);
		logger.info("<-------imageMap-------->"+imageMap);
		
		logger.info("<-------dataSource-------->"+dataSource);
		logger.info("<-------dataUserName-------->"+dataUserName);
		logger.info("<-------dataPassword-------->"+dataPassword);
		logger.info("<-------dockerVMUserName-------->"+dockerVMUserName);
		logger.info("<-------dockerVMPassword-------->"+dockerVMPassword);
		
		AzureBean azureBean=new AzureBean();
		ObjectMapper mapper = new ObjectMapper();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		
		try{
			logger.info("<-------------start pushCompositeImage------------------------------>");
		    
			HashMap<String,String> containeDetailMap=new HashMap<String,String>();
			DockerInfoList  dockerList=new DockerInfoList();
			//final String saName = SdkContext.randomResourceName("sa", 20);	   
	        final Region region = Region.US_EAST;
	        final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();//"acrsample";
	      
	        String servicePrincipalClientId = deployDataObject.getClient(); // replace with a real service principal client id
	        String servicePrincipalSecret = deployDataObject.getKey(); // and corresponding secret
	        //HashMap<String,String> containerMap=new HashMap<String,String>();
	        String containerInstanceBluePrint="";
	        //String bluePrintContainerId="";
	        
	        logger.info("<--------------dockerRegistryName--------------------------->"+dockerRegistryName);
	        logger.info("<--------------list--------------------------->"+list);
	        logger.info("<--------------sequenceList--------------------------->"+sequenceList);
	        logger.info("<---------bluePrintName------------->"+bluePrintName);
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
	
		            logger.info("Creating an SSH private and public key pair");
	
		            SSHShell.SshPublicPrivateKey sshKeys = SSHShell.generateSSHKeys("", "ACS");
		            logger.info("SSH private key value: \n" + sshKeys.getSshPrivateKey());
		            logger.info("SSH public key value: \n" + sshKeys.getSshPublicKey());
	
	
		            //=============================================================
		            // Create an Azure Container Service with Kubernetes orchestration
	
		            logger.info("Creating an Azure Container Service with Kubernetes ochestration and one agent (virtual machine)");
	
		            Date t1 = new Date();        
	
	
		            //=============================================================
		            // Create an Azure Container Registry to store and manage private Docker container images
	
		            logger.info("Creating an Azure Container Registry");
		            Date t2 = new Date();
		            t1 = new Date();
	                
		            //Get the existing Azure registry using resourceGroupName and Acr Name
		            Registry azureRegistry = azure.containerRegistries().getByResourceGroup(deployDataObject.getRgName(), deployDataObject.getAcrName());
		            
		            t2 = new Date();
		            logger.info("Created Azure Container Registry: (took " + ((t2.getTime() - t1.getTime()) / 1000) + " seconds) " + azureRegistry.id());
		            Utils.print(azureRegistry);
	
	
		            //=============================================================
		            // Create a Docker client that will be used to push/pull images to/from the Azure Container Registry
	
		            RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
		            logger.info("azureRegistry.loginServerUrl="+azureRegistry.loginServerUrl()+ ", acrCredentials.username "+ acrCredentials.username()+ ", acrCredentials.passwords" + acrCredentials.passwords().get(0).value());
		            DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath,azureBean,
		                    networkSecurityGroup,dockerRegistryPort,dockerRegistryName);
		            
		            AuthConfig authConfig = new AuthConfig()
		                    .withUsername(dockerUserName)
		                    .withPassword(dockerPwd);
		            
		            AuthConfig authConfig2 = new AuthConfig()
		                    .withUsername(bluePrintUser)
		                    .withPassword(bluePrintPass);
	
		            //=============================================================
		            // Pull a temp image from public Docker repo and create a temporary container from that image
		            // These steps can be replaced and instead build a custom image using a Dockerfile and the app's JAR
		            logger.info("Start pulling images from nexus::::::::");
		            Iterator itr=list.iterator();
		            while(itr.hasNext()){
		            	 
		            	String imageName=(String)itr.next();
		            	//logger.info("Nexus Image Name------------------->"+imageName);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig2)
		                    //.withTag(dockerImageTag)
		                    .exec(new PullImageResultCallback())
		                    .awaitSuccess();
		            		
		            	}else{
		            	dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
	                    //.withTag(dockerImageTag)
	                    .exec(new PullImageResultCallback())
	                    .awaitSuccess();
		            	}
		            	Thread.sleep(50000);
		            }
		            
		            
		            logger.info("List local Docker images:");
		            List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();
		            //for (Image image : images) {
		            	//logger.info("Docker Images \n"+ image.getRepoTags()[0]+"<----image.getId()--->"+ image.getId());
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
		            	
		            	if(imageName!=null && !"".equals(imageName)){
		            		String tag=getTagFromImage(imageName);
		            		if(tag!=null){
		            			imageTagVal=tag;
		            		}
		            	}
		            	logger.info("===imageName======="+imageName+"========imageTagVal===="+imageTagVal);
		            	if(imageName!=null && imageName.contains(bluePrintName)){
		            		containerInstanceBluePrint=dockerContainerName+"_"+dockerCount;
		            	}
		            	hmap.put(dockerContainerName+"_"+dockerCount, dockerContainerInstance);
		            	containerTagMap.put(dockerContainerName+"_"+dockerCount, imageTagVal);
		            	containerImageMap.put(dockerContainerName+"_"+dockerCount, imageName);
		            	Thread.sleep(30000);
		            	dockerCount++;
		            }
		            System.out.println("=======containerImageMap====="+containerImageMap);
		            logger.info("List All Docker containers:");
		            List<Container> dockerContainers = dockerClient.listContainersCmd()
		                    .withShowAll(true)
		                    .exec();
		            for (Container container : dockerContainers) {
		            	logger.info("All Docker container with images and Name %s (%s)\n"+container.getImage()+"<-----container.getId()----->"+container.getId());
		            }
	
		            //=============================================================
		            // Commit the new container
	               //String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" + dockerContainerName;
		          //logger.info("privateRepoUrl::::::::::::::::::"+privateRepoUrl);
		            HashMap<String,String> repoUrlMap=new HashMap<String,String>();  
		            Iterator itrContainer=hmap.entrySet().iterator();
		            while(itrContainer.hasNext()){
		            	String imageTagLatest="latest";
		            	Map.Entry pair = (Map.Entry)itrContainer.next();
		            	String containerName=(String)pair.getKey();
		            	CreateContainerResponse dockerContainerInstance=(CreateContainerResponse)pair.getValue();
		            	
		            	String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" + containerName;
		            	logger.info("dockerContainerInstance.getId():::::::::::::::::"+dockerContainerInstance.getId()+"===privateRepoUrl===="+privateRepoUrl);
		            	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
		            		imageTagLatest=containerTagMap.get(containerName);
		            	}
		            	logger.info("containerName======"+containerName+"==imageTagLatest======"+imageTagLatest);
			            String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
			                    .withRepository(privateRepoUrl)
			                    .withTag(imageTagLatest).exec();
			            logger.info("dockerImageId::::::::::::::::::"+dockerImageId);
			            repoUrlMap.put(containerName, privateRepoUrl);
			            // We can now remove the temporary container instance
			            dockerClient.removeContainerCmd(dockerContainerInstance.getId())
			                    .withForce(true)
			                    .exec();
			            Thread.sleep(5000);
		            }
		            //#####################################################################################
		            logger.info("<----Before Docker remoteDockerClient--------------------------->");
		            DockerClient remoteDockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
		                    azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), null, localEnvDockerCertPath,azureBean
		                    ,networkSecurityGroup,dockerRegistryPort,dockerRegistryName);
		            logger.info("<----After Docker remoteDockerClient--------------------------->");
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
		            
		            logger.info("<----Pushed Images to privaterepourl and removing imgage from local docker host---------->");
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
		            logger.info("<----pull images from Azure registry to locally--------->");
		             repoContainer=repoUrlMap.entrySet().iterator();
		            while(repoContainer.hasNext()){
		            	Map.Entry pair = (Map.Entry)repoContainer.next();
		            	String containerName=(String)pair.getKey();
		            	String privateRepoUrl=(String)pair.getValue();
		            	 logger.info("<----pull images from Azure registry to locally-----privateRepoUrl---->"+privateRepoUrl);
		            	dockerClient.pullImageCmd(privateRepoUrl)
	                    .withAuthConfig(dockerClient.authConfig())
	                    .exec(new PullImageResultCallback()).awaitSuccess();
		            	Thread.sleep(50000);
		            }
		            
		            logger.info("List local Docker images after pulling sample image from the Azure Container Registry:");
		            images = dockerClient.listImagesCmd()
		                    .withShowAll(true)
		                    .exec();
		            /*for (Image image : images) {
		            	logger.info("List Image after pulling locally \n"+ image.getRepoTags()[0]+"<-------ImageId--------->"+ image.getId());
		            }*/
		            
	                  logger.info("<----remoteDockerClient with privateRepoUrl--------->");
	                  int imageCount=1;
	                  int remoteCount=1;
	                  int count=0;
	                  List<DockerInfo> dockerInfoList=new ArrayList<DockerInfo>();
	                  if(sequenceList!=null && sequenceList.size() > 0){
			            	Iterator seqItr = sequenceList.iterator();
			                while (seqItr.hasNext()) {
			                    String jsonContainerName=(String)seqItr.next(); 
			                    logger.info("<----jsonContainerName--------->"+jsonContainerName);
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
			    		            	logger.info("<----tagImage------------------>"+tagImage);
			    		            	if(containerImageMap!=null && containerImageMap.get(containerName)!=null){
			    		            		imageName=containerImageMap.get(containerName);
			    		            		logger.info("<----imageName--------->"+imageName+"====imageMap=="+imageMap);
			    		            		if(imageName!=null && imageMap!=null && imageMap.get(imageName)!=null){
			    		            			finalContainerName=imageMap.get(imageName);
			    		            		}
			    		            	}
			    		            	logger.info("<--Before--jsonContainerName--------->"+jsonContainerName+"===jsonContainerName==="+jsonContainerName);
			    		            	if(finalContainerName!=null && !finalContainerName.equalsIgnoreCase(jsonContainerName)){
			    		            		 logger.info("Continue.............................................");
			    		            		continue;
			    		            	}
			    		            	
			    		            	String azureVMIP=azureBean.getAzureVMIP();
			    		            	final String vmUserName = "dockerUser";
			    		        		final String vmPassword = "12NewPA$$w0rd!";
			    		        		String repositoryName="";
			    		        		repositoryName=privateRepoUrl+":"+tagImage;
			    		        		String portNumber="";
			    		        		logger.info("====azureVMIP======: " + azureVMIP);
			    		        		logger.info("====vmUserName======: " + vmUserName);
			    		        		logger.info("====registryServerUrl======: " + azureRegistry.loginServerUrl());
			    		        		logger.info("====username======: " + acrCredentials.username());
			    		        		logger.info("====password======: " + acrCredentials.passwords().get(0).value());
			    		        		logger.info("====repositoryName======: " + repositoryName);
			    		        		logger.info("====finalContainerName======: " + finalContainerName);
			    		        		logger.info("====imageCount======: " + imageCount);
			    		        		if(containerInstanceBluePrint!=null && containerInstanceBluePrint.equalsIgnoreCase(containerName)){
			    		        			logger.info("<--if Part--containerInstanceBluePrint--------->"+containerInstanceBluePrint+"=====containerName==="+containerName);
			    		        			portNumber="8555";
			    		        			azureBean.setBluePrintIp(azureVMIP);
			            			        azureBean.setBluePrintPort(portNumber);
			    		        		}else{
			    		        			portNumber=portArr[count];
			    		        			count++;
			    		        			
			    		        		}
			    		        		imageCount++;
			    		        		dockerinfo.setIpAddress(azureVMIP);
		            		            dockerinfo.setPort(portNumber);
		            		            dockerinfo.setContainer(finalContainerName);
		            		            dockerInfoList.add(dockerinfo);
		            		            logger.info("====Start Deploying=====================repositoryName=======: "+repositoryName);
			    		        		DockerUtils.deploymentCompositeImageVM(azureVMIP, vmUserName, vmPassword, azureRegistry.loginServerUrl(),  acrCredentials.username(),
			    		        				acrCredentials.passwords().get(0).value(), repositoryName,finalContainerName,imageCount,portNumber);
			    		        		AzureContainerBean containerBean=new AzureContainerBean();
			    		        		containerBean.setContainerName(finalContainerName);
			    		        		containerBean.setContainerIp(azureVMIP);
			    		        		containerBean.setContainerPort(portNumber);
			    		        		azureContainerBeanList.add(containerBean);
			    		            }
			                    	
			                    }
			                }
	                  }  
	                  logger.info("====dockerInfoList======: " + dockerInfoList);
	                  if(dockerInfoList!=null && dockerInfoList.size() > 0){
			            	dockerList.setDockerList(dockerInfoList);
			            }
	                  logger.info("containeDetailMap==========>"+containeDetailMap+"=====dockerList====="+dockerList);
	  	              azureBean.setDockerinfolist(dockerList);	
		            
			}
          
          String azureDetails=mapper.writeValueAsString(azureBean.getDockerinfolist());  
          setuidHashmapComposite(uidNumStr,azureDetails);
          logger.info("azureDetails=============="+azureDetails);
          logger.info("Dockerinfolist=============="+mapper.writeValueAsString(azureBean.getDockerinfolist()));
  		  logger.info("bluePrint==================="+mapper.writeValueAsString(bluePrint));	
  		  DockerInfoList dockerInfoList=azureBean.getDockerinfolist();
		  String vmIP=azureBean.getAzureVMIP().trim();
		  String bluePrintPort=azureBean.getBluePrintPort().trim();
		  String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
		  String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
		  logger.info("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
		  if(azureBean.getDockerinfolist()!=null){
			  putContainerDetailsJSON(azureBean.getDockerinfolist(),urlDockerInfo);
			}
		 if(bluePrint!=null){
			 putBluePrintDetailsJSON(bluePrint,urlBluePrint);
		  }
		 if(azureContainerBeanList!=null){
       	  
   			  logger.info("Start saving data in database=============="); 
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
		 
		logger.info("<-----------------AzureCompositeSolution-----Run end-------------------------->");
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
	
	public void putContainerDetails(DockerInfoList  dockerList,String apiUrl){
		logger.info("<--------Start---putContainerDetails------->");
		try {
			logger.info("<----dockerList---------->"+dockerList.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<DockerInfoList> entity = new HttpEntity<DockerInfoList>(dockerList);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.info("<--------End---putContainerDetails------->");
	}
	public void putBluePrintDetails(Blueprint  bluePrint,String apiUrl){
		logger.info("<--------Start---putContainerDetails------->");
		try {
			logger.info("<----bluePrint---------->"+bluePrint.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpEntity<Blueprint> entity = new HttpEntity<Blueprint>(bluePrint);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
             e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.info("<--------End---putContainerDetails------->");
	}
	public void setuidHashmapComposite(String uidNumStr,String azureDetails){
		logger.info("<---------------setuidHashmap-------Run Start-------------------------->"+azureDetails+"====="+uidNumStr);
		HashMap<String,String> singlatonMap=SingletonMapClass.getInstance();
		singlatonMap.put(uidNumStr, azureDetails);
		logger.info("<---------------setuidHashmap-------Run End-------------------------->"+singlatonMap);
	}	
	
	public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		return client;
	}
	public void putContainerDetailsJSON(DockerInfoList  dockerList,String apiUrl){
		logger.info("<--------Start---putContainerDetailsJSON------->");
		try {
			logger.info("<----dockerList---------->"+dockerList.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			ObjectMapper mapper = new ObjectMapper();
			String dockerJson=mapper.writeValueAsString(dockerList);
			logger.info("<----dockerJson---------->"+dockerJson);
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    	
		    HttpEntity<String> entity = new HttpEntity<String>(dockerJson);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            e.printStackTrace();
            logger.error("<---------Exception----------->"+e.getMessage());
		 }
		logger.info("<--------End---putContainerDetailsJSON------->");
	}
	public void putBluePrintDetailsJSON(Blueprint  bluePrint,String apiUrl){
		logger.info("<--------Start---putBluePrintDetailsJSON------->");
		try {
			logger.info("<----bluePrint---------->"+bluePrint.toString()+"======apiUrl==="+apiUrl);
			final String url = apiUrl;
			ObjectMapper mapper = new ObjectMapper();
			String blueprintJson=mapper.writeValueAsString(bluePrint); 
			logger.info("<----blueprintJson---------->"+blueprintJson);
			RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpEntity<String> entity = new HttpEntity<String>(blueprintJson);
		    restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		   
		  } catch (Exception e) {
            logger.error("<---------Exception----------->"+e.getMessage());
            e.printStackTrace();
		 }
		logger.info("<--------End---putBluePrintDetailsJSON------->");
	}
	public void createDeploymentCompositeData(String dataSource,String dataUserName,String dataPassword,List<AzureContainerBean> azureContainerBeanList,
			String solutionId,String solutionRevisionId,String userId,String uidNumber,String deploymentStatusCode) throws Exception{
		logger.info("<---------Start createDeploymentCompositeData ------------------------->");
		logger.info("<---------dataSource-------->"+dataSource);
		logger.info("<-------dataUserName-------------->"+dataUserName);
		logger.info("<--------dataPassword------------->"+dataPassword);
		logger.info("<---------solutionId------------------->"+solutionId);
		logger.info("<--------solutionRevisionId-------------------->"+solutionRevisionId);
		logger.info("<------userId--------------->"+userId);
		logger.info("<------uidNumber--------------->"+uidNumber);
		logger.info("<------deploymentStatusCode--------------->"+deploymentStatusCode);
		logger.info("<------azureContainerBeanList--------------->"+azureContainerBeanList);
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
			logger.info("<---------azureDetails------------------------->"+azureDetails);
			MLPSolutionDeployment mlpDeployment=client.createSolutionDeployment(mlp);
			logger.info("<---------mlpDeployment------------------------->"+mlpDeployment);
		}
		logger.info("<---------End createDeploymentCompositeData ------------------------->");
	}

}
