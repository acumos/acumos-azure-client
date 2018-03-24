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

package org.acumos.azure.client.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.azure.client.api.APINames;
import org.acumos.azure.client.service.impl.AzureCompositeSolution;
import org.acumos.azure.client.service.impl.AzureServiceImpl;
import org.acumos.azure.client.service.impl.AzureSimpleSolution;
import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SingletonMapClass;
import org.acumos.azure.client.utils.AppProperties;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.management.Azure;
@RestController
public class AzureServiceController extends AbstractController {

	private AppProperties app;

	@Autowired
	public void setApp(AppProperties app) {
		this.app = app;
	}

	@Autowired
	private Environment env;

	Logger logger = LoggerFactory.getLogger(AzureServiceController.class);
	
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_SINGLE_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String singleImageAzureDeployment(HttpServletRequest request,@RequestBody AzureDeployBean auth,HttpServletResponse response) throws Exception {
		logger.debug("<------start----singleImageAzureDeployment------------>");
		JSONObject  jsonOutput = new JSONObject();
		
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPassword="";
		try {
			azureImpl.setEnvironment(env);
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			//String dockerRegistryPort=env.getProperty("docker.registry.port");
			String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			String dataUserName=env.getProperty("cmndatasvc.cmndatasvcuser");
			String dataPassword=env.getProperty("cmndatasvc.cmndatasvcpwd");
			dockerVMUserName=env.getProperty("docker.dockerVMUserName");
			dockerVMPassword=env.getProperty("docker.dockerVMPassword");
			String solutionPort=env.getProperty("docker.solutionPort");
			logger.debug("<------solutionPort---------->"+solutionPort);
			logger.debug("<------dockerVMUserName---------->"+dockerVMUserName);
			logger.debug("<------dockerVMPassword---------->"+dockerVMPassword);
			dockerVMUserName="dockerUser";
			dockerVMPassword="12NewPA$$w0rd!";	
			logger.debug("<------dockerVMUserName---2------->"+dockerVMUserName);
			logger.debug("<------dockerVMPassword-----2----->"+dockerVMPassword);
			/*
			String nexusUrl=env.getProperty("nexus.url");
			String nexusUserName=env.getProperty("nexus.username");
			String nexusPassword=env.getProperty("nexus.password");*/
			String dockerRegistryname=env.getProperty("docker.registry.name");
			logger.debug("<------dockerRegistryname---------->"+dockerRegistryname);
            logger.debug("<-------AcrName----->"+auth.getAcrName());
    		logger.debug("<--------Key---->"+auth.getKey());
    		logger.debug("<-------Imagetag----->"+auth.getImagetag());
    		logger.debug("<-------RgName----->"+auth.getRgName());
    		logger.debug("<--------Client---->"+auth.getClient());
    		logger.debug("<--------SubscriptionKey---->"+auth.getSubscriptionKey());
    		logger.debug("<---------StorageAccount--->"+auth.getStorageAccount());
    		logger.debug("<---------Tenant--->"+auth.getTenant());
            AzureDeployDataObject authObject=new AzureDeployDataObject();
            ArrayList<String> list=new ArrayList<String>();
            list.add(auth.getImagetag());
            if(auth.getAcrName()!=null){
            	authObject.setAcrName(auth.getAcrName());
            }
            if(auth.getKey()!=null){
            	authObject.setKey(auth.getKey());
            }
            /*if(auth.getImagetag()!=null){
            	authObject.set(auth.getAcrName());
            }*/
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
            
            Azure azure = azureImpl.authorize(authObject);
            /*if(azure!=null) {
            	azBean=azureImpl.pushSingleImage(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,dockerRegistryname);
            }*/
            AzureSimpleSolution myRunnable = new AzureSimpleSolution(azure,authObject,env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
            		env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"),false),
            				null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryname,uidNumStr,dataSource,dataUserName,dataPassword,
            				dockerVMUserName,dockerVMPassword,solutionPort);
            
            Thread t = new Thread(myRunnable);
            t.start();
            jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
            response.setStatus(200);
		}catch(Exception e){
			logger.error("<-----Exception in singleImageAzureDeployment------------>"+e.getMessage());
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("<------End----singleImageAzureDeployment------------>"+jsonOutput.toString());
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_COMPOSITE_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String compositeSolutionAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		logger.debug("<------start----compositeSolutionAzureDeployment------------>");
		JSONObject  jsonOutput = new JSONObject();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPassword="";
		String jsonFileName="blueprint.json";
		try {
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty("blueprint.ImageName");
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			
			//probe
			String probePrintImage=env.getProperty("probe.ImageName");
			String probePrintName=env.getProperty("probe.name");
			String probeInternalPort=env.getProperty("probe.internalPort");
			String probUser=env.getProperty("docker.registry.probe.username");
			String probePass=env.getProperty("docker.registry.probe.password");
			
			logger.debug("<------probePrintImage---------->"+probePrintImage);
			logger.debug("<------probePrintName---------->"+probePrintName);
			logger.debug("<------probeInternalPort---------->"+probeInternalPort);
			logger.debug("<------probUser---------->"+probUser);
			logger.debug("<------probePass---------->"+probePass);
			
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			//String dockerRegistryPort=env.getProperty("docker.registry.port");
			String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
			String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
			String nexusUrl=env.getProperty("nexus.url");
			String nexusUserName=env.getProperty("nexus.username");
			String nexusPassword=env.getProperty("nexus.password");
			String dockerRegistryname=env.getProperty("docker.registry.name");
			logger.debug("<------dockerRegistryname---------->"+dockerRegistryname);
			dockerVMUserName=env.getProperty("docker.dockerVMUserName");
			dockerVMPassword=env.getProperty("docker.dockerVMPassword");
			logger.debug("<------dockerVMUserName---------->"+dockerVMUserName);
			logger.debug("<------dockerVMPassword---------->"+dockerVMPassword);
			dockerVMUserName="dockerUser";
			dockerVMPassword="12NewPA$$w0rd!";	
			logger.debug("<------dockerVMUserName---2------->"+dockerVMUserName);
			logger.debug("<------dockerVMPassword-----2----->"+dockerVMPassword);
			String solutionPort=env.getProperty("docker.solutionPort");
			logger.debug("<------solutionPort---------->"+solutionPort);
			if (authObject == null) {
				logger.debug("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
           
            logger.debug("<------authObject.getUrlAttribute()---------->"+authObject.getUrlAttribute());
            logger.debug("<-----authObject.getJsonMapping()---------->"+authObject.getJsonMapping());
            logger.debug("<-----authObject.getJsonPosition()---------->"+authObject.getJsonPosition());
            logger.debug("<------SolutionId---------->"+authObject.getSolutionId());
			logger.debug("<------authObject.getSolutionRevisionId()---------->"+authObject.getSolutionRevisionId());
			
			Azure azure = azureImpl.authorize(authObject);
			logger.debug("<---------Azure Authentication Complete----------->");
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionRevisionId(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			logger.debug("<------bluePrintJsonStr---------->"+bluePrintJsonStr);
			ParseJSON parseJson=new ParseJSON();
			
			// Commented from 246 to 252 on 22-Feb-18 to support support probe.
			
			/*Blueprint bluePrint=parseJson.jsonFileToObject();
			//how many images
			HashMap<String,String> imageMap=parseJson.parseJsonFile();
			// images list
			ArrayList<String> list=azureImpl.iterateImageMap(imageMap);
			//sequence
			LinkedList<String> sequenceList=parseJson.getSequenceFromJSON();*/
			boolean probeIndicator=parseJson.checkProbeIndicator(jsonFileName);
			Blueprint bluePrintProbe=null;
			HashMap<String,String> imageMap=null;
			HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
			ArrayList<String> list=null;
			LinkedList<String> sequenceList=null;
			logger.debug("<------probeIndicator---------->"+probeIndicator);
			if(probeIndicator){
				//-------------- New Probe Start ------------------- ***
				//For new blueprint.json
				 bluePrintProbe =parseJson.jsonFileToObjectProbe(jsonFileName);
				//how many images
				imageMap=parseJson.parseJsonFileProbe(jsonFileName);
				//Node Type and container Name in nodes
				nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(jsonFileName);
				// images list
				list=azureImpl.iterateImageMap(imageMap);
				
				//sequence
				sequenceList=parseJson.getSequenceFromJSONProbe(jsonFileName);
			}else{
				//old code 
				bluePrintProbe=parseJson.jsonFileToObject(jsonFileName);
				imageMap=parseJson.parseJsonFile(jsonFileName);
				list=azureImpl.iterateImageMap(imageMap);
				sequenceList=parseJson.getSequenceFromJSON(jsonFileName);
			}
			
			
			//-------------- New Probe Start ------------------- ***

			logger.debug("<------bluePrintProbe.getProbeIndocator()---------->"+bluePrintProbe.getProbeIndocator());
			
			ArrayList<ProbeIndicator> probeIndicatorList = bluePrintProbe.getProbeIndocator();
			ProbeIndicator prbIndicator = null;
			if(probeIndicatorList != null && probeIndicatorList.size() >0) {
				prbIndicator = probeIndicatorList.get(0);
			}			
		    if (bluePrintProbe.getProbeIndocator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True") ) {

				if (probePrintImage != null && !"".equals(probePrintImage)) {
					list.add(probePrintImage);
					imageMap.put(probePrintImage, "Probe");
					sequenceList=azureImpl.addProbeSequence(sequenceList,"Probe");
				}
			}	

			/*if (bluePrintProbe.getProbeIndocator() != null && bluePrintProbe.getProbeIndocator().equalsIgnoreCase("True") ) {

				if (probePrintImage != null && !"".equals(probePrintImage)) {
					list.add(probePrintImage);
					imageMap.put(probePrintImage, "Probe");
					sequenceList=azureImpl.addProbeSequence(sequenceList,"Probe");
				}
			}*/
			
			if (bluePrintImage != null && !"".equals(bluePrintImage)) {
				list.add(bluePrintImage);
				imageMap.put(bluePrintImage, "BluePrintContainer");
			}
			
						
			//put condition to get probe
			
			logger.debug("<------list------------------>"+list);
			logger.debug("<------imageMap-------------->"+imageMap);
			logger.debug("<------sequenceList---------->"+sequenceList);
			if(azure!=null) {
				AzureCompositeSolution compositeRunner =new AzureCompositeSolution(azure,authObject,env.getProperty("docker.containerNamePrefix"),env.getProperty("docker.registry.username"),
                        env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), 
                        env.getProperty("docker.port"), false),null,list,bluePrintName,bluePrintUser,bluePrintPass,probeInternalPort,probePrintName,probUser,probePass,networkSecurityGroup,imageMap,
                        sequenceList,dockerRegistryname,bluePrintProbe,uidNumStr,dataSource,userName,password,dockerVMUserName,dockerVMPassword,solutionPort,nodeTypeContainerMap,bluePrintJsonStr);

	              Thread t = new Thread(compositeRunner);
                   t.start();
				/*  azBean=azureImpl.pushCompositeImages(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,imageMap,sequenceList,dockerRegistryname);*/
				  
				  /*if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }*/
				  /*dockerInfoList=azBean.getDockerinfolist();
				  vmIP=azBean.getAzureVMIP().trim();
				  bluePrintPort=azBean.getBluePrintPort().trim();*/
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				  
			}
			
			  /*logger.debug("Dockerinfolist=============="+mapper.writeValueAsString(azBean.getDockerinfolist()));
			  logger.debug("bluePrint==================="+mapper.writeValueAsString(bluePrint));
			 
			    String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
				String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
				logger.debug("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
			  if(azBean.getDockerinfolist()!=null){
					azureImpl.putContainerDetails(azBean.getDockerinfolist(),urlDockerInfo);
				}
				if(bluePrint!=null){
					azureImpl.putBluePrintDetails(bluePrint,urlBluePrint);
				}*/
				response.setStatus(200);	
		}catch(Exception e){
			logger.error("<-----Exception in compositeSolutionAzureDeployment------------>"+e.getMessage());
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("<------jsonOutput.toString()---------->"+jsonOutput.toString());
		return jsonOutput.toString();
	}
    
	@RequestMapping(value ="/getUIDDetails",  method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public String  getUIDDetails(@RequestParam("uidNumber") String uidNumber) throws Exception {
		logger.debug("<------Start -----getUIDDetails----->");
		HashMap<String,String> singlatonMap=SingletonMapClass.getInstance();
		JSONObject  jsonOutput = new JSONObject();
		String uidOutput="";
		if(singlatonMap!=null){
			if(singlatonMap.get(uidNumber)!=null){
				uidOutput=singlatonMap.get(uidNumber);
			}
		}
		logger.debug("<------End getUIDDetails---------->");
		jsonOutput.put("UIDNumber", uidOutput);
		return jsonOutput.toString();
	}
	
	private String dockerHosttoUrl(String host, String port, boolean socket) {
		return ((socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

}
