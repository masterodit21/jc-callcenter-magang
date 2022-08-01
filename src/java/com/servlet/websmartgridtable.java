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
import com.nikita.generator.action.ShowDialogAction;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Vector;
 

/**
 *
 * @author rkrzmail
 */
public class websmartgridtable extends NikitaServlet{
    Textarea txtX   ;
    VerticalLayout verticalLayout ;
            
    private ComponentGroup autocreate(Nset n, int i){
        HorizontalLayout h = new HorizontalLayout();
        Textsmart component = new Textsmart();
        component.setId("header-text-"+i); 
        component.setLabel("Header ["+(i)+"]");
        component.setText(n.getData(i).getData("text").toString()); 
        h.addComponent(component); 

        component = new Textsmart();
        component.setId("header-style-"+i); 
        component.setHint("style");
        component.setText(n.getData(i).getData("style").toString()); 
        h.addComponent(component);
        return h;
            
    }
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Smart Grid Table");
             
        Style style = new Style();
        style.setStyle("width", "625");
        style.setStyle("height", "480");
        style.setStyle("n-maximizable", "false");
        style.setStyle("n-resizable", "false");
        nf.setStyle(style);
        
        Label lbljumlah = new Label();
        lbljumlah.setId("lbl_jumlah");
        lbljumlah.setText("1");
        lbljumlah.setVisible(false);
        nf.addComponent(lbljumlah);
        
        txtX = new Textarea();
        txtX.setId("autocreate");
        txtX.setVisible(false);
        txtX.setText(request.getParameter("autodata"));
        request.retainData(txtX);
        
        
        nf.addComponent(txtX);
        verticalLayout = new VerticalLayout();
        verticalLayout.setId("con-autocreate");
        verticalLayout.setVisible(true);
        
        nf.addComponent(verticalLayout);
        
        
        Nset n = Nset.readJSON(txtX.getText());
        for (int i = 0; i < n.getArraySize(); i++) {
            verticalLayout.addComponent( autocreate(n, i) ); 
        } 
        
        
        
        HorizontalLayout
        hl = new HorizontalLayout();        
        Button btn = new Button();        
        btn.setId("btn_add");
        btn.setText("[+]");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                Nset baru = Nset.newObject();
                n.addData(baru);
                txtX.setText(n.toJSON());
                verticalLayout.setVisible(true);
                verticalLayout.addComponent(autocreate(baru, n.getArraySize()-1));
                response.refreshComponent(txtX);
                response.refreshComponent(verticalLayout);
                response.write();
            }
        }); 
        hl.addComponent(btn);  
        
        btn = new Button();        
        btn.setId("btn_remove");
        btn.setText("[-]");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                
                if (n.getArraySize()>=1) {                        
                    ((Vector)n.getInternalObject()).removeElementAt(n.getArraySize()-1);
                    verticalLayout.removeComponent(verticalLayout.getComponentCount()-1);
                    
                    txtX.setText(n.toJSON());
                    verticalLayout.setVisible(true);
                    response.refreshComponent(txtX);
                    response.refreshComponent(verticalLayout);
                    response.write();
                }
                
            }
        }); 
        hl.addComponent(btn);  
        
        
        btn = new Button();        
        btn.setId("test");
        btn.setText("Save");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                Nset data = Nset.newArray(); boolean splitmode = true;
                for (int i = 0; i < n.getArraySize(); i++) {
                     Nset dataperhoz = Nset.newObject();
                     dataperhoz.setData("text", response.getComponent("$#header-text-"+i).getText());
                     dataperhoz.setData("style", response.getComponent("$#header-style-"+i).getText());
                     
                     
                     if (!response.getComponent("$#header-style-"+i).getText().equals("")) {
                        splitmode=false;
                     }
                     data.addData(dataperhoz);
                     
                }
                Nset result = Nset.newObject();
                if (splitmode) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < n.getArraySize(); i++) {
                          
                        sb.append(i>=1?",":"").append(response.getComponent("$#header-text-"+i).getText());

                    }
                    result.setData("autodata",sb.toString());
                }else{
                    result.setData("autodata",data.toJSON());
                }
                response.closeform(response.getContent());                
                response.setResult("smarttable", result);                
                response.write();
                                
            }
        }); 
        hl.addComponent(btn);  
        nf.addComponent(hl);    
        
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                             
            }
        }); 
        
        
        
        response.setContent(nf);
    }
    
}
