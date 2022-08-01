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
import com.nikita.generator.ui.Label;
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
public class webformadd extends NikitaServlet{
    Label idtype ;
    String user ;
    Label condition;
    int dbCore;
    NikitaConnection nikitaConnection ;
    /*parameter
    mode
    id
    data
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
    
        condition = new Label();
        condition.setId("formadd-lblcreatedby");
        condition.setText(request.getParameter("unlock")); 
        condition.setTag(request.getParameter("created"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        //add 13K
        idtype = new Label();
        idtype.setId("formadd-x-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        final Label lblmode = new Label();
        lblmode.setId("formAdd-mode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
         
        final Label lbldata = new Label();
        lbldata.setId("formAdd-data");
        lbldata.setTag(request.getParameter("data"));
        lbldata.setVisible(false);
        nf.addComponent(lbldata);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "450");
        nf.setStyle(style);
        
        Textbox txt = new Textsmart();
        txt.setId("formAdd-txtName");
        txt.setStyle(Style.createStyle().setStyle("n-char-accept", "[a-z0-9A-Z._]+").setStyle("n-char-lcase", "true"));
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("formAdd-txtNamebckup");
        txt.setLabel("Name");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("formAdd-txtTitle");
        txt.setLabel("Title");
        nf.addComponent(txt);
        
        Combobox comb = new Combobox();
        
        comb.setData(Nset.readJSON("[{'id':'mobileform','text':'Mobile Form'},{'id':'webform','text':'Web Form'},{'id':'form','text':'Form'},{'id':'link','text':'Link (WebService)'},{'id':'scheduler','text':'Link (WebScheduler)'}]", true));
        comb.setId("formAdd-txtType");
        comb.setLabel("Type");
        nf.addComponent(comb);
        
        Textarea txtArea = new Textarea();
        txtArea.setId("formAdd-txtStyle");
        txtArea.setLabel("Style");  
        nf.addComponent(txtArea);
        
        txtArea = new Textarea();
        txtArea.setId("formAdd-txtDescribe");
        txtArea.setLabel("Describe");  
        nf.addComponent(txtArea);
        
        Button btn = new Button();
        btn.setId("formAdd-btnSave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "285px");
        style.setStyle("margin-top", "10px");
        btn.setStyle(style);  
        nf.addComponent(btn);        
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("formAdd-btnSave").getTag());
               
                if(lblmode.getTag().equals("edit")){                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");
                    }else{
                        response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to update", "warning",null, "OK", "");
                    }                                     
                }  else{
                    //add 28102014
                    
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT MAX(formindex) FROM web_form;");
                    
                    String fname = response.getContent().findComponentbyId("formAdd-txtName").getText();
                    if (NikitaService.isModeCloud()) {
                        String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                        if (fname.startsWith(prefix)) {                            
                        }else{
                            fname = prefix + fname;
                        }
                    }
//                    nikitaset = nikitaConnection.Query("SELECT formname FROM web_form WHERE formname = ?", response.getContent().findComponentbyId("formAdd-txtName").getText());
//                    
//                    if(nikitaset.getRows() > 0){
//                        response.showDialogResult("Warning", "form name is exist", "warning",null, "", "OK");                
//                    }else{                    
                        nikitaset = nikitaConnection.Query("INSERT INTO web_form("+
                                              "formname,formtitle,formtype,formstyle,formdescribe,createdby,createddate,formindex)"+
                                              "values(?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+",?)",
                                              fname,
                                              response.getContent().findComponentbyId("formAdd-txtTitle").getText(),
                                              response.getContent().findComponentbyId("formAdd-txtType").getText(),
                                              response.getContent().findComponentbyId("formAdd-txtStyle").getText(),
                                              response.getContent().findComponentbyId("formAdd-txtDescribe").getText(),
                                              user, (Utility.getInt(nikitaset.getText(0, 0))+1)+"" );
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("formAdd-txtName").getText());
                            data.setData("activitytype", response.getContent().findComponentbyId("formAdd-txtType").getText());
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
//                    }
                    
                }
                response.write();
                
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {

                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("formAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String nameform = response.getContent().findComponentbyId("formAdd-txtName").getText();
                    String id = nset.getData("id").toString();                        
                    Nikitaset flagroute2 = nikitaConnection.Query("SELECT formname FROM web_form WHERE formid = ? ", id);
                    String x = "%\""+flagroute2.getText(0, 0)+"\\\\%"; 
                    String y = "%\""+flagroute2.getText(0, 0)+"\"%"; 
                    Nikitaset flagroute = nikitaConnection.Query("SELECT routeid,action FROM web_route WHERE action LIKE ? OR action LIKE ? ", x,y);
                    for (int i = 0; i < flagroute.getRows(); i++) {
                        x = flagroute.getText(i, 1);
                        x = x.replace(flagroute2.getText(i, 0), nameform);
                    }

                    //form sync
                    Utility.FormSync(id);
                    
                    String fname = response.getContent().findComponentbyId("formAdd-txtName").getText();
                    if (NikitaService.isModeCloud()) {
                        String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                        if (fname.startsWith(prefix)) {                            
                        }else{
                            fname = prefix + fname;
                        }
                    }
                    
                    
                    Nikitaset nikitaset = nikitaConnection.Query("UPDATE web_form set formname=?,formtitle=?,formtype=?,formstyle=?,formDescribe=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                          "WHERE formid=?",
                                          fname,
                                          response.getContent().findComponentbyId("formAdd-txtTitle").getText(),
                                          response.getContent().findComponentbyId("formAdd-txtType").getText(),
                                          response.getContent().findComponentbyId("formAdd-txtStyle").getText(),
                                          response.getContent().findComponentbyId("formAdd-txtDescribe").getText(),
                                          user,id);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{                    
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("formAdd-txtName").getText());
                        data.setData("activitytype", response.getContent().findComponentbyId("formAdd-txtType").getText());
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
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                setupList(response);
            }
        });
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                setupList(response);
            }
        });
        if(lblmode.getTag().equals("edit")){
            nf.setText("Form Edit");
            Nset nset = Nset.readJSON(lbldata.getTag());
            
            
            String fname = nset.getData(1).toString();
            if (NikitaService.isModeCloud()) {
                String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                if (fname.startsWith(prefix)) {
                    fname = fname.substring(prefix.length());//1.34
                }
            }
            
            
            
            nf.findComponentbyId("formAdd-btnSave").setText("Update");
            nf.findComponentbyId("formAdd-txtName").setText(fname);
            nf.findComponentbyId("formAdd-txtNamebckup").setText(nset.getData(1).toString());
            nf.findComponentbyId("formAdd-txtTitle").setText(nset.getData(2).toString());
            nf.findComponentbyId("formAdd-txtType").setText(nset.getData(3).toString());
            nf.findComponentbyId("formAdd-txtStyle").setText(nset.getData(4).toString());
            nf.findComponentbyId("formAdd-txtDescribe").setText(nset.getData(5).toString());
            nf.findComponentbyId("formAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(lblmode.getTag().equals("insert")){
            nf.setText("Form Insert");
            nf.findComponentbyId("formAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Form Add");            
            nf.findComponentbyId("formAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
        
    }
    
     private void setupList(NikitaResponse response){
        if (idtype.getText().equals("")) {
            response.getContent().findComponentbyId("formAdd-txtType").setData(Nset.readJSON("[{'id':'mobileform','text':'Mobile Form'},{'id':'webform','text':'Web Form'},{'id':'form','text':'Form'},{'id':'link','text':'Link (Web Service)'}]", true));
        }else if (idtype.getText().equals("link")) {
            response.getContent().findComponentbyId("formAdd-txtType").setData(Nset.readJSON("[{'id':'link','text':'Link (WebService)'}]", true));
            response.getContent().findComponentbyId("formAdd-txtType").setText("link");
        }else if (idtype.getText().equals("webform")) {
            response.getContent().findComponentbyId("formAdd-txtType").setData(Nset.readJSON("[{'id':'webform','text':'Web Form'},{'id':'form','text':'Form'}]", true));
        }else if (idtype.getText().equals("mobileform")) {
            response.getContent().findComponentbyId("formAdd-txtType").setData(Nset.readJSON("[{'id':'mobileform','text':'Mobile Form'},{'id':'form','text':'Form'}]", true));
        }
     }   
}
    

