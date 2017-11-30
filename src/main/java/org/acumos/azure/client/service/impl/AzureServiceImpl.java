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

package org.acumos.azure.client.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.controller.AzureServiceController;
import org.acumos.azure.client.service.AzureService;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.AzureBean;
import org.acumos.azure.client.utils.Blueprint;
import org.acumos.azure.client.utils.DockerInfo;
import org.acumos.azure.client.utils.DockerInfoList;
import org.acumos.azure.client.utils.DockerUtils;
import org.acumos.azure.client.utils.SSHShell;
import org.acumos.azure.client.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.rest.LogLevel;

public class AzureServiceImpl implements AzureService {

	Logger logger = LoggerFactory.getLogger(AzureServiceController.class);

	@Override
	public Azure authorize(AzureDeployDataObject authObject) {
		// TODO Auto-generated method stub
		logger.debug("<------start----authorize in AzureServiceImpl------------>");
		logger.debug(" authentication parameters:: " + authObject.toString());

		ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(authObject.getClient(),
				authObject.getTenant(), authObject.getKey(), AzureEnvironment.AZURE);

		Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(credentials)
				.withSubscription(authObject.getSubscriptionKey());
		logger.debug("try getting some info : " + azure.subscriptionId() + " " + azure.containerRegistries());
		logger.debug("Azure AD Authorization Successful...");
		logger.debug("<------End----authorize in AzureServiceImpl------------>");
		return azure;
	}

