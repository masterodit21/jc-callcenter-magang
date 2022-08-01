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
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.action.ConnectionAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Combolist;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;
 

/**
 *
 * @author user
 */
public class webdatabase extends NikitaServlet{
//    private Nset bufferd = Nset.newObject();
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Databases");
        HorizontalLayout h = new HorizontalLayout(); 
                
        Style style = new Style();
        style.setStyle("width", "640");
        style.setStyle("height", "480");
        nf.setStyle(style);
        
        final Label lblidcomp = new Label();
        lblidcomp.setId("webdb-lblidcomp2");
        lblidcomp.setTag(request.getParameter("idcomp"));
        lblidcomp.setVisible(false);
        request.retainData(lblidcomp);
        nf.addComponent(lblidcomp);
        
        final Label lbl = new Label();
        lbl.setId("webdb-lblflag");
        lbl.setTag(request.getParameter("flag"));
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        final Label lblparam = new Label();
        lblparam.setId("webdb-lblparam");
        lblparam.setTag(request.getParameter("paramid"));
        lblparam.setVisible(false);
        request.retainData(lblparam);
        nf.addComponent(lblparam);
        
        final Label lblidform = new Label();
        lblidform.setId("webdb-lblidform2");
        lblidform.setTag(request.getParameter("idform"));
        lblidform.setVisible(false);
        request.retainData(lblidform);
        nf.addComponent(lblidform); 
        
        final Label lbldata = new Label();
        lbldata.setId("webdb-lbldata");
        lbldata.setTag(request.getParameter("search"));
        lbldata.setVisible(true);
        nf.addComponent(lbldata);
        
        //dummy query
        if (request.getParameter("search").equals("")) {
        }else  if (!request.getParameter("search").startsWith("{")) {
            
            lbldata.setTag(Nset.newObject().setData("conn", "default").setData("dbmode", "query").setData("sql", request.getParameter("search")).toJSON());
            request.setParameter("search", lbldata.getTag());
        }
        
