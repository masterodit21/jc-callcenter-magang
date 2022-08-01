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
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class login  extends NikitaServlet implements Component.OnClickListener{
    NikitaConnection nikitaConnection;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nikitaForm = new NikitaForm(this);
        nikitaForm.setText("Login");
        
        Textbox textbox = new Textbox();
        textbox.setId("username");
        textbox.setLabel("Username");
         textbox.setOnClickListener(this); 
        nikitaForm.addComponent(textbox);
        
        textbox = new Textbox();
        textbox.setId("password");
        textbox.setLabel("Password");
        textbox.setStyle(new Style().setStyle("n-password", "true"));        
        nikitaForm.addComponent(textbox);
         textbox.setOnClickListener(this);         
         
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Button tbl = new Button();
        tbl.setId("login-signout");
        tbl.setText("Cancel");
        tbl.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.setVirtual("@+SESSION-LOGON", "false");
            }
        });
        tbl.setStyle(new Style().setStyle("width", "90px"));
        horisontalLayout.addComponent(tbl); 
        
        tbl = new Button();
        tbl.setId("login-signin");
        tbl.setText("Signin");
        tbl.setOnClickListener(this);         
        tbl.setStyle(new Style().setStyle("width", "90px"));
        horisontalLayout.addComponent(tbl);
        horisontalLayout.setStyle(new Style().setStyle("margin-top", "10px").setStyle("margin-left", "92px").setStyle("float", "right"));
        
        
        nikitaForm.addComponent(horisontalLayout);
        
        nikitaForm.setStyle(new Style().setStyle("width", "380").setStyle("height", "180").setStyle("n-closable", "false").setStyle("n-minimizable", "false").setStyle("n-maximizable", "false").setStyle("n-resizable", "false"));
        
        nikitaForm.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("123")){
                    Nikitaset ns = nikitaConnection.Query("SELECT username,auth,status,name,avatar,position FROM sys_user WHERE username=? AND password=? ",response.getContent().findComponentbyId("username").getText(), Utility.MD5(response.getContent().findComponentbyId("password").getText()));
                    if( responsecode.equals("button2")){
                       login(request, response, component, ns, 0);//YA new Seesion
                    } else  if(  responsecode.equals("button1")){
                       login(request, response, component, ns, 1);
                    }                         
                }
            }
        });
        
        response.setContent(nikitaForm);
    }
    
    public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nikitaset ns = nikitaConnection.Query("SELECT username,auth,status,name,avatar,position FROM sys_user WHERE username=? AND password=? ",response.getContent().findComponentbyId("username").getText(), Utility.MD5(response.getContent().findComponentbyId("password").getText()));
                //System.out.println(response.getContent().findComponentbyId("username").getText());
                //System.out.println(Utility.MD5(response.getContent().findComponentbyId("password").getText()));
                //System.out.println(ns.toNset().toJSON());
                  
               if (ns.getError().trim().length()>=1) {
                    response.showDialog("Error", ns.getError(), "", "OK");
               }else if (ns.getRows()>=1) {
                    if (ns.getText(0, "status").equals("")) {
                        login(request, response, component, ns, 0);
                    }else if (ns.getText(0, "status").toLowerCase().equals("mdialog")) {
                        login(request, response, component, ns, 9);
                        response.showDialog("Information", "Apakah anda ingin menggunakan Session baru ?", "123", "Tidak", "Ya");
                    }else if (ns.getText(0, "status").toLowerCase().equals("multi")) {    
                        login(request, response, component, ns, 1);
                    }else{
                        response.showDialog("Information", "Username belum diaktifkan", "", "OK");
                    }
                }else{
                    response.showDialog("Information", "Username or Password wrong", "", "OK");
                }
    }
    public void login(NikitaRequest request, NikitaResponse response, Component component, Nikitaset ns, int status) {
        String token = ns.getText(0, "auth");
        if (status == 0) {
            token = Utility.MD5(response.getContent().findComponentbyId("username").getText()+":"+response.getContent().findComponentbyId("password").getText()+":"+System.currentTimeMillis());
            nikitaConnection.Query("UPDATE sys_user SET auth=? WHERE username=? ", token, response.getContent().findComponentbyId("username").getText());
        }
        response.setVirtual("@+SESSION-LOGON", token);
        response.setVirtual("@+SESSION-LOGON-NAME", ns.getText(0, "name"));
//                        response.setVirtual("@+SESSION-LOGON-USER", response.getContent().findComponentbyId("username").getText());
        response.setVirtual("@+SESSION-LOGON-MODE", ns.getText(0, "position"));
        response.setVirtual("@+SESSION-LOGON-USER", ns.getText(0, "username"));
        response.setVirtual("@+SESSION-LOGON-AVATAR", ns.getText(0, "avatar").trim());

       

        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitacode").toString().contains("cookielogin")) {
            response.setVirtual("@+COOKIE-LOGUSE", Utility.toHexString(ns.getText(0, "username").getBytes()) );
            response.setVirtual("@+COOKIE-LOGSES", Utility.MD5(Utility.NowDate()+token));
        }

        if (status == 9) {
            return;            
        }
        //history timesheet
        Nset data = Nset.newObject(); 
        data.setData("username", response.getContent().findComponentbyId("username").getText());
        data.setData("username", ns.getText(0, "username"));
        data.setData("application", "Nikita Generator");
        data.setData("activityname", "signin");
        data.setData("activitytype", "signin");
        data.setData("mode", "signin");
        data.setData("additional", "");
        Utility.SaveActivity(data, response);
        if (response.getVirtualString("@+SESSION-RELOG" ).equals("true") ) {
           // response.closeform(response.getContent());
        }else{
           // response.reloadBrowser();
        }      
        response.reloadBrowser();
    }
}
