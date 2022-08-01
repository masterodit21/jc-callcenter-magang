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
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webfilteradd extends NikitaServlet{
    
    /*parameter
    mode
    data
    id
    */
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "280");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("filterAdd-txtFilterId");
        txt.setLabel("Filter Id");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("filterAdd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        Textsmart text = new Textsmart();
        text.setId("filterAdd-txtForm");
        text.setLabel("Form Filter");
        nf.addComponent(text);
        text.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                request.setParameter("code", "selectedmodul");
                request.setParameter("type", "");
                response.showform("webform", request,"filter",true);      
                response.write();
            }
        });
        
        Textarea txtarea = new Textarea();
        txtarea.setId("filterAdd-txtDesc");
        txtarea.setLabel("Description");
        nf.addComponent(txtarea);
        
        Button btn = new Button();
        btn.setId("filterAdd-btnSave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("filterAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_filter("+
                                          "filtername,filterform,filterdesc,createdby,createddate)"+
                                          "values(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                        
                                          response.getContent().findComponentbyId("filterAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("filterAdd-txtForm").getText(),
                                          response.getContent().findComponentbyId("filterAdd-txtDesc").getText(),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("filterAdd-txtName").getText());
                        data.setData("activitytype", "filter");
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
                if(reqestcode.equals("filter") && responsecode.equals("OK")){
//                    response.getContent().findComponentbyId("filterAdd-txtForm").setTag(result.getData("id").toString());
                    response.getContent().findComponentbyId("filterAdd-txtForm").setText(result.getData("name").toString());
                    
                    response.writeContent();
                }
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("filterAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString();  
                    Nikitaset nikitaset = nikitaConnection.Query("update web_filter set filterid=?,filtername=?,filterform=?,filterdesc=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+""+
                                          "where filterid=?",
                                          id,
                                          response.getContent().findComponentbyId("filterAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("filterAdd-txtForm").getText(),
                                          response.getContent().findComponentbyId("filterAdd-txtDesc").getText(),
                                          user,id);
                    
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                       
                    else{                    
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("filterAdd-txtName").getText());
                        data.setData("activitytype", "filter");
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
            nf.setText("Filter Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("filterAdd-txtFilterId").setText(nset.getData(1).toString());
            nf.findComponentbyId("filterAdd-txtName").setText(nset.getData(2).toString());
            nf.findComponentbyId("filterAdd-txtForm").setText(nset.getData(3).toString());
            nf.findComponentbyId("filterAdd-txtDesc").setText(nset.getData(4).toString());
            
            nf.findComponentbyId("filterAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Filter Insert");
            nf.findComponentbyId("filterAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Filter Add");            
            nf.findComponentbyId("filterAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
               
    }
    
    
}
