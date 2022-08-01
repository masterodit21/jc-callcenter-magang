/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author rkrzmail
 */
public class NikitaMobile {
    //mobile only    
    public NikitaMobile (String sessionNikitaMobile){
        this.sessionNikitaMobile=sessionNikitaMobile;
        readMobileActivity();
 
    }
    private String sessionNikitaMobile = "";
    private Nset readMobileActivity(){
        try {   
            FileInputStream fis = new FileInputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +sessionNikitaMobile+"nma.nma");
            currMobileActivity = Nset.readJSON(fis);
            fis.close();
            if (currMobileActivity.isNsetObject()) {
                return currMobileActivity;
            }     
            currMobileActivity = Nset.newObject();
        } catch (Exception e) {     }
        return Nset.newObject();
    }
    private void writeMobileActivity(Nset data){
        try {   
            FileOutputStream fos = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() + sessionNikitaMobile+"nma.nma");
            data.toJSON(fos);
            fos.close();   
        } catch (Exception e) {   }
    }
    private void deleteMobileActivity(){
        try {            
            new File(NikitaService.getDirTmp() + NikitaService.getFileSeparator() + sessionNikitaMobile+"nma.nma").delete();
        } catch (Exception e) {   }
    }
    public Nset getMobileActivityStream(){
        return currMobileActivity;
    }
    public Nset currMobileActivity = Nset.newObject();
    public void clearMobileActivityStream(String...fname){
            if (fname!=null && fname.length>=1) {
                    for (int t = 0; t < fname.length; t++) {
                            String fn = fname[t];
                            currMobileActivity.setData(fn, Nset.newObject());
                    }	
                    writeMobileActivity(currMobileActivity );    
            }else{
                    currMobileActivity = Nset.newObject();//clearall
                    deleteMobileActivity();
            }        
    }
    public void setMobileActivityStream(String json){
        currMobileActivity = Nset.readJSON(json);
    }
    public void saveMobileActivityStream(String fname, Nset data){
        //getContent().getName()==Forname
        writeMobileActivity(currMobileActivity.setData(fname, data));
    }
   
}
