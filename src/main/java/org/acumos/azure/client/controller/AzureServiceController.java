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
import java.io.ByteArrayOutputStream;

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
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
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
	
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_SINGLE_IMAGE}, method = RequestMethod.POST, produces = AzureClientConstants.APPLICATION_JSON)
	@ResponseBody
	public String singleImageAzureDeployment(HttpServletRequest request,@RequestBody AzureDeployBean auth,HttpServletResponse response) throws Exception {
		logger.debug("singleImageAzureDeployment start");
		JSONObject  jsonOutput = new JSONObject();
		
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPassword="";
		String dataSource="";
		String dataUserName="";
		String dataPassword="";
		String userId="";
		String subnet="";
		String vnet="";
		String replaceChar="";
		String ignorDoller="";
		String sleepTimeFirst="";
		String sleepTimeSecond="";
		String nexusRegistyUserName="";
		String nexusRegistyPwd="";
		String nexusRegistyName="";
		String otherRegistyName="";
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			azureImpl.setEnvironment(env);
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			String bluePrintName=env.getProperty(AzureClientConstants.BLUEPRINT_NAME_PROP);
			String bluePrintUser=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_USERNAME_PROP);
			String bluePrintPass=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_PASSWORD_PROP);
			String networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			dataSource=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			dataUserName=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			dataPassword=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPWD_PROP);
			dockerVMUserName=env.getProperty(AzureClientConstants.DOCKERVMUSERNAME_PROP);
			dockerVMPassword=env.getProperty(AzureClientConstants.DOCKERVMPASSWORD_PROP);
			replaceChar=env.getProperty(AzureClientConstants.REPLACECHAR_PROP);
			ignorDoller=env.getProperty(AzureClientConstants.IGNORE_DOLLER_PROP);
			dockerVMPassword=azureUtil.replaceCharStr(dockerVMPassword,replaceChar,ignorDoller);
			String solutionPort=env.getProperty(AzureClientConstants.SOLUTIONPORT_PROP);
			subnet=env.getProperty(AzureClientConstants.SUBNET_PROP);
			vnet=env.getProperty(AzureClientConstants.VNET_PROP);
			sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			sleepTimeSecond=env.getProperty(AzureClientConstants.SLEEPTIME_SECOND);
			nexusRegistyUserName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_USERNAME);
			nexusRegistyPwd=env.getProperty(AzureClientConstants.NEXUS_REGISTY_PWD);
			nexusRegistyName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_NAME);
			otherRegistyName=env.getProperty(AzureClientConstants.OTHER_REGISTY_NAME);
			logger.debug("nexusRegistyName "+nexusRegistyName);
			logger.debug("otherRegistyName "+otherRegistyName);
			logger.debug("solutionPort "+solutionPort);
			logger.debug("subnet "+subnet);
			logger.debug("vnet "+vnet);
			logger.debug("sleepTimeFirst "+sleepTimeFirst);
			logger.debug("sleepTimeSecond "+sleepTimeSecond);
			String dockerRegistryname=env.getProperty(AzureClientConstants.REGISTRY_NAME_PROP);
            AzureDeployDataObject authObject=new AzureDeployDataObject();
            ArrayList<String> list=new ArrayList<String>();
            list.add(auth.getImagetag());
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
            	userId=auth.getUserId();
            }
            logger.debug("userId "+userId);
            Azure azure = azureImpl.authorize(authObject);
            jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
            response.setStatus(200);
            AzureSimpleSolution myRunnable = new AzureSimpleSolution(azure,authObject,env.getProperty(AzureClientConstants.CONTAINERNAMEPREFIX_PROP),
            		env.getProperty(AzureClientConstants.REGISTRY_USERNAME_PROP),env.getProperty(AzureClientConstants.REGISTRY_PASSWORD_PROP),
            		dockerHosttoUrl(env.getProperty(AzureClientConstants.HOST_PROP), env.getProperty(AzureClientConstants.PORT_PROP),false),
            		null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryname,uidNumStr,dataSource,dataUserName,
            		dataPassword,dockerVMUserName,dockerVMPassword,solutionPort,subnet,vnet,sleepTimeFirst,
            		sleepTimeSecond,nexusRegistyUserName,nexusRegistyPwd,nexusRegistyName,otherRegistyName);
            
            Thread t = new Thread(myRunnable);
            t.start();
            
		}catch(Exception e){
			logger.error("singleImageAzureDeployment failed", e);
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			azureUtil.generateNotification("Error in vm creation", userId, dataSource, dataUserName, dataPassword);
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("jsonOutput "+jsonOutput.toString()); // jsonoutput does not have  any credentials
		logger.debug("singleImageAzureDeployment End ");
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_COMPOSITE_IMAGE}, method = RequestMethod.POST, produces = AzureClientConstants.APPLICATION_JSON)
	@ResponseBody
	public String compositeSolutionAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		logger.debug("compositeSolutionAzureDeployment start");
		JSONObject  jsonOutput = new JSONObject();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPassword="";
		String dataSource="";
		String userName="";
		String password="";
		String userId="";
		String subnet="";
		String vnet="";
		String replaceChar="";
		String ignorDoller="";
		String sleepTimeFirst="";
		String sleepTimeSecond="";
		String nexusRegistyUserName="";
		String nexusRegistyPwd="";
		String nexusRegistyName="";
		String otherRegistyName="";
		String exposeDataBrokerPort="";
		String internalDataBrokerPort="";
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty(AzureClientConstants.BLUEPRINT_IMAGENAME_PROP);
			String bluePrintName=env.getProperty(AzureClientConstants.BLUEPRINT_NAME_PROP);
			String bluePrintUser=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_USERNAME_PROP);
			String bluePrintPass=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_PASSWORD_PROP);
			String probePrintImage=env.getProperty(AzureClientConstants.PROBE_IMAGENAME_PROP);
			String probePrintName=env.getProperty(AzureClientConstants.PROBE_NAME_PROP);
			String probeInternalPort=env.getProperty(AzureClientConstants.PROBE_INTERNALPORT_PROP);
			String probeNexusEndPoint=env.getProperty(AzureClientConstants.PROBE_PROBENEXUSENDPOINT_PROP);
			String probUser=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_USERNAME_PROP);
			String probePass=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_PASSWORD_PROP);
			logger.debug("probePrintImage "+probePrintImage);
			logger.debug("probePrintName "+probePrintName);
			logger.debug("probeInternalPort "+probeInternalPort);
			String networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			dataSource=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			userName=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			password=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPWD_PROP);
			String nexusUrl=env.getProperty(AzureClientConstants.NEXUS_URL_PROP);
			String nexusUserName=env.getProperty(AzureClientConstants.NEXUS_USERNAME_PROP);
			String nexusPassword=env.getProperty(AzureClientConstants.NEXUS_PASSWORD_PROP);
			String dockerRegistryname=env.getProperty(AzureClientConstants.REGISTRY_NAME_PROP);
			dockerVMUserName=env.getProperty(AzureClientConstants.DOCKERVMUSERNAME_PROP);
			dockerVMPassword=env.getProperty(AzureClientConstants.DOCKERVMPASSWORD_PROP);
			replaceChar=env.getProperty(AzureClientConstants.REPLACECHAR_PROP);
			ignorDoller=env.getProperty(AzureClientConstants.IGNORE_DOLLER_PROP);
			dockerVMPassword=azureUtil.replaceCharStr(dockerVMPassword,replaceChar,ignorDoller);
			String solutionPort=env.getProperty(AzureClientConstants.SOLUTIONPORT_PROP);
			logger.debug("solutionPort "+solutionPort);
			subnet=env.getProperty(AzureClientConstants.SUBNET_PROP);
			vnet=env.getProperty(AzureClientConstants.VNET_PROP);
			sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			sleepTimeSecond=env.getProperty(AzureClientConstants.SLEEPTIME_SECOND);
			nexusRegistyUserName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_USERNAME);
			nexusRegistyPwd=env.getProperty(AzureClientConstants.NEXUS_REGISTY_PWD);
			nexusRegistyName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_NAME);
			otherRegistyName=env.getProperty(AzureClientConstants.OTHER_REGISTY_NAME);
			exposeDataBrokerPort=env.getProperty(AzureClientConstants.EXPOSE_DATABROKER_PORT);
			internalDataBrokerPort=env.getProperty(AzureClientConstants.INTERNAL_DATABROKER_PORT);
			logger.debug("exposeDataBrokerPort "+exposeDataBrokerPort);
			logger.debug("internalDataBrokerPort "+internalDataBrokerPort);
			logger.debug("nexusRegistyName "+nexusRegistyName);
			logger.debug("otherRegistyName "+otherRegistyName);
			logger.debug("subnet "+subnet);
			logger.debug("vnet "+vnet);
			logger.debug("sleepTimeFirst "+sleepTimeFirst);
			logger.debug("sleepTimeSecond "+sleepTimeSecond);
			if (authObject == null) {
				logger.debug("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
			userId=authObject.getUserId();
			logger.debug("userId "+userId);
            logger.debug("authObject.UrlAttribute "+authObject.getUrlAttribute());
            logger.debug("authObject.JsonMapping "+authObject.getJsonMapping());
            logger.debug("authObject.JsonPosition "+authObject.getJsonPosition());
            logger.debug("SolutionId "+authObject.getSolutionId());
			logger.debug("authObject.SolutionRevisionId "+authObject.getSolutionRevisionId());
			
			Azure azure = azureImpl.authorize(authObject);
			logger.debug("Azure Authentication Complete");
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionRevisionId(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
			ParseJSON parseJson=new ParseJSON();
			
			boolean probeIndicator=parseJson.checkProbeIndicator(AzureClientConstants.JSON_FILE_NAME);
			Blueprint bluePrintProbe=null;
			HashMap<String,String> imageMap=null;
			HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
			ArrayList<String> list=null;
			LinkedList<String> sequenceList=null;
			DataBrokerBean dataBrokerBean=null;
			logger.debug("probeIndicator "+probeIndicator);
			if(probeIndicator){
				
				imageMap=parseJson.parseJsonFileImageMap(AzureClientConstants.JSON_FILE_NAME);
				//Node Type and container Name in nodes
				nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(AzureClientConstants.JSON_FILE_NAME);
				// images list
				list=azureImpl.iterateImageMap(imageMap);
				dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
				if(dataBrokerBean!=null){
					if(dataBrokerBean!=null){
						ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(nexusUrl, nexusUserName, nexusPassword, dataBrokerBean.getProtobufFile());
						logger.debug("byteArrayOutputStream "+byteArrayOutputStream);
						if(byteArrayOutputStream!=null){
							dataBrokerBean.setProtobufFile(byteArrayOutputStream.toString());
						}else{
							dataBrokerBean.setProtobufFile("");
							
						}
						
					 }
				}
				//For new blueprint.json
				 bluePrintProbe =parseJson.jsonFileToObjectProbe(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
				//sequence
				sequenceList=parseJson.getSequenceListFromJSON(AzureClientConstants.JSON_FILE_NAME);
			}else{
				
				//old code 
				imageMap=parseJson.parseJsonFileImageMap(AzureClientConstants.JSON_FILE_NAME);
				//Node Type and container Name in nodes
				nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(AzureClientConstants.JSON_FILE_NAME);
				list=azureImpl.iterateImageMap(imageMap);
				sequenceList=parseJson.getSequenceListFromJSON(AzureClientConstants.JSON_FILE_NAME);
				dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
				if(dataBrokerBean!=null){
					if(dataBrokerBean!=null){
						ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(nexusUrl, nexusUserName, nexusPassword, dataBrokerBean.getProtobufFile());
						logger.debug("byteArrayOutputStream "+byteArrayOutputStream);
						if(byteArrayOutputStream!=null){
							dataBrokerBean.setProtobufFile(byteArrayOutputStream.toString());
						}else{
							dataBrokerBean.setProtobufFile("");
							
						}
						
					 }
				}
				bluePrintProbe=parseJson.jsonFileToObject(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
			}
			
			
			//-------------- New Probe Start ------------------- ***

			logger.debug("bluePrintProbe.ProbeIndocator"+bluePrintProbe.getProbeIndicator());
			
			ArrayList<ProbeIndicator> probeIndicatorList = bluePrintProbe.getProbeIndicator();
			ProbeIndicator prbIndicator = null;
			if(probeIndicatorList != null && probeIndicatorList.size() >0) {
				prbIndicator = probeIndicatorList.get(0);
			}			
		    if (bluePrintProbe.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True") ) {

				if (probePrintImage != null && !"".equals(probePrintImage)) {
					list.add(probePrintImage);
					imageMap.put(probePrintImage, AzureClientConstants.PROBE_CONTAINER_NAME);
					sequenceList=azureImpl.addProbeSequence(sequenceList,AzureClientConstants.PROBE_CONTAINER_NAME);
				}
			}	

			if (bluePrintImage != null && !"".equals(bluePrintImage)) {
				list.add(bluePrintImage);
				imageMap.put(bluePrintImage, AzureClientConstants.BLUEPRINT_CONTAINER);
			}
			
						
			//put condition to get probe
			
			logger.debug("list "+list);
			logger.debug("imageMap "+imageMap);
			logger.debug("sequenceList "+sequenceList);
			if(azure!=null) {
				logger.debug("Calling New thread for composite solution");
				AzureCompositeSolution compositeRunner =new AzureCompositeSolution(azure,authObject,env.getProperty(AzureClientConstants.CONTAINERNAMEPREFIX_PROP),env.getProperty(AzureClientConstants.REGISTRY_USERNAME_PROP),
                        env.getProperty(AzureClientConstants.REGISTRY_PASSWORD_PROP),dockerHosttoUrl(env.getProperty(AzureClientConstants.HOST_PROP),env.getProperty(AzureClientConstants.PORT_PROP), false),
                        null,list,bluePrintName,bluePrintUser,bluePrintPass,probeInternalPort,probePrintName,probUser,probePass,networkSecurityGroup,
                        imageMap,sequenceList,dockerRegistryname,bluePrintProbe,uidNumStr,dataSource,userName,password,dockerVMUserName,dockerVMPassword,
                        solutionPort,nodeTypeContainerMap,bluePrintJsonStr,probeNexusEndPoint,subnet,vnet,dataBrokerBean,
                        sleepTimeFirst,sleepTimeSecond,nexusRegistyUserName,nexusRegistyPwd,nexusRegistyName,otherRegistyName,exposeDataBrokerPort,internalDataBrokerPort);

	              Thread t = new Thread(compositeRunner);
                   t.start();
				
				  
				  
			}
			jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
			response.setStatus(200);	
		}catch(Exception e){
			logger.error("compositeSolutionAzureDeployment failed", e);
			response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			azureUtil.generateNotification("Error in vm creation", userId, dataSource, userName, password);
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("jsonOutput "+jsonOutput.toString());
		logger.debug("compositeSolutionAzureDeployment End");
		return jsonOutput.toString();
	}
    
	
	private String dockerHosttoUrl(String host, String port, boolean socket) {
		return ((socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

}
