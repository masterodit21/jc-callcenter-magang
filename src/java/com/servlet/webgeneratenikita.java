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
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author user
 */
public class webgeneratenikita extends NikitaServlet{
    Nikitaset niki;
    Nikitaset mir;
    NikitaConnection logicx;
    StringBuffer sb = new StringBuffer();
    String xcompid ; 
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Master Gen");
        Nikitaset nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select connname,connname from web_connection ORDER BY connname;");
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout hl = new HorizontalLayout();
        
        
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
                
                   
                
                response.reload(request);
            }
        });         
        nf.addComponent(com);
        
        com = new Combobox();
        com.setId("webcrud-comtable");
        com.setLabel("Table"); 
        com.setText(request.getParameter("tbl"));
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                
                Component comp = response.getContent().findComponentbyId("webcrud-txtformname");
                String formName = response.getContent().findComponentbyId("webcrud-comtable").getText();
                 if(comp.getText().equals("") || comp.getText().equals(formName)){
                     comp.setText(formName);
                 }
                 
                NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText());
                Nikitaset ns = nc.QueryPage(1, 1, "SELECT * FROM "+formName,  null );
                System.err.println("ssssssssss");
                response.getContent().findComponentbyId("webcrud-primary").setData(new Nset(ns.getDataAllHeader()).addData("ROWID"));
