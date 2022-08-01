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
public class connectionadd extends NikitaServlet{
    
    /*parameter
    mode
    data
    id
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
                
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "300");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("conAdd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("conAdd-txtUser");
        txt.setLabel("User Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("conAdd-txtPassword");
        txt.setLabel("Password");
        txt.setStyle(new Style().setStyle("n-password", "true"));
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("conAdd-txtClass");
        txt.setLabel("Class");  
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("conAdd-txtUrl");
        txt.setLabel("Url");
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("conAdd-btnSave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "280px");
        style.setStyle("margin-top", "15px");
        btn.setStyle(style); 
        nf.addComponent(btn);        
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                NikitaConnection nikitaConnection = response.getConnection("logic");
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("conAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_connection("+
                                          "connname,connusername,connpassword,connclass,connurl)"+
                                          "values(?,?,?,?,?)",
                                          response.getContent().findComponentbyId("conAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtUser").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtPassword").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtClass").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtUrl").getText());
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
                    NikitaConnection nikitaConnection = response.getConnection("logic");
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("conAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString();                    

                    Nikitaset nikitaset = nikitaConnection.Query("update web_connection set connname=?,connusername=?,connpassword=?,connclass=?,connurl=?"+
                                          "where connid=?",
                                          response.getContent().findComponentbyId("conAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtUser").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtPassword").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtClass").getText(),
                                          response.getContent().findComponentbyId("conAdd-txtUrl").getText(),
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
            nf.setText("Connection Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("conAdd-txtName").setText(nset.getData(1).toString());
            nf.findComponentbyId("conAdd-txtUser").setText(nset.getData(2).toString());
            nf.findComponentbyId("conAdd-txtPassword").setText(nset.getData(3).toString());
            nf.findComponentbyId("conAdd-txtClass").setText(nset.getData(4).toString());
            nf.findComponentbyId("conAdd-txtUrl").setText(nset.getData(5).toString());
            nf.findComponentbyId("conAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Connection Insert");
            nf.findComponentbyId("conAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Connection Add");            
            nf.findComponentbyId("conAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
    }
    
    
}
