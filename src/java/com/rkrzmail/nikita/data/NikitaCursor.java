/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

import com.rkrzmail.nikita.data.Nikitaset;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class NikitaCursor implements ICursor{
  //{"nfid":"NikitaCursor","error":"","info":null,"header":[],"data":[[]]}
    private String error;
    private Object info;
    private Vector<String> header = new Vector<String>();
    private Vector<String> data = new Vector<String> ();
    
    private ResultSet resultSet ;
    
    public NikitaCursor(ResultSet resultSet){
        try {                    
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                header.addElement(resultSet.getMetaData().getColumnName(i+1));
            }
        } catch (Exception e) { }        
    } 
    public static boolean isNikitaCursor(Nset x){
        if (x.getInternalObject() instanceof Hashtable) {
            if (x.getData("nfid").toString().equals("NikitaCursor")) {
                Hashtable keys = (Hashtable)x.getInternalObject();
                if (keys.containsKey("error") && keys.containsKey("info") && keys.containsKey("header") & keys.containsKey("data")) {
                    return true;
                }    
            }            
        }        
        return false;
    }
    public NikitaCursor(ICursor cursor){
        error=cursor.getError();
        info=cursor.getInfo();
        header=cursor.getDataAllHeader();
        data=cursor.getDataRowsVector();
    }
    public NikitaCursor(Nset nikitaset){
        error=nikitaset.getData("error").toString();
        info=nikitaset.getData("info").getInternalObject();
        try {
            header=(Vector<String>)nikitaset.getData("header").getInternalObject();
        } catch (Exception e) {}
        try {
           data=(Vector<String>)nikitaset.getData("data").getInternalObject();
        } catch (Exception e) {}         
    }
    public NikitaCursor(String error){
        this.error=error;
    }
    public NikitaCursor(Vector<String> header, Vector<String> data){
        this.header=header;
        this.data=data;
    }
    public void setAliasName(Vector<String> header){
        this.header=header;
    }
    public void setInfo(Object info){
        this.info=info;
    }
    public Nset toNset(){
        Hashtable mst = new Hashtable();
        mst.put("nfid","NikitaCursor");
        mst.put("error", this.getError());
        mst.put("info", this.getInfo()!=null?this.getInfo():Nset.newNull());
        mst.put("header", this.getDataAllHeader());
        mst.put("data", this.getDataRowsVector()) ;
        return new Nset(mst);
    }
    
    @Override
    public Vector<String> getDataAllHeader() {
        return this.header!=null?this.header:new Vector<String>();
    }  

    @Override
    public String getError() {
        return error!=null?error:"";
    }

    @Override
    public Object getInfo() {
        return info;
    }
    
    @Override
    public int getRows() {
        if (resultSet!=null) {
            try {
                return resultSet.getRow();
            } catch (Exception e) {  }
        }
        return -1;
    }

    @Override
    public int getCols() {
        return getDataAllHeader().size();
    }
    
    @Override
    public String getHeader(int col) {
        try {
            String result = header.elementAt(col);
            if (result!=null) {
                return result;
            }
        } catch (Exception e) {}
        return "";
    }
 
    @Override
    public int getRow() {
        if (resultSet!=null) {
            try {
                return resultSet.getRow();
            } catch (Exception e) {  }
        }
        return 0;
    }
   
    public boolean moveNext() {
         if (resultSet!=null) {
            try {
                data = new Vector<String>();
                
                if (resultSet.next()) {                    
                    Vector<String> field = new Vector<String>();
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        field.addElement(resultSet.getString(i+1));
                    }                    
                    return true;
                }
                
            } catch (Exception e) {  }
        }
        return false;
    }   

    @Override
    public String getText(int col) {
         try {
            String result = data.elementAt(col);
            if (result!=null) {
                return result;
            }
        } catch (Exception e) {}
        return "";
    }

    @Override
    public String getText(String colname) {
         return getText(header.indexOf(colname));
    }

    @Override
    public Vector<String> getDataRowsVector() {
         return data;
    }
    
    
}