//                System.out.println(response.getContent().findComponentbyId("webcrud-primary").getData());
                 
                response.refreshComponent(comp);
                response.refreshComponent(response.getContent().findComponentbyId("webcrud-primary"));
            }
        }); 
  
        nf.addComponent(com);
        
        
        com = new Combobox();
        com.setId("webcrud-primary");
        com.setLabel("Primary"); 
        com.setText(request.getParameter("pk"));
        
        nf.addComponent(com);
        
        
        
        Checkbox cb = new Checkbox();
        cb.setId("webcrud-edit");
        cb.setLabel(" ");
        cb.setData(Nset.readsplitString("Search|Paging|New|Edit|Delete|Detail"));
        cb.setStyle(new Style().setStyle("n-cols", "1"));
        cb.setText(cb.getData().toJSON());
        
        nf.addComponent(cb);
        
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
        btn.setId("webcrud-btngenerate");
        btn.setText("Generate");
        Style style = new Style();
        style.setStyle("width", "100px");
        style.setStyle("height", "34px");
        btn.setStyle(style);
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
//                Nset n = Nset.readJSON(response.getContent().findComponentbyId("webcrud-edit").getText());
//                if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New"))
//                    System.out.println("in");
//                else
//                    System.out.println("out");
                response.getContent().findComponentbyId("webcrud-btngenerate").setEnable(false);
                response.refreshComponent("webcrud-btngenerate");
                response.callWait();
                response.showDialog("Generate", "Do you want to generate?", "generate", "No", "Yes");                                     
                
                
            }
        });
        
        nf.addComponent(btn);
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {

            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("generate") && responsecode.equals("button2")){      
                    logicx = response.getConnection(NikitaConnection.LOGIC);
                    
                    
                    //connection databases
                    Component conn = response.getContent().findComponentbyId("webcrud-txtconn");               
                    //form name yang diinput/yg akan dibuat
                    String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText();                    
                    
                    //formindex
                    int index = Utility.getInt( logicx.Query("select MAX(formindex) from web_form ").getText(0, 0) );
                    //formname
                    String xformname = logicx.Query("select formname from web_form WHERE formname = ? ",formName).getText(0, 0) ;
                    String xflag = "";
                    String xformidadd = "uncheck";
                    //kondisi ketika ada yang sama formnamenya
                    if(!xformname.equals(formName)){            
                        //insert form main
                        niki = logicx.Query("INSERT INTO web_form(formname,formtitle,formtype,formindex) "+ 
                                                        "VALUES(?,?,?,?)",formName,formName,"webform",(index+1)+"");                        

                        //select formid form main
                        String xformid = logicx.Query("select MAX(formid) from web_form ").getText(0, 0) ;
                        
                        if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                            //insert form main
                            niki = logicx.Query("INSERT INTO web_form(formname,formtitle,formtype,formindex) "+ 
                                                            "VALUES(?,?,?,?)",formName+"_add",formName+"_add","webform",(index+1)+"");                        

                            //select formid form main
                            xformidadd = logicx.Query("select MAX(formid) from web_form ").getText(0, 0) ;
                        }
                        insertcomp(xformid,xformidadd,response);
                        
                        response.getContent().findComponentbyId("webcrud-btngenerate").setEnable(true);
                        response.refreshComponent("webcrud-btngenerate");
                        response.showDialog("Information","Complete!","", "OK"); 
                    }else{      
                        response.getContent().findComponentbyId("webcrud-btngenerate").setEnable(true);
                        response.refreshComponent("webcrud-btngenerate");
                        response.showDialog("Information","Form Name is exsist","", "OK");  
                    }
                                      
                }else if(reqestcode.equals("generate") ){     
                     response.getContent().findComponentbyId("webcrud-btngenerate").setEnable(true);
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
                
                filltable(response);  
                     
                    
            }
        }); 
        
        nf.setStyle(new Style().setStyle("width", "390").setStyle("height", "380"));
        response.setContent(nf);
    }
    
    public void filltable(NikitaResponse response) {        
        String type = response.getContent().findComponentbyId("webcrud-txtconn").getText();
            NikitaConnection nc  =  response.getConnection(response.getContent().findComponentbyId("webcrud-txtconn").getText());
            if (nc.getError().equals("")) {
                Nikitaset nikiset =nc.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT table_name,table_name FROM user_tables ");
                }
                response.getContent().findComponentbyId("webcrud-comtable").setData(new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text"));
                
            }
    
            nc.closeConnection();
    }
    
    //=======================INSERT COMPONENT
    public void insertcomp(String xformid,String xformidadd,NikitaResponse response ) {        
        //============================================FORM MAIN
        //load
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                              "VALUES(?,?,?,?,?,?)",xformid,"load",1+"","navload",1+"",1+"");
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"load").getText(0, 0) ;   
        insertroute(xcompid,"load","",response);
        //result
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                              "VALUES(?,?,?,?,?,?)",xformid,"result",3+"","navresult",1+"",1+"");
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"result").getText(0, 0) ; 
        insertroute(xcompid,"result","",response);
        
        //horizontal
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                              "VALUES(?,?,?,?,?,?)",xformid,"horizontal",2+"","horizontallayout",1+"",1+"");
        
        //textsearch
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,complabel,compstyle,parent,enable,visible ) "+ 
                              "VALUES(?,?,?,?,?,?,?,?,?)",xformid,"textsearch",4+"","text","Searching","n-searchicon:true","$horizontal",1+"",1+"");
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"textsearch").getText(0, 0) ; 
        insertroute(xcompid,"textsearch","",response);
        
              
        
        
        if(!xformidadd.equals("uncheck")){
            //IF ADD/EDIT
            String conn = response.getContent().findComponentbyId("webcrud-txtconn").getText();
            String tbl = response.getContent().findComponentbyId("webcrud-comtable").getText();
            String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText();
            //select field                    
            mir =  response.getConnection(conn).
                    QueryPage(1,1, "SELECT ".concat(" * from ").concat(tbl));

            //loadadd
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?)",xformidadd,"load",1+"","navload",1+"",1+"");        
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"load").getText(0, 0) ; 
            insertroute(xcompid,"loadadd","",response);

            //result
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?)",xformidadd,"result",2+"","navresult",1+"",1+"");       
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"result").getText(0, 0) ; 
            insertroute(xcompid,"resultadd","",response);

            //lblhidden
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?,?)",xformidadd,"lblhidden",3+"","label","@from",0+"",0+"");
            //lblid
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?,?)",xformidadd,"lblid",4+"","label","@"+mir.getDataAllHeader().get(0),0+"",0+"");
            int x = 4;
            for (int i = 2; i <= mir.getDataAllHeader().size(); i++) {                
                    niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,complabel,comptext,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,?)", xformidadd,mir.getDataAllHeader().get(i-1),(x+1)+"","txt",mir.getDataAllHeader().get(i-1),"@"+mir.getDataAllHeader().get(i-1),1+"",1+"");
                }  

            //btnsave
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?,?)",xformidadd,"btnsave",(mir.getDataAllHeader().size()+5)+"","button","Save",1+"",1+"");
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformidadd,"btnsave").getText(0, 0) ; 

            System.out.println(xcompid);
            insertroute(xcompid,"btnsave","",response);

            //imgadd
            niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,comptext,parent,enable,visible ) "+ 
                                  "VALUES(?,?,?,?,?,?,?,?)",xformid,"imgadd",5+"","image","img/add.png","$horizontal",1+"",1+"");  
            xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"imgadd").getText(0, 0) ; 
            insertroute(xcompid,"imgadd",xformidadd,response);
        }
        
        //grid
        niki = logicx.Query("INSERT INTO web_component(formid,compname,compindex,comptype,enable,visible ) "+ 
                              "VALUES(?,?,?,?,?,?)",xformid,"grid",6+"","tablegrid",0+"",1+"");
        xcompid = logicx.Query("select compid from web_component WHERE formid = ? AND compname = ? ",xformid,"grid").getText(0, 0) ; 
        insertroute(xcompid,"grid",xformidadd,response);
        
        
    }
    
    public void insertroute(String xcompid,String xcompname,String xformidadd, NikitaResponse response) {
        String conn = response.getContent().findComponentbyId("webcrud-txtconn").getText();
        String tbl = response.getContent().findComponentbyId("webcrud-comtable").getText();
        String formName = response.getContent().findComponentbyId("webcrud-txtformname").getText();
        //select field                    
        mir =  response.getConnection(conn).
                QueryPage(1,1, "SELECT ".concat(" * from ").concat(tbl));

        String allfield;
        String ID = mir.getDataAllHeader().get(0);
        String field2 = mir.getDataAllHeader().get(1);        
                    
        String exp;        
        exp = "{\"result\":\"\",\"args\":{},\"code\":\"\",\"class\":\"TrueExpression\",\"id\":\"105\"}";
        String act;
        //load
        if(xcompname.equals("load")){
            act = "{\"args\":{\"param5\":\"\",\"param4\":\"\",\"param3\":\"%\",\"param2\":\"$textsearch\",\"param1\":\"%\",\"param9\":\"\","
                    + "\"param8\":\"\",\"param7\":\"\",\"result\":\"@searchs\",\"param6\":\"\"},\"code\":\"concat\",\"class\":\"StringManipulationAction\","
                    + "\"id\":\"1\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,1+"",exp,act);

            sb = new StringBuffer();
            for (int i = 1; i <= mir.getDataAllHeader().size(); i++) {
                sb.append("\\\\\\\"").append(mir.getDataAllHeader().get(i-1)).append("\\\\\\\"");
                if(mir.getDataAllHeader().size() != i){
                    sb.append(",");
                }
            }        
            allfield = sb.toString();
            act = "{\"args\":{\"result\":\"@tbl\",\"param4\":\"$grid\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                    + "\\\"argswhere\\\":{\\\"0\\\":\\\"@searchs\\\"},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":"
                    + "{\\\"parameter2\\\":\\\"\\\",\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"2\\\","
                    + "\\\"sqlwhere\\\":\\\""+field2+" like ? ORDER BY "+field2+"\\\",\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\","
                    + "\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\",\\\"fields\\\":\\\"["+allfield+"]\\\",\\\"args\\\":{},"
                    + "\\\"dbmode\\\":\\\"select\\\","
                    + "\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,2+"",act);
            
            String delete = "";
            String edit = "";
            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Delete")){
                delete = "[remove]";
            }if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit") &&
                    response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                edit = "[edit]";
            }
            sb = new StringBuffer();
            for (int i = 2; i <= mir.getDataAllHeader().size(); i++) {
                sb.append(mir.getDataAllHeader().get(i-1)).append(",");
            }        
            allfield = sb.toString();
            act = "{\"args\":{\"param4\":\"[#],"+allfield+edit+delete+"\",\"param3\":\"\",\"param2\":\"@tbl\",\"param1\":\"$grid\"},"
                    + "\"code\":\"inflate\",\"class\":\"FormAction\",\"id\":\"19\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,3+"",act);

            act = "{\"args\":{\"result\":\"\",\"param6\":\"@tbl\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"@tbl\"},\"code\":\"nikitaset\","
                    + "\"class\":\"DataAction\",\"id\":\"11\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,4+"",act);

            act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"@tbl\",\"param1\":\"write\"},\"code\":\"\",\"class\":\"JsonAction\",\"id\":\"26\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,5+"",act);

            act = "{\"args\":{\"param3\":\"@data\",\"param2\":\"$grid\",\"param1\":\"settag\"},\"code\":\"component\","
                    + "\"class\":\"ComponentAction\",\"id\":\"25\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,5+"",act);
        }else if(xcompname.equals("result")){
            
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,3+"",exp,act);
            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Delete")){
                act = "{\"args\":{\"param6\":\"\",\"param5\":\"@data\",\"param4\":\"\",\"param3\":\"0\",\"param2\":\"@+RESULT\","
                        + "\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,1+"",exp,act);       

                exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"remove\",\"paramb\":\"AND\",\"param3\":\"@+REQUESTCODE\","
                        + "\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\",\"param6\":\"\",\"paramd\":\"\"},"
                        + "\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"@data\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"1\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\""+ID+"\\\",\\\"parameter1\\\":\\\""+ID+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{},\\\"dbmode\\\":\\\"delete\\\",\\\"sql\\\":\\\"\\\"}\",\"param1\":\"\"},"
                        + "\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,2+"",exp,act); 
            }
            
            
        }else if(xcompname.equals("textsearch")){            
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,1+"",exp,act);  
            
        }else if(xcompname.equals("imgadd")){            
            
            sb = new StringBuffer();
            for (int i = 1; i <= mir.getDataAllHeader().size(); i++) {           
                sb.append("\\\"").append(mir.getDataAllHeader().get(i-1)).append("\\\":\\\"@").append(mir.getDataAllHeader().get(i-1)).append("\\\",");
                
            }                  
            allfield = sb.toString();
            
            act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"modal\",\"param1\":\"{\\\"args\\\":{"+allfield+"\\\"from\\\":\\\"\\\"},"
                    + "\\\"formid\\\":\\\""+xformidadd+"\\\",\\\"formname\\\":\\\""+formName+"_add\\\"}\"},\"code\":\"showform\","
                    + "\"class\":\"FormAction\",\"id\":\"3\"}";
            
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,1+"",exp,act); 
            
        }else if(xcompname.equals("grid")){            
            act = "{\"args\":{\"param3\":\"@datagrid\",\"param2\":\"$grid[tag]\",\"param1\":\"read\"},\"code\":\"\",\"class\":\"JsonAction\","
                    + "\"id\":\"26\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,3+"",exp,act);  
            
            exp = "{\"result\":\"\",\"args\":{},\"code\":\"filter\",\"class\":\"BooleanExpression\",\"id\":\"116\"}";
            act = "{\"args\":{\"param1\":\"$load\"},\"code\":\"calllogic\",\"class\":\"FormAction\",\"id\":\"45\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,1+"",exp,act);
            
            act = "{\"args\":{},\"code\":\"break\",\"class\":\"SystemAction\",\"id\":\"24\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,2+"",act);
            
            
            act = "{\"args\":{\"param6\":\"\",\"param5\":\"\",\"param4\":\"@datagrid\",\"param3\":\"@+SELECTEDROW\",\"param2\":\"@datagrid\","
                    + "\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                  "VALUES(?,?,?)",xcompid,4+"",act);
            
            int x=4;
            for (int i = 1; i <= mir.getDataAllHeader().size(); i++) {                
                act = "{\"args\":{\"param6\":\"\",\"param5\":\"@"+mir.getDataAllHeader().get(i-1)+"\",\"param4\":\"\",\"param3\":\""+(i-1)+"\","
                        + "\"param2\":\"@datagrid\",\"param1\":\"getbyindex\"},\"code\":\"get\",\"class\":\"VariableAction\",\"id\":\"44\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                      "VALUES(?,?,?)",xcompid,(x+i)+"",act);
            }  
            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Delete")){  
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"remove\",\"param1\":\"@+BUTTONGRID\"},\"code\":\"equal\","
                        + "\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"reqcode1\":\"@+BUTTONGRID\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to delete?\","
                        + "\"data\":\"@datagrid\",\"title\":\"Delete\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,(mir.getDataAllHeader().size()+5)+"",exp,act);
            }
            
            
            sb = new StringBuffer();
            for (int i = 1; i <= mir.getDataAllHeader().size(); i++) {           
                sb.append("\\\"").append(mir.getDataAllHeader().get(i-1)).append("\\\":\\\"@").append(mir.getDataAllHeader().get(i-1)).append("\\\",");
                
            }                  
            allfield = sb.toString();
            
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit") &&
                    response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){                
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"@+BUTTONGRID\"},"
                        + "\"code\":\"equal\",\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param4\":\"\",\"param3\":\"\",\"param2\":\"modal\",\"param1\":\"{\\\"args\\\":{"+allfield+"\\\"from\\\":\\\"edit\\\"},"
                        + "\\\"formid\\\":\\\""+xformidadd+"\\\",\\\"formname\\\":\\\""+formName+"_add\\\"}\"},\"code\":\"showform\","
                        + "\"class\":\"FormAction\",\"id\":\"3\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,(mir.getDataAllHeader().size()+6)+"",exp,act);
            }
            
        }else if(xcompname.equals("loadadd")){               
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit") &&
                    response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"$lblhidden\"},\"code\":\"equal\","
                        + "\"class\":\"StringComparationExpression\",\"id\":\"100\"}";
                act = "{\"args\":{\"param2\":\"Update\",\"param1\":\"$btnsave\"},\"code\":\"settext\",\"class\":\"ComponentAction\",\"id\":\"2\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,1+"",exp,act);
            }
        }else if(xcompname.equals("btnsave")){  
            System.out.println(xcompid);
            exp = "{\"result\":\"\",\"args\":{\"param2\":\"edit\",\"param1\":\"$lblhidden\"},\"code\":\"notequal\","
                    + "\"class\":\"StringComparationExpression\",\"id\":\"101\"}";
            act = "{\"args\":{\"reqcode1\":\"add\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to save?\","
                    + "\"data\":\"\",\"title\":\"Save\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,1+"",exp,act);
            
            exp = "{\"result\":\"\",\"args\":{},\"code\":\"else\",\"class\":\"BooleanExpression\",\"id\":\"113\"}";
            act = "{\"args\":{\"reqcode1\":\"update\",\"button2\":\"Yes\",\"button1\":\"No\",\"message\":\"Do you want to update?\","
                    + "\"data\":\"\",\"title\":\"Update\"},\"code\":\"dialog\",\"class\":\"ShowDialogAction\",\"id\":\"37\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                  "VALUES(?,?,?,?)",xcompid,2+"",exp,act);
        }else if(xcompname.equals("resultadd")){  
            //add
            sb = new StringBuffer();
            for (int i = 2; i <= mir.getDataAllHeader().size(); i++) {           
                sb.append("\\\"").append(mir.getDataAllHeader().get(i-1)).append("\\\":\\\"$").append(mir.getDataAllHeader().get(i-1)).append("\\\"");
                if(mir.getDataAllHeader().size() != i){
                    sb.append(",");
                }
            }                  
            allfield = sb.toString();
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){
                exp = "{\"result\":\"@add\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"add\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+REQUESTCODE\",\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"@R\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"0\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\""+mir.getDataAllHeader().get(0)+"\\\",\\\"parameter1\\\":\\\""+mir.getDataAllHeader().get(0)+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{"+allfield+"},\\\"dbmode\\\":\\\"insert\\\",\\\"sql\\\":\\\"\\\"}\","
                        + "\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,1+"",exp,act);

                exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"<>\",\"param4\":\"button1\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+RESPONSECODE\",\"parama\":\"\",\"param2\":\"add\",\"param1\":\"@+REQUESTCODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param2\":\"\",\"param1\":\"add\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,2+"",exp,act);

                act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                      "VALUES(?,?,?)",xcompid,3+"",act);
            }
            //update
            if(response.getContent().findComponentbyId("webcrud-edit").getText().contains("Edit") &&
                    response.getContent().findComponentbyId("webcrud-edit").getText().contains("New")){           
                exp = "{\"result\":\"@update\",\"args\":{\"param5\":\"\",\"paramc\":\"\",\"param4\":\"update\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+REQUESTCODE\",\"parama\":\"\",\"param2\":\"button2\",\"param1\":\"@+RESPONSECODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"result\":\"@R2\",\"param4\":\"\",\"param3\":\"\",\"param2\":\"{\\\"paramid\\\":\\\"webaction-gen-1\\\","
                        + "\\\"argswhere\\\":{},\\\"conn\\\":\\\""+conn+"\\\",\\\"where\\\":{\\\"paramargs\\\":{\\\"parameter2\\\":\\\"\\\","
                        + "\\\"parameter1\\\":\\\"$lblid\\\"},\\\"logic\\\":\\\"0\\\",\\\"type\\\":\\\"1\\\",\\\"sqlwhere\\\":\\\"\\\","
                        + "\\\"param\\\":{\\\"parameter2\\\":\\\""+mir.getDataAllHeader().get(0)+"\\\",\\\"parameter1\\\":\\\""+mir.getDataAllHeader().get(0)+"\\\"}},\\\"tbl\\\":\\\""+tbl+"\\\","
                        + "\\\"fields\\\":\\\"null\\\",\\\"args\\\":{"+allfield+"},\\\"dbmode\\\":\\\"update\\\",\\\"sql\\\":\\\"\\\"}\","
                        + "\"param1\":\"\"},\"code\":\"query\",\"class\":\"ConnectionAction\",\"id\":\"17\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,4+"",exp,act);


                exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"<>\",\"param4\":\"button1\",\"paramb\":\"AND\","
                        + "\"param3\":\"@+RESPONSECODE\",\"parama\":\"\",\"param2\":\"update\",\"param1\":\"@+REQUESTCODE\","
                        + "\"param6\":\"\",\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
                act = "{\"args\":{\"param2\":\"\",\"param1\":\"update\"},\"code\":\"setresult\",\"class\":\"DataAction\",\"id\":\"47\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression,action) "+ 
                                      "VALUES(?,?,?,?)",xcompid,5+"",exp,act);

                act = "{\"args\":{\"param1\":\"\"},\"code\":\"closeform\",\"class\":\"FormAction\",\"id\":\"13\"}";
                niki = logicx.Query("INSERT INTO web_route(compid,routeindex,action) "+ 
                                      "VALUES(?,?,?)",xcompid,6+"",act);
            }

            //orr            
            exp = "{\"result\":\"\",\"args\":{\"param5\":\"\",\"paramc\":\"<>\",\"param4\":\"true\",\"paramb\":\"OR\","
                    + "\"param3\":\"@update\",\"parama\":\"<>\",\"param2\":\"true\",\"param1\":\"@add\",\"param6\":\"\","
                    + "\"paramd\":\"\"},\"code\":\"iif\",\"class\":\"BooleanExpression\",\"id\":\"117\"}";
            niki = logicx.Query("INSERT INTO web_route(compid,routeindex,expression) "+ 
                                  "VALUES(?,?,?)",xcompid,7+"",exp);
        }
        
    }
    
    
}
