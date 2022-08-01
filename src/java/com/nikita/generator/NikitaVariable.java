/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
 
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
 

/**
 *
 * @author rkrzmail
 */
public class NikitaVariable {
    private Object data ;
   
    
    public NikitaVariable(Object var){
        this.data=var;
        if (isNull()) {
            
        }else if (isString()) {
                        
        }else if (isNikitaset()) {    
            
        }else if (isNset()) {  
            
        }else if (isNumber()) {  
            
        }     
    }    
    
    public static Nset newArray(){
        return Nset.newArray();
    }
    public static Nset newObject(){
        return Nset.newObject();
    }     
    public String geVariableType(){
        if (isNull()) {
            return "null";
        }else if (isString()) {
            return "string";    
        }else if (isNikitaset()) {    
            return "nikitaset";
        }else if (isNset()) {  
            return "nset";
        }else if (isDouble()) {  
            return "double";
        }else if (isInteger()) {  
            return "integer";
        }else if (isLong()) {     
            return "long";
        }     
        return data.getClass().getSimpleName().toLowerCase();
    }
    
    public boolean isNikitaset(){
        return (data instanceof Nikitaset);
    }
    public boolean isNset(){
        return (data instanceof Nset);
    }
    public boolean isNsetArray(){
        return isNset() && getNset().isNsetArray();
    }
    public boolean isNsetObject(){
        return isNset() && getNset().isNsetObject();
    }
    public boolean isBoolean(){
        return  (data instanceof Boolean);
    }
    public boolean isDateTime(){
        return false;
    }
    public boolean isDouble(){
        return isdouble;
    }
    public boolean isNumber(){
        return isLong()||isInteger()||isDouble();
    }
    public boolean isLong(){
        return islong||isinteger;
    }
    public boolean isInteger(){
        return isinteger;
    }
    public boolean isString(){
        return (data instanceof String);
    }
    public boolean isNull(){
        return data==null;
    }
    
    private boolean islong = false;
    private boolean isinteger = false;
    private boolean isdouble = false;
    
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  
    }
    private void parseNumber(String s){
        if (s.contains(".")) {            
            try {
                //data = Double.parseDouble(s);
                DecimalFormat df = new DecimalFormat();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator(',');
                df.setDecimalFormatSymbols(symbols);
                data = df.parse(s).doubleValue();             
                
                isdouble=true;
            } catch (Exception e) {  }
        }else{
            if (s.contains(",")) {
                s = s.replace(",","");
            }
            try {
                long l = Long.parseLong(s);                
                if (l>=Integer.MIN_VALUE  && l<=Integer.MAX_VALUE) {
                    isinteger=true;
                    data= (int)l;
                }else{
                    islong=true;
                    data=l;
                }
            } catch (Exception e) {   }
        }
    }
    
    public Long getLong(){
        if (isLong()) {
            return (long)data;
        }
        return 0L;
    }
    public int getInteger(){
        if (isInteger()) {
            return (int)data;
        }
        return 0;
    }
    public double getDouble(){
        if (isDouble()) {
            return (double)data;
        }
        return 0;
    }
    public Number getNumber(){
        if (isDouble()) {
            return  (double)data;
        }else if (isNumber()) {
            return (long)data;
        }
        return 0;
    }
     public Nset getNset(){
        if (isNset()) {
            return (Nset)data;
        } 
        return Nset.newNull();
    }
    public Nikitaset getNikitaset(){
        if (isNikitaset()) {
            return (Nikitaset)data;
        } 
        return new Nikitaset("parse nikitaset");
    }
    public String getString(){
        if (isString()) {
            return getString();
        }else if (isNull()) {
            return "null";
        }
        return "";
    }
    public String getNumberOnly(){       
        StringBuffer buf = new StringBuffer();
        String s = getString();
        for (int i = 0; i < s.length(); i++) {
            if ("01234567890".indexOf(s.charAt(i)) != -1) {
                buf.append(s.charAt(i));
            }
        }
        return buf.toString();
    }
    public String toCurr(){
        try {
             return new DecimalFormat ( "#,##0", DecimalFormatSymbols.getInstance( Locale.US) ). format( isDouble() ? getDouble() : getLong() )   ;                
        } catch (Exception e) { }
        return "";
    }
    public String toCurrDecimal(){
        try {
             return new DecimalFormat ( "#,##0.0############", DecimalFormatSymbols.getInstance( Locale.US) ). format( isDouble() ? getDouble() : getLong() )   ;                
        } catch (Exception e) { }
        return "";
    }
    
    
    
    
}
