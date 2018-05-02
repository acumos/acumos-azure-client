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

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;

public class AzureCommonUtil {
	Logger logger = LoggerFactory.getLogger(AzureCommonUtil.class);
	
	/**
	 * 
	 * @param notificationId
	 * @param userId
	 */
	/**
	 * 
	 * @param mlpNotification
	 * @return
	 */
	 public org.acumos.azure.client.transport.MLNotification createNotification(MLPNotification mlpNotification,String dataSource,
			 String dataUserName,String dataPassword) {
		 logger.debug("Start===createNotification============");
         CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
         MLNotification mlNotification = Utils.convertToMLNotification(client.createNotification(mlpNotification));
         logger.debug("End===createNotification============");
         return mlNotification;
	 }
	 
	/**
	  * 
	  * @param msg
	  * @param userId
	  */
	 public void generateNotification(String msg, String userId,String dataSource,String dataUserName,String dataPassword)throws Exception {
		 logger.debug("Start===generateNotification============");
		 logger.debug("=====userId====="+userId+"==msg==="+msg);
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
                     logger.debug("=====mLNotification.getNotificationId()====="+mLNotification.getNotificationId());
                     client.addUserToNotification(mLNotification.getNotificationId(),userId);
             }
         } catch (Exception e) {
        	 logger.error("generateNotification failed", e);
        	 throw new Exception(e.getMessage());
         }
         logger.debug("End===generateNotification============"); 
	 }
	 public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
			CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
			return client;
		}
	 public NexusArtifactClient nexusArtifactClientDetails(String nexusUrl, String nexusUserName,String nexusPassword) {
			logger.debug("<------start----nexusArtifactClientDetails------------>");
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusUrl);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);
			logger.debug("<------End----nexusArtifactClientDetails------------>");
			return nexusArtifactClient;
	}
	 public ByteArrayOutputStream getNexusUrlFile(String nexusUrl, String nexusUserName,String nexusPassword,String nexusURI)throws Exception {
			logger.debug("<------start----getNexusUrlFile------------>");
			ByteArrayOutputStream byteArrayOutputStream=null;
			try
			{
				NexusArtifactClient nexusArtifactClient=nexusArtifactClientDetails(nexusUrl, 
						nexusUserName, nexusPassword);
				 byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
				 logger.debug("<------byteArrayOutputStream------>"+byteArrayOutputStream);
			}catch (Exception e) {
				 logger.error("getNexusUrlFile failed", e);
				 throw new Exception(e.getMessage());
      }
			logger.debug("<------End----getNexusUrlFile------------>");
			return byteArrayOutputStream;
	}
	 
   public String replaceCharStr(String commonStr,String replaceChar,String replaceWithChar){
	   logger.debug("<------start----replaceCharStr------------>");
	   String finalStr="";
	   if(commonStr!=null && !"".equals(commonStr) && replaceChar!=null && !"".equals(replaceChar)
			   && replaceWithChar!=null && !"".equalsIgnoreCase(replaceWithChar)){
		   finalStr = commonStr.replace(replaceChar, replaceWithChar);
	   }else{
		   finalStr=commonStr; 
	   }
	   logger.debug("<------start----replaceCharStr------finalStr------>"+finalStr);
	   return finalStr;
   }
}
