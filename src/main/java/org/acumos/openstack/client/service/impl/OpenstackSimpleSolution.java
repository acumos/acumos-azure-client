package org.acumos.openstack.client.service.impl;


import java.util.List;
import java.util.Map;

import org.acumos.openstack.client.transport.OpenstackDeployBean;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.compute.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openstack4j.model.compute.Address;
import java.util.Iterator;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.common.ActionResponse;

public class OpenstackSimpleSolution implements Runnable{
	Logger logger = LoggerFactory.getLogger(OpenstackSimpleSolution.class);
	
	private OSClientV3 os;
	private String flavourName;
	private String  securityGropName;
	private OpenstackDeployBean auth;
	
	public OpenstackSimpleSolution(OSClientV3 os,String flavourName,String  securityGropName,OpenstackDeployBean auth){
		this.os = os;
		this.flavourName = flavourName;
		this.securityGropName = securityGropName;
		this.auth = auth;
	}
	
	public void run() {
		String serverId="";
		String flavourId="";
		String securityGropId="";
		String imageId="";
		String fixedAdd="";
		try{
			logger.debug("flavourName==============>"+flavourName);
			List< ? extends Flavor> flavourList= os.compute().flavors().list();
			logger.debug(" flavourList==========>"+flavourList.size());
			
			if(flavourList!=null && flavourList.size() >0){
				for(Flavor fl:flavourList){
					logger.debug("Flavour Id==========>"+fl.getId());
					String idVal=fl.getId();
					logger.debug("Flavour Name========>"+fl.getName());
					if(fl.getName()!=null && fl.getName().equalsIgnoreCase(flavourName)){
						flavourId=idVal;
						logger.debug("Flavour Name========>"+fl.getName()+"==flavourId=="+flavourId);	
					}
				}
			}
		  logger.debug("==flavourId======>"+flavourId);
		  logger.debug("==Start Security group part==securityGropName===>"+securityGropName);
		  List<? extends SecGroupExtension> sg = os.compute().securityGroups().list();
			for(SecGroupExtension sgt: sg){
				logger.debug("security.getName()====="+sgt.getName());
				logger.debug("security.ID====="+sgt.getId());
				logger.debug("security.Rules====="+sgt.getRules());
				if(sgt.getName()!=null && sgt.getName().equalsIgnoreCase(securityGropName)){
					securityGropId=sgt.getId();
				}
			}
		 logger.debug("== ==securityGropId===>"+securityGropId);	
		 
		 logger.debug("==Start Image part==>");
		 List<? extends Image> images = os.compute().images().list();
		 logger.debug("Images list==============>" + images);
		 Image image = images.get(0);
		 imageId=image.getId();
		 logger.debug("Image id==================>" + imageId);
		 
		 logger.debug("==Start Creating vm=========>");
		 Server server = os.compute().servers()
					.boot(Builders.server()
							.name(auth.getVmName())
							.flavor(flavourId)
							.image(image.getId())
							.keypairName(auth.getKeyName())
							.addSecurityGroup(securityGropId)
							.build());
		 serverId=server.getId();
		 logger.debug("==End Creating vm=========>"+serverId);
		 logger.debug("==Start Adding floating point ip ====================>");
		 int limit=5;
		 for(int i=0;i<limit;i++){
			   logger.debug("networking ##############################Start.."+i);
				Server server2 = os.compute().servers().get(serverId);
				 logger.debug("...Address.server2.."+server2.getAddresses());
				//listQuery(os);
				if(server2.getAddresses()!=null && server2.getAddresses().getAddresses()!=null){
				    logger.debug("...Address.server......."+server2.getAddresses());
					Map<String,List<? extends Address >> addressmap=server2.getAddresses().getAddresses();
					logger.debug("...addressmap...."+addressmap.size());
					 if(!addressmap.isEmpty()){
					    Iterator it = addressmap.entrySet().iterator();
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        logger.debug("key pair=============:"+pair.getKey());
					        logger.debug("value pair"+ pair.getValue()); 
					        List<? extends Address> listAddress=(List<? extends Address>)pair.getValue();
					        logger.debug("listAddress===="+listAddress);
					        if(listAddress.size() >0){
					        	Address add=(Address)listAddress.get(0);
					        	logger.debug("====listAddress.get(0)====>"+listAddress.get(0));
					        	logger.debug("Address===="+add.getAddr());
					        	logger.debug("MacAddr===="+add.getMacAddr());
					        	logger.debug("Version===="+add.getVersion());
					        	fixedAdd=add.getAddr();
					        }
					    }
					    logger.debug("===============Breaking the loop=================");
					    break;
					 }else{
						 logger.debug("============I am in first Sleep==================");
						 Thread.sleep(30000);
					 }
				}else{
					logger.debug("==============I am in Second Sleep===============");
				Thread.sleep(30000);
				}
			}
		 logger.debug("===========fixedAdd=============="+fixedAdd);
		 if(fixedAdd!=null && !"".equals(fixedAdd)){
			  Server server3 = os.compute().servers().get(serverId);
			  FloatingIP ip = os.compute().floatingIps().allocateIP("public");
			  String fIp=ip.getFloatingIpAddress();
			  logger.debug("ip.getFloatingIpAddress()......."+fIp);
			  ActionResponse r = os.compute().floatingIps().addFloatingIP(server3, fixedAdd, fIp);
			  logger.debug("ActionResponser........"+r.isSuccess());
			  /*logger.debug("Security Group code start==================");
			  List<? extends SecurityGroup> securityList=server3.getSecurityGroups();
				System.out.print("securityList====="+securityList.size());
				for(SecurityGroup security: securityList){
					System.out.println("security.getName()====="+security.getName());
					//System.out.print("security.ID====="+security.);
				}*/
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}

}
