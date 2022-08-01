/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
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
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author rkrzmail
 */
public class weblogicflow extends NikitaServlet{
    
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
        horisontalLayout.setVisible(false);
        horisontalLayout.addComponent(txt);
                
 
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                fillGrid(response);        
                 response.refreshComponent("logic-tblLogic");
                response.write();
            }
        });
        
        Image btn = new Image();
        btn.setId("logic-btnAdd");
        btn.setText("img/add.png");
        btn.setStyle(new Style().setStyle("margin-top", "6px"));
        btn.setTag(request.getParameter("idcomp"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
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
        
       
        nf.addComponent(horisontalLayout);
        
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
        
        
        DivLayout divLayout = new DivLayout();
        divLayout.setId("logic-flow");
        nf.addComponent(divLayout);  
        
        
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
                    
                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_route where routeid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("copy") && responsecode.equals("copy-logic")){
                
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
            
            nf.setText("Logic Flow ["+nikitaset.getText(0, 0)+"]");       
            idform.setText(nikitaset.getText(0, 0));
        }
        else{            
            nf.setText("Logic Flow");       
        }
        
        
        
        response.setContent(nf);        
        nf.setStyle(new Style().setStyle("width", "480"));
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);

            }
        });
        
    }
    
    public Component flowcondition(){
        DivLayout comp = new DivLayout();
        comp.setStyle(new Style().setStyle("width","256px").setStyle("height"," 186px"));
        
        Image cond = new Image();
        cond.setText("/static/img/apps/flow/decision.png");
        cond.setStyle(new Style().setStyle("width","128px").setStyle("height"," 128px"));
        cond.getStyle().setStyle("position", "absolute").setStyle("margin-top", "0px").setStyle("margin-left", "0px");
        
        DivLayout rflow = new DivLayout();
        rflow.setStyle(new Style().setStyle("width","192px").setStyle("height","48px").setStyle("background-image","url(static/img/apps/flow/horizontal.png)").setStyle("background-repeat","repeat-x"));
        rflow.getStyle().setStyle("position", "absolute").setStyle("margin-top", "42px").setStyle("margin-left", "64px");
        DivLayout upper_right = new DivLayout();
        
        upper_right.setStyle(new Style().setStyle("width","48px").setStyle("height","48px").setStyle("background-image","url(static/img/apps/flow/upper_right.png)"));
        upper_right.getStyle().setStyle("position", "absolute").setStyle("margin-top", "42px").setStyle("margin-left", "256px");
        DivLayout down = new DivLayout();
        down.setStyle(new Style().setStyle("width","128px").setStyle("height","256px").setStyle("background-image","url(static/img/apps/flow/call.png)"));
        down.getStyle().setStyle("position", "absolute").setStyle("margin-top", "89px").setStyle("margin-left", "256px");
        
        
        
        DivLayout fflow = new DivLayout();
        fflow.setStyle(new Style().setStyle("width","48px").setStyle("height","128px").setStyle("background-image","url(static/img/apps/flow/vertikal.png)").setStyle("background-repeat","repeat-y"));
        fflow.getStyle().setStyle("position", "absolute").setStyle("margin-top", "64px").setStyle("margin-left", "42px");
        
        
        comp.addComponent(rflow);
        comp.addComponent(fflow);
        
        comp.addComponent(upper_right);
        comp.addComponent(down);
        comp.addComponent(cond);
        return comp;
    }
    
private String wrapLogic(String s){
    return s.length()>=12?s.substring(0,12)+"...":s;
}
    private boolean isVariable(String s){
        return s.startsWith("@")||s.startsWith("$")||s.startsWith("!")||s.startsWith("#")||s.startsWith("&");
    }
    private String expression = "";
    private String action = "";
    private String comment = "";
    
    public void fillGrid(NikitaResponse response){
        DivLayout divLayout = (DivLayout)response.getContent().findComponentbyId("logic-flow") ;
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("logic-tblLogic") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("logic-txtSearch");
        
        Component cmpBtnAdd =response.getContent().findComponentbyId("logic-lblidcomp") ;
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("logic-txtFinder");
          
        Nikitaset nikiset ;
        if(cmpBtnAdd.getTag().equals("")){
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select routeid, compid, routeindex, action, expression,1 "+ 
                                                       "from web_route ORDER BY routeindex ASC WHERE compid=?",cmpBtnAdd.getTag());
            tablegrid.setData(nikiset);
                        
            response.getContent().findComponentbyId("logic-btnAdd").setVisible(nikiset.getRows()>=1 ?false:true);
        }
        else{
            String s = "%"+txt.getText()+"%";
            String p = cmpBtnAdd.getTag();
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select routeid, compid, routeindex, action, expression,1 "+ 
                                                       "from web_route where (compid = ? AND action like ? )OR (compid = ? AND expression like ?) ORDER BY routeindex ASC; ",p,s,p,s);
            
            tablegrid.setData(nikiset);
            response.getContent().findComponentbyId("logic-btnAdd").setVisible(nikiset.getRows()>=1|| !txt.getText().equals("") ?false:true);
        }
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setColStyle(5, new Style().setStyle("width", "102px"));
        tablegrid.setColHide(4, true);
        tablegrid.setColHide(5, true);
        tablegrid.setStyle(new Style().setAttr("n-table-border", "0").setAttr("n-table-ss", "0"));
          tablegrid.setVisible(false);
          
        Image start_end = new Image();
        start_end.setText("/static/img/apps/flow/start_end.png");
        start_end.setStyle(new Style().setStyle("width","128px").setStyle("height"," 48px"));
         
        divLayout.addComponent( start_end );     
 
        for (int i = 0; i < nikiset.getRows(); i++) {
            divLayout.addComponent( flowcondition());            
        }
        
         start_end = new Image();
        start_end.setText("/static/img/apps/flow/start_end.png");
        start_end.setStyle(new Style().setStyle("width","128px").setStyle("height"," 48px"));
         
        divLayout.addComponent( start_end );     
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
               
                if(col == 3){ 
                      
    
                    return flowcondition();      
                }
                                  
                return null;
            }
        });
    }  
    
}
