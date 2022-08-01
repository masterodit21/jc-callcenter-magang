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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webcomplistadd extends NikitaServlet{
    private static int maxparams = 10;
    NikitaConnection nikitaConnection;    
    int dbCore;  
    String user ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Component List Add");
        String mode = request.getParameter("mode");
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        ComponentGroup gc = new ComponentGroup();
        
        Style style = new Style();
        style.setStyle("width", "380");
        style.setStyle("height", "380");
         nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("complistadd-txtcompcode");
        txt.setLabel("Code");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("complistadd-txtcomptitle");
        txt.setLabel("Title");
        gc.addComponent(txt);
        
        Textarea txtarea = new Textarea();
        txtarea.setId("complistadd-txtcompdescribe");
        txtarea.setLabel("Describe");
        gc.addComponent(txtarea);
        
        
        txt = new Textbox();
        txt.setId("complistadd-txtmobileversion");
        txt.setLabel("Mobile Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("complistadd-txtlinkversion");
        txt.setLabel("Link Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("complistadd-txtwebversion");
        txt.setLabel("Web Version");
        gc.addComponent(txt);
        
        txtarea = new Textarea();
        txtarea.setId("complistadd-txtcompparameter");
        txtarea.setLabel("Component Parameter");
        gc.addComponent(txtarea);
        txtarea.setVisible(false);
        
        
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(gc);
        gc = new ComponentGroup();
        
        
        hr.addComponent(gc);
        nf.addComponent(hr);
      
        
        Button btn = new Button();
        btn.setId("complistadd-btnsave");
        btn.setText("save");
        nf.addComponent(btn);
        
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "280px");
        style.setStyle("margin-top", "10px");
        btn.setStyle(style);      
        
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("complistadd-btnsave").getTag());
                String mode = nset.getData("mode").toString(); 

                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    Nikitaset nikitaset = nikitaConnection.Query("insert into all_component_list("+
                                      "compcode,comptitle,compdescribe,mobileversion,linkversion,webversion,createdby,createddate)"+
                                      "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                       
                                      response.getContent().findComponentbyId("complistadd-txtcompcode").getText(),
                                      response.getContent().findComponentbyId("complistadd-txtcomptitle").getText(),
                                      response.getContent().findComponentbyId("complistadd-txtcompdescribe").getText(),
                                      response.getContent().findComponentbyId("complistadd-txtmobileversion").getText(),
                                      response.getContent().findComponentbyId("complistadd-txtlinkversion").getText(),
                                      response.getContent().findComponentbyId("complistadd-txtwebversion").getText(),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    }

                }                            
                response.write();
        
            }
        });
        
        response.setContent(nf);
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("complistadd-btnsave").getTag());
                    String id = nset.getData("id").toString(); 
                    
                    
                    Nikitaset nikitaset = nikitaConnection.Query("update all_component_list set compcode=?,comptitle=?,compdescribe=?,"+ 
                                                                 "mobileversion=?,linkversion=?,webversion=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+""+
                                                                 "where compcode=?",                                                                 
                                          response.getContent().findComponentbyId("complistadd-txtcompcode").getText(),
                                          response.getContent().findComponentbyId("complistadd-txtcomptitle").getText(),
                                          response.getContent().findComponentbyId("complistadd-txtcompdescribe").getText(),
                                          response.getContent().findComponentbyId("complistadd-txtmobileversion").getText(),
                                          response.getContent().findComponentbyId("complistadd-txtlinkversion").getText(),
                                          response.getContent().findComponentbyId("complistadd-txtwebversion").getText(),                               
                                          user,id);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{    
                        response.closeform(response.getContent());
                        response.setResult("OK", Nset.newObject() );
                    }
                    response.write();
                }
            }
        });
        
        if(mode.equals("edit")){
            nf.setText("Component List Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            
            nf.findComponentbyId("complistadd-txtcompcode").setText(nset.getData(1).toString());
            nf.findComponentbyId("complistadd-txtcomptitle").setText(nset.getData(2).toString());
            nf.findComponentbyId("complistadd-txtcompdescribe").setText(nset.getData(3).toString());
            nf.findComponentbyId("complistadd-txtmobileversion").setText(nset.getData(5).toString());
            nf.findComponentbyId("complistadd-txtlinkversion").setText(nset.getData(6).toString());
            nf.findComponentbyId("complistadd-txtwebversion").setText(nset.getData(7).toString());
            nf.findComponentbyId("complistadd-btnsave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
            
        }
        
        else{
            nf.setText("Component List Add");
            nf.findComponentbyId("complistadd-btnsave").setTag(Nset.newObject().setData("mode", "add").toJSON());
        }
        
        
        
        
        
    }
    
    
}
