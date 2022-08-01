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
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;
import com.servlet.mobile.NikitaNotification;

/**
 *
 * @author user
 */
public class nikitaregisterdeviceadd extends NikitaServlet{
    
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
        txt.setId("regisAdd-txtAccount");
        txt.setLabel("Account");
        nf.addComponent(txt);
        

        Textsmart text = new Textsmart();
        text.setId("regisAdd-txtTarget");
        text.setLabel("Target");
        nf.addComponent(text);
        text.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode","selected");
                request.setParameter("search",response.getContent().findComponentbyId("regisAdd-txtTarget").getText());
                response.showform("nikitatargetdevice", request,"target", true);
                response.write();
            }
        });
        
        txt = new Textbox();
        txt.setId("regisAdd-txtApplication");
        txt.setLabel("Application");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("regisAdd-txtImei");
        txt.setLabel("Imei");  
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("regisAdd-btnSave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("regisAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    NikitaNotification.nikitaRegisterDevice(user, 
                            response.getContent().findComponentbyId("regisAdd-txtAccount").getText(), 
                            response.getContent().findComponentbyId("regisAdd-txtTarget").getText(), 
                            response.getContent().findComponentbyId("regisAdd-txtImei").getText(),
                            response.getContent().findComponentbyId("regisAdd-txtApplication").getText(), response);
                    
                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    
                    
                }
                response.write();
                
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("target") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("regisAdd-txtTarget").setText(result.getData("targetaccount").toString());                    
                    response.writeContent();
                }
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("regisAdd-btnSave").getTag());
                    String id = nset.getData("id").toString();                    

                    Nikitaset nikitaset = nikitaConnection.Query("UPDATE nikita_register_device set registeraccount=?,registertarget=?,registerapp=?,registerimei=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+""+
                                          "WHERE registerid=?",
                                          response.getContent().findComponentbyId("regisAdd-txtAccount").getText(),
                                          response.getContent().findComponentbyId("regisAdd-txtTarget").getText(),
                                          response.getContent().findComponentbyId("regisAdd-txtApplication").getText(),
                                          response.getContent().findComponentbyId("regisAdd-txtImei").getText(),
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
            nf.setText("Register Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("regisAdd-txtAccount").setText(nset.getData(1).toString());
            nf.findComponentbyId("regisAdd-txtTarget").setText(nset.getData(2).toString());
            nf.findComponentbyId("regisAdd-txtApplication").setText(nset.getData(3).toString());
            nf.findComponentbyId("regisAdd-txtImei").setText(nset.getData(4).toString());
            nf.findComponentbyId("regisAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Register Insert");
            nf.findComponentbyId("regisAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Register Add");            
            nf.findComponentbyId("regisAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
    }
    
    
}
