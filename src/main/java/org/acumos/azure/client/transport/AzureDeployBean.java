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

public class AzureDeployBean {
	private String client;
	private String tenant;
	private String key;
	private String subscriptionKey;
	private String rgName;
	private String acrName;
	private String storageAccount;
	private String imagetag;
	private String solutionId;
	private String solutionRevisionId;
	private String userId;
	
	
	
	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
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
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSubscriptionKey() {
		return subscriptionKey;
	}
	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}
	public String getRgName() {
		return rgName;
	}
	public void setRgName(String rgName) {
		this.rgName = rgName;
	}
	public String getAcrName() {
		return acrName;
	}
	public void setAcrName(String acrName) {
		this.acrName = acrName;
	}
	public String getStorageAccount() {
		return storageAccount;
	}
	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}
	public String getImagetag() {
		return imagetag;
	}
	public void setImagetag(String imagetag) {
		this.imagetag = imagetag;
	}
	
	@Override
	public String toString() {
		return "AzureDeployDataObject [client=" + client + ", tenant=" + tenant + ", key=" + key + ", subscriptionKey="
				+ subscriptionKey + ", rgName=" + rgName + ", acrName=" + acrName + ", solutionId=" + solutionId + ", storageAccount=" + storageAccount
				+ ", solutionRevisionId=" + solutionRevisionId +", userId=" + userId + ", imagetag;=" + imagetag +"]";
	}

}
