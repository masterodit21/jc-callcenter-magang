/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author rkrzmail
 */
public class NikitaEngineManager {
    static private Hashtable<String, NikitaEngine> enginemanager = new  Hashtable<String, NikitaEngine>();    
    public static void clear(){
        enginemanager.clear();
    }
    public static NikitaEngine getInstance(NikitaConnection nikitaConnection, String formname, boolean devmode){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("engine").toString().equalsIgnoreCase("memory")) {
            return getInstance(nikitaConnection, formname);
        } 
        return new NikitaEngine(nikitaConnection, formname, devmode);
    }
    public static NikitaEngine getInstance(NikitaConnection nikitaConnection, String formname, boolean devmode, boolean singlelogic){
        if (formname.trim().equals("")) {
            return null;
        }   
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("engine").toString().equalsIgnoreCase("memory")) {
            return getInstance(nikitaConnection, formname);
        }         
        return new NikitaEngine(nikitaConnection, formname, devmode, singlelogic);
    }
        
    private static NikitaEngine getInstance(NikitaConnection nikitaConnection, String formname){      
             
        if (enginemanager.containsKey(formname)) {
            return enginemanager.get(formname).cloneNikitaEngine();//clone
        }        
        
        NikitaEngine engine = new NikitaEngine(nikitaConnection, formname, true, false);
        enginemanager.put(formname, engine);
        
        return  enginemanager.get(formname).cloneNikitaEngine();//clone
    }
    
    
}
