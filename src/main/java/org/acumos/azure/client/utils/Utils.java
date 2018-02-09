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

import com.google.common.base.Joiner;
import com.microsoft.azure.CloudException;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.appservice.AppServiceCertificateOrder;
import com.microsoft.azure.management.appservice.AppServiceDomain;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.AppSetting;
import com.microsoft.azure.management.appservice.ConnectionString;
import com.microsoft.azure.management.appservice.Contact;
import com.microsoft.azure.management.appservice.HostNameBinding;
import com.microsoft.azure.management.appservice.HostNameSslState;
import com.microsoft.azure.management.appservice.PublishingProfile;
import com.microsoft.azure.management.appservice.SslState;
import com.microsoft.azure.management.appservice.WebAppBase;
import com.microsoft.azure.management.batch.Application;
import com.microsoft.azure.management.batch.ApplicationPackage;
import com.microsoft.azure.management.batch.BatchAccount;
import com.microsoft.azure.management.batch.BatchAccountKeys;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.ContainerService;
import com.microsoft.azure.management.compute.ContainerServiceOchestratorTypes;
import com.microsoft.azure.management.compute.DataDisk;
import com.microsoft.azure.management.compute.ImageDataDisk;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineCustomImage;
import com.microsoft.azure.management.compute.VirtualMachineExtension;
import com.microsoft.azure.management.containerregistry.Registry;
import com.microsoft.azure.management.containerregistry.implementation.RegistryListCredentials;
import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.dns.ARecordSet;
import com.microsoft.azure.management.dns.AaaaRecordSet;
import com.microsoft.azure.management.dns.CNameRecordSet;
import com.microsoft.azure.management.dns.DnsZone;
import com.microsoft.azure.management.dns.MXRecordSet;
import com.microsoft.azure.management.dns.MxRecord;
import com.microsoft.azure.management.dns.NSRecordSet;
import com.microsoft.azure.management.dns.PtrRecordSet;
import com.microsoft.azure.management.dns.SoaRecord;
import com.microsoft.azure.management.dns.SoaRecordSet;
import com.microsoft.azure.management.dns.SrvRecord;
import com.microsoft.azure.management.dns.SrvRecordSet;
import com.microsoft.azure.management.dns.TxtRecord;
import com.microsoft.azure.management.dns.TxtRecordSet;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryApplication;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryGroup;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryObject;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryUser;
import com.microsoft.azure.management.graphrbac.RoleAssignment;
import com.microsoft.azure.management.graphrbac.RoleDefinition;
import com.microsoft.azure.management.graphrbac.ServicePrincipal;
import com.microsoft.azure.management.graphrbac.implementation.PermissionInner;
import com.microsoft.azure.management.keyvault.AccessPolicy;
import com.microsoft.azure.management.keyvault.Vault;
import com.microsoft.azure.management.network.ApplicationGateway;
import com.microsoft.azure.management.network.ApplicationGatewayBackend;
import com.microsoft.azure.management.network.ApplicationGatewayBackendAddress;
import com.microsoft.azure.management.network.ApplicationGatewayBackendHttpConfiguration;
import com.microsoft.azure.management.network.ApplicationGatewayFrontend;
import com.microsoft.azure.management.network.ApplicationGatewayIPConfiguration;
import com.microsoft.azure.management.network.ApplicationGatewayListener;
import com.microsoft.azure.management.network.ApplicationGatewayProbe;
import com.microsoft.azure.management.network.ApplicationGatewayRequestRoutingRule;
import com.microsoft.azure.management.network.ApplicationGatewaySslCertificate;
import com.microsoft.azure.management.network.EffectiveNetworkSecurityRule;
import com.microsoft.azure.management.network.FlowLogSettings;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerBackend;
import com.microsoft.azure.management.network.LoadBalancerFrontend;
import com.microsoft.azure.management.network.LoadBalancerHttpProbe;
import com.microsoft.azure.management.network.LoadBalancerInboundNatPool;
import com.microsoft.azure.management.network.LoadBalancerInboundNatRule;
import com.microsoft.azure.management.network.LoadBalancerPrivateFrontend;
import com.microsoft.azure.management.network.LoadBalancerProbe;
import com.microsoft.azure.management.network.LoadBalancerPublicFrontend;
import com.microsoft.azure.management.network.LoadBalancerTcpProbe;
import com.microsoft.azure.management.network.LoadBalancingRule;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NetworkSecurityRule;
import com.microsoft.azure.management.network.NetworkWatcher;
import com.microsoft.azure.management.network.NextHop;
import com.microsoft.azure.management.network.PacketCapture;
import com.microsoft.azure.management.network.PacketCaptureFilter;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.network.SecurityGroupNetworkInterface;
import com.microsoft.azure.management.network.SecurityGroupView;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.network.Topology;
import com.microsoft.azure.management.network.TopologyAssociation;
import com.microsoft.azure.management.network.TopologyResource;
import com.microsoft.azure.management.network.VerificationIPFlow;
import com.microsoft.azure.management.network.implementation.SecurityRuleInner;
import com.microsoft.azure.management.redis.RedisAccessKeys;
import com.microsoft.azure.management.redis.RedisCache;
import com.microsoft.azure.management.redis.RedisCachePremium;
import com.microsoft.azure.management.redis.ScheduleEntry;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.management.search.AdminKeys;
import com.microsoft.azure.management.search.QueryKey;
import com.microsoft.azure.management.search.SearchService;
import com.microsoft.azure.management.servicebus.AccessRights;
import com.microsoft.azure.management.servicebus.AuthorizationKeys;
import com.microsoft.azure.management.servicebus.NamespaceAuthorizationRule;
import com.microsoft.azure.management.servicebus.Queue;
import com.microsoft.azure.management.servicebus.QueueAuthorizationRule;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.ServiceBusSubscription;
import com.microsoft.azure.management.servicebus.Topic;
import com.microsoft.azure.management.servicebus.TopicAuthorizationRule;
import com.microsoft.azure.management.sql.ElasticPoolActivity;
import com.microsoft.azure.management.sql.ElasticPoolDatabaseActivity;
import com.microsoft.azure.management.sql.SqlDatabase;
import com.microsoft.azure.management.sql.SqlElasticPool;
import com.microsoft.azure.management.sql.SqlFirewallRule;
import com.microsoft.azure.management.sql.SqlServer;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.management.storage.StorageAccountKey;
import com.microsoft.azure.management.trafficmanager.TrafficManagerAzureEndpoint;
import com.microsoft.azure.management.trafficmanager.TrafficManagerExternalEndpoint;
import com.microsoft.azure.management.trafficmanager.TrafficManagerNestedProfileEndpoint;
import com.microsoft.azure.management.trafficmanager.TrafficManagerProfile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Common utils for Azure management samples.
 */

