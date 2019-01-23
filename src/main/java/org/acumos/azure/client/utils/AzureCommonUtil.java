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
package org.acumos.azure.client.utils;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;

public class AzureCommonUtil {
	Logger logger = LoggerFactory.getLogger(AzureCommonUtil.class);
	
	
	/**
	 * 
	 * @param mlpNotification notification Details 
	 * @param dataSource datasource name 
	 *  @param dataUserName Username Data
	 * @param dataPassword Password Data
	 * @return mlNotification notification bean
	 */
	 public org.acumos.azure.client.transport.MLNotification createNotification(MLPNotification mlpNotification,String dataSource,
			 String dataUserName,String dataPassword) {
		 logger.debug("createNotification Start");
         CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
         MLNotification mlNotification = Utils.convertToMLNotification(client.createNotification(mlpNotification));
         logger.debug("createNotification End");
         return mlNotification;
	 }
	 
	/**
	  * 
	  * @param msg notification message 
	  * @param userId Unique Id of user
	  * @param dataUserName UserName of Database
	  * @param dataPassword Password of database
	  * @throws Exception  if fail to create notification
	  */
	 public void generateNotification(String msg, String userId,String dataSource,String dataUserName,String dataPassword)throws Exception {
		 logger.debug("generateNotification Start");
		 logger.debug("userId "+userId+"msg "+msg);
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
                     MLNotification mLNotification = createNotification(notification,dataSource,dataUserName,dataPassword);
                     logger.debug("mLNotification.getNotificationId() "+mLNotification.getNotificationId());
                     client.addUserToNotification(mLNotification.getNotificationId(),userId);
             }
         } catch (Exception e) {
        	 logger.error("generateNotification failed", e);
        	 throw e;
         }
         logger.debug("generateNotification End"); 
	 }
	 public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
			CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password,null);
			return client;
		}
	 public NexusArtifactClient nexusArtifactClientDetails(String nexusUrl, String nexusUserName,String nexusPassword) {
			logger.debug("nexusArtifactClientDetails start");
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusUrl);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);
			logger.debug("nexusArtifactClientDetails End");
			return nexusArtifactClient;
	}
	 public ByteArrayOutputStream getNexusUrlFile(String nexusUrl, String nexusUserName,String nexusPassword,String nexusURI)throws Exception {
			logger.debug("getNexusUrlFile start");
			ByteArrayOutputStream byteArrayOutputStream=null;
			try
			{
				NexusArtifactClient nexusArtifactClient=nexusArtifactClientDetails(nexusUrl, 
						nexusUserName, nexusPassword);
				 byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
				 logger.debug("byteArrayOutputStream "+byteArrayOutputStream);
			}catch (Exception e) {
				 logger.error("getNexusUrlFile failed", e);
				 throw e;
      }
			logger.debug("getNexusUrlFile ");
			return byteArrayOutputStream;
	}
	 
   public String replaceCharStr(String commonStr,String replaceChar,String ignorDoller){
	   logger.debug("replaceCharStr Start");
	   String finalStr="";
	   if(ignorDoller!=null && ignorDoller.equalsIgnoreCase("TRUE")){
		   if(commonStr!=null && !"".equals(commonStr) && replaceChar!=null && !"".equals(replaceChar)){
			   finalStr = commonStr.replace(replaceChar, AzureClientConstants.SPECIAL_CHAR_PROP);
		   }else{
			   finalStr=commonStr; 
		   }
	   }else{
		   finalStr=commonStr; 
	   }
	   logger.debug("replaceCharStr start");
	   return finalStr;
   }
   public boolean getRepositryStatus(String imageName,String repositoryName){
		logger.debug("getRepositryName Start");
		logger.debug("imageName"+imageName+" repositoryName "+repositoryName);
		boolean checkRepositoryName=false;
		if(imageName!=null){
			String imageArr[]=imageName.split("/");
			if(repositoryName!=null){
				if(imageArr[0].contains(repositoryName)){
					checkRepositoryName=true;
				}
			}
		}
		logger.debug("checkRepositoryName"+checkRepositoryName);
		logger.debug("repositaryName End");
		return checkRepositoryName;
	  }
   public AzureDeployDataObject convertToAzureDeployDataObject(AzureKubeBean auth) {
		 AzureDeployDataObject authObject=new AzureDeployDataObject();
       if(auth.getAcrName()!=null){
       	authObject.setAcrName(auth.getAcrName());
       }
       if(auth.getKey()!=null){
       	authObject.setKey(auth.getKey());
       }
       if(auth.getRgName()!=null){
       	authObject.setRgName(auth.getRgName());
       }
       if(auth.getClient()!=null){
       	authObject.setClient(auth.getClient());
       }
       if(auth.getSubscriptionKey()!=null){
       	authObject.setSubscriptionKey(auth.getSubscriptionKey());
       }
       if(auth.getStorageAccount()!=null){
       	authObject.setStorageAccount(auth.getStorageAccount());
       }
       if(auth.getTenant()!=null){
       	authObject.setTenant(auth.getTenant());
       }
       if(auth.getSolutionId()!=null){
       	authObject.setSolutionId(auth.getSolutionId());
       }
       if(auth.getSolutionRevisionId()!=null){
       	authObject.setSolutionRevisionId(auth.getSolutionRevisionId());
       }
       if(auth.getUserId()!=null){
       	authObject.setUserId(auth.getUserId());
       }
     return  authObject; 
	}
   public String getSingleImageData(String solutionId,String revisionId,String datasource,String userName,String dataPd)throws Exception{
		logger.debug("Start getSingleImageData");
		String imageTag="";
		CommonDataServiceRestClientImpl cmnDataService=getClient(datasource,userName,dataPd);
		List<MLPArtifact> mlpSolutionRevisions = null;
		mlpSolutionRevisions = cmnDataService.getSolutionRevisionArtifacts(solutionId, revisionId);
		if(mlpSolutionRevisions != null) {
			for (MLPArtifact artifact : mlpSolutionRevisions) {
				String[] st = artifact.getUri().split("/");
				String name = st[st.length-1];
				artifact.setName(name);
				logger.debug("ArtifactTypeCode" +artifact.getArtifactTypeCode());
				logger.debug("URI" +artifact.getUri());
				if(artifact.getArtifactTypeCode()!=null && artifact.getArtifactTypeCode().equalsIgnoreCase("DI")){
					imageTag=artifact.getUri();
				}
			}
		}
		 
		logger.debug("End getSingleImageData imageTag"+imageTag);
		return imageTag;
	}
	public String getSolutionCode(String solutionId,String datasource,String userName,String dataPd){
		logger.debug("getSolution start");
		String toolKitTypeCode="";
		try{
		CommonDataServiceRestClientImpl cmnDataService=getClient(datasource,userName,dataPd);
		MLPSolution mlpSolution = cmnDataService.getSolution(solutionId);
			if (mlpSolution != null) {
				logger.debug("mlpSolution.getToolkitTypeCode() "+mlpSolution.getToolkitTypeCode());
				toolKitTypeCode=mlpSolution.getToolkitTypeCode();
			}
		}catch(Exception e){
			logger.error("Error in get solution "+e.getMessage());
			toolKitTypeCode="";
		}
		logger.debug("getSolution End toolKitTypeCode " +toolKitTypeCode);	
	  return toolKitTypeCode;
	 }
	public String getRepositoryName(String imageName){
		logger.debug("Start-geRepositoryName "+imageName);
		String repositaryName="";
		if(imageName!=null){
			String imageArr[]=imageName.split("/");
			if(imageArr!=null && imageArr[0]!=null){
				repositaryName=imageArr[0];
			}
		}
		logger.debug(" End geRepositoryName repositaryName"+repositaryName);
		return repositaryName;
	  }
	public StringBuffer getRandomPassword(int len) 
	{ 
		logger.debug("getRandomPassword Start"); 
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
		String Small_chars = "abcdefghijklmnopqrstuvwxyz"; 
		String numbers = "0123456789"; 
		String symbols = "!@#$%^&*_=+-/.?<>)"; 
        String values = Capital_chars + Small_chars + numbers + symbols; 
        Random rndm_method = new Random(); 
        StringBuffer pass = new StringBuffer(); 
		for (int i = 0; i < len; i++) 
		{ 
		   char charVal=values.charAt(rndm_method.nextInt(values.length()));
		   pass = pass.append(charVal);
		} 
		logger.debug("getRandomPassword End pass: "+pass); 
		return pass; 
	}
	public String getFileDetails(String fileDetails) throws Exception{
		String content="";
		logger.debug("fileDetails "+fileDetails);
		BufferedReader reader = new BufferedReader(new FileReader(fileDetails));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();

		content = stringBuilder.toString();
		return content;
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
	public void pullImageFromRepository(String userName,String userPd,String image,DockerClient dockerClient) {
		logger.debug("Start pullImageFromRepository ");
		logger.debug("userName "+userName+" userPd "+userPd+" image "+image);
		if(userName!=null && !"".equals(userName)) {
			AuthConfig authConfig = new AuthConfig().withUsername(userName).withPassword(userPd);
			dockerClient.pullImageCmd(image).withAuthConfig(authConfig).exec(new PullImageResultCallback()).awaitSuccess();
		}else {
			dockerClient.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();
		}
		
		logger.debug("End pullImageFromRepository  ");
	}
	
}
