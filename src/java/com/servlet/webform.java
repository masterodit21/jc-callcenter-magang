/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
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
public class webform extends NikitaServlet{
    private static final String ALL = "*";
    /*parameter
    formname
    code
    mode
    formid
    data
    id
    name
    */
    Label idtype ;
    Label idmode ;
    Label stitle ;
    String user ;
    Nikitaset formunlock;
    Nikitaset formaccess1;
    Nikitaset formaccess2;
    Nikitaset position;
    Nikitaset connex;
    int lastrow = -1;
    
    NikitaConnection nikitaConnection ;
    StringBuffer sb = new StringBuffer();
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Form");        
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        stitle = new Label();
        stitle.setId("form-title");
        stitle.setText(request.getParameter("title"));
        stitle.setVisible(false);
        nf.addComponent(stitle);
        
        //add 13K
        idtype = new Label();
        idtype.setId("form-type");
        idtype.setText(request.getParameter("type"));
        idtype.setTag(request.getParameter("code"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        idmode = new Label();
        idmode.setId("form-mode");
        idmode.setText(request.getParameter("mode"));
        idmode.setTag(request.getParameter("index"));
        idmode.setVisible(false);
        nf.addComponent(idmode);
        
        Textsmart txt = new Textsmart();
        txt.setId("form-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));                
        horisontalLayout.addComponent(txt);
         
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("form-tblForm"));
                
            }
        });       
        
        Component btn = new Image();
        btn.setId("form-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        if(idtype.getTag().equals("duplicate"))
            btn.setVisible(false);
        else
            btn.setVisible(true);
                
        btn.setStyle(style);
        btn.setTag(request.getParameter("code"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                connex = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ");
                request.setParameter("mode","add");
                request.setParameter("type",idtype.getText());
                response.showform("webformadd", request,"add", true);
                response.write();
            }
        });    
        request.retainData(idmode);
        Button tblnewcopy = new Button();
        tblnewcopy.setId("newcopy");
        tblnewcopy.setText("New Form");
        horisontalLayout.addComponent(tblnewcopy);
        tblnewcopy.setVisible(idmode.getText().equals("copy"));
        tblnewcopy.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    response.setResult("NEWFORM",Nset.newObject().setData("index", idmode.getTag()).setData("type", idtype.getText()));
                    response.closeform(response.getContent());
                    
                    response.write();
            }
        });
        
        Checkbox checkbox = new Checkbox();
        checkbox.setId("filter");
        checkbox.setData(Nset.readJSON("[['myform','My Form']]", true));
        horisontalLayout.addComponent(checkbox);
        checkbox.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
//                if(response.getContent().findComponentbyId("filter").getText().contains("myform")){
                    fillGrid(response);                    
                    response.refreshComponent(response.getContent().findComponentbyId("form-tblForm"));
