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
import com.web.utility.WebUtility;
import com.servlet.mobile.NikitaNotification;

/**
 *
 * @author user
 */
public class nikitatargetdeviceadd extends NikitaServlet{
    
    int dbCore;
    NikitaConnection nikitaConnection ;
    String user;
    /*parameter
    mode
    data
    id
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
                
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "300");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("targetAdd-txtAccount");
        txt.setLabel("Account");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("targetAdd-txtImei");
        txt.setLabel("Imei");  
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("targetAdd-btnSave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("targetAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    NikitaNotification.nikitaTargetDevice(user, 
                            response.getContent().findComponentbyId("targetAdd-txtAccount").getText(), 
                            response.getContent().findComponentbyId("targetAdd-txtImei").getText(), response);
                    
                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    
                    
                }
                response.write();
                
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("targetAdd-btnSave").getTag());
                    String id = nset.getData("id").toString();                    

                    Nikitaset nikitaset = nikitaConnection.Query("UPDATE nikita_target_device SET targetaccount=?,targetimei=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+""+
                                          "WHERE targetid=?",
                                          response.getContent().findComponentbyId("targetAdd-txtAccount").getText(),
                                          response.getContent().findComponentbyId("targetAdd-txtImei").getText(),
                                          user,
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
            nf.setText("Target Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("targetAdd-txtAccount").setText(nset.getData(1).toString());
            nf.findComponentbyId("targetAdd-txtImei").setText(nset.getData(2).toString());
            nf.findComponentbyId("targetAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Target Insert");
            nf.findComponentbyId("targetAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Target Add");            
            nf.findComponentbyId("targetAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
    }
    
    
}
