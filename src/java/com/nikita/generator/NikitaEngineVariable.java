/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Base64Coder;
import com.rkrzmail.nikita.utility.Utility;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author rkrzmail
 */
public class NikitaEngineVariable {
    public interface OnVariableListener{
        public Object onVirtual(String key);
        public Object onFunctionArray(Object currdata, Nset array);
        public Object onFunctionCast(Object currdata, String cast);
    }
    public static Object getVirtualVariable(String varname, OnVariableListener listener){
        VarResult result = parseVariable(varname);
        if (result.getData()!=null) {
            return  result.getData();
        }else{
            
            Object 
            data = getCurrentEngineInfo(result.getKeyName()); 
            if (data==null) {
                data = listener.onVirtual(result.getKeyName());
            }
            
            if (result.getFunctionArray()!=null) {
                Nset array =Nset.readJSON(result.getFunctionArray());
                
                data=listener.onFunctionArray(data, array);
            }
            if (result.getFunctionCast()!=null) {
                data=parseVariableCastDate(data, result.getFunctionCast());  
                data=parseVariableCastString(data, result.getFunctionCast());
                      
                data=listener.onFunctionCast(data, result.getFunctionCast());
            }           
            return  data;
        }
    }
    private static Object getCurrentEngineInfo(String key){
        if (key.equals("@+COREMYSQL")) {
            return NikitaConnection.CORE_MYSQL;
        }else if (key.equals("@+COREORACLE")) {
            return NikitaConnection.CORE_ORACLE;
        }else if (key.equals("@+CORESQLSERVER")) {
            return NikitaConnection.CORE_SQLSERVER;
        }else if (key.equals("@+CORESQLITE")||key.equals("@+CORESQLLITE")) {
            return NikitaConnection.CORE_SQLITE; 
        }else if (key.startsWith("@+BUTTON")) {
            return key.substring(2).toLowerCase();
        }else if (key.equals("@+ENTER")) {
            return "\r\n";
        }else if (key.equals("@+SPACE")) {
            return " ";
        }else if (key.equals("@+SPACE32")) {
            return "                                ";
        }else if (key.equals("@+TAB")) {
            return "\t";
        }else if (key.equals("@+EMPTYSTRING")) {
            return "";
        }else if (key.equals("@+VERSION")) {
            return "WEB 1.0.13 Beta";//MOBILE DEKSTOP
        }else if (key.equals("@+NOW")) {
            return Utility.Now();
        }else if (key.equals("@+TIME")) {
            return System.currentTimeMillis();
        }else if (key.equals("@+RANDOM")) {
            StringBuffer sb = new StringBuffer();
            Random randomGenerator = new Random();
            for (int idx = 1; idx <= 16; ++idx){
                sb.append( randomGenerator.nextInt(100)  );
            }
            return sb.toString();
        }else if (key.equals("@+FILESEPARATOR")) {    
            return NikitaService.getFileSeparator();
        }
        return null;        
    }
    public static Object parseVariableFunctionArray(Object data, String func){
        if (func!=null && func.startsWith("[")&& func.equals("]")) {
                        
            
        }
        return data;
    }
    public static Object parseVariableCast(Object data, String cast){
        if (cast!=null && !cast.equals("")) {
            if (cast.equals("int")||cast.equals("integer")) {
                
            }else if (cast.equals("long")||cast.equals("number")) {
                
            }else if (cast.equals("double")||cast.equals("float")||cast.equals("single")||cast.equals("decimal")) {   
            
            }else if (cast.equals("string")) {
                
            }else if (cast.equals("nset")) {
                
            }else if (cast.equals("nikitaset")) {                
                
            }else if (cast.equals("json")) {                
                          
            }else if (cast.equals("comma")||cast.equals("csv")) {
                
            }else if (cast.equals("length")||cast.equals("rows")) {
                
            }else if (cast.equals("error")) {
                
            }else if (cast.equals("header")||cast.equals("headernset")) {
                
            }else if (cast.equals("data")||cast.equals("datanset")) {
                
            }else if (cast.equals("length")||cast.equals("rows")) {
            }
           
            
        }
        return data;
    }
    public static Object parseVariableCastDate(Object data, String cast){
        if (cast!=null && !cast.equals("")) {
            if (cast.equals("fdate")||cast.equals("fdatenumber")||cast.equals("fdateint")||cast.equals("fdatelong")) {
                if (data instanceof String) {
                    return Utility.getDateTime((String)data);
                }else if (data instanceof Long) {
                    return  (Long)data;
                }
            }else if (cast.equals("fdateview")) {
                if (data instanceof String) {
                    long l = Utility.getDate((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "dd/MM/yyyy");
                    }
                }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "dd/MM/yyyy");
                    }
                }
            }else if (cast.equals("fdatetimeview")) { 
                if (data instanceof String) {
                    long l = Utility.getDateTime((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                    }
                }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                    }
                }
            }else if (cast.equals("fdatedb")||cast.equals("todate")) {   
                if (data instanceof String) {
                    long l = Utility.getDate((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd");
                    }
                }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd");
                    }
                }
            }else if (cast.equals("fdatetimedb")||cast.equals("todatetime")) {   
                //yyyy-mm-dd hh:nn:ss
                if (data instanceof String) {
                    long l = Utility.getDateTime((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                    }
                 }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                    }
                }
            }else if (cast.equals("fdatetmaxdb")||cast.equals("todatetmax")) {   
                if (data instanceof String) {
                    long l = Utility.getDateTime((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                    }
                 }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                    }
                }
            }else if (cast.equals("fdatetnowdb")||cast.equals("todatetnow")) {   
                //yyyy-mm-dd hh:nn:ss
                if (data instanceof String) {
                    long l = Utility.getDateTime((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                    }
                 }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                    }
                }
            }else if (cast.equals("fdatetmmindb")||cast.equals("todatetmin")) {   
                //yyyy-mm-dd hh:nn:ss
                if (data instanceof String) {
                    long l = Utility.getDateTime((String)data);
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                    }
                 }else if (data instanceof Long) {
                    long l = (Long)data;
                    if (l!=0) {
                        return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                    }
                }
            }
        }
        return data;
    }
    public static Object parseVariableCastString(Object data, String cast){
        if (cast!=null && !cast.equals("")) {
            if (cast.startsWith("escape")||cast.startsWith("unescape")||cast.startsWith("encode")||cast.startsWith("decode")) {  
                String buffer = "";
                if (data instanceof String) {
                    buffer =  StringEscapeUtils.escapeSql((String)data );
                }else if (data instanceof Nikitaset) {
                    buffer =  StringEscapeUtils.escapeSql(((Nikitaset)data).toNset().toString()) ;
                }else if (data instanceof Nset) {
                    buffer =  StringEscapeUtils.escapeSql(((Nset)data).toString()) ;
                }   
                
                if (cast.equals("escapesql")) {
                   return  StringEscapeUtils.escapeSql(buffer) ; 
                }else if (cast.equals("escapehtml")) {
                   return  StringEscapeUtils.escapeHtml(buffer) ; 
                }else if (cast.equals("escapejs")) {
                   return  StringEscapeUtils.escapeJavaScript(buffer) ; 
                }else if (cast.equals("escapejava")) {
                   return  StringEscapeUtils.escapeCsv(buffer) ; 
                }else if (cast.equals("escapecsv")) {
                   return  StringEscapeUtils.escapeJava(buffer) ; 
                //==============================================//
                }else if (cast.equals("unescapehtml")) {
                   return  StringEscapeUtils.unescapeHtml(buffer) ; 
                }else if (cast.equals("unescapejs")) {
                   return  StringEscapeUtils.unescapeJava(buffer) ; 
                }else if (cast.equals("unescapejava")) {
                   return  StringEscapeUtils.unescapeJavaScript(buffer) ; 
                }else if (cast.equals("unescapecsv")) {
                   return  StringEscapeUtils.unescapeCsv(buffer) ; 
                //==============================================//
                }else if (cast.equals("encodeurl")) {
                   return URLEncoder.encode( buffer   )   ; 
                }else if (cast.equals("decodeurl")) {
                    return URLDecoder.decode(buffer   )   ; 
                }else if (cast.equals("encodebase64")) {                 
                   return  Base64Coder.encodeString(buffer)  ; 
                }else if (cast.equals("decodebase64")) {
                   return   Base64Coder.decodeString(buffer)  ; 
                }
            }else if (cast.equals("trim")) {  
                 
            }else if (cast.equals("md5")) {  
                 
            }
        }
        return data;
    }
    
    public static VarResult parseVariable(String key){
        VarResult result = new VarResult();        
        if (key.startsWith("@")||key.startsWith("$")||key.startsWith("!")) {
            if (key.startsWith("@#")) {
                result.setupResult(null, null, null, key.substring(2));
            }else if ( (key.startsWith("@[") && key.trim().endsWith("]") ) || (key.startsWith("@{") && key.trim().endsWith("}"))) {
                //key.substring(1) JSON
                result.setupResult(null, null, null, Nset.readJSON(key.substring(1)));
            }else{
                String cast = "";
                String func = "";
                
                key=key.trim();
                if (key.endsWith(")")) {
                    if (key.contains("(")) {
                        cast = key.substring(key.lastIndexOf("(")+1, key.length()-1) ;
                        key=key.substring(0, key.lastIndexOf("(")).trim();
                    }
                }              
                
                if (key.endsWith("]")) {
                    if (key.contains("[")) {
                        func = key.substring(key.lastIndexOf("[")+1, key.length()-1) ;
                        key=key.substring(0, key.lastIndexOf("[")).trim();
                    }
                }
                result.setupResult(key, func, cast, null);
            }
        }else{
            result.setupResult(key, null, null, null);
        }      
        return result;
    }
    
    public static class VarResult{
        private String key ;
        private String func;
        private String cast;
        private Object dataresult = null ;
        
        private void setupResult(String key, String func, String cast, Object data){
            this.dataresult=data;
            this.key=key;
            this.func=func;
            this.cast=cast;
            
        }
        public String getKeyName(){
            return key;
        }
        public String getFunctionArray(){
            return func;
        }
        public String getFunctionCast(){
            return cast;
        }
        public Object getData(){
            return dataresult;
        }
    }
}
