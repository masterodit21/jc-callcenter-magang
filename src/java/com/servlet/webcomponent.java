/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Vector;

/**
 *
 * @author user
 */
public class webcomponent extends NikitaServlet{
   private static final String ALL = "*";
    /*parameter
    mode
    code
    search
    formname
    formid
    name
    id
    data
    */

    Label condition;
    String user ;
    private Nset componentName = Nset.newObject();
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Component");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Style style = new Style();
        
        nf.setStyle(new Style().setStyle("width", "1092").setStyle("height", "606"));
       
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));        
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        final Label buffered = new Label();
        buffered.setId("comp-buffered");
        buffered.setVisible(false);
        nf.addComponent(buffered);
        
        
        condition = new Label();
        condition.setId("comp-lblcreatedby");
        condition.setTag(request.getParameter("created"));     
        condition.setText(request.getParameter("unlock"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        Label lbltype = new Label();
        lbltype.setId("formtype");
        lbltype.setTag(request.getParameter("formtype"));        
        lbltype.setVisible(false);
        nf.addComponent(lbltype);
        
        Textbox txt = new Textsmart();
        txt.setId("comp-txtFinder");
        txt.setLabel("Form Name");
        txt.setText(request.getParameter("formname"));
        txt.setTag(request.getParameter("formid"));
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("comp-btnFinder");
        btn.setText("Finder");
        btn.setVisible(false);
        horisontalLayout.addComponent(btn);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("comp-txtFinder").getText() );
                response.showform("webform", request,"form",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        horisontalLayout = new HorizontalLayout();
        txt = new Textsmart();
        txt.setId("comp-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        
        
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
               
                response.refreshComponent(response.getContent().findComponentbyId( "comp-btnAdd"));      
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
        
        Component img = new Image();
        img.setId("comp-btnAdd");
        img.setText("img/add.png");
        style = new Style();
        style.setStyle("margin-top", "3px").setAttr("n-layout-align", "left");
        img.setStyle(style);
        
        horisontalLayout.addComponent(img);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    request.setParameter("mode","add");
                    request.setParameter("formName", response.getContent().findComponentbyId("comp-txtFinder").getText());
                    request.setParameter("formId", response.getContent().findComponentbyId("comp-txtFinder").getTag());                    
                    response.showform("webcomponentadd", request,"add", true);
                    response.write();
                }else{                    
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to add new", "warning",null, "OK", "");
                }
            }
        });   
        
        
        img = new Image();
        img.setId("comp-btnHierarki");
        img.setText("img/hierarki.png");
        img.setVisible(true);
        style = new Style();
        style.setStyle("margin-top", "3px").setAttr("n-layout-align", "left");
        img.setStyle(style);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //webformhierarki
                request.setParameter("fname", response.getContent().findComponentbyId("comp-txtFinder").getText());
                request.setParameter("fid", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                response.showformGen( "webformhierarki" , request, "", false);
                response.write();

            }
        });
        horisontalLayout.addComponent(img);
        
        
        //add right forparent
        Combobox combo = new Combobox();
        combo.setId("comp-parent");
        combo.setLabel("Parent");
        combo.setText(ALL);
        combo.setStyle(new Style().setStyle("n-searchicon", "true").setAttr("n-layout-align", "right").setStyle("n-label-width", "80px").setStyle("width", "200px"));
        combo.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);     
                response.refreshComponent(response.getContent().findComponentbyId( "comp-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(horisontalLayout);
        hr.addComponent(combo);
        
         
                
                
        img = new Image();
        img.setId("comp-btnPlay");
        img.setText("img/play.png");
        img.setVisible(true);
        style = new Style();
        style.setStyle("margin-left", "8px").setStyle("width", "24px").setStyle("height", "24px") ;
        img.setStyle(style);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                String type = response.getContent().findComponentbyId("formtype").getTag();
                if (type.equals("mobileform")||type.equals("form")) {
                    
                    Nset n = Nset.newObject();
                    n.setData("fid", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    n.setData("fname", response.getContent().findComponentbyId("comp-txtFinder").getText());
                    n.setData("mode", "play");
                    String reform = n.toJSON();
                    Nikitaset ns = nikitaConnection.Query("SELECT imei FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
                    
                    String split = ",";
                    if (ns.getText(0, 0).contains("|")) {
                        split = "|";
                    }else if (ns.getText(0, 0).contains(";")) {
                        split = ";";
                    }
                    Vector<String> imeis = Utility.splitVector(ns.getText(0, 0), split);
                    for (int i = 0; i < imeis.size(); i++) {
                        String imei =  imeis.get(i).trim();
                        //nikitaConnection.Query("INSERT INTO sys_debug (code, request, response, result, lastlog, status) VALUES (?, ?, ?, ?, ?, ?) ", "MOBILE-PLAY-"+imei, reform, "", "", Utility.Now(), "0");
                        //nikitaConnection.Query("UPDATE sys_debug SET lastlog=?,request=?,status='0' WHERE code=?", Utility.Now(),reform,"MOBILE-PLAY-"+imei);
                        //response.sendChat("*MOBILE-PLAY-"+imei+":");
                        response.sendBroadcastListener("MOBILE-PLAY-"+imei, n);
                    }
                }
                
                if(type.equals("webform")||type.equals("form")) {
                    response.showformGen( response.getContent().findComponentbyId("comp-txtFinder").getTag() , request, "", true);                 
                }else if(type.equals("mobileform")) {
                    String bfn = component.getBaseUrl("/base/"+response.getContent().findComponentbyId("comp-txtFinder").getTag()+"/"+response.getContent().findComponentbyId("comp-txtFinder").getText());
                    request.setParameter("url", bfn) ;
                    response.showformGen( "webbrowser" , request, "", true);
                }else if(type.equals("link")) {
                    request.setParameter("fid", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    request.setParameter("fname",response.getContent().findComponentbyId("comp-txtFinder").getText() );
                    response.showformGen("webargument", request, "", true);                    
                }
                    
                
                response.write();
            }
        });
        hr.addComponent(img);
        
        
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        
        
       
        nf.addComponent(hr);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("comp-tblComponent");
        tablegrid.setDataHeader(Nset.readsplitString("No,Form Id,Name,Index,Type,Label,Text,Hint,Parent,List,Style,Enable,Visible,Mandatory,Default,",","));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(7, true);
        tablegrid.setColHide(9, true);
        tablegrid.setColHide(13, true);
        tablegrid.setColHide(14, true);
        tablegrid.setStyle(new Style().setStyle("n-ondblclick", "true"));
        
        nf.addComponent(tablegrid);  
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                request.setParameter("idcomp",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString()   ); 
                request.setParameter("idform",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()   ); 
                request.setParameter("mode","logiclist");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }
                request.setParameter("created",condition.getTag());
                response.showform("weblogic", request,"edit", true);
                response.write();
            }
        });
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(response.getContent().findComponentbyId("comp-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
    
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("add") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("edit") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("copy") && responsecode.equals("NEWFORM")){
                    
                    
                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("comp-txtFinder").getTag());
     
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=compindex+2 WHERE compindex > ? AND formid=?", user,result.getData("index").toString(),result.getData("formid").toString());                
                    nikitaConnection.Query("INSERT INTO web_component(visible,enable,comptype,compindex,formid,createdby,createddate) VALUES(1,1,'',?,?,?,"+WebUtility.getDBDate(dbCore)+") ", (result.getData("index").toInteger()+1)+"",result.getData("formid").toString(),user);
                     
                    Nikitaset n = nikitaConnection.Query("SELECT MAX(compid) FROM web_component");
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", n.getText(0, 0));
                    data.setData("activitytype", "component");
                    data.setData("mode", "add");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    fillGrid(response);
                    response.writeContent();
               
                }
                
                if(reqestcode.equals("copy") && responsecode.equals("copy-comp")){
                
                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("comp-buffered").getTag());
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=compindex+2+"+result.getArraySize()+" WHERE compindex > ? AND formid=?",user,nset.getData(3).toString(),nset.getData(1).toString());                
                    
                    for (int i = 0; i < result.getArraySize(); i++) {
                        Nikitaset nikitaset  = nikitaConnection.QueryPage(1, 1, "SELECT compname,comptype,complabel,comptext,comphint,"
                                + "compdefault,complist,compstyle,enable,visible,mandatory,parent FROM web_component WHERE compid=?",result.getData(i).toString());
                        nikitaConnection.Query("INSERT INTO web_component("
                                + "compindex,formid,compname,comptype,complabel,comptext,comphint,"
                                + "compdefault,complist,compstyle,enable,visible,mandatory,parent,createdby,createddate) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+") ", (nset.getData(3).toInteger()+i+1)+"",nset.getData(1).toString(),nikitaset.getText(0, 0),nikitaset.getText(0, 1),nikitaset.getText(0, 2),nikitaset.getText(0, 3),nikitaset.getText(0, 4),nikitaset.getText(0, 5),nikitaset.getText(0, 6),nikitaset.getText(0, 7),nikitaset.getText(0, 8),nikitaset.getText(0, 9),nikitaset.getText(0, 10),nikitaset.getText(0, 11),user);
                    }
                    fillGrid(response);
                    response.writeContent();
                  
                }     
                if(reqestcode.equals("form") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("comp-txtFinder").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("comp-txtFinder").setTag(result.getData("id").toString());
                    response.getContent().findComponentbyId("formtype").setTag(result.getData("type").toString());                    
                    
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button3")){
                    
                    Nikitaset formaccess2 = nikitaConnection.Query("SELECT compid FROM web_route WHERE action LIKE ? OR action LIKE ? AND compid IN (SELECT compid FROM web_component WHERE formid = ?) ", result.getData("compname").toString(), result.getData("compname2").toString(),result.getData("idform").toString()); 
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < formaccess2.getRows() ; i++) {
                        sb.append(formaccess2.getText(i, 0));
                        if(formaccess2.getRows() != (i+1)){
                            sb.append(",");
                        }
                    }
                    String x=sb.toString();
                    
                    formaccess2 = nikitaConnection.Query("SELECT compname FROM web_component WHERE compid IN ("+x+")");                      
                    sb = new StringBuffer();
                    for (int i = 0; i < formaccess2.getRows() ; i++) {
                        sb.append(formaccess2.getText(i, 0));
                        if(formaccess2.getRows() != (i+1)){
                            sb.append(",");
                        }
                    }
                    x=sb.toString();
                    response.showDialogResult("Information ", x , "",null, "OK", "");
                    response.write();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    
                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    
                    Nikitaset n = nikitaConnection.Query("SELECT compname FROM web_component where compid=?",result.getData("id").toString());
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", n.getText(0, 0));
                    data.setData("activitytype", "component");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    nikitaConnection.Query("delete from web_component where compid=?",result.getData("id").toString());
                    
                    nikitaConnection.Query("delete from web_route where compid=?)",result.getData("id").toString());
                     
                   
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
                
        Component comp = new Component();
        comp.setId("comp-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }
                request.setParameter("created",condition.getTag());
                response.showform("webcomponentadd", request,"edit", true);
                response.write();

            }
        });        
        
        
        
        comp = new Component();
        comp.setId("comp-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    String x = "%\"$"+nset.getData(2).toString()+"\"%"; 
                    String y = "%\"$"+nset.getData(2).toString()+"\\\\%"; 
                    Nikitaset formaccess1 = nikitaConnection.Query("SELECT routeid FROM web_route WHERE action LIKE ? OR action LIKE ? AND compid IN (SELECT compid FROM web_component WHERE formid = ?) ", x,y,nset.getData(1).toString()); 
                    if(formaccess1.getRows() > 0){
                        response.showDialogResult("Warning ", "There is another component that access this component", "delete",Nset.newObject().setData("id",nset.getData(0).toString() ).setData("idform",nset.getData(1).toString() ).setData("compname", x).setData("compname2", y), "Cancel", "Delete","See Comp");
                    }else{
                        response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",nset.getData(0).toString() ), "No", "Yes");
                    }
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to delete", "warning",null, "OK", "");
                }
                response.write();

            }
        });
        
        comp = new Component();
        comp.setId("comp-btnInsert");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(component.getTag());
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    response.getContent().findComponentbyId("comp-buffered").setTag(component.getTag());
                    response.refreshComponent(response.getContent().findComponentbyId("comp-buffered"));
                    /*
                    Nset N = Nset.newObject();
                    N.setData("formid", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    N.setData("index", nset.getData(3).toString());
                    N.setData("formname", "");
                    
                    response.getContent().getOnActionResultListener().OnResult(request, response, component, "copy", "NEWFORM", N);
                    */
                   
                    request.setParameter("mode","add");
                    request.setParameter("formindex", nset.getData(3).toString());
                    request.setParameter("formName", response.getContent().findComponentbyId("comp-txtFinder").getText());
                    request.setParameter("formId", response.getContent().findComponentbyId("comp-txtFinder").getTag());                    
                    response.showform("webcomponentadd", request,"add", true);
                    response.write();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to copy", "warning",null, "OK", "");
                }
            }
        });
        
        comp = new Component();
        comp.setId("comp-btnCopy");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(component.getTag());
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    response.getContent().findComponentbyId("comp-buffered").setTag(component.getTag());
                    response.refreshComponent(response.getContent().findComponentbyId("comp-buffered"));
                    request.setParameter("idform", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    request.setParameter("index", nset.getData(3).toString());
                    request.setParameter("formname", "");
                    response.showform("webcompcopy", request,"copy", true);             
                    response.write();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to copy", "warning",null, "OK", "");
                }
            }
        });
        
        comp = new Component();
        comp.setId("comp-btnUp");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 

                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    
                    Nikitaset nikiset = nikitaConnection.Query("select compid,compindex from web_component WHERE compindex < ? AND formid = ? ORDER BY compindex DESC,compid DESC", nset.getData(3).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=? WHERE compid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=? WHERE compid =? ", user,nset.getData(3).toString(),nikiset.getText(0, 0)); 

                    fillGrid(response);
                    response.writeContent();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to change", "warning",null, "OK", "");
                }
                
            }
        });
        
        comp = new Component();
        comp.setId("comp-btnDown");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){  

                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    
                    Nikitaset nikiset = nikitaConnection.Query("select compid,compindex from web_component WHERE compindex > ? AND formid = ? ORDER BY compindex ASC,compid ASC", nset.getData(3).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=? WHERE compid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());
                    nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=? WHERE compid =? ", user,nset.getData(3).toString(),nikiset.getText(0, 0)); 

                    fillGrid(response);
                    response.writeContent();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to change", "warning",null, "OK", "");
                }

            }
        });
        if(lblmode.getTag().equals("comp")){     
            nf.setText("Component ["+nf.findComponentbyId("comp-txtFinder").getText()+"]"); 
            nf.findComponentbyId("comp-txtFinder").setVisible(false);
        }
        else{            
            nf.setText("Component");       
        }
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                response.getContent().findComponentbyId("comp-btnFinder").setTag(request.getParameter("mode"));
                
                if(!response.getContent().findComponentbyId("comp-txtFinder").getTag().equals("")){
                    fillGrid(response);
                }
                else{     
                    Component txtFinder= response.getContent().findComponentbyId("comp-btnFinder");               
                    if(txtFinder.getTag().equals("comp"))
                        txtFinder.setVisible(false);
                    
                    if (response.getContent().findComponentbyId("comp-txtFinder").getTag().equals("")) {
                        response.getContent().findComponentbyId("comp-btnAdd").setVisible(false);
                    }
                }
                
                    
                
            }
        });
        
        Nikitaset nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select compcode,comptitle from all_component_list ORDER BY comptitle;");
        for (int i = 0; i < nikiset.getRows(); i++) {
            componentName.setData(nikiset.getText(i, 0), nikiset.getText(i, 1));
        }        
    }
    
    public void fillGrid(NikitaResponse response){
        Combobox combo = (Combobox)response.getContent().findComponentbyId("comp-parent") ;  
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("comp-tblComponent") ;
        Component cmpBtnAdd =response.getContent().findComponentbyId("comp-btnAdd") ;
        Textbox txtSearch= (Textbox)response.getContent().findComponentbyId("comp-txtSearch");
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("comp-txtFinder");
        Component mode= response.getContent().findComponentbyId("comp-btnFinder");
        
        String mainquery = "select compid,formid,compname,compindex,comptype,complabel,comptext,comphint,parent,complist,compstyle,enable,visible,mandatory,compdefault,validation,1 from web_component ";
        
        if(mode.getTag().equals("comp"))
            mode.setVisible(false);
                
        Nikitaset nikiset = null;
        if(txtFinder.getText().equals("")){
            if (combo.getText().equals(ALL)) {
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),mainquery+"WHERE formid=? ORDER BY compindex ASC",txtFinder.getTag());
            }else{
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),mainquery+"WHERE formid=? AND parent=? ORDER BY compindex ASC",txtFinder.getTag(),combo.getText());
            }
            
            tablegrid.setData(nikiset);
            cmpBtnAdd.setVisible(nikiset.getRows()>=1 || !txtFinder.getText().equals("") ?false:true);
        }
        else{
            String s = "%"+txtSearch.getText()+"%";
            String p = txtFinder.getTag();
            if (combo.getText().equals(ALL)) {
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),mainquery+ "WHERE (compname like ? OR complabel  like ?  OR comptext  like ?  ) AND formid=? ORDER BY compindex ASC ; ",s,s,s,p);
            }else{
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),mainquery+ "WHERE (compname like ? OR complabel  like ?  OR comptext  like ?  ) AND formid=? AND  parent=?  ORDER BY compindex ASC ; ",s,s,s,p,combo.getText());
            }
            tablegrid.setData(nikiset);
            cmpBtnAdd.setVisible((nikiset.getRows()>=1 || !txtSearch.getText().equals(""))|| txtFinder.getText().equals("")?false:true);
            
        }
        
         
        combo.setData(new Nset(nikitaConnection.Query("select distinct parent from web_component WHERE formid IN (SELECT formid FROM web_form WHERE formname = ? ) ORDER BY parent ASC",txtFinder.getText()).getDataAllVector()));
        if (combo.getData().getInternalObject() instanceof Vector) {
            ((Vector)combo.getData().getInternalObject() ).insertElementAt(ALL, 0);
        }else{
            combo.setData(Nset.readsplitString(ALL));
        }
 
        
        tablegrid.setTag(tablegrid.getData().copyNikitasetAtCol(0).toJSON());
        
        if(nikiset.getText(0, 4).equals("link")){
            tablegrid.setColHide(15, true);
        }
        tablegrid.setColStyle(15, new Style().setStyle("width", "132px"));//102px
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
         
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 15){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("comp-btnUp["+row+"]");
                    btn.setText("img/up.png");
                    btn.setTag(data.getData(row).toJSON());
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }                      
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("comp-btnDown["+row+"]");
                    btn.setText("img/down.png");
                    btn.setTag(data.getData(row).toJSON());
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            
                        }
                    });
                    
                                         
                    btn = new Image();
                    btn.setId("comp-btnCopy["+row+"]");
                    btn.setText("img/copy.png");
                    
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("comp-btnInsert["+row+"]");
                    btn.setText("img/add.png");
                    
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("comp-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(data.getData(row).toJSON());
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }                      
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("comp-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2")); 
                    }  
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                              
                        }
                    });                   
                 
                    
                    return horisontalLayout;
                } else if(col==5 || col==6 ){   
                    Component cmp = new Component();
                    cmp.setVisible(false);
                    cmp.setText( Component.escapeHtml(data.getData(row).getData(col).toString()) );                   
                    return cmp;  
                } else if(col==4){   
                    Component cmp = new Component();
                    cmp.setVisible(false);
                    cmp.setText(componentName.getData(data.getData(row).getData(col).toString()).toString());                   
                    return cmp;                    
                } else if(col==11||col==12){
                    Component cmp = new Component();
                    cmp.setVisible(false);
                    if (data.getData(row).getData(col).toString().equals("1")) {
                        cmp.setText("true");
                    }else{
                        cmp.setText("false");
                    }                    
                    return cmp;
                }                   
                return null;
            }
        });
    }  

}
