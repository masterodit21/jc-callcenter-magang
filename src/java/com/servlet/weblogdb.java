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
 * @author rkrzmail
 */
public class weblogdb extends NikitaServlet{
     /*parameter
    search
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
        nf.setText("System LogDB");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("log-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("log-tblLog"));
            }
        }); 
        horisontalLayout.addComponent(txt);
                
        Button btn = new Button();
        btn.setId("log-btndeleteall");
        btn.setText("Delete All");
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete all?", "deleteall",null, "No", "Yes");
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
         
 
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("log-tblLog");
        tablegrid.setDataHeader(Nset.readsplitString("No|Message|Created|"));
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);        
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
        
        Component comp = new Component();
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
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                Nikitaset nikitaset;
                
                nikitaset = nikitaConnection.Query("SELECT message FROM web_log WHERE id=?", result.getData("id").toString());
                //history timesheet
                Nset data = Nset.newObject(); 
                data.setData("username", user);
                data.setData("application", "Nikita Generator");
                data.setData("activitytype", "logdb");
                data.setData("additional", "");
                data.setData("activityname", nikitaset.getText(0, 0));
                data.setData("mode", "delete");
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    
                    Utility.SaveActivity(data, response);
                    
                    nikitaset = nikitaConnection.Query("delete from web_log where id=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("deleteall") && responsecode.equals("button2")){
                    
                    nikitaset = nikitaConnection.Query("TRUNCATE TABLE web_log");
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("log-tblLog") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("log-txtSearch");
                       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select id,message,CONCAT(createddate,' ', createdby),1 "+ 
                                                       "from web_log");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select id,message,CONCAT(createddate,' ', createdby),1 "+ 
                                                       "from web_log where message like ? OR createdby like ?; ",s,s);
            tablegrid.setData(nikiset);
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 3){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("modul-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).getData(0).toString());
                    Style style = new Style();
                    style.setStyle("float", "right");
//                    style.setStyle("width", "15px");
                    horisontalLayout.setStyle(style);
                    btn.setOnClickListener(new Component.OnClickListener() {

                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    
                    horisontalLayout.addComponent(btn);
                    return horisontalLayout;                    
                }
                return null;
            }
        });
        
        
    }
    
    
    
}