	@Override
	public AzureBean pushImage(Azure azure, AzureDeployDataObject deployDataObject, String dockerContainerPrefix,
			String dockerUserName, String dockerPwd, String localEnvDockerHost, String localEnvDockerCertPath,
			ArrayList<String> list, String bluePrintName, String bluePrintUser, String bluePrintPass,
			String networkSecurityGroup, String dockerRegistryPort) throws IOException, Exception {
		logger.debug("<------start----pushImage in AzureServiceImpl------------>");
		AzureBean azureBean = new AzureBean();
		HashMap<String, String> containeDetailMap = new HashMap<String, String>();
		DockerInfoList dockerList = new DockerInfoList();
		final String saName = SdkContext.randomResourceName("sa", 20);
		final Region region = Region.US_EAST;
		final String dockerContainerName = dockerContainerPrefix + System.currentTimeMillis();// "acrsample";

		String servicePrincipalClientId = deployDataObject.getClient(); // replace with a real service principal client
																		// id
		String servicePrincipalSecret = deployDataObject.getKey(); // and corresponding secret
		HashMap<String, String> containerMap = new HashMap<String, String>();
		String containerInstanceBluePrint = "";
		String bluePrintContainerId = "";
		logger.debug("<--------------list--------------------------->" + list);
		logger.debug("<---------bluePrintName------------->" + bluePrintName);
		String portArr[] = { "8556", "8557", "8558", "8559", "8560", "8561", "8562", "8563", "8564", "8565" };
		if (list != null && list.size() > 0) {

			// =============================================================
			// If service principal client id and secret are not set via the local
			// variables, attempt to read the service
			// principal client id and secret from a secondary ".azureauth" file set through
			// an environment variable.
			//
			// If the environment variable was not set then reuse the main service principal
			// set for running this sample.

			if (servicePrincipalClientId.isEmpty() || servicePrincipalSecret.isEmpty()) {
				String envSecondaryServicePrincipal = System.getenv("AZURE_AUTH_LOCATION_2");

				if (envSecondaryServicePrincipal == null || !envSecondaryServicePrincipal.isEmpty()
						|| !Files.exists(Paths.get(envSecondaryServicePrincipal))) {
					envSecondaryServicePrincipal = System.getenv("AZURE_AUTH_LOCATION");
				}

				servicePrincipalClientId = Utils.getSecondaryServicePrincipalClientID(envSecondaryServicePrincipal);
				servicePrincipalSecret = Utils.getSecondaryServicePrincipalSecret(envSecondaryServicePrincipal);
			}

			// =============================================================
			// Create an SSH private/public key pair to be used when creating the container
			// service

			logger.debug("Creating an SSH private and public key pair");

			SSHShell.SshPublicPrivateKey sshKeys = SSHShell.generateSSHKeys("", "ACS");
			logger.debug("SSH private key value: \n" + sshKeys.getSshPrivateKey());
			logger.debug("SSH public key value: \n" + sshKeys.getSshPublicKey());

			// =============================================================
			// Create an Azure Container Service with Kubernetes orchestration

			logger.debug(
					"Creating an Azure Container Service with Kubernetes ochestration and one agent (virtual machine)");

			Date t1 = new Date();

			// =============================================================
			// Create an Azure Container Registry to store and manage private Docker
			// container images

			logger.debug("Creating an Azure Container Registry");
			Date t2 = new Date();
			t1 = new Date();

			// Get the existing Azure registry using resourceGroupName and Acr Name
			Registry azureRegistry = azure.containerRegistries().getByResourceGroup(deployDataObject.getRgName(),
					deployDataObject.getAcrName());

			t2 = new Date();
			logger.debug("Created Azure Container Registry: (took " + ((t2.getTime() - t1.getTime()) / 1000)
					+ " seconds) " + azureRegistry.id());
			Utils.print(azureRegistry);

			// =============================================================
			// Create a Docker client that will be used to push/pull images to/from the
			// Azure Container Registry

			RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
			logger.debug("azureRegistry.loginServerUrl=" + azureRegistry.loginServerUrl() + ", acrCredentials.username "
					+ acrCredentials.username() + ", acrCredentials.passwords"
					+ acrCredentials.passwords().get(0).value());
			DockerClient dockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(), region,
					azureRegistry.loginServerUrl(), acrCredentials.username(),
					acrCredentials.passwords().get(0).value(), localEnvDockerHost, localEnvDockerCertPath, azureBean,
					networkSecurityGroup, dockerRegistryPort);

			AuthConfig authConfig = new AuthConfig().withUsername(dockerUserName).withPassword(dockerPwd);

			AuthConfig authConfig2 = new AuthConfig().withUsername(bluePrintUser).withPassword(bluePrintPass);

			// =============================================================
			// Pull a temp image from public Docker repo and create a temporary container
			// from that image
			// These steps can be replaced and instead build a custom image using a
			// Dockerfile and the app's JAR
			logger.debug("Start pulling images from nexus::::::::");
			Iterator itr = list.iterator();
			while (itr.hasNext()) {

				String imageName = (String) itr.next();
				// logger.debug("Nexus Image Name------------------->"+imageName);
				if (imageName != null && imageName.contains(bluePrintName)) {
					dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig2)
							// .withTag(dockerImageTag)
							.exec(new PullImageResultCallback()).awaitSuccess();

				} else {
					dockerClient.pullImageCmd(imageName).withAuthConfig(authConfig)
							// .withTag(dockerImageTag)
							.exec(new PullImageResultCallback()).awaitSuccess();
				}
			}

			logger.debug("List local Docker images:");
			List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();
			for (Image image : images) {
				// logger.debug("Docker Images \n"+
				// image.getRepoTags()[0]+"<----image.getId()--->"+ image.getId());
			}
			int dockerCount = 1;
			HashMap<String, CreateContainerResponse> hmap = new HashMap<String, CreateContainerResponse>();
			HashMap<String, String> containerTagMap = new HashMap<String, String>();
			HashMap<String, String> containerImageMap = new HashMap<String, String>();
			HashMap<String, String> containerInstanceImageMap = new HashMap<String, String>();
			Iterator itr1 = list.iterator();
			while (itr1.hasNext()) {
				String imageTagVal = "";
				String imageName = (String) itr1.next();
				CreateContainerResponse dockerContainerInstance = dockerClient.createContainerCmd(imageName)
						.withName(dockerContainerName + "_" + dockerCount)
						// .withCmd("/hello")
						.exec();

				if (imageName != null && !"".equals(imageName)) {
					String tag = getTagFromImage(imageName);
					if (tag != null) {
						imageTagVal = tag;
					}
				}
				logger.info("===imageName=======" + imageName + "========imageTagVal====" + imageTagVal);
				if (imageName != null && imageName.contains(bluePrintName)) {
					containerInstanceBluePrint = dockerContainerName + "_" + dockerCount;
				}
				hmap.put(dockerContainerName + "_" + dockerCount, dockerContainerInstance);
				containerTagMap.put(dockerContainerName + "_" + dockerCount, imageTagVal);
				containerImageMap.put(dockerContainerName + "_" + dockerCount, imageName);
				dockerCount++;
			}
			System.out.println("=======containerImageMap=====" + containerImageMap);
			logger.debug("List All Docker containers:");
			List<Container> dockerContainers = dockerClient.listContainersCmd().withShowAll(true).exec();
			for (Container container : dockerContainers) {
				logger.debug("All Docker container with images and Name %s (%s)\n" + container.getImage()
						+ "<-----container.getId()----->" + container.getId());
			}

