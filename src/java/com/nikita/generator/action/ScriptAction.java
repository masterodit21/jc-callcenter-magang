/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 *
 * @author rkrzmail
 */
public class ScriptAction implements IAction{
 
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        String result = currdata.getData("args").getData("result").toString();  
        
        if (code.equals("gshell")) {
            Binding binding = new Binding(){
                public void setVariable(String name, Object value) {
                    if (name.startsWith("$")) {
                        if (value instanceof String) {
                            response.getComponent(name).setText((String)value);
                        }else if (value instanceof Number) {
                            response.getComponent(name).setText( (Number)value +"");
                        }else if (value instanceof Nset) {
                            response.getComponent(name).setText( ((Nset)value).toJSON());
                        }else if (value instanceof Nset) {
                            response.getComponent(name).setText( ((Nikitaset)value).toNset().toJSON());
                        }else if (value !=null) {
                            response.getComponent(name).setText(value.toString());
                        }else{
                            response.getComponent(name).setText("");
                        }
                    }else{
                        response.setVirtual("@"+name, value);
                    }
                }                
                public Object getVariable(String name) {
                    if (name.startsWith("$")) {
                        return  response.getComponent(name).getText();
                    }else{
                        return response.getVirtual("@"+name);
                    }
                }
                NikitaResponse response;
                public Binding get(NikitaResponse response){
                    this.response=response;
                    return this;
                }
            }.get(response);
            GroovyShell shell = new GroovyShell(binding);
            Object retObj = shell.evaluate(param1);            
            response.setVirtual(result, retObj);             
        }
        
        return true;
    }
    
}
