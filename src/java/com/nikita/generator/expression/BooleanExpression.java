/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.expression;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.expression.IExpression;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;
/**
 * created by 13k.mail@gmail.com
 */
public class BooleanExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");
        
        String param = currdata.getData("code").toString();
        
        if (param.equalsIgnoreCase("iftrue")) {
            String p = response.getVirtualString(data.get("param").toString());            
            
            if ( p.equals("1")||p.toLowerCase().equals("true")  ) {
                return true;
            }
        }else if (param.equalsIgnoreCase("and")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            if ( val1 && val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("or")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            if ( val1 || val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("not")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            if ( val1 != val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("2and")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            String key3 = data.getData("param3").toString();
            boolean val3 = Boolean.valueOf(response.getVirtualString(data.get("param3").toString()));
            
            String key4 = data.getData("param4").toString();
            boolean val4 = Boolean.valueOf(response.getVirtualString(data.get("param4").toString()));
            
            if ( (val1 == val2) && (val3 == val4)) {
                return true;
            }
        }else if (param.equalsIgnoreCase("xor")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            String key3 = data.getData("param3").toString();
            boolean val3 = Boolean.valueOf(response.getVirtualString(data.get("param3").toString()));
            
            String key4 = data.getData("param4").toString();
            boolean val4 = Boolean.valueOf(response.getVirtualString(data.get("param4").toString()));
            
            if ( (val1 == val2) || (val3 == val4)) {
                return true;
            }
        }else if (param.equalsIgnoreCase("3and")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            String key3 = data.getData("param3").toString();
            boolean val3 = Boolean.valueOf(response.getVirtualString(data.get("param3").toString()));
            
            String key4 = data.getData("param4").toString();
            boolean val4 = Boolean.valueOf(response.getVirtualString(data.get("param4").toString()));
            
            String key5 = data.getData("param5").toString();
            boolean val5 = Boolean.valueOf(response.getVirtualString(data.get("param5").toString()));
            
            String key6 = data.getData("param6").toString();
            boolean val6 = Boolean.valueOf(response.getVirtualString(data.get("param6").toString()));
            
            if ( (val1 == val2) && (val3 == val4) && (val5 == val6)) {
                return true;
            }
        }else if (param.equalsIgnoreCase("3or")) {
            String key1 = data.getData("param1").toString();
            boolean val1 = Boolean.valueOf(response.getVirtualString(data.get("param1").toString()));
            
            String key2 = data.getData("param2").toString();
            boolean val2 = Boolean.valueOf(response.getVirtualString(data.get("param2").toString()));
            
            String key3 = data.getData("param3").toString();
            boolean val3 = Boolean.valueOf(response.getVirtualString(data.get("param3").toString()));
            
            String key4 = data.getData("param4").toString();
            boolean val4 = Boolean.valueOf(response.getVirtualString(data.get("param4").toString()));
            
            String key5 = data.getData("param5").toString();
            boolean val5 = Boolean.valueOf(response.getVirtualString(data.get("param5").toString()));
            
            String key6 = data.getData("param6").toString();
            boolean val6 = Boolean.valueOf(response.getVirtualString(data.get("param6").toString()));
            
            if ( (val1 || val2) && (val3 || val4) && (val5 || val6)) {
                return true;
            }
        }else if (param.equalsIgnoreCase("first")) {
            if (logic.getLoopCount()>=1) {
                return false;
            }
            return true;
        }else if (param.equalsIgnoreCase("iserror")) {
            String param1 = currdata.getData("args").getData("param1").toString();  
        
            Object obj = response.getVirtual(param1);
            if (obj instanceof Nikitaset) {
               
                return ((Nikitaset)obj).getError().length()>=1;
            }else if (obj instanceof Nset) {
                
            }
        }else if (param.equalsIgnoreCase("else")) {
            try {
                return !((Boolean)response.getVirtual("@EXPESSION"));
            } catch (Exception e) { }
            
        }else if (param.equalsIgnoreCase("isnikitasetrows")) {
            String param1 = currdata.getData("args").getData("param1").toString();  
        
            Object obj = response.getVirtual(param1);
            if (obj instanceof Nikitaset) {
                return ((Nikitaset)obj).getRows()>=1; 
            }
        }else if (param.equalsIgnoreCase("varlen")) {
            String param1 = currdata.getData("args").getData("param1").toString();  
            int l = Utility.getInt( response.getVirtualString(currdata.getData("args").get("param2").toString())  );  
            
            Object obj = response.getVirtual(param1);
            if (obj instanceof Nikitaset) {
                int i = ((Nikitaset)obj).getRows();
                return i < l && i> 0; 
            }else if (obj instanceof Nset) {
                int i = ((Nset)obj).getArraySize();
                if (i!=0) {
                    return i < l && i> 0; 
                }
                i = ((Nset)obj).getObjectKeys().length;
                return i < l && i> 0; 
            }else{
                
            }
        }else if (param.equalsIgnoreCase("filter")) {
            return request.getParameter("action").startsWith("page");
        }else if (param.equalsIgnoreCase("iif")) {
            String param1 = response.getVirtualString(data.getData("param1").toString());
            String parama = response.getVirtualString(data.getData("parama").toString());
            String param2 = response.getVirtualString(data.getData("param2").toString());
            String paramb = response.getVirtualString(data.getData("paramb").toString());
            String param3 = response.getVirtualString(data.getData("param3").toString());
            String paramc = response.getVirtualString(data.getData("paramc").toString());
            String param4 = response.getVirtualString(data.getData("param4").toString());
            String paramd = response.getVirtualString(data.getData("paramd").toString());
            String param5 = response.getVirtualString(data.getData("param5").toString());
            String param6 = response.getVirtualString(data.getData("param6").toString());
             
            boolean b = iif(iif(param1, parama, param2), paramb, iif(param3, paramc, param4));
            return iif(b, paramd, iif(param5, "=", param6));
          
        }else if (param.equalsIgnoreCase("expression")) {
        }        
        
        return false;
    }
    private String correctionformat(String ex){
        ex="("+ex+")";
    	ex=Utility.replace(ex, "(", " ( ");
    	ex=Utility.replace(ex, ")", " ) ");
    	ex=Utility.replace(ex, "  ", " ");
    	ex=Utility.replace(ex, "  ", " ");
    	ex=Utility.replace(ex, "  ", " ");
    	
    	StringBuffer stringBuffer = new StringBuffer();
    	Vector<String> sx = Utility.splitVector(ex, " ");
    	for (int i = 0; i < sx.size(); i++) {
            if (sx.elementAt(i).equals("(")) {
                stringBuffer.append("[\"");
            }else if (sx.elementAt(i).equals(")")) {   
                stringBuffer.append("\"]");
            }else{
                stringBuffer.append("\"").append(sx.elementAt(i)).append("\"");
            }
	}
    	Nset n = Nset.readJSON(sx.toString());
        
        
    	return ex;
    }
    
    
    private boolean iif(boolean b1, String exp, boolean b2){
        if (exp.equals("OR")) {
            return b1 || b2;
        }else if (exp.equals("AND")) {
            return b1 &&  b2;
        }else{
            return b1;
        }
    }
    private boolean iif(String s1, String exp ,String s2 ){
       
        if (exp.equals("<>")) {
            return !(s1.equals(s2)); 
        }else if (exp.equals("=")) {
            return s1.equals(s2); 
        }else if (exp.equals(">=")) { 
            return Utility.getDouble(s1) >= Utility.getDouble(s2) ;
        }else if (exp.equals(">")) {   
            return Utility.getDouble(s1) > Utility.getDouble(s2) ;
        }else if (exp.equals("<=")) {
            return Utility.getDouble(s1) <= Utility.getDouble(s2) ;
        }else if (exp.equals("<")) {
            return Utility.getDouble(s1) < Utility.getDouble(s2) ;
        }else if (exp.equals("equalignorecase")) {    
            return s1.equalsIgnoreCase(s2); 
        }else if (exp.equals("containignorecase")) {
            return s1.toLowerCase().contains(s2.toLowerCase()); 
        }else if (exp.equals("contain")) {
            return s1.contains(s2); 
        }else if (exp.equals("startwithcignorecase")) {
            return s1.toLowerCase().startsWith(s2.toLowerCase()); 
        }else if (exp.equals("startwith")) {
            return s1.startsWith(s2); 
        }else if (exp.equals("endwithignorecase")) {   
            return s1.toLowerCase().endsWith(s2.toLowerCase()); 
        }else if (exp.equals("endwith")) {       
            return s1.endsWith(s2); 
        }else{
            return s1.equals(s2);//default equal
        }
    }
}
