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
import java.util.UUID;

import org.acumos.azure.client.transport.AzureContainerBean;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

//import org.yaml.snakeyaml.Yaml;

public class ParseJSON {
	
	Logger log =LoggerFactory.getLogger(ParseJSON.class);
	public  HashMap<String,String> parseJsonFile()throws  Exception{
		log.debug("<----------Start parseJsonFile in ParseJSON--------------------------->");
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		try
		{
		 
        Object obj = new JSONParser().parse(new FileReader("blueprint.json"));
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
	
	public static void jsonArrayParse(Object obj){
		JSONArray jsonArr = (JSONArray) obj;
		Iterator itr = jsonArr.iterator();
		Iterator<Map.Entry> itr1=null;
		 while (itr.hasNext()) {
			 itr1 = ((Map) itr.next()).entrySet().iterator();
	            while (itr1.hasNext()) {
	                Map.Entry pair = itr1.next();
	                System.out.println("-->"+pair.getKey() + " : " + pair.getValue());
	            }
         }
	}
	
	public  Blueprint jsonFileToObject()throws  Exception{
		//log.debug("<----------Start jsonFileToObject in --------------------------->");
		ArrayList<String> list=new ArrayList<String>();	
		Blueprint blueprint=new Blueprint();
		ObjectMapper mapper = new ObjectMapper();
		try
		{
		Object obj = new JSONParser().parse(new FileReader("blueprint.json"));
		
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
        blueprint.setOrchestrator(orchestratorBean);
        
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
	                	operationSignature.setOperation(value);
	                }
	               
	            }
	            operationList.add(operationSignature);
	        }
        }
        blueprint.setInputs(operationList);
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
		                	node.setDependsOn(listComponent);
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
		//log.debug("<----------End jsonFileToObject in ParseJSON---------------------list---->"+list);
		return blueprint;	
	}
	public static ArrayList<Component> jsonArrayParseObject(Object obj){
		
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
	                		opr.setOperation(operation);
	                		System.out.println("=======operation==========="+operation);
	                		component.setOperationSignature(opr);
	                	}
	                }
	                if(key!=null && key.equalsIgnoreCase("name")){
	                	component.setName((String)pair.getValue());
	                }
	                
	            }
	            listComponent.add(component); 
         }
	  return listComponent;	 
	}
	
public LinkedList<String> getSequenceFromJSON()throws  Exception{
		
		String contentString="";
		HashMap<String,String> imageMap=new HashMap<String,String>();
		ArrayList<String> list=new ArrayList<String>();	
		LinkedList<String> linkedList=new LinkedList<String>();
		try
		{
			NodeTree<String> root = new NodeTree<String>("BluePrintContainer");
        Object obj = new JSONParser().parse(new FileReader("blueprint.json"));
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
	        	System.out.println("Second while");
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
	                	System.out.println("-bbbbbkey->"+pair.getKey() );
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
        System.out.println("=======Print==================================");
        printTree(root, " ",linkedList);
        Collections.reverse(linkedList);
        System.out.println("=======Print=======================linkedList==========="+linkedList);
        
        }catch(Exception e){
        	//log.error("<-In Exception-contentString--parseJsonFile-->"+e.getMessage());
        	e.printStackTrace();
    	    throw new Exception(e.getMessage());
       }
		//log.debug("<----------End parseJsonFile in ParseJSON---------------------imageMap---->"+imageMap);
		return linkedList;	
	}

public static void sequenceJsonParse(Object obj,NodeTree<String> newNode,NodeTree<String> rootNode){
	JSONArray jsonArr = (JSONArray) obj;
	Iterator itr = jsonArr.iterator();
	Iterator<Map.Entry> itr1=null;
	 while (itr.hasNext()) {
		 itr1 = ((Map) itr.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                System.out.println("--iiii>"+pair.getKey() + " : " + pair.getValue());
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
                System.out.println("ccccc-->"+pair.getKey() + " : " + pair.getValue());
            }
     }
}