        final Label lblmode = new Label();
        lblmode.setId("webdb-mode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        request.retainData(lblmode);
        nf.addComponent(lblmode);
        
        Nikitaset nikiset;
        if (NikitaService.isModeCloud()) {
            String user = response.getVirtualString("@+SESSION-LOGON-USER");
            nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname as codeid,connname from web_connection WHERE createdby = '"+user+"' OR connname = 'sample'  OR connname = 'default'  ORDER BY connname ;");
        }else{
             nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname as codeid,connname from web_connection ORDER BY connname;");
        }
        
        
        //connection
        Combobox com = new Combobox();
        com.setId("webdb-txtconn");
        com.setLabel("Connection");
        com.setText(request.getParameter("conn"));
        com.setData( new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "default").setData("text", "Default"))  ); 
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                filltable(response);            
    
                request.setParameter("action", "");
                request.setParameter("idcomp", response.getContent().findComponentbyId("webdb-lblidcomp2").getTag()); 
                request.setParameter("idform", response.getContent().findComponentbyId("webdb-lblidform2").getTag()); 
                request.setParameter("dbmode", component.getText());
                request.setParameter("conn", response.getContent().findComponentbyId("webdb-txtconn").getText());
                request.setParameter("paramid", response.getContent().findComponentbyId("webdb-lblparam").getTag());
                request.setParameter("flag", response.getContent().findComponentbyId("webdb-lblflag").getTag());
                //System.out.println("txt= "+response.getContent().findComponentbyId("webdb-txtconn").getText());
                response.reload(request);
            }
        }); 
        request.retainData(com);        
        nf.addComponent(com);
        
         //mode
        final Combobox comModel = new Combobox();
        comModel.setId("webdb-txtmode");
        comModel.setLabel("Mode");
        comModel.setVisible(false);
        comModel.setText(request.getParameter("dbmode"));
        
        comModel.setData( Nset.readsplitString("pilih salah satu|query|call|select|insert|update|delete") ); 
        comModel.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                lblmode.setTag(comModel.getText());
                request.setParameter("action", "");
                request.setParameter("idcomp", response.getContent().findComponentbyId("webdb-lblidcomp2").getTag()); 
                request.setParameter("idform", response.getContent().findComponentbyId("webdb-lblidform2").getTag()); 
                request.setParameter("dbmode", component.getText());
                request.setParameter("conn", response.getContent().findComponentbyId("webdb-txtconn").getText());
                request.setParameter("paramid", response.getContent().findComponentbyId("webdb-lblparam").getTag());
                request.setParameter("flag", response.getContent().findComponentbyId("webdb-lblflag").getTag());
                response.reload(request);
            }
        });  
        request.retainData(comModel);
        nf.addComponent(comModel);        
        
        VerticalLayout  layout = new VerticalLayout();
        //nquery        
        Textarea txtarea = new Textarea();
        txtarea.setId("webdb-txtquery");
        txtarea.setLabel("");
        txtarea.setVisible(false);
        Style st = new Style();
        txtarea.setStyle(st);
        txtarea.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                  
                request.setParameter("conn",response.getContent().findComponentbyId("webdb-txtconn").getText());
                
                
                response.showform("webdatabaseautoquery", request,"", true);
            }
        });
        
         
        txtarea.setStyle(new Style().setStyle("width", "618px").setStyle("height", "235px").setStyle("resize", "both"));//vertical
        layout.addComponent(txtarea);
        nf.addComponent(layout);
        
        txtarea = new Textarea();
        txtarea.setId("webdb-txtcall");
        txtarea.setLabel("Call");
        txtarea.setVisible(false);
        txtarea.setStyle(new Style().setStyle("width", "518px"));//vertical
        nf.addComponent(txtarea);
        
        
        
        //param query
        Button btn = new Button();
        btn.setId("webdb-btnparam");
        btn.setText("Param");
        btn.setVisible(false);
        btn.setOnClickListener(new Component.OnClickListener(){
                @Override
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Nset n = null;
                    if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query")){
                        execquery(response.getContent().findComponentbyId("webdb-txtquery").getText(),response,n);   
                    }
                    if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("call")){
                        execquery(response.getContent().findComponentbyId("webdb-txtcall").getText(),response,n);   
                    }
                    response.writeContent();      
                }
            }
        ); 
        style = new Style();

        style.setStyle("width", "100px");
        style.setStyle("height", "34px");
        style.setStyle("margin-left", "525px");//256px
        //style.setStyle("margin-top", "10px");
        //style.setStyle("margin-bottom", "5px");
        //style.setStyle("padding", "7px");
        btn.setStyle(style);
        nf.addComponent(btn);   
        
        
        //table

        com = new Combobox();
        com.setId("webdb-comtable");
        com.setLabel("Table"); 
        com.setText(request.getParameter("tbl"));
        com.setVisible(false);
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                    
                String type = response.getContent().findComponentbyId("webdb-comtable").getText();
                if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("insert") ||  response.getContent().findComponentbyId("webdb-txtmode").getText().equals("update")){
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1, 1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
                 
                     //?????
                    Nset narray = Nset.newArray();                    
                        for (int i = 0; i < nikiset.getDataAllHeader().size(); i++) {
                            narray.addData(Nset.newObject().setData("text",nikiset.getHeader(i)).setData("id",nikiset.getHeader(i)));
                        }                       
                    fillcolumn(response, narray,0,null);
                  
                }
                
                if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("select")){
                    
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1, 1,"SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
  
                    response.getContent().findComponentbyId("webdb-fields").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webdb-fields"));
                    response.refreshComponent(response.getContent().findComponentbyId("webdb-fields"));                    
                    
                    response.getContent().findComponentbyId("webdb-orderby").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webdb-orderby"));
                    response.refreshComponent(response.getContent().findComponentbyId("webdb-orderby"));
                    
                    
                }
                response.writeContent();
            }
        });
        nf.addComponent(com);
        
        final Combolist cl = new Combolist();
        cl.setId("webdb-fields");
        cl.setLabel("Fields");
        cl.setVisible(false);
        nf.addComponent(cl);
        cl.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
                if (!response.getContent().findComponentbyId("webdb-fields-sort").getText().equals("[]")) {
                    reorder(cl);
                    response.refreshComponent(cl);
                }
            }
        });
        
        Checkbox cb = new Checkbox();
        cb.setId("webdb-fields-sort");
        cb.setVisible(false);
        cb.setData(Nset.readsplitString("Sort"));
        nf.addComponent(cb);
        
        ComponentGroup wherelayout = new ComponentGroup();
        
        //where
        Nset nsetData = Nset.readJSON("[{'id':'0','text':'Pilih salah satu'},{'id':'1','text':'Field'},{'id':'2','text':'Custom'}]",true);        
        com = new Combobox();
        com.setId("webdb-comwhere");
        com.setLabel("Where"); 
        com.setData(nsetData);
        com.setVisible(false); 
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                              
                response.getContent().findComponentbyId("webdb-comfield").setText("");
                response.getContent().findComponentbyId("webdb-txtfield").setText("");
                response.getContent().findComponentbyId("webdb-comfield2").setText("");
                response.getContent().findComponentbyId("webdb-txtfield2").setText("");
                response.getContent().findComponentbyId("webdb-comlogic").setText("");
                response.getContent().findComponentbyId("webdb-txtwhere").setText("");
                condition2(response);
                response.writeContent();
            }
        }); 
        wherelayout.addComponent(com);
        
        h = new HorizontalLayout();
        com = new Combobox();
        com.setId("webdb-comfield");
        com.setLabel("Field[1] Name");
        com.setVisible(false);
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                
            }
        }); 
        wherelayout.addComponent(com);
        
        Textsmart txtsmart = new Textsmart();
        txtsmart.setId("webdb-txtfield");
        txtsmart.setLabel("Field[1] Value");
        txtsmart.setVisible(false);
        txtsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        }); 
        wherelayout.addComponent(txtsmart);
        
        //wherelayout.addComponent(h);
        
        //logical
        nsetData = Nset.readJSON("[{'id':'0','text':'NONE'},{'id':'1','text':'OR'},{'id':'2','text':'AND'}]",true);
        com = new Combobox();
        com.setId("webdb-comlogic");
        com.setVisible(false);
        com.setLabel("Comparation");
        com.setData(nsetData);
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                if(response.getContent().findComponentbyId("webdb-comlogic").getText().equals("0")){
                    response.getContent().findComponentbyId("webdb-comfield2").setVisible(true);
                }
            }
        }); 
        wherelayout.addComponent(com);
               
        com = new Combobox();
        com.setId("webdb-comfield2");
        com.setLabel("Field[2] Name");
        com.setVisible(false);
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                
            }
        }); 
        wherelayout.addComponent(com);
        
        txtsmart = new Textsmart();
        txtsmart.setId("webdb-txtfield2");
        txtsmart.setLabel("Field[2] Value");
        txtsmart.setVisible(false);
        txtsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        }); 
        wherelayout.addComponent(txtsmart);
        
        txtarea = new Textarea();
        txtarea.setId("webdb-txtwhere");
        txtarea.setStyle(st);
        txtarea.setLabel(" ");
        txtarea.setVisible(false);
        wherelayout.addComponent(txtarea);        
        
        btn = new Button();
        btn.setId("webdb-btnparamwhere");
        btn.setText("Param");
        btn.setVisible(false);
        btn.setOnClickListener(new Component.OnClickListener(){

                @Override
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Nset n = null;
                    execquery(response.getContent().findComponentbyId("webdb-txtwhere").getText(),response,n);
                    response.writeContent();
                }
            }
        );
        style = new Style();
        style.setStyle("width", "60px");
        style.setStyle("margin-left", "105px");
        btn.setStyle(style);        
        wherelayout.addComponent(btn);
        
        //column
        ComponentGroup verticalLayout = new ComponentGroup();
        verticalLayout.setId("webdb-column");        
        nf.addComponent(verticalLayout);
        nf.addComponent(wherelayout);

        
        Nset nsetData2 = Nset.readJSON("[{'id':'0','text':'Pilih salah satu'},{'id':'1','text':'ASC'},{'id':'2','text':'DESC'},{'id':'3','text':'Custom'}]",true);        
        com = new Combobox();
        com.setId("webdb-conditionorder");
        com.setLabel("Order By"); 
        com.setData(nsetData2);
        com.setVisible(false); 
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                response.getContent().findComponentbyId("webdb-orderby").setText("");
                response.getContent().findComponentbyId("webdb-txtordercustom").setText("");
                condition3(response);
                response.writeContent();       
            }
        }); 
        nf.addComponent(com);

        Textbox txt = new Textbox();
        txt.setId("webdb-txtordercustom");
        txt.setLabel(" ");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        final Combolist cl2 = new Combolist();
        cl2.setId("webdb-orderby");
        cl2.setLabel(" ");
        cl2.setVisible(false);
        nf.addComponent(cl2);
        cl2.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
                if (!response.getContent().findComponentbyId("webdb-orderby-sort").getText().equals("[]")) {
                    reorder(cl2);
                    response.refreshComponent(cl2);
                }
            }
        });
        
        cb = new Checkbox();
        cb.setId("webdb-orderby-sort");
        cb.setLabel(" ");
        cb.setVisible(false);
        cb.setData(Nset.readsplitString("Sort"));
        nf.addComponent(cb);
       
        for (int i = 0; i < 250; i++) {
            Textsmart component = new Textsmart();
            component.setId("webdb-gen-"+i);//just bufferd
            verticalLayout.addComponent(component);            
        }
        
        //column
        ComponentGroup verticalLayout2 = new ComponentGroup();
        verticalLayout2.setId("webdb-column2");        //paranm call & query maybe
        nf.addComponent(verticalLayout2);
        for (int i = 0; i < 100; i++) {
            Textsmart component = new Textsmart();
            component.setId("webdb-gen2-"+i);//just bufferd
            verticalLayout2.addComponent(component);
            component.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {          
                }
            }); 
        }
        
        Textsmart textsmart = new Textsmart();
        textsmart.setId("webexpression-result");
        textsmart.setLabel("Result");
        textsmart.setHint("Variable to Save SQL");
        textsmart.setVisible(false);
        nf.addComponent(textsmart);
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        h = new HorizontalLayout();
        //execute query
        btn = new Button();
        btn.setId("webdb-btnexec");
        btn.setText("Execute");
        btn.setVisible(false);
 
        btn = new Button();
        btn.setId("webdb-btnexecqueryall");
        btn.setText("Execute");
         
 
        style.setStyle("width", "100px");
        style.setStyle("height", "34px");
        style.setStyle("margin-left", "10px");
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
                
                if (response.getContent().findComponentbyId("webdb-txtmode").getText().equals("select")) {
                    if ( n.getData("args").getArraySize()==0) {
                            response.showform("webtableview", request,"", true);
                        }else{
                            response.showform("webdatabaseparameter", request,"", true);
                        }
                }else if (response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query") ) {
                    if (n.getData("query").toString().contains("select")||n.getData("query").toString().contains("show")) {
                        if ( n.getData("args").getArraySize()==0) {
                            response.showform("webtableview", request,"", true);
                        }else{
                            response.showform("webdatabaseparameter", request,"", true);
                        }
                    }else{
                        response.showDialogResult("Execute", "Are you sure to exec ?", "execute", n, "NO", "YES");
                    }
                }else if (response.getContent().findComponentbyId("webdb-txtmode").getText().equals("call") ) {
                    if (n.getData("call").toString().contains("select")||n.getData("call").toString().contains("show")) {
                        if ( n.getData("args").getArraySize()==0) {
                            response.showform("webtableview", request,"", true);
                        }else{
                            response.showform("webdatabaseparameter", request,"", true);
                        }
                    }else{
                        response.showDialogResult("Execute", "Are you sure to exec ?", "execute", n, "NO", "YES");
                    }
                }else{
                    response.showDialogResult("Execute", "Are you sure to exec ?", "execute", n, "NO", "YES");
                }
                
                response.write();
            }
        });
        h.addComponent(btn);
        
        btn = new Button();
        btn.setId("webdb-btndone");
        btn.setText("Done");  
        if(lbl.getTag().equals("home")){
            btn.setVisible(false);
        }else{            
            btn.setVisible(true);
        }
        style.setStyle("width", "100px");
        style.setStyle("height", "34px");         
        btn.setStyle(style);
        
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                    Nset exp = getNikitaConnection(response);
                    
                    Nset result = Nset.newObject();
                    
                    //dummy query
                    if (response.getContent().findComponentbyId("webdb-txtconn").getText().equals("default") && response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query") && !response.getContent().findComponentbyId("webdb-txtquery").getText().contains("?")) {
                        result.setData("variable", response.getContent().findComponentbyId("webdb-txtquery").getText() ); 
                    }else if (response.getContent().findComponentbyId("webdb-txtconn").getText().equals("default") && response.getContent().findComponentbyId("webdb-txtmode").getText().equals("call") && !response.getContent().findComponentbyId("webdb-txtcall").getText().contains("?")) {
                        result.setData("variable", response.getContent().findComponentbyId("webdb-txtcall").getText() ); 
                    }else{
                        result.setData("variable", exp.toJSON()); 
                    }
          
                    result.setData("paramid",response.getContent().findComponentbyId("webdb-lblparam").getTag());
                    
                    if ( response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query")  && exp.getData("sql").toString().trim().toLowerCase().startsWith("select") ) {                        
                        response.showDialogResult("Execute Query than Save", "Please choose one, guys..", "queryfhide", result, "Save", "Execute");
                    }else{
                        
                        response.closeform(response.getContent());

                        response.setResult("webdatabase", result);                
                        response.write();
                    }
                            
                    
                     
                     
                    
            }
        }); 
        h.addComponent(btn);      
        h.setStyle(Style.createStyle("float","right"));
        
        nf.addComponent(h);
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", "");

                if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("insert") || 
                        response.getContent().findComponentbyId("webdb-txtmode").getText().equals("update")){                    
                     Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1, 1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
                    //??????????????
                    Nset narray = Nset.newArray();
                      for (int i = 0; i < nikiset.getDataAllHeader().size(); i++) {
                            narray.addData(Nset.newObject().setData("text",nikiset.getHeader(i)).setData("id",nikiset.getHeader(i)));

                        }                     
                    fillcolumn(response, narray,0,null);            
                }
                
                if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals("select")){
               
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1,1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
                     
                    response.getContent().findComponentbyId("webdb-fields").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webdb-fields"));
                    response.getContent().findComponentbyId("webdb-orderby").setData(new Nset(nikiset.getDataAllHeader()));
                    reorder(response.getContent().findComponentbyId("webdb-orderby"));
                    //response.getContent().findComponentbyId("webdb-fields").setText(response.getContent().findComponentbyId("webdb-fields").getData().toJSON());
                }
                
                if(!response.getContent().findComponentbyId("webdb-txtmode").getText().equals("insert")){
                    Nset n = null;
                    ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column2");
                    for (int i = 0; i < verticalLayout.getComponentCount(); i++) {
                        verticalLayout.getComponent(i).setLabel("Param ["+(Utility.getInt(verticalLayout.getComponent(i).getTag())+1)+"]");
                        verticalLayout.getComponent(i).setOnClickListener(new Component.OnClickListener() {
                            @Override
                            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   

                            }
                        }); 
                    }
                }
                filltable(response);   
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column");
                ComponentGroup verticalLayout2 = (ComponentGroup)response.getContent().findComponentbyId("webdb-column2");
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webdb-lbldata").getTag());            
                Nset n =  nset.getData("where");    
                Nset o =  nset.getData("orderby");    
                
                String fieldsx = nset.getData("fields").toString();
                String orderbyx = o.getData("orderbys").toString();
               
                
                if(!nset.getData("conn").toString().equals("")){ 
                    
                    response.getContent().findComponentbyId("webdb-txtmode").setText(nset.getData("dbmode").toString());
                    response.getContent().findComponentbyId("webdb-txtconn").setText(nset.getData("conn").toString());                    
                    response.getContent().findComponentbyId("webdb-txtquery").setText(nset.getData("sql").toString());
                    response.getContent().findComponentbyId("webdb-txtcall").setText(nset.getData("callz").toString());
                    response.getContent().findComponentbyId("webdb-comtable").setText(nset.getData("tbl").toString());
                    response.getContent().findComponentbyId("webdb-comfield").setText(n.getData("param").getData("parameter1").toString());
                    response.getContent().findComponentbyId("webdb-txtfield").setText(n.getData("paramargs").getData("parameter1").toString());
                    response.getContent().findComponentbyId("webdb-comfield2").setText(n.getData("param").getData("parameter2").toString());
                    response.getContent().findComponentbyId("webdb-txtfield2").setText(n.getData("paramargs").getData("parameter2").toString());
                    response.getContent().findComponentbyId("webdb-comlogic").setText(n.getData("logic").toString());
                    response.getContent().findComponentbyId("webdb-comwhere").setText(n.getData("type").toString());
                    response.getContent().findComponentbyId("webdb-txtwhere").setText(n.getData("sqlwhere").toString());      
                    
                    Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1,1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
                    
                    response.getContent().findComponentbyId("webdb-fields").setData(new Nset(nikiset.getDataAllHeader()));    
                    response.getContent().findComponentbyId("webdb-fields").setText(fieldsx);
                    reorder(response.getContent().findComponentbyId("webdb-fields"));
                    
                    response.getContent().findComponentbyId("webdb-conditionorder").setText(o.getData("conditionorders").toString());
                    response.getContent().findComponentbyId("webdb-txtordercustom").setText(o.getData("customs").toString());
                    response.getContent().findComponentbyId("webdb-orderby").setData(new Nset(nikiset.getDataAllHeader()));    
                    response.getContent().findComponentbyId("webdb-orderby").setText(orderbyx);
                    reorder(response.getContent().findComponentbyId("webdb-orderby"));
                }else{                    
                    if(response.getContent().findComponentbyId("webdb-txtconn").getText().equals("")){
                        response.getContent().findComponentbyId("webdb-txtconn").setText("default");                    
                    }
                }
                
                if(nset.getData("dbmode").toString().equals("insert") || 
                        nset.getData("dbmode").toString().equals("update") || 
                        nset.getData("dbmode").toString().equals("query") || 
                        nset.getData("dbmode").toString().equals("call") || 
                        nset.getData("dbmode").toString().equals("delete")  || 
                        nset.getData("dbmode").toString().equals("select")){
                }else{
                    verticalLayout.removeAllComponents();
                    verticalLayout2.removeAllComponents();
                }
                
                if(nset.getData("dbmode").toString().equals("query")){
                        verticalLayout.removeAllComponents();
                    if(response.getContent().findComponentbyId("webdb-txtquery").getText().equals(""))                        
                        verticalLayout2.removeAllComponents();
                }else if(nset.getData("dbmode").toString().equals("call")){
                        verticalLayout.removeAllComponents();
                    if(response.getContent().findComponentbyId("webdb-txtcall").getText().equals(""))                        
                        verticalLayout2.removeAllComponents();
                }else{
                    if (n.getData("type").toString().contains("0")||n.getData("type").toString().contains("1")) {
                        verticalLayout2.removeAllComponents();
                    }
                    if (!n.getData("sqlwhere").toString().contains("?")) {
                        verticalLayout2.removeAllComponents();
                    }    
                }
                if(nset.getData("dbmode").toString().equals("delete")){
                    verticalLayout.removeAllComponents();
                }
                if(nset.getData("dbmode").toString().equals("select")){
                    verticalLayout.removeAllComponents();
                }
                if(nset.getData("dbmode").toString().equals("insert")){
                    verticalLayout2.removeAllComponents();
                }
               
                //13K    
                response.getContent().findComponentbyId("webdb-txtmode").setVisible(true);
               
                
                
                if(response.getContent().findComponentbyId("webdb-txtmode").getText().equals(""))
                    response.getContent().findComponentbyId("webdb-txtmode").setText("pilih salah satu");
                    
                condition2(response);
                condition3(response);
                condition(response.getContent().findComponentbyId("webdb-txtconn").getText(),response.getContent().findComponentbyId("webdb-txtmode").getText(), response, request);

                if(!response.getContent().findComponentbyId("webdb-txtquery").getText().equals("")){
                    execquery(response.getContent().findComponentbyId("webdb-txtquery").getText(),response,nset.getData("argswhere"));      
                }
                if(!response.getContent().findComponentbyId("webdb-txtcall").getText().equals("")){
                    execquery(response.getContent().findComponentbyId("webdb-txtcall").getText(),response,nset.getData("argswhere"));      
                }
                if(!response.getContent().findComponentbyId("webdb-txtwhere").getText().equals("")){
                    execquery(response.getContent().findComponentbyId("webdb-txtwhere").getText(),response,nset.getData("argswhere"));
                } 

                if(nset.getData("dbmode").toString().equals("insert") || nset.getData("dbmode").toString().equals("update")){ 
                    String type = response.getContent().findComponentbyId("webdb-comtable").getText();
                         Nikitaset nikiset = response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1, 1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 
                         //???????????
                        Nset narray = Nset.newArray();
                        for (int i = 0; i < nikiset.getDataAllHeader().size(); i++) {
                            narray.addData(Nset.newObject().setData("text",nikiset.getHeader(i)).setData("id",nikiset.getHeader(i)));

                        }   
                        fillcolumn(response, narray,0,nset.getData("args"));
                }                
            }
        }); 
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(responsecode.equals("webvariablelist")){
                    response.getContent().findComponentbyId(result.getData("paramid").toString()).setText(result.getData("variable").toString());
                    response.writeContent();
                }else if(reqestcode.equals("execute")&&responsecode.equals("button2")){
                        request.setParameter("sql",result.getData("query").toString());
                        request.setParameter("callz",result.getData("call").toString());
                        request.setParameter("conn", result.getData("conn").toString()); 
                        request.setParameter("args", result.getData("args").toJSON()); 
                        request.setParameter("argsname", result.getData("argsname").toJSON());
                        
                        if ( result.getData("args").getArraySize()==0) {
                            response.showform("webtableview", request,"", true);
                        }else{
                            response.showform("webdatabaseparameter", request,"", true);
                        }
                        response.write();
                }else if(reqestcode.equals("queryfhide")){
                    Nset nv = Nset.readJSON(result.getData("variable").toString());
                    
                    nv.setData("fhide", "[\"\"]" );
                    if (responsecode.equals("button2")) {  
                        if (  !nv.getData("sql").toString().equals("") ) {
                            Nset n   =  Nset.readJSON(nv.getData("args").toString());
                            String[] arg = new String[n.getArraySize()<=0?0:n.getArraySize()];
                            for (int i = 0; i < arg.length; i++) {
                                arg[i] = n.getData(i).toString();
                            }

                            NikitaConnection nikitaConnection = response.getConnection(nv.getData("conn").toString());
                            Nikitaset nikiset = nikitaConnection.QueryPage(1,1,nv.getData("sql").toString(), arg);
                             
                            nv.setData("fileds", "");
                            nv.setData("fhide", new Nset(nikiset.getDataAllHeader()).toJSON() );
                            result.setData("variable",nv.toJSON());
                        }
                    }
                    
                    response.closeform(response.getContent());                
                    response.setResult("webdatabase", result);                
                    response.write();
                }
            }
        });
        response.setContent(nf);            
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
    
    
    public Nset getNikitaConnection(NikitaResponse response){
        Nset exp = Nset.newObject();
                    exp.setData("args", getfillcolumn(response));
                    exp.setData("argswhere", getfillcolumn2(response));
                    exp.setData("dbmode", response.getContent().findComponentbyId("webdb-txtmode").getText());
                    exp.setData("conn", response.getContent().findComponentbyId("webdb-txtconn").getText());
                    exp.setData("sql", response.getContent().findComponentbyId("webdb-txtquery").getText());
                    exp.setData("callz", response.getContent().findComponentbyId("webdb-txtcall").getText());
                    exp.setData("paramid",response.getContent().findComponentbyId("webdb-lblparam").getTag());
                    exp.setData("fields",response.getContent().findComponentbyId("webdb-fields").getText());
                    
                    if (response.getContent().findComponentbyId("webdb-txtmode").getText().equals("select") && response.getContent().findComponentbyId("webdb-fields").getText().length()<=7 ) {
                      exp.setData("fhide",response.getContent().findComponentbyId("webdb-fields").getData().toJSON());
                    }else{
                      exp.setData("fhide","[\"\"]");
                    }
                            
                    
                    
//                    exp.setData("orderbys",response.getContent().findComponentbyId("webdb-orderby").getText());
//                    exp.setData("conditionorders",response.getContent().findComponentbyId("webdb-conditionorder").getText());
                    exp.setData("tbl", response.getContent().findComponentbyId("webdb-comtable").getText());
                    exp.setData("orderby", Nset.newObject().setData("orderbys", 
                                                                  response.getContent().findComponentbyId("webdb-orderby").getText()).
                                                          setData("conditionorders", 
                                                                  response.getContent().findComponentbyId("webdb-conditionorder").getText()).
                                                          setData("customs", 
                                                                  response.getContent().findComponentbyId("webdb-txtordercustom").getText()));

                    exp.setData("where", Nset.newObject().setData("logic", 
                                                                  response.getContent().findComponentbyId("webdb-comlogic").getText()).
                                                          setData("type", 
                                                                  response.getContent().findComponentbyId("webdb-comwhere").getText()).
                                                          setData("sqlwhere", 
                                                                  response.getContent().findComponentbyId("webdb-txtwhere").getText()).
                                                          setData("paramargs",Nset.newObject().setData("parameter1",
                                                                    response.getContent().findComponentbyId("webdb-txtfield").getText()).
                                                                                           setData("parameter2", 
                                                                    response.getContent().findComponentbyId("webdb-txtfield2").getText())).                            
                                                          setData("param",Nset.newObject().setData("parameter1",response.getContent().findComponentbyId("webdb-comfield").getText()).
                                                                                           setData("parameter2",response.getContent().findComponentbyId("webdb-comfield2").getText())));

                    
            return exp;
    }

    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if(component.startsWith("webdb-gen-") || component.startsWith("webdb-gen2-") || component.startsWith("webdb-txtfield")){
            request.setParameter("search", response.getContent().findComponentbyId(component).getText());
            request.setParameter("paramid", component);            
            request.setParameter("idcomp", response.getContent().findComponentbyId("webdb-lblidcomp2").getTag()); 
            request.setParameter("idform", response.getContent().findComponentbyId("webdb-lblidform2").getTag());  
            
            response.showform("webvariablelist", request);
        }else{
            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }
    }
    public void filltable(NikitaResponse response) {        
        String type = response.getContent().findComponentbyId("webdb-txtconn").getText();
            NikitaConnection nc  ;
            if (NikitaService.isModeCloud()) {
                String cname = response.getContent().findComponentbyId("webdb-txtconn").getText();
                String user = response.getVirtualString("@+SESSION-LOGON-USER");
                String prefix = NikitaService.getPrefixUserCloud(response.getConnection(NikitaConnection.LOGIC), user);
                if (cname.startsWith(prefix) || cname.equalsIgnoreCase("sample") || cname.equalsIgnoreCase("default") ) {
                     nc = response.getConnection(cname);
                }else{
                    nc = response.getConnection("");
                }
            }else{
                nc  =  response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText());
            }
            if (nc.getError().equals("")) {
                Nikitaset nikiset =nc.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT table_name,table_name FROM user_tables ");
                }
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT tbl_name,tbl_name FROM sqlite_master ");
                }
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT TABLE_NAME,TABLE_NAME FROM information_schema.tables ");
                }
                
                response.getContent().findComponentbyId("webdb-comtable").setData(new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "none").setData("text", "Pilih salah satu")));
                if(response.getContent().findComponentbyId("webdb-comtable").getText().equals("")){
                    response.getContent().findComponentbyId("webdb-comtable").setText("none");
                }
                fillcol(response); 
            }
    
