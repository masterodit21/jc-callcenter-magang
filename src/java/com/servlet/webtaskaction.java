/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
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
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
 

/**
 *
 * @author user
 */
public class webtaskaction extends NikitaServlet{
    
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
        
        final Label lblmode = new Label();
        lblmode.setId("webexpression-mode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setText(request.getParameter("data"));  
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "400");
        nf.setText("Task Action");
        nf.setStyle(style);
        
        //VERTICAL LAYOUT
        VerticalLayout vModeConn = new VerticalLayout();
        vModeConn.setVisible(false);
        vModeConn.setId("vModeConn");
        VerticalLayout vModeHttp = new VerticalLayout();
        vModeHttp.setVisible(false);
        vModeHttp.setId("vModeHttp");
        VerticalLayout vModeNtask = new VerticalLayout();
        vModeNtask.setVisible(false);
        vModeNtask.setId("vModeNtask");
        
        
        Combobox comb = new Combobox();
        comb.setData(Nset.readJSON("[{'id':'connection','text':'Connection'},{'id':'transaction','text':'Transaction'},{'id':'http','text':'Http Connection'},{'id':'ntask','text':'Nikita task'},{'id':'break','text':'Break Task'},{'id':'deactive','text':'Deactive Scheduller'}]", true));
        comb.setId("webtaskact-cmbMode");
        comb.setText("connection");
        comb.setLabel("Mode");
        comb.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.getContent().findComponentbyId("webtaskact-txtConnection").setText("");
                response.getContent().findComponentbyId("webtaskact-txtQuery").setText("");
                response.getContent().findComponentbyId("webtaskact-txtUrl").setText("");
                response.getContent().findComponentbyId("webtaskact-txtNtask").setText("");
                response.getContent().findComponentbyId("webtaskact-areaArgs").setText("");
                response.getContent().findComponentbyId("webtaskact-areaNote").setText("");
                response.getContent().findComponentbyId("webtaskact-txtResp").setText("");
                response.getContent().findComponentbyId("webtaskact-txtResult").setText("");
                response.getContent().findComponentbyId("webtaskact-txtError").setText("");
                condition2(response);
                response.writeContent();
            }
        });
        nf.addComponent(comb);
        
        Textbox txt = new Textbox();
        txt.setId("webtaskact-txtConnection");
        txt.setLabel("Connection");        
        vModeConn.addComponent(txt);
        
        txt = new Textsmart();
        txt.setId("webtaskact-txtQuery");
        txt.setLabel("Query");        
        txt.setVisible(false);  
        vModeConn.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webtaskact-txtUrl");
        txt.setLabel("URL");        
        vModeHttp.addComponent(txt);
        
        Textsmart text = new Textsmart();
        text.setId("webtaskact-txtNtask");
        text.setLabel("Nikita Task");  
        text.setOnClickListener(new Component.OnClickListener() {
          public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", response.getContent().findComponentbyId("webtaskact-txtNtask").getText());
                request.setParameter("code", "selected");                 
                request.setParameter("type", "link");
                response.showform("webform", request,"link",true);     
//                response.showformGen("webform", request, "", false, "link");          
                response.write();
           }
        });
        vModeNtask.addComponent(text);
        
        Textarea area = new Textarea();
        area.setId("webtaskact-areaArgs");
        area.setLabel("Args");        
        vModeHttp.addComponent(area);  
        
        area = new Textarea();
        area.setId("webtaskact-areaArgs2");
        area.setLabel("Args");        
        vModeNtask.addComponent(area);
        
        area = new Textarea();
        area.setId("webtaskact-areaQuery");
        area.setLabel("Query");  
        area.setVisible(false);
        vModeConn.addComponent(area);
        
        area = new Textarea();
        area.setId("webtaskact-areaNote");
        area.setVisible(false);
        area.setLabel("Note");  
        nf.addComponent(area);
        
        txt = new Textbox();
        txt.setId("webtaskact-txtResp");
        txt.setLabel("Resp. Code");        
        vModeHttp.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webtaskact-txtResult");
        txt.setLabel("Result");        
        vModeConn.addComponent(txt);
                
        txt = new Textbox();
        txt.setId("webtaskact-txtResult2");
        txt.setLabel("Result");        
        vModeHttp.addComponent(txt);   
        
        txt = new Textbox();
        txt.setId("webtaskact-txtResult3");
        txt.setLabel("Result");     
        vModeNtask.addComponent(txt);        
        
        txt = new Textbox();
        txt.setId("webtaskact-txtError");
        txt.setLabel("Error");        
        vModeConn.addComponent(txt);
        
        nf.addComponent(vModeConn);
        nf.addComponent(vModeHttp);
        nf.addComponent(vModeNtask);
        
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
                
        
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
                exp.setData("conn", response.getContent().findComponentbyId("webtaskact-txtConnection").getText());
                exp.setData("url", response.getContent().findComponentbyId("webtaskact-txtUrl").getText());
                exp.setData("ntask", response.getContent().findComponentbyId("webtaskact-txtNtask").getText());
                exp.setData("resultz", response.getContent().findComponentbyId("webtaskact-txtResult").getText());
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("http")){
                    exp.setData("args", response.getContent().findComponentbyId("webtaskact-areaArgs").getText());
                    exp.setData("resultz", response.getContent().findComponentbyId("webtaskact-txtResult2").getText());
                }
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("ntask")){
                    exp.setData("args", response.getContent().findComponentbyId("webtaskact-areaArgs2").getText());
                    exp.setData("resultz", response.getContent().findComponentbyId("webtaskact-txtResult3").getText());
                }
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("transaction")){
                    exp.setData("query", response.getContent().findComponentbyId("webtaskact-areaQuery").getText());
                }
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("connection")){
                    exp.setData("query", response.getContent().findComponentbyId("webtaskact-txtQuery").getText());
                }
                exp.setData("note", response.getContent().findComponentbyId("webtaskact-areaNote").getText());
                exp.setData("respcodez", response.getContent().findComponentbyId("webtaskact-txtResp").getText());
                exp.setData("errz", response.getContent().findComponentbyId("webtaskact-txtError").getText());
                exp.setData("tmode", response.getContent().findComponentbyId("webtaskact-cmbMode").getText());
                
                
                response.showDialogResult("Update",  "Do you want to update?", "update", Nset.newObject().setData("taskid", id.getData("taskid").toString() ).setData("action", exp.toJSON()), "No", "Yes");                  
                
                response.write();
            
            }
        });
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {             
                Nset n = Nset.readJSON(  lblmode.getText());
                Nset exp = Nset.readJSON( n.getData("action").toString() );
                
                response.getContent().findComponentbyId("webtaskact-cmbMode").setText(exp.getData("tmode").toString());
                condition2(response);                
                response.getContent().findComponentbyId("webtaskact-txtConnection").setText(exp.getData("conn").toString());
                
                response.getContent().findComponentbyId("webtaskact-txtUrl").setText(exp.getData("url").toString());
                response.getContent().findComponentbyId("webtaskact-txtNtask").setText(exp.getData("ntask").toString());
                response.getContent().findComponentbyId("webtaskact-areaNote").setText(exp.getData("note").toString());
                response.getContent().findComponentbyId("webtaskact-txtResp").setText(exp.getData("respcodez").toString());
                response.getContent().findComponentbyId("webtaskact-txtResult").setText(exp.getData("resultz").toString());
                
                if(exp.getData("tmode").toString().equals("http")){
                    response.getContent().findComponentbyId("webtaskact-areaArgs").setText(exp.getData("args").toString());
                    response.getContent().findComponentbyId("webtaskact-txtResult2").setText(exp.getData("resultz").toString());
                }
                if(exp.getData("tmode").toString().equals("ntask")){
                    response.getContent().findComponentbyId("webtaskact-areaArgs2").setText(exp.getData("args").toString());
                    response.getContent().findComponentbyId("webtaskact-txtResult3").setText(exp.getData("resultz").toString());
                }
                
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("transaction")){
                    response.getContent().findComponentbyId("webtaskact-areaQuery").setText(exp.getData("query").toString());
                }
                if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("connection")){
                    response.getContent().findComponentbyId("webtaskact-txtQuery").setText(exp.getData("query").toString());
                }
                
                response.getContent().findComponentbyId("webtaskact-txtError").setText(exp.getData("errz").toString());
                
            }
        });
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("link") && responsecode.equals("OK")){                    
                    response.getContent().findComponentbyId("webtaskact-txtNtask").setTag(result.getData("id").toString());
                    response.getContent().findComponentbyId("webtaskact-txtNtask").setText(result.getData("name").toString());
                    condition2(response);
                    response.writeContent();
                }
                if(responsecode.equals("webdatabase")){
                    response.getContent().findComponentbyId("webtaskact-txtQuery").setText(result.getData("variable").toString());
                    condition2(response);
                    response.writeContent();
                }
                if(lblmode.getTag().equals("edit")){
                    
                    
                    if(reqestcode.equals("update") && responsecode.equals("button2")){
                        
                        Nikitaset nikitaset = nikitaConnection.Query("UPDATE web_scheduller_task SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",action=?"+
                                                                     "WHERE taskid=?",                                                                 
                                              user,result.getData("action").toString(),  result.getData("taskid").toString());
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
            
            if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("connection") ||
                response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("transaction")){
                
                response.getContent().findComponentbyId("vModeConn").setVisible(true);
                response.getContent().findComponentbyId("vModeHttp").setVisible(false);
                response.getContent().findComponentbyId("vModeNtask").setVisible(false);                
                response.getContent().findComponentbyId("webtaskact-areaNote").setVisible(false);
            }else if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("http")){
                response.getContent().findComponentbyId("vModeConn").setVisible(false);
                response.getContent().findComponentbyId("vModeHttp").setVisible(true);
                response.getContent().findComponentbyId("vModeNtask").setVisible(false); 
                response.getContent().findComponentbyId("webtaskact-areaNote").setVisible(false);              
            }else if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("ntask")){
                response.getContent().findComponentbyId("vModeConn").setVisible(false);
                response.getContent().findComponentbyId("vModeHttp").setVisible(false);
                response.getContent().findComponentbyId("vModeNtask").setVisible(true);     
                response.getContent().findComponentbyId("webtaskact-areaNote").setVisible(false);        
            }else if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("break") ||
                response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("deactive")){
                
                response.getContent().findComponentbyId("vModeConn").setVisible(false);
                response.getContent().findComponentbyId("vModeHttp").setVisible(false);
                response.getContent().findComponentbyId("vModeNtask").setVisible(false);
                response.getContent().findComponentbyId("webtaskact-areaNote").setVisible(true);
            }else {
                response.getContent().findComponentbyId("vModeConn").setVisible(true);
                response.getContent().findComponentbyId("vModeHttp").setVisible(false);
                response.getContent().findComponentbyId("vModeNtask").setVisible(false);
                response.getContent().findComponentbyId("webtaskact-areaNote").setVisible(false);
                
            }
            
            if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("connection")){
                response.getContent().findComponentbyId("webtaskact-txtQuery").setVisible(true);
                response.getContent().findComponentbyId("webtaskact-areaQuery").setVisible(false);
            }
            if(response.getContent().findComponentbyId("webtaskact-cmbMode").getText().equals("transaction")){
                response.getContent().findComponentbyId("webtaskact-txtQuery").setVisible(false);
                response.getContent().findComponentbyId("webtaskact-areaQuery").setVisible(true);
            }
            
            
    }
    
    
}
