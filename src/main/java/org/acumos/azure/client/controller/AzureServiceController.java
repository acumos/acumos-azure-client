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
import org.acumos.azure.client.transport.SingletonMapClass;
import org.acumos.azure.client.utils.AppProperties;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.ParseJSON;
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
	
	/*@RequestMapping(value ={org.acumos.azure.client.api.APINames.AZURE_AUTH_ASYNC_SINGLE_IMAGE},  method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public DeferredResult<String> singleImageAsyncDeployment(@RequestParam("acrName") String acrName,@RequestParam("client") String client,@RequestParam("imagetag") String imagetag,
			                                                @RequestParam("key") String key,@RequestParam("rgName") String rgName,@RequestParam("storageAccount") String storageAccount,
			                                                @RequestParam("subscriptionKey") String subscriptionKey,@RequestParam("tenant") String tenant) throws Exception {
	  final DeferredResult<String> deferredResult = new DeferredResult<>();
	  JSONObject  jsonOutput = new JSONObject();
	  AzureBean azBean=new AzureBean();
	  String responseString="";
	  logger.debug("<------Start----singleImageAsyncDeployment------------>");
	  AzureServiceImpl azureImpl=new AzureServiceImpl();
	  logger.debug("<-------acrName----->"+acrName);
	  logger.info("<-------client----->"+client);
	  logger.info("<-------imagetag----->"+imagetag);
	  logger.info("<-------key----->"+key);
	  logger.info("<-------rgName----->"+rgName);
	  logger.info("<-------storageAccount----->"+storageAccount);
	  logger.info("<-------subscriptionKey----->"+subscriptionKey);
	  logger.info("<-------tenant----->"+tenant);
	  String vmIp="";
	  String appPort="8557";
	  String appDetail="";
	  try {
			azureImpl.setEnvironment(env);
			
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
	          
	          AzureDeployDataObject authObject=new AzureDeployDataObject();
	          ArrayList<String> list=new ArrayList<String>();
	          list.add(imagetag);
	          if(acrName!=null){
	          	authObject.setAcrName(acrName);
	          }
	          if(key!=null){
	          	authObject.setKey(key);
	          }
	          if(auth.getImagetag()!=null){
	          	authObject.set(auth.getAcrName());
	          }
	          if(rgName!=null){
	          	authObject.setRgName(rgName);
	          }
	          if(client!=null){
	          	authObject.setClient(client);
	          }
	          if(subscriptionKey!=null){
	          	authObject.setSubscriptionKey(subscriptionKey);
	          }
	          if(storageAccount!=null){
	          	authObject.setStorageAccount(storageAccount);
	          }
	          if(tenant!=null){
	          	authObject.setTenant(tenant);
	          }
	          Azure azure = azureImpl.authorize(authObject);
	          if(azure!=null) {
	          	azBean=azureImpl.pushSingleImage(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
							  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
							  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort,dockerRegistryname);
	          }
	          vmIp=azBean.getAzureVMIP();
	          appDetail=vmIp+"#"+appPort;
	          logger.info("<------appDetail---------->"+appDetail);
	          jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
	          //response.setStatus(200);
			}catch(Exception e){
				logger.error("<-----Exception in singleImageAzureDeployment------------>"+e.getMessage());
				//response.setStatus(401);
				jsonOutput.put("status", APINames.FAILED);
				logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
				responseString=jsonOutput.toString();
			}
		logger.info("<------End----singleImageAsyncDeployment------------>");
	  	
	  jsonOutput.put("APPDetails",appDetail);
	  responseString=jsonOutput.toString();
	  deferredResult.setResult(responseString);
	  return deferredResult;
	}
	
	@RequestMapping(value ={org.acumos.azure.client.api.APINames.AZURE_AUTH_ASYNC_COMPOSITE_SOLUTION},  method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public DeferredResult<String> compositeSolutionAsyncAzure(@RequestParam("acrName") String acrName,@RequestParam("client") String client,@RequestParam("solutionId") String solutionId,
			                                          @RequestParam("key") String key,@RequestParam("rgName") String rgName,@RequestParam("storageAccount") String storageAccount,
			                                          @RequestParam("subscriptionKey") String subscriptionKey,@RequestParam("tenant") String tenant,@RequestParam("solutionVersion") String solutionVersion) throws Exception {
		logger.info("<------Start----compositeSolutionAsyncAzure------------>");
		logger.info("<-------acrName----->"+acrName);
	    logger.info("<-------client----->"+client);
	    logger.info("<-------solutionId----->"+solutionId);
	    logger.info("<-------key----->"+key);
	    logger.info("<-------rgName----->"+rgName);
	    logger.info("<-------storageAccount----->"+storageAccount);
	    logger.info("<-------subscriptionKey----->"+subscriptionKey);
	    logger.info("<-------tenant----->"+tenant);
	    logger.info("<-------solutionVersion----->"+solutionVersion);
		final DeferredResult<String> deferredResult = new DeferredResult<>();
		JSONObject  jsonOutput = new JSONObject();
		AzureBean azBean=new AzureBean();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String responseString="";
		
		try {
			  AzureDeployDataObject authObject=new AzureDeployDataObject();
	          
	          if(acrName!=null){
	          	authObject.setAcrName(acrName);
	          }
	          if(key!=null){
	          	authObject.setKey(key);
	          }
	          if(auth.getImagetag()!=null){
	          	authObject.set(auth.getAcrName());
	          }
	          if(rgName!=null){
	          	authObject.setRgName(rgName);
	          }
	          if(client!=null){
	          	authObject.setClient(client);
	          }
	          if(subscriptionKey!=null){
	          	authObject.setSubscriptionKey(subscriptionKey);
	          }
	          if(storageAccount!=null){
	          	authObject.setStorageAccount(storageAccount);
	          }
	          if(tenant!=null){
	          	authObject.setTenant(tenant);
	          }
	          if(solutionId!=null ){
	        	  authObject.setSolutionId(solutionId); 
	          }
	          if(solutionVersion!=null){
	        	  authObject.setSolutionVersion(solutionVersion); 
	          }
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
			logger.info("<------SolutionVersion---------->"+authObject.getSolutionRevisionId());
			String bluePrintStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionRevisionId(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
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
				  
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  dockerInfoList=azBean.getDockerinfolist();
				  vmIP=azBean.getAzureVMIP().trim();
				  bluePrintPort=azBean.getBluePrintPort().trim();
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				  
			}
			  
			  logger.info("Dockerinfolist=============="+mapper.writeValueAsString(azBean.getDockerinfolist()));
			  logger.info("bluePrint==================="+mapper.writeValueAsString(bluePrint));
			  
			  if(dockerInfoList!=null){
				  List<DockerInfo> dockerListVal =dockerInfoList.getDockerList();
				  if(dockerListVal!=null){
					  
					 for(DockerInfo dockerInfo: dockerListVal){
						if(dockerInfo!=null){
							String containerName=dockerInfo.getContainer();
							String containerIp=dockerInfo.getIpAddress();
							String containerPort=dockerInfo.getPort();
							jsonOutput.put(containerName, containerIp+"#"+containerPort);
						}
					 }
				  }
			  }
			  logger.info("<-----jsonOutput---------->"+jsonOutput);
			    String urlDockerInfo="http://"+vmIP+":"+bluePrintPort+"/putDockerInfo";  
				String urlBluePrint="http://"+vmIP+":"+bluePrintPort+"/putBlueprint";
				logger.info("<-----urlDockerInfo---------->"+urlDockerInfo+"<----urlBluePrint----->"+urlBluePrint);
			  if(azBean.getDockerinfolist()!=null){
					azureImpl.putContainerDetails(azBean.getDockerinfolist(),urlDockerInfo);
				}
				if(bluePrint!=null){
					azureImpl.putBluePrintDetails(bluePrint,urlBluePrint);
				}
				//response.setStatus(200);	
		}catch(Exception e){
			logger.error("<-----Exception in compositeSolutionAzureDeployment------------>"+e.getMessage());
			//response.setStatus(401);
			jsonOutput.put("status", APINames.FAILED);
			logger.error(e.getMessage() + " Returning... " + jsonOutput.toString());
			responseString= jsonOutput.toString();
		}
		
		//jsonOutput.put("APPDetails",appDetail);
		responseString=jsonOutput.toString();
		deferredResult.setResult(responseString);
		logger.info("<------End----compositeSolutionAsyncAzure------------>");
	    return deferredResult;
	}

	@CrossOrigin
	//@ApiOperation(value = "Gets a page of solutions matching the specified tag.", response = String.class, responseContainer = "Page")
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String authorizeAndPushImage(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		
		
		JSONObject  jsonOutput = new JSONObject();
		logger.info("<------start----authorizeAndPushImage in AzureServiceController------------>");
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
//			String dataSource=env.getProperty("cmndatasvc.cmndatasvcendpoinurl");
//			String userName=env.getProperty("cmndatasvc.cmndatasvcuser");
//			String password=env.getProperty("cmndatasvc.cmndatasvcpwd");
//			String nexusUrl=env.getProperty("nexus.url");
//			String nexusUserName=env.getProperty("nexus.username");
//			String nexusPassword=env.getProperty("nexus.password");
			String dockerRegistryname=env.getProperty("docker.registry.name");
			logger.info("<------dockerRegistryname---------->"+dockerRegistryname);
			if (authObject == null) {
				System.out.println("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
            
			Azure azure = azureImpl.authorize(authObject);	
			List<NetworkSecurityGroup> networkSecurityGroups = azure.networkSecurityGroups().list();
	        for (NetworkSecurityGroup networkSecurityGroup: networkSecurityGroups) {
	            Utils.print(networkSecurityGroup);
	            List<Subnet> subnetList=networkSecurityGroup.listAssociatedSubnets();
	            
	        }
			logger.info("<------SolutionId---------->"+authObject.getSolutionId());
			logger.info("<------revisionVersion---------->"+authObject.getSolutionRevisionId());
			//azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionVersion(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			
			
			ParseJSON parseJson=new ParseJSON();
			Blueprint bluePrint=parseJson.jsonFileToObject();
			
			HashMap<String,String> imageMap=new HashMap<String,String>();//parseJson.parseJsonFile();
			ArrayList<String> list=new ArrayList<String>();//azureImpl.iterateImageMap(imageMap);
//			Blueprint bluePrint=parseJson.jsonFileToObject();
			
			logger.info("<------bluePrintImage---------->"+bluePrintImage);
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
				  DockerInfoList dockerInfoList=azBean.getDockerinfolist();
				  String vmIP=azBean.getAzureVMIP().trim();
				  String bluePrintPort=azBean.getBluePrintPort().trim();
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
			}
			  Thread.sleep(30000);
			  //new call 
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
	}*/
	
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_SINGLE_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String singleImageAzureDeployment(HttpServletRequest request, @RequestBody AzureDeployBean auth, HttpServletResponse response) throws Exception {
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
			logger.debug("<------dockerVMUserName---------->"+dockerVMUserName);
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
		logger.debug("<------start----singleImageAzureDeployment------------>");
		JSONObject  jsonOutput = new JSONObject();
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		String uidNumStr="";
		String dockerVMUserName="";
		String dockerVMPassword="";
		try {
			UUID uidNumber = UUID.randomUUID();
			uidNumStr=uidNumber.toString();
			azureImpl.setEnvironment(env);
			String bluePrintImage=env.getProperty("blueprint.ImageName");
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
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
			logger.debug("<------dockerVMUserName---------->"+dockerVMUserName);
			String solutionPort=env.getProperty("docker.solutionPort");
			logger.debug("<------solutionPort---------->"+solutionPort);
			if (authObject == null) {
				logger.debug("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
            Azure azure = azureImpl.authorize(authObject);
            logger.debug("<------SolutionId---------->"+authObject.getSolutionId());
			logger.debug("<------authObject.getSolutionRevisionId()---------->"+authObject.getSolutionRevisionId());
			String bluePrintStr=azureImpl.getBluePrintNexus(authObject.getSolutionId(), authObject.getSolutionRevisionId(),dataSource,userName,password,nexusUrl,nexusUserName,nexusPassword);
			logger.debug("<------bluePrintStr---------->"+bluePrintStr);
			ParseJSON parseJson=new ParseJSON();
			Blueprint bluePrint=parseJson.jsonFileToObject();
			
			
			
			HashMap<String,String> imageMap=parseJson.parseJsonFile();
			ArrayList<String> list=azureImpl.iterateImageMap(imageMap);
			LinkedList<String> sequenceList=parseJson.getSequenceFromJSON();
			
			if(bluePrintImage!=null && !"".equals(bluePrintImage)){
				list.add(bluePrintImage);
				imageMap.put(bluePrintImage, "BluePrintContainer");
			}
			logger.debug("<------list---------->"+list);
			logger.debug("<------imageMap---------->"+imageMap);
			if(azure!=null) {
				AzureCompositeSolution compositeRunner =new AzureCompositeSolution(azure,authObject,env.getProperty("docker.containerNamePrefix"),env.getProperty("docker.registry.username"),
                        env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), 
                        env.getProperty("docker.port"), false),null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,imageMap,
                        sequenceList,dockerRegistryname,bluePrint,uidNumStr,dataSource,userName,password,dockerVMUserName,dockerVMPassword,solutionPort);


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
