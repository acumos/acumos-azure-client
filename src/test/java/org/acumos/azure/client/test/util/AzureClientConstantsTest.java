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
package org.acumos.azure.client.test.util;

import org.acumos.azure.client.test.transport.TransportBeanTest;
import org.acumos.azure.client.utils.AzureClientConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Assert;
import org.junit.Test;

public class AzureClientConstantsTest {
	
	private static Logger logger = LoggerFactory.getLogger(AzureClientConstantsTest.class);
	@Test	
	public void azureClientConstantsTestparameter(){
		 AzureClientConstants constant=new AzureClientConstants();
		    Assert.assertEquals(constant.APPLICATION_JSON,"application/json");
			Assert.assertEquals(constant.BLUEPRINT_CONTAINER_NAME, "BluePrintContainer");
			Assert.assertEquals(constant.PROBE_CONTAINER_NAME, "Probe");
			Assert.assertEquals(constant.JSON_FILE_NAME, "blueprint.json");
			Assert.assertEquals(constant.AZURE_AUTH_LOCATION, "AZURE_AUTH_LOCATION");
			Assert.assertEquals(constant.AZURE_AUTH_LOCATION_NEXT, "AZURE_AUTH_LOCATION_2");
			Assert.assertEquals(constant.SSH_ACS, "ACS");
			Assert.assertEquals(constant.IMAGE_TAG_LATEST, "latest");
			Assert.assertEquals(constant.PRIVATE_REPO_PREFIX, "/samples/");
			Assert.assertEquals(constant.CONTAINER_NAME_PREFIX, "-private_");
			Assert.assertEquals(constant.PROBE_NODE_TYPE, "Probe");
			Assert.assertEquals(constant.DEFAULT_NODE_TYPE, "Default");
			Assert.assertEquals(constant.PUT_DOCKER_INFO_URL, "putDockerInfo");
			Assert.assertEquals(constant.PUT_BLUEPRINT_INFO_URL, "putBlueprint");
			Assert.assertEquals(constant.CONFIG_DB_URL, "configDB");
			Assert.assertEquals(constant.DATABROKER_NAME, "DataBroker");
			Assert.assertEquals(constant.DEPLOYMENT_PROCESS, "DP");
			Assert.assertEquals(constant.DEPLOYMENT_FAILED, "FA");
			Assert.assertEquals(constant.DATA_BROKER_CSV_FILE, "csv");
			Assert.assertEquals(constant.ARTIFACT_TYPE_BLUEPRINT, "BP");
			Assert.assertEquals(constant.HTTP_PROPERTY, "https.protocols");
			Assert.assertEquals(constant.HTTP_PROPERTY_VALUE, "TLSv1");
			Assert.assertEquals(constant.SSL_DOCKER, "docker");
			Assert.assertEquals(constant.DOCKER_CERT_PATH,"DOCKER_CERT_PATH");
			Assert.assertEquals(constant.CA_PEM,"ca.pem");
			Assert.assertEquals(constant.KEY_PEM,"key.pem");
			Assert.assertEquals(constant.CERT_PEM,"cert.pem");
			Assert.assertEquals(constant.VNET_NAME,"vnet");
			Assert.assertEquals(constant.FRONT_END_NSG_NAME,"fensg");
			Assert.assertEquals(constant.NETWORK_INTERFACE_NAME,"nic1");
			Assert.assertEquals(constant.PUBLIC_IP_ADDRESS_LEAF,"pip1");
			Assert.assertEquals(constant.NODES,"nodes");
			Assert.assertEquals(constant.DEPENDS_ON,"depends_on");
			Assert.assertEquals(constant.CONTAINER_NAME,"container_name");
			Assert.assertEquals(constant.IMAGE,"image");
			Assert.assertEquals(constant.NAME,"name");
			Assert.assertEquals(constant.VERSION,"version");
			Assert.assertEquals(constant.ORCHESTRATOR,"orchestrator");
			Assert.assertEquals(constant.INPUT_OPERATION_SIGNATURES,"input_operation_signatures");
			Assert.assertEquals(constant.OPERATION,"operation");
			Assert.assertEquals(constant.PROBE_INDOCATOR,"probeIndocator");
			Assert.assertEquals(constant.PROBE_INDICATOR,"probeIndicator");
			Assert.assertEquals(constant.OPERATION_SIGNATURE,"operation_signature");
			Assert.assertEquals(constant.CONNECTED_TO,"connected_to");
			Assert.assertEquals(constant.OPERATION_NAME,"operation_name");
			Assert.assertEquals(constant.INPUT_MESSAGE_NAME,"input_message_name");
			Assert.assertEquals(constant.OUTPUT_MESSAGE_NAME,"output_message_name");
			Assert.assertEquals(constant.BLUEPRINT_CONTAINER,"BluePrintContainer");
			Assert.assertEquals(constant.CONTAINER_TEST,"test");
			Assert.assertEquals(constant.TRAINING_CLIENTS,"training_clients");
			Assert.assertEquals(constant.INPUT_PORTS,"input_ports");
			Assert.assertEquals(constant.OPERATION_SIGNATURE_LIST,"operation_signature_list");
			Assert.assertEquals(constant.NODE_TYPE,"node_type");
			Assert.assertEquals(constant.PROTO_URI,"proto_uri");
			Assert.assertEquals(constant.SCRIPT,"script");
			Assert.assertEquals(constant.DATA_BROKER_MAP,"data_broker_map");
			Assert.assertEquals(constant.DATA_BROKER_TYPE,"data_broker_type");
			Assert.assertEquals(constant.DEFAULT,"Default");
			Assert.assertEquals(constant.DATA_BROKER,"DataBroker");
			Assert.assertEquals(constant.CSV_FILE_NAME,"csv");
			Assert.assertEquals(constant.TARGET_SYSTEM_URL,"target_system_url");
			Assert.assertEquals(constant.LOCAL_SYSTEM_DATA_FILE_PATH,"local_system_data_file_path");
			Assert.assertEquals(constant.FIRST_ROW,"first_row");
			Assert.assertEquals(constant.CSV_FILE_FIELD_SEPARATOR,"csv_file_field_separator");
			Assert.assertEquals(constant.MAP_INPUTS,"map_inputs");
			Assert.assertEquals(constant.INPUT_FIELD,"input_field");
			Assert.assertEquals(constant.TYPE,"type");
			Assert.assertEquals(constant.CHECKED,"checked");
			Assert.assertEquals(constant.MAPPED_TO_FIELD,"mapped_to_field");
			Assert.assertEquals(constant.MAP_OUTPUTS,"map_outputs");
			Assert.assertEquals(constant.OUTPUT_FIELD,"output_field");
			Assert.assertEquals(constant.TAG,"tag");
			Assert.assertEquals(constant.TYPE_AND_ROLE_HIERARCHY_LIST,"type_and_role_hierarchy_list");
			Assert.assertEquals(constant.ROLE,"role");
			Assert.assertEquals(constant.VM_CREATION_ERROR,"role");
			Assert.assertEquals(constant.SETUP_SCRIPT_NAME,"setup-docker.sh");
			Assert.assertEquals(constant.DOCKER_CONTAINER_PREFIX,"acumos-e6e");
			Assert.assertEquals(constant.MSG_SEVERITY_ME , "ME");
			Assert.assertEquals(constant.BLUEPRINT_NAME_PROP,"blueprint.name");
			Assert.assertEquals(constant.REGISTRY_BLUEPRINT_USERNAME_PROP,"docker.registry.bluePrint.username");
			Assert.assertEquals(constant.REGISTRY_BLUEPRINT_PD_PROP,"docker.registry.bluePrint.password");
			Assert.assertEquals(constant.REGISTRY_NETWORKGROUPNAME_PROP,"docker.registry.networkgroupName");
			Assert.assertEquals(constant.REGISTRY_PORT_PROP,"docker.registry.port");
			Assert.assertEquals(constant.CMNDATASVC_CMNDATASVCENDPOINURL_PROP,"cmndatasvc.cmndatasvcendpoinurl");
			Assert.assertEquals(constant.CMNDATASVC_CMNDATASVCUSER_PROP,"cmndatasvc.cmndatasvcuser");
			Assert.assertEquals(constant.CMNDATASVC_CMNDATASVCPD_PROP,"cmndatasvc.cmndatasvcpwd");
			Assert.assertEquals(constant.DOCKERVMUSERNAME_PROP,"docker.dockerVMUserName");
			Assert.assertEquals(constant.DOCKERVMPD_PROP,"docker.dockerVMPassword");
			Assert.assertEquals(constant.REPLACECHAR_PROP,"docker.replaceChar");
			Assert.assertEquals(constant.IGNORE_DOLLER_PROP,"docker.ignordoller");
			Assert.assertEquals(constant.SPECIAL_CHAR_PROP,"$");
			Assert.assertEquals(constant.SOLUTIONPORT_PROP,"docker.solutionPort");
			Assert.assertEquals(constant.SUBNET_PROP,"docker.subnet");
			Assert.assertEquals(constant.VNET_PROP,"docker.vnet");
			Assert.assertEquals(constant.SLEEPTIME_FIRST,"docker.sleepTimeFirst");
			Assert.assertEquals(constant.SLEEPTIME_SECOND,"docker.sleepTimeSecond");
			Assert.assertEquals(constant.REGISTRY_NAME_PROP,"docker.registry.name");
			Assert.assertEquals(constant.CONTAINERNAMEPREFIX_PROP,"docker.containerNamePrefix");
			Assert.assertEquals(constant.REGISTRY_USERNAME_PROP,"docker.registry.username");
			Assert.assertEquals(constant.REGISTRY_PD_PROP,"docker.registry.password");
			Assert.assertEquals(constant.HOST_PROP,"docker.host");
			Assert.assertEquals(constant.PORT_PROP,"docker.port");
			Assert.assertEquals(constant.BLUEPRINT_IMAGENAME_PROP,"blueprint.ImageName");
			Assert.assertEquals(constant.PROBE_IMAGENAME_PROP,"probe.ImageName");
			Assert.assertEquals(constant.PROBE_NAME_PROP,"probe.name");
			Assert.assertEquals(constant.PROBE_INTERNALPORT_PROP,"probe.internalPort");
			Assert.assertEquals(constant.PROBE_PROBENEXUSENDPOINT_PROP,"probe.probeNexusEndPoint");
			Assert.assertEquals(constant.DOCKER_REGISTRY_PROBE_USERNAME_PROP,"docker.registry.probe.username");
			Assert.assertEquals(constant.DOCKER_REGISTRY_PROBE_PD_PROP,"docker.registry.probe.password");
			Assert.assertEquals(constant.NEXUS_URL_PROP,"nexus.url");
			Assert.assertEquals(constant.NEXUS_USERNAME_PROP,"nexus.username");
			Assert.assertEquals(constant.NEXUS_PD_PROP,"nexus.password");
			Assert.assertEquals(constant.NEXUS_REGISTY_USERNAME,"docker.nexusRegistyUserName");
			Assert.assertEquals(constant.NEXUS_REGISTY_PD,"docker.nexusRegistyPwd");
			Assert.assertEquals(constant.NEXUS_REGISTY_NAME,"docker.nexusRegistyName");
			Assert.assertEquals(constant.OTHER_REGISTY_NAME,"docker.otherRegistyName");
			Assert.assertEquals(constant.EXPOSE_DATABROKER_PORT,"docker.exposeDataBrokerPort");
			Assert.assertEquals(constant.INTERNAL_DATABROKER_PORT,"docker.internalDataBrokerPort");
			Assert.assertEquals(constant.NGINX_CONTAINER,"Nginx");
			Assert.assertEquals(constant.NGINX_IMAGE,"nginx");
			Assert.assertEquals(constant.NGINX_MAPFOLDER,"docker.nginxMapFolder");
			Assert.assertEquals(constant.NGINX_WEBFOLDER,"docker.nginxWebFolder");
			Assert.assertEquals(constant.NGINX_IMAGENAME,"docker.nginxImageName");
			Assert.assertEquals(constant.NGINX_INTERNALPORT,"docker.nginxInternalPort");
			Assert.assertEquals(constant.DATAFILE_FOLDER,"docker.azureDataFiles");
			Assert.assertEquals(constant.KUBERNETESCLIENT_URL,"docker.kubernetesClientUrl");
		
	}

}
