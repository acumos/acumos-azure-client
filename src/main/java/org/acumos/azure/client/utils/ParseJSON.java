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


import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.transport.DeploymentBean;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJSON {
	
	Logger log =LoggerFactory.getLogger(ParseJSON.class);
	public  HashMap<String,String> parseJsonFile(String jsonFileName)throws  Exception{
		log.debug("<----------Start parseJsonFile in ParseJSON--------------------jsonFileName------->"+jsonFileName);
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		try
		{
		 
        Object obj = new JSONParser().parse(new FileReader(jsonFileName));
        JSONObject jo = (JSONObject) obj;
        JSONArray nodes = (JSONArray) jo.get("nodes");
        if(nodes!=null && !nodes.isEmpty()){
        	Iterator itr3 = nodes.iterator();
	        int nodeCount=0; 
	        while (itr3.hasNext()) 
	        {
	        	Iterator<Map.Entry> itr4 = ((Map) itr3.next()).entrySet().iterator();
	        	log.debug("Nodes-->"+ ++nodeCount);
	        	String containerName="";
	        	String imageName="";
	            while (itr4.hasNext()) {
	                Map.Entry pair = itr4.next();
	                String key=(String)pair.getKey();
	                String val=(String)pair.getValue().toString();
	                if(key!=null && key.equalsIgnoreCase("depends_on")){
	                	jsonArrayParse(pair.getValue());
	                }if(key!=null && key.equalsIgnoreCase("container_name")){
	                	containerName=val;
	                }else{
	                	log.debug("-key->"+pair.getKey() + " --value-->" + pair.getValue());
	                }
	                if(key!=null && key.equalsIgnoreCase("image")){
	                	imageName=val;
	                	list.add(val);
	                 }
	                if(containerName!=null && imageName!=null && !"".equals(containerName) && !"".equals(imageName)){
	                	imageMap.put(imageName, containerName);
	                }
	                
	            }
	        }
         }
        }catch(Exception e){
        	log.error("<----Exception in method parseJsonFile of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------End parseJsonFile in ParseJSON---------------------imageMap---->"+imageMap);
		return imageMap;	
	}
	
	public  void jsonArrayParse(Object obj){
		JSONArray jsonArr = (JSONArray) obj;
		Iterator itr = jsonArr.iterator();
		Iterator<Map.Entry> itr1=null;
		 while (itr.hasNext()) {
			 itr1 = ((Map) itr.next()).entrySet().iterator();
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                log.debug("-->"+pair.getKey() + " : " + pair.getValue());
	            }
         }
	}
	
	public  Blueprint jsonFileToObject(String jsonFileName,DataBrokerBean dataBrokerBean)throws  Exception{
		log.debug("<----------Start jsonFileToObject in -----------------jsonFileName---------->"+jsonFileName);
		ArrayList<String> list=new ArrayList<String>();	
		Blueprint blueprint=new Blueprint();
		ObjectMapper mapper = new ObjectMapper();
		try
		{
		Object obj = new JSONParser().parse(new FileReader(jsonFileName));
		
        JSONObject jo = (JSONObject) obj;
        String prettyJSONString = jo.toString();
        String name = (String) jo.get("name");
        String version = (String) jo.get("version");
        blueprint.setName(name);
        blueprint.setVersion(version);
        Iterator<Map.Entry> itr1=null;
        Orchestrator orchestratorBean=new Orchestrator();
        Map orchestrator = ((Map)jo.get("orchestrator"));
        if(orchestrator!=null){
	        itr1 = orchestrator.entrySet().iterator();
	        while (itr1.hasNext()) {
	            Map.Entry pair = itr1.next();
	            String key=(String)pair.getKey();
	            String value=(String)pair.getValue();
	            log.debug("-->"+pair.getKey() + " : " + pair.getValue());
	            if(key!=null && key.equalsIgnoreCase("name")){
	            	orchestratorBean.setName(value);
	             }
	            if(key!=null && key.equalsIgnoreCase("version")){
	            	orchestratorBean.setVersion(value);
	             }
	            if(key!=null && key.equalsIgnoreCase("image")){
	            	orchestratorBean.setImage(value);
	             }
	           }
        }
        
        JSONArray inputOperation = (JSONArray) jo.get("input_operation_signatures");
        ArrayList<OperationSignature> operationList=new ArrayList<OperationSignature>();
        if(inputOperation!=null){
        	log.debug("input_operation_signatures-->");
	        Iterator itr2 = inputOperation.iterator();
	        while (itr2.hasNext()) 
	        {
	        	OperationSignature operationSignature=new OperationSignature();
	            itr1 = ((Map) itr2.next()).entrySet().iterator();
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                String key=(String)pair.getKey();
	                String value=(String)pair.getValue();
	                log.debug("-->"+pair.getKey() + " : " + pair.getValue());
	                if(key!=null && key.equalsIgnoreCase("operation")){
	                	operationSignature.setOperationName(value);
	                }
	               
	            }
	            operationList.add(operationSignature);
	        }
        }
        JSONArray nodes = (JSONArray) jo.get("nodes");
        ArrayList<Node> nodeList=new ArrayList<Node>();
        if(nodes!=null ){
        	Iterator itr3 = nodes.iterator();
        	int nodeCount=0; 
	        while (itr3.hasNext()) 
	        {
	        	Node node=new Node();
	            itr1 = ((Map) itr3.next()).entrySet().iterator();
	            log.debug("Nodes-->"+ ++nodeCount);
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                String key=(String)pair.getKey();
	                if(key!=null && key.equalsIgnoreCase("depends_on")){
	                	if(pair.getValue()!=null){
	                		ArrayList<Component> listComponent=jsonArrayParseObject(pair.getValue());
	                	}
	                }else{
	                	log.debug("-->"+pair.getKey() + " : " + pair.getValue());
	                if(key!=null && key.equalsIgnoreCase("container_name")){
	                	node.setContainerName((String)pair.getValue());
	                  }
	                if(key!=null && key.equalsIgnoreCase("image")){
	                	node.setImage((String)pair.getValue());
	                  }
	              //Data Broker code
					JSONObject dataBrokerObject=null;
					if(key != null && key.equalsIgnoreCase("data_broker_map")){
                    	 dataBrokerObject = (JSONObject)pair.getValue();
                    	 if(dataBrokerObject!=null  ){
                    		 if(dataBrokerObject.get("data_broker_type")!=null){
                    			 String dataBrokerType=(String)dataBrokerObject.get("data_broker_type");
                    			 log.debug("dataBrokerType-->"+dataBrokerType);
                    			 if(dataBrokerType!=null && dataBrokerType.equalsIgnoreCase("CSV_File")){
                    				 if(dataBrokerBean!=null){
                    					 node.setDataBrokerMap(dataBrokerBean.getDataBrokerMap()); 
                    				 }
                    			 }
                    			 
                    		 }
                    	 } 
					}
					//End
	                }
	            }
	            nodeList.add(node);
	        }
	         
        }
        blueprint.setNodes(nodeList);
        log.debug("blueprint==="+ mapper.writeValueAsString(blueprint)+"===="+blueprint.toString());
		}catch(Exception e){
			log.error("<----Exception in method jsonFileToObject of ParseJSON----------->"+e.getMessage());
			throw new Exception(e.getMessage());
       }
		log.debug("<----------End jsonFileToObject in ParseJSON---------------------list---->"+list);
		return blueprint;	
	}

	/**
	 * This method returns probe indicator
	 * @param jo
	 * @return
	 */
	private String getProbeIndicator(JSONObject jo) {
		log.debug("<----------Start getProbeIndicator in ParseJSON------------------->");
		JSONArray probeIndicator = (JSONArray) jo.get("probeIndicator");
		if(probeIndicator == null){
			probeIndicator = (JSONArray) jo.get("probeIndocator");
		}		
		Iterator itr = probeIndicator.iterator();
		Iterator<Map.Entry> itr1=null;
		String value = null;
		 while (itr.hasNext()) {
			 itr1 = ((Map) itr.next()).entrySet().iterator();
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                value = (String)pair.getValue();
	            }
		 }
	   log.debug("<----------End getProbeIndicator in ParseJSON-------------value------>"+value);
	  return value;	
	}

	public  ArrayList<Component> jsonArrayParseObject(Object obj){
		log.debug("<----------Start jsonArrayParseObject in ParseJSON------------------->");
		JSONArray jsonArr = (JSONArray) obj;
		Iterator itr = jsonArr.iterator();
		ArrayList<Component> listComponent=new ArrayList<Component>();
		Iterator<Map.Entry> itr1=null;
		 while (itr.hasNext()) {
			 itr1 = ((Map) itr.next()).entrySet().iterator();
			 Component component=new Component();
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                String key=(String)pair.getKey();
	                
	                if(key!=null && key.equalsIgnoreCase("operation_signature")){
	                	JSONObject objVar =(JSONObject)pair.getValue();
	                	if(objVar!=null){
	                		String operation=(String)objVar.get("operation");
	                		OperationSignature opr=new OperationSignature();
	                		opr.setOperationName(operation);
	                		log.debug("=======operation==========="+operation);
	                		component.setOperationSignature(opr);
	                	}
	                }
	                if(key!=null && key.equalsIgnoreCase("name")){
	                	component.setName((String)pair.getValue());
	                }
	                
	            }
	            listComponent.add(component); 
         }
	  log.debug("<----------End jsonArrayParseObject in ParseJSON-------------listComponent------>"+listComponent);	 
	  return listComponent;	 
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public ArrayList<OperationSignatureList>  jsonArrayParseObjectProb(Object obj, ArrayList<OperationSignatureList> listComponent) {
		log.debug("<----------Start jsonArrayParseObjectProb in ParseJSON------------------->");
		JSONArray jsonArr = (JSONArray) obj;
		Iterator itr = jsonArr.iterator();
		log.debug("obj========================" + obj);
		OperationSignatureList oprListObj=listComponent.get(0);
		ArrayList<ConnectedTo> connectedList=new ArrayList<ConnectedTo>();
		Iterator<Map.Entry> itr1 = null;
		Iterator<Map.Entry> itr3 = null;
		
		while (itr.hasNext()) {
			itr1 = ((Map) itr.next()).entrySet().iterator();
			
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				String key = (String) pair.getKey();
				log.debug("Key========================" + key);
				ConnectedTo connectedTo=null;
				if (key != null && key.equalsIgnoreCase("connected_to")) {
					JSONArray connArr = (JSONArray) pair.getValue();
					Iterator conItr = connArr.iterator();
					connectedTo=new ConnectedTo();
					while (conItr.hasNext()) {

						itr3 = ((Map) conItr.next()).entrySet().iterator();
						while (itr3.hasNext()) {
							Map.Entry pair1 = itr3.next();
							String connectedKey = (String) pair1.getKey();
							if (connectedKey != null && connectedKey.equalsIgnoreCase("container_name")) {
								String containerKey = (String) pair1.getKey();
								String containerName = (String) pair1.getValue();
								connectedTo.setContainerName(containerName);
							}

							if (connectedKey != null && connectedKey.equalsIgnoreCase("operation_signature")) {
								JSONObject objVar = (JSONObject) pair1.getValue();
								OperationSignature opr = new OperationSignature();
								if (objVar != null) {
									String operation = (String) objVar.get("operation_name");
									opr.setOperationName(operation);
									log.debug("=======operation===========" + operation);
									connectedTo.setOperationSignature(opr);
								}
							}
						}
						
					}
					if(connectedTo!=null){
						connectedList.add(connectedTo);
					}
					
				}
                if(connectedList!=null && connectedList.size() > 0){
                	oprListObj.setConnectedTo(connectedList);
                }
                if (key != null && key.equalsIgnoreCase("operation_signature")) {
                	JSONObject objVar = (JSONObject) pair.getValue();
					OperationSignature oprSignature = new OperationSignature();
					if (objVar != null) {
						String oprName = (String) objVar.get("operation_name");
						if(oprName!=null && !"".equals(oprName)){
							oprSignature.setOperationName(oprName);	
						}
						
						String inputMessage = (String) objVar.get("input_message_name");
						if(inputMessage!=null && !"".equals(inputMessage)){
							oprSignature.setInputMessageName(inputMessage);
						}
						
						String outputMessage = (String) objVar.get("output_message_name");
						if(outputMessage!=null && !"".equals(outputMessage)){
							oprSignature.setOutputMessageName(outputMessage);
						}
					}
					oprListObj.setOperationSignature(oprSignature);
                }
				

			}
			
			
			
		}
		
		
		
		log.debug("<----------End jsonArrayParseObjectProb in ParseJSON-----------listComponent-------->"+listComponent);
		return listComponent;
	}

	
