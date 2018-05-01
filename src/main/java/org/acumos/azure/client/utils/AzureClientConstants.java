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

public final class AzureClientConstants {
	
	public static final String APPLICATION_JSON= "application/json";
	public static final String BLUEPRINT_CONTAINER_NAME= "BluePrintContainer";
	public static final String PROBE_CONTAINER_NAME= "Probe";
	public static final String JSON_FILE_NAME= "blueprint.json";
	public static final String AZURE_AUTH_LOCATION= "AZURE_AUTH_LOCATION";
	public static final String AZURE_AUTH_LOCATION_NEXT= "AZURE_AUTH_LOCATION_2";
	public static final String SSH_ACS= "ACS";
	public static final String IMAGE_TAG_LATEST= "latest";
	public static final String PRIVATE_REPO_PREFIX= "/samples/";
	public static final String CONTAINER_NAME_PREFIX= "-private_";
	public static final String PROBE_NODE_TYPE= "Probe";
	public static final String DEFAULT_NODE_TYPE= "Default";
	public static final String PUT_DOCKER_INFO_URL= "putDockerInfo";
	public static final String PUT_BLUEPRINT_INFO_URL= "putBlueprint";
	public static final String CONFIG_DB_URL= "configDB";
	public static final String DATABROKER_NAME= "DataBroker";
	public static final String DEPLOYMENT_PROCESS= "DP";
	public static final String DEPLOYMENT_FAILED= "FA";
	public static final String DATA_BROKER_CSV_FILE= "CSV_File";
	public static final String ARTIFACT_TYPE_BLUEPRINT= "BP";
	public static final String HTTP_PROPERTY= "https.protocols";
	public static final String HTTP_PROPERTY_VALUE= "TLSv1";
	public static final String SSL_DOCKER= "docker";
	public static final String DOCKER_CERT_PATH="DOCKER_CERT_PATH";
	public static final String CA_PEM="ca.pem";
	public static final String KEY_PEM="key.pem";
	public static final String CERT_PEM="cert.pem";
	public static final String VNET_NAME="vnet";
	public static final String FRONT_END_NSG_NAME="fensg";
	public static final String NETWORK_INTERFACE_NAME="nic1";
	public static final String PUBLIC_IP_ADDRESS_LEAF="pip1";
	public static final int SSH_PORT=22;
	public static final int DOCKER_HOST_PORT=80;
	public static final String NODES="nodes";
	public static final String DEPENDS_ON="depends_on";
	public static final String CONTAINER_NAME="container_name";
	public static final String IMAGE="image";
	public static final String NAME="name";
	public static final String VERSION="version";
	public static final String ORCHESTRATOR="orchestrator";
	public static final String INPUT_OPERATION_SIGNATURES="input_operation_signatures";
	public static final String OPERATION="operation";
	public static final String PROBE_INDOCATOR="probeIndocator";
	public static final String PROBE_INDICATOR="probeIndicator";
	public static final String OPERATION_SIGNATURE="operation_signature";
	public static final String CONNECTED_TO="connected_to";
	public static final String OPERATION_NAME="operation_name";
	public static final String INPUT_MESSAGE_NAME="input_message_name";
	public static final String OUTPUT_MESSAGE_NAME="output_message_name";
	public static final String BLUEPRINT_CONTAINER="BluePrintContainer";
	public static final String CONTAINER_TEST="test";
	public static final String TRAINING_CLIENTS="training_clients";
	public static final String INPUT_PORTS="input_ports";
	public static final String OPERATION_SIGNATURE_LIST="operation_signature_list";
	public static final String NODE_TYPE="node_type";
	public static final String PROTO_URI="proto_uri";
	public static final String SCRIPT="script";
	public static final String DATA_BROKER_MAP="data_broker_map";
	public static final String DATA_BROKER_TYPE="data_broker_type";
	public static final String DEFAULT="Default";
	public static final String DATA_BROKER="DataBroker";
	public static final String CSV_FILE_NAME="CSV_File";
	public static final String TARGET_SYSTEM_URL="target_system_url";
	public static final String LOCAL_SYSTEM_DATA_FILE_PATH="local_system_data_file_path";
	public static final String FIRST_ROW="first_row";
	public static final String CSV_FILE_FIELD_SEPARATOR="csv_file_field_separator";
	public static final String MAP_INPUTS="map_inputs";
	public static final String INPUT_FIELD="input_field";
	public static final String TYPE="type";
	public static final String CHECKED="checked";
	public static final String MAPPED_TO_FIELD="mapped_to_field";
	public static final String MAP_OUTPUTS="map_outputs";
	public static final String OUTPUT_FIELD="output_field";
	public static final String TAG="tag";
	public static final String TYPE_AND_ROLE_HIERARCHY_LIST="type_and_role_hierarchy_list";
	public static final String ROLE="role";
	public static final String VM_CREATION_ERROR="role";
	/* JUnit Test Constants*/
	public static final String TEST_CONTAINER_NAME_ADDER="Adder1";
	public static final String TEST_IP_ADDER="11.11.10.90";
	public static final String TEST_PORT_ADDER="8557";
	public static final String TEST_CLIENT="testclient";
	public static final String TEST_TENANT="testtenant";
	public static final String TEST_KEY="testkey";
	public static final String TEST_SUBSCRIPTION_KEY="testsubscriptionKey";
	public static final String TEST_RG_NAME="testrg";
	public static final String TEST_ACR_NAME="testacr";
	public static final String TEST_STORAGE_ACCOUNT="teste6";
	public static final String TEST_IMAGETAG="newadder";
	public static final String TEST_SOLUTION_ID="02eab846-2bd0-4cfe-8470-9fc69";
	public static final String TEST_SOLUTIONREVISION_ID="a9e68bc6-f4b4-41c6-ae8e";
	public static final String TEST_USER_ID="0505e537-ce79-4b1f-bf43";
	public static final String TEST_HOST="8557";
	public static final String TEST_PORT="10.21.13.63";
	public static final String TEST_CONFIG="config";
	public static final String TEST_BLUEPRINTIP = "11.11.10.90";
	public static final String TEST_BLUEPRINTPORT = "8556";
	public static final String TEST_AZUREVMIP = "11.11.10.90";
	public static final String TEST_BLUEPRINT_NAME = "bluePrint";
	public static final String TEST_BLUEPRINT_VERSION = "1";
	public static final String TEST_IMAGE = "newadder1:1";
	public static final String TEST_COMPONENT_NAME = "testName";
	public static final String TEST_VERSION = "1";
	public static final String TEST_BLUEPRINT_FILE = "blueprint.json";
	public static final String TEST_BLUEPRINT_OLD_FILE = "blueprint2.json";
	

}
