/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

import com.nikita.generator.NikitaService;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class NikitaProperty {
    InputStream inp ;
 
    public NikitaProperty(InputStream inifile){
        inp=inifile;
    }  
    
    
    public NikitaProperty(String inifile){
        try {
            if (new File(NikitaService.newNikitaINI).isFile()) {
                inp = new FileInputStream(NikitaService.newNikitaINI);
                return;
            }            
        } catch (Exception e) { }
        try {
            inp=getClass().getClassLoader().getResourceAsStream(inifile);
        } catch (Exception e) {  }
    } 
    
    public synchronized Nset read(){
        Hashtable<String, Hashtable> h = new Hashtable();
        try {  
            Hashtable<String, String> sec = new Hashtable();
            BufferedReader br=new BufferedReader(new InputStreamReader(inp));
            String s = br.readLine();
            String section = "";
            while (s!=null) { 
                if (s.toUpperCase().trim().startsWith("[")) {
                    if (!section.equals("")) {
                        h.put(section, sec);
                    }                   
                    sec = new Hashtable();
                    section = s.substring(s.indexOf("[")+1,s.indexOf("]")).trim();
                } else if (s.contains("=")) {                         
                    sec.put(s.substring(0,s.indexOf("=")).trim(), s.substring(s.indexOf("=")+1).trim());
                }
                s = br.readLine();
            }  
            if (!section.equals("")) {
                h.put(section, sec);
            }  
            br.close();
        } catch (Exception e) { }
        return new Nset(h);
    }
    public synchronized Nset read(String section){
        Hashtable<String, String> h = new Hashtable();
        try {            
            boolean bfirtsection = false ;
            BufferedReader br=new BufferedReader(new InputStreamReader(inp));
            String s = br.readLine();
            while (s!=null) {                
                if (bfirtsection) {
                    if (s.toUpperCase().trim().startsWith("[")) {
                        break;
                    }     
                    if (s.contains("=")) {                         
                        h.put(s.substring(0,s.indexOf("=")).trim(), s.substring(s.indexOf("=")+1).trim());
                    }
                }else if (s.toUpperCase().trim().startsWith("["+section.toUpperCase()+"]")) {
                    bfirtsection=true;
                }
                s = br.readLine();
            }           
            br.close();
        } catch (Exception e) { }
        return new Nset(h);
    }
            
    public synchronized String read(String section, String key){
         return read(section).getData(key).toString();
    }
     

}
