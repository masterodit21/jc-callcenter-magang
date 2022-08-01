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
public class webschtask extends NikitaServlet{
    
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
 
        final Label buffered = new Label();
        buffered.setId("logic-buffered");
        buffered.setVisible(false);
        nf.addComponent(buffered);
        
        final Label idthread = new Label();
        idthread.setId("logic-lblidthread");
        idthread.setTag(request.getParameter("idthread"));
        idthread.setVisible(false);
        nf.addComponent(idthread);
         
        
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
        btn.setId("logic-btnAdd");
        btn.setText("img/add.png");
        btn.setStyle(new Style().setStyle("margin-top", "6px"));
        btn.setTag(request.getParameter("idthread"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                
                    
                    Nikitaset nikitaset = nikitaConnection.Query("insert into web_scheduller_task("+
                                          "threadid, taskindex,createdby,createddate)"+
                                          "values(?,1,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("logic-txtFinder").getTag(),user);
                    if(nikitaset.getError().length() > 1){
                        response.showAlert(nikitaset.getError()); 
                        response.write();
                    }else{
                        fillGrid(response);
                        response.writeContent();
                    }
                
                
            }
        });     
        
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(horisontalLayout);
         
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
        tablegrid.setDataHeader(Nset.readsplitString("No,Thread Id, Task Index, Action, Expression, Describe,",","));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(2, true);        
        tablegrid.setColStyle(6, new Style().setStyle("width", "150px"));
        
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
                    
                    
                    nikitaConnection.Query("delete from web_scheduller_task where taskid=?",result.getData("id").toString());
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
                request.setParameter("mode","edit");
                response.showform("webtaskaction", request,"edit", true);             
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
                request.setParameter("mode","edit");
                response.showform("webtaskexpression", request,"edit", true);             
                response.write();
            }
        });   
        
        comp = new Component();
        comp.setId("logic-btnUp");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                    Nset nset = Nset.readJSON(component.getTag());
                
                    Nikitaset nikiset = nikitaConnection.Query("SELECT taskid,taskindex FROM web_scheduller_task WHERE taskindex < ? AND threadid = ? ORDER BY taskindex DESC,taskid DESC", nset.getData(2).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_scheduller_task set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",taskindex=? WHERE taskid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());                
                    nikitaConnection.Query("UPDATE web_scheduller_task set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",taskindex=? WHERE taskid =? ", user,nset.getData(2).toString(),nikiset.getText(0, 0)); 

                    fillGrid(response);
                    response.writeContent();
                

            }
        });
        
        comp = new Component();
        comp.setId("logic-btnDown");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                    
                    Nset nset = Nset.readJSON(component.getTag());
                
                    Nikitaset nikiset = nikitaConnection.Query("select taskid,taskindex from web_scheduller_task WHERE taskindex > ? AND threadid = ? ORDER BY taskindex ASC,taskid ASC", nset.getData(2).toString(), nset.getData(1).toString());
                    nikitaConnection.Query("UPDATE web_scheduller_task set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",taskindex=? WHERE taskid =? ", user,nikiset.getText(0, 1),nset.getData(0).toString());                
                    nikitaConnection.Query("UPDATE web_scheduller_task set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",taskindex=? WHERE taskid =? ", user,nset.getData(2).toString(),nikiset.getText(0, 0)); 
                    
                    fillGrid(response);
                    response.writeContent();
                

            }
        });
        
        
        
        comp = new Component();
        comp.setId("logic-btnInsert");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    
                    Nset nset = Nset.readJSON(component.getTag());

                    nikitaConnection.Query("UPDATE web_scheduller_task SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",taskindex=taskindex+2 WHERE taskindex > ? AND threadid=?", user,nset.getData(2).toString(),nset.getData(1).toString());                
                    nikitaConnection.Query("INSERT INTO web_scheduller_task(taskindex,threadid,createdby,createddate) VALUES(?,?,?,"+WebUtility.getDBDate(dbCore)+") ", (nset.getData(2).toInteger()+1)+"",nset.getData(1).toString(),user);
                    
                    fillGrid(response);
                    response.writeContent();
                

            }
        });
        
        
        comp = new Component();
        comp.setId("logic-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                                  
                    response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                
                response.write();

            }
        });
        
        
        
        comp = new Component();
        comp.setId("logic-start");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                response.showDialogResult("Start" , "Do you want to start?", "start",Nset.newObject().setData("id",n.getData(0).toString() ), "No", "Yes");
                 
                response.write();

            }
        });
        
        
        
        comp = new Component();
        comp.setId("logic-stop");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                response.showDialogResult("Stop" , "Do you want to stop?", "start",Nset.newObject().setData("id",n.getData(0).toString() ), "No", "Yes");
                 
                response.write();

            }
        });
        
        
        
        
        if(lblmode.getTag().equals("logiclist")){     
            Nikitaset nikitaset = nikitaConnection.Query("SELECT threadname FROM web_scheduller WHERE threadid=?", idthread.getTag()); 
            nf.findComponentbyId("logic-txtFinder").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("logic-txtFinder").setTag(idthread.getTag());
            
            nf.setText("Scheduller Task ["+nikitaset.getText(0, 0)+"]");    
        }
        else{            
            nf.setText("Scheduller Task");       
        }
        
        
        
        response.setContent(nf);        
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);

            }
        });
        
    }
     
    private static String getBoldView(String s){
        return "<a style=\"color:#E58B1D\"><b>"+s+"</b></a>";
    }
    private static String getColorView(String s){
        s=wrapLogic(s);
        return "<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>";
    }
    private static String getDefaultView(String s, String comp, String def){
        return s.equals(comp)?def:s;
    }
    private static String getFViif(String  data){    
            Nset n = Nset.readJSON(data);
            StringBuffer sb = new StringBuffer(); 
            sb.append("(");  
            sb.append( getColorView(n.getData("param1").toString()) );  
            sb.append(getDefaultView(n.getData("parama").toString(),"", "=="));  
            sb.append(getColorView(n.getData("param2").toString()));  
            if (n.getData("paramb").toString().equals("")||n.getData("paramb").toString().equals("none")||n.getData("paramb").toString().toLowerCase().contains("not use")) {
            }else{
                sb.append((n.getData("paramb").toString()));  
                sb.append(getColorView(n.getData("param3").toString()));  
                sb.append(getDefaultView(n.getData("paramc").toString(),"","=="));  
                sb.append(getColorView(n.getData("param4").toString())); 
            }
            if (n.getData("paramd").toString().equals("")||n.getData("paramd").toString().toLowerCase().contains("not use")) {
            }else{
                sb.append((n.getData("paramd").toString())); 
                sb.append(getColorView(n.getData("param5").toString()));
                sb.append(" == "); 
                sb.append(getColorView(n.getData("param6").toString())); 
            }
            
            sb.append(")");  
            return  (sb.toString()) ;
     }
     private static String getFVact(String  data){    
            Nset n = Nset.readJSON(data);//{"url":"","resultz":"Result","conn":"Connection","tmode":"transaction","errz":"Error","ntask":"","query":"Query","note":"","respcodez":""}
            StringBuffer sb = new StringBuffer(); 
            
            if (n.getData("tmode").toString().equals("connection")) {
                sb.append( getBoldView("Connection") ).append(" : ");  
                sb.append( getColorView(n.getData("query").toString()) ); 
                
            }else if (n.getData("tmode").toString().equals("transaction")) {
                sb.append( getBoldView("Transaction ") ).append(" : ");  
                sb.append( getColorView(n.getData("query").toString()) ); 
            }else if (n.getData("tmode").toString().equals("ntask")) {
                sb.append( getBoldView("Nikita Task") ).append(" : ");  
                sb.append( getColorView(n.getData("ntask").toString()) );
            }else if (n.getData("tmode").toString().equals("http")) {
                sb.append( getBoldView("Url") ).append(" : ");  
                sb.append( getColorView(n.getData("url").toString()) );
            }else if (n.getData("tmode").toString().equals("break")) {
                sb.append( getBoldView("Break") ).append(" : ");  
                sb.append( "(" );
                sb.append( getColorView(n.getData("note").toString()) );
                sb.append( ")" );
            }else if (n.getData("tmode").toString().equals("deactive")) {
                sb.append( getBoldView("Deactivate") ).append(" : ");  
                sb.append( "(" );
                sb.append( getColorView(n.getData("note").toString()) );
                sb.append( ")" );
            }
              
            return  (sb.toString()) ;
     }
   

    
