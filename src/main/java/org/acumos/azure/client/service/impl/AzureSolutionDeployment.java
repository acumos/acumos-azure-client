package org.acumos.azure.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DataBrokerBean;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.azure.client.utils.ProbeIndicator;
import org.acumos.azure.client.utils.SSHShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.core.command.PullImageResultCallback;
import com.jcraft.jsch.JSchException;

public class AzureSolutionDeployment implements Runnable{
	Logger logger = LoggerFactory.getLogger(AzureSolutionDeployment.class);
	
	private SolutionDeployment deploymentBean;
	private TransportBean tbean;
	private boolean singleSolution;
	
	public AzureSolutionDeployment() {
			}
	public AzureSolutionDeployment(SolutionDeployment deploymentBean,TransportBean tbean,boolean singleSolution) {
		this.deploymentBean=deploymentBean;
		this.tbean=tbean;
		this.singleSolution=singleSolution;
	}
	public void run() {
		logger.debug("AzureSolutionDeployment run start ");
		try {
		    if(singleSolution) {
		    	logger.debug("singleSolution Run start");
		    }else {
		    	logger.debug("composite solution Run start");
		    	
		    }
		 }catch(Exception e) {
			 logger.error("AzureSolutionDeployment failed", e);
		}
		logger.debug("AzureSolutionDeployment run End ");
	}
	public void compositeSolutionDetails(SolutionDeployment bean,TransportBean tbean)throws Exception{
		AzureServiceImpl azureImpl=new AzureServiceImpl();
		ParseJSON parseJson=new ParseJSON();
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		try {
			String bluePrintJsonStr=azureImpl.getBluePrintNexus(bean.getSolutionId(), bean.getSolutionRevisionId(),
					        tbean.getDataSourceUrl(),tbean.getDataSourceUserName(),tbean.getDataSourcePd(),
					        tbean.getNexusUrl(),tbean.getNexusUserName(),tbean.getNexusPd());
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
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
        }catch(Exception e) {
        	logger.error("compositeSolutionDetails failed", e);
       	    throw e;
		}
		
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
			sshShell = SSHShell.open(deploymentBean.getVmHost(),22,deploymentBean.getVmUserName(),deploymentBean.getVmUserPd());
			logger.debug("Upload docker install script 1 ");
			
			 sshShell.upload(new ByteArrayInputStream(removeDockerScript.getBytes()),
						"removeDockerScript.sh", ".azuredocker", true, "4095");
			 logger.debug("Start executing script ");
			 output = sshShell
						.executeCommand("bash -c ~/.azuredocker/removeDockerScript.sh", true, true);
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
	public String checkInstallDocker(SolutionDeployment deploymentBean)throws Exception{
		logger.debug("checkInstallDocker Start ");
		SSHShell sshShell = null;
		String output="";
		try {
			
			 String repArray[]= {};//deploymentBean.getRepositoryDetails().split(",");
			 String daemon_file="";
			 String INSTALL_DOCKER = ""
					    +"if [[ $(which docker) && $(docker --version) ]]; then \n"
					    +"portNum=$(docker ps --format {{.Ports}}|sed \\ 's/.*0.0.0.0://g'|sed 's/->.*//g'|sed ':a;N;$!ba;s/\\n/,/g') \n"
					    +"port occupied by app docker $portNum \n"
					    +"else \n"
						+ "echo Running: \"if [ ! -d ~/.azuredocker/tls ]; then mkdir -p ~/.azuredocker/tls ; fi\" \n"
						+ "if [ ! -d ~/.azuredocker/tls ]; then mkdir -p ~/.azuredocker/tls ; fi \n"
						+ "echo Running: sudo apt-get update \n" + "sudo apt-get update \n"
						+ "echo Running: sudo apt-get install -y --no-install-recommends apt-transport-https ca-certificates curl software-properties-common \n"
						+ "sudo apt-get install -y --no-install-recommends apt-transport-https ca-certificates curl software-properties-common \n"
						+ "echo Running: curl -fsSL https://apt.dockerproject.org/gpg | sudo apt-key add - \n"
						+ "curl -fsSL https://apt.dockerproject.org/gpg | sudo apt-key add - \n"
						+ "echo Running: sudo add-apt-repository \"deb https://apt.dockerproject.org/repo/ ubuntu-$(lsb_release -cs) main\" \n"
						+ "sudo add-apt-repository \"deb https://apt.dockerproject.org/repo/ ubuntu-xenial main\" \n"
						+ "echo Running: sudo apt-get update \n" + "sudo apt-get update \n"
						+ "echo Running: sudo apt-get -y install docker-engine \n" + "sudo apt-get -y install docker-engine \n"
						+ "echo Running: sudo groupadd docker \n" + "sudo groupadd docker \n"
						+ "echo Running: sudo usermod -aG docker $USER \n" + "sudo usermod -aG docker $USER \n"
						+ "sudo usermod -aG docker $USER \n"
						+ "sudo sudo chmod 777 /var/run/docker.sock \n"
						+ "echo Code for nexus repository \n"
						+ "sudo chmod 777 /etc/docker \n"
						+ "sudo cp -f ~/.azuredocker/daemon.json /etc/docker/daemon.json"
						+ "sudo chmod 777 /etc/docker \n"
						+"sudo service docker restart \n"
						+ "echo Daemon restart done \n"
						+ "sudo sudo chmod 777 /var/run/docker.sock \n"
						+"fi ";
			 
			 
			 String daemonFirstPart=""
					    +	"{ \n"
						+	 " \"insecure-registries\": [ \n";
			String daemonSecondpart="";
			for(int i=0;i<repArray.length;i++ ){
				if(daemonSecondpart!=null && !"".equalsIgnoreCase(daemonSecondpart)){
					daemonSecondpart=daemonSecondpart+","+"\""+repArray[i]+"\"";
				}else{
					daemonSecondpart=daemonSecondpart+"\""+repArray[i]+"\"";
				}
				
			 }
			String daemonThirdPart=	  "], \n"
			+	 " \"disable-legacy-registry\": true \n"
			+	"} \n";
			
			 daemon_file=daemonFirstPart+daemonSecondpart+daemonThirdPart;	
			 logger.debug("daemon_file "+daemon_file);
			 sshShell = SSHShell.open(deploymentBean.getVmHost(),22,deploymentBean.getVmUserName(),deploymentBean.getVmUserPd());
			 sshShell.upload(new ByteArrayInputStream(INSTALL_DOCKER.getBytes()),
						"INSTALL_DOCKER.sh", ".azuredocker", true, "4095");
			 logger.debug("Upload docker install script 1 ");
			
			 sshShell.upload(new ByteArrayInputStream(daemon_file.getBytes()),
						"daemon.json", ".azuredocker", true, "4095");
			 logger.debug("Upload docker install script 2 ");
			 logger.debug("Start installing docker ");
			 output = sshShell
						.executeCommand("bash -c ~/.azuredocker/INSTALL_DOCKER.sh", true, true);
			 //sshShell.
			 logger.debug("SSH Cmplete output "+output);
		} catch (JSchException jSchException) {
			logger.error("JSchException in checkInstallDocker "+jSchException);
			throw jSchException;
		} catch (IOException ioException) {
			logger.error("JSchException in checkInstallDocker "+ioException);
			throw ioException;
		} catch (Exception exception) {
			logger.error("JSchException in checkInstallDocker "+exception);
			throw exception;
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		logger.debug(" checkInstallDocker End ");
		return output;
	}

}
