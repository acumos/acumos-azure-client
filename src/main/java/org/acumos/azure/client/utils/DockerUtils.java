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
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Date;

import javax.net.ssl.SSLContext;

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
	 * @param password
	 *            - password to connect with to the private container registry
	 * @return an instance of DockerClient
	 * @throws Exception
	 *             exception thrown
	 */
	public static DockerClient createDockerClient(Azure azure, String rgName, Region region, String registryServerUrl,
			String username, String password, String localEnvDockerHost, String localEnvDockerCertPath,
			AzureBean azureBean, String networkSecurityGroup, String dockerRegistryPort) throws Exception {
		// final String envDockerHost = System.getenv("DOCKER_HOST");
		final String envDockerHost = localEnvDockerHost;
		final String envDockerCertPath = System.getenv("DOCKER_CERT_PATH");
		String dockerHostUrl;
		DockerClient dockerClient;
		dockerPort = dockerRegistryPort;
		log.info("====createDockerClient====networkSecurityGroup======" + networkSecurityGroup
				+ "====dockerRegistryPort=======" + dockerRegistryPort + "===dockerPort=" + dockerPort);
		if (envDockerHost == null || envDockerHost.isEmpty()) {
			// Could not find a Docker environment; presume that there is no local Docker
			// engine running and
			// attempt to configure a Docker engine running inside a new Azure virtual
			// machine
			dockerClient = fromNewDockerVM(azure, rgName, region, registryServerUrl, username, password, azureBean,
					networkSecurityGroup, dockerRegistryPort);
		} else {
			dockerHostUrl = envDockerHost;
			System.out.println("Using local settings to connect to a Docker service: " + dockerHostUrl);
			log.info("Using local settings to connect to a Docker service: " + dockerHostUrl);

			DockerClientConfig dockerClientConfig;
			if (envDockerCertPath == null || envDockerCertPath.isEmpty()) {
				dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, password);
			} else {
				String caPemPath = envDockerCertPath + File.separator + "ca.pem";
				String keyPemPath = envDockerCertPath + File.separator + "key.pem";

				String certPemPath = envDockerCertPath + File.separator + "cert.pem";
				String keyPemContent = new String(Files.readAllBytes(Paths.get(keyPemPath)));
				String certPemContent = new String(Files.readAllBytes(Paths.get(certPemPath)));
				String caPemContent = new String(Files.readAllBytes(Paths.get(caPemPath)));

				dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, password,
						caPemContent, keyPemContent, certPemContent);
			}

			dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
			System.out.println("List Docker host info");
			/*System.out.println("\tFound Docker version: " + dockerClient.versionCmd().exec().toString());
			System.out.println("\tFound Docker info: " + dockerClient.infoCmd().exec().toString());
			log.info("List Docker host info");
			log.info("\tFound Docker version: " + dockerClient.versionCmd().exec().toString());
			log.info("\tFound Docker info: " + dockerClient.infoCmd().exec().toString());*/
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
	 * @param password
	 *            - password to connect with to the private container registry
	 * @param caPemContent
	 *            - content of the ca.pem certificate file
	 * @param keyPemContent
	 *            - content of the key.pem certificate file
	 * @param certPemContent
	 *            - content of the cert.pem certificate file
	 * @return an instance of DockerClient configuration
	 */
	public static DockerClientConfig createDockerClientConfig(String host, String registryServerUrl, String username,
			String password, String caPemContent, String keyPemContent, String certPemContent) {
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(true)
				.withCustomSslConfig(new DockerSSLConfig(caPemContent, keyPemContent, certPemContent))
				.withRegistryUrl(registryServerUrl).withRegistryUsername(username).withRegistryPassword(password)
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
	 * @param password
	 *            - password to connect with to the private container registry
	 * @return an instance of DockerClient configuration
	 */
	public static DockerClientConfig createDockerClientConfig(String host, String registryServerUrl, String username,
			String password) {
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(false)
				.withRegistryUrl(registryServerUrl).withRegistryUsername(username).withRegistryPassword(password)
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
	 * @param password
	 *            - password to connect with to the private container registry
	 * @return an instance of DockerClient
	 * @throws Exception
	 *             exception thrown
	 */
	public static DockerClient fromNewDockerVM(Azure azure, String rgName, Region region, String registryServerUrl,
			String username, String password, AzureBean azureBean, String networkSecurityGroup,
			String dockerRegistryPort) throws Exception {
		final String dockerVMName = SdkContext.randomResourceName("dockervm", 15);
		final String publicIPDnsLabel = SdkContext.randomResourceName("pip", 10);
		final String vnetName = SdkContext.randomResourceName("vnet", 24);
		final String frontEndNSGName = SdkContext.randomResourceName("fensg", 24);
		final String networkInterfaceName1 = SdkContext.randomResourceName("nic1", 24);
		final String publicIPAddressLeafDNS1 = SdkContext.randomResourceName("pip1", 24);
		final String vmUserName = "dockerUser";
		final String vmPassword = "12NewPA$$w0rd!";

		log.info("========frontEndNSGName======" + frontEndNSGName + "====vnetName=======" + vnetName);
		log.info("========networkSecurityGroup======" + networkSecurityGroup + "====dockerRegistryPort======="
				+ dockerRegistryPort);
		// Could not find a Docker environment; presume that there is no local Docker
		// engine running and
		// attempt to configure a Docker engine running inside a new Azure virtual
		// machine
		/****************** Start code for resourceGroup *******************/
		log.info("Creating a virtual network ...");

		System.out.println("Walking through network security groups");
		/*
		 * List<NetworkSecurityGroup> networkSecurityGroups =
		 * azure.networkSecurityGroups().list(); for (NetworkSecurityGroup
		 * networkSecurityGroup: networkSecurityGroups) {
		 * Utils.print(networkSecurityGroup); }
		 */
		String subNet = "Cognita-OAM-vsubnet";
		/*
		 * Network network = azure.networks().define(vnetName) .withRegion(region)
		 * .withNewResourceGroup(rgName) .withAddressSpace("135.197.0.0/16")
		 * .defineSubnet(subNet) .withAddressPrefix("135.197.0.0/16") .attach()
		 * .create();
		 */
		Network network = azure.networks().getByResourceGroup(rgName, "Cognita-OAM-vnet");
		// azure.networks().getr
		log.info("Created a virtual network: " + network.id());
		log.info("========Network created =======" + vnetName + "======subNet=====" + subNet);
		Utils.print(network);
		log.info("Creating a security group for the front end - allows SSH and HTTP");
		NetworkSecurityGroup frontEndNSG = azure.networkSecurityGroups().getByResourceGroup(rgName,
				networkSecurityGroup);
		log.info("====================Created NetworkSecurityGroup============");

		NetworkInterface networkInterface1 = azure.networkInterfaces().define(networkInterfaceName1).withRegion(region)
				.withExistingResourceGroup(rgName).withExistingPrimaryNetwork(network).withSubnet(subNet)
				.withPrimaryPrivateIPAddressDynamic().withNewPrimaryPublicIPAddress(publicIPAddressLeafDNS1)
				.withIPForwarding().withExistingNetworkSecurityGroup(frontEndNSG).create();

		log.info("Created network interface for the front end");
		log.info("====================Created NetworkInterface============");
		Utils.print(networkInterface1);

		/****************** End code for resourceGroup ********************/
		log.info("Creating an Azure virtual machine running Docker");
		Date t1 = new Date();
		/*
		 * VirtualMachine dockerVM = azure.virtualMachines().define(dockerVMName)
		 * .withRegion(region) .withExistingResourceGroup(rgName)
		 * .withNewPrimaryNetwork("10.0.0.0/28") .withPrimaryPrivateIPAddressDynamic()
		 * .withNewPrimaryPublicIPAddress(publicIPDnsLabel)
		 * .withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_16_04_LTS)
		 * .withRootUsername(vmUserName) .withRootPassword(vmPassword)
		 * .withSize(VirtualMachineSizeTypes.STANDARD_D2_V2) .create();
		 */

		VirtualMachine dockerVM = azure.virtualMachines().define(frontEndNSGName).withRegion(region)
				.withExistingResourceGroup(rgName).withExistingPrimaryNetworkInterface(networkInterface1)
				.withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_16_04_LTS)
				.withRootUsername(vmUserName).withRootPassword(vmPassword)
				.withSize(VirtualMachineSizeTypes.STANDARD_D2_V2).create();

		log.info(azure.publicIPAddresses().list()
				+ "====================Created VirtualMachine====1===NetworkSecurityGroup====="
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().id());
		log.info("====================Created VirtualMachine===2====NetworkSecurityGroup====="
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().key());
		log.info("====================Created VirtualMachine====3===NetworkSecurityGroup====="
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().name());
		log.info("====================Created VirtualMachine=====4==NetworkSecurityGroup====="
				+ dockerVM.getPrimaryNetworkInterface().getNetworkSecurityGroup().resourceGroupName());
		log.info("Walking through network security groups");

		Date t2 = new Date();
		System.out.println("Created Azure Virtual Machine: (took " + ((t2.getTime() - t1.getTime()) / 1000)
				+ " seconds) " + dockerVM.id() + " , " + dockerVM.toString());

		// Get the IP of the Docker host
		NicIPConfiguration nicIPConfiguration = dockerVM.getPrimaryNetworkInterface().primaryIPConfiguration();
		PublicIPAddress publicIp = nicIPConfiguration.getPublicIPAddress();
		String dockerHostIP = publicIp.ipAddress();
		log.info("===== dockerHostIP=====" + dockerHostIP + "========azureBean========" + azureBean);
		if (azureBean != null && dockerHostIP != null && !"".equals(dockerHostIP)) {
			log.info("=====Setting dockerHostIP=====" + dockerHostIP);
			azureBean.setAzureVMIP(dockerHostIP);
		}

		DockerClient dockerClient = installDocker(dockerHostIP, vmUserName, vmPassword, registryServerUrl, username,
				password);
		/*System.out.println("List Docker host info");
		System.out.println("\tFound Docker version: " + dockerClient.versionCmd().exec().toString());
		System.out.println("\tFound Docker info: " + dockerClient.infoCmd().exec().toString());
		log.info("List Docker host info");
		log.info("\tFound Docker version: " + dockerClient.versionCmd().exec().toString());
		log.info("\tFound Docker info: " + dockerClient.infoCmd().exec().toString());*/
		log.info("dockerHostIP=====" + dockerHostIP);

		return dockerClient;
	}

	/**
	 * Install Docker on a given virtual machine and return a DockerClient.
	 * 
	 * @param dockerHostIP
	 *            - address (IP) of the Docker host machine
	 * @param vmUserName
	 *            - user name to connect with to the Docker host machine
	 * @param vmPassword
	 *            - password to connect with to the Docker host machine
	 * @param registryServerUrl
	 *            - address of the private container registry
	 * @param username
	 *            - user name to connect with to the private container registry
	 * @param password
	 *            - password to connect with to the private container registry
	 * @return an instance of DockerClient
	 */
	public static DockerClient installDocker(String dockerHostIP, String vmUserName, String vmPassword,
			String registryServerUrl, String username, String password) {
		String keyPemContent = ""; // it stores the content of the key.pem certificate file
		String certPemContent = ""; // it stores the content of the cert.pem certificate file
		String caPemContent = ""; // it stores the content of the ca.pem certificate file
		boolean dockerHostTlsEnabled = false;
		String dockerHostUrl = "tcp://" + dockerHostIP + ":80";
		SSHShell sshShell = null;

		try {
			System.out.println("Copy Docker setup scripts to remote host: " + dockerHostIP);
			log.info("Copy Docker setup scripts to remote host: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPassword);

			sshShell.upload(new ByteArrayInputStream(INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.getBytes()),
					"INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.sh", ".azuredocker", true, "4095");

			/*
			 * sshShell.upload(new
			 * ByteArrayInputStream(CREATE_OPENSSL_TLS_CERTS_FOR_UBUNTU.replaceAll(
			 * "HOST_IP", dockerHostIP).getBytes()),
			 * "CREATE_OPENSSL_TLS_CERTS_FOR_UBUNTU.sh", ".azuredocker", true, "4095");
			 * sshShell.upload(new
			 * ByteArrayInputStream(INSTALL_DOCKER_TLS_CERTS_FOR_UBUNTU.getBytes()),
			 * "INSTALL_DOCKER_TLS_CERTS_FOR_UBUNTU.sh", ".azuredocker", true, "4095");
			 * sshShell.upload(new
			 * ByteArrayInputStream(DEFAULT_DOCKERD_CONFIG_TLS_ENABLED.getBytes()),
			 * "dockerd_tls.config", ".azuredocker", true, "4095"); sshShell.upload(new
			 * ByteArrayInputStream(CREATE_DEFAULT_DOCKERD_OPTS_TLS_ENABLED.getBytes()),
			 * "CREATE_DEFAULT_DOCKERD_OPTS_TLS_ENABLED.sh", ".azuredocker", true, "4095");
			 */
			sshShell.upload(new ByteArrayInputStream(DEFAULT_DOCKERD_CONFIG_TLS_DISABLED.getBytes()),
					"dockerd_notls.config", ".azuredocker", true, "4095");
			sshShell.upload(new ByteArrayInputStream(CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.getBytes()),
					"CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.sh", ".azuredocker", true, "4095");
		} catch (JSchException jSchException) {
			System.out.println(jSchException.getMessage());
			log.error(jSchException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
			log.error(ioException.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			log.error(exception.getMessage());
		} finally {
			if (sshShell != null) {
				sshShell.close();
				sshShell = null;
			}
		}
		try {
			System.out.println("Trying to install Docker host at: " + dockerHostIP);
			log.info("Trying to install Docker dockerHostIP at: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPassword);

			String output = sshShell
					.executeCommand("bash -c ~/.azuredocker/INSTALL_DOCKER_FOR_UBUNTU_SERVER_16_04_LTS.sh", true, true);
			System.out.println(output);
			log.info("====output=======: " + output);
		} catch (JSchException jSchException) {
			System.out.println(jSchException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			if (sshShell != null) {
				sshShell.close();
			}
		}

		/*
		 * try { System.out.println("Trying to create OPENSSL certificates"); sshShell =
		 * SSHShell.open(dockerHostIP, 22, vmUserName, vmPassword);
		 * 
		 * String output = sshShell.
		 * executeCommand("bash -c ~/.azuredocker/CREATE_OPENSSL_TLS_CERTS_FOR_UBUNTU.sh"
		 * , true, true); System.out.println(output); } catch (JSchException
		 * jSchException) { System.out.println(jSchException.getMessage()); } catch
		 * (IOException ioException) { System.out.println(ioException.getMessage()); }
		 * catch (Exception exception) { System.out.println(exception.getMessage()); }
		 * finally { if (sshShell != null) { sshShell.close(); } }
		 * 
		 * try { System.out.println("Trying to install TLS certificates"); sshShell =
		 * SSHShell.open(dockerHostIP, 22, vmUserName, vmPassword);
		 * 
		 * String output = sshShell.
		 * executeCommand("bash -c ~/.azuredocker/INSTALL_DOCKER_TLS_CERTS_FOR_UBUNTU.sh"
		 * , true, true); System.out.println(output);
		 * System.out.println("Download Docker client TLS certificates from: " +
		 * dockerHostIP); keyPemContent = sshShell.download("key.pem",
		 * ".azuredocker/tls", true); certPemContent = sshShell.download("cert.pem",
		 * ".azuredocker/tls", true); caPemContent = sshShell.download("ca.pem",
		 * ".azuredocker/tls", true); } catch (JSchException jSchException) {
		 * System.out.println(jSchException.getMessage()); } catch (IOException
		 * ioException) { System.out.println(ioException.getMessage()); } catch
		 * (Exception exception) { System.out.println(exception.getMessage()); } finally
		 * { if (sshShell != null) { sshShell.close(); } }
		 */
		String dockerHostPort = "80";
		try {
			System.out.println("Trying to setup Docker config: " + dockerHostIP);
			log.info("Trying to setup Docker config: " + dockerHostIP);
			sshShell = SSHShell.open(dockerHostIP, 22, vmUserName, vmPassword);
			log.info("====sshShell==========Enter==================================: ");
			// // Setup Docker daemon to allow connection from any Docker clients
			String output = sshShell
					.executeCommand("bash -c ~/.azuredocker/CREATE_DEFAULT_DOCKERD_OPTS_TLS_DISABLED.sh", true, true);
			System.out.println(output);
			log.info("====output==========1==================================: "+output);
			
			dockerHostTlsEnabled = false;

			// Setup Docker daemon to allow connection from authorized Docker clients only
			// String output = sshShell.executeCommand("bash -c
			// ~/.azuredocker/CREATE_DEFAULT_DOCKERD_OPTS_TLS_ENABLED.sh", true, true);
			// System.out.println(output);
			// String dockerHostPort = "2376"; // Default Docker port when secured
			// connection is enabled
			// dockerHostTlsEnabled = true;

			
			log.info("====dockerHostUrl============================================: "+dockerHostUrl);
		} catch (JSchException jSchException) {
			System.out.println(jSchException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			if (sshShell != null) {
				sshShell.close();
			}
		}
		dockerHostUrl = "tcp://" + dockerHostIP + ":" + dockerHostPort;
		dockerHostTlsEnabled = false;
		log.info(dockerHostUrl+"====dockerHostTlsEnabled============================================: "+dockerHostTlsEnabled);
		DockerClientConfig dockerClientConfig;
		if (dockerHostTlsEnabled) {
			log.info("====dockerHostTlsEnabled============2================================: "+dockerHostTlsEnabled);
			dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, password,
					caPemContent, keyPemContent, certPemContent);
		} else {
			log.info("====dockerHostTlsEnabled============3================================: "+dockerHostTlsEnabled);
			dockerClientConfig = createDockerClientConfig(dockerHostUrl, registryServerUrl, username, password);
		}
		log.info("====dockerClientConfig============3================================: "+dockerClientConfig);
		return DockerClientBuilder.getInstance(dockerClientConfig).build();
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
	public static final String DEFAULT_DOCKERD_CONFIG_TLS_DISABLED = "" + "[Service]\n" + "ExecStart=\n"
			+ "ExecStart=/usr/bin/dockerd --tls=false -H tcp://0.0.0.0:80 -H unix:///var/run/docker.sock --insecure-registry cognita-nexus01:8001 \n";

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
