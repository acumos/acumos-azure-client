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

package org.acumos.azure.client.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.acumos.azure.client.transport.AzureKubeTransportBean;
import org.acumos.azure.client.transport.TransportBean;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glassfish.jersey.SslConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.SSLConfig;
import com.github.dockerjava.core.util.CertificateUtils;
import com.jcraft.jsch.JSchException;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.KnownLinuxVirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NicIPConfiguration;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;

/**
 * Utility class to be used by Azure Container Registry sample. - Creates "in
 * memory" SSL configuration to be used by the Java Docker client - Builds a
 * Docker client config object - Creates a new Azure virtual machine and
 * installs Docker - Creates a Java DockerClient to be used for communicating
 * with a Docker host/engine
 */
public class DockerUtils {

	/**
	 * Creates "in memory" SSL configuration to be used by the Java Docker Client.
	 */
	static Logger log = LoggerFactory.getLogger(DockerUtils.class);
	static String dockerPort;

	public static class DockerSSLConfig implements SSLConfig, Serializable {

		private static final long serialVersionUID = -472691726142100440L;

		private SslConfigurator sslConfig;

		/**
		 * Constructor for the class.
		 * 
		 * @param caPem
		 *            - content of the ca.pem certificate file
		 * @param keyPem
		 *            - content of the key.pem certificate file
		 * @param certPem
		 *            - content of the cert.pem certificate file
		 */
		public DockerSSLConfig(String caPem, String keyPem, String certPem) {
			try {
				Security.addProvider(new BouncyCastleProvider());
				String e = System.getProperty("https.protocols");
				System.setProperty("https.protocols", "TLSv1");
				sslConfig = SslConfigurator.newInstance(true);
				if (e != null) {
					System.setProperty("https.protocols", e);
				}

				sslConfig.keyStore(CertificateUtils.createKeyStore(keyPem, certPem));
				sslConfig.keyStorePassword("docker");
				sslConfig.trustStore(CertificateUtils.createTrustStore(caPem));
			} catch (Exception e) {
				log.error("DockerSSLConfig failed", e);
				throw new DockerClientException(e.getMessage(), e);
			}
		}

		@Override
		public SSLContext getSSLContext() {
			return sslConfig.createSSLContext();
		}
	}

	/**
	 * Instantiate a Docker client that will be used for Docker client related
	 * operations.
	 * 
	 * @param azure
	 *            - instance of Azure
	 * @param rgName
	 *            - name of the Azure resource group to be used when creating a
	 *            virtual machine
	 * @param region
	 *            - region to be used when creating a virtual machine
	 * @param registryServerUrl
	 *            - address of the private container registry
	 * @param username
	 *            - user name to connect with to the private container registry
	 * @param acrPd
	 *            - acrPd to connect with to the private container registry
	 * @param localEnvDockerHost
	 *            local docker host
	 * @param localEnvDockerCertPath
	 *            local docker cert path
	 * @param azureBean
	 *            Azure bean
	 * @param networkSecurityGroup
	 *            network security group
	 * @param dockerRegistryPort
	 *            docker registry port
	 * @param dockerRegistryName
	 *            docker registyr name
	 *  @param dockerVMUserName  docker vm username
	 *  @param dockerVMPd  docker vm pd
	 *  @param subNet     subnet details
	 *  @param vnet       vnet details 
	 *  @param sleepTimeFirstInt   Thread sleep time    
	 * @return an instance of DockerClient
	 * @throws Exception
	 *             exception thrown
	 */
	public static DockerClient createDockerClient(Azure azure, String rgName, Region region, String registryServerUrl,
			String username, String acrPd, String localEnvDockerHost, String localEnvDockerCertPath,
			AzureBean azureBean, String networkSecurityGroup, String dockerRegistryPort, String dockerRegistryName,
			String dockerVMUserName,String dockerVMPd,String subNet,String vnet,int sleepTimeFirstInt)
			throws Exception {
		// final String envDockerHost = System.getenv("DOCKER_HOST");
		final String envDockerHost = localEnvDockerHost;
		final String envDockerCertPath = System.getenv("DOCKER_CERT_PATH");
		String dockerHostUrl;
		DockerClient dockerClient;
		dockerPort = dockerRegistryPort;
		if (envDockerHost == null || envDockerHost.isEmpty()) {
			// Could not find a Docker environment; presume that there is no local Docker
			// engine running and
			// attempt to configure a Docker engine running inside a new Azure virtual
			// machine
			dockerClient = fromNewDockerVM(azure, rgName, region, registryServerUrl, username, acrPd, azureBean,
					networkSecurityGroup, dockerRegistryPort, dockerRegistryName,dockerVMUserName,dockerVMPd,subNet,vnet,sleepTimeFirstInt);
		} else {
			dockerHostUrl = envDockerHost;
			log.debug("Using local settings to connect to a Docker service: " + dockerHostUrl);
			log.debug("Using local settings to connect to a Docker service: " + dockerHostUrl);

			DockerClientConfig dockerClientConfig;
			if (envDockerCertPath == null || envDockerCertPath.isEmpty()) {
				dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, acrPd);
			} else {
				String caPemPath = envDockerCertPath + File.separator + "ca.pem";
				String keyPemPath = envDockerCertPath + File.separator + "key.pem";

				String certPemPath = envDockerCertPath + File.separator + "cert.pem";
				String keyPemContent = new String(Files.readAllBytes(Paths.get(keyPemPath)));
				String certPemContent = new String(Files.readAllBytes(Paths.get(certPemPath)));
				String caPemContent = new String(Files.readAllBytes(Paths.get(caPemPath)));

				dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, acrPd,
						caPemContent, keyPemContent, certPemContent);
			}

			dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
			log.debug("List Docker host info");
		}

		return dockerClient;
	}

	/**
	 * Creates a DockerClientConfig object to be used when creating the Java Docker
	 * client using a secured connection.
	 * 
	 * @param host
	 *            - Docker host address (IP) to connect to
	 * @param registryServerUrl
	 *            - address of the private container registry
	 * @param username
	 *            - user name to connect with to the private container registry
	 * @param acrPd
	 *            - acrPd to connect with to the private container registry
	 * @param caPemContent
	 *            - content of the ca.pem certificate file
	 * @param keyPemContent
	 *            - content of the key.pem certificate file
	 * @param certPemContent
	 *            - content of the cert.pem certificate file
	 * @return an instance of DockerClient configuration
	 */
	public static DockerClientConfig createDockerClientConfig(String host, String registryServerUrl, String username,
			String acrPd, String caPemContent, String keyPemContent, String certPemContent) {
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(true)
				.withCustomSslConfig(new DockerSSLConfig(caPemContent, keyPemContent, certPemContent))
				.withRegistryUrl(registryServerUrl).withRegistryUsername(username).withRegistryPassword(acrPd)
				.build();
	}

	/**
	 * Creates a DockerClientConfig object to be used when creating the Java Docker
	 * client using an unsecured connection.
	 * 
	 * @param host
	 *            - Docker host address (IP) to connect to
	 * @param registryServerUrl
	 *            - address of the private container registry
	 * @param username
	 *            - user name to connect with to the private container registry
	 * @param acrPd
	 *            - acrPd to connect with to the private container registry
	 * @return an instance of DockerClient configuration
	 */
	public static DockerClientConfig createDockerClientConfig(String host, String registryServerUrl, String username,
			String acrPd) {
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(false)
				.withRegistryUrl(registryServerUrl).withRegistryUsername(username).withRegistryPassword(acrPd)
				.build();
	}

	/**
	 * It creates a new Azure virtual machine and it instantiate a Java Docker
	 * client.
	 * 
	 * @param azure
	 *            - instance of Azure
	 * @param rgName
	 *            - name of the Azure resource group to be used when creating a
	 *            virtual machine
	 * @param region
	 *            - region to be used when creating a virtual machine
	 * @param registryServerUrl
	 *            - address of the private container registry
	 * @param username
	 *            - user name to connect with to the private container registry
	 * @param acrPd
	 *            - acrPd to connect with to the private container registry
	 * @param azureBean
	 *            Azure bean
	 * @param networkSecurityGroup
	 *            network security group
	 * @param dockerRegistryPort
	 *            docker registry port
	 * @param dockerRegistryName
	 *            docker registry name
	 *  @param dockerVMUserName  docker vm username
	 *  @param dockerVMPd  docker vm pd
	 *  @param subNet     subnet details
	 *  @param vnet       vnet details 
	 *  @param sleepTimeFirstInt   Thread sleep time 
	 * @return an instance of DockerClient
	 * @throws Exception
	 *             exception thrown
	 */
	public static DockerClient fromNewDockerVM(Azure azure, String rgName, Region region, String registryServerUrl,
			String username, String acrPd, AzureBean azureBean, String networkSecurityGroup,
			String dockerRegistryPort, String dockerRegistryName,String dockerVMUserName,String dockerVMPd,
			String subNet,String vnet,int sleepTimeFirstInt) throws Exception {
		// final String dockerVMName = SdkContext.randomResourceName("dockervm", 15);
		// final String publicIPDnsLabel = SdkContext.randomResourceName("pip", 10);
		final String vnetName = SdkContext.randomResourceName("vnet", 24);
		final String frontEndNSGName = SdkContext.randomResourceName("fensg", 24);
		final String networkInterfaceName1 = SdkContext.randomResourceName("nic1", 24);
		final String publicIPAddressLeafDNS1 = SdkContext.randomResourceName("pip1", 24);
		final String vmUserName=dockerVMUserName;
		final String vmPd=dockerVMPd;

		
		// Could not find a Docker environment; presume that there is no local Docker
		// engine running and
		// attempt to configure a Docker engine running inside a new Azure virtual
		// machine
		/****************** Start code for resourceGroup *******************/
		log.debug("Creating a virtual network ...");
		log.debug("sleepTimeFirstInt "+sleepTimeFirstInt);       
		log.debug("Walking through network security groups");
		
		
		Network network = azure.networks().getByResourceGroup(rgName, vnet);
		log.debug("Created a virtual network: " + network.id());
		
		Utils.print(network);
		log.debug("Creating a security group for the front end - allows SSH and HTTP");
		NetworkSecurityGroup frontEndNSG = azure.networkSecurityGroups().getByResourceGroup(rgName,
				networkSecurityGroup);
		log.debug("Created NetworkSecurityGroup ");

		NetworkInterface networkInterface1 = azure.networkInterfaces().define(networkInterfaceName1).withRegion(region)
				.withExistingResourceGroup(rgName).withExistingPrimaryNetwork(network).withSubnet(subNet)
				.withPrimaryPrivateIPAddressDynamic().withNewPrimaryPublicIPAddress(publicIPAddressLeafDNS1)
				.withIPForwarding().withExistingNetworkSecurityGroup(frontEndNSG).create();

		log.debug("Created network interface for the front end");
		log.debug(" Created NetworkInterface ");
		Utils.print(networkInterface1);

		/****************** End code for resourceGroup ********************/
		log.debug("Creating an Azure virtual machine running Docker");
		Date t1 = new Date();
		

		VirtualMachine dockerVM = azure.virtualMachines().define(frontEndNSGName).withRegion(region)
				.withExistingResourceGroup(rgName).withExistingPrimaryNetworkInterface(networkInterface1)
				.withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_16_04_LTS)
				.withRootUsername(vmUserName).withRootPassword(vmPd)
				.withSize(VirtualMachineSizeTypes.STANDARD_D2_V2).create();

		log.debug(azure.publicIPAddresses().list()
				+ " Created VirtualMachine NetworkSecurityGroup "
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().id());
		log.debug(" Created VirtualMachine NetworkSecurityGroup "
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().key());
		log.debug("Walking through network security groups");

		Date t2 = new Date();
		log.debug("Created Azure Virtual Machine: (took " + ((t2.getTime() - t1.getTime()) / 1000)
				+ " seconds) " + dockerVM.id() + " , " + dockerVM.toString());

		// Get the IP of the Docker host
		NicIPConfiguration nicIPConfiguration = dockerVM.getPrimaryNetworkInterface().primaryIPConfiguration();
		PublicIPAddress publicIp = nicIPConfiguration.getPublicIPAddress();
		String dockerHostIP = publicIp.ipAddress();
		String vmName=publicIp.name();
		
		log.debug(dockerVM.computerName()+"dockerHostIP " + dockerHostIP + " vmName " + vmName);
		if (azureBean != null && dockerHostIP != null && !"".equals(dockerHostIP)) {
			log.debug(" Setting dockerHostIP " + dockerHostIP);
			azureBean.setAzureVMIP(dockerHostIP);
			azureBean.setAzureVMName(dockerVM.computerName());
		}

		DockerClient dockerClient = installDocker(dockerHostIP, vmUserName, vmPd, registryServerUrl, username,
				acrPd, dockerRegistryName,sleepTimeFirstInt);
		
		log.debug("dockerHostIP " + dockerHostIP);

		return dockerClient;
	}
	
	public static String createNewAzureVM(Azure azure, String rgName, Region region, 
			  String networkSecurityGroup,String dockerVMUserName,String dockerVMPd,
			  String subNet,String vnet,AzureKubeTransportBean kubeTransportBean) throws Exception {
		    log.debug("createNewAzureVM Start");
			final String vnetName = SdkContext.randomResourceName("vnet", 24);
			final String frontEndNSGName = SdkContext.randomResourceName("fensg", 24);
			final String networkInterfaceName1 = SdkContext.randomResourceName("nic1", 24);
			final String publicIPAddressLeafDNS1 = SdkContext.randomResourceName("pip1", 24);
			final String vmUserName=dockerVMUserName;
			final String vmPd=dockerVMPd;
			String hostIP="";
			log.debug("vmPd "+vmPd);
			// Could not find a Docker environment; presume that there is no local Docker
			// engine running and
			// attempt to configure a Docker engine running inside a new Azure virtual
			// machine
			/****************** Start code for resourceGroup *******************/
			log.debug("Creating a virtual network ...");
			log.debug("Walking through network security groups");
			
			
			Network network = azure.networks().getByResourceGroup(rgName, vnet);
			log.debug("Created a virtual network: " + network.id());
			
			Utils.print(network);
			log.debug("Creating a security group for the front end - allows SSH and HTTP");
			NetworkSecurityGroup frontEndNSG = azure.networkSecurityGroups().getByResourceGroup(rgName,
					networkSecurityGroup);
			log.debug("Created NetworkSecurityGroup ");
	
			NetworkInterface networkInterface1 = azure.networkInterfaces().define(networkInterfaceName1).withRegion(region)
					.withExistingResourceGroup(rgName).withExistingPrimaryNetwork(network).withSubnet(subNet)
					.withPrimaryPrivateIPAddressDynamic().withNewPrimaryPublicIPAddress(publicIPAddressLeafDNS1)
					.withIPForwarding().withExistingNetworkSecurityGroup(frontEndNSG).create();
	
			log.debug("Created network interface for the front end");
			log.debug(" Created NetworkInterface ");
			Utils.print(networkInterface1);
	
			/****************** End code for resourceGroup ********************/
			log.debug("Creating an Azure virtual machine running Docker");
			Date t1 = new Date();
			
	
			VirtualMachine vm = azure.virtualMachines().define(frontEndNSGName).withRegion(region)
					.withExistingResourceGroup(rgName).withExistingPrimaryNetworkInterface(networkInterface1)
					.withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_16_04_LTS)
					.withRootUsername(vmUserName).withRootPassword(vmPd)
					.withSize(VirtualMachineSizeTypes.STANDARD_D2_V2).create();
	
			log.debug(azure.publicIPAddresses().list()
					+ " Created VirtualMachine NetworkSecurityGroup "
					+ vm.getPrimaryNetworkInterface().getNetworkSecurityGroup().id());
			log.debug(" Created VirtualMachine NetworkSecurityGroup "
					+ vm.getPrimaryNetworkInterface().getNetworkSecurityGroup().key());
			log.debug("Walking through network security groups");
	
			Date t2 = new Date();
			log.debug("Created Azure Virtual Machine: (took " + ((t2.getTime() - t1.getTime()) / 1000)
					+ " seconds) " + vm.id() + " , " + vm.toString());
	
			// Get the IP of the Docker host
			NicIPConfiguration nicIPConfiguration = vm.getPrimaryNetworkInterface().primaryIPConfiguration();
			PublicIPAddress publicIp = nicIPConfiguration.getPublicIPAddress();
			hostIP = publicIp.ipAddress();
			String vmName=publicIp.name();
			
			log.debug(vm.computerName()+" HostIP " + hostIP + " vmName " + vmName);
			if (kubeTransportBean != null && hostIP != null && !"".equals(hostIP)) {
				log.debug(" Setting hostIP " + hostIP);
				kubeTransportBean.setAzureVMIP(hostIP);
				kubeTransportBean.setAzureVMName(vm.computerName());
			}
	
			
			log.debug("dockerHostIP " + hostIP);
			log.debug("createNewAzureVM End");
			return hostIP;
	}
	
	public static void  uploadZipVM(AzureKubeTransportBean kubeTransportBean) throws Exception{
		log.debug(" uploadZipVM Start");
		SSHShell sshShell = null;
		InputStream inputStream=null;
		try {
			 inputStream=kubeTransportBean.getSolutionZipStream();	
			 sshShell = SSHShell.open(kubeTransportBean.getAzureVMIP(), 22, kubeTransportBean.getDockerVMUserName(),
					 kubeTransportBean.getDockerVMPd());
			 sshShell.upload(inputStream, "solution.zip", "kubernetesSolution", true,"4095");
		}catch(Exception exception) {
			log.error("Error in uploadZipVM", exception);
			throw exception;
		}
		log.debug(" uploadZipVM End");
		
	}

	/**
	 * Install Docker on a given virtual machine and return a DockerClient.
	 * 
	 * @param dockerHostIP
	 *            - address (IP) of the Docker host machine
	 * @param vmUserName
	 *            - vm user name
	 * @param vmPd
	 *            - vmPd to connect with to the Docker host machine
	 * @param tbean
	 *            transport bean
	 * 
	 */
	public static void protoFileVM(String dockerHostIP, String vmUserName, String vmPd,TransportBean tbean)
			throws Exception{
		SSHShell sshShell = null;
		log.debug("protoFileVM Start");
		try{
		sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
		String createFolderScript = sshShell.executeCommand("sudo mkdir -p "+tbean.getNginxMapFolder()+" ", true,true);
		log.debug("createFolderScript  " + createFolderScript);
		Iterator protoItr = tbean.getProtoMap().entrySet().iterator();
	    while (protoItr.hasNext()) {
	        Map.Entry protoPair = (Map.Entry)protoItr.next();
	        if(protoPair!=null && protoPair.getKey()!=null && protoPair.getValue()!=null){
	        	log.debug(protoPair.getKey() + " keyAndValue " + protoPair.getValue());
	        	String protoFilePathName=(String)protoPair.getKey();
	        	String protoDetails=(String)protoPair.getValue();
	        	int index = protoFilePathName.lastIndexOf("/");
	        	String protoFileName=protoFilePathName.substring(index+1);
	        	String protoUriFolder= protoFilePathName.substring(0,index);
	        	String copyFolderName=tbean.getNginxMapFolder()+"/"+protoUriFolder;
	        	log.debug("protoFileName "+protoFileName);
	        	log.debug("protoUriFolder "+protoUriFolder);
	        	log.debug("copyFolderName "+copyFolderName);
	        	createFolderScript = sshShell.executeCommand("sudo mkdir -p "+copyFolderName+" ", true,true);
	    		log.debug("createFolderScript folder " + createFolderScript);
	    		
	    		sshShell.upload(new ByteArrayInputStream(protoDetails.getBytes()), protoFileName,
	        			"AzureDataFiles", true, "4095");
	        	log.debug("File uploaded in AzureDataFiles folder " );
	        	String copyScript = sshShell.executeCommand("sudo cp -R "+tbean.getAzureDataFiles()+"/"+protoFileName+" "+ copyFolderName, true,true);
	        	log.debug("copy file in folder finish"+copyScript);
	        }
	    }
		}catch(Exception e){
			log.error("protoFileVM failed", e);
			throw e;
		}
		log.debug("protoFileVM End");
	}
	public static String deploymentCompositeImageVM(String dockerHostIP, String vmUserName, String vmPd,
			String registryServerUrl, String username, String acrPd, String repositoryName,
			String finalContainerName, int imageCount, String portNumber,String probeNexusEndPoint,int sleepTimeFirstInt,TransportBean tbean) {
		log.debug("deploymentCompositeImageVM Start");
		log.debug("dockerHostIP " + dockerHostIP);
		log.debug("repositoryName " + repositoryName);
		log.debug("finalContainerName " + finalContainerName);
		log.debug("imageCount " + imageCount);
		log.debug("portNumber " + portNumber);
		log.debug("probeNexusEndPoint " + probeNexusEndPoint);
		String portNumberString = portNumber;
		log.debug("portNumberString " + portNumberString);
		log.debug("sleepTimeFirstInt " + sleepTimeFirstInt);
		SSHShell sshShell = null;
		try {

			String PULL_IMAGE = "" + "docker login --username=" + username + " --password=" + acrPd + " "
					+ registryServerUrl + " \n" + "docker pull " + repositoryName + " \n";
			log.debug("start deploymentImageVM PULL_IMAGE  " + PULL_IMAGE);

			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			sshShell.upload(new ByteArrayInputStream(PULL_IMAGE.getBytes()), "PULL_IMAGE_" + imageCount + ".sh",
					".azuredocker", true, "4095");
			log.debug("start deploymentImageVM  ");

			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			String output2 = sshShell.executeCommand("bash -c ~/.azuredocker/PULL_IMAGE_" + imageCount + ".sh", true,
					true);
			log.debug("start deploymentImageVM output2  " + output2);
			 Thread.sleep(sleepTimeFirstInt);
			log.debug(" start deploymentImageVM ");
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			String RUN_IMAGE="";
			if(finalContainerName!=null && finalContainerName.trim().equalsIgnoreCase(AzureClientConstants.PROBE_CONTAINER_NAME)){
				log.debug("Probe Condition");
				RUN_IMAGE = "" + "docker run --name " + finalContainerName + " -itd -p 0.0.0.0:" + portNumberString
						+ "  -e NEXUSENDPOINTURL='"+probeNexusEndPoint+"' " + repositoryName + " \n";
			}else if(finalContainerName!=null && finalContainerName.equalsIgnoreCase(AzureClientConstants.NGINX_CONTAINER)){
				log.debug("nginx Condition");
				RUN_IMAGE = "" + "docker run --name "+finalContainerName+" -v "+tbean.getNginxMapFolder()+":"+tbean.getNginxWebFolder()+":ro  -d -p 0.0.0.0:" + portNumberString
						+ "  " + repositoryName + " \n";
			}else{
				log.debug("Other Condition");
				RUN_IMAGE = "" + "docker run --name " + finalContainerName + " -d -p 0.0.0.0:" + portNumberString
						+ "  " + repositoryName + " \n";
				
			}
		    
			log.debug("RUN_IMAGE " + RUN_IMAGE);

			sshShell.upload(new ByteArrayInputStream(RUN_IMAGE.getBytes()), "RUN_DOCKER_IMAGE_" + imageCount + ".sh",
					".azuredocker", true, "4095");
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);

			String output3 = sshShell.executeCommand("bash -c ~/.azuredocker/RUN_DOCKER_IMAGE_" + imageCount + ".sh",
					true, true);
			log.debug("output3" + output3);
			Thread.sleep(sleepTimeFirstInt);
		} catch (JSchException jSchException) {
			log.error("deploymentCompositeImageVM failed", jSchException);
		} catch (IOException ioException) {
			log.error("deploymentCompositeImageVM failed", ioException);
		} catch (Exception exception) {
			log.error("deploymentCompositeImageVM failed", exception);
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}

		log.debug("deploymentCompositeImageVM End");
		return "success";
	}

	public static String deploymentImageVM(String dockerHostIP, String vmUserName, String vmPd,
			String registryServerUrl, String username, String acrPd, String repositoryName,
			String portNumberString,int sleepTimeFirstInt) {
		log.debug("dockerHostIP " + dockerHostIP);
		log.debug("registryServerUrl " + registryServerUrl);
		log.debug("repositoryName " + repositoryName);
		log.debug("portNumberString " + portNumberString);
		log.debug("sleepTimeFirstInt " + sleepTimeFirstInt);
		log.debug("start deploymentImageVM ");
		SSHShell sshShell = null;
		try {

			String PULL_IMAGE = "" + "docker login --username=" + username + " --password=" + acrPd + " "
					+ registryServerUrl + " \n" + "docker pull " + repositoryName + " \n";
			log.debug(" start deploymentImageVM PULL_IMAGE " + PULL_IMAGE);

			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			sshShell.upload(new ByteArrayInputStream(PULL_IMAGE.getBytes()), "PULL_IMAGE.sh", ".azuredocker", true,
					"4095");
			log.debug(" start deploymentImageVM  ");

			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			String output2 = sshShell.executeCommand("bash -c ~/.azuredocker/PULL_IMAGE.sh", true, true);
			log.debug("deploymentImageVM output2 " + output2);
			Thread.sleep(sleepTimeFirstInt);
			log.debug("start deploymentImageVM ");
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			String RUN_IMAGE = "" + "docker run -d -p 0.0.0.0:"+portNumberString+" "+ repositoryName + " \n";
			log.debug("RUN_IMAGE Complete ");

			sshShell.upload(new ByteArrayInputStream(RUN_IMAGE.getBytes()), "RUN_DOCKER_IMAGE.sh", ".azuredocker", true,
					"4095");
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);

			String output3 = sshShell.executeCommand("bash -c ~/.azuredocker/RUN_DOCKER_IMAGE.sh", true, true);
			log.debug("output3: " + output3);

		} catch (JSchException jSchException) {
			log.error("deploymentImageVM failed", jSchException);
		} catch (IOException ioException) {
			log.error("deploymentImageVM failed", ioException);
		} catch (Exception exception) {
			log.error("deploymentImageVM failed", exception);
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		log.debug(" deploymentImageVM End");
		return "sucess";
	}
	
	

	public static DockerClient installDocker(String dockerHostIP, String vmUserName, String vmPd,
			String registryServerUrl, String username, String acrPd, String dockerRegistryNameVal, int sleepTimeFirstInt) {
		String keyPemContent = ""; // it stores the content of the key.pem certificate file
		String certPemContent = ""; // it stores the content of the cert.pem certificate file
		String caPemContent = ""; // it stores the content of the ca.pem certificate file
		boolean dockerHostTlsEnabled = false;
		String dockerHostUrl = "tcp://" + dockerHostIP + ":80";
		SSHShell sshShell = null;
		String dockerConfig_diabled = "";
		String dockerRegistryName = dockerRegistryNameVal;

		try {
			log.debug("Copy Docker setup scripts to remote host: " + dockerHostIP);
			log.debug("Copy Docker setup scripts to remote host: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);

			sshShell.upload(new ByteArrayInputStream(INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.getBytes()),
					"INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.sh", ".azuredocker", true, "4095");

			dockerConfig_diabled = "" + "[Service]\n" + "ExecStart=\n"
					+ "ExecStart=/usr/bin/dockerd --tls=false -H tcp://0.0.0.0:80 -H unix:///var/run/docker.sock --insecure-registry "
					+ dockerRegistryName + " \n";

			sshShell.upload(new ByteArrayInputStream(dockerConfig_diabled.getBytes()), "dockerd_notls.config",
					".azuredocker", true, "4095");
			log.debug("dockerConfig_diabled " + dockerConfig_diabled);
			sshShell.upload(new ByteArrayInputStream(CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.getBytes()),
					"CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.sh", ".azuredocker", true, "4095");
			log.debug("dockerConfig_diabled " + dockerConfig_diabled);
		} catch (JSchException jSchException) {
			log.error("installDocker failed", jSchException);
		} catch (IOException ioException) {
			log.error("installDocker failed", ioException);
		} catch (Exception exception) {
			log.error("installDocker failed", exception);
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		try {
			log.debug("Trying to install Docker host at: " + dockerHostIP);
			log.debug("Trying to install Docker dockerHostIP at: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);

			String output = sshShell
					.executeCommand("bash -c ~/.azuredocker/INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.sh", true, true);
			Thread.sleep(sleepTimeFirstInt);
			log.debug(output);
			log.debug("output " + output);
		} catch (JSchException jSchException) {
			log.error("installDocker failed ", jSchException);
		} catch (IOException ioException) {
			log.error("installDocker failed", ioException);
		} catch (Exception exception) {
			log.error("installDocker failed", exception);
		} finally {
			if (sshShell != null) {
				sshShell.close();
			}
		}
		String dockerHostPort = "80";
		try {
			log.debug("Trying to setup Docker config: " + dockerHostIP);
			log.debug("Trying to setup Docker config: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPd);
			log.debug("Enter in ssh shell ");
			// // Setup Docker daemon to allow connection from any Docker clients
			String output = sshShell
					.executeCommand("bash -c ~/.azuredocker/CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.sh", true, true);
			Thread.sleep(sleepTimeFirstInt);
			
			log.debug(output);
			log.debug("output " + output);

			dockerHostTlsEnabled = false;

			log.debug("dockerHostUrl ");
		} catch (JSchException jSchException) {
			log.error("installDocker failed", jSchException);
		} catch (IOException ioException) {
			log.error("installDocker failed", ioException);
		} catch (Exception exception) {
			log.error("installDocker failed", exception);
		}  finally {
			if (sshShell != null) {
				sshShell.close();
			}
		}
		dockerHostUrl = "tcp://" + dockerHostIP + ":" + dockerHostPort;
		dockerHostTlsEnabled = false;
		log.debug(dockerHostUrl + "dockerHostTlsEnabled "+ dockerHostTlsEnabled);
		DockerClientConfig dockerClientConfig;
		if (dockerHostTlsEnabled) {
			log.debug("dockerHostTlsEnabled " + dockerHostTlsEnabled);
			dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, acrPd,
					caPemContent, keyPemContent, certPemContent);
		} else {
			log.debug("dockerHostTlsEnabled " + dockerHostTlsEnabled);
			dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, acrPd);
		}
		log.debug("dockerClientConfig " + dockerClientConfig);
		DockerClient dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
		try {
			Thread.sleep(sleepTimeFirstInt);
		} catch (Exception e) {
			log.error("sleep  failed", e);
		}
		return dockerClient;
	}

	/**
	 * Installs Docker Engine and tools and adds current user to the docker group.
	 */
	public static final String INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS = ""
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
			+ "echo Running: sudo usermod -aG docker $USER \n" + "sudo usermod -aG docker $USER \n";

	/**
	 * Linux bash script that creates the TLS certificates for a secured Docker
	 * connection.
	 */
	public static final String CREATE_OPENSSL_TLS_CERTS_FOR_UBUNTU = ""
			+ "echo Running: \"if [ ! -d ~/.azuredocker/tls ]; then rm -f -r ~/.azuredocker/tls ; fi\" \n"
			+ "if [ ! -d ~/.azuredocker/tls ]; then rm -f -r ~/.azuredocker/tls ; fi \n"
			+ "echo Running: mkdir -p ~/.azuredocker/tls \n" + "mkdir -p ~/.azuredocker/tls \n"
			+ "echo Running: cd ~/.azuredocker/tls \n" + "cd ~/.azuredocker/tls \n"
			// Generate CA certificate
			+ "echo Running: openssl genrsa -passout pass:$CERT_CA_PWD_PARAM$ -aes256 -out ca-key.pem 2048 \n"
			+ "openssl genrsa -passout pass:$CERT_CA_PWD_PARAM$ -aes256 -out ca-key.pem 2048 \n"
			// Generate Server certificates
			+ "echo Running: openssl req -passin pass:$CERT_CA_PWD_PARAM$ -subj '/CN=Docker Host CA/C=US' -new -x509 -days 365 -key ca-key.pem -sha256 -out ca.pem \n"
			+ "openssl req -passin pass:$CERT_CA_PWD_PARAM$ -subj '/CN=Docker Host CA/C=US' -new -x509 -days 365 -key ca-key.pem -sha256 -out ca.pem \n"
			+ "echo Running: openssl genrsa -out server-key.pem 2048 \n" + "openssl genrsa -out server-key.pem 2048 \n"
			+ "echo Running: openssl req -subj '/CN=HOST_IP' -sha256 -new -key server-key.pem -out server.csr \n"
			+ "openssl req -subj '/CN=HOST_IP' -sha256 -new -key server-key.pem -out server.csr \n"
			+ "echo Running: \"echo subjectAltName = DNS:HOST_IP IP:127.0.0.1 > extfile.cnf \" \n"
			+ "echo subjectAltName = DNS:HOST_IP IP:127.0.0.1 > extfile.cnf \n"
			+ "echo Running: openssl x509 -req -passin pass:$CERT_CA_PWD_PARAM$ -days 365 -sha256 -in server.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out server.pem -extfile extfile.cnf \n"
			+ "openssl x509 -req -passin pass:$CERT_CA_PWD_PARAM$ -days 365 -sha256 -in server.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out server.pem -extfile extfile.cnf \n"
			// Generate Client certificates
			+ "echo Running: openssl genrsa -passout pass:$CERT_CA_PWD_PARAM$ -out key.pem \n"
			+ "openssl genrsa -passout pass:$CERT_CA_PWD_PARAM$ -out key.pem \n"
			+ "echo Running: openssl req -passin pass:$CERT_CA_PWD_PARAM$ -subj '/CN=client' -new -key key.pem -out client.csr \n"
			+ "openssl req -passin pass:$CERT_CA_PWD_PARAM$ -subj '/CN=client' -new -key key.pem -out client.csr \n"
			+ "echo Running: \"echo extendedKeyUsage = clientAuth,serverAuth > extfile.cnf \" \n"
			+ "echo extendedKeyUsage = clientAuth,serverAuth > extfile.cnf \n"
			+ "echo Running: openssl x509 -req -passin pass:$CERT_CA_PWD_PARAM$ -days 365 -sha256 -in client.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out cert.pem -extfile extfile.cnf \n"
			+ "openssl x509 -req -passin pass:$CERT_CA_PWD_PARAM$ -days 365 -sha256 -in client.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out cert.pem -extfile extfile.cnf \n"
			+ "echo Running: cd ~ \n" + "cd ~ \n";

	/**
	 * Bash script that sets up the TLS certificates to be used in a secured Docker
	 * configuration file; must be run on the Docker dockerHostUrl after the VM is
	 * provisioned.
	 */
	public static final String INSTALL_DOCKER_TLS_CERTS_FOR_UBUNTU = ""
			+ "echo \"if [ ! -d /etc/docker/tls ]; then sudo mkdir -p /etc/docker/tls ; fi\" \n"
			+ "if [ ! -d /etc/docker/tls ]; then sudo mkdir -p /etc/docker/tls ; fi \n"
			+ "echo sudo cp -f ~/.azuredocker/tls/ca.pem /etc/docker/tls/ca.pem \n"
			+ "sudo cp -f ~/.azuredocker/tls/ca.pem /etc/docker/tls/ca.pem \n"
			+ "echo sudo cp -f ~/.azuredocker/tls/server.pem /etc/docker/tls/server.pem \n"
			+ "sudo cp -f ~/.azuredocker/tls/server.pem /etc/docker/tls/server.pem \n"
			+ "echo sudo cp -f ~/.azuredocker/tls/server-key.pem /etc/docker/tls/server-key.pem \n"
			+ "sudo cp -f ~/.azuredocker/tls/server-key.pem /etc/docker/tls/server-key.pem \n"
			+ "echo sudo chmod -R 755 /etc/docker \n" + "sudo chmod -R 755 /etc/docker \n";

	/**
	 * Docker daemon config file allowing connections from any Docker client.
	 */
	public static final String DEFAULT_DOCKERD_CONFIG_TLS_ENABLED = "" + "[Service]\n" + "ExecStart=\n"
			+ "ExecStart=/usr/bin/dockerd --tlsverify --tlscacert=/etc/docker/tls/ca.pem --tlscert=/etc/docker/tls/server.pem --tlskey=/etc/docker/tls/server-key.pem -H tcp://0.0.0.0:2376 -H unix:///var/run/docker.sock\n";

	/**
	 * Bash script that creates a default TLS secured Docker configuration file;
	 * must be run on the Docker dockerHostUrl after the VM is provisioned.
	 */
	public static final String CREATE_DEFAULT_DOCKERD_OPTS_TLS_ENABLED = ""
			+ "echo Running: sudo service docker stop \n" + "sudo service docker stop \n"
			+ "echo \"if [ ! -d /etc/systemd/system/docker.service.d ]; then sudo mkdir -p /etc/systemd/system/docker.service.d ; fi\" \n"
			+ "if [ ! -d /etc/systemd/system/docker.service.d ]; then sudo mkdir -p /etc/systemd/system/docker.service.d ; fi \n"
			+ "echo sudo cp -f ~/.azuredocker/dockerd_tls.config /etc/systemd/system/docker.service.d/custom.conf \n"
			+ "sudo cp -f ~/.azuredocker/dockerd_tls.config /etc/systemd/system/docker.service.d/custom.conf \n"
			+ "echo Running: sudo systemctl daemon-reload \n" + "sudo systemctl daemon-reload \n"
			+ "echo Running: sudo service docker start \n" + "sudo service docker start \n";

	/**
	 * Docker daemon config file allowing connections from any Docker client.
	 */
	
	/**
	 * Bash script that creates a default unsecured Docker configuration file; must
	 * be run on the Docker dockerHostUrl after the VM is provisioned.
	 */
	public static final String CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED = ""
			+ "echo Running: sudo service docker stop\n" + "sudo service docker stop\n"
			+ "echo \"if [ ! -d /etc/systemd/system/docker.service.d ]; then sudo mkdir -p /etc/systemd/system/docker.service.d ; fi\" \n"
			+ "if [ ! -d /etc/systemd/system/docker.service.d ]; then sudo mkdir -p /etc/systemd/system/docker.service.d ; fi \n"
			+ "echo sudo cp -f ~/.azuredocker/dockerd_notls.config /etc/systemd/system/docker.service.d/custom.conf \n"
			+ "sudo cp -f ~/.azuredocker/dockerd_notls.config /etc/systemd/system/docker.service.d/custom.conf \n"
			+ "echo Running: sudo systemctl daemon-reload \n" + "sudo systemctl daemon-reload \n"
			+ "echo Running: sudo service docker start \n" + "sudo service docker start \n";
}
