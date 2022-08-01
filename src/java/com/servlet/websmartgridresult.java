/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nset;
 

/**
 *
 * @author rkrzmail
 */
public class websmartgridresult extends NikitaServlet{
 
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Smart Grid Result");
        
        Style style = new Style();
        style.setStyle("width", "408");
        style.setStyle("height", "480");
        style.setStyle("n-maximizable", "false");
        style.setStyle("n-resizable", "false");
        nf.setStyle(style);
         
        Label 
        lbl = new Label();
        lbl.setText(request.getParameter("idcomp"));
        lbl.setTag(request.getParameter("idform"));
        lbl.setId("lbl_tampung");
        lbl.setVisible(false);
        nf.addComponent(lbl);  
                
        Textsmart txt = new Textsmart();
        txt.setVisible(false);
        txt.setId("websmartgrid-txtSearch");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("paramid"));
        nf.addComponent(txt);
        
        Nset data =  Nset.readJSON(request.getParameter("search"));
        //System.out.println(request.getParameter("search"));
        
           
        //data 
        Textsmart              
        textsmart = new Textsmart();
        textsmart.setLabel("Action View");
        textsmart.setId("actionview");
        textsmart.setText(data.getData("action").toString());
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {               
                
                
                String s = response.getContent().findComponentbyId("actionview").getText();
                if (s.startsWith("[")&& s.endsWith("]")) {
                    request.setParameter("autodata", s);
                }else{
                    Nset v = Nset.readsplitString(s,",");
                    Nset x = Nset.newArray();
                    for (int i = 0; i < v.getArraySize(); i++) {
                        x.addData(Nset.newObject().setData("text",  v.getData(i).toString()  )   );
                        
                    }
                    
                    request.setParameter("autodata", x.toJSON());
                }               
                
                response.showform("websmartgridaction", request,"actview", true);
                
                response.write();
                
                
                
            }
        });
        nf.addComponent(textsmart);   
         
                   
        textsmart = new Textsmart();
        textsmart.setLabel("Data View");
        textsmart.setId("dataview");
        textsmart.setText(data.getData("data").toString());
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {               
                
                
                String s = response.getContent().findComponentbyId("dataview").getText();
                if (s.startsWith("[")&& s.endsWith("]")) {
                    request.setParameter("autodata", s);
                }else{
                    Nset v = Nset.readsplitString(s,",");
                    Nset x = Nset.newArray();
                    for (int i = 0; i < v.getArraySize(); i++) {
                        x.addData(Nset.newObject().setData("text",  v.getData(i).toString()  )   );
                        
                    }
                    
                    request.setParameter("autodata", x.toJSON());
                }
                
                request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
                request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
                request.setParameter("from", "smartresult");
                response.showform("websmartgridview", request,"view", true);
                
                response.write();
                
                
                
            }
        });
        nf.addComponent(textsmart);   
        textsmart.setVisible(false);
        
        
        textsmart = new Textsmart();
        textsmart.setLabel("Detail View");
        textsmart.setId("detail");
        textsmart.setText(data.getData("detail").toString());
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {               
                
                request.setParameter("search", component.getText());
                request.setParameter("paramid", component.getId());
            
                request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
                request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
                request.setParameter("from", "smartresult");                
                response.showform("webformargs", request);  
                response.write();
                
                
                
            }
        });
        nf.addComponent(textsmart);   
        textsmart.setVisible(false);
        
        Checkbox 
        checkbox = new Checkbox();
        checkbox.setId("showdetailview");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['showdetailview','Show Detail View']]",true));
        checkbox.setText(data.getData("showdetailview").toString().equals("true")||data.getData("showdetailview").toString().equals("")?"[\"showdetailview\"]":"");
        nf.addComponent(checkbox); 
        
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        Button btn = new Button();
        btn.setId("btn_save");
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
                Nset result = Nset.newObject();
                Nset smartdata = Nset.newObject();
                smartdata.setData("action", response.getContent().findComponentbyId("actionview").getText());//actionview
                smartdata.setData("data", response.getContent().findComponentbyId("dataview").getText());//dataview
                smartdata.setData("detail", response.getContent().findComponentbyId("detail").getText());//dataview
                smartdata.setData("showdetailview", response.getContent().findComponentbyId("showdetailview").getText().contains("showdetailview")?"true":"false");// showaction,

                result.setData("variable", smartdata.toJSON()); 
                result.setData("paramid", response.getContent().findComponentbyId("websmartgrid-txtSearch").getTag());
            
                response.closeform(response.getContent());
                response.setResult("websmartgrid", result); 
            }
        });      
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(responsecode.equals("smartview")){
                    response.getContent().findComponentbyId("dataview").setText(result.getData("autodata").toString());
                    response.writeContent();
                }
                if(responsecode.equals("smartaction")){
                    response.getContent().findComponentbyId("actionview").setText(result.getData("autodata").toString());
                    response.writeContent();
                }
                
                if(responsecode.equals("webdatabase")){//webformarg
                    response.getContent().findComponentbyId(result.getData("paramid").toString()).setText(result.getData("variable").toString());
                    response.writeContent();
                }
                
            }
        });
        
           
        
        
        response.setContent(nf);
    }
    
   
    
}
