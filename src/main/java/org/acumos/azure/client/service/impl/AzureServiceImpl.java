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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.service.AzureService;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Ports;
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

public class AzureServiceImpl implements AzureService {
	
	Logger logger =LoggerFactory.getLogger(AzureServiceImpl.class);
	
   private Environment env;
	
	public void setEnvironment(Environment envrionment){
	 this.env=envrionment;
	}
	
	public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		logger.debug("<------start----getClient------------>");
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		logger.debug("<------End----getClient---------client--->"+client);
		return client;
	}
	public NexusArtifactClient nexusArtifactClient(String nexusUrl, String nexusUserName,String nexusPassword) {
		logger.debug("<------start----nexusArtifactClient------------>");
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusUrl);
		repositoryLocation.setUsername(nexusUserName);
		repositoryLocation.setPassword(nexusPassword);
		NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);
		logger.debug("<------End----nexusArtifactClient------------>");
		return nexusArtifactClient;
	}
	@Override
	public Azure authorize(AzureDeployDataObject authObject) {
		// TODO Auto-generated method stub
		logger.debug("<------start----authorize in AzureServiceImpl------------>");
		logger.debug(" authentication parameters:: "+ authObject.toString() );
		
		ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(
				authObject.getClient(), authObject.getTenant(), authObject.getKey(), AzureEnvironment.AZURE);

		Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC)
				.authenticate(credentials)
				.withSubscription(authObject.getSubscriptionKey());
		logger.debug("try getting some info : " + azure.subscriptionId() + " " + azure.containerRegistries());
		logger.debug("Azure AD Authorization Successful...");
		logger.debug("<------End----authorize in AzureServiceImpl------------>");
		return azure;
	}
	
	public ArrayList<String> iterateImageMap(HashMap<String,String> imageMap){
		logger.debug("<--Start-------iterateImageMap-------imageMap---->"+imageMap);
		ArrayList<String> list=new ArrayList<String>();
		 Iterator it = imageMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        logger.debug(pair.getKey() + " = " + pair.getValue());
		        if(pair.getKey()!=null){
		        	list.add((String)pair.getKey());
		        }
		    }
		logger.debug("<--End-------iterateImageMap-------list---->"+list);
		return list;
	}
	public String getBluePrintNexus(String solutionId, String revisionId,String datasource,String userName,String password,
			String nexusUrl,String nexusUserName,String nexusPassword) throws  Exception{
		  logger.debug("------ Start getBluePrintNexus-----------------");
		  logger.debug("-------solutionId-----------"+solutionId);
		  logger.debug("-------revisionId-----------"+revisionId);
		  List<MLPSolutionRevision> mlpSolutionRevisionList;
		  String solutionRevisionId = revisionId;
		  List<MLPArtifact> mlpArtifactList;
		  String nexusURI = "";
		  String artifactType="BP";
		  String bluePrintStr="";
		  ByteArrayOutputStream byteArrayOutputStream = null;
		  CommonDataServiceRestClientImpl cmnDataService=getClient(datasource,userName,password);
			if (null != solutionRevisionId) {
				// 3. Get the list of Artifiact for the SolutionId and SolutionRevisionId.
				mlpArtifactList = cmnDataService.getSolutionRevisionArtifacts(solutionId, solutionRevisionId);
				if (null != mlpArtifactList && !mlpArtifactList.isEmpty()) {
					nexusURI = mlpArtifactList.stream()
							.filter(mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)).findFirst()
							.get().getUri();
					logger.debug("------ Nexus URI : " + nexusURI + " -------");
					if (null != nexusURI) {
						NexusArtifactClient nexusArtifactClient=nexusArtifactClient(nexusUrl,nexusUserName,nexusPassword);
						File f = new File("blueprint.json");
						if(f.exists() && !f.isDirectory()) { 
						    f.delete();
						}
						byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
						logger.debug("------- byteArrayOutputStream ---blueprint.json-------"+byteArrayOutputStream.toString());
						OutputStream outputStream = new FileOutputStream("blueprint.json"); 
						byteArrayOutputStream.writeTo(outputStream);
						bluePrintStr=byteArrayOutputStream.toString();
					}
				}
			}	
			File file = new File("blueprint.json");
			if(!file.exists()){
				 throw  new Exception("blueprint.json file is not exist");
			}
			logger.debug("------ End getBluePrintNexus-----------------");	
		return bluePrintStr;	
	  }
	  private List<MLPSolutionRevision> getSolutionRevisionsList(String solutionId,String datasource,String userName,String password)throws  Exception{
			logger.debug("------- getSolutionRevisions() : Start ----------");
			List<MLPSolutionRevision> solRevisionsList = null;
			CommonDataServiceRestClientImpl cmnDataService=getClient(datasource,userName,password);
			solRevisionsList = cmnDataService.getSolutionRevisions(solutionId);
			logger.debug("------- getSolutionRevisions() : End ----------");
			return solRevisionsList;
		}
	  public LinkedList<String> getSequence(HashMap<String,String> hmap){
			LinkedList<String> sequenceList=new LinkedList<String>();
			Iterator itrContainer=hmap.entrySet().iterator();
	        while(itrContainer.hasNext()){
	        	
	        	Map.Entry pair = (Map.Entry)itrContainer.next();
	        	String containerName=(String)pair.getKey();
	        	sequenceList.add((String)pair.getValue());
	        }
	        logger.debug("======sequenceList=============="+sequenceList);
	        return sequenceList;
		}
	  public LinkedList<String> addProbeSequence(LinkedList<String> sequenceList,String probeContainerName){
		  logger.debug("Start===addProbeSequence============");
		  logger.debug("====probeContainerName==="+probeContainerName+"====sequenceList====="+sequenceList);
		  if(sequenceList!=null && sequenceList.size() > 0 && probeContainerName!=null && !"".equals(probeContainerName)){
			  int length=sequenceList.size();
			  logger.debug("length============"+length);
			  sequenceList.add((length-1), probeContainerName); 
			}
		  logger.debug("End====addProbeSequence==============="+sequenceList);
		  return sequenceList;
	  }
}
