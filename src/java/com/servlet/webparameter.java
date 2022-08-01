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
import com.nikita.generator.NikitaService;
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
public class webparameter extends NikitaServlet{
    Label idtype ;
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
        nf.setText("Parameter");
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        idtype = new Label();
        idtype.setId("webparam-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        Textsmart txt = new Textsmart();
        txt.setId("webparam-txtSearch");
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
        btn.setId("webparam-btnAdd");
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
                response.showform("webparameteradd", request,"add", true);
                
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webparam-tblParam");
        tablegrid.setDataHeader(Nset.readsplitString("No|Key|Value|Describe|Type|Priority|")); //set header table
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);
         
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
                    Nikitaset nikitaset;
                    if (NikitaService.isModeCloud()) {
                        nikitaset = nikitaConnection.Query("SELECT paramkey FROM web_parameters where paramkey=? AND createdby = ?",result.getData("id").toString(), user);
                    }else{
                        nikitaset = nikitaConnection.Query("SELECT paramkey FROM web_parameter where paramkey=?",result.getData("id").toString());
                    }
                    
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", nikitaset.getText(0, 0));
                    data.setData("activitytype", "parameter");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    if (NikitaService.isModeCloud()) {
                        nikitaset = nikitaConnection.Query("delete from web_parameters where paramkey=? AND createdby = ?",result.getData("id").toString(), user);
                    }else{
                        nikitaset = nikitaConnection.Query("delete from web_parameter where paramkey=?",result.getData("id").toString());
                    }
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("webparam-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                request.setParameter("type",idtype.getText());
                response.showform("webparameteradd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("webparam-btnDelete");
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
        Nikitaset ns = nikitaConnection.Query("SELECT position FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
        String user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        if (ns.getText(0, 0).equals("9") || ns.getText(0, 0).equals("3")) {
            priority = "'system','admin','user'";
        }else if (ns.getText(0, 0).equals("2")) {
            priority = "'admin','user'";
        }else{
            priority = "'user'";
        }   
        response.getContent().setText((idtype.getText().equals("link")?"Link Parameter":(idtype.getText().equals("webform")?"Web Parameter":(idtype.getText().equals("mobileform")?"Mobile Parameter":"Parameter"))));
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webparam-tblParam");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("webparam-txtSearch");
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,paramkey,paramvalue,paramdescribe,paramtype,parampriority,1 "+ 
                                                       "from "+(NikitaService.isModeCloud()?"web_parameters where createdby = '"+user+"' AND ":"web_parameter where")+"  paramtype=? AND parampriority IN ("+priority+") ORDER BY paramtype,paramkey",idtype.getText().equals("mobileform")?"mobile":"web/link");
            
            tablegrid.setData(nikiset);
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,paramkey,paramvalue,paramdescribe,paramtype,parampriority,1 "+ 
                                                       "from "+(NikitaService.isModeCloud()?"web_parameters where createdby = '"+user+"' AND ":"web_parameter where")+"  (paramkey like ? OR paramvalue like ? )AND paramtype=? AND parampriority IN ("+priority+") ORDER BY paramtype,paramkey ",s,s,idtype.getText().equals("mobileform")?"mobile":"web/link");
            tablegrid.setData(nikiset);
        }
        tablegrid.setColStyle(6, new Style().setStyle("width", "50px").setStyle("width", "50px") );
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 6){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("webparam-btnEdit["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setText("img/edit.png");
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
                    btn.setId("webparam-btnDelete["+row+"]");
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
