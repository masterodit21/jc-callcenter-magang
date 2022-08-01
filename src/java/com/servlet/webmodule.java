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

/**
 *
 * @author user
 */
public class webmodule extends NikitaServlet{
    
    /*parameter
    mode
    data
    */
    NikitaConnection nikitaConnection ;   
    int dbCore;
    String user ;  
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Module");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        final Label lblmode = new Label();
        lblmode.setId("modul-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Textsmart txt = new Textsmart();
        txt.setId("modul-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setText(request.getParameter("search"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("modul-txtSearch_text");
            }
        }); 
        
        Component btn = new Image();
        btn.setId("modul-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        if(!lblmode.getTag().equals("mobile")){
            horisontalLayout.addComponent(btn);
        }
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webmoduleadd", request,"add",true);
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("modul-tblModul");
        tablegrid.setDataHeader(Nset.readsplitString("|Module Id|Name|Title|Style|")); //set header table
        style = new Style();
        style.setStyle("width", "30px");
        tablegrid.setColStyle(4, style);
        tablegrid.showRowIndex(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(0, true);
        if(lblmode.getTag().equals("mobile")){            
            tablegrid.setColHide(5, true);
        }
        nf.addComponent(tablegrid);
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                fillGrid(response);
                if(lblmode.getTag().equals("mobile")){                    
                    Nset result = Nset.newObject();
                    result.setData("namemodule", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString()); 
                    result.setData("idmodule", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());  
                    
                    response.closeform(response.getContent());
                    response.setResult("OK", result);
                }else{
                    
                    request.setParameter("moduleid", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                    request.setParameter("modulename", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());
                    request.setParameter("mode","comp");
                    response.showform("webmoduleform", request, "module", true);
                }
                response.write();
            }
        });
         
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                    fillGrid(response);
                    response.writeContent();
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
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT modulename FROM web_module WHERE moduleid=?", result.getData("id").toString());
            
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", nikitaset.getText(0, 0));
                    data.setData("activitytype", "module");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    nikitaset = nikitaConnection.Query("delete from web_module where moduleid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("modul-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webmoduleadd", request,"edit", true);
                response.write();

            }
        }); 
        
        /*
        comp = new Component();
        comp.setId("modul-btnAsset");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(component.getTag());
                request.setParameter("moduleid", nset.getData(1).toString());
                request.setParameter("modulename", nset.getData(2).toString());
                request.setParameter("mode","asset");
                response.showform("webmoduleasset", request, "asset", true);
                
                response.write();

            }
        }); 
        */
        
        comp = new Component();
        comp.setId("modul-btnResource");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(component.getTag());
                request.setParameter("moduleid", nset.getData(1).toString());
                request.setParameter("modulename", nset.getData(2).toString());
                request.setParameter("mode","resource");
                response.showform("webmoduleresource", request, "resource", true);
                
                response.write();

            }
        }); 
        
        comp = new Component();
        comp.setId("modul-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("modul-tblModul");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("modul-txtSearch");               
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,moduleid,modulename,moduletitle,modulestyle,1 "+ 
                                                       "from web_module");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,moduleid,modulename,moduletitle,modulestyle,1 "+ 
                                                       "from web_module where modulename like ? OR moduleid like ? ; ",s,s);
            tablegrid.setData(nikiset);
        }
        tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("modul-btnResource["+row+"]");
                    btn.setText("img/browse.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {

                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    
                    btn = new Image();
                    btn.setId("modul-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());

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
                    btn.setId("modul-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setTag(data.getData(row).getData(1).toString());
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
