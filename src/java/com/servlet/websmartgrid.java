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
public class websmartgrid extends NikitaServlet{
 
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Smart Grid");
        
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
        textsmart.setLabel("Header View");
        textsmart.setId("text_header");
        textsmart.setText(data.getData("header").toString());
        nf.addComponent(textsmart);  
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                String s = response.getContent().findComponentbyId("text_header").getText();
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
                
                response.showform("websmartgridtable", request,"tbl", true);
                
                response.write();
            }
        });  
         
        textsmart = new Textsmart();
        textsmart.setLabel("Data");
        textsmart.setTag("paramid");
        textsmart.setVisible(false);
        textsmart.setId("text_data");
        nf.addComponent(textsmart); 
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
            request.setParameter("search", response.getContent().findComponentbyId("text_data").getText());
            request.setParameter("paramid", response.getContent().findComponentbyId("text_data").getTag());
            request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
            request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
            response.showform("webvariablelist", request,"list", true);
                
            response.write();
            }
        });
        
        
         
                    
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
                
                response.showform("websmartgridview", request,"view", true);
                
                response.write();
                
                
                
            }
        });
        nf.addComponent(textsmart);   
        
                          
                                  
        textsmart = new Textsmart();
        textsmart.setLabel("Row Looper");
        textsmart.setId("text_looper");
        textsmart.setText( data.getData("looper").toString());
        nf.addComponent(textsmart); 
        textsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
            request.setParameter("search", response.getContent().findComponentbyId("text_looper").getText());
            request.setParameter("paramid", response.getContent().findComponentbyId("text_data").getTag());
            request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
            request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
            response.showform("webvariablelist", request,"list2", true);
                
            response.write();
            }
        });

        
        
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
        

        //(v)showrownum
        //(v)showaction
        //(*)multiselect [autonumber/fieldPK]
        Checkbox 
        checkbox = new Checkbox();
        checkbox.setId("showrownum");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['showrownum','Show Row Number']]",true));
        checkbox.setText(data.getData("showrownum").toString() .equals("true")?"[\"showrownum\"]":"");
        nf.addComponent(checkbox); 
        
        checkbox = new Checkbox();
        checkbox.setId("showaction");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['showaction','Show Grid Action']]",true));
        checkbox.setText(data.getData("showaction").toString() .equals("true")?"[\"showaction\"]":"");
        nf.addComponent(checkbox);
        
        checkbox = new Checkbox();
        checkbox.setId("sortable");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['sortable','Sortable']]",true));
        checkbox.setText(data.getData("sortable").toString() .equals("true")?"[\"sortable\"]":"");
        nf.addComponent(checkbox);
        
        HorizontalLayout
        hl = new HorizontalLayout();
        checkbox = new Checkbox();
        checkbox.setId("multiselect");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['multiselect','Multi Select']]",true));
        checkbox.setText(data.getData("multiselect").toString() .equals("true")?"[\"multiselect\"]":"");
        hl.addComponent(checkbox);
            
                                          
                                          
        
        textsmart = new Textsmart();
        textsmart.setId("multiselectfield");
        textsmart.setText(data.getData("multiselectfield").toString() );
        hl.addComponent(textsmart);
        
        nf.addComponent(hl);
        
        
        //add selected row
        textsmart = new Textsmart();
        textsmart.setLabel("Selected Row");
        textsmart.setId("selectrow");
        textsmart.setText(data.getData("select").toString());
        textsmart.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {              
                request.setParameter("search", response.getContent().findComponentbyId("selectrow").getText());
                request.setParameter("paramid", response.getContent().findComponentbyId("text_data").getTag());
                request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
                request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
                response.showform("webvariablelist", request,"listselect", true);

                response.write();
            }
        });
        nf.addComponent(textsmart);   
        
        
   
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
                         smartdata.setData("header", response.getContent().findComponentbyId("text_header").getText());//text_header
                    smartdata.setData("action", response.getContent().findComponentbyId("actionview").getText());//actionview
                              smartdata.setData("data", response.getContent().findComponentbyId("dataview").getText());//dataview
                    smartdata.setData("looper", response.getContent().findComponentbyId("text_looper").getText());//text_looper
                    smartdata.setData("showrownum", response.getContent().findComponentbyId("showrownum").getText().contains("showrownum")?"true":"");// showrownum,
                    smartdata.setData("showaction", response.getContent().findComponentbyId("showaction").getText().contains("showaction")?"true":"");// showaction,
                    smartdata.setData("sortable", response.getContent().findComponentbyId("sortable").getText().contains("sortable")?"true":"");// sortable,
                    smartdata.setData("detail", response.getContent().findComponentbyId("detail").getText());//dataview
                    
                    smartdata.setData("select", response.getContent().findComponentbyId("selectrow").getText());//dataview
                    
                    smartdata.setData("multiselect", response.getContent().findComponentbyId("multiselect").getText().contains("multiselect")?"true":"");// multiselect,
                    smartdata.setData("multiselectfield", response.getContent().findComponentbyId("multiselectfield").getText());// multiselectfield

                result.setData("variable", smartdata.toJSON()); 
                result.setData("paramid", response.getContent().findComponentbyId("websmartgrid-txtSearch").getTag());
            
                response.closeform(response.getContent());
                response.setResult("websmartgrid", result); 
            }
        });      
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
        
                if(reqestcode.equals("list")){
                    response.getContent().findComponentbyId("text_data").setText(result.getData("variable").toString());
                    response.writeContent();
                }
                if(reqestcode.equals("list2")){
                    response.getContent().findComponentbyId("text_looper").setText(result.getData("variable").toString());
                    response.writeContent();
                }
                if(reqestcode.equals("listselect")){
                    response.getContent().findComponentbyId("selectrow").setText(result.getData("variable").toString());
                    response.writeContent();
                }               
                
                if(responsecode.equals("smarttable")){
                    response.getContent().findComponentbyId("text_header").setText(result.getData("autodata").toString());
                    response.writeContent();
                }
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
