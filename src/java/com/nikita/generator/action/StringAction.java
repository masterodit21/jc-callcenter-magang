/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author lenovo
 */
public class StringAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");        
        String code = response.getVirtualString(data.get("code").toString());
        String param = response.getVirtualString(data.get("param").toString());
        
        if (code.equalsIgnoreCase("()")) {
            int beginIndex = Utility.getInt(response.getVirtualString(data.get("beginIndex").toString()));
            param = param.substring(beginIndex);
        }else if (code.equalsIgnoreCase("(,)")) {
            int beginIndex = Utility.getInt(response.getVirtualString(data.get("beginIndex").toString()));
            int endIndex = Utility.getInt(response.getVirtualString(data.get("endIndex").toString()));
            param = param.substring(beginIndex);
        }else if (code.equalsIgnoreCase("lenght")||code.equalsIgnoreCase("length")) {//????????????????????????????????????????????/
            int x = Utility.getInt(response.getComponent(data.get("comp").toString()).getData().toString());
            String lenght = String.valueOf(x);
            response.getComponent(data.get("result").toString()).setText(lenght);
        }

        response.getComponent(data.get("result").toString()).setText(param);
        return true;
    }
    
    public static String getStringStream(String s, String stream){
        Nset n = Nset.readJSON(stream);
        if (n.getData(0).toString().equals("substring")) {
            try {
                if (n.getArraySize()==2) {
                    return s.substring(n.getData(1).toInteger());
                }else if (n.getArraySize()==3) {
                    return s.substring(n.getData(1).toInteger(),n.getData(2).toInteger());
                }
            } catch (Exception e) { }
            return "";   
        }else if (n.getData(0).toString().equals("trim")) {  
            return s.trim();
        }else if (n.getData(0).toString().equals("equal")) {  
            return s.equals(n.getData(1).toString())?"true":"false";
        }else if (n.getData(0).toString().equals("length")) {       
            return s.length()+"";
        }else if (n.getData(0).toString().equals("equalignorecase")) { 
            return s.equalsIgnoreCase(n.getData(1).toString())?"true":"false";
        }else if (n.getData(0).toString().equals("contain")) {   
            return s.contains(n.getData(1).toString())?"true":"false";
        }else if (n.getData(0).toString().equals("replace")) {
            return Utility.replace(s, n.getData(1).toString(), n.getData(2).toString());
        }else if (n.getData(0).toString().equals("splittojson")) { 
            return new Nset(Utility.splitVector(s, n.getData(1).toString())).toJSON();
        }else if (n.getData(0).toString().equals("ucase")||n.getData(0).toString().equals("uppercase")) { 
            return s.toUpperCase();
        }else if (n.getData(0).toString().equals("lcase")||n.getData(0).toString().equals("lowercase")) {  
            return s.toLowerCase();
        }else if (n.getData(0).toString().equals("indexof")) {   
            return s.indexOf(n.getData(1).toString())+"";
        }else if (n.getData(0).toString().equals("isnumber")) {  
            return Utility.isNumeric(s)?"true":"false";
        }else if (n.getData(0).toString().equals("concat")) { 
            for (int i = 1; i < n.getArraySize(); i++) {
                 s=s.concat(n.getData(i).toString());                
            }
            return s;
        }else if (n.getData(0).toString().equals("capital")) { 
            return StringUtils.capitalize(s);
        }else if (n.getData(0).toString().equals("right")) { 
            return StringUtils.right(s, n.getData(1).toInteger());
        }else if (n.getData(0).toString().equals("rightspace")) { 
        	StringBuffer stringBuffer = new StringBuffer();
        	for (int i = 0; i < n.getData(1).toInteger(); i++) {
        		stringBuffer.append(" ");
			}
        	stringBuffer.append(s);
            return StringUtils.right(stringBuffer.toString(), n.getData(1).toInteger());
        }else if (n.getData(0).toString().equals("left")) { 
            return StringUtils.left(s, n.getData(1).toInteger());
        }else if (n.getData(0).toString().equals("mid")) {   
            return StringUtils.mid(s, n.getData(1).toInteger(), n.getData(2).toInteger());
        }else if (n.getData(0).toString().equals("reverse")) {   
            return StringUtils.reverse(s);
        }else if (n.getData(0).toString().equals("repeat")) {   
            return StringUtils.repeat(s, n.getData(1).toInteger());
            
        }else if (n.getData(0).toString().equals("escapesql")) {   
        	return StringEscapeUtils.escapeSql(s) ;
        }else if (n.getData(0).toString().equals("escapehtml")) {   
        	return StringEscapeUtils.escapeHtml(s) ;
        }else if (n.getData(0).toString().equals("escapexml")) {   
        	return StringEscapeUtils.escapeXml(s) ;
        }else if (n.getData(0).toString().equals("escapecsv")) {   
        	return StringEscapeUtils.escapeCsv(s) ;		
        }else if (n.getData(0).toString().equals("escapejava")) {   
        	return StringEscapeUtils.escapeJava(s) ;	
        }else if (n.getData(0).toString().equals("escapejs")) {   
        	return StringEscapeUtils.escapeJavaScript(s) ;	
        }else if (n.getData(0).toString().equals("unescapehtml")) {   
        	return StringEscapeUtils.unescapeHtml(s) ;
        }else if (n.getData(0).toString().equals("unescapexml")) {   
        	return StringEscapeUtils.unescapeXml(s) ;
        }else if (n.getData(0).toString().equals("unescapecsv")) {   
        	return StringEscapeUtils.unescapeCsv(s) ;		
        }else if (n.getData(0).toString().equals("unescapejava")) {   
        	return StringEscapeUtils.unescapeJava(s) ;	
        }else if (n.getData(0).toString().equals("unescapejs")) {   
        	return StringEscapeUtils.unescapeJavaScript(s) ;		
        }
        return s;
    }
}
