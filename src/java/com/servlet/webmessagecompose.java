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
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
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
public class webmessagecompose extends NikitaServlet{
    
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Compose");
        
      
        
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "330");
        nf.setStyle(style);
        
        Label lbl = new Label();
        lbl.setId("compose-title");
        lbl.setText("New Message");
        style.setStyle("font-family", "Times new roman");
        style.setStyle("margin-bottom", "30px");
        style.setStyle("font-weight", "bold");
        style.setStyle("margin-top", "10px");
        style.setStyle("font-size", "20px");
        style.setStyle("border-style", "none");
        lbl.setStyle(style);
        lbl.setVisible(false);
        nf.addComponent(lbl);
        
        Textbox txt = new Textbox();
        txt.setId("compose-sender");
        txt.setLabel("Sender");
        txt.setTag(request.getParameter("mode"));
        txt.setVisible(false);
        nf.addComponent(txt);                
                
        txt = new Textbox();
        txt.setId("compose-target");
        txt.setLabel("Target");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("compose-subject");
        txt.setLabel("Subject");
        nf.addComponent(txt);
        
        Textarea area = new Textarea();
        area.setId("compose-body");
        area.setLabel("Body");
        nf.addComponent(area);
        
        Checkbox cb2 = new Checkbox();   
        cb2.setId("compose-type");
         cb2.setLabel("   ");
 
        cb2.setData(Nset.readJSON("[['alert','Display as Alert']]", true)); 
        nf.addComponent(cb2);
        
        
        
        
        Combobox comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'none','text':'Pilih salah satu'},{'id':'alert','text':'Alert'},{'id':'txt','text':'Text'}]", true));
        comb.setId("xcompose-type");
        comb.setText("none");
        comb.setLabel("Type");
        comb.setVisible(false);
        nf.addComponent(comb);
        
        
        Button btn = new Button();
        btn.setId("compose-btnSave");
        btn.setText("Send");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("compose-btnSave").getTag());
                
                    Nikitaset nikitaset = nikitaConnection.Query("INSERT INTO nikita_message("+
                                          "subject,body,target,msgtype,username,sender,status,sdate)"+
                                          "values(?,?,?,"+(response.getContent().findComponentbyId("compose-type").getText().contains("alert")?"'alert'":"'txt'")+",?,?,'outbox',"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("compose-subject").getText().toUpperCase(),
                                          response.getContent().findComponentbyId("compose-body").getText(),
                                          response.getContent().findComponentbyId("compose-target").getText(),
                                          user,user);
                    
                    nikitaset = nikitaConnection.Query("INSERT INTO nikita_message("+
                                          "subject,body,target,msgtype,username,sender,status,sdate)"+
                                          "values(?,?,?,"+(response.getContent().findComponentbyId("compose-type").getText().contains("alert")?"'alert'":"'txt'")+",?,?,'inbox',"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("compose-subject").getText().toUpperCase(),
                                          response.getContent().findComponentbyId("compose-body").getText(),
                                          response.getContent().findComponentbyId("compose-target").getText(),
                                          response.getContent().findComponentbyId("compose-target").getText(),user);
                    
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", "composemsg");
                        data.setData("activitytype", "composemsg");
                        data.setData("mode", "send");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);

                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    }
                clearList(response);
                response.sendChat("*NIKITA");
                response.write();
        
            }
        });
        nf.setOnBackListener(new NikitaForm.OnBackListener() {

            @Override
            public void OnBack(NikitaRequest request, NikitaResponse response, Component component) {
                    Nset nset = Nset.newObject();  
                    response.closeform(response.getContent());
                    response.setResult("back",nset);    
                    
                    response.write();
        
            }
        });
        nf.setBackConsume(true);
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
            }
        });
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                if(!response.getContent().findComponentbyId("compose-sender").getTag().equals("")){
                    response.getContent().findComponentbyId("compose-title").setVisible(false);
                    response.getContent().findComponentbyId("compose-sender").setVisible(true);
                    response.getContent().findComponentbyId("compose-btnSave").setVisible(false);
                }
            }
        });
        
        
        if(!nf.findComponentbyId("compose-sender").getTag().equals("")){
            Nikitaset nikitaset = nikitaConnection.Query("SELECT sender,target,subject,body,msgtype FROM nikita_message WHERE messageid=?", request.getParameter("msgid"));
            nf.findComponentbyId("compose-sender").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("compose-target").setText(nikitaset.getText(0, 1));
            nf.findComponentbyId("compose-subject").setText(nikitaset.getText(0, 2));
            nf.findComponentbyId("compose-body").setText(nikitaset.getText(0, 3));
            if(nikitaset.getText(0, 4).equals("alert")){                    
                nf.findComponentbyId("compose-type").setText("[\"alert\"]");
            }
//            nf.setText((response.getContent().findComponentbyId("compose-sender").getTag().equals("inbox")?"Inbox":"Outbox"));
        }
        
        response.setContent(nf);
               
    }
    
    
    private void clearList(NikitaResponse response){
        response.getContent().findComponentbyId("compose-subject").setText("");
        response.getContent().findComponentbyId("compose-target").setText("");
        response.getContent().findComponentbyId("compose-body").setText("");
        response.getContent().findComponentbyId("compose-type").setText("none");
    }
    
     
    
}
