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
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
 

/**
 *
 * @author user
 */
public class webparameteradd extends NikitaServlet{
    
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
        
        idtype = new Label();
        idtype.setId("webparamadd-x-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "450");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webparamadd-txtkey");
        txt.setLabel("Key [Name]");
        txt.setStyle(new Style().setStyle("text-transform", "uppercase"));
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("webparamadd-txtvalue");
        txt.setLabel("Value");
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("webparamadd-txtdescribe");
        txt.setLabel("Describe");
        nf.addComponent(txt);
        
        Combobox comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'mobileform','text':'Mobile Form'},{'id':'webform','text':'Web Form'},{'id':'link','text':'Link (WebService)'},{'id':'scheduler','text':'Link (WebScheduler)'}]", true));
        comb.setId("webparamadd-txtType");
        comb.setLabel("Type");
        nf.addComponent(comb);
        
        comb = new Combobox();
        Nikitaset ns = nikitaConnection.Query("SELECT position FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
        if (ns.getText(0, 0).equals("9") || ns.getText(0, 0).equals("3")) {
            comb.setData(Nset.readJSON("[{'id':'user','text':'User'},{'id':'admin','text':'Admin'},{'id':'system','text':'System'}]", true));
        }else if (ns.getText(0, 0).equals("2")) {
            comb.setData(Nset.readJSON("[{'id':'user','text':'User'},{'id':'admin','text':'Admin'}]", true));
        }else{
            comb.setData(Nset.readJSON("[{'id':'user','text':'User'}]", true));
        }   
 
        comb.setId("webparamadd-txtPriority");
        comb.setLabel("Priority");
        nf.addComponent(comb);
        if (NikitaService.isModeCloud()) {
            comb.setVisible(false);
        }
        Button btn = new Button();
        btn.setId("webparamadd-btnSave");
        btn.setText("Save");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webparamadd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    Nikitaset nikitaset = nikitaConnection.Query("insert into "+(NikitaService.isModeCloud()?"web_parameters":"web_parameter")+"("+
                                          "paramkey,paramvalue,paramdescribe,paramtype,parampriority,createdby,createddate)"+
                                          "values(?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("webparamadd-txtkey").getText().toUpperCase(),
                                          response.getContent().findComponentbyId("webparamadd-txtvalue").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtdescribe").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtType").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtPriority").getText(),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webparamadd-txtkey").getText().toUpperCase());
                        data.setData("activitytype", "parameter");
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
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webparamadd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString();  
                    Nikitaset nikitaset ;
                    
                    if (NikitaService.isModeCloud()) {
                        nikitaset = nikitaConnection.Query("update web_parameters set paramkey=?, paramvalue=?,paramdescribe=?,paramtype=?,parampriority=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" where paramkey=? AND createdby = ? ",
                                          response.getContent().findComponentbyId("webparamadd-txtkey").getText().toUpperCase(),
                                          response.getContent().findComponentbyId("webparamadd-txtvalue").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtdescribe").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtType").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtPriority").getText(),
                                          user,id, user);
                    }else{
                        nikitaset = nikitaConnection.Query("update web_parameter set paramkey=?, paramvalue=?,paramdescribe=?,paramtype=?,parampriority=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" where paramkey=?",
                                          response.getContent().findComponentbyId("webparamadd-txtkey").getText().toUpperCase(),
                                          response.getContent().findComponentbyId("webparamadd-txtvalue").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtdescribe").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtType").getText(),
                                          response.getContent().findComponentbyId("webparamadd-txtPriority").getText(),
                                          user,id);
                    }
                   
                    
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{                    
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("webparamadd-txtkey").getText().toUpperCase());
                        data.setData("activitytype", "parameter");
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
        
        if(mode.equals("edit")){
            nf.setText("Parameter Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("webparamadd-txtkey").setText(nset.getData(1).toString());
            nf.findComponentbyId("webparamadd-txtvalue").setText(nset.getData(2).toString());
            nf.findComponentbyId("webparamadd-txtdescribe").setText(nset.getData(3).toString());
            nf.findComponentbyId("webparamadd-txtType").setText(nset.getData(4).toString());
            nf.findComponentbyId("webparamadd-txtPriority").setText(nset.getData(5).toString());
            nf.findComponentbyId("webparamadd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Parameter Insert");
            nf.findComponentbyId("webparamadd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Parameter Add");            
            nf.findComponentbyId("webparamadd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
               
    }
    
    
     private void setupList(NikitaResponse response){
        if (idtype.getText().equals("")) {
            response.getContent().findComponentbyId("webparamadd-txtType").setData(Nset.readJSON("[{'id':'mobile','text':'Mobile'},{'id':'web/link','text':'Web/Link'}]", true));
        
        }else if (idtype.getText().equals("link")) {
            response.getContent().findComponentbyId("webparamadd-txtType").setData(Nset.readJSON("[{'id':'web/link','text':'Web/Link'}]", true));
           
        }else if (idtype.getText().equals("webform")) {
            response.getContent().findComponentbyId("webparamadd-txtType").setData(Nset.readJSON("[{'id':'web/link','text':'Web/Link'}]", true));            
        }else if (idtype.getText().equals("mobileform")) {
            response.getContent().findComponentbyId("webparamadd-txtType").setData(Nset.readJSON("[{'id':'mobile','text':'Mobile'}]", true));
            
        }
     } 
    
}
