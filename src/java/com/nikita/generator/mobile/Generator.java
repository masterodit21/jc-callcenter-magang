/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.mobile;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class Generator {
    public static String getCoreVirtualString(NikitaResponse nikitaResponse, String key) {
        Nset dataAll = nikitaResponse.getMobileGenerator();
        if (key.startsWith("@+CORE-MOBILE-ACTIVITY-")) { //@+CORE-MOBILE-ACTIVITY-FORMNAME-COMPONENTNAME [NSET]
                
                key = key.substring("@+CORE-MOBILE-ACTIVITY-".length());
                String fname = "";
                String cname = "";
                String castx = "";
                if (key.contains("-")) {
                        fname = key.substring(0, key.indexOf("-")).trim();
                        cname =  key.substring(key.indexOf("-")+1).trim();
                }	
                if (cname.contains("[")) {
                        castx =  cname.substring(cname.indexOf("[")).trim();
                        cname = cname.substring(0, cname.indexOf("[")).trim();				
                }
                
                cname = cname.startsWith("$")? cname.substring(1):cname ;
                return dataAll.getData(fname).getData(cname).getData(0).toString();
        }
        return "";
    }
    public static void setCoreOpenForm(NikitaResponse nikitaResponse, String key, Object data) {
         nikitaResponse.openFormStream(key, data);
    }
    public static String getCoreVirtualPublicString(NikitaResponse nikitaResponse, String key) {
        return nikitaResponse.getNikitaFormPublic().getData(key).toString();
    }
    public static void setCoreVirtualPublicString(NikitaResponse nikitaResponse, String key, Object data) {
        nikitaResponse.setNikitaFormPublic(key, data);
    }
    
   
    private static Nset initparam =  Nset.newObject();
    public static Nset getNikitaParameters(NikitaResponse nikitaResponse){
            return initparam;
    }

    public static void saveOpenForms(NikitaResponse nikitaResponse) {
        try {
            if (nikitaResponse.getContent()!=null) {
                Vector<Component> components =  nikitaResponse.getContent().populateAllComponents();
                for (int i = 0; i < components.size(); i++) {	
                    //Nset n = nikitaResponse.getMobileGenerator().getData(nikitaResponse.getContent().getName()).getData(components.elementAt(i).getName());
                    Nset n = Nset.newArray();
                    n.addData(components.elementAt(i).getText() );//tetx
                    n.addData(components.elementAt(i).isVisible()?"1":"0");//v
                    n.addData(components.elementAt(i).isEnable()?"1":"0");//e
                    n.addData(components.elementAt(i).isFileComponent()?"file":"");//file
                    n.addData(components.elementAt(i).getType());//tipe
                    n.addData(components.elementAt(i).getComment());//commnet
                    n.addData(components.elementAt(i).isMandatory()?"1":"0");//m
                    nikitaResponse.getMobileGenerator().getData(nikitaResponse.getContent().getName()).setData(components.elementAt(i).getName(), n);
                }
            }
        } catch (Exception e) {}
    }
    public static void setVirtual(NikitaResponse nikitaResponse, String key,  Object data) {

    }
    public static Nset getStreamFormsWithCurrentForm(NikitaResponse nikitaResponse,String curform){
          return  Nset.newObject();
    }
    public static Nset getStreamForms(NikitaResponse nikitaResponse){
    	return getStreamForms(nikitaResponse, new String[0]);
    }
    public static Nset getStreamForms(NikitaResponse nikitaResponse,String...fname){
        Nset result = Nset.newObject();Nset forms = Nset.newObject();
        
        
       
        
        if (fname!=null && fname.length>=1) {
                String[] keys =  nikitaResponse.getMobileGenerator().getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                        for (int j = 0; j < fname.length; j++) {
                                if (keys[i].equals(fname[j])) {
                                      result.setData(keys[i], nikitaResponse.getMobileGenerator().getData(fname[j]));
                                      break;
                                }
                        } 
                }		
        }else{
             result  = nikitaResponse.getMobileGenerator();
        }
		
        //result.setData("nikitageneratorformname", forms);
        result.setData( Utility.MD5("init").toLowerCase(Locale.ENGLISH ), Nset.newObject().setData("nikitageneratorcurrentform",nikitaResponse.getContent().getName()).setData("nikitapublicform", nikitaResponse.getNikitaFormPublic())     );
        return result;
    }
      
    
}
