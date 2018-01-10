package org.acumos.azure.client.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ArrayList;

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.service.AzureService;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.SingletonMapClass;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.rest.LogLevel;

import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;



public class AzureSimpleSolution implements Runnable{
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
private String uidNumStr;


public AzureSimpleSolution(Azure azure,AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String dockerUserName,String dockerPwd,
		String localEnvDockerHost,String localEnvDockerCertPath,ArrayList<String> list,String bluePrintName,String bluePrintUser,String bluePrintPass,
		String networkSecurityGroup,String dockerRegistryName,String uidNumStr) {
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
    this.uidNumStr = uidNumStr;
    
   }

public void run() {
	logger.info("<-----------------AzureSimpleSolution-----Run Started-------------------------->");
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

	AzureBean azureBean=new AzureBean();
	try{
	final String saName = SdkContext.randomResourceName("sa", 20);	   
    final Region region = Region.US_EAST;
    final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();//"acrsample";
    String servicePrincipalClientId = deployDataObject.getClient(); // replace with a real service principal client id
    String servicePrincipalSecret = deployDataObject.getKey(); // and corresponding secret
    
	
	 String portArr[]={"8556","8557","8558","8559","8560","8561","8562","8563","8564","8565"};
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
        
        RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
        logger.info("azureRegistry.loginServerUrl="+azureRegistry.loginServerUrl()+ ", acrCredentials.username "+ acrCredentials.username()+ ", acrCredentials.passwords" + acrCredentials.passwords().get(0).value());
        DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
                azureRegistry.loginServerUrl(), acrCredentials.username(), acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath,azureBean,
                networkSecurityGroup,dockerRegistryPort,dockerRegistryName);
        
        AuthConfig authConfig = new AuthConfig()
                .withUsername(dockerUserName)
                .withPassword(dockerPwd);
        logger.info("Start pulling images from nexus::::::::");
        Iterator itr=list.iterator();
        while(itr.hasNext()){
        	String imageName=(String)itr.next();
        	dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
            //.withTag(dockerImageTag)
            .exec(new PullImageResultCallback())
            .awaitSuccess();
        	Thread.sleep(30000);
        }
        
        int dockerCount=1;
        HashMap<String,CreateContainerResponse> hmap=new HashMap<String,CreateContainerResponse>();
        HashMap<String,String> containerTagMap=new HashMap<String,String>();
        HashMap<String,String> containerImageMap=new HashMap<String,String>();
        HashMap<String,String> containerInstanceImageMap=new HashMap<String,String>();
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
        	/*if(imageName!=null && imageName.contains(bluePrintName)){
        		containerInstanceBluePrint=dockerContainerName+"_"+dockerCount;
        	}*/
        	hmap.put(dockerContainerName+"_"+dockerCount, dockerContainerInstance);
        	containerTagMap.put(dockerContainerName+"_"+dockerCount, imageTagVal);
        	containerImageMap.put(dockerContainerName+"_"+dockerCount, imageName);
        	Thread.sleep(30000);
        	dockerCount++;
        }
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
        
        logger.info("<----remoteDockerClient with privateRepoUrl--------->");
        
        repoContainer=repoUrlMap.entrySet().iterator();
        while(repoContainer.hasNext()){
        	Map.Entry pair = (Map.Entry)repoContainer.next();
        	String containerName=(String)pair.getKey();
        	String privateRepoUrl=(String)pair.getValue();
        	String tagImage="latest";
        	if(containerTagMap!=null && containerTagMap.get(containerName)!=null){
        		tagImage=containerTagMap.get(containerName);
        	}
        	
        	String azureVMIP=azureBean.getAzureVMIP();
        	final String vmUserName = "dockerUser";
    		final String vmPassword = "12NewPA$$w0rd!";
    		String repositoryName="";
    		repositoryName=privateRepoUrl+":"+tagImage;
    		logger.info("<----azureBean VM-------->"+azureBean.getAzureVMIP());
    		logger.info("<----azureBean VM-------->"+azureRegistry.loginServerUrl());
    		logger.info("<----azureBean VM-------->"+azureRegistry.loginServerUrl());
    		logger.info("<----azureBean VM-------->"+acrCredentials.passwords().get(0).value());
        	DockerUtils.deploymentImageVM(azureVMIP, vmUserName, vmPassword, azureRegistry.loginServerUrl(),  acrCredentials.username(), acrCredentials.passwords().get(0).value(), repositoryName);
        }
	}catch(Exception e){
		logger.error("Error in AzureSimpleSolution===========" +e.getMessage());
		e.printStackTrace();
	}
	String vmDetail=azureBean.getAzureVMIP()+"#8557";
	setuidHashmap(uidNumStr,vmDetail);
    logger.info("<---------------AzureSimpleSolution-------Run End-------------------------->");
    // code in the other thread, can reference "var" variable
  }

public void setuidHashmap(String uidNumStr,String vmDetails){
	logger.info("<---------------setuidHashmap-------Run Start-------------------------->"+uidNumStr+"====vmDetails======"+vmDetails);
	HashMap<String,String> singlatonMap=SingletonMapClass.getInstance();
	singlatonMap.put(uidNumStr, vmDetails);
	logger.info("<---------------setuidHashmap-------Run End-------------------------->"+singlatonMap);
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

}