public final class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Print virtual machine info.
	 *
	 * @param resource
	 *            a virtual machine
	 */
	
	public static void print(NetworkInterface resource) {
		StringBuilder info = new StringBuilder();
		info.append("NetworkInterface: ").append(resource.id()).append("Name: ").append(resource.name())
				.append("\n\tResource group: ").append(resource.resourceGroupName()).append("\n\tRegion: ")
				.append(resource.region()).append("\n\tTags: ").append(resource.tags())
				.append("\n\tInternal DNS name label: ").append(resource.internalDnsNameLabel())
				.append("\n\tInternal FQDN: ").append(resource.internalFqdn())
				.append("\n\tInternal domain name suffix: ").append(resource.internalDomainNameSuffix())
				.append("\n\tNetwork security group: ").append(resource.networkSecurityGroupId())
				.append("\n\tApplied DNS servers: ").append(resource.appliedDnsServers().toString())
				.append("\n\tDNS server IPs: ");

		// Output dns servers
		for (String dnsServerIp : resource.dnsServers()) {
			info.append("\n\t\t").append(dnsServerIp);
		}

		info.append("\n\tIP forwarding enabled? ").append(resource.isIPForwardingEnabled())
				.append("\n\tAccelerated networking enabled? ").append(resource.isAcceleratedNetworkingEnabled())
				.append("\n\tMAC Address:").append(resource.macAddress()).append("\n\tPrivate IP:")
				.append(resource.primaryPrivateIP()).append("\n\tPrivate allocation method:")
				.append(resource.primaryPrivateIPAllocationMethod()).append("\n\tPrimary virtual network ID: ")
				.append(resource.primaryIPConfiguration().networkId()).append("\n\tPrimary subnet name:")
				.append(resource.primaryIPConfiguration().subnetName());

		log.debug(info.toString());
	}
		
		public static void print(Network resource) {
		StringBuilder info = new StringBuilder();

		info.append("Network: ").append(resource.id()).append("Name: ").append(resource.name())
				.append("\n\tResource group: ").append(resource.resourceGroupName()).append("\n\tRegion: ")
				.append(resource.region()).append("\n\tTags: ").append(resource.tags()).append("\n\tAddress spaces: ")
				.append(resource.addressSpaces()).append("\n\tDNS server IPs: ").append(resource.dnsServerIPs());

		// Output subnets
		for (Subnet subnet : resource.subnets().values()) {
			info.append("\n\tSubnet: ").append(subnet.name()).append("\n\t\tAddress prefix: ")
					.append(subnet.addressPrefix());
			NetworkSecurityGroup subnetNsg = subnet.getNetworkSecurityGroup();
			if (subnetNsg != null) {
				info.append("\n\t\tNetwork security group: ").append(subnetNsg.id());
			}
		}

		log.debug(info.toString());
	}
	
	public static String getSecondaryServicePrincipalClientID(String envSecondaryServicePrincipal) throws Exception {
		Properties authSettings = new Properties();
		FileInputStream credentialsFileStream = new FileInputStream(new File(envSecondaryServicePrincipal));
		authSettings.load(credentialsFileStream);
		credentialsFileStream.close();

		return authSettings.getProperty("client");
	}
	
	public static String getSecondaryServicePrincipalSecret(String envSecondaryServicePrincipal) throws Exception {
		Properties authSettings = new Properties();
		FileInputStream credentialsFileStream = new FileInputStream(new File(envSecondaryServicePrincipal));
		authSettings.load(credentialsFileStream);
		credentialsFileStream.close();

		return authSettings.getProperty("key");
	}
	
	public static void print(Registry azureRegistry) {
		StringBuilder info = new StringBuilder();

		RegistryListCredentials acrCredentials = azureRegistry.listCredentials();
		info.append("Azure Container Registry: ").append(azureRegistry.id()).append("\n\tName: ")
				.append(azureRegistry.name()).append("\n\tServer Url: ").append(azureRegistry.loginServerUrl())
				.append("\n\tUser: ").append(acrCredentials.username()).append("\n\tFirst Password: ")
				.append(acrCredentials.passwords().get(0).value()).append("\n\tSecond Password: ")
				.append(acrCredentials.passwords().get(1).value());
		log.debug(info.toString());
	}
}
