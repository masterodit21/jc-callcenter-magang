/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * created by 13k.mail@gmail.com
 */
public class NikitaRequest {
    
    public HttpServletRequest data ;
    private NikitaServletResponse resp;
    
    public Hashtable<String, String> getInternalParameter(){
        return  resp.getInternalParameter();
    }
    
    public void setParameter(String name, String value){
        resp.setParameter(name, value);
    } 
    
    public String getParameter(String name){         
        return resp.getParameter(name);
    }
    public Nset getRequestParameter(){ 
        Nset n = Nset.newObject();
        Map<String, String[]> map = data.getParameterMap();         
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next()) ;
            try {
                n.setData(key, map.get(key)[0]);    
            } catch (Exception e) {  }            
        }         
        return n;
    }
     
     public NikitaRequest newInstance(){
        NikitaRequest n = new NikitaRequest(data,resp);
        return n;
    }
    public Nset getResult(){
        return Nset.readJSON(getParameter("result"));
    }
    
    
    public NikitaRequest (HttpServletRequest request, NikitaServletResponse resp){
        data=request;
        this.resp=resp;
    }
    public HttpServletRequest getHttpServletRequest(){
        return data;
    } 
    
    private Nset nsetretain = null;
    public NikitaViewV3 setNikitaBufferData(String nikitaid){
        NikitaViewV3 v3 = NikitaViewV3.create();
        
        if (nsetretain == null && false) {             
            if ((!getParameter("action").equals("")) && ( getParameter("nikitaformv3").startsWith("{") && getParameter("nikitaformv3").endsWith("}")) ) {
                
                String fnikita = Nset.readJSON(getParameter("nikitaformv3")).getData("fnikita").toString();
                Nset fNikita =NikitaViewV3.read(nikitaid, fnikita);
                v3.setFormNikita(fnikita);
                v3.setDataNikita(fNikita);                
                nsetretain = Nset.readJSON(Component.unescapeHtml(getParameter("data")));
                
                //System.out.println(getParameter("nikitaformv3"));
                 
                String[] keys = nsetretain.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    String jsid =nsetretain.getData(keys[i]).getData("i").toString();//jsid
                    //System.out.println(fNikita.getData(jsid).toJSON());
                    //System.out.println(nsetretain.getData(keys[i]).toJSON());
                    
                      
                    if (fNikita.getData(jsid).getData(1).toString().equalsIgnoreCase("true")  && fNikita.getData(jsid).getData(2).toString().equalsIgnoreCase("true") ) {
                    }else{
                        if (fNikita.containsKey(jsid) && fNikita.getData(jsid).getArraySize()>=4) {
                            nsetretain.getData(keys[i]).setData("t", fNikita.getData(jsid).getData(3).toString() );
                        }                        
                    }
                    nsetretain.getData(keys[i]).setData("n", fNikita.getData(jsid).getData(4).toString() );
                    nsetretain.getData(keys[i]).setData("v", fNikita.getData(jsid).getData(1).toBoolean()?"1":"0" );
                    nsetretain.getData(keys[i]).setData("e", fNikita.getData(jsid).getData(2).toBoolean()?"1":"0");
                    nsetretain.getData(keys[i]).setData("i", jsid);  
                    
                    nsetretain.getData(keys[i]).setData("u", "v3");
                     
                    if (fNikita.getData(jsid).getData(5).toString().equals("Tablegrid")) {
                        Nset internaldata = Nset.readJSON(nsetretain.getData(keys[i]).getData("t").toString());
                        String[] keysinternaldata = internaldata.getObjectKeys();
                      
                        for (int j = 0; j < keysinternaldata.length; j++) {
                            String jsid2 =internaldata.getData(keysinternaldata[j]).getData("i").toString();
                             
                            internaldata.getData(keys[i]).setData("u", "v3");
                            internaldata.getData(keysinternaldata[j]).setData("n", fNikita.getData(jsid2).getData(4).toString() );//tag
                            if (internaldata.getData(keysinternaldata[j]).containsKey("c")) {
                                internaldata.getData(keysinternaldata[j]).setData("n", internaldata.getData(keysinternaldata[j]).getData("c").toString());
                            }
                            
                        }
                         
                        nsetretain.getData(keys[i]).setData("t", internaldata.toJSON() );
                    }
                    
                }
                
                
                
            }
        }
        return v3;
    }
    public Nset getNikitaBufferData(){
        if (nsetretain == null) {
            return Nset.readJSON(Component.unescapeHtml(getParameter("data")));
        }
        return nsetretain;//new v3
    }
    public void retainData(Component...component){        
        if ((!getParameter("action").equals("")) && ( getParameter("data").startsWith("{") || getParameter("data").startsWith("[")) ) {
            if (nsetretain == null) {
                nsetretain = getNikitaBufferData();
            }            
            for (int i = 0; i < component.length; i++) {
                component[i].restoreData(nsetretain);
            }
        }
    }
    
}