public LinkedList<String> getSequenceFromJSON(String jsonFileName)throws  Exception{
	log.debug("<----------Start getSequenceFromJSON in ParseJSON-----------jsonFileName-------->"+jsonFileName);

		String contentString="";
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		LinkedList<String> linkedList=new LinkedList<String>();
		try
		{
			NodeTree<String> root = new NodeTree<String>("BluePrintContainer");
        Object obj = new JSONParser().parse(new FileReader(jsonFileName));
        JSONObject jo = (JSONObject) obj;
        JSONArray nodes = (JSONArray) jo.get("nodes");
        if(nodes!=null && !nodes.isEmpty()){
        	Iterator itr3 = nodes.iterator();
	        int nodeCount=0; 
	        while (itr3.hasNext()) 
	        {
	        	Iterator<Map.Entry> itr4 = ((Map) itr3.next()).entrySet().iterator();
	        	Iterator<Map.Entry> itr5 = itr4;
	        	String containerName="";
	        	String imageName="";
	        	
	        	String contName="test";
	        	NodeTree<String> testNode=new NodeTree<String>(contName);
	        	log.debug("Second while");
	            while (itr4.hasNext()) {
	                Map.Entry pair = itr4.next();
	                String key=(String)pair.getKey();
	                String val=(String)pair.getValue().toString();
	                if(key!=null && key.equalsIgnoreCase("depends_on")){
	                	sequenceJsonParse(pair.getValue(),testNode,root);
	                }if(key!=null && key.equalsIgnoreCase("container_name")){
	                	containerName=val;
	                	contName=val;
	                }else{
	                	log.debug("------------------------key->"+pair.getKey() );
	                }
	                if(key!=null && key.equalsIgnoreCase("image")){
	                	imageName=val;
	                	list.add(val);
	                 }
	                if(containerName!=null && imageName!=null && !"".equals(containerName) && !"".equals(imageName)){
	                	imageMap.put(imageName, containerName);
	                }
	                
	            }
	            testNode.setData(contName);
	            NodeTree<String> searchNode=findDataInTree(root, contName);
	            if(searchNode!=null){
	            	NodeTree<String> parent=searchNode.getParent();
	            	String parentData=parent.getData();
	            	if (parent != null) {
	    				int index = parent.getChildren().indexOf(searchNode);
	    				parent.getChildren().remove(searchNode);
	    			 }
	            	NodeTree<String> parrentNode=findDataInTree(root, parentData);
	            	if(parrentNode!=null){
	            		parrentNode.addChild(testNode);
	            	}
	            }else{
	            	root.addChild(testNode);
	            }
	        }
         }
        log.debug("=======Print==================================");
        printTree(root, " ",linkedList);
        Collections.reverse(linkedList);
        log.debug("=======Print=======================linkedList==========="+linkedList);
        
        }catch(Exception e){
        	log.error("<----Exception in method getSequenceFromJSON of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------Start getSequenceFromJSON in ParseJSON--------------linkedList----->"+linkedList);
		return linkedList;	
	}


 public  void sequenceJsonParse(Object obj,NodeTree<String> newNode,NodeTree<String> rootNode){
	log.debug("<----------Start sequenceJsonParse in ParseJSON------------------->"); 
	JSONArray jsonArr = (JSONArray) obj;
	Iterator itr = jsonArr.iterator();
	Iterator<Map.Entry> itr1=null;
	 while (itr.hasNext()) {
		 itr1 = ((Map) itr.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                log.debug("-----------key value->"+pair.getKey() + " : " + pair.getValue());
                if(pair.getKey() !=null && pair.getKey().equals("name") && pair.getValue()!=null){
                	String data=String.valueOf(pair.getValue());
                	NodeTree<String> subNode=new NodeTree<String>(data);
                	 NodeTree<String> searchNode=findDataInTree(rootNode, data);
                	 if(searchNode!=null){
     	            	NodeTree<String> parent=searchNode.getParent();
     	            	String parentData=parent.getData();
     	            	if (parent != null) {
     	    				parent.getChildren().remove(searchNode);
     	    				newNode.addChild(searchNode);
     	    			 }
     	            	
     	            }else{
     	            	newNode.addChild(subNode);
     	            }
                }
                log.debug("-----key value-->"+pair.getKey() + " : " + pair.getValue());
            }
     }
	 log.debug("<----------End sequenceJsonParse in ParseJSON------------------->"); 	 
}


	public void sequenceJsonParseProbe(Object obj, NodeTree<String> newNode, NodeTree<String> rootNode) {
		log.debug("<----------Start sequenceJsonParseProbe in ParseJSON------------------->"); 
		JSONArray jsonArr = (JSONArray) obj;
		Iterator itr = jsonArr.iterator();
		Iterator<Map.Entry> itr1 = null;
		while (itr.hasNext()) {
			itr1 = ((Map) itr.next()).entrySet().iterator();
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				log.debug("--------------key value->" + pair.getKey() + " : " + pair.getValue());
				
				if (pair.getKey() != null && pair.getKey().equals("connected_to") && pair.getValue() != null) {
					
						JSONArray connArr = (JSONArray) pair.getValue();
						Iterator conItr = connArr.iterator();
						while (conItr.hasNext()) {
							String data = null;
							Iterator<Map.Entry> contrItr = ((Map) conItr.next()).entrySet().iterator();
							while (contrItr.hasNext()) {
								Map.Entry cntPair = contrItr.next();
								String connectedKey = (String) cntPair.getKey();
								if (connectedKey != null && connectedKey.equalsIgnoreCase("container_name")) {
									String containerKey = (String) cntPair.getKey();
									data = (String) cntPair.getValue();
									NodeTree<String> subNode = new NodeTree<String>(data);
									NodeTree<String> searchNode = findDataInTree(rootNode, data);
									if (searchNode != null) {
										NodeTree<String> parent = searchNode.getParent();
										String parentData = parent.getData();
										if (parent != null) {
											parent.getChildren().remove(searchNode);
											newNode.addChild(searchNode);
										}
										
									} else {
										newNode.addChild(subNode);
									}
								}
							}
					}
				}
					log.debug("------------key value-->" + pair.getKey() + " : " + pair.getValue());
			}
		}
			
		log.debug("<----------End sequenceJsonParseProbe in ParseJSON------------------->");					
	}

