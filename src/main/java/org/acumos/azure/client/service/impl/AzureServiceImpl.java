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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.service.AzureService;
import org.acumos.azure.client.transport.AzureDeployDataObject;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.acumos.azure.client.utils.AzureCommonUtil;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.rest.LogLevel;

public class AzureServiceImpl implements AzureService {

	private final Logger logger = LoggerFactory.getLogger(AzureServiceImpl.class);

	// TODO: can this and the setter be removed?
	private Environment env;

	
	

	public void setEnvironment(Environment envrionment) {
		this.env = envrionment;
	}

	

	@Override
	public Azure authorize(AzureDeployDataObject authObject) {
		logger.debug("authorize in AzureServiceImpl start");
		ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(authObject.getClient(),
				authObject.getTenant(), authObject.getKey(), AzureEnvironment.AZURE);
		Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(credentials)
				.withSubscription(authObject.getSubscriptionKey());
		logger.debug("authorize: subscription ID {}, container registries {}", azure.subscriptionId(),
				azure.containerRegistries());
		logger.debug("authorize in AzureServiceImpl End");
		return azure;
	}
	
	
	public Azure authorizeAzure(String azureClient,String azureTenant,String azureKey,String azureSubscriptionKey) {
		logger.debug("authorizeAzure in AzureServiceImpl start");
		ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(azureClient,
				azureTenant, azureKey, AzureEnvironment.AZURE);
		Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(credentials)
				.withSubscription(azureSubscriptionKey);
		logger.debug("authorize: subscription ID {}, container registries {}", azure.subscriptionId(),
				azure.containerRegistries());
		logger.debug("authorizeAzure in AzureServiceImpl End");
		return azure;
	}

	public ArrayList<String> iterateImageMap(HashMap<String, String> imageMap) {
		logger.debug("iterateImageMap ");
		logger.debug("imageMap " + imageMap);
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Map.Entry<String, String>> it = imageMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pair = it.next();
			logger.debug(pair.getKey() + " = " + pair.getValue());
			if (pair.getKey() != null) {
				list.add((String) pair.getKey());
			}
		}
		logger.debug("iterateImageMap: list {}", list);
		logger.debug("iterateImageMap End ");
		return list;
	}

	public String getBluePrintNexus(String solutionId, String revisionId, String datasource, String userName,
			String dataPd, String nexusUrl, String nexusUserName, String nexusPd) throws Exception {
		logger.debug("getBluePrintNexus Start: solutionId {}, revisionId {}", solutionId, revisionId);
		String solutionRevisionId = revisionId;
		List<MLPArtifact> mlpArtifactList;
		String nexusURI = "";
		String bluePrintStr = "";
		ByteArrayOutputStream byteArrayOutputStream = null;
		AzureCommonUtil azureUtil=new AzureCommonUtil();
		CommonDataServiceRestClientImpl cmnDataService = azureUtil.getClient(datasource, userName, dataPd);
		if (null != solutionRevisionId) {
			// 3. Get the list of Artifiact for the SolutionId and SolutionRevisionId.
			mlpArtifactList = cmnDataService.getSolutionRevisionArtifacts(solutionId, solutionRevisionId);
			if (null != mlpArtifactList && !mlpArtifactList.isEmpty()) {
				nexusURI = mlpArtifactList.stream()
						.filter(mlpArt -> mlpArt.getArtifactTypeCode()
								.equalsIgnoreCase(AzureClientConstants.ARTIFACT_TYPE_BLUEPRINT))
						.findFirst().get().getUri();
				logger.debug("getBluePrintNexus: Nexus URI : " + nexusURI);
				if (null != nexusURI) {
					NexusArtifactClient nexusArtifactClient = azureUtil.nexusArtifactClientDetails(nexusUrl, nexusUserName, nexusPd);
					File f = new File(AzureClientConstants.JSON_FILE_NAME);
					if (f.exists() && !f.isDirectory()) {
						f.delete();
					}
					byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
					logger.debug("getBluePrintNexus: byteArrayOutputStream length {}", byteArrayOutputStream.size());
					OutputStream outputStream = new FileOutputStream(AzureClientConstants.JSON_FILE_NAME);
					byteArrayOutputStream.writeTo(outputStream);
					bluePrintStr = byteArrayOutputStream.toString();
				}
			}
		}
		File file = new File(AzureClientConstants.JSON_FILE_NAME);
		if (!file.exists()) {
			throw new Exception(AzureClientConstants.JSON_FILE_NAME + " file is not exist");
		}
		logger.debug("getBluePrintNexus End");
		return bluePrintStr;
	}

	public LinkedList<String> getSequence(HashMap<String, String> hmap) {
		LinkedList<String> sequenceList = new LinkedList<String>();
		Iterator<Map.Entry<String, String>> itrContainer = hmap.entrySet().iterator();
		while (itrContainer.hasNext()) {
			Map.Entry<String,String> pair = itrContainer.next();
			sequenceList.add((String) pair.getValue());
		}
		logger.debug("getSequence: sequenceList {}", sequenceList);
		return sequenceList;
	}

	public LinkedList<String> addContainerSequence(LinkedList<String> sequenceList, String containerName) {
		logger.debug("addContainerSequence Start: containerName {}, sequenceList {}", containerName, sequenceList);
		if (sequenceList != null && sequenceList.size() > 0 && containerName != null && !"".equals(containerName)) {
			int length = sequenceList.size();
			sequenceList.add((length - 1), containerName);
		}
		logger.debug("addContainerSequence End: revised list {}",  sequenceList);
		return sequenceList;
	}
}
