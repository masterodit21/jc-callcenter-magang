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
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.FileUploder;
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
public class webuseradd extends NikitaServlet {
    NikitaConnection nikitaConnection;
    int position = 0;
    int dbCore;
    String user ; 
    
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");                
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
         
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "320");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webuseradd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webuseradd-txtUser");
        txt.setLabel("User Name");
        if (NikitaService.isModeCloud()) {
            txt.setEnable(false);
        }        
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webuseradd-txtPassword");
        txt.setLabel("Password");
        txt.setStyle(new Style().setStyle("n-password", "true"));
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webuseradd-txtImei");
        txt.setLabel("Imei");  
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webuseradd-mode");
        txt.setText(mode);
        txt.setVisible(false);
        nf.addComponent(txt);
        
        request.retainData(txt);
        
        txt = new Textbox();
        txt.setId("webuseradd-updfile");
        txt.setLabel("Avatar");    
//        upd.setStyle(new Style().setAttr("n-action", "webassetadd").setAttr("n-accept", "*.pdf"));
        nf.addComponent(txt);
        
        request.retainData(txt);
        
        
        Combobox comb = new Combobox();
        
        Nikitaset ns = nikitaConnection.Query("SELECT position FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
        if (ns.getText(0, 0).equals("9")) {
            comb.setData(Nset.readJSON("[{'id':'0','text':'User'},{'id':'1','text':'Super User'},{'id':'2','text':'Admin'},{'id':'3','text':'System'},{'id':'9','text':'Nikita Generator'}]", true));
        }else if (ns.getText(0, 0).equals("3")) {
            comb.setData(Nset.readJSON("[{'id':'0','text':'User'},{'id':'1','text':'Super User'},{'id':'2','text':'Admin'},{'id':'3','text':'System'}]", true));
        }else if (ns.getText(0, 0).equals("2")) {
            comb.setData(Nset.readJSON("[{'id':'0','text':'User'},{'id':'1','text':'Super User'},{'id':'2','text':'Admin'}]", true));
        }else if (ns.getText(0, 0).equals("1")) {
            comb.setData(Nset.readJSON("[{'id':'0','text':'User'},{'id':'1','text':'Super User'}]", true));
        }else{
            comb.setData(Nset.readJSON("[{'id':'0','text':'User'}]", true));
        }   
        position=Utility.getInt((ns.getText(0, 0)));
 
        comb.setId("webuseradd-txtPosition");
        comb.setLabel("Position");
        nf.addComponent(comb);
        
        Button btn = new Button();
        btn.setId("webuseradd-btnSave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webuseradd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    if (NikitaService.isModeCloud()) {
                        response.showDialog("Add", "Don't Hack Please", "", "OK");
                        response.write();
                        return;
                    }
                            
                    String password = response.getContent().findComponentbyId("webuseradd-txtPassword").getText().trim();
                    if (password.length()>=1) {
                        password=Utility.MD5(password);
                    }
                            
                    Nikitaset nikitaset = nikitaConnection.Query("insert into sys_user("+
                                          "name,username,password,imei,position,avatar,createdby,createddate)"+
                                          "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("webuseradd-txtName").getText(),
                                          response.getContent().findComponentbyId("webuseradd-txtUser").getText(),
                                          password,
                                          Utility.replace(response.getContent().findComponentbyId("webuseradd-txtImei").getText(), " ", ""),
                                          response.getContent().findComponentbyId("webuseradd-txtPosition").getText(),
                                          response.getContent().findComponentbyId("webuseradd-updfile").getText(),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webuseradd-txtUser").getText());
                        data.setData("activitytype", "user");
                        data.setData("mode", "add");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);

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
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webuseradd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString();      
                    Nikitaset nikitaset ;
                    nikitaset = nikitaConnection.Query("SELECT password,position FROM sys_user WHERE userid = ?", id); 
                                  
                    String password = response.getContent().findComponentbyId("webuseradd-txtPassword").getText().trim();
                    if (response.getContent().findComponentbyId("webuseradd-txtPassword").getText().equals(Utility.MD5(nikitaset.getText(0, 0)))  ) {
                        password=nikitaset.getText(0, 0);
                    }else{
                        password = Utility.MD5( response.getContent().findComponentbyId("webuseradd-txtPassword").getText() );
                    }                  
                                       
                    String username = response.getContent().findComponentbyId("webuseradd-txtUser").getText();
                    if (NikitaService.isModeCloud()) {
                        username = response.getVirtualString("@+SESSION-LOGON-USER");                
                    }else if (Utility.getInt(nikitaset.getText(0, 1))!=9) {
                        username = response.getVirtualString("@+SESSION-LOGON-USER"); 
                    }
                    nikitaset = nikitaConnection.Query("update sys_user set name=?,username=?,password=?,imei=?,avatar=?,position=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                          "where userid=?",
                                          response.getContent().findComponentbyId("webuseradd-txtName").getText(),
                                          username,
                                          password,
                                          Utility.replace(response.getContent().findComponentbyId("webuseradd-txtImei").getText(), " ", ""),
                                          response.getContent().findComponentbyId("webuseradd-updfile").getText(),
                                          response.getContent().findComponentbyId("webuseradd-txtPosition").getText(),
                                          user,id);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{                    
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webuseradd-txtUser").getText());
                        data.setData("activitytype", "user");
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
            nf.findComponentbyId("webuseradd-txtName").setText(nset.getData(1).toString());
            nf.findComponentbyId("webuseradd-txtUser").setText(nset.getData(2).toString());
            nf.findComponentbyId("webuseradd-txtPassword").setText(Utility.MD5(nset.getData(3).toString()));
            nf.findComponentbyId("webuseradd-txtImei").setText(Utility.insertString(nset.getData(4).toString(), " ", 4));
            nf.findComponentbyId("webuseradd-txtPosition").setText(nset.getData(5).toString());
            nf.findComponentbyId("webuseradd-updfile").setText(nset.getData(6).toString());
            nf.findComponentbyId("webuseradd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("User Insert");
            nf.findComponentbyId("webuseradd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("User Add");            
            nf.findComponentbyId("webuseradd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        
        
        response.setContent(nf);
    }
    
}
