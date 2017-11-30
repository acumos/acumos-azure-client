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

	 Logger logger =LoggerFactory.getLogger(AzureServiceController.class);
	@CrossOrigin
	//@ApiOperation(value = "Gets a page of solutions matching the specified tag.", response = String.class, responseContainer = "Page")
	@RequestMapping(value = {org.acumos.azure.client.api.APINames.AZURE_AUTH_PUSH_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public String authorizeAndPushImage(HttpServletRequest request, @RequestBody AzureDeployDataObject authObject, HttpServletResponse response) throws Exception {
		
		JSONObject  jsonOutput = new JSONObject();
		logger.debug("<------start----authorizeAndPushImage in AzureServiceController------------>");
		DockerInfoList  dockerList=new DockerInfoList();
		AzureBean azBean=new AzureBean();
		try {
			if (authObject == null) {
				System.out.println("Insufficient data to authneticate with Azure AD");
				jsonOutput.put("status", APINames.AUTH_FAILED);
				return jsonOutput.toString();
			}
			ObjectMapper mapper = new ObjectMapper();
            AzureServiceImpl azureImpl=new AzureServiceImpl();
			Azure azure = azureImpl.authorize(authObject);	
			/*List<NetworkSecurityGroup> networkSecurityGroups = azure.networkSecurityGroups().list();
	        for (NetworkSecurityGroup networkSecurityGroup: networkSecurityGroups) {
	            Utils.print(networkSecurityGroup);
	            List<Subnet> subnetList=networkSecurityGroup.listAssociatedSubnets();
	            
	        }*/
			ParseJSON parseJson=new ParseJSON();
			ArrayList<String> list=parseJson.parseJsonFile();
			Blueprint bluePrint=parseJson.jsonFileToObject();
			String bluePrintImage=env.getProperty("blueprint.ImageName");
			String bluePrintName=env.getProperty("blueprint.name");
			String bluePrintUser=env.getProperty("docker.registry.bluePrint.username");
			String bluePrintPass=env.getProperty("docker.registry.bluePrint.password");
			String networkSecurityGroup=env.getProperty("docker.registry.networkgroupName");
			String dockerRegistryPort=env.getProperty("docker.registry.port");
			logger.debug("<------bluePrintImage---------->"+bluePrintImage);
			if(bluePrintImage!=null && !"".equals(bluePrintImage)){
				list.add(bluePrintImage);
			}
			 //Authorization done, now try to push the image
			  if(azure!=null) {
				  azBean=azureImpl.pushImage(azure, authObject, env.getProperty("docker.containerNamePrefix"), env.getProperty("docker.registry.username"),
						  env.getProperty("docker.registry.password"),dockerHosttoUrl(env.getProperty("docker.host"), env.getProperty("docker.port"), false),
						  null,list,bluePrintName,bluePrintUser,bluePrintPass,networkSecurityGroup,dockerRegistryPort);
				  
				  if(azBean!=null && azBean.getBluePrintMap()!=null){
					  HashMap<String,String> hmap=new HashMap<String,String>();
					  hmap=azBean.getBluePrintMap();
				  }
				  if(azBean!=null ){
					  logger.debug("<------azBean.getAzureVMIP()---------->"+azBean.getAzureVMIP()+"<----azBean.getBluePrintPort()------>"+azBean.getBluePrintPort());
					  if(azBean.getAzureVMIP()!=null && azBean.getBluePrintPort()!=null){
						String urlDockerInfo="http://"+azBean.getAzureVMIP()+":"+azBean.getBluePrintPort()+"/putDockerInfo";  
						String urlBluePrint="http://"+azBean.getAzureVMIP()+":"+azBean.getBluePrintPort()+"/putBlueprint";
						
						logger.debug("<------urlDockerInfo---------->"+urlDockerInfo);
						logger.debug("<------urlBluePrint---------->"+urlBluePrint);
						List<Node> nodeList=bluePrint.getNodes();
						DockerInfoList dockerInfoList=azBean.getDockerinfolist();
						List<DockerInfo> dockerArList=dockerInfoList.getDockerList();
						if(dockerArList!=null  && dockerArList.size() > 0){
							for(DockerInfo dockerInfo:dockerArList){
								if(dockerInfo!=null && dockerInfo.getContainer() !=null 
										&& !"".equals(dockerInfo.getContainer())){
									String imageName=dockerInfo.getContainer();
									String imageContainerName="";
									if(nodeList!=null && nodeList.size() > 0){
										for(Node node:nodeList){
											if(node!=null && node.getImage()!=null && node.getImage().equalsIgnoreCase(imageName)){
												logger.debug("<----node.getImage()--->"+node.getImage()+"==imageName=="+imageName);
												imageContainerName=node.getContainerName();
											}
										}
									}else{
										logger.debug("<------Node List in blank in Bluprint---------->");
										imageContainerName="";
									}
									logger.debug("<----Set ImageContainerName--->"+imageContainerName);
									dockerInfo.setContainer(imageContainerName);
									
								}
								
							}
						}
						logger.debug("blueprint==========>"+ mapper.writeValueAsString(bluePrint));
						logger.debug("dockerInfoList==========>"+ mapper.writeValueAsString(dockerInfoList));
						if(azBean.getDockerinfolist()!=null){
							azureImpl.putContainerDetails(azBean.getDockerinfolist(),urlDockerInfo);
						}
						if(bluePrint!=null){
							azureImpl.putBluePrintDetails(bluePrint,urlBluePrint);
						}
					  }
					  
					  
				  }
				  
				  
				  jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
			}
			response.setStatus(200);	
			
		} catch (com.microsoft.aad.adal4j.AuthenticationException e) {
			System.out.println(e.getMessage() + " Returning... " + APINames.AUTH_FAILED );
			response.setStatus(401);
			jsonOutput.put("status", APINames.AUTH_FAILED);
			return jsonOutput.toString();
		} catch (Exception e) {
			if(e.getMessage().contains("com.microsoft.aad.adal4j.AuthenticationException")) {
				//Authentication error, set status code 401
				response.setStatus(401);
				jsonOutput.put("status", APINames.AUTH_FAILED);
				logger.error(e.getMessage() + " Returning... "+jsonOutput.toString());
				return jsonOutput.toString();
			} else {
				// for now we do not want to treat any other error as error, return 200
				e.printStackTrace();
				logger.debug(e.getMessage() + " Returning... " + APINames.SUCCESS_RESPONSE);
				response.setStatus(200);
				jsonOutput.put("status", APINames.SUCCESS_RESPONSE);
				return jsonOutput.toString();
			}
		}
		logger.debug("<------End----authorizeAndPushImage in AzureServiceController------------>");
		return jsonOutput.toString();
	}
	
	private String dockerHosttoUrl(String host, String port, boolean socket) {
		return ((socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}
	

}