public static NodeTree<String> findDataInTree(NodeTree node, String searchQuery) {
	NodeTree<String> ss=null;
	 if(node.getData().equals(searchQuery)) {
		 System.out.println("========node.getData()========="+node.getData());
	    return node;
	 }
	 List<NodeTree<String>> children=node.getChildren(); 
	 int count=children.size();
	 for(NodeTree each : children) {
		 System.out.println(each.getData());
		 NodeTree<String> findDataInTree = findDataInTree(each, searchQuery);
		 System.out.println("======findDataInTree==node.getData()========="+node.getData());
		 if(findDataInTree!=null){
			 return findDataInTree; 
		 }
		 
	    
	 }
	return ss; 
}
 public static <T> void printTree(NodeTree<T> node, String appender,LinkedList<String> linkedList) {
	  System.out.println(appender + node.getData());
	  linkedList.add(String.valueOf(node.getData()));
	  node.getChildren().forEach(each ->  printTree(each, (appender + appender),linkedList));
	  
 }	
 /*public static CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		ICommonDataServiceRestClient client1 = CommonDataServiceRestClientImpl.getInstance(datasource, userName, password);
		return client;
	}
public static void main(String args[]){
		try{
			CommonDataServiceRestClientImpl client = getClient("http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8000/ccds","ccds_client","ccds_client");
			UUID uidNumber = UUID.randomUUID();
			List<AzureContainerBean> AzureContainerBeanList=new ArrayList<AzureContainerBean>();
			AzureContainerBean containerBean=new AzureContainerBean();
			
			AzureContainerBean containerBean2=new AzureContainerBean();
			
			containerBean.setContainerIp("11.11.10.80");
			containerBean.setContainerPort("8080");
			containerBean.setContainerName("A");
			
			containerBean2.setContainerIp("11.11.10.81");
			containerBean2.setContainerPort("8081");
			containerBean2.setContainerName("B");
			AzureContainerBeanList.add(containerBean);
			AzureContainerBeanList.add(containerBean2);
			
			ObjectMapper mapper = new ObjectMapper();
			String azureDetails=mapper.writeValueAsString(AzureContainerBeanList);
			System.out.println("=========azureDetails========"+AzureContainerBeanList);
			//ObjectMapper mapper = new ObjectMapper();
			//uidNumStr=uidNumber.toString();
			String deploymentStatusCode=uidNumber.toString();
			System.out.println("=================="+deploymentStatusCode);
			//List<MLPSolutionRevision> testList=client.getSolutionRevisions("02eab846-2bd0-4cfe-8470-9fc69fa0d877");
			//System.out.println(testList.get(0));
			//getSolutionRevisions(String solutionId)
			MLPSolutionDeployment mlp=new MLPSolutionDeployment("1bb7424a-69d8-493d-8e5a-dbb87561f08c", "fa7038b2-1c3d-4f17-b439-cd59a7c0a38b", "7cd47ca4-1c5d-4cdc-909c-f7c17367b4d4",
					"DP");
			mlp.setDeploymentId(deploymentStatusCode);
			mlp.setDetail(azureDetails);
			String oldstring = "2018-01-10T16:50:39.402Z";
			mlp.setTarget("pp");
			//sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			//Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(oldstring);
			mlp.setCreated(date);
			mlp.setModified(date);pn
			mlp.setTarget("test");
			mlp.setDetail("test2");
			
			
			 {
			 -- "created": "2018-01-10T16:50:39.402Z",
			  "deploymentId": "3956dec8-1028-4116-9669-ca5f43628f86",
			  "deploymentStatusCode": "DP",
			--  "detail": "1",
			 -- "modified": "2018-01-10T16:50:39.402Z",
			  "revisionId": "a9e68bc6-f4b4-41c6-ae8e-4e97ec3916a6",
			  "solutionId": "02eab846-2bd0-4cfe-8470-9fc69fa0d877",
			 -- "target": "111",
			  "userId": "0505e537-ce79-4b1f-bf43-68d88933c369"
			}
			
			//mlp.setDeploymentStatusCode("DP");
			//client.createSolutionDeployment(arg0)
			
			client.createSolutionDeployment(mlp);
		ParseJSON p=new ParseJSON();
		System.out.println("===========vvvv=======");
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	
}
