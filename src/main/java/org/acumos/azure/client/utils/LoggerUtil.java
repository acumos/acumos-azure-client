package org.acumos.azure.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.azure.client.transport.SolutionDeployment;
import org.acumos.azure.client.transport.TransportBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
	public String printSingleSolutionImpl(AzureDeployDataObject deployDataObject,String dockerContainerPrefix,String localEnvDockerHost,
			String localEnvDockerCertPath,ArrayList<String> list,String uidNumStr,String solutionPort,String solutionId,String solutionRevisionId,
			String userId,String sleepTimeFirst,String sleepTimeSecond,String nexusRegistyName,String otherRegistyName) {
		logger.debug("deployDataObject " + deployDataObject);
		logger.debug("dockerContainerPrefix " + dockerContainerPrefix);
		logger.debug("localEnvDockerHost " + localEnvDockerHost);
		logger.debug("localEnvDockerCertPath " + localEnvDockerCertPath);
		logger.debug("list " + list);
		logger.debug("uidNumStr " + uidNumStr);
		logger.debug("solutionPort " + solutionPort);
		logger.debug("solutionId " + solutionId);
		logger.debug("solutionRevisionId " + solutionRevisionId);
		logger.debug("userId " + userId);
		logger.debug("sleepTimeFirst " + sleepTimeFirst);
		logger.debug("sleepTimeSecond " + sleepTimeSecond);
		logger.debug("nexusRegistyName "+nexusRegistyName);
		logger.debug("otherRegistyName "+otherRegistyName);
		
		return "success";
		
	}
	public String printExistingVMDetails(SolutionDeployment bean,TransportBean tbean) {
		logger.debug("vmHostIP "+bean.getVmHostIP());
		logger.debug("vmHostName "+bean.getVmHostName());
		logger.debug("solutionId "+bean.getSolutionId());
		logger.debug("solutionRevisionId "+bean.getSolutionRevisionId());
		logger.debug("DataSourceUrl "+tbean.getDataSourceUrl());
		logger.debug("DataSourceUserName "+tbean.getDataSourceUserName());
		logger.debug("DataSourcePd "+tbean.getDataSourcePd());
		logger.debug("probePrintImage "+tbean.getProbePrintImage());
		logger.debug("probePrintName "+tbean.getProbeName());
		logger.debug("probeInternalPort "+tbean.getProbeInternalPort());
		logger.debug("sleepTimeFirst "+tbean.getSleepTimeFirst());
		logger.debug("nexusRegistyUserName "+tbean.getNexusRegistyUserName());
		logger.debug("nexusRegistyPd "+tbean.getNexusRegistyPd());
		logger.debug("registryUserName "+tbean.getRegistryUserName());
		logger.debug("registryPd "+tbean.getRegistryPd());
		logger.debug("localHostEnv "+tbean.getLocalHostEnv());
		return "success";
	}
	public String printCompositeSolutionDetails(String userId,String azureDataFiles,String nginxInternalPort,String nginxImageName,
			String exposeDataBrokerPort,String internalDataBrokerPort,String nexusRegistyName,String otherRegistyName,
			String subnet,String vnet,String sleepTimeFirst,String sleepTimeSecond,String nginxMapFolder,
			String nginxWebFolder,AzureDeployDataObject authObject) {
			logger.debug("userId "+userId);
			logger.debug("azureDataFiles "+azureDataFiles);
			logger.debug("nginxInternalPort "+nginxInternalPort);
			logger.debug("nginxImageName "+nginxImageName);
			logger.debug("exposeDataBrokerPort "+exposeDataBrokerPort);
			logger.debug("internalDataBrokerPort "+internalDataBrokerPort);
			logger.debug("nexusRegistyName "+nexusRegistyName);
			logger.debug("otherRegistyName "+otherRegistyName);
			logger.debug("subnet "+subnet);
			logger.debug("vnet "+vnet);
			logger.debug("sleepTimeFirst "+sleepTimeFirst);
			logger.debug("sleepTimeSecond "+sleepTimeSecond);
			logger.debug("nginxMapFolder "+nginxMapFolder);
			logger.debug("nginxWebFolder "+nginxWebFolder);
	        logger.debug("authObject.UrlAttribute "+authObject.getUrlAttribute());
	        logger.debug("authObject.JsonMapping "+authObject.getJsonMapping());
	        logger.debug("authObject.JsonPosition "+authObject.getJsonPosition());
	        logger.debug("SolutionId "+authObject.getSolutionId());
			logger.debug("authObject.SolutionRevisionId "+authObject.getSolutionRevisionId());
		return "success";
	}
	public String printCompositeSolutionImplDetails(AzureDeployDataObject deployDataObject,String dockerContainerPrefix,ArrayList<String> list,
			String bluePrintName,String uidNumStr,LinkedList<String> sequenceList,HashMap<String,String> imageMap,String solutionPort,
			HashMap<String,DeploymentBean> nodeTypeContainerMap,String bluePrintJsonStr,String probeName,String probeInternalPort,
			String probeNexusEndPoint,String sleepTimeFirst,String sleepTimeSecond,String nexusRegistyName,String otherRegistyName,
			String exposeDataBrokerPort,String internalDataBrokerPort,TransportBean tbean) {
			logger.debug("deployDataObject "+deployDataObject);
			logger.debug("dockerContainerPrefix "+dockerContainerPrefix);
			logger.debug("list "+list);
			logger.debug("bluePrintName "+bluePrintName);
			logger.debug("uidNumStr "+uidNumStr);
			logger.debug("sequenceList "+sequenceList);
			logger.debug("imageMap "+imageMap);
			logger.debug("solutionPort "+solutionPort);
			logger.debug("nodeTypeContainerMap "+nodeTypeContainerMap);
			logger.debug("bluePrintJsonStr "+bluePrintJsonStr);
			logger.debug("probeName "+probeName);
			logger.debug("probeInternalPort "+probeInternalPort);
			logger.debug("probeNexusEndPoint "+probeNexusEndPoint);
			logger.debug("sleepTimeFirst " + sleepTimeFirst);
			logger.debug("sleepTimeSecond " + sleepTimeSecond);
			logger.debug("nexusRegistyName "+nexusRegistyName);
			logger.debug("otherRegistyName "+otherRegistyName);
			logger.debug("exposeDataBrokerPort "+exposeDataBrokerPort);
			logger.debug("internalDataBrokerPort "+internalDataBrokerPort);
			logger.debug("ProtoContainerMap "+tbean.getProtoContainerMap());
			logger.debug("NginxMapFolder "+tbean.getNginxMapFolder());
			logger.debug("NginxWebFolder "+tbean.getNginxWebFolder());
			logger.debug("NginxInternalPort "+tbean.getNginxInternalPort());
		return "success";
	}
}
