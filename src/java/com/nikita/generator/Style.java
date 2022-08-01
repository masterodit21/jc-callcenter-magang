/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

 
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
import java.util.Vector;

/**
 * created by 13k.mail@gmail.com
 */
public class Style {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String DISPLAY = "display";
    public static final String POSITION = "position";
    public static final String BACKGROUND = "background";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String GRAVITY = "gravity";
    
    
    public static final String COLOR = "color";
    public static final String TEXT_ALIGN = "text-align";    
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_WIGHT = "font-weight";
    
    public static final String N_GRID_CELL_ALIGN = "nikita-grid-cell-align";
    public static final String N_ALIGMNET = "nikita-align";
    
    public static final String MARGIN           = "margin";
    public static final String MARGIN_TOP       = "margin-top";
    public static final String MARGIN_BOTTOM    = "margin-bottom";
    public static final String MARGIN_LEFT      = "margin-left";
    public static final String MARGIN_RIGHT     = "margin-right";
 
    public static final String PADDING           = "padding";
    public static final String PADDING_TOP       = "padding-top";
    public static final String PADDING_BOTTOM    = "padding-bottom";
    public static final String PADDING_LEFT      = "padding-left";
    public static final String PADDING_RIGHT     = "padding-right";    
        
    public static Style createStyle(){
        return new Style();
    }
    public static Style createStyle(Style style){
        if (style!=null) {
            return style;
        }
        return new Style();
    }
    public static Style createStyle(String name, String value){
        return new Style().setStyle(name, value);
    }
    
    
    public Style(){}
    private Style(Nset n){
        style = n.getData("style");
        cls   = n.getData("class");
        attr  = n.getData("attr");
    }
    public String getViewStylePrefix(String prefix){
        StringBuilder buffer = new StringBuilder();
        String[] names = style.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().startsWith(prefix)) {
                buffer.append(names[i].substring(prefix.length())).append(":").append(style.getData(names[i]).toString().trim()).append(";");                               
            }   
        } 
        return buffer.toString();
    }
    public String getViewStylePrefixHide(String...prefix){
        StringBuilder buffer = new StringBuilder();
        String[] names = style.getObjectKeys();
        for (int i = 0; i < names.length; i++) {  
            if (!startsWiths(names[i].trim(), prefix)) {
                buffer.append(names[i]).append(":").append(style.getData(names[i]).toString().trim()).append(";");                               
            }   
        }
        return buffer.toString();
    }
    public String getViewAttrPrefix(String prefix){
        StringBuilder buffer = new StringBuilder();
        String[] names = attr.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().startsWith(prefix)) {
                buffer.append(" ").append(names[i].substring(prefix.length())).append("=\"").append(attr.getData(names[i]).toString().trim()).append("\" ");                               
            }   
        }
        return buffer.toString();
    }
    public String getViewAttrPrefixHide(String...prefix){
        StringBuilder buffer = new StringBuilder();
        String[] names = attr.getObjectKeys();
        for (int i = 0; i < names.length; i++) {  
            if (!startsWiths(names[i].trim(), prefix)) {
                buffer.append(" ").append(names[i]).append("=\"").append(attr.getData(names[i]).toString().trim()).append("\"");                               
            }   
        }
        return buffer.toString();
    }
    
     
     
    private boolean startsWiths(String string, String...find){
        if (find!=null) {
            for (String find1 : find) {
                if (string.startsWith(find1)) {
                    return true;
                }
            }   
        }
        return false;
    }
     
     
    @Override
    public Style clone() {
        return new Style(Nset.newObject().setData("style", style).setData("class", cls).setData("attr", attr));
    }
        
    private Nset cls    = Nset.newArray();
    private Nset style  = Nset.newObject();
    private Nset attr   = Nset.newObject();
    
    public Nset getInternalObject(){
        return Nset.newObject().setData("style", style).setData("class", cls).setData("attr", attr);
    }    
    public Nset getInternalStyle(){
        return style;
    }
    public Nset getInternalAttr(){
        return attr;
    }
    
    public Style setAttrIfNoExist(String name, String value){
        if (!attr.containsKey(name)) {
            attr.setData(name, value);
        }                
        return this;
    }
    public Style setStyleIfNoExist(String name, String value){
        if (!style.containsKey(name)) {
            style.setData(name, value);
        }                
        return this;
    }
    public Style setStyle(String name, String value){
        style.setData(name, value);
        return this;
    }
    public Style addClass(String value){
        cls.addData(value);
        return this;
    }
    public Style setAttr(String name, String value){
        attr.setData(name, value);
        return this;
    }
    public void removeClass(String value){
        try {
            for (int i = 0; i < cls.getArraySize(); i++) {
                if (cls.getData(i).toString().equals(value)) {
                    ((Vector)cls.getInternalObject()).removeElementAt(i);break;
                }
            }
        } catch (Exception e) { }
    }
    private void removeStyle(String name){
        try {
            ((Hashtable)style.getInternalObject()).remove(name);
        } catch (Exception e) { }
    }
    public String getViewClass(){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cls.getArraySize(); i++) {
             buffer.append(" ").append(cls.getData(i).toString()).append(" ");            
        }
        return buffer.toString();
    }
    public String getViewAttr(){
        StringBuffer buffer = new StringBuffer();
        String[] names = attr.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
             buffer.append(" ").append(names[i]).append("=\"").append(attr.getData(names[i]).toString()).append("\" ");            
        }
        return buffer.toString();
    }
    public String getViewAttr(String exception){
        StringBuffer buffer = new StringBuffer();
        String[] names = attr.getObjectKeys();
        Vector<String> ex = Utility.splitVector(exception, ";");
        for (int i = 0; i < names.length; i++) {            
            if (exception.length()>=2) {
                boolean b = false;
                for (int j = 0; j < ex.size(); j++) {
                    if (names[i].trim().startsWith(ex.elementAt(j))) {
                        b=true;
                        break;
                    }
                }
                if (!b) {
                    buffer.append(" ").append(names[i]).append("=\"").append(attr.getData(names[i]).toString()).append("\" ");      
                }
            }else{
                buffer.append(" ").append(names[i]).append("=\"").append(attr.getData(names[i]).toString()).append("\" ");        
            }                      
        }
        return buffer.toString();
    }
     public String getViewAttr(String filterstart, String filterreplace){
        StringBuffer buffer = new StringBuffer();
        String[] names = attr.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().startsWith(filterstart)) {
                if (filterreplace.equals("")) {
                    buffer.append(" ").append(names[i]).append("=\"").append(attr.getData(names[i]).toString()).append("\" ");  
                }else{
                    buffer.append(" ").append(names[i].trim().replace(filterstart, filterreplace.trim())).append("=\"").append(attr.getData(names[i]).toString()).append("\" ");  
                } 
            }
        }
        return buffer.toString();
    }
    public String getViewStyle(){
        StringBuffer buffer = new StringBuffer();
        String[] names = style.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
             buffer.append(names[i].trim()).append(":").append(style.getData(names[i]).toString().trim()).append(";");            
        }
        return buffer.toString();
    }
    public String getViewStyle(String exception){
        StringBuffer buffer = new StringBuffer();
        String[] names = style.getObjectKeys();
        Vector<String> ex = Utility.splitVector(exception, ";");
        for (int i = 0; i < names.length; i++) {
            if (exception.length()>=2) {
                boolean b = false;
                for (int j = 0; j < ex.size(); j++) {
                    if (names[i].trim().startsWith(ex.elementAt(j))) {
                        b=true;
                        break;
                    }
                }
                if (!b) {
                    buffer.append(names[i].trim()).append(":").append(style.getData(names[i]).toString().trim()).append(";");      
                }
            }else{
                buffer.append(names[i].trim()).append(":").append(style.getData(names[i]).toString().trim()).append(";");           
            }
        }
        return buffer.toString();
    }
    public String getViewStyle(String filterstart, String filterreplace){
        StringBuffer buffer = new StringBuffer();
        String[] names = style.getObjectKeys();
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().startsWith(filterstart)) {
                if (filterreplace.equals("")) {
                    buffer.append(names[i].trim()).append(":").append(style.getData(names[i]).toString().trim()).append(";");  
                }else{
                    buffer.append(names[i].trim().replace(filterstart, filterreplace.trim())).append(":").append(style.getData(names[i]).toString().trim()).append(";");  
                }              
            }   
        }
        return buffer.toString();
    }
    private static void nv3Style(String style){
    	if (style.trim().startsWith("nv3:")) {
            /*
            nv3:##############:
                .nasndas{
                    asafasd;
                }
                .anasndas{
                    asafasd;
                }
            ##############
            nv3:style:##############:
                .nasndas{
                    asafasd;
                }
                .anasndas{
                    asafasd;
                }
            ##############    
            nv3:script:##############:
            
            ##############
            nv3:mobile:##############:
                .nasndas{
                    asafasd;
                }
                .anasndas{
                    asafasd;
                }
            ##############
            nv3:\r\n:
                nfid=nanadi
            nv3:\n:
                nfid=nanadi
                background:#red
                n-action-name      
            nv3::
                nfid=nanadi;
                background:#red
                n-action-name;       
         
            */	
            
            
        }
    }
    public static Style createStyle(String style){
        
        
        Style style1 = new Style();
        style = Utility.replace(style, "/@+CONTEXT/", NikitaService.getBaseContext()+"/");
        
        style = Utility.replace(style, "\r", "\n");
        style = Utility.replace(style, ";", "\n");
        style = Utility.replace(style, "\n\n", "\n");
        style = Utility.replace(style, "\n\n", "\n");
        
        style = Utility.replace(style, "|", ";");
        Vector<String> v = Utility.splitVector(style, "\n");
        
        for (int i = 0; i < v.size(); i++) {
            if (v.elementAt(i).trim().startsWith("n-arg")) {
            }else if (v.elementAt(i).contains("class=")||v.elementAt(i).contains("class-add=")) {
                String s= v.elementAt(i);
                style1.addClass(s.substring(s.indexOf("=")+1).trim());
            }else if (v.elementAt(i).contains("class-remove=")) {
                String s= v.elementAt(i);
                style1.removeClass(s.substring(s.indexOf("=")+1).trim());
            } else if (v.elementAt(i).contains(":")) {
                String s= v.elementAt(i);
                style1.setStyle(s.substring(0,s.indexOf(":")).trim(), s.substring(s.indexOf(":")+1).trim());
            }else if (v.elementAt(i).contains("=")) {
                String s= v.elementAt(i);
                style1.setAttr(s.substring(0,s.indexOf("=")).trim(), s.substring(s.indexOf("=")+1).trim());            
            }
        }
        return style1;
    }
}
