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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.acumos.azure.client.controller.AzureServiceController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

//import org.yaml.snakeyaml.Yaml;

public class ParseJSON {
	
	Logger log =LoggerFactory.getLogger(ParseJSON.class);
	public  HashMap<String,String> parseJsonFile()throws  Exception{
		log.debug("<----------Start parseJsonFile in ParseJSON--------------------------->");
		String contentString="";
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
		String contentString="";
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
	/*public static void main(String args[]){
		try{
		ParseJSON p=new ParseJSON();
		System.out.println("=================="+p.parseJsonFile());
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	
}
