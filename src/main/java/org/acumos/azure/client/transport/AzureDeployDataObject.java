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

package org.acumos.azure.client.transport;

public class AzureDeployDataObject {

	private String client;
	private String tenant;
	private String key;
	private String subscriptionKey;
	private String rgName;
	private String acrName;
	private String solutionId;
	private String storageAccount;
	//private String solutionVersion;
	/*private String imagetag;*/
	private String solutionRevisionId;
	private String userId;
	private String urlAttribute;
	private String jsonPosition;
	private String jsonMapping;
	
	private String userName;
	private String password;
	private String host;
	private String port;
	

	
	
	public String getUrlAttribute() {
		return urlAttribute;
	}

	public void setUrlAttribute(String urlAttribute) {
		this.urlAttribute = urlAttribute;
	}

	public String getJsonPosition() {
		return jsonPosition;
	}

	public void setJsonPosition(String jsonPosition) {
		this.jsonPosition = jsonPosition;
	}

	public String getJsonMapping() {
		return jsonMapping;
	}

	public void setJsonMapping(String jsonMapping) {
		this.jsonMapping = jsonMapping;
	}

	public String getSolutionRevisionId() {
		return solutionRevisionId;
	}

	public void setSolutionRevisionId(String solutionRevisionId) {
		this.solutionRevisionId = solutionRevisionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * @return the tenant
	 */
	public String getTenant() {
		return tenant;
	}

	/**
	 * @param tenant
	 *            the tenant to set
	 */
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the subscriptionKey
	 */
	public String getSubscriptionKey() {
		return subscriptionKey;
	}

	/**
	 * @param subscriptionKey
	 *            the subscriptionKey to set
	 */
	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	/**
	 * @return the rgName
	 */
	public String getRgName() {
		return rgName;
	}

	/**
	 * @param rgName
	 *            the rgName to set
	 */
	public void setRgName(String rgName) {
		this.rgName = rgName;
	}

	/**
	 * @return the acrName
	 */
	public String getAcrName() {
		return acrName;
	}

	/**
	 * @param acrName
	 *            the acrName to set
	 */
	public void setAcrName(String acrName) {
		this.acrName = acrName;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	/**
	 * @return the storageAccount
	 */
	public String getStorageAccount() {
		return storageAccount;
	}

	/**
	 * @param storageAccount
	 *            the storageAccount to set
	 */
	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}
    
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	
	/*public String getSolutionVersion() {
		return solutionVersion;
	}

	public void setSolutionVersion(String solutionVersion) {
		this.solutionVersion = solutionVersion;
	}*/

	/*public String getImagetag() {
		return imagetag;
	}
	public void setImagetag(String imagetag) {
		this.imagetag = imagetag;
	}*/

	@Override
	public String toString() {
		return "AzureDeployDataObject [client=" + client + ", tenant=" + tenant + ", key=" + key + ", subscriptionKey="
				+ subscriptionKey + ", rgName=" + rgName + ", acrName=" + acrName + ", solutionId=" + solutionId + ", storageAccount=" + storageAccount
				+ ", solutionRevisionId=" + solutionRevisionId +", userId=" + userId + "]";
	}

}