//            nc.closeConnection();
    }
    
    public void fillcol(NikitaResponse response) {        
        Nikitaset nikiset =  response.getConnection(response.getContent().findComponentbyId("webdb-txtconn").getText()).QueryPage(1,1, "SELECT ".concat(" * from ").concat(response.getContent().findComponentbyId("webdb-comtable").getText())); 

     

        response .getContent().findComponentbyId("webdb-comfield").setData(new Nset( nikiset.getDataAllHeader() ) );
        response .getContent().findComponentbyId("webdb-comfield2").setData(new Nset( nikiset.getDataAllHeader() ) );
    }
    
    public void fillcolumn(NikitaResponse response, Nset n, int x,Nset argsn) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column");
        String data = "";        
        
        if(x != 0){
            for (int i = 0; i < x; i++) {  
                if(n != null)            
                    data = n.getData(""+i+"").toString();
                
                createGeneratorVariable2(i, verticalLayout,data);
            }  
            for (int i = verticalLayout.getComponentCount()-1; i >= x; i--) {
               verticalLayout.removeComponent(i);
            }
        }else{
            for (int i = 0; i < n.getData().getArraySize(); i++) {    
                Nset v = n.getData(i);

                if(argsn != null)                            
                data = argsn.getData(v.getData("text").toString()).toString();
                
                createGeneratorVariable(i, v,verticalLayout,data);
            }  
            for (int i = verticalLayout.getComponentCount()-1; i >= n.getData().getArraySize(); i--) {
               verticalLayout.removeComponent(i);
            }
        }
        
    }
    
    public void fillcolumn2(NikitaResponse response, Nset n, int x,Nset argsn) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column2");
        String data = "";        
         
            for (int i = 0; i < x; i++) {  
                if(n != null)            
                    data = n.getData(""+i+"").toString();
                
                createGeneratorVariable2(i, verticalLayout,data);
            }  
            for (int i = verticalLayout.getComponentCount()-1; i >= x; i--) {
               verticalLayout.removeComponent(i);
            }
    }
    
    private Component createGeneratorVariable(int i, Nset param,ComponentGroup verticalLayout,String temp){
        Textsmart txt = (Textsmart)verticalLayout.getComponent(i);
        txt.setVisible(true);
        txt.setEnable(true);
        if(!temp.equals(""))
            txt.setText(temp); 
        txt.setLabel(param.getData("text").toString());            
        txt.setTag(param.getData("text").toString());
        txt.setHint("            not used");        
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        });         
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        return horisontalLayout;
    }
    
    
    
    private Component createGeneratorVariable2(int i, ComponentGroup verticalLayout,String temp){
        Textsmart txt = (Textsmart)verticalLayout.getComponent(i);
        txt.setVisible(true);
        txt.setEnable(true);         
        txt.setTag(i+"");
        if(!temp.equals(""))
            txt.setText(temp); 
        
        txt.setLabel("Param ["+(i+1)+"]");
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        });         
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        return horisontalLayout;
    }
    
    public Nset getfillcolumn(NikitaResponse response ) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column");
        Nset result =  Nset.newObject();             
        for (int i = 0; i < verticalLayout.getComponentCount(); i++) {    
            if (!verticalLayout.getComponent(i).getText().equals("")) {
               result.setData(verticalLayout.getComponent(i).getTag(),verticalLayout.getComponent(i).getText()); 
            }           
        }         
        return result;
    }
    
    public Nset getfillcolumn2(NikitaResponse response ) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webdb-column2");
        Nset result =  Nset.newObject();             
        for (int i = 0; i < verticalLayout.getComponentCount(); i++) {    
            if (!verticalLayout.getComponent(i).getTag().equals("")) {
               result.setData(verticalLayout.getComponent(i).getTag(),verticalLayout.getComponent(i).getText()); 
            }           
        }         
        return result;
    }
    
    //condition for mode
    public void condition(String con,String mode,NikitaResponse response,NikitaRequest request) {
        if(!con.equals("")){
            response.getContent().findComponentbyId("webdb-txtconn").setText(con);
            response.getContent().findComponentbyId("webdb-txtmode").setVisible(true);
            if(!mode.equals("pilih salah satu")){
                filltable(response);  
                if(mode.equals("query")){
                    response.getContent().findComponentbyId("webdb-txtquery").setVisible(true);
                    response.getContent().findComponentbyId("webdb-btnparam").setVisible(true);
                    response.getContent().findComponentbyId("webdb-btndone").setText("Save");
                }else if(mode.equals("call")){
                    response.getContent().findComponentbyId("webdb-txtcall").setVisible(true);
                    response.getContent().findComponentbyId("webdb-btnparam").setVisible(true);
                    response.getContent().findComponentbyId("webdb-btndone").setText("Save");
                }else if(mode.equals("insert")){                    
                    response.getContent().findComponentbyId("webdb-comtable").setVisible(true);
                    response.getContent().findComponentbyId("webdb-comwhere").setVisible(false);
                }else if(mode.equals("select")){                    
                    response.getContent().findComponentbyId("webdb-comtable").setVisible(true);
                    response.getContent().findComponentbyId("webdb-conditionorder").setVisible(true);
//                    response.getContent().findComponentbyId("webdb-btndone2").setVisible(true);
                    response.getContent().findComponentbyId("webdb-fields").setVisible(true);
                    response.getContent().findComponentbyId("webdb-fields-sort").setVisible(true);
                    response.getContent().findComponentbyId("webdb-comwhere").setVisible(true);
 
                }else{ 
                    response.getContent().findComponentbyId("webdb-comtable").setVisible(true);
                    response.getContent().findComponentbyId("webdb-comwhere").setVisible(true);
                }
            }            
                
        }               
    }
    
    //condition for where
    public void condition2(NikitaResponse response){
        if(!response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query") || !response.getContent().findComponentbyId("webdb-txtmode").getText().equals("call")){              
            if(response.getContent().findComponentbyId("webdb-comwhere").getText().equals("2")){
                response.getContent().findComponentbyId("webdb-txtwhere").setVisible(true);
                response.getContent().findComponentbyId("webdb-btnparamwhere").setVisible(true);
                response.getContent().findComponentbyId("webdb-comfield").setVisible(false);
                response.getContent().findComponentbyId("webdb-txtfield").setVisible(false);
                response.getContent().findComponentbyId("webdb-comfield2").setVisible(false);
                response.getContent().findComponentbyId("webdb-txtfield2").setVisible(false);
                response.getContent().findComponentbyId("webdb-comlogic").setVisible(false);
            }else if(response.getContent().findComponentbyId("webdb-comwhere").getText().equals("1")){
                response.getContent().findComponentbyId("webdb-txtwhere").setVisible(false);
                response.getContent().findComponentbyId("webdb-btnparamwhere").setVisible(false);
                response.getContent().findComponentbyId("webdb-comfield").setVisible(true);
                response.getContent().findComponentbyId("webdb-txtfield").setVisible(true);
                response.getContent().findComponentbyId("webdb-comfield2").setVisible(true);
                response.getContent().findComponentbyId("webdb-txtfield2").setVisible(true);
                response.getContent().findComponentbyId("webdb-comlogic").setVisible(true);
            }else{
                response.getContent().findComponentbyId("webdb-txtwhere").setVisible(false);
                response.getContent().findComponentbyId("webdb-btnparamwhere").setVisible(false);
                response.getContent().findComponentbyId("webdb-comfield").setVisible(false);
                response.getContent().findComponentbyId("webdb-txtfield").setVisible(false);
                response.getContent().findComponentbyId("webdb-comfield2").setVisible(false);
                response.getContent().findComponentbyId("webdb-txtfield2").setVisible(false);
                response.getContent().findComponentbyId("webdb-comlogic").setVisible(false);
            }
        }
    }
    
    //condition for order by
    public void condition3(NikitaResponse response){
        if(!response.getContent().findComponentbyId("webdb-txtmode").getText().equals("query") || !response.getContent().findComponentbyId("webdb-txtmode").getText().equals("call")){              
            if(response.getContent().findComponentbyId("webdb-conditionorder").getText().equals("3")){
                response.getContent().findComponentbyId("webdb-txtordercustom").setVisible(true);
                response.getContent().findComponentbyId("webdb-orderby").setVisible(false);
                response.getContent().findComponentbyId("webdb-orderby-sort").setVisible(false);
            }else if(response.getContent().findComponentbyId("webdb-conditionorder").getText().equals("1") ||
                    response.getContent().findComponentbyId("webdb-conditionorder").getText().equals("2")){
                response.getContent().findComponentbyId("webdb-txtordercustom").setVisible(false);
                response.getContent().findComponentbyId("webdb-orderby").setVisible(true);
                response.getContent().findComponentbyId("webdb-orderby-sort").setVisible(true);
            }else{                
                response.getContent().findComponentbyId("webdb-txtordercustom").setVisible(false);
                response.getContent().findComponentbyId("webdb-orderby").setVisible(false);
                response.getContent().findComponentbyId("webdb-orderby-sort").setVisible(false);
            }
        }
    }
    
    public void execquery(String x,NikitaResponse response,Nset argsn) { 
        String s1=x;
        int count = 0;
        for(int i=0;i<s1.length();i++) {
            if(s1.charAt(i)=='?'){
                count++;                            
            }
        }
        fillcolumn2(response,argsn, count,argsn);             
    }
}