			// =============================================================
			// Commit the new container
			// String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" +
			// dockerContainerName;
			// logger.debug("privateRepoUrl::::::::::::::::::"+privateRepoUrl);
			HashMap<String, String> repoUrlMap = new HashMap<String, String>();
			Iterator itrContainer = hmap.entrySet().iterator();
			while (itrContainer.hasNext()) {
				String imageTagLatest = "latest";
				Map.Entry pair = (Map.Entry) itrContainer.next();
				String containerName = (String) pair.getKey();
				CreateContainerResponse dockerContainerInstance = (CreateContainerResponse) pair.getValue();

				String privateRepoUrl = azureRegistry.loginServerUrl() + "/samples/" + containerName;
				logger.debug("dockerContainerInstance.getId():::::::::::::::::" + dockerContainerInstance.getId()
						+ "===privateRepoUrl====" + privateRepoUrl);
				if (containerTagMap != null && containerTagMap.get(containerName) != null) {
					imageTagLatest = containerTagMap.get(containerName);
				}
				logger.debug("containerName======" + containerName + "==imageTagLatest======" + imageTagLatest);
				String dockerImageId = dockerClient.commitCmd(dockerContainerInstance.getId())
						.withRepository(privateRepoUrl).withTag(imageTagLatest).exec();
				logger.debug("dockerImageId::::::::::::::::::" + dockerImageId);
				repoUrlMap.put(containerName, privateRepoUrl);
				// We can now remove the temporary container instance
				dockerClient.removeContainerCmd(dockerContainerInstance.getId()).withForce(true).exec();
			}
			// #####################################################################################
			logger.debug("<----Before Docker remoteDockerClient--------------------------->");
			DockerClient remoteDockerClient = DockerUtils.createDockerClient(azure, deployDataObject.getRgName(),
					region, azureRegistry.loginServerUrl(), acrCredentials.username(),
					acrCredentials.passwords().get(0).value(), null, localEnvDockerCertPath, azureBean,
					networkSecurityGroup, dockerRegistryPort);
			logger.debug("<----After Docker remoteDockerClient--------------------------->");
			// =============================================================
			// Push the new Docker image to the Azure Container Registry
			Iterator repoContainer = repoUrlMap.entrySet().iterator();
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();