//                }
            }
        });
        
        
        if(NikitaService.isModeCloud()){
            checkbox.setText("myform");
            checkbox.setVisible(false);
        }
       
        
        //add right forparent
        Combobox combo = new Combobox();
        combo.setId("form-typez");
        combo.setLabel("Type");
        combo.setText(ALL);
        combo.setStyle(new Style().setStyle("n-searchicon", "true").setAttr("n-layout-align", "right").setStyle("n-label-width", "80px").setStyle("width", "200px"));
        combo.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);     
                response.refreshComponent(response.getContent().findComponentbyId("form-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("form-typez"));
                response.refreshComponent(response.getContent().findComponentbyId("form-tblForm"));
                response.write();
            }
        });
        
         
        HorizontalLayout hr = new HorizontalLayout();
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        hr.addComponent(horisontalLayout);
        hr.addComponent(combo);
        
        nf.addComponent(hr);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("form-tblForm");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Title|Type|Style|Describe|Created By|"));
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(6, true);
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);  
        tablegrid.setStyle(new Style().setStyle("n-ondblclick", "true"));
    
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(tablegrid);
                response.refreshComponent(response.getContent().findComponentbyId("form-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("form-typez"));
                response.write();
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                  
                if(reqestcode.equals("play") && responsecode.equals("button2")){
                    if(result.getData("type").toString().equals("webform")||result.getData("type").toString().equals("form")) {
                        response.showformGen(result.getData("form").toString(), request, "", true);
                    }else if(result.getData("type").toString().equals("link")) {
                        request.setParameter("fid", result.getData("fid").toString() );
                        request.setParameter("fname",result.getData("fname").toString() );

                        response.showformGen("webargument", request, "", true);
                    }
                    response.write();
                
                }
                if(reqestcode.equals("play") && responsecode.equals("button3")){
                    response.showDialogResult("Information ", result.getData("data").toString() , "",null, "OK", "");
                    response.write();
                
                }               
                if(reqestcode.equals("add") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("edit") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("delete") && responsecode.equals("button3")){
                    
                    formaccess2 = nikitaConnection.Query("SELECT compid FROM web_route WHERE action LIKE ? OR action LIKE ? ", result.getData("formname").toString(),result.getData("formname2").toString()); 
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < formaccess2.getRows() ; i++) {
                        sb.append(formaccess2.getText(i, 0));
                        if(formaccess2.getRows() != (i+1)){
                            sb.append(",");
                        }
                    }
                    String x=sb.toString();
                    
                    formaccess1 = nikitaConnection.Query("SELECT formname FROM web_form WHERE formid IN ((SELECT formid FROM web_component WHERE compid IN ("+x+")))");                      
                    sb = new StringBuffer();
                    for (int i = 0; i < formaccess1.getRows() ; i++) {
                        sb.append(formaccess1.getText(i, 0));
                        if(formaccess1.getRows() != (i+1)){
                            sb.append(",");
                        }
                    }
                    x=sb.toString();
                    response.showDialogResult("Information ", x , "",null, "OK", "");
                    response.write();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){                                        
                    nikitaConnection.Query("DELETE FROM web_form WHERE formid=?",result.getData("id").toString());                    
                    nikitaConnection.Query("DELETE FROM sys_lock_form WHERE formid=?",result.getData("id").toString());
                    nikitaConnection.Query("DELETE FROM web_route WHERE compid IN(SELECT compid FROM  web_component WHERE formid=?)",result.getData("id").toString());
                    nikitaConnection.Query("DELETE FROM web_component WHERE formid=?",result.getData("id").toString());
                    
                    fillGrid(response);
                    response.writeContent();
                }
                
                
                
                if(reqestcode.equals("copy") && responsecode.equals("NEWFORM")){
   
                    nikitaConnection.Query("UPDATE web_form SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",formindex=formindex+2 WHERE formindex > ?", user,result.getData("index").toString());   
                    nikitaConnection.Query("INSERT INTO web_form( formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,createddate) VALUES('','',?,'','',?,?,"+WebUtility.getDBDate(dbCore)+")",result.getData("type").toString(),user,""+(result.getData("index").toInteger()+1));
                    
                    
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("copy") && responsecode.equals("OK")){
                    nikitaConnection.Query("UPDATE web_form SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",formindex=formindex+2 WHERE formindex > ?", user,result.getData("index").toString()); 
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_form WHERE formid=?",result.getData("id").toString());
                    int xindex = Utility.getInt(nikitaConnection.Query("SELECT COUNT( * ) FROM web_form WHERE formname = ? ",nikitaset.getText(0, "formname")).getText(0, 0)) ;
                    
                    nikitaset =  nikitaConnection.Query("insert into web_form( formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,createddate) values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",nikitaset.getText(0, "formname")+"_"+xindex,nikitaset.getText(0, "formtitle")+"_"+xindex,nikitaset.getText(0, "formtype"),nikitaset.getText(0, "formstyle"),nikitaset.getText(0, "formdescribe"),user,""+(result.getData("index").toInteger()+1));
                    String newformid = nikitaset.getText(0, 0);
                    nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid=?",result.getData("id").toString());
                    
                    for (int i = 0; i < nikitaset.getRows(); i++) {
                        String copycompid = nikitaset.getText(i, "compid");  
                        
                        Nikitaset v = nikitaConnection.Query("INSERT INTO web_component("+
                                          "formid,compname,compindex,comptype,complabel,comptext,comphint,"+ 
                                          "compdefault,complist,compstyle,enable,visible,mandatory,parent,createdby,createddate)"+
                                          "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", newformid,
                                          nikitaset.getText(i, "compname"),
                                          nikitaset.getText(i, "compindex"),
                                          nikitaset.getText(i, "comptype"),
                                          nikitaset.getText(i, "complabel"),
                                          nikitaset.getText(i, "comptext"),
                                          nikitaset.getText(i, "comphint"),
                                          nikitaset.getText(i, "compdefault"),
                                          nikitaset.getText(i, "complist"),
                                          nikitaset.getText(i, "compstyle"),
                                          nikitaset.getText(i, "enable"),
                                          nikitaset.getText(i, "visible"),
                                          nikitaset.getText(i, "mandatory"),
                                          nikitaset.getText(i, "parent"),user) ;
                        String newcompid = v.getText(0, 0);   
                        Nikitaset ns = nikitaConnection.Query("SELECT * FROM web_route WHERE compid=?",copycompid);
                        for (int j = 0; j < ns.getRows(); j++) {
                             nikitaConnection.Query("INSERT INTO web_route("+
                                      "compid, routeindex, action, expression,createdby,createddate)"+
                                      "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",newcompid,ns.getText(j, "routeindex"),ns.getText(j, "action"),ns.getText(j, "expression"),user);
                             
                        }
                    }
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
                
        Component comp = new Component();
        comp.setId("form-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                formunlock = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", n.getData(0).toString());
                formaccess1 = nikitaConnection.Query("SELECT createdby FROM web_form WHERE formid = ? AND formname = ? ", n.getData(0).toString(),n.getData(1).toString());
                position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", user);   
                formaccess2 = nikitaConnection.Query("SELECT accessusername FROM sys_user_access WHERE username=? AND accessusername=? ", 
                                                        formaccess1.getText(0, 0),user);    
                if(!formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){                            
                    request.setParameter("unlock","open");
                }else{                            
                    request.setParameter("unlock","");
                }
                
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                request.setParameter("type",idtype.getText());         
                request.setParameter("created",n.getData(6).toString());
                response.showform("webformadd", request,"edit", true);

                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("form-btnCopy");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                request.setParameter("mode","copy");
                request.setParameter("code","selected");
                request.setParameter("type",idtype.getText());
                request.setParameter("index",Nset.readJSON(component.getTag()).getData(7).toString());                
                response.showformGen("webform", request, "copy", true, "copy" );
                response.write();

            }
        });
        
        
        comp = new Component();
        comp.setId("form-btnPlay");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                Nset n = Nset.readJSON(component.getTag()); 
                 
                String l = getUsedForm(n.getData(0).toString(), n.getData(1).toString()) ;
                if(l.equals("")){   
                    if(n.getData(3).toString().equals("mobileform")||n.getData(3).toString().equals("form")) {
                            Nset nn = Nset.newObject();
                            nn.setData("fid", n.getData(0).toString());
                            nn.setData("fname", n.getData(1).toString());
                            nn.setData("mode", "play");
                            String reform = nn.toJSON();
                            Nikitaset ns = nikitaConnection.Query("SELECT imei FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
                            
                            String split = ",";
                            if (ns.getText(0, 0).contains("|")) {
                                split = "|";
                            }else if (ns.getText(0, 0).contains(";")) {
                                split = ";";
                            }
                            Vector<String> imeis = Utility.splitVector(ns.getText(0, 0), split);
                            for (int i = 0; i < imeis.size(); i++) {
                                String imei = imeis.get(i).trim();
                                //nikitaConnection.Query("INSERT INTO sys_debug (code, request, response, result, lastlog, status) VALUES (?, ?, ?, ?, ?, ?) ", "MOBILE-PLAY-"+imei, reform, "", "", Utility.Now(), "0");
                                //nikitaConnection.Query("UPDATE sys_debug SET lastlog=?,request=?,status='0' WHERE code=?", Utility.Now(),reform,"MOBILE-PLAY-"+imei);
                                //response.sendChat("*MOBILE-PLAY-"+imei+":");
                                response.sendBroadcastListener("MOBILE-PLAY-"+imei, nn);
                            }
                            
                    } 
                    
                    if (request.getParameter("autocompile").equals("true")) {
                        NikitaEngine.compile(response.getConnection(NikitaConnection.LOGIC), n.getData(1).toString()  );  
                    }
                    
                    if(n.getData(3).toString().equals("webform")||n.getData(3).toString().equals("form")) {
                        response.showformGen(n.getData(0).toString(), request, "", true);
                    }else if(n.getData(3).toString().equals("mobileform")) {
                        request.setParameter("url", component.getBaseUrl("/base/"+n.getData(0).toString()+"/"+n.getData(1).toString()) );
                        response.showformGen( "webbrowser" , request, "", true);
                    }else if(n.getData(3).toString().equals("link")) {
                        request.setParameter("fid", n.getData(0).toString());
                        request.setParameter("fname",n.getData(1).toString() );
                        response.showformGen("webargument", request, "", true);
                    }
                       
                    response.write();
                }else{
                    response.showDialogResult("Warning ", "There is another form that doesn't exist "+l, "play",Nset.newObject().setData("fid",n.getData(0).toString() ).setData("fname",n.getData(1).toString() ).setData("type",n.getData(3).toString() ).setData("form", n.getData(0).toString()).setData("data",l).setData("comp", Nset.readJSON(component.getTag()).getData(1).toString()), "cancel", "play","see form");
                }
                 
                response.write();

            }
        });
        
        comp = new Component();
        comp.setId("form-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                formunlock = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", n.getData(0).toString());    
                formaccess1 = nikitaConnection.Query("SELECT createdby FROM web_form WHERE formid = ? AND formname = ? ", n.getData(0).toString(),n.getData(1).toString());  
                position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", user);   
                formaccess2 = nikitaConnection.Query("SELECT accessusername FROM sys_user_access WHERE username=? AND accessusername=? ", 
                                                        formaccess1.getText(0, 0),user);  
                String userowner=formaccess1.getText(0, 0);
                if(n.getData(6).toString().equals(user) || !formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){
                    String x = "%\""+n.getData(1).toString()+"\\\\%"; 
                    String y = "%\""+n.getData(1).toString()+"\"%"; 
                    formaccess1 = nikitaConnection.Query("SELECT routeid FROM web_route WHERE action LIKE ? OR action LIKE ? ", x,y);
                    if(formaccess1.getRows() > 0){
                        response.showDialogResult("Warning ["+userowner+"]", "There is another form that access this form", "delete",Nset.newObject().setData("id",n.getData(0).toString() ).setData("formname", x).setData("formname2", y), "Cancel", "Delete","See Form");
                    }else{
                        response.showDialogResult("Delete ["+userowner+"]" , "Do you want to delete?", "delete",Nset.newObject().setData("id",n.getData(0).toString() ), "No", "Yes");
                    }
                }else{
                    response.showDialogResult("Warning from "+formaccess1.getText(0, 0), "You don't have access to delete", "warning",null, "OK", "");
                }   
                response.write();

            }
        });
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });        
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });   
        
    }
    public String getUsedForm(String id, String name){      
        Nset result = Nset.newArray();   Nset fnfound = Nset.newArray();   
        result = webexport.populateAllForms( result, fnfound, nikitaConnection,  id);
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fnfound.getSize(); i++) {
            sb.append(i>=1?", ":"").append(fnfound.getData(i).toString());            
        }
        
        return sb.toString();
    }
    public String getUsedFormOld(String id,String name){         
                String act = "";
                String flag ="tidak";
                int x,y,h;
                Nset result = Nset.newArray(); 
                String z = "%formname%"; 
                formaccess1 = nikitaConnection.Query("SELECT action FROM web_route WHERE action LIKE ? AND compid IN (SELECT compid FROM web_component WHERE formid = (SELECT formid FROM web_form WHERE formid = ? AND formname = ?)) ", z,id,name);
                for (int j = 0; j < formaccess1.getRows(); j++) {
                    act = formaccess1.getText(j, 0);
                    x =  act.indexOf("formname");
                    y =  act.indexOf("ConnectionAction");
                    h =  act.indexOf("inflate");
                    if(x != -1 && y == -1 && h == -1){
                        x=x+13;
                        act = act.substring(x);
                        x = act.indexOf("\"");
                        act= act.substring(0,x-1);
                        flag="tidak";
                        for (int k = 0; k < result.getArraySize(); k++) {                                    
                            if(act.equals(result.getData(k).toString())){
                                flag="ada";
                            }
                        }
                        if(!flag.equals("ada") && !act.equals(""))
                            result.addData(act);
                    }
                } 
                
                for (int i = 0; i < result.getArraySize(); i++) {
                    formaccess1 = nikitaConnection.Query("SELECT action FROM web_route WHERE compid IN (SELECT compid FROM web_component WHERE formid = ?) AND action LIKE ? ", result.getData(i).toString(),z); 
                    for (int j = 0; j < formaccess1.getRows(); j++) {
                        act = formaccess1.getText(j, 0);
                        x =  act.indexOf("formname");
                        y =  act.indexOf("ConnectionAction");
                        h =  act.indexOf("inflate");
                        if(x != -1 && y == -1 && h == -1){
                                x=x+11;
                                act = act.substring(x);
                                x = act.indexOf("},");
                                flag="tidak";
                                for (int k = 0; k < result.getArraySize(); k++) {                                    
                                    if(act.substring(0,x-4).equals(result.getData(k).toString())){
                                        flag="ada";
                                    }
                                }
                                if(!flag.equals("ada") && !act.substring(0,x-4).equals(""))
                                    result.addData(act.substring(0,x-4));//formname
                            }
                    }                       
                }
                String l="";
                for (int i = 0; i < result.getArraySize(); i++) {
                    formaccess1 = nikitaConnection.Query("SELECT formname FROM web_form WHERE formname = ? ",result.getData(i).toString());                      
                    sb = new StringBuffer();
                    if(formaccess1.getRows() == 0){
                        sb.append(result.getData(i).toString());
                    }
                    l=sb.toString();
                                           
                }
                return l;
    }
    public void fillGrid(NikitaResponse response){ 
        response.getContent().setText((idtype.getText().equals("link")?"Link Form" :(idtype.getText().equals("webform")?"Web Form":(idtype.getText().equals("mobileform")?"Mobile Form":"Form "+ (idtype.getTag().equals("")?"":" [Duplicated]")))) + (idmode.getText().equals("copy")?" [Copy]":"") );  
                
        Combobox combo = (Combobox)response.getContent().findComponentbyId("form-typez") ; 
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("form-tblForm") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("form-txtSearch");
        
        
        if(response.getContent().findComponentbyId("form-btnAdd").getTag().equals("selected")){
            
            response.getContent().findComponentbyId("form-btnAdd").setVisible(false);
            tablegrid.setColHide(7, true);
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("form-tblForm") ;
                    Nset nset = Nset.newObject();
                    nset.setData("id", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                    nset.setData("name", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                    nset.setData("index", idmode.getTag());
                    nset.setData("type", idtype.getText());
                    response.closeform(response.getContent());
                    response.setResult("OK",nset);
                    
                    response.write();
                }
            });
        }else if(response.getContent().findComponentbyId("form-btnAdd").getTag().equals("selectedmodul")){
            response.getContent().findComponentbyId("form-btnAdd").setVisible(false);
            tablegrid.setColHide(7, true);
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("form-tblForm") ;
                    Nset nset = Nset.newObject();
                    nset.setData("id", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                    nset.setData("name", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                    nset.setData("type", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString());
                    response.setResult("OK",nset);
                    response.closeform(response.getContent());
                    
                    response.write();
                }
            });
        }else{
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("form-tblForm") ;
                    
                    fillGrid(response);
                    formunlock = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());    
                    formaccess1 = nikitaConnection.Query("SELECT createdby FROM web_form WHERE formid = ? ", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());  
                    position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", user);   
                    formaccess2 = nikitaConnection.Query("SELECT accessusername FROM sys_user_access WHERE username=? AND accessusername=? ", 
                                                            formaccess1.getText(0, 0),user);    
                    if(tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString().equals("link") ||
                            tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString().equals("scheduler") ){
                        Nikitaset nset2 = compLink(response);
                        request.setParameter("idform", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                        request.setParameter("nameform", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                        request.setParameter("idcomp", nset2.getText(0, 0));
                        request.setParameter("mode","logiclist");
                       
                        if(!formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){                            
                            request.setParameter("unlock","open");
                        }else{                            
                            request.setParameter("unlock","");
                        }
                        request.setParameter("created",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(6).toString());
                        response.showform("weblogic", request,"edit", true);
                    }else{
                        if(!formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){                            
                            request.setParameter("unlock","open");
                        }else{                            
                            request.setParameter("unlock","");
                        }
                        request.setParameter("formid", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                        request.setParameter("formname", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                        request.setParameter("formtype", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString());
                        request.setParameter("mode","comp");
                        request.setParameter("created",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(6).toString());
                        response.showform("webcomponent", request,"comp", true);
                    }
                    response.write();
                }
            });
        }
        String filter =response.getContent().findComponentbyId("filter").getText() ;
        if(NikitaService.isModeCloud()){
            filter = "myform";
        }
        Nikitaset nikiset;
        String x = "form";
        if (idtype.getText().equalsIgnoreCase("link")) {
            x = "link";
        }
        
        if(txt.getText().equals("")){
            if (combo.getText().equals(ALL)) {
                if(idtype.getTag().equals("duplicate")){
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" formname IN(SELECT formname FROM (SELECT formname,count(*) AS cnt FROM web_form GROUP BY formname) m2m WHERE cnt >= 2) ORDER BY formname,formid;");
                }else{                
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,DBL.cnt "+ 
                                " FROM web_form LEFT JOIN (SELECT formname AS fnm,count(*) AS cnt FROM web_form GROUP BY formname)as DBL ON(web_form.formname=DBL.fnm) "+(filter.contains("myform")?" WHERE createdby = '"+user+"' "+(idtype.getText().equals("")?"":" AND (formtype ='"+idtype.getText()+"' OR formtype = '"+x+"')"+"")+" ":""+(idtype.getText().equals("")?"":" WHERE (formtype ='"+idtype.getText()+"' OR formtype = '"+x+"')"+"")+" ORDER BY formindex ASC;"));
                }
            }else{
                if(idtype.getTag().equals("duplicate")){
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE  "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" formtype ='"+combo.getText()+"'"+
                                                       " AND formname IN(SELECT formname FROM (SELECT formname,count(*) AS cnt FROM web_form GROUP BY formname) m2m WHERE cnt >= 2) ORDER BY formname,formid;");
                }else{
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,DBL.cnt  FROM web_form LEFT JOIN (SELECT formname AS fnm,count(*) AS cnt FROM web_form GROUP BY formname)as DBL ON(web_form.formname=DBL.fnm) WHERE  "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" formtype ='"+combo.getText()+"' ORDER BY formindex ASC;");
                }
            }
                tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            if (combo.getText().equals(ALL)) {
                if(idtype.getTag().equals("duplicate")){
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE  "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" (formname LIKE ? OR formtitle LIKE ? ) "+ 
                                                       " AND formname IN(SELECT formname FROM (SELECT formname,count(*) AS cnt FROM web_form GROUP BY formname) m2m WHERE cnt >= 2) ORDER BY formname,formid;",s,s);
                
                }else{
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,DBL.cnt  FROM web_form LEFT JOIN (SELECT formname AS fnm,count(*) AS cnt FROM web_form GROUP BY formname)as DBL ON(web_form.formname=DBL.fnm) WHERE  "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" (formname LIKE ? OR formtitle LIKE ? ) " + (idtype.getText().equals("")?"":" AND (formtype ='"+idtype.getText()+"' OR formtype = '"+x+"') ORDER BY formindex ASC;"),s,s);
                }
            }else{   
                if(idtype.getTag().equals("duplicate")){
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" (formname LIKE ? OR formtitle LIKE ? ) AND formtype ='"+combo.getText()+"'"+
                                                       " AND formname IN(SELECT formname FROM (SELECT formname,count(*) AS cnt FROM web_form GROUP BY formname) m2m WHERE cnt >= 2) ORDER BY formname,formid;",s,s);
                
                }else{
                    nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex,DBL.cnt  FROM web_form LEFT JOIN (SELECT formname AS fnm,count(*) AS cnt FROM web_form GROUP BY formname)as DBL ON(web_form.formname=DBL.fnm) WHERE "+(filter.contains("myform")?"createdby = '"+user+"' AND ":"")+" (formname LIKE ? OR formtitle LIKE ? ) AND formtype ='"+combo.getText()+"' ORDER BY formindex ASC;",s,s);
                }
            }
            tablegrid.setData(nikiset);
        }
        /*
        if (idtype.getText().equals("link")) {
            combo.setData(Nset.newArray().addData("link"));
        }else if (idtype.getText().equals("webform")) {
            combo.setData(Nset.newArray().addData("webform").addData("from"));
        }else if (idtype.getText().equals("mobileform")) {
            combo.setData(Nset.newArray().addData("mobileform").addData("from"));
        }
        */
        
        combo.setData(new Nset(nikitaConnection.Query("select distinct formtype from web_form "+(idtype.getText().equals("")?"":" WHERE formtype ='"+idtype.getText()+"' OR formtype = '"+x+"' ;")).getDataAllVector()));
        if (combo.getData().getInternalObject() instanceof Vector) {
            ((Vector)combo.getData().getInternalObject() ).insertElementAt(ALL, 0);
        }else{
            combo.setData(Nset.readsplitString(ALL));
        }
        lastrow=-1;
        tablegrid.setColHide(7, idmode.getText().equals("copy"));
        tablegrid.setColHide(7, response.getContent().findComponentbyId("form-btnAdd").getTag().equals("selected"));
        tablegrid.setColStyle(7, new Style().setStyle("width", "102px"));
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent,Nset data) {
                if (lastrow!=row) {
                    lastrow=row;
                    formunlock = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", data.getData(row).getData(0).toString());
                    formaccess1 = nikitaConnection.Query("SELECT createdby FROM web_form WHERE formid = ? ", data.getData(row).getData(0).toString());  
                    position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", user);   
                    formaccess2 = nikitaConnection.Query("SELECT accessusername FROM sys_user_access WHERE username=? AND accessusername=? ", 
                                                            formaccess1.getText(0, 0),user);  
                }
                  
                if(col == 1||col == 2){
                    Component component = new Component();
                    if(idtype.getTag().equals("duplicate")){
                        component.setText(data.getData(row).getData(col).toString());
                    }else if (data.getData(row).getData(data.getData(row).getArraySize()-1).toInteger()>1) {
                        component.setText("<a style=\"color:orange\">"+data.getData(row).getData(col).toString()+"</a>");
                    }else{
                        component.setText(data.getData(row).getData(col).toString());
                    }                   
                    component.setVisible(false);
                    
                    return component;
                }else if(col == 4){
                    Component component = new Component();
                    component.setText(wrapForm(data.getData(row).getData(col).toString()));              
                    component.setVisible(false);
                    
                    return component;
                }else if(col == 7  ){    
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component
                   
         
                    btn = new Image();
                    btn.setId("form-btnPlay["+row+"]");
                    btn.setText("img/play.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    if(data.getData(row).getData(3).toString().equals("webform") || 
                            data.getData(row).getData(3).toString().equals("form")|| 
                            data.getData(row).getData(3).toString().equals("link")){
                        btn.setVisible(true);
                    }else{
                    }
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    horisontalLayout.addComponent(btn);   
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            
                        }
                    });
                   
                             
                    btn = new Image(){
                        private String href="";
                        public String getView(NikitaViewV3 v3) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("<a href=\""+getBaseUrl(href)+"\" target=\"_blank\">");
                            sb.append(super.getView(v3));
                            sb.append("</a>");
                            return sb.toString(); 
                            
                        }
                        public Image get(String href){
                            this.href=href;
                            return this;
                        }
                    }.get("/base/"+data.getData(row).getData(0).toString()+"/"+data.getData(row).getData(1).toString()+"/");
                    btn.setId("form-btnRun["+row+"]");
                    btn.setText("img/run.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    btn.setVisible(data.getData(row).getData(3).toString().equals("mobileform")?false:true);                    
                    
                     
                    style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    if (!idtype.getText().equals("mobileform")) {
                        horisontalLayout.addComponent(btn);
                    }       
                    
                    
                                         
                    btn = new Image();
                    btn.setId("form-btnCopy["+row+"]");
                    btn.setText("img/copy.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("form-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    if(data.getData(row).getData(6).toString().equals(user) || 
                        !formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){                        
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    } else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    
                    btn = new Image();
                    btn.setId("form-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    if(data.getData(row).getData(6).toString().equals(user) || 
                        !formunlock.getText(0, 0).equals("") || formaccess2.getRows() != 0 || position.getText(0, 0).equals("9")){
                    } else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2")); 
                    }                   
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                        }
                    });                   
                    
                   
                                          
                    return horisontalLayout;
        
                }                    
                return null;
            }
        });
    }  
    private String wrapForm(String s){
        return s.length()>=32?s.substring(0,32)+"...":s;
    }
    private Nikitaset compLink(NikitaResponse response){
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("form-tblForm") ;
                        
                        Nikitaset nset = nikitaConnection.Query("SELECT compid FROM web_component WHERE comptype = ? and formid = ?",
                                "link",
                                tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                        if(nset.getRows()<1){
                            nikitaConnection.Query("insert into web_component(formid,compname,comptype,compindex,createdby,createddate) values(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                       
                            tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString(),
                            tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString(),
                            "link","1",user);
                        }
                        Nikitaset nset2 = nikitaConnection.Query("SELECT compid FROM web_component WHERE formid = ?",
                                tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                        
                        return nset2;
    }
    
    
}
