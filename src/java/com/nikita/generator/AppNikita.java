/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;

/**
 *
 * @author rkrzmail
 */
public class AppNikita {
    public static final String APP_VER_CODE = "2.16.1316";
    
    private static volatile AppNikita singleton;	
    public static AppNikita getInstance(){
        if (singleton == null) {
            singleton = new AppNikita();
        }
        return singleton;
    }  
    public static String getDeviceId(){
        return APP_VER_CODE;
    }
    
    private Hashtable<String, Object> virtual = new Hashtable<String, Object> ();
    public void setVirtual(String key, Object data){
            virtual.put(key, data);
    }
    public String getVirtualString(String key){
        return String.valueOf(getVirtual(key));
    }
    public Object getVirtual(String key){
            return virtual.get(key);
    }
    public void removeVirtual(String key){
            virtual.remove(key);
    }
    public void clearAllVirtual(){
            virtual.clear();
    }
    public boolean containVirtual(String key){
            return virtual.containsKey(key);
    }
    public void onCreate() {
        singleton = this;
    }
    
      public static Hashtable<String, String> getArgsData(NikitaResponse nikitaResponse){
		        Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("userid", 		 nikitaResponse.getVirtualString("@+SETTING-N-USER" ) );
		hashtable.put("username", 		 nikitaResponse.getVirtualString("@+SETTING-N-USER" ));
		hashtable.put("imei", 			 "");
		hashtable.put("localdate", 		 Utility.Now());
		hashtable.put("session", 		 nikitaResponse.getVirtualString("@+SETTING-N-SESSION" ));
		hashtable.put("auth", 			 nikitaResponse.getVirtualString("@+SETTING-N-AUTH" ));
		hashtable.put("batch",			 nikitaResponse.getVirtualString("@+SETTING-N-BATCH" ));
		hashtable.put("logicversion", 	"" );	

		hashtable.put("version", APP_VER_CODE );		
		return hashtable;
	}
}
