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


import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.azure.client.AzureClientServiceApplication;
import org.acumos.azure.client.api.APINames;
import org.acumos.azure.client.service.impl.AzureServiceImpl;
import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.Subnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@CrossOrigin
	//@ApiOperation(value = "Gets a page of solutions matching the specified tag.", response = String.class, responseContainer = "Page")
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String authorizeAndPushImage(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		
		
		JSONObject  jsonOutput = new JSONObject();
		logger.info("<------start----authorizeAndPushImage in AzureServiceController------------>");
		DockerInfoList  dockerList=new DockerInfoList();
		AzureBean azBean=new AzureBean();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		try {
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty("blueprint.ImageName");
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			String dockerRegistryPort=env.getProperty("docker.registry.port");
			String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
			String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
			String nexusUrl=env.getProperty("nexus.url");
			String nexusUserName=env.getProperty("nexus.username");
			String nexusPassword=env.getProperty("nexus.password");
			String dockerRegistryname=env.getProperty("docker.registry.name");
			logger.info("<------dockerRegistryname---------->"+dockerRegistryname);
			if (authObject == null) {
				System.out.println("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
			ObjectMapper mapper = new ObjectMapper();
            
			Azure azure = azureImpl.authorize(authObject);	
			/*List<NetworkSecurityGroup> networkSecurityGroups = azure.networkSecurityGroups().list();
	        for (NetworkSecurityGroup networkSecurityGroup: networkSecurityGroups) {
	            Utils.print(networkSecurityGroup);
	            List<Subnet> subnetList=networkSecurityGroup.listAssociatedSubnets();
	            
	        }*/
			logger.info("<------SolutionId---------->"+authObject.getSolutionId());
			logger.info("<------SolutionVersion---------->"+authObject.getSolutionVersion());
			//azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionVersion(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			
			
			/*ParseJSON parseJson=new ParseJSON();
			Blueprint bluePrint=parseJson.jsonFileToObject();*/
			
			HashMap<String,String> imageMap=new HashMap<String,String>();//parseJson.parseJsonFile();
			ArrayList<String> list=new ArrayList<String>();//azureImpl.iterateImageMap(imageMap);
//			Blueprint bluePrint=parseJson.jsonFileToObject();
			
			logger.info("<------bluePrintImage---------->"+bluePrintImage);
			String vmIP="";
			String bluePrintPort="";
			DockerInfoList dockerInfoList=new DockerInfoList();
			if(bluePrintImage!=null && !"".equals(bluePrintImage)){
				list.add(bluePrintImage);
				list.add("cognita-nexus01:8001/adder:2");
				imageMap.put(bluePrintImage, "BluePrintContainer");
				imageMap.put("cognita-nexus01:8001/adder:2", "Adder_Container");
			}
			LinkedList<String> sequenceList=new LinkedList<String>();//parseJson.getSequenceFromJSON();//parseJson.getSequenceFromJSON();
			sequenceList.add("BluePrintContainer");
			sequenceList.add("Adder_Container");
			 //Authorization done, now try to push the image
			  if(azure!=null) {
				  azBean=azureImpl.pushImage(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,imageMap,sequenceList,dockerRegistryname);
				  
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  dockerInfoList=azBean.getDockerinfolist();
				  vmIP=azBean.getAzureVMIP().trim();
				  bluePrintPort=azBean.getBluePrintPort().trim();
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
			}
			  Thread.sleep(30000);
			  //new call 
			  /*logger.info("Dockerinfolist=============="+mapper.writeValueAsString(azBean.getDockerinfolist()));
			  logger.info("bluePrint==================="+mapper.writeValueAsString(bluePrint));
			 
			    String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
				String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
				logger.info("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
			  if(azBean.getDockerinfolist()!=null){
					azureImpl.putContainerDetails(azBean.getDockerinfolist(),urlDockerInfo);
				}
				if(bluePrint!=null){
					azureImpl.putBluePrintDetails(bluePrint,urlBluePrint);
				}*/
			response.setStatus(200);	
			
		} catch (com.microsoft.aad.adal4j.AuthenticationException e) {
			System.out.println(e.getMessage() + " Returning... " + APINames.AUTH_FAILED );
			response.setStatus(401);
			jsonOutput.put("status", APINames.AUTH_FAILED);
			return jsonOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().contains("com.microsoft.aad.adal4j.AuthenticationException")) {
				//Authentication error, set status code 401
				response.setStatus(401);
				jsonOutput.put("status", APINames.AUTH_FAILED);
				logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
				return jsonOutput.toString();
			} else {
				// for now we do not want to treat any other error as error, return 200
				e.printStackTrace();
				logger.info(e.getMessage() + " Returning... " + APINames.SUCCESS_RESPONSE);
				response.setStatus(200);
				jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				return jsonOutput.toString();
			}
			
		}
		logger.info("<------End----authorizeAndPushImage in AzureServiceController------------>");
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_SINGLE_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String singleImageAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployBean auth, HttpServletResponse response) throws Exception {
		logger.info("<------start----singleImageAzureDeployment------------>");
		JSONObject  jsonOutput = new JSONObject();
		
		AzureBean azBean=new AzureBean();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		try {
			azureImpl.setEnvironment(env);
			
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			String dockerRegistryPort=env.getProperty("docker.registry.port");
			/*String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
			String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
			String nexusUrl=env.getProperty("nexus.url");
			String nexusUserName=env.getProperty("nexus.username");
			String nexusPassword=env.getProperty("nexus.password");*/
			String dockerRegistryname=env.getProperty("docker.registry.name");
			logger.info("<------dockerRegistryname---------->"+dockerRegistryname);
            ObjectMapper mapper = new ObjectMapper();
            logger.info("<-------AcrName----->"+auth.getAcrName());
    		logger.info("<--------Key---->"+auth.getKey());
    		logger.info("<-------Imagetag----->"+auth.getImagetag());
    		logger.info("<-------RgName----->"+auth.getRgName());
    		logger.info("<--------Client---->"+auth.getClient());
    		logger.info("<--------SubscriptionKey---->"+auth.getSubscriptionKey());
    		logger.info("<---------StorageAccount--->"+auth.getStorageAccount());
    		logger.info("<---------Tenant--->"+auth.getTenant());
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
            Azure azure = azureImpl.authorize(authObject);
            if(azure!=null) {
            	azBean=azureImpl.pushSingleImage(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,dockerRegistryname);
            }
            jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
            response.setStatus(200);
		}catch(Exception e){
			logger.error("<-----Exception in singleImageAzureDeployment------------>"+e.getMessage());
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
			return jsonOutput.toString();
		}
		logger.info("<------End----singleImageAzureDeployment------------>");
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_COMPOSITE_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String compositeSolutionAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		logger.info("<------start----singleImageAzureDeployment------------>");
		JSONObject  jsonOutput = new JSONObject();
		DockerInfoList  dockerList=new DockerInfoList();
		AzureBean azBean=new AzureBean();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		try {
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty("blueprint.ImageName");
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			String dockerRegistryPort=env.getProperty("docker.registry.port");
			String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
			String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
			String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
			String nexusUrl=env.getProperty("nexus.url");
			String nexusUserName=env.getProperty("nexus.username");
			String nexusPassword=env.getProperty("nexus.password");
			String dockerRegistryname=env.getProperty("docker.registry.name");
			DockerInfoList dockerInfoList=new DockerInfoList();
			String vmIP="";
			String bluePrintPort="";
			logger.info("<------dockerRegistryname---------->"+dockerRegistryname);
			if (authObject == null) {
				logger.info("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
			ObjectMapper mapper = new ObjectMapper();
            Azure azure = azureImpl.authorize(authObject);
            logger.info("<------SolutionId---------->"+authObject.getSolutionId());
			logger.info("<------SolutionVersion---------->"+authObject.getSolutionVersion());
			String bluePrintStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionVersion(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			logger.info("<------bluePrintStr---------->"+bluePrintStr);
			ParseJSON parseJson=new ParseJSON();
			Blueprint bluePrint=parseJson.jsonFileToObject();
			
			
			
			HashMap<String,String> imageMap=parseJson.parseJsonFile();
			ArrayList<String> list=azureImpl.iterateImageMap(imageMap);
			LinkedList<String> sequenceList=parseJson.getSequenceFromJSON();
			
			if(bluePrintImage!=null && !"".equals(bluePrintImage)){
				list.add(bluePrintImage);
				imageMap.put(bluePrintImage, "BluePrintContainer");
			}
			logger.info("<------list---------->"+list);
			logger.info("<------imageMap---------->"+imageMap);
			if(azure!=null) {
				  azBean=azureImpl.pushCompositeImages(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,imageMap,sequenceList,dockerRegistryname);
				  
				  /*if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }*/
				  dockerInfoList=azBean.getDockerinfolist();
				  vmIP=azBean.getAzureVMIP().trim();
				  bluePrintPort=azBean.getBluePrintPort().trim();
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				  
			}
			
			  logger.info("Dockerinfolist=============="+mapper.writeValueAsString(azBean.getDockerinfolist()));
			  logger.info("bluePrint==================="+mapper.writeValueAsString(bluePrint));
			 
			    String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
				String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
				logger.info("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
			  if(azBean.getDockerinfolist()!=null){
					azureImpl.putContainerDetails(azBean.getDockerinfolist(),urlDockerInfo);
				}
				if(bluePrint!=null){
					azureImpl.putBluePrintDetails(bluePrint,urlBluePrint);
				}
				response.setStatus(200);	
		}catch(Exception e){
			logger.error("<-----Exception in compositeSolutionAzureDeployment------------>"+e.getMessage());
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
			return jsonOutput.toString();
		}
		return jsonOutput.toString();
	}

	private String dockerHosttoUrl(String host, String port, boolean socket) {
		return ((socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

}