public  NodeTree<String> findDataInTree(NodeTree node, String searchQuery) {
	log.debug("<----------Start findDataInTree in ParseJSON------------------->");
	NodeTree<String> ss=null;
	 if(node.getData().equals(searchQuery)) {
		 log.debug("========node.getData()========="+node.getData());
	    return node;
	 }
	 List<NodeTree<String>> children=node.getChildren(); 
	 int count=children.size();
	 for(NodeTree each : children) {
		 NodeTree<String> findDataInTree = findDataInTree(each, searchQuery);
		 log.debug("======findDataInTree==node.getData()========="+node.getData());
		 if(findDataInTree!=null){
			 return findDataInTree; 
		 }
		 
	    
	 }
	log.debug("<----------End findDataInTree in ParseJSON------------------->");	 
	return ss; 
}
 public  <T> void printTree(NodeTree<T> node, String appender,LinkedList<String> linkedList) {
	  log.debug(appender + node.getData());
	  linkedList.add(String.valueOf(node.getData()));
	  node.getChildren().forEach(each ->  printTree(each, (appender + appender),linkedList));
	  
 }	
 public static CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		ICommonDataServiceRestClient client1 = CommonDataServiceRestClientImpl.getInstance(datasource, userName, password);
		return client;
	}
   
   public boolean checkProbeIndicator(String jsonFileName)  throws  Exception {
	   log.debug("<----------Start checkProbeIndicator --------------------------->");
	   boolean probeIndicator=true;
	   try {
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));
			JSONObject jo = (JSONObject) obj;
			JSONArray probeIndicatorArr = (JSONArray) jo.get("probeIndicator");
			log.debug("<----------probeIndicatorArr--------->"+probeIndicatorArr);
			if(probeIndicatorArr!=null){
				probeIndicator=true;
			}else{
				probeIndicatorArr  = (JSONArray) jo.get("probeIndocator");
				if(probeIndicatorArr!=null) {
				  probeIndicator=true;
				} else {				
				  probeIndicator=false;
				}
			}
	   } catch (Exception e) {
		   log.error("<----Exception in method checkProbeIndicator of ParseJSON----------->"+e.getMessage());
			throw new Exception(e.getMessage());
		}
	   log.debug("<----------end checkProbeIndicator -------------------probeIndicator-------->"+probeIndicator);
	   return probeIndicator;
			
   }

	public Blueprint jsonFileToObjectProbe(String jsonFileName,DataBrokerBean dataBrokerBean)  throws  Exception {
		 log.debug("<----------Start jsonFileToObjectProbe -------------jsonFileName-------------->"+jsonFileName);
		
		ArrayList<String> list = new ArrayList<String>();
		Blueprint blueprint = new Blueprint();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));

			JSONObject jo = (JSONObject) obj;
			String prettyJSONString = jo.toString();
			String name = (String) jo.get("name");
			String version = (String) jo.get("version");

			String probeIndicator = getProbeIndicator(jo);
			JSONArray trainingClients = (JSONArray) jo.get("training_clients");
			
			ArrayList<ProbeIndicator> list_of_pb_indicators = new ArrayList<ProbeIndicator>();
			ProbeIndicator prbIndicator = new ProbeIndicator();
			if(probeIndicator!=null){
				prbIndicator.setValue(probeIndicator);
			}else{
				prbIndicator.setValue("");
			}
			list_of_pb_indicators.add(prbIndicator);
			
			blueprint.setProbeIndicator(list_of_pb_indicators);
			blueprint.setName(name);
			blueprint.setVersion(version);
			Iterator<Map.Entry> itr1 = null;
			
			
			JSONArray inputPorts = (JSONArray) jo.get("input_ports");
			ArrayList<OperationSignature> operationList = new ArrayList<OperationSignature>();
			List<InputPort> inputPortList=null;
			if (inputPorts != null) {
				inputPortList=new ArrayList<InputPort>();
				log.debug("input ports-->");
				
				Iterator itr2 = inputPorts.iterator();
				while (itr2.hasNext()) {
					InputPort inputPortObj=new InputPort();
					OperationSignature operationSignature = new OperationSignature();
					itr1 = ((Map) itr2.next()).entrySet().iterator();
					while (itr1.hasNext()) {
						Map.Entry pair = itr1.next();
						String key = (String) pair.getKey();
						log.debug("-->" + pair.getKey() + " : " + pair.getValue());
						if (key != null && key.equalsIgnoreCase("operation_signature")) {
							JSONObject jsonObject = (JSONObject) pair.getValue();
							String operationName =(String) jsonObject.get("operation_name");
							operationSignature.setOperationName(operationName);
							inputPortObj.setOperationSignature(operationSignature);
						}
						if (key != null && key.equalsIgnoreCase("container_name")) {
							inputPortObj.setContainerName((String) pair.getValue());
						}
						
					}
					inputPortList.add(inputPortObj);
				}
				
			}
			if(inputPorts!=null && inputPorts.size() > 0){
				blueprint.setInputPorts(inputPortList);
			}
			
			JSONArray nodes = (JSONArray) jo.get("nodes");
			ArrayList<Node> nodeList = new ArrayList<Node>();
			if (nodes != null) {
				Iterator itr3 = nodes.iterator();
				int nodeCount = 0;
				while (itr3.hasNext()) {
					Node node = new Node();
					ArrayList<OperationSignatureList> operSigList = new ArrayList<OperationSignatureList>();
					OperationSignatureList obpListObject=new OperationSignatureList();
					operSigList.add(obpListObject);
					itr1 = ((Map) itr3.next()).entrySet().iterator();
					log.debug("Nodes-->" + ++nodeCount);
					while (itr1.hasNext()) {
						Map.Entry pair = itr1.next();
					   if(pair!=null && pair.getKey()!=null && pair.getValue()!=null){
							String key = (String) pair.getKey();
							if(key != null && key.equalsIgnoreCase("operation_signature_list")) {
								
								if (pair.getValue() != null) {
									operSigList = jsonArrayParseObjectProb(pair.getValue(),operSigList);
									
									log.debug("operSigList-->" + operSigList);
								}
								
							
						  }else {
								log.debug("-->" + pair.getKey() + " : " + pair.getValue());
								if (key != null && key.equalsIgnoreCase("container_name")) {
									node.setContainerName((String) pair.getValue());
								}
								if (key != null && key.equalsIgnoreCase("image")) {
									node.setImage((String) pair.getValue());
								}
								
								if(key != null && key.equalsIgnoreCase("node_type")) {
									node.setNodeType((String)pair.getValue());
								}
								if(key != null && key.equalsIgnoreCase("proto_uri")) {
									node.setProtoUri((String)pair.getValue());
								}
								JSONObject dataBrokerObject=null;
								if(key != null && key.equalsIgnoreCase("data_broker_map")){
			                    	 dataBrokerObject = (JSONObject)pair.getValue();
			                    	 if(dataBrokerObject!=null  ){
			                    		 if(dataBrokerObject.get("data_broker_type")!=null){
			                    			 String dataBrokerType=(String)dataBrokerObject.get("data_broker_type");
			                    			 log.debug("dataBrokerType-->"+dataBrokerType);
			                    			 if(dataBrokerType!=null && dataBrokerType.equalsIgnoreCase("CSV_File")){
			                    				 if(dataBrokerBean!=null){
			                    					 node.setDataBrokerMap(dataBrokerBean.getDataBrokerMap()); 
			                    				 }
			                    			 }
			                    			 
			                    		 }
			                    	 } 
								}
								
							}
					 }	
					}
					node.setOperationSignatureList(operSigList);
					nodeList.add(node);
				}

			}
			blueprint.setNodes(nodeList);
			log.debug("blueprint===" + mapper.writeValueAsString(blueprint) + "====" + blueprint.toString());
		} catch (Exception e) {
			log.error("<----Exception in method jsonFileToObjectProbe of ParseJSON----------->"+e.getMessage());
			throw new Exception(e.getMessage());
		}
		log.debug("<----------Start jsonFileToObjectProbe ------------------blueprint--------->"+blueprint);
		return blueprint;
	}

	public HashMap<String, String> parseJsonFileProbe(String jsonFileName) throws Exception {
		log.debug("<----------Start parseJsonFileProbe in ParseJSON-----------------jsonFileName---------->"+jsonFileName);
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		try
		{
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));
			Iterator<Map.Entry> itr1 = null;
			JSONObject jo = (JSONObject) obj;
	        JSONArray nodes = (JSONArray) jo.get("nodes");
	        ArrayList<Node> nodeList = new ArrayList<Node>();
			if (nodes != null) {
				Iterator itr3 = nodes.iterator();
				int nodeCount = 0;
				while (itr3.hasNext()) {
					Node node = new Node();
					itr1 = ((Map) itr3.next()).entrySet().iterator();
					log.debug("Nodes-->" + ++nodeCount);
					String containerName = null,imageName  = null;
					while (itr1.hasNext()) {
						
						Map.Entry pair = itr1.next();
						if(pair!=null && pair.getKey()!=null && pair.getValue()!=null){
							String key = (String) pair.getKey();
							if (key != null && key.equalsIgnoreCase("container_name")) {
									containerName =(String)pair.getValue();
								}
								if (key != null && key.equalsIgnoreCase("image")) {
									imageName =(String)pair.getValue();
								}
								
							if(containerName!=null && imageName!=null && !"".equals(containerName) && !"".equals(imageName)){
								
			                	imageMap.put(imageName, containerName);
			                }
					  }	
					}
			
				}
			}
			
		}catch(Exception e){
			log.error("<----Exception in method parseJsonFileProbe of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------End parseJsonFileProbe in ParseJSON---------------------imageMap---->"+imageMap);
		return imageMap;	
	}
	
	public HashMap<String, DeploymentBean> getNodeTypeContainerMap(String jsonFileName) throws Exception {
		log.debug("<----------Start getNodeTypeContainerMap in ParseJSON----------------jsonFileName----------->"+jsonFileName);
		HashMap<String,DeploymentBean> imageMap=new HashMap<String,DeploymentBean>();
		ArrayList<String> list=new ArrayList<String>();	
		try
		{
		
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));
			Iterator<Map.Entry> itr1 = null;
			JSONObject jo = (JSONObject) obj;
	        JSONArray nodes = (JSONArray) jo.get("nodes");
	        ArrayList<Node> nodeList = new ArrayList<Node>();
			if (nodes != null) {
				Iterator itr3 = nodes.iterator();
				int nodeCount = 0;
				while (itr3.hasNext()) {
					Node node = new Node();
					itr1 = ((Map) itr3.next()).entrySet().iterator();
					log.debug("Nodes-->" + ++nodeCount);
					String containerName = null,nodeType  = null,script=null;
					DeploymentBean bean=new DeploymentBean();
					while (itr1.hasNext()) {
						Map.Entry pair = itr1.next();
						if(pair!=null && pair.getKey()!=null && pair.getValue()!=null){
							String key = (String) pair.getKey();
							if (key != null && key.equalsIgnoreCase("container_name")) {
									containerName =(String)pair.getValue();
									bean.setContainerName(containerName);
								}
								if (key != null && key.equalsIgnoreCase("node_type")) {
									nodeType =(String)pair.getValue();
									bean.setNodeType(nodeType);
								}
								if (key != null && key.equalsIgnoreCase("script")) {
									script =(String)pair.getValue();
									bean.setScript(script);
								}
								if(key!=null && key.equalsIgnoreCase("data_broker_map")){
									JSONObject dataBrokerObject = (JSONObject) pair.getValue();
			                    	 if(dataBrokerObject!=null  ){
			                    		 if(dataBrokerObject.get("data_broker_type")!=null){
			                    			 String dataBrokerType=(String)dataBrokerObject.get("data_broker_type");
			                    			 log.debug("dataBrokerType-->"+dataBrokerType);
			                    			 bean.setDataBrokerType(dataBrokerType);
			                    		 }
			                    	 } 
								}
								
								
							if(containerName!=null && nodeType!=null && !"".equals(containerName) && !"".equals(nodeType)){
								if(bean.getDataBrokerType() == null){
									bean.setDataBrokerType("Default");
								}
			                	imageMap.put(containerName,bean);
			                }
					  }	
					}
					log.debug("<---container_name-->"+bean.getContainerName()+"--node_type--"+bean.getNodeType()+
							"--Script--"+bean.getScript()+"--DataBrokerType--"+bean.getDataBrokerType());
			
				}
			}
			
		}catch(Exception e){
			log.error("<----Exception in method getNodeTypeContainerMap of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------End getNodeTypeContainerMap in ParseJSON---------------------imageMap---->"+imageMap);
		return imageMap;	
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */

	public LinkedList<String> getSequenceFromJSONProbe(String jsonFileName)  throws  Exception {
		log.debug("<----------Start getSequenceFromJSONProbe in ParseJSON----------------jsonFileName----------->"+jsonFileName);
		String contentString="";
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		LinkedList<String> linkedList=new LinkedList<String>();
		try
		{
		NodeTree<String> root = new NodeTree<String>("BluePrintContainer");
        Object obj = new JSONParser().parse(new FileReader(jsonFileName));
        JSONObject jo = (JSONObject) obj;
        JSONArray nodes = (JSONArray) jo.get("nodes");
        if(nodes!=null && !nodes.isEmpty()){
        	Iterator itr3 = nodes.iterator();
	        int nodeCount=0; 
	        while (itr3.hasNext()) 
	        {
	        	Iterator<Map.Entry> itr4 = ((Map) itr3.next()).entrySet().iterator();
	        	Iterator<Map.Entry> itr5 = itr4;
	        	String containerName="";
	        	String imageName="";
	        	
	        	String contName="test";
	        	NodeTree<String> testNode=new NodeTree<String>(contName);
	        	log.debug("Second while");
	            while (itr4.hasNext()) {
	                Map.Entry pair = itr4.next();
	                if(pair!=null && pair.getKey()!=null && pair.getValue()!=null){
	                	
		                String key=(String)pair.getKey();
		                String val=(String)pair.getValue().toString();
		                if(key!=null && key.equalsIgnoreCase("operation_signature_list")){
		                	sequenceJsonParseProbe(pair.getValue(),testNode,root);
		                }if(key!=null && key.equalsIgnoreCase("container_name")){
		                	containerName=val;
		                	contName=val;
		                }else{
		                	log.debug("-----------------key->"+pair.getKey() );
		                }
		                if(key!=null && key.equalsIgnoreCase("image")){
		                	imageName=val;
		                	list.add(val);
		                 }
		                if(containerName!=null && imageName!=null && !"".equals(containerName) && !"".equals(imageName)){
		                	imageMap.put(imageName, containerName);
		                }
		             } 
	            }
	            testNode.setData(contName);
	            NodeTree<String> searchNode=findDataInTree(root, contName);
	            if(searchNode!=null){
	            	NodeTree<String> parent=searchNode.getParent();
	            	String parentData=parent.getData();
	            	if (parent != null) {
	    				int index = parent.getChildren().indexOf(searchNode);
	    				parent.getChildren().remove(searchNode);
	    			 }
	            	NodeTree<String> parrentNode=findDataInTree(root, parentData);
	            	if(parrentNode!=null){
	            		parrentNode.addChild(testNode);
	            	}
	            }else{
	            	root.addChild(testNode);
	            }
	        }
         }
        log.debug("=======Print==================================");
        printTree(root, " ",linkedList);
        Collections.reverse(linkedList);
        log.debug("=======Print=======================linkedList==========="+linkedList);
        
        }catch(Exception e){
        	log.error("<----Exception in method getSequenceFromJSONProbe of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------End getSequenceFromJSONProbe in ParseJSON--------------------------->");
		return linkedList;	
	}
	public DataBrokerBean getDataBrokerContainer(String jsonFileName) throws Exception {
		log.debug("<----------Start getDataBrokerContainer in ParseJSON----------------jsonFileName----------->"+jsonFileName);
		ArrayList<String> list=new ArrayList<String>();	
		DataBrokerBean dataBrokerBean=null;
		try
		{
		
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));
			Iterator<Map.Entry> itr1 = null;
			JSONObject jo = (JSONObject) obj;
	        JSONArray nodes = (JSONArray) jo.get("nodes");
	        int nodeLength=-1;
	        int dataBrokerVal=-1;
	        ArrayList<Integer>dataBrokerList=new ArrayList<Integer>();
	        ArrayList<Node> nodeList = new ArrayList<Node>();
			if (nodes != null) {
				Iterator itr3 = nodes.iterator();
				int nodeCount = 0;
				while (itr3.hasNext()) {
					Node node = new Node();
					itr1 = ((Map) itr3.next()).entrySet().iterator();
					log.debug("Nodes-->" + ++nodeCount);
					nodeLength++;
					String containerName = null,nodeType  = null,script=null;
					while (itr1.hasNext()) {
						Map.Entry pair = itr1.next();
						if(pair!=null && pair.getKey()!=null && pair.getValue()!=null){
							String key = (String) pair.getKey();
							
								if (key != null && key.equalsIgnoreCase("node_type")) {
									nodeType =(String)pair.getValue();
									if(nodeType!=null && nodeType.equalsIgnoreCase("DataBroker")){
										dataBrokerList.add(nodeLength);
									}
								}
							
					  }	
					}
				}
				log.debug("dataBrokerList Nodes-->"+dataBrokerList);
				if(dataBrokerList!=null && dataBrokerList.size() > 0){
					for(Integer num: dataBrokerList){
						dataBrokerVal=num;
						log.debug("dataBrokerVal -------->"+dataBrokerVal);
						dataBrokerBean=new DataBrokerBean();
						DataBrokerMap dataBrokerMap=new DataBrokerMap();
						ArrayList<MapInputs> mapInputList=new ArrayList<MapInputs>();
						ArrayList<MapOutputs> mapOutputList=new ArrayList<MapOutputs>();
						
						
						log.debug("DataBroker Nodes-->"+nodes.get(dataBrokerVal));
						JSONObject jsonObject =(JSONObject)nodes.get(dataBrokerVal);
						String containerName="";
						String nodeType="";
						String image="";
						String protoUri="";
						JSONArray mapInputArray=null;
						JSONArray mapoutputArray=null;
						JSONObject dataBrokerObject=null;
						if(jsonObject!=null){
							if(jsonObject.get("container_name")!=null){
								containerName=(String)jsonObject.get("container_name");
								
							}
		                    if(jsonObject.get("node_type")!=null){
		                    	nodeType=(String)jsonObject.get("node_type");
							}
		                    if(jsonObject.get("image")!=null){
		                    	image=(String)jsonObject.get("image");
							}
		                    if(jsonObject.get("proto_uri")!=null){
		                    	protoUri=(String)jsonObject.get("proto_uri");
		                    	dataBrokerBean.setProtobufFile(protoUri);
							}
		                    if(jsonObject.get("data_broker_map")!=null){
		                    	 dataBrokerObject = (JSONObject) jsonObject.get("data_broker_map");
		                    	 if(dataBrokerObject!=null  ){
		                    		 if(dataBrokerObject.get("data_broker_type")!=null){
		                    			 String dataBrokerType=(String)dataBrokerObject.get("data_broker_type");
		                    			 log.debug("dataBrokerType-->"+dataBrokerType);
		                    			 if(dataBrokerType!=null && dataBrokerType.equalsIgnoreCase("CSV_File")){
		                    				 dataBrokerMap.setDataBrokerType((String)dataBrokerObject.get("data_broker_type")) ;
		                    			 }else{
		                    				 dataBrokerBean=null;
		                    				 break;
		                    			 }
		                    			 
		                    		 }
		                             if(dataBrokerObject.get("script")!=null){
		                            	 dataBrokerMap.setScript((String)dataBrokerObject.get("script"));
		                    		 }
									 if(dataBrokerObject.get("target_system_url")!=null){
										dataBrokerMap.setTargetSystemUrl((String)dataBrokerObject.get("target_system_url"));
									 }
									 if(dataBrokerObject.get("local_system_data_file_path")!=null){
										dataBrokerMap.setLocalSystemDataFilePath((String)dataBrokerObject.get("local_system_data_file_path"));
									 }
									 if(dataBrokerObject.get("first_row")!=null){
										dataBrokerMap.setFirstRow((String)dataBrokerObject.get("first_row"));
									 }
									 if(dataBrokerObject.get("csv_file_field_separator")!=null){
										dataBrokerMap.setCsvFileFieldSeparator((String)dataBrokerObject.get("csv_file_field_separator"));
									 }
		                             if(dataBrokerObject.get("map_inputs")!=null){
		                            	MapInputs mapInputBean=new MapInputs();
		                            	mapInputArray = (JSONArray) dataBrokerObject.get("map_inputs");
		                            	log.debug("mapInputArray=======================" + mapInputArray);
		                            	Iterator itr5 = null;
		                        		Iterator<Map.Entry> itr6 = null;
		                        		itr5=mapInputArray.iterator();
		                            	while (itr5.hasNext()) {
		                        			itr6 = ((Map) itr5.next()).entrySet().iterator();
		                        			InputField inputFieldBean=new InputField();
		                        			while (itr6.hasNext()) {
		                        				Map.Entry pair = itr6.next();
		                        				String key = (String) pair.getKey();
		                        				log.debug("Key========================" + key);
		                        				log.debug("value========================" + pair.getValue());
		                        				if(key!=null && key.equalsIgnoreCase("input_field")){
		                        					JSONObject inputFieldJsonObject=(JSONObject)pair.getValue();
		                        					if(inputFieldJsonObject!=null){
		                        						if(inputFieldJsonObject.get("name")!=null){
		                        							inputFieldBean.setName((String)inputFieldJsonObject.get("name"));
		                        						}
		                                                if(inputFieldJsonObject.get("type")!=null){
		                                                	inputFieldBean.setType((String)inputFieldJsonObject.get("type"));
		                        						}
		                                                if(inputFieldJsonObject.get("checked")!=null){
		                                                	inputFieldBean.setChecked((String)inputFieldJsonObject.get("checked"));
		                        						}
		                                                if(inputFieldJsonObject.get("mapped_to_field")!=null){
		                                                	inputFieldBean.setMappedToField((String)inputFieldJsonObject.get("mapped_to_field"));
		                        						}
		                        					}
		                        				}
		                        			}
		                        			mapInputBean.setInputField(inputFieldBean);
		                    		    }
		                            	mapInputList.add(mapInputBean);
		                            }
		                            dataBrokerMap.setMapInputs(mapInputList);
		                            if(dataBrokerObject.get("map_outputs")!=null){
		                            	MapOutputs mapOutputBean=new MapOutputs();
		                            	mapoutputArray = (JSONArray) dataBrokerObject.get("map_outputs");
		                            	log.debug("mapoutputArray=======================" + mapoutputArray);
		                            	Iterator itr7 = null;
		                        		Iterator<Map.Entry> itr8 = null;
		                        		itr7=mapoutputArray.iterator();
		                            	while (itr7.hasNext()) {
		                        			itr8 = ((Map) itr7.next()).entrySet().iterator();
		                        			OutputField outputFieldBean=new OutputField();
		                        			while (itr8.hasNext()) {
		                        				Map.Entry pair = itr8.next();
		                        				String key = (String) pair.getKey();
		                        				log.debug("Key====map_outputs===================" + key);
		                        				log.debug("value===map_outputs===================" + pair.getValue());
		                        				if(key!=null && key.equalsIgnoreCase("output_field")){
		                        					JSONObject outputFieldJsonObject=(JSONObject)pair.getValue();
		                        					if(outputFieldJsonObject!=null){
		                        						if(outputFieldJsonObject.get("tag")!=null){
		                        							outputFieldBean.setTag((String)outputFieldJsonObject.get("tag"));
		                        						}
		                                                if(outputFieldJsonObject.get("name")!=null){
		                                                	outputFieldBean.setName((String)outputFieldJsonObject.get("name"));
		                        						}
		                                                if(outputFieldJsonObject.get("type_and_role_hierarchy_list")!=null){
		                                                	ArrayList<TypeAndRoleHierarchyList> roleHirerachryList=new ArrayList<TypeAndRoleHierarchyList>();
		                                                	JSONArray hierarchyListArray = (JSONArray) outputFieldJsonObject.get("type_and_role_hierarchy_list");
		                                                	if(hierarchyListArray!=null){
		                                                		Iterator itr9 = null;
		                                                		Iterator<Map.Entry> itr10 = null;
		                                                		itr9=hierarchyListArray.iterator();
		                                                		while (itr9.hasNext()) {
		                                                			itr10 = ((Map) itr9.next()).entrySet().iterator();
		                                                			TypeAndRoleHierarchyList typeAndRoleHierarchyListBean=new TypeAndRoleHierarchyList();
		                                                			while (itr10.hasNext()) {
		                                                				Map.Entry mapPair = itr10.next();
		                                                				String keyVal = (String) mapPair.getKey();
		                                                				log.debug("Key========type_and_role_hierarchy_list================" + keyVal);
		                                                				log.debug("value=======type_and_role_hierarchy_list=================" + mapPair.getValue());
		                                                				if(keyVal!=null){
		                                                					if(keyVal.equalsIgnoreCase("name")){
		                                                						typeAndRoleHierarchyListBean.setName((String)mapPair.getValue());
		                                                					}
		                                                                    if(keyVal.equalsIgnoreCase("role")){
		                                                                    	typeAndRoleHierarchyListBean.setRole((String)mapPair.getValue());
		                                                					}
		                                                				}
		                                                			}
		                                                			roleHirerachryList.add(typeAndRoleHierarchyListBean);
		                                                		}		
		                                                	}
		                                                	outputFieldBean.setTypeAndRoleHierarchyList(roleHirerachryList);
		                        						}
		                                                
		                        					}
		                        				}
		                        			}
		                        			mapOutputBean.setOutputField(outputFieldBean);
		                    		    }
		                            	
		                            	mapOutputList.add(mapOutputBean);
		                    		 }
		                            dataBrokerMap.setMapOutputs(mapOutputList);
		                    	 }
							}
		                    dataBrokerBean.setDataBrokerMap(dataBrokerMap);
		
						}
				 }	
			   }	
			}
			
		}catch(Exception e){
			log.error("<----Exception in method getDataBrokerContainer of ParseJSON----------->"+e.getMessage());
    	    throw new Exception(e.getMessage());
       }
		log.debug("<----------End getDataBrokerContainer in ParseJSON---------------------dataBrokerBean---->"+dataBrokerBean);
		return dataBrokerBean;	
	}
	
	
	
}
