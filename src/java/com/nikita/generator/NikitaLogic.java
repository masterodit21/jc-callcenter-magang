/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Hashtable;
/**
 * created by 13k.mail@gmail.com
 */
public class NikitaLogic extends Nikitaset{
    
    private String formid = "";
    private String formname = "";
    private String formtitle = "";
    private String formstyle = "";
    private String formtype = "";
    private String formversion = "";
    private boolean isPrivate =false;
            
    /*
    private Hashtable<String, NikitaConnection> ncarr = new Hashtable<String, NikitaConnection>();
    public NikitaConnection getConnection(){
        
        return ncarr.get(NikitaConnection.LOGIC);
    }
    
    public NikitaConnection getConnection(String s){
        s=s.equals("")?"default":s;
                
        NikitaConnection nc =  ncarr.get(s);
        if (nc==null) {
            nc = NikitaConnection.getConnection(s);
            ncarr.put(s, nc);      
        }
        return nc;
    }
    public NikitaLogic(NikitaConnection nc) {
        super("");
        ncarr.put(NikitaConnection.LOGIC, nc);
    }    
    */
    
    private String fIndex = "";

    public String getFnameIndex() {
        return fIndex;
    }

    public void setFnameIndex(String fIndex) {
        this.fIndex = fIndex;
    }
    
    
    
    public NikitaLogic( ) {
        super("");
        
    }  
    
    public void setForm(String id, String name, String title, String type, String style){
        this.formid=id;
        this.formname=name;
        this.formtitle=title;
        this.formtype=type;
        this.formstyle=style;
    }
    public String getFormStyle(){
        return this.formstyle;
    }
     
    public String getFormType(){
        return this.formtype;
    }
    public String getFormId(){
        return this.formid;
    }
    
    public String getFormName(){
        return this.formname;
    }
    
    public String getFormTitle(){
        return this.formtitle;
    }
    
    public boolean isFormPrivate(){
        return this.isPrivate;
    }
    
    public String getFormVersion(){
        return this.formversion;
    }
        
    public void setFormVersion(String version, boolean local){
        this.isPrivate=local;
        this.formversion=version;
    }
    
    
     
    public String getText(int i){
        return "";
    }
    public String getText(String col){
        return "";
    }    
     
    public boolean expression;
   
    
    private int irow = 0;
    private int lcount = 0;
    public void setCurrentRow(int i){
        irow=i; 
    }
    public void setLoopCount(int m){
        lcount=m;
    }    
    public int getCurrentRow(){
        return irow;
    }
    public int getLoopCount(){
        return lcount;
    }
}
