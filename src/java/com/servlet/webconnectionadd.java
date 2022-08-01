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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webconnectionadd extends NikitaServlet{
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ; 
    /*parameter
    mode
    data
    id
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
                
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "300");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("conAdd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("conAdd-txtNamebckup");
        txt.setVisible(false);
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("conAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                Nikitaset nikitaset = null;
                if(mode.equals("edit")){                    
                    if(!response.getContent().findComponentbyId("conAdd-txtName").getText().equals(response.getContent().findComponentbyId("conAdd-txtNamebckup").getText())){
                        nikitaset = nikitaConnection.Query("SELECT connname FROM web_connection WHERE connname = ?", response.getContent().findComponentbyId("conAdd-txtName").getText());
                        if(nikitaset.getText(0, 0).equals(response.getContent().findComponentbyId("conAdd-txtName").getText())){
                            response.showDialogResult("Warning", "connection name is exist", "warning",null, "", "OK");                
                        }
                        else{  
                            response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                        }
                    }else{
                        response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                    }
                }
                else{
//                    if (NikitaService.isModeCloud()) {
//                        response.showDialog("Add", "Don't Hack Please", "", "OK");
//                        response.write();
//                        return;
//                    }
                    nikitaset = nikitaConnection.Query("SELECT connname FROM web_connection WHERE connname = ?", response.getContent().findComponentbyId("conAdd-txtName").getText());
                   
                    if(nikitaset.getRows() > 0){
                        response.showDialogResult("Warning", "connection name is exist", "warning",null, "", "OK");                
                    }else{
                         String fname = response.getContent().findComponentbyId("conAdd-txtName").getText();
                        if (NikitaService.isModeCloud()) {
                                String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                                if (fname.startsWith(prefix)) {                            
                                }else{
                                    fname = prefix + fname;
                                }
                        }
                        
                        nikitaset = nikitaConnection.Query("insert into web_connection("+
                                              "connname,connusername,connpassword,connclass,connurl,createdby,createddate)"+
                                              "values(?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              fname,
                                              response.getContent().findComponentbyId("conAdd-txtUser").getText(),
                                              response.getContent().findComponentbyId("conAdd-txtPassword").getText(),
                                              response.getContent().findComponentbyId("conAdd-txtClass").getText(),
                                              response.getContent().findComponentbyId("conAdd-txtUrl").getText(),user);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("conAdd-txtName").getText());
                            data.setData("activitytype", "connection");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }
                }
                response.write();
                
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("conAdd-btnSave").getTag());
                    String id = nset.getData("id").toString();                    
                    String password=response.getContent().findComponentbyId("conAdd-txtPassword").getText();
                    
                    Nikitaset nikitaset;
                    nikitaset = nikitaConnection.Query("SELECT connpassword FROM web_connection WHERE connid = ?", id); 
                    
                    if (response.getContent().findComponentbyId("conAdd-txtPassword").getText().equals(Utility.MD5(nikitaset.getText(0, 0)))  ) {
                        password=nikitaset.getText(0, 0);
                    }else{
                        password = ( response.getContent().findComponentbyId("conAdd-txtPassword").getText() );
                    }      
                       String fname = response.getContent().findComponentbyId("conAdd-txtName").getText();
                        if (NikitaService.isModeCloud()) {
                                String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                                if (fname.startsWith(prefix)) {                            
                                }else{
                                    fname = prefix + fname;
                                }
                        }
                        nikitaset = nikitaConnection.Query("update web_connection set connname=?,connusername=?,connpassword=?,connclass=?,connurl=?,"+
                                              "modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                              "where connid=?",
                                              fname,
                                              response.getContent().findComponentbyId("conAdd-txtUser").getText(),
                                              password,
                                              response.getContent().findComponentbyId("conAdd-txtClass").getText(),
                                              response.getContent().findComponentbyId("conAdd-txtUrl").getText(),
                                              user,id);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{                    

                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("conAdd-txtName").getText());
                            data.setData("activitytype", "connection");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

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
            
            String fname = nset.getData(1).toString();
            if (NikitaService.isModeCloud()) {
                String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                if (fname.startsWith(prefix)) {
                    fname = fname.substring(prefix.length());//1.34
                }
            }
            
            nf.findComponentbyId("conAdd-txtName").setText(fname);
            nf.findComponentbyId("conAdd-txtNamebckup").setText(nset.getData(1).toString());
            nf.findComponentbyId("conAdd-txtUser").setText(nset.getData(2).toString());
            nf.findComponentbyId("conAdd-txtPassword").setText(Utility.MD5(nset.getData(3).toString()));
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
