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
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author lenovo
 */
public class webmapadd extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "230");
        nf.setStyle(style);
        
        Textsmart txt = new Textsmart();
        txt.setId("mapAdd-txtformid");
        txt.setLabel("Form Name");
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        txt.setText(request.getParameter("formName"));
        txt.setTag(request.getParameter("formId"));        
        nf.addComponent(txt);     
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");                
                request.setParameter("search", response.getContent().findComponentbyId("mapAdd-txtformid").getText() );
                response.showform("webform", request,"form",true);      
                response.write();
            }
        });
        
        txt = new Textsmart();
        txt.setId("mapAdd-txtmodelid");
        txt.setLabel("Model Name");
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        txt.setText(request.getParameter("modelName"));
        txt.setTag(request.getParameter("modelId"));
        nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("mapAdd-txtmodelid").getText() );
                response.showform("webmodel", request,"model",true);      
                response.write();
            }
        });
        
        
        Textbox txt1 = new Textbox();
        txt1.setId("mapAdd-txtmfindex");
        txt1.setLabel("Index");
        txt1.setVisible(false);
        txt1.setText("1");
        nf.addComponent(txt1);
        
        Button btn = new Button();
        btn.setId("mapAdd-btnSave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "285px");
        style.setStyle("margin-top", "15px");
        btn.setStyle(style); 
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("mapAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
        
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{   
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_model_form("+
                                          "modelid,formid,mfindex)"+
                                          "values(?,?,?)",
                                          response.getContent().findComponentbyId("mapAdd-txtmodelid").getTag(),
                                          response.getContent().findComponentbyId("mapAdd-txtformid").getTag(),
                                          response.getContent().findComponentbyId("mapAdd-txtmfindex").getText());
                    
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
        
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("form") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("mapAdd-txtformid").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("mapAdd-txtformid").setTag(result.getData("id").toString());
                    response.writeContent();
                }
                
                if(reqestcode.equals("model") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("mapAdd-txtmodelid").setText(result.getData("namemodel").toString());
                    response.getContent().findComponentbyId("mapAdd-txtmodelid").setTag(result.getData("idmodel").toString());
                    response.writeContent();
                }
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("mapAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString(); 
                    Nikitaset nikitaset = nikitaConnection.Query("update web_model_form set modelid=?,formid=?,mfindex=?"+
                                          "where mfid=?",
                                          response.getContent().findComponentbyId("mapAdd-txtmodelid").getTag(),
                                          response.getContent().findComponentbyId("mapAdd-txtformid").getTag(),
                                          response.getContent().findComponentbyId("mapAdd-txtmfindex").getText(),
                                          id);
                    
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{                    
                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    }
                    response.write();
                }
            }
        });
        
        if(mode.equals("edit")){
            nf.setText("Mapping Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            
            NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
            Nikitaset nikitaset = nikitaConnection.Query("SELECT formname FROM web_form WHERE formid=?", nset.getData(3).toString());    
            Nikitaset nikitaset2 = nikitaConnection.Query("SELECT modelname FROM web_model WHERE modelid=?", nset.getData(2).toString());            
            
            nf.findComponentbyId("mapAdd-txtmodelid").setText(nikitaset2.getText(0, 0));
            nf.findComponentbyId("mapAdd-txtmodelid").setTag(nset.getData(2).toString());
            nf.findComponentbyId("mapAdd-txtformid").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("mapAdd-txtformid").setTag(nset.getData(3).toString());
            nf.findComponentbyId("mapAdd-txtmfindex").setText(nset.getData(4).toString());
            nf.findComponentbyId("mapAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
        }
        else if(mode.equals("insert")){
            nf.setText("Mapping Insert");
            nf.findComponentbyId("mapAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Mapping Add");
            nf.findComponentbyId("mapAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        
        response.setContent(nf);
    }
    
    
}
