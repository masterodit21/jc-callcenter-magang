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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.action.ConnectionAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Combolist;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Vector;

/**
 *
 * @author user
 */
public class webgeneratemaster extends NikitaServlet{
    Nikitaset niki;
    Nikitaset mir;
    NikitaConnection logicx;
    StringBuffer sb = new StringBuffer();
    StringBuffer sb2 = new StringBuffer();
    StringBuffer sb3 = new StringBuffer();
    String xcompid ; 
    String strprimary ; 
    String user ; 
    NikitaConnection nikitaConnection ;
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Master Generator");
        
        Nikitaset nikiset;//Nikitaset nikiset = nikitaConnection.Query("select connname,connname from web_connection ORDER BY connname;");   
        if (NikitaService.isModeCloud()) {
            String user = response.getVirtualString("@+SESSION-LOGON-USER");
            nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname,connname from web_connection WHERE createdby = '"+user+"' OR connname = 'sample'  OR connname = 'default'  ORDER BY connname ;");
        }else{
             nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname,connname from web_connection ORDER BY connname;");
        }     
        HorizontalLayout h = new HorizontalLayout(); 
        
        
        //connection
        Combobox com = new Combobox();
        com.setId("webcrud-txtconn");
        com.setLabel("Connection");
        com.setText(request.getParameter("conn"));
        com.setData( new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "default").setData("text", "Default"))  ); 
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                filltable(response);            
                request.setParameter("conn", response.getContent().findComponentbyId("webcrud-txtconn").getText());             
                response.getContent().findComponentbyId("webcrud-grid").setText("");  
                response.getContent().findComponentbyId("webcrud-griddetail").setText("");  
                response.getContent().findComponentbyId("webcrud-edit").setText("");  
                
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-comtable")); 
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-primary"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-grid"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-griddetail"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-edit"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fields"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsnew"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsedit"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsdetail"));
               
            }
        });         
        nf.addComponent(com);
        request.retainData(com);
        
        
        com = new Combobox();
        com.setId("webcrud-comtable");
        com.setLabel("Table"); 
        com.setText(request.getParameter("tbl"));
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                Component comp = response.getContent().findComponentbyId("webcrud-txtformname");
                String formName = response.getContent().findComponentbyId("webcrud-comtable").getText();
                if(comp.getText().equals("") || !comp.getText().equals(formName)){
                    comp.setText(formName);
                }
                
                NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText());
                Nikitaset ns = nc.QueryPage(1, 1, "SELECT * FROM "+formName,  null );
                response.getContent().findComponentbyId("webcrud-primary").setData(new Nset(ns.getDataAllHeader()).addData("ROWID"));
                 
                response.refreshComponent(comp);
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-primary"));
                
                //fields             
                ns = response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webcrud-comtable").getText())); 
                
                if(response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid")){      
                    response.getContent().findComponentbyId("webcrud-fields").setData(new Nset(ns.getDataAllHeader()));                
                    reorder(response.getContent().findComponentbyId("webcrud-fields"));
                    response.getContent().findComponentbyId("webcrud-fields").setVisible(true);
                }
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fields")); 
                
                
                //fieldsearch
                if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Search")){      
                    response.getContent().findComponentbyId("webcrud-fieldsearch").setData(new Nset(ns.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                    response.getContent().findComponentbyId("webcrud-fieldsearch").setVisible(true);
                }
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){      
                    response.getContent().findComponentbyId("webcrud-fieldsdetail").setData(new Nset(ns.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsdetail"));
                    response.getContent().findComponentbyId("webcrud-fieldsdetail").setVisible(true);
                }
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){      
                    response.getContent().findComponentbyId("webcrud-fieldsnew").setData(new Nset(ns.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsnew"));
                    response.getContent().findComponentbyId("webcrud-fieldsnew").setVisible(true);
                }
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){      
                    response.getContent().findComponentbyId("webcrud-fieldsedit").setData(new Nset(ns.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsedit"));
                    response.getContent().findComponentbyId("webcrud-fieldsedit").setVisible(true);
                }
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsdetail"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsnew"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsedit"));
                
                
            }
        }); 
  
        request.retainData(com);
        nf.addComponent(com);
        
        
        com = new Combobox();
        com.setId("webcrud-primary");
        com.setLabel("Primary"); 
        com.setText(request.getParameter("pk"));        
        nf.addComponent(com);
          
        Checkbox cb = new Checkbox();
        cb.setId("webcrud-grid");
        cb.setLabel(" ");
        cb.setData(Nset.readJSON("[['Grid','Grid']]",true));
//        cb.setText(Nset.readJSON("['Grid']",true ).toJSON());
        nf.addComponent(cb);
        cb.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webcrud-comtable").getText())); 
                if(response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid")){ 
                    response.getContent().findComponentbyId("webcrud-fields").setData(new Nset(nikiset.getDataAllHeader()));                
                    reorder(response.getContent().findComponentbyId("webcrud-fields"));
                    response.getContent().findComponentbyId("webcrud-griddetail").setEnable(true);
                    response.getContent().findComponentbyId("webcrud-fields").setVisible(true);                    
                }else{
                    response.getContent().findComponentbyId("webcrud-griddetail").setText("");                    
                }
                
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-griddetail"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fields"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                
                
            }
        });
        
        cb = new Checkbox();
        cb.setId("webcrud-griddetail");
        cb.setLabel(" ");
        cb.setData(Nset.readsplitString("Search|Delete|Paging|Multi Check"));
        cb.setEnable(false);
