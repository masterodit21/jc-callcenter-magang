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

/**
 *
 * @author user
 */
public class webschmanagement extends NikitaServlet{
    Label idtype ;
    private static final String ALL = "*";
    String priority;
    NikitaConnection nikitaConnection ;
    String user ;
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Scheduller");
        //nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        idtype = new Label();
        idtype.setId("websch-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        Textsmart txt = new Textsmart();
        txt.setId("websch-txtSearch");
        txt.setLabel("Searching");
        horisontalLayout.addComponent(txt);
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
                response.writeContent();
            }
        });
        
        Component btn = new Image();
        btn.setId("websch-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");

   
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode","add");
                request.setParameter("type",idtype.getText());
                response.showform("webschmanagementadd", request,"add", true);
                
                response.write();
            }
        });
        
        //add right forparent
        Combobox combo = new Combobox();
        combo.setId("websch-typez");
        combo.setLabel("Type");
        combo.setData(Nset.readJSON("[{'id':'*','text':'ALL'},{'id':'web','text':'Web Scheduller'},{'id':'mobile','text':'Mobile Scheduller'}]", true));
        combo.setText("*");
        combo.setStyle(new Style().setStyle("n-searchicon", "true").setAttr("n-layout-align", "right").setStyle("n-label-width", "80px").setStyle("width", "200px"));
        combo.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);     
//                response.refreshComponent(response.getContent().findComponentbyId("form-tblSch")); 
                response.writeContent();
            }
        });
        
         
        HorizontalLayout hr = new HorizontalLayout();
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        hr.addComponent(horisontalLayout);
        hr.addComponent(combo);
        nf.addComponent(hr);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("websch-tblSch");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Text|Mode|Describe|")); //set header table
        tablegrid.setRowCounter(true);
        tablegrid.setColStyle(0, Style.createStyle("width", "50px"));
        tablegrid.setColStyle(1, Style.createStyle("width", "150px"));
        tablegrid.setColStyle(2, Style.createStyle("width", "200px"));
        tablegrid.setColStyle(3, Style.createStyle("width", "100px"));
        tablegrid.setColStyle(5, Style.createStyle("width", "100px"));
        nf.addComponent(tablegrid);
         
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                request.setParameter("idthread",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString()   ); 
                request.setParameter("mode","logiclist");
                response.showform("webschtask", request,"edit", true);
                response.write();
            }
        });
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(tablegrid);
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
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT threadname FROM web_scheduller where threadid=?",result.getData("id").toString());
            
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", nikitaset.getText(0, 0));
                    data.setData("activitytype", "scheduller");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    nikitaset = nikitaConnection.Query("delete from web_scheduller where threadid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("websch-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webschmanagementadd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("websch-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                response.write();

            }
        });
        
        comp = new Component();
        comp.setId("websch-start");
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
        comp.setId("websch-stop");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                response.showDialogResult("Stop" , "Do you want to stop?", "start",Nset.newObject().setData("id",n.getData(0).toString() ), "No", "Yes");
                 
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
    }
    
    public void fillGrid(NikitaResponse response){
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("websch-tblSch");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("websch-txtSearch");
        Combobox combo = (Combobox)response.getContent().findComponentbyId("websch-typez") ;
        Nikitaset nikiset;
        if(txt.getText().equals("")){
            if (combo.getText().equals("*")) {
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT threadid,threadname,threadtext,threadmode,1,status "+ 
                                                           "FROM web_scheduller ORDER BY threadid ASC");

            }else{
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT threadid,threadname,threadtext,threadmode,1,status "+ 
                                                           "FROM web_scheduller WHERE threadtype = ? ORDER BY threadid ASC",combo.getText());

            }
                tablegrid.setData(nikiset);
                
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            if (combo.getText().equals("*")) {
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT threadid,threadname,threadtext,threadmode,1,status "+ 
                                                           "FROM web_scheduller WHERE (threadname like ? OR threadtext like ? ) ORDER BY threadid ASC ",s,s);
            }else{
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT threadid,threadname,threadtext,threadmode,1,status "+ 
                                                           "FROM web_scheduller WHERE threadtype = ? AND  (threadname like ? OR threadtext like ? ) ORDER BY threadid ASC ",combo.getText(),s,s);
                
            }
            tablegrid.setData(nikiset);
        }
        tablegrid.setColStyle(6, new Style().setStyle("width", "50px").setStyle("width", "50px") );
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    Component 
                    
                    btn = new Image();
                    btn.setId("websch-start["+row+"]");
                    if(data.getData(row).getData(5).toString().equals("0")) {                  
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
                    btn.setId("websch-stop["+row+"]");
                    btn.setText("img/schstop.png");
                    btn.setTag(data.getData(row).getData(0).toString());

                    if(data.getData(row).getData(5).toString().equals("1")) {                  
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
                    btn.setId("websch-btnEdit["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px").setStyle("margin-left","9px")); 
                    btn.setText("img/edit.png");
                    btn.setTag(data.getData(row).getData(0).toString());

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
                    btn.setId("websch-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setTag(data.getData(row).getData(0).toString());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {

                        @Override
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
