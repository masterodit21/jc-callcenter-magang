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
public class webtaskexpression extends NikitaServlet{
    
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
        nf.setText("Task Expression");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webtaskexp-txtParam1");
        txt.setLabel("Param1");
        nf.addComponent(txt);
               
        final Label lblmode = new Label();
        lblmode.setId("webexpression-mode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setText(request.getParameter("data"));  
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Combobox comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'equal','text':'Equal'},{'id':'contain','text':'Contain'},{'id':'startwith','text':'Start With'},{'id':'endwith','text':'End With'},{'id':'error','text':'Contain Error'}]", true));
        comb.setId("webtaskexp-txtExp");
        comb.setText("equal");
        comb.setLabel(" Boolean");
        nf.addComponent(comb);
        
        txt = new Textbox();
        txt.setId("webtaskexp-txtParam2");
        txt.setLabel("Param2");        
        nf.addComponent(txt);
        
        comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'none','text':'None'},{'id':'or','text':'OR'},{'id':'and','text':'AND'}]", true));
        comb.setId("webtaskexp-txtLogic");
        comb.setText("");
        comb.setHint("not use");
        comb.setLabel(" Logical");
        comb.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                               
                response.getContent().findComponentbyId("webtaskexp-txtParam3").setText("");
                response.getContent().findComponentbyId("webtaskexp-txtExp2").setText("equal");
                response.getContent().findComponentbyId("webtaskexp-txtParam4").setText("");
                condition2(response);
                response.writeContent();
            }
        }); 
        nf.addComponent(comb);
        
        
        txt = new Textbox();
        txt.setId("webtaskexp-txtParam3");
        txt.setLabel("Param3");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        
        comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'equal','text':'Equal'},{'id':'contain','text':'Contain'},{'id':'startwith','text':'Start With'},{'id':'endwith','text':'End With'},{'id':'error','text':'Contain Error'}]", true));
        comb.setId("webtaskexp-txtExp2");
        comb.setText("equal");
        comb.setLabel(" Boolean");
        comb.setVisible(false);
        nf.addComponent(comb);
        
        txt = new Textbox();
        txt.setId("webtaskexp-txtParam4");
        txt.setLabel("Param4");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        
        
        Button btn = new Button();
        btn.setId("webtaskexp-btnSave");
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
                Nset exp = Nset.newObject();
                
                Nset id = Nset.readJSON(lblmode.getText());   
                exp.setData("param1", response.getContent().findComponentbyId("webtaskexp-txtParam1").getText());
                exp.setData("parama", response.getContent().findComponentbyId("webtaskexp-txtExp").getText());
                exp.setData("param2", response.getContent().findComponentbyId("webtaskexp-txtParam2").getText());
                exp.setData("param3", response.getContent().findComponentbyId("webtaskexp-txtParam3").getText());
                exp.setData("paramc", response.getContent().findComponentbyId("webtaskexp-txtExp2").getText());
                exp.setData("paramb", response.getContent().findComponentbyId("webtaskexp-txtLogic").getText());
                exp.setData("param4", response.getContent().findComponentbyId("webtaskexp-txtParam4").getText());
                
                response.showDialogResult("Update",  "Do you want to update?", "update", Nset.newObject().setData("taskid", id.getData("taskid").toString() ).setData("expression", exp.toJSON()), "No", "Yes");                  
                
                response.write();
            
            }
        });
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {             
                Nset n = Nset.readJSON(  lblmode.getText());
                Nset exp = Nset.readJSON( n.getData("expression").toString() );
                
                response.getContent().findComponentbyId("webtaskexp-txtLogic").setText(exp.getData("paramb").toString());
                condition2(response);
                response.getContent().findComponentbyId("webtaskexp-txtParam1").setText(exp.getData("param1").toString());
                response.getContent().findComponentbyId("webtaskexp-txtParam2").setText(exp.getData("param2").toString());
                response.getContent().findComponentbyId("webtaskexp-txtParam3").setText(exp.getData("param3").toString());
                response.getContent().findComponentbyId("webtaskexp-txtParam4").setText(exp.getData("param4").toString());
                response.getContent().findComponentbyId("webtaskexp-txtExp").setText(exp.getData("parama").toString());
                response.getContent().findComponentbyId("webtaskexp-txtExp2").setText(exp.getData("paramc").toString());
                
            }
        });
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(lblmode.getTag().equals("edit")){
                    
                    if(reqestcode.equals("update") && responsecode.equals("button2")){
                        
                        Nikitaset nikitaset = nikitaConnection.Query("UPDATE web_scheduller_task SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",expression=?"+
                                                                     "WHERE taskid=?",                                                                 
                                              user,result.getData("expression").toString(),  result.getData("taskid").toString());
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{   
     
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                        response.write();
                    }
                }
            }
        });
        response.setContent(nf);
               
        
        if(lblmode.getTag().equals("edit")){
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("webtaskexp-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("idtask", nset.getData(0).toString()).toJSON());
        }
        
    }
    public void condition2(NikitaResponse response){
            
            if(response.getContent().findComponentbyId("webtaskexp-txtLogic").getText().equals("none")){
                response.getContent().findComponentbyId("webtaskexp-txtParam3").setVisible(false);
                response.getContent().findComponentbyId("webtaskexp-txtExp2").setVisible(false);
                response.getContent().findComponentbyId("webtaskexp-txtParam4").setVisible(false);
            }else {
                response.getContent().findComponentbyId("webtaskexp-txtParam3").setVisible(true);
                response.getContent().findComponentbyId("webtaskexp-txtExp2").setVisible(true);
                response.getContent().findComponentbyId("webtaskexp-txtParam4").setVisible(true);
            }
    }
    
    
}
