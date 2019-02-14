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
import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
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
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.Period;	

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
         MLNotification mlNotification = convertToMLNotification(client.createNotification(mlpNotification));
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
                     Instant startDate = Instant.now();
     				 Instant endDate = startDate.plus(Period.ofDays(365));
                     notification.setStart(startDate);
                     notification.setEnd(endDate);
                     notification.setCreated(startDate);
                     CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
                     notification.setMsgSeverityCode(AzureClientConstants.MSG_SEVERITY_ME);
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
	public  StringBuffer getRandomPassword(int len) 
	{ 
		logger.debug("getRandomPassword Start"); 
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
		String Small_chars = "abcdefghijklmnopqrstuvwxyz"; 
		String numbers = "0123456789"; 
		String symbols = "!@#$%^&*=?<>)"; 
		int indexCap=getRandomChar(Capital_chars);
		char charCap=Capital_chars.charAt(indexCap);
		
		int indexSmall=getRandomChar(Small_chars);
		char charSmall=Small_chars.charAt(indexSmall);
		
		int indexNum=getRandomChar(numbers);
		char charNum=numbers.charAt(indexNum);
		
		int indexSymbol=getRandomChar(symbols);
		char charSymbol=symbols.charAt(indexSymbol);
		
		logger.debug(charCap +" "+charSmall+" "+charNum+" "+charSymbol);
		
        String values = Capital_chars + Small_chars + numbers + symbols; 
        Random rndm_method = new Random(); 
        StringBuffer pass = new StringBuffer(); 
        pass = pass.append(charCap);
        pass = pass.append(charSmall);
        pass = pass.append(charNum);
        pass = pass.append(charSymbol);
		for (int i = 0; i < (len-4); i++) 
		{ 
		   char charVal=values.charAt(rndm_method.nextInt(values.length()));
		   pass = pass.append(charVal);
		} 
		logger.debug("getRandomPassword End"); 
		return pass; 
	}
	
	public  int getRandomChar(String charString) {
		logger.debug("getRandomChar Start"); 
		int index=0;
		if(charString!=null) {
			int len=charString.length();
			int randomVal=(int)(Math.random()*100);
			while(true){
				logger.debug("randomVal "+randomVal);
	            if(randomVal >(len-1)){
	            	randomVal=(int)(randomVal/2);
	            }else {
	            	index=randomVal;
	            	break;
	            }
	        }
			logger.debug("index "+index);
			
		}
		logger.debug("getRandomChar end"); 
		return index;
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
	
	public static MLNotification convertToMLNotification(MLPNotification mlpNotification) {
		MLNotification mlNotification = new MLNotification();
		if (!isEmptyOrNullString(mlpNotification.getNotificationId())) {
			mlNotification.setNotificationId(mlpNotification.getNotificationId());
		}
		if (!isEmptyOrNullString(mlpNotification.getTitle())) {
			mlNotification.setTitle(mlpNotification.getTitle());
		}
		if (!isEmptyOrNullString(mlpNotification.getMessage())) {
			mlNotification.setMessage(mlpNotification.getMessage());
		}
		if (!isEmptyOrNullString(mlpNotification.getUrl())) {
			mlNotification.setUrl(mlpNotification.getUrl());
		}
		if (mlpNotification.getStart() != null) {
			mlNotification.setStart(mlpNotification.getStart());
		}
		if (mlpNotification.getEnd() != null) {
			mlNotification.setEnd(mlpNotification.getEnd());
		}
		return mlNotification;
	}
	
	
	public static boolean isEmptyOrNullString(String input) {
		boolean isEmpty = false;
		if (null == input || 0 == input.trim().length()) {
			isEmpty = true;
		}
		return isEmpty;
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
	
	public void createDeploymentCompositeData(String dataSource,String dataUserName,String dataPd,List<AzureContainerBean> azureContainerBeanList,
			String solutionId,String solutionRevisionId,String userId,String uidNumber,String deploymentStatusCode) throws Exception{
		logger.debug("createDeploymentCompositeData start");
		logger.debug("solutionId "+solutionId);
		logger.debug("solutionRevisionId "+solutionRevisionId);
		logger.debug("userId "+userId);
		logger.debug("uidNumber "+uidNumber);
		logger.debug("deploymentStatusCode "+deploymentStatusCode);
		logger.debug("azureContainerBeanList "+azureContainerBeanList);
		ObjectMapper mapper = new ObjectMapper();
		 AzureCommonUtil azureUtil=new AzureCommonUtil();
		CommonDataServiceRestClientImpl client=azureUtil.getClient(dataSource,dataUserName,dataPd);
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
	
	public MLPSolutionDeployment createDeploymentData(String dataSource, String dataUserName, String dataPd,
			AzureContainerBean containerBean, String solutionId, String solutionRevisionId, String userId,
			String uidNumber, String deploymentStatusCode) throws Exception {
			logger.debug(" createDeploymentData Start");
			logger.debug("solutionId " + solutionId);
			logger.debug("solutionRevisionId " + solutionRevisionId);
			logger.debug("userId " + userId);
			logger.debug("uidNumber " + uidNumber);
			logger.debug("deploymentStatusCode " + deploymentStatusCode);
			MLPSolutionDeployment mlpDeployment=null;
			ObjectMapper mapper = new ObjectMapper();
			CommonDataServiceRestClientImpl client = getClient(dataSource, dataUserName, dataPd);
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
	public void putDataBrokerDetails(String urlAttribute,String jsonMapping,String jsonPosition,String apiUrl)throws Exception{
		logger.debug("putDataBrokerDetails Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			logger.debug("UrlAttribute "+urlAttribute);
			logger.debug("JsonMapping "+jsonMapping);
			logger.debug("JsonPosition "+jsonPosition);
			//logger.debug("=====dataBrokerScript==="+dataBrokerScript);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("jsonUrl", urlAttribute);
			//map.add("jsonScript", dataBrokerScript);
			map.add("jsonMapping", jsonMapping);
			map.add("jsonPosition", jsonPosition);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			restTemplate.exchange(apiUrl, HttpMethod.PUT, request, String.class);
		    
		  } catch (Exception e) {
			  logger.error("generateNotification failed", e);
			  throw e;
		 }
		logger.debug("putDataBrokerDetails End");
	}
	
	 public void callCsvConfigDB(String userName,String UserPd,String host,String port,String apiUrl,DataBrokerBean dataBrokerBean)throws Exception{
			logger.debug("callCsvConfigDB Start");
			try {
				logger.debug("apiUrl "+apiUrl);
				final String url = apiUrl;
				dataBrokerBean.setUserName(userName);
				dataBrokerBean.setUserPd(UserPd);
				dataBrokerBean.setHost(host);
				dataBrokerBean.setPort(port);
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
	 public TransportBean setTransportValues(TransportBean tbean,String nexusUrl,String nexusUserName,String nexusPd,String nginxMapFolder,
			 String nginxWebFolder,String nginxImageName,String nginxInternalPort,String azureDataFiles,String probePrintImage,
			 String bluePrintImage,String exposeDataBrokerPort,String internalDataBrokerPort, String probeInternalPort,
			 String nexusRegistyName,String nexusRegistyUserName,String nexusRegistyPd,String registryUserName,
			 String registryPd,String bluePrintUser,String bluePrintPass,String probUser,String probePass,String uidNumStr,String solutionPort,
			 String dataSource,String dataUserName,String dataPd,String sleepTimeFirst,String localHostEnv) {
		        logger.debug("setTransportValues Start");
			    tbean.setNexusUrl(nexusUrl);
				tbean.setNexusUserName(nexusUserName);	
				tbean.setNexusPd(nexusPd);
				tbean.setNginxMapFolder(nginxMapFolder);
				tbean.setNginxWebFolder(nginxWebFolder);
				tbean.setNginxImageName(nginxImageName);
				tbean.setNginxInternalPort(nginxInternalPort);
				tbean.setAzureDataFiles(azureDataFiles);
				tbean.setProbePrintImage(probePrintImage);
				tbean.setBluePrintImage(bluePrintImage);
				tbean.setNginxInternalPort(nginxInternalPort);
				tbean.setExposeDataBrokerPort(exposeDataBrokerPort);
				tbean.setInternalDataBrokerPort(internalDataBrokerPort);
				tbean.setProbeInternalPort(probeInternalPort);
				tbean.setNexusRegistyName(nexusRegistyName);
				tbean.setNexusRegistyUserName(nexusRegistyUserName);
				tbean.setNexusRegistyPd(nexusRegistyPd);
				tbean.setRegistryUserName(registryUserName);
				tbean.setRegistryPd(registryPd);
				tbean.setBluePrintUser(bluePrintUser);
				tbean.setBluePrintPass(bluePrintPass);
				tbean.setProbUser(probUser);
				tbean.setProbePass(probePass);
				tbean.setUidNumStr(uidNumStr);
				tbean.setSolutionPort(solutionPort);
				tbean.setDataSourceUrl(dataSource);
				tbean.setDataSourceUserName(dataUserName);
				tbean.setDataSourcePd(dataPd);
				tbean.setSleepTimeFirst(sleepTimeFirst);
				tbean.setLocalHostEnv(localHostEnv);
				logger.debug("setTransportValues End");
			return tbean;
	 }

	
}
