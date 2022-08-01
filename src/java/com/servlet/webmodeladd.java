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
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author user
 */
public class webmodeladd extends NikitaServlet{
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "250");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("modelAdd-txtModelId");
        txt.setLabel("Model Id");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("modelAdd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("modelAdd-txtOrderTable");
        txt.setLabel("Order Name");
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("modelAdd-btnSave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("modelAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_model("+
                                          "modelid,modelname,ordername)"+
                                          "values(?,?,?)",
                                          response.getContent().findComponentbyId("modelAdd-txtModelId").getText(),
                                          response.getContent().findComponentbyId("modelAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("modelAdd-txtOrderTable").getText());
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
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("modelAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString(); 
                    Nikitaset nikitaset = nikitaConnection.Query("update web_model set modelid=?,modelname=?,ordername=?"+
                                          "where modelid=?",
                                          id,
                                          response.getContent().findComponentbyId("modelAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("modelAdd-txtOrderTable").getText(),
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
            nf.setText("Model Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("modelAdd-txtModelId").setText(nset.getData(1).toString());
            nf.findComponentbyId("modelAdd-txtName").setText(nset.getData(2).toString());
            nf.findComponentbyId("modelAdd-txtOrderTable").setText(nset.getData(3).toString());
            nf.findComponentbyId("modelAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Model Insert");
            nf.findComponentbyId("modelAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Model Add");            
            nf.findComponentbyId("modelAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
               
    }
    
    
}
