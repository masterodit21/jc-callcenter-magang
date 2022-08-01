/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.TabLayout;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * @author rkrzmail
 */
public class webvariablearraylist extends NikitaServlet{
    
    
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Variable Array List");                       
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
     
         
        
        Style style = new Style();
        style.setStyle("width", "380");
        style.setStyle("height", "480");
        nf.setStyle(style);
        
 
         
        
        ComponentGroup verticalLayout = new ComponentGroup();
        verticalLayout.setId("webaction-variable");        
        nf.addComponent(verticalLayout);
        
        for (int i = 0; i < 11; i++) {
            Component component = new Textsmart();
            component.setLabel("Parameter " + (i+1));
            component.setId("pgen-"+i);//just bufferd
            verticalLayout.addComponent(component);  
        }
 
        
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        Button btn = new Button();
        btn.setTag(request.getParameter("search"));
        btn.setId("btnDone");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "156px");
        style.setStyle("height", "40px");
        style.setStyle("margin-left", "200px");
        btn.setStyle(style);          
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset r = Nset.newObject();
                
                Nset arr = Nset.newArray();
                for (int i = 0; i < 11; i++) {
                    if (response.getContent().findComponentbyId("pgen-"+i).getText().equals("")) {
                        break;
                    }
                    arr.addData(response.getContent().findComponentbyId("pgen-"+i).getText());
                }
                
                if (arr.getArraySize()>=1) {
                    r.setData("variable", arr.toJSON());
                }else{
                    r.setData("variable", "");
                }                
                response.setResult("webvariablearraylist", r);
                response.closeform(response.getContent());
                response.write();
            }
        });      
        request.retainData(btn);
        Nset arr = Nset.readJSON(btn.getTag());
        for (int i = 0; i < 11; i++) {
            nf.findComponentbyId("pgen-"+i).setText(arr.getData(i).toString());
        }
                
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                 
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {         
                 
               
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                 
                
                
            }
        });
        
           
        response.setContent(nf);
  
    }
    private void chageTexttoArea(ComponentGroup com, int i){
        Component org = com.getComponent(i);
        Component component = new Textarea();
        component.setId(org.getId());
        component.setText(org.getText());
        component.setLabel(org.getLabel());
        component.setTag(org.getTag());
        component.setHint(org.getHint());
        component.setName(org.getName());
        component.setForm( org.getForm() );
        component.setTooltip(org.getTooltip());
        
        component.setVisible(org.isVisible());
        component.setEnable(org.isEnable());
        
        
        com.setComponentAt(component, i);
    }
    
    private void chageTexttoCombo(ComponentGroup com, int i,String param){
        param=param.substring(5);
        Component org = com.getComponent(i);
        Component component = new Combobox();
        component.setId(org.getId());
        component.setText(org.getText());
        component.setLabel(org.getLabel());
        component.setTag(org.getTag());
        component.setHint(org.getHint());
        component.setName(org.getName());
        component.setForm( org.getForm() );
        component.setTooltip(org.getTooltip());
        component.setData(Nset.readJSON(param));
        
        component.setVisible(org.isVisible());
        component.setEnable(org.isEnable());
        
        
        com.setComponentAt(component, i);
    }
    
    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if(component.startsWith("webaction-gen-")){
            request.setParameter("search", response.getContent().findComponentbyId(component).getText());
            request.setParameter("paramid", component);
            request.setParameter("idcomp", response.getContent().findComponentbyId("webaction-lblidcomp").getTag()); 
            request.setParameter("idform", response.getContent().findComponentbyId("webaction-lblidform").getTag());  
            
            Nset n = Nset.readJSON(response.getContent().findComponentbyId(component).getTag() );
            if (n.getData("type").toString().startsWith("variable")) { 
                response.showform("webvariablelist", request);  
            }else if (n.getData("type").toString().startsWith("query")) {
                response.showform("webdatabase", request);  
            }else if (n.getData("type").toString().startsWith("route")) {
                response.showform("webroute", request);      
            }else if (n.getData("type").toString().startsWith("form")) {
                response.showform("webformargs", request);      
            }else if (n.getData("type").toString().startsWith("resource")) {
                request.setParameter("code", "selected");
                response.showform("webresource", request);    
            }else if (n.getData("type").toString().startsWith("{")||n.getData("type").toString().startsWith("[")||n.getData("type").toString().startsWith("|[")) {
                request.setParameter("mode", "combobox");   
                if (n.getData("type").toString().startsWith("{")) {
                    request.setParameter("data", Nset.readJSON(n.getData("type").toString()).getData("data").toJSON() );
                }else{
                    request.setParameter("data", n.getData("type").toString() );
                }               
                response.showform("webfinderlist", request);  
            }else {   
                
            }
             
            
            
        }else{
            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }
    }   
    
    
    public void fillAction(NikitaResponse response, String parameter, Nset buffered) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webaction-variable");
        Nset n =  Nset.readJSON(parameter);         

        for (int i = 0; i < n.getData("args").getArraySize(); i++) {    
            Nset v = n.getData("args").getData(i);              
            createGeneratorVariable(i, v, buffered.getData("args").getData(v.getData("id").toString()).toString(),verticalLayout);
        }        
        for (int i = verticalLayout.getComponentCount()-1; i >= n.getData("args").getArraySize(); i--) {    
           verticalLayout.removeComponent(i);
        } 
        
    }
    private void createGeneratorVariable(int i, Nset param, String bufferd,ComponentGroup verticalLayout){
        Component txt = verticalLayout.getComponent(i);
        txt.setId("webaction-gen-"+i);
        txt.setVisible(true);
        txt.setEnable(true);
        txt.setLabel(param.getData("text").toString());
        txt.setTag(param.toJSON());
        txt.setText(bufferd);
        if (param.getData("type").toString().startsWith("{")||param.getData("type").toString().startsWith("[")) {
            txt.setStyle(new Style().setStyle("n-lock", "true"));
            if (param.getData("type").toString().startsWith("{")) {
                 txt.setHint(Nset.readJSON(param.getData("type").toString()).getData("hint").toString());
            }else{
                 txt.setHint("Pilih salah satu");
            }            
        }else if (param.getData("type").toString().endsWith("s")){
            chageTexttoArea(verticalLayout,i);
        }else if (param.getData("type").toString().startsWith("combo")){
            chageTexttoCombo(verticalLayout,i,param.getData("type").toString());
        }
        if (param.getData("type").toString().trim().equals("")||param.getData("type").toString().trim().startsWith("string")) {
        }else{
            txt.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                  
                }
            });
        }        
 
    }

    public Nset getAction(NikitaResponse response, Nset param ) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webaction-variable");
        Nset result =  Nset.newObject();
        Nset n =  param;
 
        for (int i = 0; i < verticalLayout.getComponentCount(); i++) {             
            if (!verticalLayout.getComponent(i).getTag() .equals("")) {

               result.setData(Nset.readJSON(verticalLayout.getComponent(i).getTag() ).getData("id").toString(), verticalLayout.getComponent(i).getText() );   
            }
           
        } 
        
        return result;
    }
}