				dockerClient.pushImageCmd(privateRepoUrl).withAuthConfig(dockerClient.authConfig())
						.exec(new PushImageResultCallback()).awaitSuccess();
			}

			logger.debug("<----Pushed Images to privaterepourl and removing imgage from local docker host---------->");
			// Remove the temp image from the local Docker host
			try {
				Iterator itr5 = list.iterator();
				while (itr5.hasNext()) {
					String imageName = (String) itr5.next();
					dockerClient.removeImageCmd(imageName).withForce(true).exec();
				}
			} catch (NotFoundException e) {
				logger.error("Error in removing images " + e.getMessage());
			}

			// =============================================================
			// Verify that the image we saved in the Azure Container registry can be pulled
			// and instantiated locally
			logger.debug("<----pull images from Azure registry to locally--------->");
			repoContainer = repoUrlMap.entrySet().iterator();
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();

				dockerClient.pullImageCmd(privateRepoUrl).withAuthConfig(dockerClient.authConfig())
						.exec(new PullImageResultCallback()).awaitSuccess();
			}

			logger.debug("List local Docker images after pulling sample image from the Azure Container Registry:");
			images = dockerClient.listImagesCmd().withShowAll(true).exec();
			for (Image image : images) {
				// logger.debug("List Image after pulling locally \n"+
				// image.getRepoTags()[0]+"<-------ImageId--------->"+ image.getId());
			}

			// ++++++++++++++++++++ Verify if docker image can be pulled in to remote ACS
			logger.debug("<----remoteDockerClient with privateRepoUrl--------->");

			repoContainer = repoUrlMap.entrySet().iterator();
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();

				remoteDockerClient.pullImageCmd(privateRepoUrl).withAuthConfig(dockerClient.authConfig())
						.exec(new PullImageResultCallback()).awaitSuccess();
			}
			logger.debug("#######List Docker images after pulling in the Azure Container Registry:");
			List<Image> remoteImages = remoteDockerClient.listImagesCmd().withShowAll(true).exec();
			for (Image image : remoteImages) {
				// logger.debug("<==================Remote Docker image %s (%s)======> \n"+
				// image.getRepoTags()[0]+"<-----ImageId------>"+image.getId());
			}

			logger.debug("<----remoteDockerContainerInstance--------->");
			int remoteCount = 1;
			ArrayList<CreateContainerResponse> alist = new ArrayList<CreateContainerResponse>();
			repoContainer = repoUrlMap.entrySet().iterator();
			int portCount = 0;
			while (repoContainer.hasNext()) {
				Map.Entry pair = (Map.Entry) repoContainer.next();
				String containerName = (String) pair.getKey();
				String privateRepoUrl = (String) pair.getValue();
				CreateContainerResponse remoteDockerContainerInstance = null;
				String tagImage = "latest";
				String imageName = "";

				if (containerTagMap != null && containerTagMap.get(containerName) != null) {
					tagImage = containerTagMap.get(containerName);
				}
				if (containerImageMap != null && containerImageMap.get(containerName) != null) {
					imageName = containerImageMap.get(containerName);
				}
				logger.debug("<----containerName--------->" + containerName + "===tagImage==" + tagImage);
				if (containerInstanceBluePrint != null && containerInstanceBluePrint.equalsIgnoreCase(containerName)) {
					logger.debug("<--if Part--containerInstanceBluePrint--------->" + containerInstanceBluePrint
							+ "=====containerName===" + containerName);
					// ExposedPort exportPort=new ExposedPort(8555);
					/*
					 * remoteDockerContainerInstance =
					 * remoteDockerClient.createContainerCmd(privateRepoUrl+":"+tagImage)
					 * .withName(dockerContainerName +
					 * "-private_"+remoteCount).withExposedPorts(exportPort).exec();
					 */
					ExposedPort tcp8555 = ExposedPort.tcp(8555);
					Ports portBindings = new Ports();
					portBindings.bind(tcp8555, new Ports.Binding("0.0.0.0", "8555"));
					remoteDockerContainerInstance = remoteDockerClient
							.createContainerCmd(privateRepoUrl + ":" + tagImage)
							.withName(dockerContainerName + "-private_" + remoteCount).withExposedPorts(tcp8555)
							.withPortBindings(portBindings).exec();
					logger.debug("<--if Part-complete-containerInstanceBluePrint--------->" + containerInstanceBluePrint
							+ "=====containerName===" + containerName);
				} else {
					if (portCount < 10) {
						String portNumber = portArr[portCount];
						ExposedPort tcp = ExposedPort.tcp(Integer.parseInt(portNumber));
						logger.debug("<--if portCount--------->" + portCount + "=====portNumber===" + portNumber);
						Ports portBindings = new Ports();
						portBindings.bind(tcp, new Ports.Binding("0.0.0.0", portNumber));
						remoteDockerContainerInstance = remoteDockerClient
								.createContainerCmd(privateRepoUrl + ":" + tagImage)
								.withName(dockerContainerName + "-private_" + remoteCount).withExposedPorts(tcp)
								.withPortBindings(portBindings).exec();
						logger.debug("<--if Part-complete-containerInstanceBluePrint--------->"
								+ containerInstanceBluePrint + "=====containerName===" + containerName);
						portCount = portCount + 1;

					} else {
						logger.debug("<--else part-of -portCount--------->" + containerInstanceBluePrint
								+ "=====containerName===" + containerName);
						ExposedPort exportPort = new ExposedPort(8080);
						Ports portBindings = new Ports();
						portBindings.bind(exportPort, new Ports.Binding("0.0.0.0", null));
						remoteDockerContainerInstance = remoteDockerClient
								.createContainerCmd(privateRepoUrl + ":" + tagImage)
								.withName(dockerContainerName + "-private_" + remoteCount)
								.withPortBindings(portBindings).withPublishAllPorts(true).exec();
						logger.debug("<--else part-Complete-otherImages--------->" + containerInstanceBluePrint
								+ "=====containerName===" + containerName);
					}

				}

				logger.debug("<----Id   remoteDockerContainerInstance.getId()--------->"
						+ remoteDockerContainerInstance.getId());
				containerInstanceImageMap.put(remoteDockerContainerInstance.getId(), imageName);
				alist.add(remoteDockerContainerInstance);
				remoteCount = remoteCount + 1;
			}
			logger.debug("<----List   remoteDockerContainerInstance--------->");
			List<Container> dockerContainerList = remoteDockerClient.listContainersCmd().withShowAll(true).exec();
			for (Container container : dockerContainerList) {
				logger.debug("All Docker container with images and Name %s (%s)\n" + container.getImage()
						+ "<-----container.getId()----->" + container.getId() + "===containerInstanceBluePrint="
						+ containerInstanceBluePrint);
				if (containerInstanceBluePrint != null && container.getImage().contains(containerInstanceBluePrint)) {
					containerMap.put(container.getId(), container.getImage());
					bluePrintContainerId = container.getId();
				}
				// containerMap.put(container.getId(), container.getImage());

			}

			System.out.println("=======containerMap=====" + containerMap);
			System.out.println("=======containerInstanceImageMap=====" + containerInstanceImageMap);
			azureBean.setBluePrintMap(containerMap);
			List<DockerInfo> dockerInfoList = new ArrayList<DockerInfo>();
			Iterator it6 = alist.iterator();
			while (it6.hasNext()) {
				DockerInfo dockerinfo = new DockerInfo();
				CreateContainerResponse remoteDockerContainerInstance = (CreateContainerResponse) it6.next();
				String ipNumber = "";
				String portNumber = "";
				String containerId = "";
				if (remoteDockerContainerInstance != null) {
					remoteDockerClient.startContainerCmd(remoteDockerContainerInstance.getId()).exec();
					logger.debug("<----List  ID  remoteDockerContainerInstance.getId()--------->"
							+ remoteDockerContainerInstance.getId());
					InspectContainerResponse inspectContainer = remoteDockerClient
							.inspectContainerCmd(remoteDockerContainerInstance.getId()).exec();
					if (inspectContainer != null && inspectContainer.getNetworkSettings() != null
							&& inspectContainer.getNetworkSettings().getIpAddress() != null) {
						ipNumber = inspectContainer.getNetworkSettings().getIpAddress();
						containerId = remoteDockerContainerInstance.getId();
						logger.debug("<----remoteDockerContainerInstance--------->"
								+ inspectContainer.getNetworkSettings().getIpAddress());
					} else {
						logger.debug(
								"Else part of==getIpAddress==================" + remoteDockerContainerInstance.getId());
					}

					if (inspectContainer != null && inspectContainer.getNetworkSettings() != null
							&& inspectContainer.getNetworkSettings().getPorts() != null) {
						Ports ports = inspectContainer.getNetworkSettings().getPorts();
						Iterator bindIterrator = ports.getBindings().entrySet().iterator();

						while (bindIterrator.hasNext()) {
							Map.Entry pair = (Map.Entry) bindIterrator.next();
							ExposedPort exposedPort = (ExposedPort) pair.getKey();
							String portNumberStr = String.valueOf(exposedPort.getPort());
							if (portNumberStr != null) {
								portNumber = portNumberStr;
							}
							logger.debug("ExposedPort====" + exposedPort.getPort() + "===Binding=" + pair.getValue());

						}

					}
				}

				if (ipNumber != null && !"".equals(ipNumber)) {
					String imageVal = "";
					dockerinfo.setIpAddress(ipNumber);
					dockerinfo.setPort(portNumber);
					// dockerinfo.setContainer(containerId);
					dockerInfoList.add(dockerinfo);
					if (containerInstanceImageMap != null && containerInstanceImageMap.get(containerId) != null) {
						imageVal = containerInstanceImageMap.get(containerId);
						if (imageVal != null) {
							dockerinfo.setContainer(imageVal);
						}
					}
					if (bluePrintContainerId != null && bluePrintContainerId.equalsIgnoreCase(containerId)) {
						logger.debug("BluePrintContainer Id==========>" + bluePrintContainerId);
						azureBean.setBluePrintIp(ipNumber);
						azureBean.setBluePrintPort(portNumber);
					}
					containeDetailMap.put(ipNumber, portNumber);
				}

			}
			if (dockerInfoList != null && dockerInfoList.size() > 0) {
				dockerList.setDockerList(dockerInfoList);
			}
		} else {
			logger.debug("List is blank or null==========>" + list);
		}
		logger.debug("containeDetailMap==========>" + containeDetailMap + "=====dockerList=====" + dockerList);
		azureBean.setDockerinfolist(dockerList);
		// #####################################################################################
		// Code below is repeated
		// #####################################################################################
		// =============================================================
		// Push the new Docker image to the Azure Container Registry

		/*
		 * dockerClient.pushImageCmd(privateRepoUrl)
		 * .withAuthConfig(dockerClient.authConfig()) .exec(new
		 * PushImageResultCallback()).awaitSuccess();
		 * 
		 * // Remove the temp image from the local Docker host try {
		 * dockerClient.removeImageCmd(deployDataObject.getImagetag()).withForce(true).
		 * exec(); } catch (NotFoundException e) { // just ignore if not exist }
		 * 
		 * //============================================================= // Verify
		 * that the image we saved in the Azure Container registry can be pulled and
		 * instantiated locally
		 * 
		 * dockerClient.pullImageCmd(privateRepoUrl)
		 * .withAuthConfig(dockerClient.authConfig()) .exec(new
		 * PullImageResultCallback()).awaitSuccess(); System.out.
		 * println("List local Docker images after pulling sample image from the Azure Container Registry:"
		 * ); images = dockerClient.listImagesCmd() .withShowAll(true) .exec(); for
		 * (Image image : images) {
		 * //System.out.format("\tFound Docker image %s (%s)\n", image.getRepoTags()[0],
		 * image.getId()); } dockerContainerInstance =
		 * dockerClient.createContainerCmd(privateRepoUrl) .withName(dockerContainerName
		 * + "-private") //.withCmd("/hello") .exec();
		 * 
		 * System.out.
		 * println("List Docker containers after instantiating container from the Azure Container Registry sample image:"
		 * ); dockerContainers = dockerClient.listContainersCmd() .withShowAll(true)
		 * .exec(); for (Container container : dockerContainers) {
		 * System.out.println("\tFound Docker container "+ container.getImage()); }
		 */
		// #####################################################################################

		Thread.sleep(5000);
		logger.debug("Successfuly Deployed the docker image to Azure Container Resgistry");
		logger.debug("<------End----pushImage in AzureServiceImpl------------>");
		return azureBean;
	}

	public void putContainerDetails(DockerInfoList dockerList, String apiUrl) {
		logger.debug("<--------Start---putContainerDetails------->");
		try {
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			HttpEntity<DockerInfoList> entity = new HttpEntity<DockerInfoList>(dockerList);
			restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("<---------Exception----------->" + e.getMessage());
		}
		logger.debug("<--------End---putContainerDetails------->");
	}

	public void putBluePrintDetails(Blueprint bluePrint, String apiUrl) {
		logger.debug("<--------Start---putContainerDetails------->");
		try {
			final String url = apiUrl;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpEntity<Blueprint> entity = new HttpEntity<Blueprint>(bluePrint);
			restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("<---------Exception----------->" + e.getMessage());
		}
		logger.debug("<--------End---putContainerDetails------->");
	}

	public String getTagFromImage(String imageName) {
		String imageTag = null;
		final int endColon = imageName.lastIndexOf(':');
		if (endColon < 0) {
			imageTag = null;
		} else {
			final String tag = imageName.substring(endColon + 1);
			if (tag.indexOf('/') < 0) {
				imageTag = tag;
			} else {
				imageTag = null;
			}
		}
		return imageTag;
	}
}
