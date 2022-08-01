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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author rkrzmail
 */
public class NikitaEngine implements Cloneable{
    public static final String NFID_ARRAY_INDEX = "_nfid_index_";
    public static String getFormNameNoIndex(String form){  
		if (form.contains(NFID_ARRAY_INDEX)) {
			String s = form.substring(form.indexOf(NFID_ARRAY_INDEX)+NFID_ARRAY_INDEX.length());
			if (Utility.isNumeric(s)) {
				return form.substring(0, form.indexOf(NFID_ARRAY_INDEX));
			}
		}
    return form ;
    }
	public static String getFormIndex(String form){  
		if (form.contains(NFID_ARRAY_INDEX)) {
			String s = form.substring(form.indexOf(NFID_ARRAY_INDEX)+NFID_ARRAY_INDEX.length());
			if (Utility.isNumeric(s)) {
                            return s;
			}
		}
        return "" ;
    }
        
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  
    }
    public NikitaEngine cloneNikitaEngine() {
        try {
            return (NikitaEngine)clone();
        } catch (Exception e) { }
        return new NikitaEngine();
    }
    
    private static String enginpath ;
    static {
        enginpath = new NikitaEngine().getClass().getResource("").getFile();
        if (enginpath.indexOf("nikita")>=0) {
            enginpath=enginpath.substring(0, enginpath.lastIndexOf("nikita"))+"nv3"+NikitaService.getFileSeparator();
        }
    }
    public static boolean isNv3Exist(String form, boolean devmode){        
        return !devmode && new File(enginpath+getFormNameNoIndex(form)+".nv3").exists();
    }
    public static Nikitaset getForm(NikitaConnection nikitaConnection, String form){        
        return queryForm(nikitaConnection, getFormNameNoIndex(form));
    }
    public static Nikitaset getComponents(NikitaConnection nikitaConnection, String form){
        return queryComponents(nikitaConnection, getFormNameNoIndex(form));
    }
    public static Nikitaset getComponentLogic(NikitaConnection nikitaConnection, String form, String compid){
        return queryComponentLogic(nikitaConnection, compid);
    }
    private boolean fileexist = false;
    private boolean isSourceFileExist(){
      return fileexist;  
    }
    private String frmname;
    private Nikitaset form;
    private Nikitaset component;
    private Nset componentlogic;
    private NikitaConnection nikitaConnection;
    private boolean devmode =false;//default
    private boolean  singlelogic  = false;//default
    private NikitaEngine (){        
    }
    public NikitaEngine (NikitaConnection nikitaConnection, String formname, boolean devmode){  
        this(nikitaConnection, formname, devmode, false);
    }
    public NikitaEngine (NikitaConnection nikitaConnection, String formname, boolean devmode, boolean singlelogic){        
        this.singlelogic = singlelogic;
        this.nikitaConnection = nikitaConnection;
        this.frmname = formname;
        this.devmode=devmode;
        parseSourceFile();
    }    
    public static void clean ( String form){
        try {
            new File( getNv3FileName(form) ).delete();
        } catch (Exception e) { }
    }
    public static void cleanAll(  ){
        try {
            File[] files = new File(getNv3FileName("")).listFiles();
            for (int i = 0; i < files.length; i++) {
                clean(files[i].getName());
            }            
        } catch (Exception e) { }
    }
    private boolean isNv3Exist(String fname){
        return new File(getNv3FileName(fname)).exists();
    }
    public boolean isNv3(){
        return fileexist;
    }
    public boolean isLogicAll(){
        return !singlelogic;
    }
    
    private static String getNv3FileName(String fname){
        fname = getFormNameNoIndex(fname);//19/08/2016
        if (fname.equals("")) {
        }else if (isNumeric(fname)) {    
            return enginpath+Utility.MD5(fname)+".nv3";
        }                
        return enginpath+fname+".nv3";
    }
    public static void compile (NikitaConnection nikitaConnection, String form){
        try {
            FileOutputStream fos = new FileOutputStream(getNv3FileName(form));
            writeNv3(createNv3(nikitaConnection, form), fos);
            fos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public static void compile (NikitaConnection nikitaConnection, String form, OutputStream out){
    	writeNv3(createNv3(nikitaConnection, form), out);
    }
    public static String viewNv3(String form){
        try {
            return Utility.readInputStreamAsString(getNv3FileName(form));
        } catch (IOException ex) { ex.printStackTrace(); }
        return "";
    }
    public static String createNv3 (NikitaConnection nikitaConnection, String form){
        Nset n = Nset.newObject(); Nset nlogic = Nset.newObject();
        /*
        n.setData("nfid","nv3");Nikitaset frm = NikitaEngine.queryForm(nikitaConnection, form);
        n.setData("form", frm.toNset());
        Nikitaset components = NikitaEngine.queryComponents(nikitaConnection, frm.getText(0, "formid"));
        n.setData("component", components.toNset());
        for (int i = 0; i < components.getRows(); i++) {
            nlogic.setData(components.getText(i, "compid"), queryComponentLogic(nikitaConnection, components.getText(i, "compid")).toNset());            
        }
        n.setData("logic", nlogic);         
        */
        n.setData("date", Utility.Now());
        n.setData("version", "nv3-v1.0");
        n.setData("nfid","nv3-logicall"); Nikitaset frm = NikitaEngine.queryForm(nikitaConnection, form);
        n.setData("form", frm.toNset());
        n.setData("component",  NikitaEngine.queryComponents(nikitaConnection, frm.getText(0, "formid")).toNset());
        n.setData("logicall",  NikitaEngine.queryComponentsLogicAll(nikitaConnection, frm.getText(0, "formid")).toNset());         
        //n.setData("logic",  nv3LogicAlltoLogic( NikitaEngine.queryComponentsLogicAll(nikitaConnection, frm.getText(0, "formid")) ) );
        return n.toJSON();
    }
    public static void compile (String nv3Data, String form){
    	try {
            FileOutputStream fos = new FileOutputStream(getNv3FileName(form));
            writeNv3(nv3Data, fos);
            fos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    


    private static Nset nv3LogicAlltoLogic(Nikitaset logicall){
        Nset nlogic = Nset.newObject(); 
        for (int i = 0; i < logicall.getRows(); i++) {
            if (logicall.getText(i, "compid").equals("")||logicall.getText(i, "compid").equalsIgnoreCase("null")) {                
            }else if (nlogic.containsKey(logicall.getText(i, "compid"))) {
                nlogic.getData(logicall.getText(i, "compid")).addData(logicall.getDataAllVector().elementAt(i));
            }else{
                nlogic.setData(logicall.getText(i, "compid"), Nset.newArray().addData(logicall.getDataAllVector().elementAt(i)));                
            }            
        }
        String[] keys = nlogic.getObjectKeys();
        for (int i = 0; i < keys.length; i++) {
            Nikitaset v = new Nikitaset( (Vector<String>)logicall.getDataAllHeader().clone() , (Vector<Vector<String>>)nlogic.getData(keys[i]).getInternalObject()   );
            nlogic.setData(keys[i], v.toNset());
        }
        return nlogic;
    }
    private void parseSourceFile(){    
        if (frmname.equals("")) {
            form = new Nikitaset("");
            component = new Nikitaset("");
            componentlogic = Nset.newObject();
        }else if (devmode) {
            //System.out.println("devmode:"+frmname);
            form = queryForm(nikitaConnection, frmname);;
            component = queryComponents(nikitaConnection, form.getText(0, "formid"));;
            if (!singlelogic) {
                componentlogic = nv3LogicAlltoLogic( queryComponentsLogicAll(nikitaConnection, form.getText(0, "formid")) );
            }            
        }else  if (isNv3Exist(frmname)) {
            // System.out.println("nv3:"+frmname);
            fileexist=true;
            Nset n = Nset.readJSON(readNv3(   getNv3FileName(frmname) ));
            form = new Nikitaset(n.getData("form"));
            component = new Nikitaset(n.getData("component"));
            
            if (n.getData("nfid").toString().equals("nv3-logicall")) {
                singlelogic=false;
                componentlogic = nv3LogicAlltoLogic( new Nikitaset( n.getData("logicall") )  );
            }else{
                singlelogic=false;
                componentlogic = n.getData("logic");
            }            
        }else{
            //System.out.println("parseSourceFile:"+frmname);
            form = queryForm(nikitaConnection, frmname);;
            component = queryComponents(nikitaConnection, form.getText(0, "formid"));;
            if (!singlelogic) {
                componentlogic = nv3LogicAlltoLogic( queryComponentsLogicAll(nikitaConnection, form.getText(0, "formid")) );
            }
        }
    }  
    private static Nikitaset queryForm(NikitaConnection nikitaConnection, String frmname){
        return nikitaConnection.QueryPage(1, 1, "SELECT formid,formname,formtitle,formtype,formstyle FROM web_form WHERE "+(isNumeric(frmname)?"formid":"formname")+" = ?",frmname);
    }
    private static Nikitaset queryComponents(NikitaConnection nikitaConnection, String frmname){
        return nikitaConnection.Query("SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC;", frmname);
    }
    private static Nikitaset queryComponentLogic(NikitaConnection nikitaConnection, String compid){
        return nikitaConnection.Query("SELECT expression,action,routeindex FROM web_route WHERE compid = ? ORDER BY routeindex ASC;", compid);
    }
    private static Nikitaset queryComponentsLogicAll(NikitaConnection nikitaConnection, String frmid){            
        return nikitaConnection.Query("SELECT web_route.expression,web_route.action,web_route.routeindex, web_route.compid FROM web_route WHERE compid IN (SELECT compid FROM web_component WHERE formid = ? )  ORDER BY web_route.routeindex ASC ;", frmid);    
        //return nikitaConnection.Query("SELECT web_route.expression,web_route.action,web_route.routeindex, web_route.compid FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE web_component.formid = ? ORDER BY web_route.routeindex ASC;", frmid);
    }
    public Nikitaset getForm(){  
        if (isSourceFileExist()) {
            return form;
        }else{
            return form;//queryForm(nikitaConnection, frmname);
        }
    }
    public Nikitaset getComponents(){        
        if (isSourceFileExist()) {
            return component;
        }else{
            return component;//queryComponents(nikitaConnection, frmname);
        }
    }
    public Nikitaset getComponentLogic(String compid){   
        if (isSourceFileExist()) {
            return new Nikitaset(componentlogic.getData(compid));
        }else{
            if (!singlelogic) {
                return new Nikitaset(componentlogic.getData(compid));//queryComponentLogic(nikitaConnection, compid);
            }else{
                return queryComponentLogic(nikitaConnection, compid);                 
            }            
        }
    }
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    private static String readNv3(String file) {
        String out = "";
        try {
            FileInputStream fos = new FileInputStream(file);
            out =  readNv3(fos);
            fos.close();
        } catch (Exception e) {  }
        return  out;        
    }
    private static String readNv3(InputStream input) {
        try {
            //GZIPInputStream is = new GZIPInputStream(input);   
            InputStream is=input;
            byte[] buffer = new byte[1024];
            int length;StringBuffer sb = new StringBuffer();
            while ((length = is.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, length));
            }
            return sb.toString();
        } catch (Exception e) { }
        return "";
   }
    private static void writeNv3(String json, OutputStream out) {
        try {
            //GZIPOutputStream gzip = new GZIPOutputStream(out);
            //gzip.write(json.getBytes());
            //gzip.close();
            
            out.write(json.getBytes());
        } catch (Exception e) { e.printStackTrace(); }
    }
}
