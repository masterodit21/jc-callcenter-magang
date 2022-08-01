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
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
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
public class websmartgridaction extends NikitaServlet{
    Textarea txtX   ;
    VerticalLayout verticalLayout ;
    NikitaConnection nikitaConnection ;
            
    private ComponentGroup autocreate(Nset n,final int i, NikitaRequest request){
        
        VerticalLayout v = new VerticalLayout();
        HorizontalLayout h = new HorizontalLayout();
        
        
        Combobox com = new Combobox();
        com.setId("act-text-"+i); 
        com.setLabel("Action ["+(i)+"]");
        com.setText(n.getData(i).getData("text").toString());  
        com.setData(Nset.readJSON("['add','edit','delete','remove','view','map','up','down','copy','paste','cut','run','play','new','find','image','clear','search','camera','download','upload','move']", true));
//        com.setData(  new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text")  );   
        
        h.addComponent(com);
        request.retainData(com); 
        com.setVisible(true);
        com.setEnable(true);
        
        com = new Combobox();
        com.setId("act-style-"+i); 
        
        com.setText(n.getData(i).getData("visible").toString()); 
        com.setData(Nset.readJSON("[{'id':'visible','text':'Visible'},{'id':'invisible','text':'Invisible'},{'id':'disable','text':'Disable'},{'id':'gone','text':'Gone'},{'id':'custom','text':'Custom'}]", true));
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {    
                if(response.getContent().findComponentbyId("act-style-"+i).getText().equals("custom")){
                    response.getContent().findComponentbyId("act-custstyle-"+i).setVisible(true);                    
                    response.refreshComponent(response.getContent().findComponentbyId("act-custstyle-"+i));
                }else{
                    response.getContent().findComponentbyId("act-custstyle-"+i).setVisible(false);                    
                    response.refreshComponent(response.getContent().findComponentbyId("act-custstyle-"+i));                    
                }
//                response.write();
            }
        }); 
        h.addComponent(com);
        request.retainData(com);
        com.setVisible(true);
        com.setEnable(true);
        
        v.addComponent(h);
        
        Textarea area = new Textarea();
       
        area.setId("act-custstyle-"+i); 
        area.setStyle(new Style().setStyle("width", "497px"));
        area.setText(n.getData(i).getData("custom").toString()); 
        area.setHint("style"); 
        area.setVisible(com.getText().equals("custom"));
        v.addComponent(area);
        request.retainData(area);
         
        //area.setVisible(true);
        //area.setEnable(true);
        
        h.setVisible(true);
        v.setVisible(true);
        h.setEnable(true);
        v.setEnable(true);
        return v;
            
    }
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Smart Grid Action");
              
        Style style = new Style();
        style.setStyle("width", "625");
        style.setStyle("height", "480");
        style.setStyle("n-maximizable", "false");
        style.setStyle("n-resizable", "false");
        nf.setStyle(style);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);      
        
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
            verticalLayout.addComponent( autocreate(n, i,request) ); 
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
                verticalLayout.addComponent(autocreate(baru, n.getArraySize()-1 , request));
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
                     dataperhoz.setData("text", response.getComponent("$#act-text-"+i).getText());
                     dataperhoz.setData("visible", response.getComponent("$#act-style-"+i).getText());
                     dataperhoz.setData("custom", response.getComponent("$#act-custstyle-"+i).getText());
                     
                     
                     if (!response.getComponent("$#act-style-"+i).getText().equals("")) {
                        splitmode=false;
                     }
                     data.addData(dataperhoz);
                     
                }
                Nset result = Nset.newObject();
                if (splitmode) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < n.getArraySize(); i++) {
                          
                        sb.append(i>=1?",":"").append(response.getComponent("$#act-text-"+i).getText());

                    }
                    result.setData("autodata",sb.toString());
                }else{
                    result.setData("autodata",data.toJSON());
                }
                
                response.closeform(response.getContent());                
                response.setResult("smartaction", result);                
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