//        cb.setText(Nset.readsplitString("Search,Paging,Multi Check", ",").toJSON());
        cb.setStyle(new Style().setStyle("margin-left","3px").setStyle("n-cols", "1")); 
        nf.addComponent(cb);
        cb.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webcrud-comtable").getText())); 
                
                if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Search")){   
                    response.getContent().findComponentbyId("webcrud-fieldsearch").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                    response.getContent().findComponentbyId("webcrud-fieldsearch").setVisible(true);
                }
                
                response.getContent().findComponentbyId("webcrud-griddetail").setEnable(true);
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-griddetail"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsearch"));
                
            }
        });    
        
                
        cb = new Checkbox();
        cb.setId("webcrud-edit");
        cb.setLabel(" ");        
        cb.setData(Nset.readsplitString("New|Edit|Detail"));
//        cb.setData(Nset.readJSON("[['New','New (_child)'],['Edit','Edit (_child)'],['Delete','Delete (_child)'],['Detail','Detail (_child)'] ]",true));
//        cb.setText(Nset.readsplitString("New,Edit,Delete,Detail", ",").toJSON());
//        cb.setData(Nset.readsplitString("Search|Paging|Grid|New|Edit|Delete|Detail"));
//        cb.setText(Nset.readsplitString("Search,Paging,Grid,New,Edit,Delete,Detail", ",").toJSON());
        
        cb.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webcrud-comtable").getText())); 
                
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){ 
                    response.getContent().findComponentbyId("webcrud-fieldsdetail").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsdetail"));
                    response.getContent().findComponentbyId("webcrud-fieldsdetail").setVisible(true);
                }
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){ 
                    response.getContent().findComponentbyId("webcrud-fieldsnew").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsnew"));
                    response.getContent().findComponentbyId("webcrud-fieldsnew").setVisible(true);
                }
                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){ 
                    response.getContent().findComponentbyId("webcrud-fieldsedit").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webcrud-fieldsedit"));
                    response.getContent().findComponentbyId("webcrud-fieldsedit").setVisible(true);
                }
                
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-edit"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsdetail"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsnew"));
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-fieldsedit"));
                
            }
        });    
        cb.setStyle(new Style().setStyle("n-cols", "1"));
