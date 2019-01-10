package org.acumos.azure.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.ContainerInfo;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.AzureEncrypt;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AzureSolutionDeployment implements Runnable{
	Logger logger = LoggerFactory.getLogger(AzureSolutionDeployment.class);
	
	private SolutionDeployment solutionBean;
	private TransportBean tbean;
	
	public AzureSolutionDeployment() {
			}
	public AzureSolutionDeployment(SolutionDeployment solutionBean,TransportBean tbean) {
		this.solutionBean=solutionBean;
		this.tbean=tbean;
	}
	public void run() {
		logger.debug("AzureSolutionDeployment run start ");
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			String solutionToolKitType=azureUtil.getSolutionCode(solutionBean.getSolutionId(), 
					tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
			logger.debug("solutionToolKitType "+solutionToolKitType);
			String checkSoftware=checkPrerequisites(solutionBean,azureUtil);
			logger.debug("checkSoftware "+checkSoftware);
			if(checkSoftware!=null && "success".equalsIgnoreCase(checkSoftware)) {
				String removeContainer=removeAllContainer(solutionBean);
				logger.debug("removeContainer "+removeContainer);
				if(removeContainer!=null && "success".equalsIgnoreCase(removeContainer) ) {
					if(solutionToolKitType!=null && !"".equals(solutionToolKitType) && "CP".equalsIgnoreCase(solutionToolKitType)){
				   		logger.debug("Composite Solution Details Start");
				   		compositeSolutionDetails(solutionBean,tbean);
				   		logger.debug("Composite Solution Details End");
				   	 }else{
				   		logger.debug("Single Solution Details Start");
				   		singleSolutionDetails(solutionBean, tbean);
				   		logger.debug("Single Solution Details End");
				   	 }
				}
			}
		   	
		 }catch(Exception e) {
			 logger.error("AzureSolutionDeployment failed", e);
		}
		logger.debug("AzureSolutionDeployment run End ");
	}
	public void singleSolutionDetails(SolutionDeployment solutionBean,TransportBean tbean)throws Exception{
		 logger.debug("singleSolutionDetails Start");
		 AzureCommonUtil azureUtil=new AzureCommonUtil();
		 AzureContainerBean containerBean = new AzureContainerBean();
		 String regUserName="";
		 String regPass="";
		 try {
			 String singleImageTag=azureUtil.getSingleImageData(solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					 tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
			 logger.debug("singleImageTag "+singleImageTag);
			 String repositoryName=azureUtil.getRepositoryName(singleImageTag);
			 logger.debug("repositoryName "+repositoryName);
			 if(azureUtil.getRepositryStatus(singleImageTag, tbean.getNexusRegistyName())){
				 regUserName=tbean.getNexusRegistyUserName();
				 regPass=tbean.getNexusRegistyPd(); 
			 }else {
				 regUserName=tbean.getRegistryUserName();
				 regPass=tbean.getNexusRegistyPd(); 
			 }
			 logger.debug("regUserName "+regUserName+ " regPass "+regPass); 
			 String portNumberString="8557"+":"+tbean.getSolutionPort();
			 logger.debug("portNumberString "+portNumberString); 
			 DockerUtils.deploymentImageVM(solutionBean.getVmHostIP(),  solutionBean.getVmUserName(), solutionBean.getVmUserPd(), 
					repositoryName,regUserName, regPass, singleImageTag,
					portNumberString,Integer.parseInt(tbean.getSleepTimeFirst()));
			 containerBean.setContainerIp(solutionBean.getVmHostIP());
			 containerBean.setContainerPort("8557");
			 containerBean.setContainerName("ContainerOne");
			 azureUtil.generateNotification("Single Solution Deployed, IP is: "+solutionBean.getVmHostIP(), solutionBean.getUserId(),
					 tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
			 createDeploymentData(tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(), containerBean,
					 solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					 solutionBean.getUserId(), tbean.getUidNumStr(), AzureClientConstants.DEPLOYMENT_PROCESS);
		 } catch (Exception e) {
			 logger.error("AzureSimpleSolution  for existing vm failed", e);
			 try{
					azureUtil.generateNotification("Error in vm creation", solutionBean.getUserId(),
							tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd());
					createDeploymentData(tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(), containerBean,
							solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
							solutionBean.getUserId(), tbean.getUidNumStr(), AzureClientConstants.DEPLOYMENT_FAILED);
				}catch(Exception ex){
					logger.error("createDeploymentData failed", e);
				}
		 }
		logger.debug("singleSolutionDetails End");
	}
	public void compositeSolutionDetails(SolutionDeployment solutionBean,TransportBean tbean)throws Exception{
		logger.debug("compositeSolutionDetails Start");
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		ParseJSON parseJson=new ParseJSON();
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		List<AzureContainerBean> azureContainerBeanList=new ArrayList<AzureContainerBean>();
		List<DeploymentBean> deploymentList=new ArrayList<DeploymentBean>();
		DockerInfoList  dockerList=new DockerInfoList();
		ArrayList<DockerInfo> dockerInfoList=new ArrayList<DockerInfo>();
		Blueprint bluePrintProbe=null;
		try {
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(solutionBean.getSolutionId(), solutionBean.getSolutionRevisionId(),
					        tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(),
					        tbean.getNexusUrl(),tbean.getNexusUserName(),tbean.getNexusPd());
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
			boolean probeIndicator=parseJson.checkProbeIndicator(AzureClientConstants.JSON_FILE_NAME);
			
			HashMap<String,String> imageMap=null;
			HashMap<String,DeploymentBean> nodeTypeContainerMap=null;
			ArrayList<String> list=null;
			LinkedList<String> sequenceList=null;
			DataBrokerBean dataBrokerBean=null;
			Map<String,String> protoContainerMap=null;
			logger.debug("probeIndicator "+probeIndicator);
			
			 if(probeIndicator){
					
					imageMap=parseJson.parseJsonFileImageMap(AzureClientConstants.JSON_FILE_NAME);
					//Node Type and container Name in nodes
					nodeTypeContainerMap=parseJson.getNodeTypeContainerMap(AzureClientConstants.JSON_FILE_NAME);
					// images list
					list=azureImpl.iterateImageMap(imageMap);
					//proto files
					tbean.setProtoContainerMap(parseJson.getProtoDetails(AzureClientConstants.JSON_FILE_NAME));
					dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
					if(dataBrokerBean!=null){
						if(dataBrokerBean!=null){
							ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(tbean.getNexusUrl(),tbean.getNexusUserName(),
									            tbean.getNexusPd(), dataBrokerBean.getProtobufFile());
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
					//proto files
					dataBrokerBean=parseJson.getDataBrokerContainer(AzureClientConstants.JSON_FILE_NAME);
					if(dataBrokerBean!=null){
						if(dataBrokerBean!=null){
							ByteArrayOutputStream byteArrayOutputStream=azureUtil.getNexusUrlFile(tbean.getNexusUrl(),tbean.getNexusUserName(),
									            tbean.getNexusPd(), dataBrokerBean.getProtobufFile());
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
			    	list.add(tbean.getNginxImageName());
					imageMap.put(tbean.getNginxImageName(), AzureClientConstants.NGINX_CONTAINER);
					sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.NGINX_CONTAINER);
					if (tbean.getProbePrintImage() != null && !"".equals(tbean.getProbePrintImage())) {
						list.add(tbean.getProbePrintImage());
						imageMap.put(tbean.getProbePrintImage(), AzureClientConstants.PROBE_CONTAINER_NAME);
						sequenceList=azureImpl.addContainerSequence(sequenceList,AzureClientConstants.PROBE_CONTAINER_NAME);
					}
				}	

				if (tbean.getBluePrintImage() != null && !"".equals(tbean.getBluePrintImage())) {
					list.add(tbean.getBluePrintImage());
					imageMap.put(tbean.getBluePrintImage(), AzureClientConstants.BLUEPRINT_CONTAINER);
				}
				logger.debug("list "+list);
				logger.debug("imageMap "+imageMap);
				logger.debug("sequenceList "+sequenceList);
				int portNoIncrement=8557;
				String portNumberString="";
				String probeNexusEndPoint="";
				int imageCount=0;
				String bluePrintPort="";
				String probeIP="";
				String probePort="";
				if(sequenceList!=null && sequenceList.size() > 0){
	            	Iterator seqItr = sequenceList.iterator();
	                while (seqItr.hasNext()) {
	                    String jsonContainerName=(String)seqItr.next(); 
	                    logger.debug(" jsonContainerName "+jsonContainerName);
	                    Iterator imageMapItr=imageMap.entrySet().iterator();
			            while(imageMapItr.hasNext()){
			            	Map.Entry pair = (Map.Entry)imageMapItr.next();
			            	String imageName=(String)pair.getKey();
			            	String imageContainerName=(String)pair.getValue();
			            	 logger.debug(" imageName "+imageName);
			            	 logger.debug(" imageContainerName "+imageContainerName);
			            	 if(imageContainerName!=null && imageContainerName.equalsIgnoreCase(jsonContainerName)){
			            		 logger.debug(" Inside processing of Images ");
			            		 String portNumber="";
			            		 String regUserName="";
			            		 String regPass="";
			            		 String nodeTypeContainer="";
			            		 String nodeTypeName="";
			            		 AzureContainerBean containerBean=new AzureContainerBean();
			            		 DeploymentBean deployment=new DeploymentBean();
			            		 DockerInfo dockerinfo=new DockerInfo();
			            		 String repositoryName=azureUtil.getRepositoryName(imageName);
			            		 logger.debug(" imageName "+imageName+" repositoryName "+repositoryName);
			            		 if(nodeTypeContainerMap!=null && nodeTypeContainerMap.size() > 0 && nodeTypeContainerMap.get(jsonContainerName)!=null){
		    		            		DeploymentBean dBean=nodeTypeContainerMap.get(jsonContainerName);
		    		            		if(dBean!=null){
		    		            			nodeTypeContainer=dBean.getNodeType();
		    		            			nodeTypeName=dBean.getDataBrokerType();
		    		            		}
		    		            		
		    		            	}
			            		 if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.BLUEPRINT_CONTAINER_NAME)){
			            			 portNumber="8555"; 
			            			 bluePrintPort=portNumber;
			            			 portNumberString=portNumber+":"+portNumber;
			            			 regUserName=tbean.getBluePrintUser();
			            			 regPass=tbean.getBluePrintPass();
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            		 }else if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.PROBE_CONTAINER_NAME)) {
			            			 portNumberString=tbean.getProbeInternalPort()+":"+tbean.getProbeInternalPort();
	    		        			 probeNexusEndPoint="http://"+solutionBean.getVmHostName()+":"+tbean.getNginxPort();
	    		        			 probePort=tbean.getProbeInternalPort();
	    		        			 regUserName=tbean.getProbUser();
			            			 regPass=tbean.getProbePass();
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.PROBE_NODE_TYPE);
			            		 }else if(jsonContainerName.equalsIgnoreCase(AzureClientConstants.NGINX_CONTAINER)) {
			            			 portNumber=String.valueOf(portNoIncrement);
			            			 portNoIncrement=portNoIncrement+1;
			            			 portNumberString=portNumber+":"+tbean.getNginxInternalPort();
	    		        			 tbean.setNginxPort(portNumber);
	    		        			 regUserName="";
			            			 regPass="";
			            			 deployment.setDataBrokerType("");
			            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            		 }else{
			            			 portNumber=String.valueOf(portNoIncrement);
			            			 portNoIncrement=portNoIncrement+1;
			            			 logger.debug(" portNumber "+portNumber);
			            			 if(tbean.getSolutionPort()!=null && !"".equals(tbean.getSolutionPort())){
		    		        			 portNumberString=portNumber+":"+tbean.getSolutionPort();
		    		        			}else{
		    		        			 portNumberString=portNumber+":"+portNumber;
		    		        		  }
			            			 
			            			 if(azureUtil.getRepositryStatus(imageName, tbean.getNexusRegistyName())){
			            				 regUserName=tbean.getNexusRegistyUserName();
				            			 regPass=tbean.getNexusRegistyPd(); 
			            			 }else {
			            				 regUserName=tbean.getRegistryUserName();
				            			 regPass=tbean.getNexusRegistyPd(); 
			            			 }
			            			 if(nodeTypeContainer!=null && !"".equals(nodeTypeContainer)){
			            				    deployment.setNodeType(nodeTypeContainer);
			    		        			deployment.setDataBrokerType(nodeTypeName);
			            			 }else {
			            				 deployment.setDataBrokerType(""); 
				            			 deployment.setNodeType(AzureClientConstants.DEFAULT_NODE_TYPE);
			            			 }
			            			 
			            		 }
			            		 logger.debug(" portNumber "+portNumber);
			            		 logger.debug(" portNumberString "+portNumberString);
			            		 logger.debug(" regUserName "+regUserName+" regPass "+regPass);
			            		 imageCount=imageCount+1;
			            		 DockerUtils.deploymentCompositeImageVM(solutionBean.getVmHostIP(), solutionBean.getVmUserName(), solutionBean.getVmUserPd(),
			            				    repositoryName, regUserName,regPass, imageName,jsonContainerName,imageCount,
		    		        				portNumberString,probeNexusEndPoint,Integer.parseInt(tbean.getSleepTimeFirst()),tbean);
			            		 
			            		    
			            		    dockerinfo.setIpAddress(solutionBean.getVmHostName());
	            		            dockerinfo.setPort(portNumber);
	            		            dockerinfo.setContainer(jsonContainerName);
	            		            
		    		        		containerBean.setContainerName(jsonContainerName);
		    		        		containerBean.setContainerIp(solutionBean.getVmHostIP());
		    		        		containerBean.setContainerPort(portNumber);
		    		        		
		    		        		deployment.setAzureVMIP(solutionBean.getVmHostIP());
		    		        		deployment.setAzureVMName(solutionBean.getVmHostName());
		    		        		deployment.setContainerName(jsonContainerName);
		    		        		deployment.setContainerPort(portNumber);
		    		        		
		    		        		dockerInfoList.add(dockerinfo);
		    		        		azureContainerBeanList.add(containerBean);
		    		        		deploymentList.add(deployment);
			            	 }
			            }
	                }
				}
				if(dockerInfoList!=null && dockerInfoList.size() > 0){
	            	dockerList.setDockerList(dockerInfoList);
	            }
				String urlDockerInfo="http://"+solutionBean.getVmHostIP()+":"+bluePrintPort+"/"+AzureClientConstants.PUT_DOCKER_INFO_URL;  
				String urlBluePrint="http://"+solutionBean.getVmHostIP()+":"+bluePrintPort+"/"+AzureClientConstants.PUT_BLUEPRINT_INFO_URL;
				logger.debug("urlDockerInfo "+urlDockerInfo+" urlBluePrint "+urlBluePrint);
				String dataBrokerPort=getDataBrokerPort(deploymentList,AzureClientConstants.DATABROKER_NAME);
				String urlDataBroker="http://"+solutionBean.getVmHostIP()+":"+dataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
				String csvDataBrokerPort="";
				String csvDataBrokerUrl="";
				if(dataBrokerBean!=null){
					  csvDataBrokerPort=getDataBrokerPortCSV(deploymentList,AzureClientConstants.DATABROKER_NAME);
				 }
				if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
					  csvDataBrokerUrl="http://"+solutionBean.getVmHostIP()+":"+csvDataBrokerPort+"/"+AzureClientConstants.CONFIG_DB_URL;
				  }
				 logger.debug("csvDataBrokerUrl "+csvDataBrokerUrl);
				 logger.debug("csvDataBrokerPort "+csvDataBrokerPort);
				 logger.debug("urlDataBroker "+urlDataBroker);
				 logger.debug("dataBrokerPort "+dataBrokerPort);
				// Added for probe
				  if(csvDataBrokerPort!=null && !"".equalsIgnoreCase(csvDataBrokerPort)){
					  logger.debug("Inside csv Data Broker ConfigDB  "); 
					  callCsvConfigDB(solutionBean,csvDataBrokerUrl,dataBrokerBean);
					 }
				// putBlueprint
				 if(bluePrintProbe!=null){
					 putBluePrintDetailsJSON(bluePrintJsonStr,urlBluePrint);
				  }
				// putDockerInfo
				 if(dockerList != null){
					  logger.debug("Inside probeContainerBeanList ");
					  putContainerDetailsJSONProbe(dockerList,urlDockerInfo);
					}
				 // configDB
				 if(dataBrokerPort!=null &&  !"".equals(dataBrokerPort)){
					 logger.debug("Inside putDataBrokerDetails ");
					  putDataBrokerDetails(solutionBean,urlDataBroker);
					}
				
				// Added notification for probe code
				 probeIndicatorList = bluePrintProbe.getProbeIndicator();
				 prbIndicator = null;
				 if(probeIndicatorList != null && probeIndicatorList.size() >0) {
						prbIndicator = probeIndicatorList.get(0);
				 }	
				 
				 if(solutionBean.getVmHostIP()!=null && !"".equals(solutionBean.getVmHostIP())){
					 azureUtil.generateNotification("Composite Solution is deployed, IP is: "+solutionBean.getVmHostIP(), solutionBean.getUserId(),
							 tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
				 }
				 //if (bluePrint.getProbeIndocator() != null && bluePrint.getProbeIndocator().equalsIgnoreCase("True"))  {
				 if (bluePrintProbe.getProbeIndicator() != null && prbIndicator != null && prbIndicator.getValue().equalsIgnoreCase("True"))  {
					 logger.debug("Probe indicator true. Starting generatenotircation deployDataObject.getUserId() "+solutionBean.getUserId());
					 logger.debug(" probeIP "+solutionBean.getVmHostIP()+" probePort "+probePort);
					 azureUtil.generateNotification("Probe IP and Port: "+solutionBean.getVmHostIP()+":"+probePort,solutionBean.getUserId(),
							 tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
				 }
				 if(azureContainerBeanList!=null){
			   			logger.debug("Start saving data in database "); 
			   			createDeploymentCompositeData(tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd(),azureContainerBeanList,solutionBean.getSolutionId(),
			   					solutionBean.getSolutionRevisionId(),solutionBean.getUserId(),tbean.getUidNumStr(),AzureClientConstants.DEPLOYMENT_PROCESS);
			      }
		 	
        }catch(Exception e) {
        	logger.error("compositeSolutionDetails for existing failed", e);
        	try{
	        	azureUtil.generateNotification("Error in vm creation", solutionBean.getUserId(),
	        			tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd());
				createDeploymentCompositeData(tbean.getDataSourceUrl(), tbean.getDataSourceUserName(), tbean.getDataSourcePd(),azureContainerBeanList,solutionBean.getSolutionId(),
						solutionBean.getSolutionRevisionId(),solutionBean.getUserId(),tbean.getUidNumStr(),AzureClientConstants.DEPLOYMENT_FAILED);
        	}catch(Exception ex){
				logger.error("compositeSolutionDetails for existing failed  in saving data", e);
			}
		}
	  logger.debug("compositeSolutionDetails End");
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
	public String checkPrerequisites(SolutionDeployment deploymentBean,AzureCommonUtil azureUtil)throws Exception{
		 String installScript=azureUtil.getFileDetails(AzureClientConstants.SETUP_SCRIPT_NAME);
		 logger.debug("installScript "+installScript);
		return "success";
	}
	public String removeAllContainer(SolutionDeployment deploymentBean)throws Exception{
		logger.debug("removeAllContainer Start ");
		SSHShell sshShell = null;
		String output="";
		String returnStr="success";
		try {
			String removeDockerScript=""
					                 + "docker stop $(docker ps -a -q) \n"
						             +"docker rm $(docker ps -a -q) ";
			sshShell = SSHShell.open(deploymentBean.getVmHostIP(),22,deploymentBean.getVmUserName(),deploymentBean.getVmUserPd());
			logger.debug("Upload docker install script 1 ");
			
			 sshShell.upload(new ByteArrayInputStream(removeDockerScript.getBytes()),
						"removeDockerScript.sh", "azuredockerscript", true, "4095");
			 logger.debug("Start executing script ");
			 output = sshShell
						.executeCommand("bash -c ~/azuredockerscript/removeDockerScript.sh", true, true);
			 logger.debug("output "+output);
			
		} catch (Exception exception) {
			logger.error("Exception in checkInstallDocker "+exception);
			returnStr="fail";
			throw exception;
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		
		logger.debug("removeAllContainer End returnStr "+returnStr);
		return returnStr;
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
	
	public void callCsvConfigDB(SolutionDeployment deployDataObject,String apiUrl,DataBrokerBean dataBrokerBean)throws Exception{
		logger.debug("callCsvConfigDB Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			final String url = apiUrl;
			if(deployDataObject!=null){
				dataBrokerBean.setUserName(deployDataObject.getUsername());
				dataBrokerBean.setUserPd(deployDataObject.getUserPd());
				dataBrokerBean.setHost(deployDataObject.getHost());
				dataBrokerBean.setPort(deployDataObject.getPort());
			}
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
  
	public void putDataBrokerDetails(SolutionDeployment deployDataObject,String apiUrl)throws Exception{
		logger.debug("putDataBrokerDetails Start");
		try {
			logger.debug("apiUrl "+apiUrl);
			logger.debug("UrlAttribute "+deployDataObject.getUrlAttribute());
			logger.debug("JsonMapping "+deployDataObject.getJsonMapping());
			logger.debug("JsonPosition "+deployDataObject.getJsonPosition());
			//logger.debug("=====dataBrokerScript==="+dataBrokerScript);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("jsonUrl", deployDataObject.getUrlAttribute());
			//map.add("jsonScript", dataBrokerScript);
			map.add("jsonMapping", deployDataObject.getJsonMapping());
			map.add("jsonPosition", deployDataObject.getJsonPosition());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			restTemplate.exchange(apiUrl, HttpMethod.PUT, request, String.class);
		    
		  } catch (Exception e) {
			  logger.error("generateNotification failed", e);
			  throw e;
		 }
		logger.debug("putDataBrokerDetails End");
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
			AzureCommonUtil azureUtil=new AzureCommonUtil();
			CommonDataServiceRestClientImpl client = azureUtil.getClient(dataSource, dataUserName, dataPd);
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

}
