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
        	log.error("<-In Exception-contentString--parseJsonFile-->"+e.getMessage());
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
	
	public  Blueprint jsonFileToObject(String jsonFileName)throws  Exception{
		log.debug("<----------Start jsonFileToObject in -----------------jsonFileName---------->"+jsonFileName);
		ArrayList<String> list=new ArrayList<String>();	
		Blueprint blueprint=new Blueprint();
		ObjectMapper mapper = new ObjectMapper();
		try
		{
		Object obj = new JSONParser().parse(new FileReader(jsonFileName));
		
        JSONObject jo = (JSONObject) obj;
        String prettyJSONString = jo.toString();
        //testDumpWriterFromJSON(prettyJSONString);
        // getting firstName and lastName
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
        //blueprint.setOrchestrator(orchestratorBean);
        
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
        //blueprint.setInputs(operationList);
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
		                	//node.setDependsOn(listComponent);
	                	}
	                }else{
	                	log.debug("-->"+pair.getKey() + " : " + pair.getValue());
	                if(key!=null && key.equalsIgnoreCase("container_name")){
	                	node.setContainerName((String)pair.getValue());
	                  }
	                if(key!=null && key.equalsIgnoreCase("image")){
	                	node.setImage((String)pair.getValue());
	                  }
	                }
	            }
	            nodeList.add(node);
	        }
	         
        }
        blueprint.setNodes(nodeList);
        log.debug("blueprint==="+ mapper.writeValueAsString(blueprint)+"===="+blueprint.toString());
        //testDumpWriter(data);
		}catch(Exception e){
			log.error("<-In Exception-contentString--jsonFileToObject-->"+e.getMessage());
			throw new Exception(e.getMessage());
    	   //e.printStackTrace();
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
	                //String value=(String)pair.getValue();
	                
	                if(key!=null && key.equalsIgnoreCase("operation_signature")){
	                	JSONObject objVar =(JSONObject)pair.getValue();
	                	if(objVar!=null){
	                		String operation=(String)objVar.get("operation");
	                		OperationSignature opr=new OperationSignature();
	                		//opr.setOperation(operation);
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
		//ArrayList<Component> listComponent = new ArrayList<Component>();
		//ArrayList<OperationSignatureList> listComponent = new ArrayList<OperationSignatureList>();
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

						// JSONObject obj = conItr.next();
						itr3 = ((Map) conItr.next()).entrySet().iterator();
						//Component component = new Component();
						while (itr3.hasNext()) {
							Map.Entry pair1 = itr3.next();
							String connectedKey = (String) pair1.getKey();
							if (connectedKey != null && connectedKey.equalsIgnoreCase("container_name")) {
								String containerKey = (String) pair1.getKey();
								String containerName = (String) pair1.getValue();
								connectedTo.setContainerName(containerName);
								//component.setName(containerName);
							}

							if (connectedKey != null && connectedKey.equalsIgnoreCase("operation_signature")) {
								JSONObject objVar = (JSONObject) pair1.getValue();
								OperationSignature opr = new OperationSignature();
								if (objVar != null) {
									String operation = (String) objVar.get("operation_name");
									opr.setOperationName(operation);
									//opr.setOperation(operation);
									log.debug("=======operation===========" + operation);
									//component.setOperationSignature(opr);
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
				// parent operation .. needs to check with mukesh
				

			}
			
			
			
		}
		
		
		/*JSONObject objVar1 = (JSONObject) obj;
		JSONObject objVar =(JSONObject)objVar1.get("operation_signature");
		OperationSignature oprSignature = new OperationSignature();
		if(objVar!=null ){
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
		}*/
		//istComponent.add(oprListObj);
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
	        	//log.debug("Nodes-->"+ ++nodeCount);
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
        	//log.error("<-In Exception-contentString--parseJsonFile-->"+e.getMessage());
        	e.printStackTrace();
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
                		//NodeTree<String> searchNodeTemp=new NodeTree<String>(searchNode); 
     	            	NodeTree<String> parent=searchNode.getParent();
     	            	String parentData=parent.getData();
     	            	if (parent != null) {
     	    				//int index = parent.getChildren().indexOf(searchNode);
     	    				parent.getChildren().remove(searchNode);
     	    				newNode.addChild(searchNode);
     	    			 }
     	            	/*NodeTree<String> parrentNode=findDataInTree(rootNode, parentData);
     	            	if(parrentNode!=null){
     	            		parrentNode.addChild(subNode);
     	            	}*/
     	            }else{
     	            	newNode.addChild(subNode);
     	            }
                	//newNode.addChild(new NodeTree<String>(data));
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
							// JSONObject obj = conItr.next();
							Iterator<Map.Entry> contrItr = ((Map) conItr.next()).entrySet().iterator();
							while (contrItr.hasNext()) {
								Map.Entry cntPair = contrItr.next();
								String connectedKey = (String) cntPair.getKey();
								if (connectedKey != null && connectedKey.equalsIgnoreCase("container_name")) {
									String containerKey = (String) cntPair.getKey();
									data = (String) cntPair.getValue();
									///
									//String data = String.valueOf(pair.getValue());
									NodeTree<String> subNode = new NodeTree<String>(data);
									NodeTree<String> searchNode = findDataInTree(rootNode, data);
									if (searchNode != null) {
										// NodeTree<String> searchNodeTemp=new NodeTree<String>(searchNode);
										NodeTree<String> parent = searchNode.getParent();
										String parentData = parent.getData();
										if (parent != null) {
											// int index = parent.getChildren().indexOf(searchNode);
											parent.getChildren().remove(searchNode);
											newNode.addChild(searchNode);
										}
										/*
										 * NodeTree<String> parrentNode=findDataInTree(rootNode, parentData);
										 * if(parrentNode!=null){ parrentNode.addChild(subNode); }
										 */
									} else {
										newNode.addChild(subNode);
									}
										// newNode.addChild(new NodeTree<String>(data));
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
		 //log.debug(each.getData());
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
			log.error("<-In Exception-checkProbeIndicator---->" + e.getMessage());
			throw new Exception(e.getMessage());
			// e.printStackTrace();
		}
	   log.debug("<----------end checkProbeIndicator -------------------probeIndicator-------->"+probeIndicator);
	   return probeIndicator;
			
   }

	public Blueprint jsonFileToObjectProbe(String jsonFileName)  throws  Exception {
		 log.debug("<----------Start jsonFileToObjectProbe -------------jsonFileName-------------->"+jsonFileName);
		
		ArrayList<String> list = new ArrayList<String>();
		Blueprint blueprint = new Blueprint();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object obj = new JSONParser().parse(new FileReader(jsonFileName));

			JSONObject jo = (JSONObject) obj;
			String prettyJSONString = jo.toString();
			// testDumpWriterFromJSON(prettyJSONString);
			// getting firstName and lastName
			String name = (String) jo.get("name");
			String version = (String) jo.get("version");

			String probeIndicator = getProbeIndicator(jo);
			
			//Get input_ports
			//JSONArray inputPorts = (JSONArray) jo.get("input_ports");
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
			
			/*Orchestrator orchestratorBean = new Orchestrator();
			Map orchestrator = ((Map) jo.get("orchestrator"));
			if (orchestrator != null) {
				itr1 = orchestrator.entrySet().iterator();
				while (itr1.hasNext()) {
					Map.Entry pair = itr1.next();
					String key = (String) pair.getKey();
					String value = (String) pair.getValue();
					log.debug("-->" + pair.getKey() + " : " + pair.getValue());
					if (key != null && key.equalsIgnoreCase("name")) {
						orchestratorBean.setName(value);
					}
					if (key != null && key.equalsIgnoreCase("version")) {
						orchestratorBean.setVersion(value);
					}
					if (key != null && key.equalsIgnoreCase("image")) {
						orchestratorBean.setImage(value);
					}
				}
			}
			blueprint.setOrchestrator(orchestratorBean);	*/		
			
			/**
			 * 
			 * "input_operation_signatures": [
	    		{
	      			"operation_signature": "JSON representation of the input operation signature"
	    		},
	    		{
	      			"operation_signature": "JSON representation of the input operation signature"
	    		}
	    		
	    		new 
	    		 "input_ports": [
	    		{
	      			"container_name": "Aggregator - 1",
	      			"operation_signature": {
	        		"operation_name": "aggregate"
	      		}
	    		},
	    		{
	      		"container_name": "Aggregator - 2",
	      		"operation_signature": {
	        	"operation_name": "aggregate - operation names can be identical but IP and Port of containers will differ"
	      		}
	    		}
	  			],
			 */
			
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
						// String value = null;
						log.debug("-->" + pair.getKey() + " : " + pair.getValue());
						if (key != null && key.equalsIgnoreCase("operation_signature")) {
							JSONObject jsonObject = (JSONObject) pair.getValue();
							String operationName =(String) jsonObject.get("operation_name");
							operationSignature.setOperationName(operationName);
							//operationList.add(operationSignature);
							inputPortObj.setOperationSignature(operationSignature);
						}
						if (key != null && key.equalsIgnoreCase("container_name")) {
							inputPortObj.setContainerName((String) pair.getValue());
						}
						/*// Add container name
						if (key != null && key.equalsIgnoreCase("container_name")) {
					         String value =(String)pair.getValue();									
						
						}*/
						
					}
					inputPortList.add(inputPortObj);
				}
				
			}
			if(inputPorts!=null && inputPorts.size() > 0){
				blueprint.setInputPorts(inputPortList);
			}
			
			
			/**
			 * 
			 * old 
			 * "nodes": [
			    {
			      "container_name": "Predictor-1",
			      "image": "cognita-nexus01:8001/h2omodel_1110:1",
			      "depends_on": [
			        {
			          "name": "Classifier-1",
			          "operation_signature": "JSON representation of classify (Prediction) returns (Classification)"
			        },
			        {
			          "name": "Classifier-2",
			          "operation_signature": "JSON representation of operation signatures in Protbuf.json file"
			        }
			      ]
			    }
			 * 
			 * new
			 *  "nodes": [
		    {
		      "container_name": "Aggregator-1",
		      "node_type": "DataMapper or MLModel or DataBroker or TrainingClient",
		      "image": "url of the docker image of the named node in Nexus. Information consumed by deployer",
		      "proto_uri": "url of the proto file of the ML Model, otherwise empty",
		      "operation_signature_list": [
		        {
		          "operation_signature": {
		            "operation_name": "aggregate",
		            "input_message_name": "DataFrame - MC should send this message name to Probe along with proto_uri",
		            "output_message_name": "DataFrames"
		          },
		          "connected_to": [
		            {
		              "container_name": "Predictor-1",
		              "operation_signature": {
		                "operation_name": "predict"
		              }
		            },
		            {
		              "container_name": "Predictor-2",
		              "operation_signature": {
		                "operation_name": "predict"
		              }
			 * 
			 * 
			 * 
			 */
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
							//if (key != null && key.equalsIgnoreCase("connected_to")) {
							/*if (key != null && key.equalsIgnoreCase("connected_to")) {
								if (pair.getValue() != null) {
									ArrayList<Component> listComponent = jsonArrayParseObjectProb(pair.getValue());
									node.setDependsOn(listComponent);
								}
							} else*/ if(key != null && key.equalsIgnoreCase("operation_signature_list")) {
								
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
									//node.setNode_type((String)pair.getValue());
									node.setNodeType((String)pair.getValue());
								}
								if(key != null && key.equalsIgnoreCase("proto_uri")) {
									//node.setNode_type((String)pair.getValue());
									node.setProtoUri((String)pair.getValue());
									//node.setNodeType((String)pair.getValue());
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
			// testDumpWriter(data);
		} catch (Exception e) {
			log.error("<-In Exception-jsonFileToObjectProbe---->" + e.getMessage());
			throw new Exception(e.getMessage());
			// e.printStackTrace();
		}
		// log.debug("<----------End jsonFileToObject in
		// ParseJSON---------------------list---->"+list);
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
        	log.error("<-In Exception-contentString--parseJsonFileProbe-->"+e.getMessage());
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
					String containerName = null,nodeType  = null,script=null,protoUri=null;
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
								if (key != null && key.equalsIgnoreCase("proto_uri")) {
									protoUri =(String)pair.getValue();
									bean.setProtoUri(protoUri);
									//bean.setScript(script);
								}
								
							if(containerName!=null && nodeType!=null && !"".equals(containerName) && !"".equals(nodeType)){
								
			                	imageMap.put(containerName,bean);
			                }
					  }	
					}
					log.debug("<---container_name-->"+bean.getContainerName()+"--node_type--"+bean.getNodeType()+"--Script--"+bean.getScript());
			
				}
			}
			
		}catch(Exception e){
        	log.error("<-In Exception-getNodeTypeContainerMap--parseJsonFile-->"+e.getMessage());
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
	        	//log.debug("Nodes-->"+ ++nodeCount);
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
        	//log.error("<-In Exception-contentString--parseJsonFile-->"+e.getMessage());
        	e.printStackTrace();
    	    throw new Exception(e.getMessage());
       }
		//log.debug("<----------End parseJsonFile in ParseJSON---------------------imageMap---->"+imageMap);
		log.debug("<----------End getSequenceFromJSONProbe in ParseJSON--------------------------->");
		return linkedList;	
	}
	
	/*public static void main(String args[]){
		ParseJSON parseJson=new ParseJSON();
		try{
			ObjectMapper mapper = new ObjectMapper();
		//HashMap<String,String> imageMap=parseJson.parseJsonFileProbe("blueprint_old.json");
			Blueprint bluePrintProbe=parseJson.jsonFileToObjectProbe("blueprint.json");
			System.out.println("======="+bluePrintProbe);
			String blueprintJson=mapper.writeValueAsString(bluePrintProbe); 
			System.out.println("<----blueprintJson---------->"+blueprintJson);
			System.out.println("<----imageMap---------->"+parseJson.parseJsonFileProbe("blueprint.json"));
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
}