private static String wrapLogic(String s){
    if (s.startsWith("@[")||s.startsWith("@{")||s.startsWith("@#")) {
    }else if (s.startsWith("@")||s.startsWith("$")||s.startsWith("!")) {
        return " "+s+ " ";
    }            
    return " '"+(s.length()>=35?s.substring(0,35)+"...":s )+"' ";
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
        
        Component cmpBtnAdd =response.getContent().findComponentbyId("logic-lblidthread") ;
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("logic-txtFinder");
               
        if(cmpBtnAdd.getTag().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT taskid, threadid, taskindex, action, expression,1,status "+ 
                                                       "FROM web_scheduller_task ORDER BY taskindex ASC WHERE threadid=?",cmpBtnAdd.getTag());
            tablegrid.setData(nikiset);
                        
            response.getContent().findComponentbyId("logic-btnAdd").setVisible(nikiset.getRows()>=1 ?false:true);
        }
        else{
            String s = "%"+txt.getText()+"%";
            String p = cmpBtnAdd.getTag();
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT taskid, threadid, taskindex, action, expression,1,status "+ 
                                                       "FROM web_scheduller_task WHERE (threadid = ? AND action like ? ) OR (threadid = ? AND expression LIKE ?) ORDER BY taskindex ASC; ",p,s,p,s);
            
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
                    lbl.setText(getFVact(data.getData(row).getData(3).toString())); 
                   
                    
                    Component btn = new Image();
                    btn.setId("logic-btnAction["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("taskid", data.getData(row).getData(0)).setData("action", data.getData(row).getData(3)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    
                    horisontalLayout.addComponent(btn);
                    horisontalLayout.addComponent(lbl);
    
                    return horisontalLayout;      
                }
                if(col == 4){//exp
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Label lbl = new Label();
                    lbl.setId("logic-btnLabel");
                    lbl.setText(getFViif (data.getData(row).getData(4).toString()) ); 
                    //        
                    
                    
                    
                    Component btn = new Image();
                    btn.setId("logic-btnExpression["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("taskid", data.getData(row).getData(0)).setData("expression", data.getData(row).getData(4)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    
                    horisontalLayout.addComponent(btn);
                    horisontalLayout.addComponent(lbl);
    
                    return horisontalLayout;      
                }
                if(col == 6){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    Component 
                    
                    btn = new Image();
                    btn.setId("logic-start["+row+"]");
                    if(data.getData(row).getData(6).toString().equals("0")) {                  
                        btn.setVisible(true);
                    } else{
                        btn.setVisible(false);
                    }
                    btn.setText("img/schstart.png");
                    btn.setTag(data.getData(row).getData(0).toString());

                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });       
                     btn = new Image();
                    btn.setId("logic-stop["+row+"]");
                    btn.setText("img/schstop.png");
                    btn.setTag(data.getData(row).getData(0).toString());

                    if(data.getData(row).getData(6).toString().equals("1")) {                  
                        btn.setVisible(true);
                    } else{
                        btn.setVisible(false);
                    }
                    style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });        
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                    btn = new Image();
                    btn.setId("logic-btnUp["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    
                    btn.setText("img/up.png");
                    
                    style = new Style();
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
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    
                    btn.setText("img/down.png");
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                                            
                   
                    btn = new Image();
                    btn.setId("logic-btnInsert["+row+"]");
                    btn.setText("img/add.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                                       
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("logic-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    
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
