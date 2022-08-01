/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.lang.reflect.Method;

/**
 *
 * @author rkrzmail
 */
public class NikitaScript {
    Binding binding ;
    GroovyShell shell  ;
    public NikitaScript(){
        binding = new Binding(){
            public void setVariable(String name, Object value) {
                System.out.println(":setVariable: "+name);
                super.setVariable(name, value); 
            }                
            public Object getVariable(String name) {
                System.out.println(":getVariable: "+name);
                return super.getVariable(name); 
            }
        };
        shell = new GroovyShell(binding);
    }
    public void setVariable(String name, Object value){
        binding.setVariable(name, value);
    }
    public Object getVariable(String name){
        return binding.getVariable(name);
    }
    public Object getVariables(){
        return binding.getVariables();
    }
    public Object evaluate(String textscript){       
        return shell.evaluate(textscript);
    }   
    
    
    
    
    
    public static void evalSingleNikita(String scrpt){
        Nset n = Nset.readJSON(scrpt);        
        //result=, class= object{new|static}= args= param=  
        //{result:$data, class:'java.lang.Math', args:['java.lang.Double'], methode:'abs', param:['a','2']}
        try {
            Method method = Class.forName("java.lang.Math").getMethod("abs", Double.TYPE  );
            Object object = method.invoke(null, -123);
            //Class.getPrimitiveClass("float");

            
            System.err.println(object.getClass().getTypeName());
        } catch (Exception e) {  }
        
         
    }
    public Class<?> getClassTypae( Class<?>  c){
        return null;
    }
    public Class getClassType(String classtype){
       
        
        if (classtype.equalsIgnoreCase("double")) {
            return Double.TYPE; 
        }else if (classtype.equalsIgnoreCase("integer")||classtype.equalsIgnoreCase("int")) {
            return Integer.TYPE;
        }else if (classtype.equalsIgnoreCase("long")) {
            return Long.TYPE;
        }else if (classtype.equalsIgnoreCase("float")) {
            return Float.TYPE;
         }else if (classtype.equalsIgnoreCase("short")) {
            return Short.TYPE;
        }else if (classtype.equalsIgnoreCase("byte")) {
            return Byte.TYPE;
        }else if (classtype.equalsIgnoreCase("boolean")) {
            return Boolean.TYPE;
        }else if (classtype.equalsIgnoreCase("char")) {
            return Character.TYPE;
        }else if (classtype.equalsIgnoreCase("string")) {
            return String.class;
        } 
        
        
        try {
            return Class.forName(classtype) ;
        } catch (Exception e) { }     
        return null;
    }
}