//        cb.setStyle(new Style().setStyle("n-cols", "1").setStyle("n-enable", "Detail"));
        
        nf.addComponent(cb);
  
        
        Combolist cl2 = new Combolist();
        cl2.setId("webcrud-fields");
        cl2.setLabel("Fields Grid");
        cl2.setVisible(false);
        nf.addComponent(cl2);
        
        cl2 = new Combolist();
        cl2.setId("webcrud-fieldsearch");
        cl2.setLabel("Fields Search");
        cl2.setVisible(false);
        request.retainData(cl2);
        nf.addComponent(cl2);
        
        cl2 = new Combolist();
        cl2.setId("webcrud-fieldsnew");
        cl2.setLabel("Fields New");
        cl2.setVisible(false);
        request.retainData(cl2);
        nf.addComponent(cl2);
        
        cl2 = new Combolist();
        cl2.setId("webcrud-fieldsedit");
        cl2.setLabel("Fields Edit");
        cl2.setVisible(false);
        request.retainData(cl2);
        nf.addComponent(cl2);
        
        cl2 = new Combolist();
        cl2.setId("webcrud-fieldsdetail");
        cl2.setLabel("Fields Detail");
        cl2.setVisible(false);
        request.retainData(cl2);
        nf.addComponent(cl2);
        
        
        Textbox txt = new Textbox();
        txt.setId("webcrud-txtformname");
        txt.setLabel("Form Name");
        nf.addComponent(txt);
        
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        Button btn = new Button();
        btn.setId("webcrud-btnview");
        btn.setText("View");
        Style style = new Style();
        style.setStyle("width", "100px");
        style.setStyle("height", "34px");
        btn.setStyle(style);
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                
                Nset n =   ConnectionAction.parseNikitaConnection(response,  getNikitaConnection(response));
                request.setParameter("sql",n.getData("query").toString());
                request.setParameter("callz",n.getData("call").toString());
                request.setParameter("conn", n.getData("conn").toString()); 
                request.setParameter("args", n.getData("args").toJSON()); 
                request.setParameter("argsname", n.getData("argsname").toJSON());
                response.showform("webtableview", request,"", true);
            }
        });        
        h.addComponent(btn);
        
        btn = new Button();
        btn.setId("webcrud-btngenerate");
        btn.setText("Generate");
        style = new Style();
        style.setStyle("width", "100px");
        style.setStyle("height", "34px");
        btn.setStyle(style);
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                if(response.getContent().findComponentbyId("webcrud-edit").getText().equals("[]") && 
                        response.getContent().findComponentbyId("webcrud-grid").getText().equals("[]") &&
                        response.getContent().findComponentbyId("webcrud-griddetail").getText().equals("[]")){
                    response.showDialogResult("Warning", "Please select minimum one function", "warning",null, "OK", "");                             
                }else{                    
                    response.refreshComponent("webcrud-btngenerate");
                    response.callWait();
                    response.showDialog("Generate", "Do you want to generate?", "generate", "No", "Yes");   
                }
            }
        });
        h.addComponent(btn);
        nf.addComponent(h);
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {

            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("generate") && responsecode.equals("button2")){
                    logicx = nikitaConnection;
                    strprimary = response.getContent().findComponentbyId("webcrud-primary").getText();
                    
                    //connection databases
                    Component conn = response.getContent().findComponentbyId("webcrud-txtconn");               
                    //form name yang diinput/yg akan dibuat
                    String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText().toLowerCase();                  
                    if (NikitaService.isModeCloud()) {
                        String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                        if (formName.startsWith(prefix)) {                            
                        }else{
                            formName = prefix + formName;
                        }
                    }                            
                            
                    //formindex
                    int index = Utility.getInt( logicx.Query("select MAX(formindex) from web_form ").getText(0, 0) );
                    //formname
                    String xformname = logicx.Query("select formname from web_form WHERE formname = ? ",formName).getText(0, 0) ;
                    String xflag = "";
                    String xformidadd = "uncheck";
                    String xformid="";
                    //kondisi ketika ada yang sama formnamenya
                    if(!xformname.equals(formName)){            
                        //insert form main
                        if(response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid")){
                            niki = logicx.Query("INSERT INTO web_form(formname,formtitle,formtype,formindex,createdby,createddate) "+ 
                                                "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",formName,formName,"webform",(index+1)+"",user);                        
                        }
                        //select formid form main
                        xformid = logicx.Query("select MAX(formid) from web_form ").getText(0, 0) ;
                        if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New") ||
                                response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit") ||
                                response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){
                            //insert form main
                            niki = logicx.Query("INSERT INTO web_form(formname,formtitle,formtype,formstyle,formindex,createdby,createddate) "+ 
                                                    "VALUES(?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                                    (!response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid") ? formName : formName+"_child" ),
                                                    (!response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid") ? formName : formName+"_child" ),
                                                    "webform","width:400;",(index+1)+"",user);                        
                            //select formid form main
                            xformidadd = logicx.Query("select MAX(formid) from web_form ").getText(0, 0) ;
                    }

                        insertcomp(xformid,xformidadd,response);                       
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", formName);
                        data.setData("activitytype", "master");
                        data.setData("mode", "generate");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);
                        
                        response.refreshComponent("webcrud-btngenerate");
                        response.showDialog("Information","Complete!","", "OK"); 
                    }else{
                        response.refreshComponent("webcrud-btngenerate");
                        response.showDialog("Information","Form Name is exsist","", "OK");  
                    }
                                      
                }else if(reqestcode.equals("generate") ){     
                    response.refreshComponent("webcrud-btngenerate");
                }
                
                }
        });
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                
                
                filltable(response);   
            }
        });
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                response.getContent().findComponentbyId("webcrud-txtconn").setText("default");                    
            }
        }); 
        
        nf.setStyle(new Style().setStyle("width", "390").setStyle("height", "500").setStyle("n-maximizable", "false"));
        response.setContent(nf);
    }
    
    public Nset getNikitaConnection(NikitaResponse response){
        Nset exp = Nset.newObject();
                    exp.setData("dbmode", "select");
                    exp.setData("conn", response.getContent().findComponentbyId("webcrud-txtconn").getText());
                    exp.setData("tbl",response.getContent().findComponentbyId("webcrud-comtable").getText());
            return exp;
    }
    
    public void reorder(Component comp){
        Nset cur = Nset.readJSON(comp.getText());
        Nset dat = comp.getData();

        try {
            if ( cur.getArraySize()==0 ){
            }else if(dat.getArraySize()==0 ){      
                comp.setData(cur);
            }else if ( cur.getArraySize()==dat.getArraySize()){
                comp.setData(cur);
            }else{
                for (int i = 0; i < cur.getArraySize(); i++) {
                    if (((Vector)dat.getInternalObject()).toString().contains(cur.getData(i).toString()+",") || ((Vector)dat.getInternalObject()).toString().contains(cur.getData(i).toString()+"]") ) {
                    }else{
                        ((Vector)cur.getInternalObject()).remove(i);
                        i--;
                    }
                }

                for (int i = 0; i < dat.getArraySize(); i++) {
                    if (((Vector)cur.getInternalObject()).toString().contains(dat.getData(i).toString()+",") || ((Vector)cur.getInternalObject()).toString().contains(dat.getData(i).toString()+"]") ) {
                        ((Vector)dat.getInternalObject()).remove(i);
                        i--;                         
                    }                    
                }

                if (cur.getArraySize()>=1 && dat.getArraySize()>=1) {
                    ((Vector) dat.getInternalObject()).addAll(0, (Vector)cur.getInternalObject());
                    comp.setData(dat);
                } 
            }
        } catch (Exception e) { }
              
        
    }
    
    public void filltable(NikitaResponse response) {        
            //NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText());
            NikitaConnection nc  ;
            if (NikitaService.isModeCloud()) {
                String cname = response.getContent().findComponentbyId("webcrud-txtconn").getText();
                String user = response.getVirtualString("@+SESSION-LOGON-USER");
                String prefix = NikitaService.getPrefixUserCloud(response.getConnection(NikitaConnection.LOGIC), user);
                if (cname.startsWith(prefix) || cname.equalsIgnoreCase("sample") || cname.equalsIgnoreCase("default") ) {
                     nc = response.getConnection(cname);
                }else{
                    nc = response.getConnection("");
                }
            }else{
                nc  =  response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText());
            }
            if (nc.getError().equals("")) {
                Nikitaset nikiset =nc.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT table_name,table_name FROM user_tables ");
                }
                response.getContent().findComponentbyId("webcrud-comtable").setData(new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "none").setData("text", "Pilih salah satu")) );
                if(response.getContent().findComponentbyId("webcrud-comtable").getText().equals("")){
                    response.getContent().findComponentbyId("webcrud-comtable").setText("none");
                }
            }
    
    }
    
    //=======================INSERT COMPONENT
    public void insertcomp(String xformid,String xformidadd,NikitaResponse response ) {        
        //============================================FORM MAIN
        //IF ADD/EDIT
        String conn = response.getContent().findComponentbyId("webcrud-txtconn").getText();
        String tbl = response.getContent().findComponentbyId("webcrud-comtable").getText();
        String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText();
        if (NikitaService.isModeCloud()) {
            String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
            if (formName.startsWith(prefix)) {                            
            }else{
                formName = prefix + formName;
            }
        }    
        //select field                    
        mir =  response.getConnection(conn).
                QueryPage(1,1, "SELECT ".concat(" * from ").concat(tbl));
            
        //COMPONENT FORM MAIN
        if(response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid")){ 
            //===============================load
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"load",1+"","navload",1+"",1+"",user);
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"load").getText(0, 0) ;   
            insertroute(xcompid,"load","",response);
            //===============================horizontal
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"horizontal",2+"","horizontallayout",1+"",1+"",user);
            //===============================textsearch
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Search")){            
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,complabel,compstyle,parent,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"textsearch",3+"","text","Searching","n-searchicon:true","$horizontal",1+"",1+"",user);
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"textsearch").getText(0, 0) ; 
                insertroute(xcompid,"textsearch","",response);
            }        
            //===============================imgadd
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){                
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,parent,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"imgadd",4+"","image","img/add.png","$horizontal",1+"",1+"",user);  
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"imgadd").getText(0, 0) ; 
                insertroute(xcompid,"imgadd",xformidadd,response);
            }
            //===============================grid
            if(response.getContent().findComponentbyId("webcrud-grid").getText().contains("Grid")){
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"grid",5+"","tablegrid",0+"",1+"",user);
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"grid").getText(0, 0) ; 
                insertroute(xcompid,"grid",xformidadd,response);
            }
            //===============================result
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"result",6+"","navresult",1+"",1+"",user);
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"result").getText(0, 0) ; 
            insertroute(xcompid,"result","",response);        
            
        }
        
        
        
        //============================================FORM CHILD        
            //===============================loadadd
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"load",1+"","navload",1+"",1+"",user);        
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"load").getText(0, 0) ; 
            insertroute(xcompid,"loadadd","",response);
            Nset n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fields").getText());   
            
            //===============================result            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New") ||
                response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){                
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"result",2+"","navresult",1+"",1+"",user);       
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"result").getText(0, 0) ; 
                insertroute(xcompid,"resultadd","",response);            
            }
            
            //===============================lblhidden
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"lblhidden",3+"","label","@from",0+"",0+"",user);
            //===============================lblid
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"lblid",4+"","label","@"+strprimary,0+"",0+"",user);
        
            Nikitaset niki2;
            int x = 5;
            sb = new StringBuffer();
            for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                niki2 = nikitaConnection.Query("SELECT COLUMN_TYPE FROM information_schema.columns WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ", tbl, mir.getDataAllHeader().get(i));       
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,complabel,enable,visible,createdby,createddate ) "+ 
                          "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", xformidadd,mir.getDataAllHeader().get(i).toLowerCase(),x+(i)+"",(niki2.getText(0, 0).contains("date")?  "datetime" : "text"),mir.getDataAllHeader().get(i),1+"",0+"",user);

            }
            
            int xindex;
            //===============================btnsave
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                xindex = Utility.getInt(logicx.Query("select max(compindex) from web_component WHERE formid = ? ",xformidadd).getText(0, 0)) ;
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"btnsave",(xindex+1) +"","button","Save",1+"",0+"",user);
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"btnsave").getText(0, 0) ; 
                insertroute(xcompid,"btnsave","",response);
            }
            
            //===============================btnupdate
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){
                xindex = Utility.getInt(logicx.Query("select max(compindex) from web_component WHERE formid = ? ",xformidadd).getText(0, 0)) ;
                niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                                      "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformidadd,"btnupdate",(xindex+1) +"","button","Update",1+"",0+"",user);
                xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"btnupdate").getText(0, 0) ; 
                insertroute(xcompid,"btnupdate","",response);
            }            
            
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"load").getText(0, 0) ; 
            insertroute(xcompid,"loadadddetail",xformidadd,response);
    }
    
    public void insertroute(String xcompid,String xcompname,String xformidadd, NikitaResponse response) {
        String conn = response.getContent().findComponentbyId("webcrud-txtconn").getText();
        String tbl = response.getContent().findComponentbyId("webcrud-comtable").getText();
        String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText();
        if (NikitaService.isModeCloud()) {
            String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
            if (formName.startsWith(prefix)) {                            
            }else{
                formName = prefix + formName;
            }
        }    
        //===============================select field                    
        mir =  response.getConnection(conn).
                QueryPage(1,1, "SELECT ".concat(" * from ").concat(tbl));

        String allfield;
        String allfield2;
        String allfield3;
        String ID = mir.getDataAllHeader().get(0);
        String field2 = mir.getDataAllHeader().get(1);        
                    
        String exp;        
        exp = "{\"result\":\"\",\"args\":{},\"code\":\"\",\"class\":\"TrueExpression\",\"id\":\"105\"}";
        String act;
        
        Nset n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fields").getText());   
        Nset ndetail = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsdetail").getText());   
        Nset nnew = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsnew").getText());   
        Nset nedit = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsedit").getText());   
        
        //===============================load
        if(xcompname.equals("load")){            
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Search")){
                act = "{\"args\":{\"param5\":\"\",\"param4\":\"\",\"param3\":\"%\",\"param2\":\"$textsearch\",\"param1\":\"%\",\"param9\":\"\","
                        + "\"param8\":\"\",\"param7\":\"\",\"result\":\"@searchs\",\"param6\":\"\"},\"code\":\"concat\",\"class\":\"StringManipulationAction\","
                        + "\"id\":\"1\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);
            }

            sb = new StringBuffer();
            for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                sb.append("\\\\\\\"").append(mir.getDataAllHeader().get(i)).append("\\\\\\\"");
                if(mir.getDataAllHeader().size() != i){
                    sb.append(",");
                }
            }         
            allfield = sb.toString();
            
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Search")){
            Nset n2 = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsearch").getText());    
                sb2 = new StringBuffer();
                sb3 = new StringBuffer();
                if(n2.getArraySize() <= 0){
                    for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                        sb2.append(mir.getDataAllHeader().get(i)).append(" like ? ");
                        sb3.append("\\\"").append(i).append("\\\":\\\"@searchs\\\"");
                        if(mir.getDataAllHeader().size()-1 != i){
                            sb2.append("OR ");
                            sb3.append(",");
                        }
                    }
                }else{
                    for (int i = 0; i < n2.getArraySize(); i++) {
                        sb2.append(n2.getData(i).toString()).append(" like ? ");
                        sb3.append("\\\"").append(i).append("\\\":\\\"@searchs\\\"");
                        if(n2.getArraySize()-1 != i){
                            sb2.append("OR ");
                            sb3.append(",");
                        }
                    }
                }
                allfield2 = sb2.toString();
                allfield3 = sb3.toString();
                act = "{\"args\":{\"result\":\"@tbl\",\"param4\":"+(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Paging") ? "\"$grid\"":"\"\"")+",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                    + "\\\"argswhere\\\":{"+allfield3+"},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":"
                    + "{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"2\\\","
                    + "\\\"sqlwhere\\\":\\\"" +allfield2+" ORDER BY "+field2+" ASC \\\",\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\","
                    + "\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\",\\\"fields\\\":\\\"["+allfield+"]\\\",\\\"args\\\":{},"
                    + "\\\"dbmode\\\":\\\"select\\\","
                    + "\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",act,user);

            }else{
                act = "{\"args\":{\"result\":\"@tbl\",\"param4\":"+(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Paging") ? "\"$grid\"":"\"\"")+",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"0\\\",\\\"sqlwhere\\\":\\\" \\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\",\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"["+allfield+"]\\\",\\\"args\\\":{},\\\"dbmode\\\":\\\"select\\\",\\\"sql\\\":\\\"\\\"}\","
                        + "\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                    "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",exp,act,user);
            }
            
            String delete = ""; String edit = "";String view = "";String grid = "[#]";
            
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Delete")){
                delete = "[remove]";
            }if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){
                edit = "[edit]";
            }if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){
                view = "[view]";
            }
                       
            n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fields").getText());  
            sb = new StringBuffer();
            String comp;
            if(n.getArraySize() <= 0){
                for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                    comp=mir.getDataAllHeader().get(i);
                    sb.append(comp).append(",");
                }
            }else{
                for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                    comp="";
                    for (int j = 0; j < n.getArraySize(); j++) {
                        if(mir.getDataAllHeader().get(i).equals(n.getData(j).toString()))
                            comp=mir.getDataAllHeader().get(i);

                    }                     
                        if(comp.equals(""))
                            sb.append(",");
                        else
                            sb.append(comp).append(",");
                }
            }
                    
            allfield = sb.toString();
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Multi Check")){
                grid = "[v]";
            }
            act = "{\"args\":{\"param4\":\""+grid+allfield+view+edit+delete+"\",\"param3\":\"\",\"param2\":\"@tbl\",\"param1\":\"$grid\"},"
                    + "\"code\":\"inflate\",\"class\":\"FormAction\",\"id\":\"19\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",act,user);

            act = "{\"args\":{\"result\":\"\",\"param6\":\"@tbl\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"@tbl\"},\"code\":\"nikitaset\","
                    + "\"class\":\"DataAction\",\"id\":\"11\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",act,user);

            act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"@tbl\",\"param1\":\"write\"},\"code\":\"\",\"class\":\"JsonAction\",\"id\":\"26\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",act,user);

            act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"$grid\",\"param1\":\"settag\"},\"code\":\"component\","
                    + "\"class\":\"ComponentAction\",\"id\":\"25\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",act,user);
        }else if(xcompname.equals("result")){            
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",exp,act,user);
            
            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Delete")){
                act = "{\"args\":{\"param6\":\"\",\"param5\":\"@data\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"@+RESULT\","
                        + "\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);       

                exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"remove\",\"paramb\":\"AND\",\"param3\":\"@+REQUESTCODE\","
                        + "\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\",\"param6\":\"\",\"paramd\":\"\"},"
                        + "\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"@data\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"1\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\""+strprimary+"\\\",\\\"parameter1\\\":\\\""+strprimary+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{},\\\"dbmode\\\":\\\"delete\\\",\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},"
                        + "\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",exp,act,user); 
            }
        }else if(xcompname.equals("textsearch")){            
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);  
            
        }else if(xcompname.equals("imgadd")){                
            act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"modal\",\"param1\":\"{\\\"args\\\":{\\\"from\\\":\\\"\\\"},"
                    + "\\\"formid\\\":\\\""+xformidadd+"\\\",\\\"formname\\\":\\\""+formName+"_child\\\"}\"},\"code\":\"showform\","
                    + "\"class\":\"FormAction\",\"id\":\"3\"}";
            
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user); 
            
        }else if(xcompname.equals("grid")){            
            act = "{\"args\":{\"param3\":\"@datagrid\",\"param2\":\"$grid[tag]\",\"param1\":\"read\"},\"code\":\"\",\"class\":\"JsonAction\","
                    + "\"id\":\"26\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",exp,act,user);  
            
            exp = "{\"result\":\"\",\"args\":{},\"code\":\"filter\",\"class\":\"BooleanExpression\",\"id\":\"116\"}";
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);
            
            act = "{\"args\":{},\"code\":\"break\",\"class\":\"SystemAction\",\"id\":\"24\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",act,user);
            
            
            act = "{\"args\":{\"param6\":\"\",\"param5\":\"\",\"param4\":\"@datagrid\",\"param3\":\"@+SELECTEDROW\",\"param2\":\"@datagrid\","
                    + "\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",act,user);
            int key=0;
            for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                if(strprimary.equals(mir.getDataAllHeader().get(i)))
                    key=i;
            }
            act = "{\"args\":{\"param6\":\"\",\"param5\":\"@"+strprimary+"\",\"param4\":\"\",\"param3\":\""+key+"\","
                    + "\"param2\":\"@datagrid\",\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",act,user);                        
                         
            act = "{\"args\":{\"param4\":\"@"+strprimary+"\",\"param3\":\"0\",\"param2\":\"\",\"param1\":\"@idz\"},\"code\":\"new\",\"class\":\"VariableAction\",\"id\":\"29\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,6+"",act,user);   

            if(response.getContent().findComponentbyId("webcrud-griddetail").getText().contains("Delete")){  
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"remove\",\"param1\":\"@+BUTTONGRID\"},\"code\":\"equal\","
                        + "\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"reqcode1\":\"@+BUTTONGRID\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to delete?\","
                        + "\"data\":\"@idz\",\"title\":\"Delete\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
                
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,7+"",exp,act,user);
            }
            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){          
                sb = new StringBuffer(); 
                sb.append("\\\"").append(strprimary).append("\\\":\\\"@").append(strprimary).append("\\\",");
                allfield = sb.toString();
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"@+BUTTONGRID\"},"
                        + "\"code\":\"equal\",\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"modal\",\"param1\":\"{\\\"args\\\":{"+allfield+"\\\"from\\\":\\\"edit\\\"},"
                        + "\\\"formid\\\":\\\""+xformidadd+"\\\",\\\"formname\\\":\\\""+formName+"_child\\\"}\"},\"code\":\"showform\","
                        + "\"class\":\"FormAction\",\"id\":\"3\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,8+"",exp,act,user);
            }
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){                         
                sb = new StringBuffer(); 
                sb.append("\\\"").append(strprimary).append("\\\":\\\"@").append(strprimary).append("\\\",");
                allfield = sb.toString();
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"view\",\"param1\":\"@+BUTTONGRID\"},"
                        + "\"code\":\"equal\",\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"modal\",\"param1\":\"{\\\"args\\\":{"+allfield+"\\\"from\\\":\\\"view\\\"},"
                        + "\\\"formid\\\":\\\""+xformidadd+"\\\",\\\"formname\\\":\\\""+formName+"_child\\\"}\"},\"code\":\"showform\","
                        + "\"class\":\"FormAction\",\"id\":\"3\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,9+"",exp,act,user);
            }
            
        }else if(xcompname.equals("loadadd")){               
        }else if(xcompname.equals("loadadddetail")){               
            String visible="";
            String comp="";
            int xindex;
            Nikitaset niki2;
            
            act = "{\"args\":{\"result\":\"@data\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                    + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                    + "\\\"parameter1\\\":\\\"$lblid\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"1\\\",\\\"sqlwhere\\\":\\\"\\\","
                    + "\\\"param\\\":{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\""+strprimary+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                    + "\\\"fields\\\":\\\"null\\\",\\\"orderby\\\":{\\\"customs\\\":\\\"\\\",\\\"conditionorders\\\":\\\"0\\\","
                    + "\\\"orderbys\\\":\\\"null\\\"},\\\"fhide\\\":\\\"[\\\\\\\"userid\\\\\\\",\\\\\\\"username\\\\\\\","
                    + "\\\\\\\"name\\\\\\\",\\\\\\\"type\\\\\\\",\\\\\\\"createdby\\\\\\\",\\\\\\\"createddate\\\\\\\","
                    + "\\\\\\\"modifiedby\\\\\\\",\\\\\\\"modifieddate\\\\\\\"]\\\",\\\"args\\\":{},\\\"callz\\\":\\\"\\\","
                    + "\\\"dbmode\\\":\\\"select\\\",\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},\"code\":\"query\","
                    + "\"class\":\"ConnectionAction\",\"id\":\"17\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user); 
                
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){                
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"$lblhidden\"},\"code\":\"equal\","
                        + "\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param2\":\"Update\",\"param1\":\"$btnsave\"},\"code\":\"settext\",\"class\":\"ComponentAction\",\"id\":\"2\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",exp,act,user); 
                
                act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$btnupdate\"},"
                            + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                    niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                          "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",act,user);   
                int x=4;
                if(nedit.getArraySize() <= 0){
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {  
                        niki2 = nikitaConnection.Query("SELECT COLUMN_TYPE FROM information_schema.columns WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ", tbl, mir.getDataAllHeader().get(i));       
                        if(niki2.getText(0, 0).contains("date")){
                            act = "{\"args\":{\"result\":\"!"+mir.getDataAllHeader().get(i)+"\",\"format\":\"1\",\"param\":\"!"+mir.getDataAllHeader().get(i)+"\"},\"code\":\"\",\"class\":\"DateFormatAction\",\"id\":\"40\"}";
                            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                            x++;
                        }
                        act = "{\"args\":{\"param2\":\"!"+mir.getDataAllHeader().get(i)+"\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    } 
                }else{
                    for (int i = 0; i < nedit.getArraySize() ; i++) { 
                        niki2 = nikitaConnection.Query("SELECT COLUMN_TYPE FROM information_schema.columns WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ", tbl, nedit.getData(i).toString());       
                        if(niki2.getText(0, 0).contains("date")){
                            act = "{\"args\":{\"result\":\"!"+nedit.getData(i).toString()+"\",\"format\":\"1\",\"param\":\"!"+nedit.getData(i).toString()+"\"},\"code\":\"\",\"class\":\"DateFormatAction\",\"id\":\"40\"}";
                            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                            x++;
                        }
                        
                        act = "{\"args\":{\"param2\":\"!"+nedit.getData(i).toString()+"\",\"param1\":\"$"+nedit.getData(i).toString().toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < nedit.getArraySize() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+nedit.getData(i).toString().toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    }
                }           
            }
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"\",\"param1\":\"$lblhidden\"},\"code\":\"equal\","
                        + "\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(xindex+1)+"",exp,user);
                
                act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$btnsave\"},"
                        + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(xindex+2)+"",act,user);   
                
                int x=xindex+3;
                if(nnew.getArraySize() <= 0){
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {                        
                        act = "{\"args\":{\"param2\":\"!"+mir.getDataAllHeader().get(i)+"\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    } 
                }else{
                    for (int i = 0; i < nnew.getArraySize() ; i++) {                        
                        act = "{\"args\":{\"param2\":\"!"+nnew.getData(i).toString()+"\",\"param1\":\"$"+nnew.getData(i).toString().toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < nnew.getArraySize() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+nnew.getData(i).toString().toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    }
                }
            }
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Detail")){                
                xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                exp = "{\"result\":\"\",\"flag\":\"\",\"args\":{\"param2\":\"view\",\"param1\":\"$lblhidden\"},"
                        + "\"code\":\"equal\",\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param2\":\"false\",\"param1\":\"$btnsave\"},\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(xindex+1)+"",exp,act,user);
                
                int x=xindex+2;
                if(ndetail.getArraySize() <= 0){
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {                        
                        niki2 = nikitaConnection.Query("SELECT COLUMN_TYPE FROM information_schema.columns WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ", tbl, mir.getDataAllHeader().get(i));       
                        if(niki2.getText(0, 0).contains("date")){
                            act = "{\"args\":{\"result\":\"!"+mir.getDataAllHeader().get(i)+"\",\"format\":\"1\",\"param\":\"!"+mir.getDataAllHeader().get(i)+"\"},\"code\":\"\",\"class\":\"DateFormatAction\",\"id\":\"40\"}";
                            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                            x++;
                        }
                        
                        act = "{\"args\":{\"param2\":\"!"+mir.getDataAllHeader().get(i)+"\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < mir.getDataAllHeader().size() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+mir.getDataAllHeader().get(i).toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    } 
                }else{
                    for (int i = 0; i < ndetail.getArraySize() ; i++) {  
                        niki2 = nikitaConnection.Query("SELECT COLUMN_TYPE FROM information_schema.columns WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ", tbl, ndetail.getData(i).toString());       
                        if(niki2.getText(0, 0).contains("date")){
                            act = "{\"args\":{\"result\":\"!"+ndetail.getData(i).toString()+"\",\"format\":\"1\",\"param\":\"!"+ndetail.getData(i).toString()+"\"},\"code\":\"\",\"class\":\"DateFormatAction\",\"id\":\"40\"}";
                            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                            x++;
                        }
                        
                        act = "{\"args\":{\"param2\":\"!"+ndetail.getData(i).toString()+"\",\"param1\":\"$"+ndetail.getData(i).toString().toLowerCase()+"\"},\"code\":\"settext\","
                                + "\"class\":\"ComponentAction\",\"id\":\"2\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user);
                    } 
                    xindex = Utility.getInt(logicx.Query("SELECT MAX(routeindex) FROM web_route WHERE compid = ? ",xcompid).getText(0, 0)) ;
                    x=xindex+1;
                    for (int i = 0; i < ndetail.getArraySize() ; i++) {                                            
                        act = "{\"args\":{\"param2\":\"true\",\"param1\":\"$"+ndetail.getData(i).toString().toLowerCase()+"\"},"
                                + "\"code\":\"setvisible\",\"class\":\"ComponentAction\",\"id\":\"6\"}";
                        niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                              "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,(x+i)+"",act,user); 
                    }
                }
            }
        }else if(xcompname.equals("btnsave")){ 
            exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"$lblhidden\"},\"code\":\"notequal\","
                    + "\"class\":\"StringComparationExpression\",\"id\":\"101\"}";
            act = "{\"args\":{\"reqcode1\":\"add\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to save?\","
                    + "\"data\":\"\",\"title\":\"Save\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);
        }else if(xcompname.equals("btnupdate")){ 
            exp = "{\"result\":\"\",\"args\":{},\"code\":\"else\",\"class\":\"BooleanExpression\",\"id\":\"113\"}";
            act = "{\"args\":{\"reqcode1\":\"update\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to update?\","
                    + "\"data\":\"\",\"title\":\"Update\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);
        }else if(xcompname.equals("resultadd")){  
            //add  
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsnew").getText());  
                if(n.getArraySize() <= 0 ){
                    sb = new StringBuffer();
                    for (int i = 0; i < mir.getDataAllHeader().size(); i++) {           
                        sb.append("\\\"").append(mir.getDataAllHeader().get(i)).append("\\\":\\\"$").append(mir.getDataAllHeader().get(i).toLowerCase()).append("\\\"");
                        if(mir.getDataAllHeader().size()-1 != i){
                            sb.append(",");
                        }
                    }
                }else{                
                    sb = new StringBuffer();
                    for (int i = 0; i < n.getArraySize(); i++) {
                            sb.append("\\\"").append(n.getData(i).toString()).append("\\\":\\\"$").append(n.getData(i).toString().toLowerCase()).append("\\\"");
                            if(n.getArraySize()-1 != i){
                                sb.append(",");
                            }
                    }  
                }

                allfield = sb.toString();
                exp = "{\"result\":\"@add\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"add\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+REQUESTCODE\",\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"@R\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"0\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{"+allfield+"},\\\"dbmode\\\":\\\"insert\\\",\\\"sql\\\":\\\"\\\"}\","
                        + "\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);

                exp = "{\"result\":\"@flagerr\",\"args\":{\"param2\":\"Incorrect integer value\",\"param1\":\"@R(error)\"},"
                        + "\"code\":\"startwith\",\"class\":\"StringComparationExpression\",\"id\":\"4\"}";
                act = "{\"args\":{\"reqcode1\":\"\",\"button2\":\"\",\"button1\":\"OK\",\"message\":\"Please input integer value\","
                        + "\"data\":\"\",\"title\":\"Warning\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";

                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",exp,act,user);

                exp = "{\"result\":\"\",\"flag\":\"\",\"args\":{\"param2\":\"Incorrect datetime value\",\"param1\":\"@R(error)\"},"
                        + "\"code\":\"contain\",\"class\":\"StringComparationExpression\",\"id\":\"3\"}";
                act = "{\"args\":{\"reqcode1\":\"\",\"button2\":\"\",\"button1\":\"OK\",\"message\":\"Please input all datetime value\","
                        + "\"data\":\"\",\"title\":\"Warning\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";

                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",exp,act,user);

                exp = "{\"result\":\"@x\",\"args\":{\"param5\":\"@+RESPONSECODE\",\"paramc\":\"\",\"param4\":\"add\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+REQUESTCODE\",\"parama\":\"\",\"param2\":\"false\",\"param1\":\"@flagerr\",\"param6\":\"button2\","
                        + "\"paramd\":\"AND\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param2\":\"\",\"param1\":\"add\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",exp,act,user);

                exp="{\"result\":\"\",\"flag\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"\",\"paramb\":\"AND\","
                        + "\"param3\":\"@R(error)\",\"parama\":\"\",\"param2\":\"true\",\"param1\":\"@x\",\"param6\":\"\","
                        + "\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",exp,act,user);
                
            }
            //update
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit")){
                n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-fieldsedit").getText());  
                if(n.getArraySize() <= 0 ){
                    sb = new StringBuffer();
                    for (int i = 0; i < mir.getDataAllHeader().size(); i++) {           
                        sb.append("\\\"").append(mir.getDataAllHeader().get(i)).append("\\\":\\\"$").append(mir.getDataAllHeader().get(i).toLowerCase()).append("\\\"");
                        if(mir.getDataAllHeader().size()-1 != i){
                            sb.append(",");
                        }
                    }
                }else{                
                    sb = new StringBuffer();
                    for (int i = 0; i < n.getArraySize(); i++) {
                        sb.append("\\\"").append(n.getData(i).toString()).append("\\\":\\\"$").append(n.getData(i).toString().toLowerCase()).append("\\\"");
                        if(n.getArraySize()-1 != i){
                            sb.append(",");
                        }
                    }  
                }

                allfield = sb.toString();
                exp = "{\"result\":\"@update\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"update\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+REQUESTCODE\",\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"@R2\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"$lblid\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"1\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\""+strprimary+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{"+allfield+"},\\\"dbmode\\\":\\\"update\\\",\\\"sql\\\":\\\"\\\"}\","
                        + "\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,6+"",exp,act,user);

                exp = "{\"result\":\"@flagerr\",\"args\":{\"param2\":\"Incorrect integer value\",\"param1\":\"@R2(error)\"},"
                        + "\"code\":\"startwith\",\"class\":\"StringComparationExpression\",\"id\":\"4\"}";
                act = "{\"args\":{\"reqcode1\":\"\",\"button2\":\"\",\"button1\":\"OK\",\"message\":\"Please input integer value\","
                         + "\"data\":\"\",\"title\":\"Warning\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,7+"",exp,act,user);

                exp = "{\"result\":\"\",\"flag\":\"\",\"args\":{\"param2\":\"Incorrect datetime value\",\"param1\":\"@R2(error)\"},"
                        + "\"code\":\"contain\",\"class\":\"StringComparationExpression\",\"id\":\"3\"}";
                act = "{\"args\":{\"reqcode1\":\"\",\"button2\":\"\",\"button1\":\"OK\",\"message\":\"Please input all datetime value\","
                        + "\"data\":\"\",\"title\":\"Warning\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";

                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,8+"",exp,act,user);


                exp = "{\"result\":\"@y\",\"args\":{\"param5\":\"@+REQUESTCODE\",\"paramc\":\"\",\"param4\":\"button2\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+RESPONSECODE\",\"parama\":\"\",\"param2\":\"false\",\"param1\":\"@flagerr\",\"param6\":\"update\","
                        + "\"paramd\":\"AND\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param2\":\"\",\"param1\":\"update\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,9+"",exp,act,user);

                exp="{\"result\":\"\",\"flag\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"\",\"paramb\":\"AND\","
                        + "\"param3\":\"@R(error)\",\"parama\":\"\",\"param2\":\"true\",\"param1\":\"@y\",\"param6\":\"\","
                        + "\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,10+"",exp,act,user);
               
            }

            //orr            
            exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"<>\",\"param4\":\"true\",\"paramb\":\"OR\","
                    + "\"param3\":\"@update\",\"parama\":\"<>\",\"param2\":\"true\",\"param1\":\"@add\",\"param6\":\"\","
                    + "\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,11+"",exp,user);
        }
        
    }
    
    
}
