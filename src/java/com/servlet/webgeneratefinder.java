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
import com.nikita.generator.action.ConnectionAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Combolist;
import com.nikita.generator.ui.Textbox;
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
public class webgeneratefinder extends NikitaServlet{
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
        logicx =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);     
        HorizontalLayout h = new HorizontalLayout(); 
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Master Finder");
        
         Nikitaset nikiset;//Nikitaset nikiset = nikitaConnection.Query("select connname,connname from web_connection ORDER BY connname;");
        if (NikitaService.isModeCloud()) {
            String user = response.getVirtualString("@+SESSION-LOGON-USER");
            nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname,connname from web_connection WHERE createdby = '"+user+"' OR connname = 'sample'  OR connname = 'default'  ORDER BY connname ;");
        }else{
             nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname,connname from web_connection ORDER BY connname;");
        }
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout hl = new HorizontalLayout();
        HorizontalLayout h2 = new HorizontalLayout();
        
        
        //connection
        Combobox com = new Combobox();
        com.setId("webfinder-txtconn");
        com.setLabel("Connection");
        com.setText(request.getParameter("conn"));
        com.setData( new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "default").setData("text", "Default"))  ); 
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                filltable(response);            
                request.setParameter("conn", response.getContent().findComponentbyId("webfinder-txtconn").getText());
                
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-comtable"));
            }
        });        
        request.retainData(com);
        nf.addComponent(com);
        
        com = new Combobox();
        com.setId("webfinder-comtable");
        com.setLabel("Table"); 
        com.setText(request.getParameter("tbl"));
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                Component comp = response.getContent().findComponentbyId("webfinder-txtformname");
                String formName = response.getContent().findComponentbyId("webfinder-comtable").getText();
                 if(comp.getText().equals("") || !comp.getText().equals(formName)){
                     comp.setText(formName);
                 }
                 
                NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText());
                Nikitaset ns = nc.QueryPage(1, 1, "SELECT * FROM "+formName,  null );
                 
                response.refreshComponent(comp);
                
                
                callrefresh(request,response);
            }
        }); 
  
        request.retainData(com);
        nf.addComponent(com);
        
        Combolist cl2 = new Combolist();
        cl2.setId("webfinder-fields");
        cl2.setLabel("Fields");
        request.retainData(cl2);
        nf.addComponent(cl2);
        request.retainData(cl2);
        
        Checkbox cb = new Checkbox();
        cb.setId("webfinder-search");
        cb.setLabel(" ");
        cb.setData(Nset.readsplitString("Search"));
        cb.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search")){      
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webfinder-comtable").getText())); 
                    response.getContent().findComponentbyId("webfinder-fieldsearch").setVisible(true);
                    response.getContent().findComponentbyId("webfinder-fieldsearch").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webfinder-fieldsearch"));
                }
                    response.refreshComponent(response.getContent().findComponentbyId("webfinder-fieldsearch"));
                
            }
        });        
        h2.addComponent(cb);
        
        cb = new Checkbox();
        cb.setId("webfinder-paging");
        cb.setData(Nset.readsplitString("Paging"));
        cb.setText(Nset.readsplitString("Paging", ",").toJSON());
        h2.addComponent(cb);
        
        cb = new Checkbox();
        cb.setId("webfinder-multi");
        cb.setData(Nset.readsplitString("Multi Check"));
        cb.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webfinder-comtable").getText())); 
                    response.getContent().findComponentbyId("webfinder-result2").setVisible(false);
                    response.getContent().findComponentbyId("webfinder-result3").setVisible(false);
                    response.getContent().findComponentbyId("webfinder-result4").setVisible(false);      
                    response.getContent().findComponentbyId("webfinder-result1").setData(Nset.readJSON("[{'id':'autonumber','text':'Auto Number'},{'id':'col0','text':'Col 0'},{'id':'col1','text':'Col 1'},{'id':'col2','text':'Col 2'}]", true));
                    
                    response.refreshComponent(response.getContent().findComponentbyId("webfinder-result1"));
                    response.refreshComponent(response.getContent().findComponentbyId("webfinder-result2"));
                    response.refreshComponent(response.getContent().findComponentbyId("webfinder-result3"));
                    response.refreshComponent(response.getContent().findComponentbyId("webfinder-result4"));
                }else{
                    callrefresh2(request, response);
                }
                    
            }
        });        
        h2.addComponent(cb);
        
        nf.addComponent(h2);
        
        cl2 = new Combolist();
        cl2.setId("webfinder-fieldsearch");
        cl2.setLabel("Fields Search");
        cl2.setVisible(false);
        request.retainData(cl2);
        nf.addComponent(cl2);
        
        Textbox txt = new Textbox();
        txt.setId("webfinder-txtformname");
        txt.setLabel("Form Name");
        request.retainData(txt);
        nf.addComponent(txt);
        
        Combobox cl = new Combobox();
        cl.setId("webfinder-result1");
        cl.setLabel("Result 1");
        request.retainData(cl);
        nf.addComponent(cl);
        
        cl = new Combobox();
        cl.setId("webfinder-result2");
        cl.setLabel("Result 2");
        request.retainData(cl);
        nf.addComponent(cl);
        
        cl = new Combobox();
        cl.setId("webfinder-result3");
        cl.setLabel("Result 3");
        request.retainData(cl);
        nf.addComponent(cl);
        
        cl = new Combobox();
        cl.setId("webfinder-result4");
        cl.setLabel("Result 4");
        request.retainData(cl);
        nf.addComponent(cl);
        
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        
        
        Button  btn = new Button();
        btn.setId("webfinder-btnview");
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
        btn.setId("webfinder-btngenerate");
        btn.setText("Generate");
        btn.setStyle(style);
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                response.getContent().findComponentbyId("webfinder-btngenerate").setEnable(false);
                response.refreshComponent("webfinder-btngenerate");
                response.callWait();
                response.showDialog("Generate", "Do you want to generate?", "generate", "No", "Yes");  
                
                
            }
        });
        
        h.addComponent(btn);
        nf.addComponent(h);
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {

            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("generate") && responsecode.equals("button2")){     
                    
                    //connection databases
                    Component conn = response.getContent().findComponentbyId("webfinder-txtconn");
                    //form name yang diinput/yg akan dibuat
                    String formName = response.getContent().findComponentbyId("webfinder-txtformname").getText();                    
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
                    //kondisi ketika ada yang sama formnamenya
                    if(!xformname.equals(formName)){            
                        //insert form main
                        niki = logicx.Query("INSERT INTO web_form(formname,formtitle,formtype,formindex,createdby,createddate) "+ 
                                                        "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",formName,formName,"webform",(index+1)+"",user);                        

                        //select formid form main
                        String xformid = logicx.Query("select MAX(formid) from web_form ").getText(0, 0) ;
                        
                        insertcomp(xformid,response);
                        
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", formName);
                        data.setData("activitytype", "finder");
                        data.setData("mode", "generate");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);
                        
                        response.getContent().findComponentbyId("webfinder-btngenerate").setEnable(true);
                        response.refreshComponent("webfinder-btngenerate");
                        response.showDialog("Information","Complete!","", "OK"); 
                    }else{      
                        response.getContent().findComponentbyId("webfinder-btngenerate").setEnable(true);
                        response.refreshComponent("webfinder-btngenerate");
                        response.showDialog("Information","Form Name is exsist","", "OK");  
                    }
                                      
                }else if(reqestcode.equals("generate") ){     
                     response.getContent().findComponentbyId("webfinder-btngenerate").setEnable(true);
                    response.refreshComponent("webfinder-btngenerate");
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
                
                response.getContent().findComponentbyId("webfinder-txtconn").setText("default");                     
                    
            }
        }); 
        
        nf.setStyle(new Style().setStyle("width", "390").setStyle("height", "460").setStyle("n-maximizable", "false"));
        response.setContent(nf);
    }
    
    public Nset getNikitaConnection(NikitaResponse response){
        Nset exp = Nset.newObject();
                    exp.setData("dbmode", "select");
                    exp.setData("conn", response.getContent().findComponentbyId("webfinder-txtconn").getText());
                    exp.setData("tbl",response.getContent().findComponentbyId("webfinder-comtable").getText());
            return exp;
    }
    
    public void filltable(NikitaResponse response) {        
            String type = response.getContent().findComponentbyId("webfinder-txtconn").getText();
            //NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText());
            NikitaConnection nc  ;
            if (NikitaService.isModeCloud()) {
                String cname = response.getContent().findComponentbyId("webfinder-txtconn").getText();
                String user = response.getVirtualString("@+SESSION-LOGON-USER");
                String prefix = NikitaService.getPrefixUserCloud(response.getConnection(NikitaConnection.LOGIC), user);
                if (cname.startsWith(prefix) || cname.equalsIgnoreCase("sample") || cname.equalsIgnoreCase("default") ) {
                     nc = response.getConnection(cname);
                }else{
                    nc = response.getConnection("");
                }
            }else{
                nc  =  response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText());
            }
            if (nc.getError().equals("")) {
                Nikitaset nikiset =nc.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT table_name,table_name FROM user_tables ");
                }
                response.getContent().findComponentbyId("webfinder-comtable").setData(new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "none").setData("text", "Pilih salah satu")));
                if(response.getContent().findComponentbyId("webfinder-comtable").getText().equals("")){
                    response.getContent().findComponentbyId("webfinder-comtable").setText("none");
                }
            }
    }
    
    //=======================INSERT COMPONENT
    public void insertcomp(String xformid,NikitaResponse response ) {        
        //============================================FORM MAIN
        //load
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"load",1+"","navload",1+"",1+"",user);
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"load").getText(0, 0) ;   
        insertroute(xcompid,"load",response);
        
        //horizontal
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"horizontal",2+"","horizontallayout",1+"",1+"",user);
        
        
        if(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search")){
            //textsearch
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,complabel,comptext,compstyle,parent,enable,visible,createdby,createddate ) "+ 
                                "VALUES(?,?,?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"textsearch",3+"","text","Searching","@search","n-searchicon:true","$horizontal",1+"",1+"",user);
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"textsearch").getText(0, 0) ; 
            insertroute(xcompid,"textsearch",response);     
        }    
        
        
        if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){                        
            //button
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"btnok",9+"","button","OK",1+"",1+"",user);
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"btnok").getText(0, 0) ; 
            insertroute(xcompid,"button",response);
        } 
        
        //grid
        if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){            
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,compstyle,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"grid",4+"","tablegrid",(response.getContent().findComponentbyId("webfinder-result1").getText().equals("autonumber")?"":"n-multicheck-col:"+response.getContent().findComponentbyId("webfinder-result1").getText()+";"),0+"",1+"",user);
        }else{            
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible,createdby,createddate ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,"grid",4+"","tablegrid",0+"",1+"",user);
        }
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"grid").getText(0, 0) ; 
        insertroute(xcompid,"grid",response);
        
        if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
            response.getContent().findComponentbyId("webfinder-result2").setText("none");
            response.getContent().findComponentbyId("webfinder-result3").setText("none");
            response.getContent().findComponentbyId("webfinder-result4").setText("none");      
        }
        //lbl1
        if(!response.getContent().findComponentbyId("webfinder-result1").getText().equals("none") ){
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,""+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"lbl1":"lbl"+response.getContent().findComponentbyId("webfinder-result1").getText())+"",5+"","label",""+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"":"@"+response.getContent().findComponentbyId("webfinder-result1").getText())+"",0+"",0+"",user);
        }
        //lbl2
        if(!response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")){
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,""+(response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")?"lbl2":"lbl"+response.getContent().findComponentbyId("webfinder-result2").getText())+"",6+"","label",""+(response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")?"":"@"+response.getContent().findComponentbyId("webfinder-result2").getText())+"",0+"",0+"",user);
        }
        //lbl3
        if(!response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")){
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,""+(response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")?"lbl3":"lbl"+response.getContent().findComponentbyId("webfinder-result3").getText())+"",7+"","label",""+(response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")?"":"@"+response.getContent().findComponentbyId("webfinder-result3").getText())+"",0+"",0+"",user);
        }
        //lbl4
        if(!response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")){
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible,createdby,createddate ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xformid,""+(response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")?"lbl4":"lbl"+response.getContent().findComponentbyId("webfinder-result4").getText())+"",8+"","label",""+(response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")?"":"@"+response.getContent().findComponentbyId("webfinder-result4").getText())+"",0+"",0+"",user);
        }
    }
    
    public void callrefresh(NikitaRequest request, NikitaResponse response){
        Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webfinder-comtable").getText())); 
                
                //fields
                response.getContent().findComponentbyId("webfinder-fields").setData(new Nset(nikiset.getDataAllHeader()));
                reorder(response.getContent().findComponentbyId("webfinder-fields"));
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-fields")); 
                
                //fieldsearch
                response.getContent().findComponentbyId("webfinder-fieldsearch").setData(new Nset(nikiset.getDataAllHeader()));
                reorder(response.getContent().findComponentbyId("webfinder-fieldsearch"));
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-fieldsearch")); 

        callrefresh2(request, response);
                    
    }
    
    public void callrefresh2(NikitaRequest request, NikitaResponse response){
        Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webfinder-comtable").getText())); 
               
                //result1       
                nikiset = response.getConnection(response.getContent().findComponentbyId("webfinder-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webfinder-comtable").getText())); 
                response.getContent().findComponentbyId("webfinder-result1").setData(new Nset(nikiset.getDataAllHeader()).addData("none"));
                response.getContent().findComponentbyId("webfinder-result1").setText("none");  
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-result1"));   
                //result2 
                response.getContent().findComponentbyId("webfinder-result2").setData(new Nset(nikiset.getDataAllHeader()));
                response.getContent().findComponentbyId("webfinder-result2").setText("none"); 
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-result2"));   
                //result3       
                response.getContent().findComponentbyId("webfinder-result3").setData(new Nset(nikiset.getDataAllHeader()));
                response.getContent().findComponentbyId("webfinder-result3").setText("none");   
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-result3"));   
                //result4       
                response.getContent().findComponentbyId("webfinder-result4").setData(new Nset(nikiset.getDataAllHeader()));
                response.getContent().findComponentbyId("webfinder-result4").setText("none"); 
                response.refreshComponent(response.getContent().findComponentbyId("webfinder-result4"));   
                
                    
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
    
    
    public void insertroute(String xcompid,String xcompname, NikitaResponse response) {
        String conn = response.getContent().findComponentbyId("webfinder-txtconn").getText();
        String tbl = response.getContent().findComponentbyId("webfinder-comtable").getText();
        String formName = response.getContent().findComponentbyId("webfinder-txtformname").getText();                       
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

        String allfield;
        String allfield2;
        String allfield3;
        String ID = mir.getDataAllHeader().get(0);
        String field2 = mir.getDataAllHeader().get(1);        
                    
        String exp;        
        exp = "{\"result\":\"\",\"args\":{},\"code\":\"\",\"class\":\"TrueExpression\",\"id\":\"105\"}";
        String act;
        if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
            response.getContent().findComponentbyId("webfinder-result2").setText("none");
            response.getContent().findComponentbyId("webfinder-result3").setText("none");
            response.getContent().findComponentbyId("webfinder-result4").setText("none");      
        }
        //load
        if(xcompname.equals("load")){
            if(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search")){
                act = "{\"args\":{\"param5\":\"\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"\",\"param1\":\"@search\",\"param10\":\"\","
                        + "\"param9\":\"\",\"param8\":\"\",\"param7\":\"\",\"param6\":\"\"},\"code\":\"result\",\"class\":\"DefinitionAction\",\"id\":\"10\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",act,user);  

                act = "{\"args\":{\"param5\":\"\",\"param4\":\"\",\"param3\":\"%\",\"param2\":\"$textsearch\",\"param1\":\"%\",\"param9\":\"\","
                        + "\"param8\":\"\",\"param7\":\"\",\"result\":\"@searchs\",\"param6\":\"\"},\"code\":\"concat\",\"class\":\"StringManipulationAction\","
                        + "\"id\":\"1\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",exp,act,user);
            }  
            
            act = "{\"args\":{\"param5\":\"\",\"param4\":"+(response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")?"\"\"":"\"@"+response.getContent().findComponentbyId("webfinder-result4").getText()+"\"")+",\"param3\":"+(response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")?"\"\"":"\"@"+response.getContent().findComponentbyId("webfinder-result3").getText()+"\"")+",\"param2\":"+(response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")?"\"\"":"\"@"+response.getContent().findComponentbyId("webfinder-result2").getText()+"\"")+",\"param1\":"+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"\"\"":"\"@"+response.getContent().findComponentbyId("webfinder-result1").getText()+"\"")+",\"param10\":\"\","
                        + "\"param9\":\"\",\"param8\":\"\",\"param7\":\"\",\"param6\":\"\"},\"code\":\"result\",\"class\":\"DefinitionAction\",\"id\":\"10\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",act,user);  

            
            Nset n = Nset.readJSON(response.getContent().findComponentbyId("webfinder-fields").getText());   
            if(n.getArraySize() <= 0 ){
                sb = new StringBuffer();
                for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                    sb.append("\\\\\\\"").append(mir.getDataAllHeader().get(i)).append("\\\\\\\"");
                    if(mir.getDataAllHeader().size()-1 != i){
                        sb.append(",");
                    }
                }        
            }else{
                sb = new StringBuffer();
                for (int i = 0; i < n.getArraySize(); i++) {
                        sb.append("\\\\\\\"").append(n.getData(i).toString()).append("\\\\\\\"");
                        if(n.getArraySize()-1 != i){
                            sb.append(",");
                    }
                }
            }
            allfield = sb.toString();
            
            Nset n2 = Nset.readJSON(response.getContent().findComponentbyId("webfinder-fieldsearch").getText());              
            if(n2.getArraySize() <= 0){
                act = "{\"args\":{\"result\":\"@tbl\",\"param4\":"+(response.getContent().findComponentbyId("webfinder-paging").getText().contains("Paging") ? "\"$grid\"":"\"\"")+",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{"+(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search") ? "\\\"0\\\":\\\"@searchs\\\"":"")+"},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":"
                        + "{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\""+(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search")? "2": "0" )+"\\\","
                        + "\\\"sqlwhere\\\":\\\""+(response.getContent().findComponentbyId("webfinder-search").getText().contains("Search")? ""+field2+" LIKE ? ORDER BY "+field2+" ASC ": "" )+"\\\",\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\","
                        + "\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\",\\\"fields\\\":\\\"["+allfield+"]\\\",\\\"args\\\":{},"
                        + "\\\"dbmode\\\":\\\"select\\\","
                        + "\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",exp,act,user);
            }else{
            
                sb2 = new StringBuffer();
                sb3 = new StringBuffer();
                for (int i = 0; i < n2.getArraySize(); i++) {
                    sb2.append(n2.getData(i).toString()).append(" like ? ");
                    sb3.append("\\\"").append(i).append("\\\":\\\"@searchs\\\"");
                    if(n2.getArraySize()-1 != i){
                        sb2.append("OR ");
                        sb3.append(",");
                    }
                }
                allfield2 = sb2.toString();
                allfield3 = sb3.toString();

                act = "{\"args\":{\"result\":\"@tbl\",\"param4\":"+(response.getContent().findComponentbyId("webfinder-paging").getText().contains("Paging") ? "\"$grid\"":"\"\"")+",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{"+allfield3+"},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":"
                        + "{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"2\\\","
                        + "\\\"sqlwhere\\\":\\\"" +allfield2+" ORDER BY "+field2+"\\\",\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\","
                        + "\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\",\\\"fields\\\":\\\"["+allfield+"]\\\",\\\"args\\\":{},"
                        + "\\\"dbmode\\\":\\\"select\\\","
                        + "\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",exp,act,user);
            }
            
            if(n.getArraySize() <= 0 ){
                sb = new StringBuffer();
                for (int i = 0; i < mir.getDataAllHeader().size(); i++) {
                    if(i != mir.getDataAllHeader().size()){
                        sb.append(mir.getDataAllHeader().get(i)).append(",");
                    }
                    else{
                        sb.append(mir.getDataAllHeader().get(i));
                    }
                }
            }else{                
                sb = new StringBuffer();
                for (int i = 0; i < n.getArraySize(); i++) {
                    if(i != n.getArraySize()-1){
                        sb.append(n.getData(i).toString()).append(",");
                    }
                    else{
                        sb.append(n.getData(i).toString());
                    }
                }  
            }
            allfield = sb.toString();
            String headertype = "[#]";            
            if(response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
                headertype = "[v]";
            }
                
            act = "{\"args\":{\"param4\":\""+headertype+allfield+"\",\"param3\":\"\",\"param2\":\"@tbl\",\"param1\":\"$grid\"},"
                    + "\"code\":\"inflate\",\"class\":\"FormAction\",\"id\":\"19\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",act,user);

            if(!response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
                act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"@tbl\",\"param1\":\"write\"},\"code\":\"\",\"class\":\"JsonAction\",\"id\":\"26\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,6+"",act,user);

                act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"$grid\",\"param1\":\"settag\"},\"code\":\"component\","
                        + "\"class\":\"ComponentAction\",\"id\":\"25\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,7+"",act,user);
            }
        }else if(xcompname.equals("textsearch")){            
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                  "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);  
            
        }else if(xcompname.equals("grid")){
            
            if(!response.getContent().findComponentbyId("webfinder-multi").getText().contains("Multi Check")){
                act = "{\"args\":{\"param3\":\"@datagrid\",\"param2\":\"$grid[tag]\",\"param1\":\"nikitaset\"},"
                        + "\"code\":\"\",\"class\":\"JsonAction\",\"id\":\"26\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",exp,act,user);
            
                
                act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"@REST\","
                        + "\"param1\":\"\"},\"code\":\"new\",\"class\":\"VariableAction\",\"id\":\"29\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",act,user);
                
                act = "{\"args\":{\"param5\":\""+(response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")?"":"@datagrid[\\\"data\\\",\\\"@+SELECTEDROW\\\",\\\""+response.getContent().findComponentbyId("webfinder-result2").getText()+"\\\"]")+"\",\"param4\":\""+(response.getContent().findComponentbyId("webfinder-result2").getText().equals("none")?"":"$lbl"+response.getContent().findComponentbyId("webfinder-result2").getText())+"\",\"param3\":\""+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"":"@datagrid[\\\"data\\\",\\\"@+SELECTEDROW\\\",\\\""+response.getContent().findComponentbyId("webfinder-result1").getText()+"\\\"]")+"\","
                        + "\"param2\":\""+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"":"$lbl"+response.getContent().findComponentbyId("webfinder-result1").getText())+"\",\"param1\":\"@REST\",\"param9\":\""+(response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")?"":"@datagrid[\\\"data\\\",\\\"@+SELECTEDROW\\\",\\\""+response.getContent().findComponentbyId("webfinder-result4").getText()+"\\\"]")+"\","
                        + "\"param8\":\""+(response.getContent().findComponentbyId("webfinder-result4").getText().equals("none")?"":"$lbl"+response.getContent().findComponentbyId("webfinder-result4").getText())+"\",\"param7\":\""+(response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")?"":"@datagrid[\\\"data\\\",\\\"@+SELECTEDROW\\\",\\\""+response.getContent().findComponentbyId("webfinder-result3").getText()+"\\\"]")+"\",\"param6\":\""+(response.getContent().findComponentbyId("webfinder-result3").getText().equals("none")?"":"$lbl"+response.getContent().findComponentbyId("webfinder-result3").getText())+"\"},"
                        + "\"code\":\"set\",\"class\":\"VariableAction\",\"id\":\"42\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,5+"",act,user);

                act = "{\"args\":{\"param2\":\"@REST\",\"param1\":\"FINDER\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,11+"",act,user);
            
                act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                    "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,12+"",act,user);
            }
            
            
            if(response.getContent().findComponentbyId("webfinder-paging").getText().contains("Paging")){
                exp = "{\"result\":\"\",\"args\":{},\"code\":\"filter\",\"class\":\"BooleanExpression\",\"id\":\"116\"}";
                act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",exp,act,user);

                act = "{\"args\":{},\"code\":\"break\",\"class\":\"SystemAction\",\"id\":\"24\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",act,user);
            }
            
            
        }else if(xcompname.equals("button")){            
           
                act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"@REST\","
                        + "\"param1\":\"\"},\"code\":\"new\",\"class\":\"VariableAction\",\"id\":\"29\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,1+"",act,user);

                act = "{\"args\":{\"param5\":\"\",\"param4\":\"\",\"param3\":\"@+CHECKEDROWS\","
                        + "\"param2\":\""+(response.getContent().findComponentbyId("webfinder-result1").getText().equals("none")?"":"$lbl"+response.getContent().findComponentbyId("webfinder-result1").getText())+"\",\"param1\":\"@REST\",\"param9\":\"\","
                        + "\"param8\":\"\",\"param7\":\"\",\"param6\":\"\"},"
                        + "\"code\":\"set\",\"class\":\"VariableAction\",\"id\":\"42\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,2+"",act,user);


                act = "{\"args\":{\"param2\":\"@REST\",\"param1\":\"FINDER\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action,createdby,createddate) "+ 
                                      "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,3+"",act,user);
            
                
            act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action,createdby,createddate) "+ 
                                "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",xcompid,4+"",exp,act,user);
            
        }
    }
    
    
}