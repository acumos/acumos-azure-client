package org.acumos.azure.client.transport;

/**
 *
 */
public class AzureDeployDataObject {

	private String client;
	private String tenant;
	private String key;
	private String subscriptionKey;
	private String rgName;
	private String acrName;
	private String solutionId;
	private String storageAccount;
	private String imagetag;

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
	 * @param storageAccount the storageAccount to set
	 */
	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}

	public String getImagetag() {
		return imagetag;
	}

	/**
	 * @param imagetag
	 */
	public void setImagetag(String imagetag) {
		this.imagetag = imagetag;
	}

	@Override
	public String toString() {
		return "AzureDeployDataObject [client=" + client + ", tenant=" + tenant + ", key=" + key + ", subscriptionKey="
				+ subscriptionKey + ", rgName=" + rgName + ", acrName=" + acrName + ", solutionId=" + solutionId + ", storageAccount=" + storageAccount
				+ ", imagetag=" + imagetag + "]";
	}

}
