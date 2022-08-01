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
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author rkrzmail
 */
public class weblogic extends NikitaServlet{
    
    Label condition;
    private Label idform ;
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        condition = new Label();
        condition.setId("logic-lblcreatedby");
        condition.setTag(request.getParameter("created")); 
        condition.setText(request.getParameter("unlock"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
 
        final Label buffered = new Label();
        buffered.setId("logic-buffered");
        buffered.setVisible(false);
        nf.addComponent(buffered);
        
        final Label idcomp = new Label();
        idcomp.setId("logic-lblidcomp");
        idcomp.setTag(request.getParameter("idcomp"));
        idcomp.setVisible(false);
        nf.addComponent(idcomp);
        
        idform = new Label();
        idform.setId("logic-lblidform");
        idform.setTag(request.getParameter("idform"));
        idform.setVisible(false);
        nf.addComponent(idform);    
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Style style = new Style();
        style.setStyle("maximize", "true");
        nf.setStyle(style);   
        nf.setStyle(new Style().setStyle("width", "1064").setStyle("height", "560"));
         
        
       
        
        Textbox txt = new Textbox();
        txt.setId("logic-txtFinder");
        txt.setLabel("Comp Name");
        txt.setEnable(false);
        txt.setVisible(false);
        horisontalLayout.addComponent(txt);
        
        
        nf.addComponent(horisontalLayout);
        
        horisontalLayout = new HorizontalLayout();
        txt = new Textsmart();
        txt.setId("logic-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setText(request.getParameter("search"));
        horisontalLayout.addComponent(txt);
        
       Image btn = new Image();
        btn.setId("flow");
        btn.setText("img/hierarki.png");
        btn.setVisible(false);
        btn.setStyle(new Style().setStyle("margin-top", "3px"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                request.setParameter("idform", idform.getTag());
                request.setParameter("idcomp", idcomp.getTag());
                request.setParameter("mode", lblmode.getTag());
                
                response.showform("weblogicflow",request,"",true); 
                response.write();
            }
        });  
        
        btn = new Image();
        btn.setId("logic-btnAdd");
        btn.setText("img/add.png");
        btn.setStyle(new Style().setStyle("margin-top", "6px"));
        btn.setTag(request.getParameter("idcomp"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_route("+
                                          "compid, routeindex,createdby,createddate)"+
                                          "values(?,1,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("logic-txtFinder").getTag(),user);
                    if(nikitaset.getError().length() > 1){
                        response.showAlert(nikitaset.getError()); 
                        response.write();
                    }else{
                        fillGrid(response);
                        response.writeContent();
                    }
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to add new", "warning",null, "OK", "");
                }
                
                
            }
        });     
        
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(horisontalLayout);
         
        
        Image img = new Image();
        img.setId("btnPlay");
        img.setText("img/play.png");
        img.setVisible(true);
        style = new Style();
        style.setStyle("width", "24px").setStyle("height", "24px") ;
        img.setStyle(style);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nikitaset nsv  = nikitaConnection.Query("SELECT formname,formtype FROM web_form WHERE formid=?", idform.getTag());
                String type = nsv.getText(0, 1);
                if (type.equals("mobileform")||type.equals("form")) {
                    
                    Nset n = Nset.newObject();
                    n.setData("fid", idform.getTag());
                    n.setData("fname", nsv.getText(0, 0));
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
                    response.showformGen( idform.getTag() , request, "", true);                 
                }else if(type.equals("mobileform")) {
                    String bfn = component.getBaseUrl("/base/"+idform.getTag()+"/"+nsv.getText(0, 0));
                    request.setParameter("url", bfn) ;
                    response.showformGen( "webbrowser" , request, "", true);
                }else if(type.equals("link")) {
                    request.setParameter("fid", idform.getTag());
                    request.setParameter("fname",nsv.getText(0, 0) );
                    response.showformGen("webargument", request, "", true);                    
                }     
                
                response.write();
            }
        });
        hr.addComponent(img);
 
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                fillGrid(response);        
                 response.refreshComponent("logic-tblLogic");
                response.write();
            }
        });
        
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
       
        
       
        nf.addComponent(hr);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("logic-tblLogic");
        tablegrid.setDataHeader(Nset.readsplitString("No,Component Id, Route Index, Action, Expression,",","));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(2, true);
        
         tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
    
        
        nf.addComponent(tablegrid);  
        
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
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    nikitaConnection.Query("delete from web_route where routeid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("copy") && responsecode.equals("copy-logic")){
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("logic-buffered").getTag());
                    nikitaConnection.Query("UPDATE web_route set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=routeindex+2+"+result.getArraySize()+" WHERE routeindex > ? AND compid=?",user,nset.getData(2).toString(),nset.getData(1).toString());                
                    
                    for (int i = 0; i < result.getArraySize(); i++) {
                        Nikitaset nikitaset  = nikitaConnection.QueryPage(1, 1, "SELECT action,expression FROM web_route WHERE routeid=?",result.getData(i).toString());
                        nikitaConnection.Query("INSERT INTO web_route(routeindex,compid,action,expression,createdby,createddate) VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+") ", (nset.getData(2).toInteger()+i+1)+"",nset.getData(1).toString(),nikitaset.getText(0, 0),nikitaset.getText(0, 1),user);
                    }
                    fillGrid(response);
                    response.writeContent();
                  
                }
            }
        });
                
        Component 
        comp = new Component();
        comp.setId("logic-btnAction");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("idcomp", idcomp.getTag()); 
                request.setParameter("idform", idform.getTag()); 
                request.setParameter("mode","edit");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }                
                request.setParameter("created",condition.getTag());
                response.showform("webaction", request,"edit", true);
                response.write();

            }
        });        
        comp = new Component();
        comp.setId("logic-btnExpression");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag()); 
                request.setParameter("idcomp", idcomp.getTag()); 
                request.setParameter("idform", idform.getTag()); 
                request.setParameter("mode","edit");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }  
                request.setParameter("created",condition.getTag());
                response.showform("webexpression", request,"edit", true);             
                response.write();
            }
        });   
        
        comp = new Component();
        comp.setId("logic-btnUp");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    Nset nset = Nset.readJSON(component.getTag());
                
                    Nikitaset nikiset = nikitaConnection.Query("select routeid,routeindex from web_route WHERE routeindex < ? AND compid = ? ORDER BY routeindex DESC,routeid DESC", nset.getData(2).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_route set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=? WHERE routeid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());                
                    nikitaConnection.Query("UPDATE web_route set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=? WHERE routeid =? ", user,nset.getData(2).toString(),nikiset.getText(0, 0)); 

                    fillGrid(response);
                    response.writeContent();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to change", "warning",null, "OK", "");
                }

            }
        });
        
        comp = new Component();
        comp.setId("logic-btnDown");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    Nset nset = Nset.readJSON(component.getTag());
                
                    Nikitaset nikiset = nikitaConnection.Query("select routeid,routeindex from web_route WHERE routeindex > ? AND compid = ? ORDER BY routeindex ASC,routeid ASC", nset.getData(2).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_route set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=? WHERE routeid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());                
                    nikitaConnection.Query("UPDATE web_route set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=? WHERE routeid =? ", user,nset.getData(2).toString(),nikiset.getText(0, 0)); 
                    
                    fillGrid(response);
                    response.writeContent();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to change", "warning",null, "OK", "");
                }

            }
        });
        
        comp = new Component();
        comp.setId("logic-btnCopy");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    response.getContent().findComponentbyId("logic-buffered").setTag(component.getTag());
                    response.refreshComponent(response.getContent().findComponentbyId("logic-buffered"));
                    request.setParameter("idcomp", response.getContent().findComponentbyId("logic-lblidcomp").getTag());
                    request.setParameter("idform", response.getContent().findComponentbyId("logic-lblidform").getTag());
                    request.setParameter("formname", "");
                    request.setParameter("compname", response.getContent().findComponentbyId("logic-lblidform").getText());
                    response.showform("weblogiccopy", request,"copy", true);             
                    response.write();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to copy", "warning",null, "OK", "");
                }
            }
        });
        
        comp = new Component();
        comp.setId("logic-btnInsert");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    //form sync
                    Utility.FormSync(idform.getTag());
                    
                    Nset nset = Nset.readJSON(component.getTag());

                    nikitaConnection.Query("UPDATE web_route SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",routeindex=routeindex+2 WHERE routeindex > ? AND compid=?", user,nset.getData(2).toString(),nset.getData(1).toString());                
                    nikitaConnection.Query("INSERT INTO web_route(routeindex,compid,createdby,createddate) VALUES(?,?,?,"+WebUtility.getDBDate(dbCore)+") ", (nset.getData(2).toInteger()+1)+"",nset.getData(1).toString(),user);
                    
                    fillGrid(response);
                    response.writeContent();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to add new", "warning",null, "OK", "");
                }

            }
        });
        
        
        comp = new Component();
        comp.setId("logic-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if(condition.getTag().equals(user) || !condition.getText().equals("")){                     
                    response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to delete", "warning",null, "OK", "");
                }
                response.write();

            }
        });
        
        
        
        if(lblmode.getTag().equals("logiclist")){     
            Nikitaset nikitaset = nikitaConnection.Query("SELECT compname FROM web_component WHERE compid=?", idcomp.getTag()); 
            nf.findComponentbyId("logic-txtFinder").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("logic-txtFinder").setTag(idcomp.getTag());
            
            nf.setText("Logic/Route ["+nikitaset.getText(0, 0)+"]");       
            idform.setText(nikitaset.getText(0, 0));
        }
        else{            
            nf.setText("Logic/Route");       
        }
        
        
        
        response.setContent(nf);        
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);

            }
        });
        
    }
    private static String expFriendlyView(Nset n){
        String result = null;
        if ((result=getFViif(n))!=null) {
            return result;
        }else if ((result=getFVequal(n))!=null) {
            return result;
        }else if ((result=getFVstartwith(n))!=null) {
            return result;
        }else if ((result=getFVcontain(n))!=null) {
            return result;
        }
        StringBuffer sb = new StringBuffer();
        String[] keys = n.getData("args").getObjectKeys();
        sb.append("(");
        for (int i = 0; i < keys.length; i++) {
            String s = wrapLogic(n.getData("args").getData(keys[i]).toString());
            if (s.trim().length()==0) {
                s="''";
            }
            sb.append(i>=1?",":"").append("<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>");
        }
        sb.append(")");
        return "<a style=\"color:black\">"+n.getData("class").toString().replace("Expression", "")+ (n.getData("code").toString().equals("")?"":".")+WordUtils.capitalizeFully(n.getData("code").toString())+"</a>"+ (keys.length!=0?sb.toString():"") ;
 
    }
    private static String getColorView(String s){
        s=wrapLogic(s);
        return "<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>";
    }
    private static String getDefaultView(String s, String comp, String def){
        return s.equals(comp)?def:s;
    }
    private static String getFViif(Nset n){
        if (n.getData("code").toString().equalsIgnoreCase("iif") && n.getData("class").toString().equalsIgnoreCase("BooleanExpression") ) {
            StringBuffer sb = new StringBuffer(); 
            sb.append("(");  
            sb.append( getColorView(n.getData("args").getData("param1").toString()) );  
            sb.append(getDefaultView(n.getData("args").getData("parama").toString(),"","=="));  
            sb.append(getColorView(n.getData("args").getData("param2").toString()));  
            if (n.getData("args").getData("paramb").toString().equals("")||n.getData("args").getData("paramb").toString().toLowerCase().contains("not use")) {
            }else{
                sb.append((n.getData("args").getData("paramb").toString()));  
                sb.append(getColorView(n.getData("args").getData("param3").toString()));  
                sb.append(getDefaultView(n.getData("args").getData("paramc").toString(),"","=="));  
                sb.append(getColorView(n.getData("args").getData("param4").toString())); 
            }
            if (n.getData("args").getData("paramd").toString().equals("")||n.getData("args").getData("paramd").toString().toLowerCase().contains("not use")) {
            }else{
                sb.append((n.getData("args").getData("paramd").toString())); 
                sb.append(getColorView(n.getData("args").getData("param5").toString()));
                sb.append(" == "); 
                sb.append(getColorView(n.getData("args").getData("param6").toString())); 
            }
            
            sb.append(")");  
            return(sb.toString()) ;
 
        }
        return null;
    }
    private static String getFVequal(Nset n){
        if (n.getData("code").toString().equalsIgnoreCase("equal") && n.getData("class").toString().equalsIgnoreCase("StringComparationExpression") ) {
            String[] keys = n.getData("args").getObjectKeys();
            StringBuffer sb = new StringBuffer(""); 
            sb.append("(");  
            sb.append( getColorView(n.getData("args").getData("param1").toString()) );  
            sb.append("equal");  
            sb.append(getColorView(n.getData("args").getData("param2").toString()));  
            sb.append(")");  
            return(sb.toString()) ;
 
        }
        return null;
    }
    private static String getFVstartwith(Nset n){
        if (n.getData("code").toString().equalsIgnoreCase("startwith") && n.getData("class").toString().equalsIgnoreCase("StringComparationExpression") ) {
            String[] keys = n.getData("args").getObjectKeys();
            StringBuffer sb = new StringBuffer(""); 
            sb.append("(");  
            sb.append( getColorView(n.getData("args").getData("param1").toString()) );  
            sb.append("startwith");  
            sb.append(getColorView(n.getData("args").getData("param2").toString()));  
            sb.append(")");  
            return(sb.toString()) ;
 
        }
        return null;
    }
    private static String getFVcontain(Nset n){
        if (n.getData("code").toString().equalsIgnoreCase("contain") && n.getData("class").toString().equalsIgnoreCase("StringComparationExpression") ) {
            String[] keys = n.getData("args").getObjectKeys();
            StringBuffer sb = new StringBuffer(""); 
            sb.append("(");  
            sb.append( getColorView(n.getData("args").getData("param1").toString()) );  
            sb.append("contain");  
            sb.append(getColorView(n.getData("args").getData("param2").toString()));  
            sb.append(")");  
            return(sb.toString()) ;
 
        }
        return null;
    }
    
