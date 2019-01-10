package org.acumos.azure.client.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.acumos.azure.client.service.AzureService;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.transport.AzureKubeBean;
import org.acumos.azure.client.transport.AzureKubeTransportBean;
import org.acumos.azure.client.transport.TransportBean;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.azure.client.utils.AzureEncrypt;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.ParseJSON;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;


public class AzureKubeSolution implements Runnable{
	Logger logger = LoggerFactory.getLogger(AzureKubeSolution.class);
	private AzureKubeBean auth;
	private AzureKubeTransportBean kubeTransportBean;
	private Azure azure;
	
	public AzureKubeSolution() {
		
	}
	
    public AzureKubeSolution(AzureKubeBean auth,AzureKubeTransportBean kubeTransportBean,Azure azure) {
    	 this.auth = auth;
    	 this.kubeTransportBean = kubeTransportBean;
    	 this.azure=azure;
	}
    
    public void run() {
    	logger.debug("AzureKubeSolution Run Started ");
    	AzureEncrypt azEncrypt=new AzureEncrypt();
    	AzureCommonUtil azureUtil=new AzureCommonUtil();
    	try {
    		InputStream inputStream = getAzureSolutionZip(auth,kubeTransportBean.getKubernetesClientUrl());
    		logger.debug("Zip Input stream completed ");
    		final Region region = Region.US_EAST;
    		int sleepTimeInt=Integer.parseInt(kubeTransportBean.getSleepTimeFirst());
    		if(inputStream!=null) {
    			kubeTransportBean.setSolutionZipStream(inputStream);
    			//String dockerVMPd=azureUtil.getRandomPassword(10).toString();
    			//kubeTransportBean.setDockerVMPd(dockerVMPd);
    			//logger.debug("VM PD "+azEncrypt.encrypt(dockerVMPd));
	    		String hostIp=DockerUtils.createNewAzureVM(azure, auth.getRgName(), region, kubeTransportBean.getNetworkSecurityGroup(),
	    				kubeTransportBean.getDockerVMUserName(),kubeTransportBean.getDockerVMPd(),kubeTransportBean.getSubnet(),
	    				kubeTransportBean.getVnet(),kubeTransportBean);
	    		 logger.debug("sleepTime "+sleepTimeInt);
	    		 Thread.sleep(sleepTimeInt);
	    		 logger.debug("VM completed "+hostIp);
	    		 DockerUtils.uploadZipVM(kubeTransportBean);
	    		 logger.debug("Upload file part completed ");
	    		 /*Send detail to your through notification*/
	    		 azureUtil.generateNotification("VM is created, IP is: "+hostIp, auth.getUserId(),
	    				 kubeTransportBean.getCmnDataUrl(), kubeTransportBean.getCmnDataUser(), kubeTransportBean.getCmnDataPd());
    		}
    	}catch(Exception e) {
    		logger.error("Exception in AzureKubeSolution failed", e);
    	}
    	logger.debug("AzureKubeSolution Run End ");
    }
	
	public String getSolutionCode(String solutionId,String datasource,String userName,String dataPd){
		logger.debug("getSolution start");
		String toolKitTypeCode="";
		try{
		AzureCommonUtil	azureUtil =new AzureCommonUtil();
		CommonDataServiceRestClientImpl cmnDataService=azureUtil.getClient(datasource,userName,dataPd);
		MLPSolution mlpSolution = cmnDataService.getSolution(solutionId);
			if (mlpSolution != null) {
				logger.debug("mlpSolution.getToolkitTypeCode() "+mlpSolution.getToolkitTypeCode());
				toolKitTypeCode=mlpSolution.getToolkitTypeCode();
			}
		}catch(Exception e){
			logger.error("Error in get solution "+e.getMessage());
			toolKitTypeCode="";
		}
		logger.debug("getSolution End toolKitTypeCode " +toolKitTypeCode);	
	  return toolKitTypeCode;
	 }	
	
	public InputStream getAzureSolutionZip(AzureKubeBean bean,String url)throws Exception{
		logger.debug("getAzureSolutionZip Start");
		InputStream inputStream=null;
		try {
			logger.debug("url "+url);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String kubeUrl=url+"/"+bean.getSolutionId()+"/"+bean.getSolutionRevisionId();
			logger.debug("kubeUrl "+kubeUrl);
			ResponseEntity<Resource> exchange = restTemplate.exchange(kubeUrl, HttpMethod.GET, entity, Resource.class);
			inputStream = exchange.getBody().getInputStream();
		  } catch (Exception e) {
			  logger.error("getAzureSolutionZip failed", e);
			  throw e;
		 }
		logger.debug("getAzureSolutionZip End");
		return inputStream;
	}
	
	
	

}
