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
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import org.slf4j.MDC;

import org.acumos.azure.client.api.APINames;
import org.acumos.azure.client.logging.ACUMOSLogConstants.MDCs;
import org.acumos.azure.client.logging.LogConfig;
import org.acumos.azure.client.service.impl.AzureCompositeSolution;
import org.acumos.azure.client.service.impl.AzureKubeSolution;
import org.acumos.azure.client.service.impl.AzureServiceImpl;
import org.acumos.azure.client.service.impl.AzureSimpleSolution;
import org.acumos.azure.client.service.impl.AzureSolutionDeployment;
import org.acumos.azure.client.transport.AzureDeployBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.transport.AzureKubeTransportBean;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AppProperties;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.LoggerUtil;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
		LogConfig.setEnteringMDCs("acumos-azure-client","singleImageAzureDeployment","");
		logger.debug("singleImageAzureDeployment start");
		JSONObject  jsonOutput = new JSONObject();
		
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPd="";
		String dataSource="";
		String dataUserName="";
		String dataPd="";
		String userId="";
		String subnet="";
		String vnet="";
		String replaceChar="";
		String ignorDoller="";
		String sleepTimeFirst="";
		String sleepTimeSecond="";
		String nexusRegistyUserName="";
		String nexusRegistyPd="";
		String nexusRegistyName="";
		String otherRegistyName="";
		String requestId="";
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			azureImpl.setEnvironment(env);
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			String bluePrintName=env.getProperty(AzureClientConstants.BLUEPRINT_NAME_PROP);
			String bluePrintUser=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_USERNAME_PROP);
			String bluePrintPass=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_PD_PROP);
			String networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			dataSource=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			dataUserName=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			dataPd=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPD_PROP);
			dockerVMUserName=env.getProperty(AzureClientConstants.DOCKERVMUSERNAME_PROP);
			dockerVMPd=env.getProperty(AzureClientConstants.DOCKERVMPD_PROP);
			replaceChar=env.getProperty(AzureClientConstants.REPLACECHAR_PROP);
			ignorDoller=env.getProperty(AzureClientConstants.IGNORE_DOLLER_PROP);
			dockerVMPd=azureUtil.replaceCharStr(dockerVMPd,replaceChar,ignorDoller);
			String solutionPort=env.getProperty(AzureClientConstants.SOLUTIONPORT_PROP);
			subnet=env.getProperty(AzureClientConstants.SUBNET_PROP);
			vnet=env.getProperty(AzureClientConstants.VNET_PROP);
			sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			sleepTimeSecond=env.getProperty(AzureClientConstants.SLEEPTIME_SECOND);
			nexusRegistyUserName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_USERNAME);
			nexusRegistyPd=env.getProperty(AzureClientConstants.NEXUS_REGISTY_PD);
			nexusRegistyName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_NAME);
			otherRegistyName=env.getProperty(AzureClientConstants.OTHER_REGISTY_NAME);
			requestId=MDC.get(MDCs.REQUEST_ID); 

			logger.debug("requestId "+requestId);
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
            logger.debug("azure object detail "+azure);
            jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
            response.setStatus(200);
            AzureSimpleSolution myRunnable = new AzureSimpleSolution(azure,authObject,env.getProperty(AzureClientConstants.CONTAINERNAMEPREFIX_PROP),
            		env.getProperty(AzureClientConstants.REGISTRY_USERNAME_PROP),env.getProperty(AzureClientConstants.REGISTRY_PD_PROP),
            		dockerHosttoUrl(env.getProperty(AzureClientConstants.HOST_PROP), env.getProperty(AzureClientConstants.PORT_PROP),false),
            		null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryname,uidNumStr,dataSource,dataUserName,
            		dataPd,dockerVMUserName,dockerVMPd,solutionPort,subnet,vnet,sleepTimeFirst,
            		sleepTimeSecond,nexusRegistyUserName,nexusRegistyPd,nexusRegistyName,otherRegistyName,requestId);
            
            Thread t = new Thread(myRunnable);
            t.start();
            
		}catch(Exception e){
			logger.error("singleImageAzureDeployment failed", e);
			LogConfig.clearMDCDetails();
			try {
			 azureUtil.generateNotification("Error in vm creation", userId, dataSource, dataUserName, dataPd);
			}catch(Exception ex) {
			 logger.error("generateNotification Error",e);	
			}
			response.setStatus(400);
			jsonOutput.put("status", APINames.FAILED);
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("jsonOutput "+jsonOutput.toString()); // jsonoutput does not have  any credentials
		logger.debug("singleImageAzureDeployment End ");
		LogConfig.clearMDCDetails();
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_COMPOSITE_IMAGE}, method = RequestMethod.POST, produces = AzureClientConstants.APPLICATION_JSON)
	@ResponseBody
	public String compositeSolutionAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		LogConfig.setEnteringMDCs("acumos-azure-client","compositeSolutionAzureDeployment","");
		logger.debug("compositeSolutionAzureDeployment start");
		JSONObject  jsonOutput = new JSONObject();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPd="";
		String dataSource="";
		String userName="";
		String dataPd="";
		String userId="";
		String subnet="";
		String vnet="";
		String replaceChar="";
		String ignorDoller="";
		String sleepTimeFirst="";
		String sleepTimeSecond="";
		String nexusRegistyUserName="";
		String nexusRegistyPd="";
		String nexusRegistyName="";
		String otherRegistyName="";
		String exposeDataBrokerPort="";
		String internalDataBrokerPort="";
		String nginxMapFolder="";
		String nginxWebFolder="";
		String nginxImageName="";
		String nginxInternalPort="";
		String azureDataFiles="";
		String requestId="";
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		TransportBean tbean=new TransportBean();
		LoggerUtil loggerUtil=new LoggerUtil();
		try {
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty(AzureClientConstants.BLUEPRINT_IMAGENAME_PROP);
			String bluePrintName=env.getProperty(AzureClientConstants.BLUEPRINT_NAME_PROP);
			String bluePrintUser=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_USERNAME_PROP);
			String bluePrintPass=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_PD_PROP);
			String probePrintImage=env.getProperty(AzureClientConstants.PROBE_IMAGENAME_PROP);
			String probePrintName=env.getProperty(AzureClientConstants.PROBE_NAME_PROP);
			String probeInternalPort=env.getProperty(AzureClientConstants.PROBE_INTERNALPORT_PROP);
			String probeNexusEndPoint=env.getProperty(AzureClientConstants.PROBE_PROBENEXUSENDPOINT_PROP);
			String probUser=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_USERNAME_PROP);
			String probePass=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_PD_PROP);
			logger.debug("probePrintImage "+probePrintImage);
			logger.debug("probePrintName "+probePrintName);
			logger.debug("probeInternalPort "+probeInternalPort);
			String networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			dataSource=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			userName=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			dataPd=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPD_PROP);
			String nexusUrl=env.getProperty(AzureClientConstants.NEXUS_URL_PROP);
			String nexusUserName=env.getProperty(AzureClientConstants.NEXUS_USERNAME_PROP);
			String nexusPd=env.getProperty(AzureClientConstants.NEXUS_PD_PROP);
			String dockerRegistryname=env.getProperty(AzureClientConstants.REGISTRY_NAME_PROP);
			dockerVMUserName=env.getProperty(AzureClientConstants.DOCKERVMUSERNAME_PROP);
			dockerVMPd=env.getProperty(AzureClientConstants.DOCKERVMPD_PROP);
			replaceChar=env.getProperty(AzureClientConstants.REPLACECHAR_PROP);
			ignorDoller=env.getProperty(AzureClientConstants.IGNORE_DOLLER_PROP);
			dockerVMPd=azureUtil.replaceCharStr(dockerVMPd,replaceChar,ignorDoller);
			String solutionPort=env.getProperty(AzureClientConstants.SOLUTIONPORT_PROP);
			logger.debug("solutionPort "+solutionPort);
			subnet=env.getProperty(AzureClientConstants.SUBNET_PROP);
			vnet=env.getProperty(AzureClientConstants.VNET_PROP);
			sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			sleepTimeSecond=env.getProperty(AzureClientConstants.SLEEPTIME_SECOND);
			nexusRegistyUserName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_USERNAME);
			nexusRegistyPd=env.getProperty(AzureClientConstants.NEXUS_REGISTY_PD);
			nexusRegistyName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_NAME);
			otherRegistyName=env.getProperty(AzureClientConstants.OTHER_REGISTY_NAME);
			exposeDataBrokerPort=env.getProperty(AzureClientConstants.EXPOSE_DATABROKER_PORT);
			internalDataBrokerPort=env.getProperty(AzureClientConstants.INTERNAL_DATABROKER_PORT);
			nginxMapFolder=env.getProperty(AzureClientConstants.NGINX_MAPFOLDER);
			nginxWebFolder=env.getProperty(AzureClientConstants.NGINX_WEBFOLDER);
			nginxImageName=env.getProperty(AzureClientConstants.NGINX_IMAGENAME);
			nginxInternalPort=env.getProperty(AzureClientConstants.NGINX_INTERNALPORT);
			azureDataFiles=env.getProperty(AzureClientConstants.DATAFILE_FOLDER);
			requestId=MDC.get(MDCs.REQUEST_ID); 

			logger.debug("requestId "+requestId);
			
			userId=authObject.getUserId();
			loggerUtil.printCompositeSolutionDetails(userId,azureDataFiles,nginxInternalPort,nginxImageName,exposeDataBrokerPort,
					internalDataBrokerPort,nexusRegistyName,otherRegistyName,subnet,vnet,sleepTimeFirst,sleepTimeSecond,
					nginxMapFolder,nginxWebFolder,authObject);
			Azure azure = azureImpl.authorize(authObject);
			logger.debug("Azure Authentication Complete");
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionRevisionId(),dataSource,userName,dataPd,nexusUrl,nexusUserName,nexusPd);
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
			ParseJSON parseJson=new ParseJSON();
			
			boolean probeIndicator=parseJson.checkProbeIndicator(AzureClientConstants.JSON_FILE_NAME);
			Blueprint bluePrintProbe=null;
			HashMap<String,String> imageMap=null;
			HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
			ArrayList<String> list=null;
			LinkedList<String> sequenceList=null;
			DataBrokerBean dataBrokerBean=null;
			Map<String,String> protoContainerMap=null;
			logger.debug("probeIndicator "+probeIndicator);
			if(probeIndicator){
				tbean.setProtoContainerMap(parseJson.getProtoDetails(AzureClientConstants.JSON_FILE_NAME));
				bluePrintProbe =parseJson.jsonFileToObjectProbe(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
			}else{
				bluePrintProbe=parseJson.jsonFileToObject(AzureClientConstants.JSON_FILE_NAME,dataBrokerBean);
			}
			
			imageMap=parseJson.parseJsonFileImageMap(AzureClientConstants.JSON_FILE_NAME);
			nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(AzureClientConstants.JSON_FILE_NAME);
			list=azureImpl.iterateImageMap(imageMap);
			dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
			sequenceList=parseJson.getSequenceListFromJSON(AzureClientConstants.JSON_FILE_NAME);
			if(dataBrokerBean!=null){
				if(dataBrokerBean!=null){
					ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(nexusUrl, nexusUserName, nexusPd, dataBrokerBean.getProtobufFile());
					logger.debug("byteArrayOutputStream "+byteArrayOutputStream);
					if(byteArrayOutputStream!=null){
						dataBrokerBean.setProtobufFile(byteArrayOutputStream.toString());
					}else{
						dataBrokerBean.setProtobufFile("");
						
					}
					
				 }
			}
			//-------------- New Probe Start ------------------- ***

			logger.debug("bluePrintProbe.ProbeIndocator"+bluePrintProbe.getProbeIndicator());
			
			ArrayList<ProbeIndicator> probeIndicatorList = bluePrintProbe.getProbeIndicator();
			ProbeIndicator prbIndicator = null;
			if(probeIndicatorList != null && probeIndicatorList.size() >0) {
				prbIndicator = probeIndicatorList.get(0);
			}			
		    if (bluePrintProbe.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True") ) {
		    	list.add(nginxImageName);
				imageMap.put(nginxImageName, AzureClientConstants.NGINX_CONTAINER);
				sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.NGINX_CONTAINER);
				if (probePrintImage != null && !"".equals(probePrintImage)) {
					list.add(probePrintImage);
					imageMap.put(probePrintImage, AzureClientConstants.PROBE_CONTAINER_NAME);
					sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.PROBE_CONTAINER_NAME);
				}
			}	

			if (bluePrintImage != null && !"".equals(bluePrintImage)) {
				list.add(bluePrintImage);
				imageMap.put(bluePrintImage, AzureClientConstants.BLUEPRINT_CONTAINER);
			}
			//set velues in bean
			tbean.setNexusUrl(nexusUrl);
			tbean.setNexusUserName(nexusUserName);	
			tbean.setNexusPd(nexusPd);
			tbean.setNginxMapFolder(nginxMapFolder);
			tbean.setNginxWebFolder(nginxWebFolder);
			tbean.setNginxImageName(nginxImageName);
			tbean.setNginxInternalPort(nginxInternalPort);
			tbean.setAzureDataFiles(azureDataFiles);
			tbean.setRequestId(requestId);
			//put condition to get probe
			
			logger.debug("list "+list);
			logger.debug("imageMap "+imageMap);
			logger.debug("sequenceList "+sequenceList);
			if(azure!=null) {
				logger.debug("Calling New thread for composite solution");
				AzureCompositeSolution compositeRunner =new AzureCompositeSolution(azure,authObject,env.getProperty(AzureClientConstants.CONTAINERNAMEPREFIX_PROP),env.getProperty(AzureClientConstants.REGISTRY_USERNAME_PROP),
                        env.getProperty(AzureClientConstants.REGISTRY_PD_PROP),dockerHosttoUrl(env.getProperty(AzureClientConstants.HOST_PROP),env.getProperty(AzureClientConstants.PORT_PROP), false),
                        null,list,bluePrintName,bluePrintUser,bluePrintPass,probeInternalPort,probePrintName,probUser,probePass,networkSecurityGroup,
                        imageMap,sequenceList,dockerRegistryname,bluePrintProbe,uidNumStr,dataSource,userName,dataPd,dockerVMUserName,dockerVMPd,
                        solutionPort,nodeTypeContainerMap,bluePrintJsonStr,probeNexusEndPoint,subnet,vnet,dataBrokerBean,
                        sleepTimeFirst,sleepTimeSecond,nexusRegistyUserName,nexusRegistyPd,nexusRegistyName,otherRegistyName,exposeDataBrokerPort,internalDataBrokerPort,tbean);

	              Thread t = new Thread(compositeRunner);
                   t.start();
				
				  
				  
			}
			jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
			response.setStatus(200);	
		}catch(Exception e){
			logger.error("compositeSolutionAzureDeployment failed", e);
			LogConfig.clearMDCDetails();
			try {
			  azureUtil.generateNotification("Error in vm creation", userId, dataSource, userName, dataPd);
			}catch(Exception ex) {
			  logger.error("generateNotification Error",e);	
			}
			response.setStatus(400);
			jsonOutput.put("status", APINames.FAILED);
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		logger.debug("jsonOutput "+jsonOutput.toString());
		logger.debug("compositeSolutionAzureDeployment End");
		LogConfig.clearMDCDetails();
		return jsonOutput.toString();
	}
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_KUBERNETES}, method = RequestMethod.POST, produces = AzureClientConstants.APPLICATION_JSON)
	@ResponseBody
	public String kubernetesDeployment(HttpServletRequest request,@RequestBody AzureKubeBean auth,HttpServletResponse response) throws Exception {
		LogConfig.setEnteringMDCs("acumos-azure-client","azureKubernetes","");
		logger.debug("kubernetesDeployment start");
		JSONObject  jsonOutput = new JSONObject();
		String cmnDataUrl="";
		String cmnDataUser="";
		String cmnDataPd="";
		String kubernetesClientUrl="";
		String subnet="";
		String vnet="";
		String networkSecurityGroup="";
		String dockerVMUserName="";
		String dockerVMPd="";
		String sleepTimeFirst="";
		String uidNumStr="";
		String replaceChar="";
		String ignorDoller="";
		String requestId="";
		
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try{
			  UUID uidNumber = UUID.randomUUID();
			  uidNumStr=uidNumber.toString();
			  AzureCommonUtil cutil=new AzureCommonUtil();
			  AzureKubeTransportBean kubeTransportBean=new AzureKubeTransportBean();
			  cmnDataUrl=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			  cmnDataUser=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			  cmnDataPd=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPD_PROP);
			  subnet=env.getProperty(AzureClientConstants.SUBNET_PROP);
			  vnet=env.getProperty(AzureClientConstants.VNET_PROP);
		      networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			  dockerVMUserName=env.getProperty(AzureClientConstants.DOCKERVMUSERNAME_PROP);
			  dockerVMPd=env.getProperty(AzureClientConstants.DOCKERVMPD_PROP);
			  sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			  kubernetesClientUrl=env.getProperty(AzureClientConstants.KUBERNETESCLIENT_URL);
			  replaceChar=env.getProperty(AzureClientConstants.REPLACECHAR_PROP);
			  ignorDoller=env.getProperty(AzureClientConstants.IGNORE_DOLLER_PROP);
			  requestId=MDC.get(MDCs.REQUEST_ID); 
			  
			  logger.debug("requestId "+requestId);
			  logger.debug("cmnDataUrl "+cmnDataUrl);
			  logger.debug("cmnDataUser "+cmnDataUser);
			  logger.debug("cmnDataPd "+cmnDataPd);
			  logger.debug("subnet "+subnet);
			  logger.debug("vnet "+vnet);
			  logger.debug("networkSecurityGroup "+networkSecurityGroup);
			  logger.debug("dockerVMUserName "+dockerVMUserName);
			  logger.debug("sleepTimeFirst "+sleepTimeFirst);
			  logger.debug("kubernetesClientUrl "+kubernetesClientUrl);
			  logger.debug("replaceChar "+replaceChar);
			  logger.debug("ignorDoller "+ignorDoller);
			  AzureDeployDataObject authObject=cutil.convertToAzureDeployDataObject(auth);
			  AzureServiceImpl azureImpl=new AzureServiceImpl();
			  Azure azure = azureImpl.authorize(authObject);
			  logger.debug("Azure Authorization Done");
			  kubeTransportBean.setDockerVMPd(azureUtil.replaceCharStr(dockerVMPd,replaceChar,ignorDoller));
			  kubeTransportBean.setDockerVMUserName(dockerVMUserName);
			  kubeTransportBean.setKubernetesClientUrl(kubernetesClientUrl);
			  kubeTransportBean.setNetworkSecurityGroup(networkSecurityGroup);
			  kubeTransportBean.setSleepTimeFirst(sleepTimeFirst);
			  kubeTransportBean.setSubnet(subnet);
			  kubeTransportBean.setVnet(vnet);
			  kubeTransportBean.setCmnDataPd(cmnDataPd);
			  kubeTransportBean.setCmnDataUrl(cmnDataUrl);
			  kubeTransportBean.setCmnDataUser(cmnDataUser);
			  kubeTransportBean.setRequestId(requestId);
			  if(azure!=null) {
				  AzureKubeSolution kubeSolution=new AzureKubeSolution(auth,kubeTransportBean,azure);
				  Thread t = new Thread(kubeSolution);
                  t.start();
			  }
			 
			jsonOutput.put("status", APINames.SUCCESS_RESPONSE);  
		   	response.setStatus(200);
		   	}catch(Exception e){
		   		logger.error("kubernetesDeployment failed", e);
		   		LogConfig.clearMDCDetails();
		   		try {
					 azureUtil.generateNotification("Error in vm creation", auth.getUserId(), cmnDataUrl, cmnDataUser, cmnDataPd);
				  }catch(Exception ex) {
					 logger.error("generateNotification Error",e);	
				  }
				  response.setStatus(400);
				  jsonOutput.put("status", APINames.FAILED);
			}
		logger.debug("kubernetesDeployment end");
		jsonOutput.put("UIDNumber", uidNumStr);
		LogConfig.clearMDCDetails();
		return jsonOutput.toString();
	}
    
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_EXISTINGVM}, method = RequestMethod.POST, produces = AzureClientConstants.APPLICATION_JSON)
	@ResponseBody
	public String existingAzureVM(HttpServletRequest request,@RequestBody SolutionDeployment bean,HttpServletResponse response) throws Exception {
		LogConfig.setEnteringMDCs("acumos-azure-client","existingAzureVM","");
		logger.debug("existingAzureVM start");
		
		String bluePrintImage="";
		String bluePrintName="";
		String bluePrintUser="";
		String bluePrintPass="";
		String probePrintImage="";
		String probePrintName="";
		String probeInternalPort="";
		String probeNexusEndPoint="";
		String probUser="";
		String probePass="";
		String dataSource="";
		String dataUserName="";
		String dataPd="";
		String uidNumStr="";
		String nexusUrl="";
		String nexusUserName="";
		String nexusPd="";
		String nginxMapFolder="";
		String nginxWebFolder="";
		String nginxImageName="";
		String nginxInternalPort="";
		String azureDataFiles="";
		String nexusRegistyName="";
		String networkSecurityGroup="";
		String exposeDataBrokerPort="";
		String internalDataBrokerPort="";
		String nexusRegistyUserName="";
		String nexusRegistyPd="";
		String registryUserName="";
		String registryPd="";
		String sleepTimeFirst="";
		String solutionPort="";
		String localHostEnv="";
		String requestId="";
		AzureSolutionDeployment azureDeployment=new AzureSolutionDeployment();
		JSONObject  jsonOutput = new JSONObject();
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		TransportBean tbean=new TransportBean();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		boolean singleSolution=false;
		LoggerUtil loggerUtil=new LoggerUtil();
		try {
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			bluePrintImage=env.getProperty(AzureClientConstants.BLUEPRINT_IMAGENAME_PROP);
			bluePrintName=env.getProperty(AzureClientConstants.BLUEPRINT_NAME_PROP);
			bluePrintUser=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_USERNAME_PROP);
			bluePrintPass=env.getProperty(AzureClientConstants.REGISTRY_BLUEPRINT_PD_PROP);
			probePrintImage=env.getProperty(AzureClientConstants.PROBE_IMAGENAME_PROP);
			probePrintName=env.getProperty(AzureClientConstants.PROBE_NAME_PROP);
			probeInternalPort=env.getProperty(AzureClientConstants.PROBE_INTERNALPORT_PROP);
			probeNexusEndPoint=env.getProperty(AzureClientConstants.PROBE_PROBENEXUSENDPOINT_PROP);
			probUser=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_USERNAME_PROP);
			probePass=env.getProperty(AzureClientConstants.DOCKER_REGISTRY_PROBE_PD_PROP);
			networkSecurityGroup=env.getProperty(AzureClientConstants.REGISTRY_NETWORKGROUPNAME_PROP);
			dataSource=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCENDPOINURL_PROP);
			dataUserName=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCUSER_PROP);
			dataPd=env.getProperty(AzureClientConstants.CMNDATASVC_CMNDATASVCPD_PROP);
			nexusUrl=env.getProperty(AzureClientConstants.NEXUS_URL_PROP);
			nexusUserName=env.getProperty(AzureClientConstants.NEXUS_USERNAME_PROP);
			nexusPd=env.getProperty(AzureClientConstants.NEXUS_PD_PROP);
			nginxMapFolder=env.getProperty(AzureClientConstants.NGINX_MAPFOLDER);
			nginxWebFolder=env.getProperty(AzureClientConstants.NGINX_WEBFOLDER);
			nginxImageName=env.getProperty(AzureClientConstants.NGINX_IMAGENAME);
			nginxInternalPort=env.getProperty(AzureClientConstants.NGINX_INTERNALPORT);
			azureDataFiles=env.getProperty(AzureClientConstants.DATAFILE_FOLDER);
			nexusRegistyName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_NAME);
			exposeDataBrokerPort=env.getProperty(AzureClientConstants.EXPOSE_DATABROKER_PORT);
			internalDataBrokerPort=env.getProperty(AzureClientConstants.INTERNAL_DATABROKER_PORT);
			nexusRegistyUserName=env.getProperty(AzureClientConstants.NEXUS_REGISTY_USERNAME);
			nexusRegistyPd=env.getProperty(AzureClientConstants.NEXUS_REGISTY_PD);
			registryUserName=env.getProperty(AzureClientConstants.REGISTRY_USERNAME_PROP);
			registryPd=env.getProperty(AzureClientConstants.REGISTRY_PD_PROP);
			sleepTimeFirst=env.getProperty(AzureClientConstants.SLEEPTIME_FIRST);
			solutionPort=env.getProperty(AzureClientConstants.SOLUTIONPORT_PROP);
			localHostEnv=dockerHosttoUrl(env.getProperty(AzureClientConstants.HOST_PROP),env.getProperty(AzureClientConstants.PORT_PROP), false);
			requestId=MDC.get(MDCs.REQUEST_ID); 
			logger.debug("requestId "+requestId);
			// Azure Authenticatiobn 
			Azure azure = azureImpl.authorizeAzure(bean.getClient(), bean.getTenant(), bean.getKey(), bean.getSubscriptionKey());
		    tbean=azureUtil.setTransportValues(tbean, nexusUrl, nexusUserName, nexusPd, nginxMapFolder, nginxWebFolder, 
					nginxImageName, nginxInternalPort, azureDataFiles, probePrintImage, bluePrintImage, 
					exposeDataBrokerPort, internalDataBrokerPort, probeInternalPort, nexusRegistyName, 
					nexusRegistyUserName, nexusRegistyPd, registryUserName, registryPd, bluePrintUser, 
					bluePrintPass, probUser, probePass, uidNumStr, solutionPort, dataSource, dataUserName, 
					dataPd, sleepTimeFirst, localHostEnv);
			tbean.setRequestId(requestId);
			loggerUtil.printExistingVMDetails(bean, tbean);
			if(azure!=null) {
				logger.debug("Calling New thread for solution");
				AzureSolutionDeployment deployment =new AzureSolutionDeployment(bean,tbean,azure);
		        Thread t = new Thread(deployment);
	            t.start();
				jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				response.setStatus(200);
			}
			logger.debug("existingAzureVM start");
		}catch(Exception e){
			logger.error("existingAzureVM failed", e);
			LogConfig.clearMDCDetails();
			try {
			  azureUtil.generateNotification("existingAzureVM Deployment fail", "", dataSource, dataUserName, dataPd);
			}catch(Exception ex) {
			  logger.error("generateNotification Error",e);	
			}
			response.setStatus(400);
			jsonOutput.put("status", APINames.FAILED);
			return jsonOutput.toString();
		}
		jsonOutput.put("UIDNumber", uidNumStr);
		LogConfig.clearMDCDetails();
		return jsonOutput.toString();
	}
	
	private String dockerHosttoUrl(String host, String port, boolean socket) {
		return ((socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

}