private static String wrapLogic(String s){
    if (s.startsWith("@[")||s.startsWith("@{")||s.startsWith("@#")) {
    }else if (s.startsWith("@")||s.startsWith("$")||s.startsWith("!")) {
        return " "+s+ " ";
    }            
    return " '"+(s.length()>=12?s.substring(0,12)+"...":s )+"' ";
}
    private static boolean isVariable(String s){
        s=s.trim();
        return s.startsWith("@")||s.startsWith("$")||s.startsWith("!")||s.startsWith("#")||s.startsWith("&");
    }
    private String expression = "";
    private String action = "";
    private String comment = "";
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("logic-tblLogic") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("logic-txtSearch");
        
        Component cmpBtnAdd =response.getContent().findComponentbyId("logic-lblidcomp") ;
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("logic-txtFinder");
               
        if(cmpBtnAdd.getTag().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select routeid, compid, routeindex, action, expression,1 "+ 
                                                       "from web_route ORDER BY routeindex ASC WHERE compid=?",cmpBtnAdd.getTag());
            tablegrid.setData(nikiset);
                        
            response.getContent().findComponentbyId("logic-btnAdd").setVisible(nikiset.getRows()>=1 ?false:true);
        }
        else{
            String s = "%"+txt.getText()+"%";
            String p = cmpBtnAdd.getTag();
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select routeid, compid, routeindex, action, expression,1 "+ 
                                                       "from web_route where (compid = ? AND action like ? )OR (compid = ? AND expression like ?) ORDER BY routeindex ASC; ",p,s,p,s);
            
            tablegrid.setData(nikiset);
            response.getContent().findComponentbyId("logic-btnAdd").setVisible(nikiset.getRows()>=1|| !txt.getText().equals("") ?false:true);
        }
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setColStyle(5, new Style().setStyle("width", "102px"));
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 3){//action
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Label lbl = new Label();
                    lbl.setId("logic-btnLabel");
                    Nset n = Nset.readJSON(data.getData(row).getData(3).toString());
                    StringBuffer sb = new StringBuffer();
                    String[] keys = n.getData("args").getObjectKeys();
                    sb.append("(");
                    for (int i = 0; i < keys.length; i++) {
                        String s = wrapLogic(n.getData("args").getData(keys[i]).toString());
                        if (s.trim().length()==0) {
                            s="''";
                        }
                        sb.append(i>=1?",":"").append("<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>");
                    }
                    sb.append(")");
                     
                    String flag = Nset.readJSON(data.getData(row).getData(4).toString()).getData("flag").toString();
                    String sact="<a style=\"color:black\">"+n.getData("class").toString().replace("Action", "")+(n.getData("code").toString().equals("")?"":".")+WordUtils.capitalizeFully(n.getData("code").toString())+"</a>"+ (keys.length!=0?sb.toString():""); 
                    if (flag.equals("hide")) {
                        lbl.setText("<strike><em>"+sact+"</em></strike>"); 
                    }else{
                        lbl.setText(sact); 
                    } 
                    if (n.getData("class").toString().equals("") && n.getData("code").toString().equals("")) {
                        String color = n.getData("args").getData("param2").toString().trim();
                        lbl.setText("<a style=\"color:"+(color.equals("")?"#7f7f7f":color)+"\"><em>"+n.getData("args").getData("param1").toString()+"</em></a>"); 
                        comment=n.getData("args").getData("param1").toString();
                        if (!comment.equals("")) {
                            horisontalLayout.setStyle(new Style().setAttr("n-td-colspan", "2"));
                        }                        
                    }else{
                        comment="";
                    }
                    if (n.getData("class").toString().trim().length()!=0) {
                        action="true";
                    }else{
                        action="";
                    }
                    
                    Component btn = new Image();
                    btn.setId("logic-btnAction["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("logicid", data.getData(row).getData(0)).setData("action", data.getData(row).getData(3)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    if (!comment.equals("")) {
                        horisontalLayout.addComponent(lbl);
                        horisontalLayout.addComponent(btn);
                        btn.getStyle().setStyle("float", "right");
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("n-table-width", "100%");        
                        
                        
                    }else{
                        horisontalLayout.addComponent(btn);
                        horisontalLayout.addComponent(lbl);
                    }
                    if (flag.equals("hide")) {
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("opacity","0.3");
                    }
    
                    return horisontalLayout;      
                }
                if(col == 4){//exp
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Label lbl = new Label();
                    lbl.setId("logic-btnLabel");
                    
                    
                    Nset n = Nset.readJSON(data.getData(row).getData(4).toString());
                    /*
                    StringBuffer sb = new StringBuffer();
                    String[] keys = n.getData("args").getObjectKeys();
                    sb.append("(");
                    for (int i = 0; i < keys.length; i++) {
                        String s = wrapLogic(n.getData("args").getData(keys[i]).toString());
                        if (s.trim().length()==0) {
                            s="''";
                        }
                        sb.append(i>=1?",":"").append("<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>");
                    }
                    sb.append(")");
                    expression="<a style=\"color:black\">"+n.getData("class").toString().replace("Expression", "")+ (n.getData("code").toString().equals("")?"":".")+WordUtils.capitalizeFully(n.getData("code").toString())+"</a>"+ (keys.length!=0?sb.toString():"") ;
                    */
                    expression=expFriendlyView(n);
                    
                    if (n.getData("flag").toString().equals("hide")) {
                        lbl.setText("<strike><em>"+expression+"</em></strike>"); 
                    }else{
                        lbl.setText(expression); 
                    }                    
                    if (n.getData("class").toString().trim().length()==0) {
                        if (action.equals("true")) {
                            lbl.setText("<a style=\"color:#7f7f7f\"><em>Block</em></a>"); 
                        }
                    }
                    if (!comment.equals("")) {
                        horisontalLayout.setStyle(new Style().setStyle("n-td-display", "none"));
                    }
                            
                    
                    Component btn = new Image();
                    btn.setId("logic-btnExpression["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("logicid", data.getData(row).getData(0)).setData("expression", data.getData(row).getData(4)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    if (n.getData("flag").toString().equals("hide")) {
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("opacity","0.3");
                    }
                    horisontalLayout.addComponent(btn);
                    horisontalLayout.addComponent(lbl);
    
                    return horisontalLayout;      
                }
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("logic-btnUp["+row+"]");
                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    btn.setText("img/up.png");
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("logic-btnDown["+row+"]");                    
                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    btn.setText("img/down.png");
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                                            
                    btn = new Image();
                    btn.setId("logic-btnCopy["+row+"]");
                    btn.setText("img/copy.png");
                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
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
                    btn.setId("logic-btnInsert["+row+"]");
                    btn.setText("img/add.png");
                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
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
                    btn.setId("logic-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
                    btn.setTag(data.getData(row).getData(0).toString());
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
    
}
