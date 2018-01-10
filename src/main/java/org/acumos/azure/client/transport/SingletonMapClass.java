package org.acumos.azure.client.transport;

import java.util.HashMap;

public class SingletonMapClass {
	
private static HashMap<String,String> singlatonMap;
    
    private SingletonMapClass(){}
    
    public static synchronized HashMap<String,String> getInstance(){
        if(singlatonMap == null){
        	singlatonMap = new HashMap<String,String>();
        }
        return singlatonMap;
    }

}
