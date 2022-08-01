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
import com.nikita.generator.action.DateFormatAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.DateTime;
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
public class webschmanagementadd extends NikitaServlet{
    
    Label idtype ;
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
        style.setStyle("height", "400");
        style.setStyle("n-maximizable", "false");
        style.setStyle("n-resizable", "false");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webschadd-txtName");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("webschadd-txtText");
        txt.setLabel("Text");
        nf.addComponent(txt);
        
        Combobox comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'active','text':'Active'},{'id':'inactive','text':'Inactive'}]", true));
        comb.setId("webschadd-txtMode");
        comb.setLabel("Mode");
        nf.addComponent(comb);
        
        comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'mobile','text':'Mobile Scheduller'},{'id':'web','text':'Web Scheduller'}]", true));
        comb.setId("webschadd-txtType");
        comb.setLabel("Type");
        nf.addComponent(comb);
        
        Textarea area = new Textarea();        
        area.setId("webschadd-areaEvent");
        area.setVisible(false);
        area.setLabel("Event");
        nf.addComponent(area);
        
        txt = new Textbox();
        txt.setId("webschadd-runFirst");
        txt.setHint("First Time (14:00:00)");
        txt.setLabel("Run First");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webschadd-interval");
        txt.setHint("second");
        txt.setLabel("Interval");
        txt.setStyle(new Style().setStyle("text-align", "right"));
        nf.addComponent(txt);
        
        DateTime dt = new DateTime();
        dt.setId("webschadd-startDate");
        dt.setVisible(false);
        dt.setLabel("Start Date");
        nf.addComponent(dt);
        
        dt = new DateTime();
        dt.setId("webschadd-finishDate");
        dt.setLabel("Finish Date");
        dt.setVisible(false);
        nf.addComponent(dt);
        
        
        Button btn = new Button();
        btn.setId("webschadd-btnSave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "90px");
        style.setStyle("height", "35px");
        style.setStyle("float", "right");
        style.setStyle("margin-top", "25px");
        style.setStyle("margin-right", "35px")      ;
        btn.setStyle(style); 
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webschadd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    //long l = Utility.getDateTime(response.getContent().findComponentbyId("webschadd-runFirst").getText());
                    long m = Utility.getDateTime(response.getContent().findComponentbyId("webschadd-startDate").getText());
                    long n = Utility.getDateTime(response.getContent().findComponentbyId("webschadd-finishDate").getText());
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_scheduller("+
                                          "threadname,threadtext,threadmode,threadtype,runfirst,runinterval,startdate,finishdate,createdby,createddate)"+
                                          "values(?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("webschadd-txtName").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtText").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtMode").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtType").getText(),
                                          response.getContent().findComponentbyId("webschadd-runFirst").getText(),
                                          Utility.getInt(response.getContent().findComponentbyId("webschadd-interval").getText())+"",
                                          Utility.formatDate(m, "yyyy-MM-dd HH:mm:ss"),
                                          Utility.formatDate(n, "yyyy-MM-dd HH:mm:ss"),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webschadd-txtName").getText().toUpperCase());
                        data.setData("activitytype", "scheduller");
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
                    
                    NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webschadd-btnSave").getTag());
                    String id = nset.getData("id").toString();  
                    Nikitaset nikitaset = nikitaConnection.Query("UPDATE web_scheduller SET threadname=?,threadtext=?,threadmode=?,threadtype=?,runfirst=?,runinterval=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" WHERE threadid=?",
                                          response.getContent().findComponentbyId("webschadd-txtName").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtText").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtMode").getText(),
                                          response.getContent().findComponentbyId("webschadd-txtType").getText(),
                                          response.getContent().findComponentbyId("webschadd-runFirst").getText(),
                                          Utility.getInt(response.getContent().findComponentbyId("webschadd-interval").getText())+"",
                                          user,id);
                    
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{                    
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webschadd-txtName").getText().toUpperCase());
                        data.setData("activitytype", "scheduller");
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
            nf.setText("Scheduller Edit");
//            response.showAlert(request.getParameter("data"));
            Nikitaset nikitaset = nikitaConnection.Query("SELECT threadname,threadtext,threadmode,threadtype,threadevent,runfirst,runinterval,startdate,finishdate FROM web_scheduller WHERE threadid=?", request.getParameter("data"));
            nf.findComponentbyId("webschadd-txtName").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("webschadd-txtText").setText(nikitaset.getText(0, 1));
            nf.findComponentbyId("webschadd-txtMode").setText(nikitaset.getText(0, 2));
            nf.findComponentbyId("webschadd-txtType").setText(nikitaset.getText(0, 3));
            nf.findComponentbyId("webschadd-areaEvent").setText(nikitaset.getText(0, 4));
            nf.findComponentbyId("webschadd-runFirst").setText(nikitaset.getText(0, 5));
            nf.findComponentbyId("webschadd-interval").setText(nikitaset.getText(0, 6));
            nf.findComponentbyId("webschadd-btnSave").setText("Update");
            nf.findComponentbyId("webschadd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", request.getParameter("data")).toJSON());
        }
        else{
            nf.setText("Scheduller Add");            
            nf.findComponentbyId("webschadd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
               
    }
    
    
}
