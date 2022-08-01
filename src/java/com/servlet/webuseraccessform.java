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
public class webuseraccessform extends NikitaServlet{
int dbCore;
NikitaConnection nikitaConnection ;
String user ; 
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("User Can Access");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
                
        Textsmart txt = new Textsmart();
        txt.setId("mf-txtSearch");
        txt.setLabel("Searching");
        txt.setTag(request.getParameter("username"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("mf-tblModul"));
            }
        }); 
        
        Component btn = new Image();
        btn.setId("mf-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                request.setParameter("code", "selectedmodul"); 
                request.setParameter("users", response.getContent().findComponentbyId("mf-txtSearch").getTag());
                response.showform("webuser", request,"user",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("mf-tblModul");
        tablegrid.setDataHeader(Nset.readsplitString("No|User Name|Name|")); //set header table
        style = new Style();
        style.setStyle("width", "10px");
        tablegrid.setColStyle(3, style);  
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);
        
         
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
                                
                if(reqestcode.equals("user") && responsecode.equals("OK")){
                    Nikitaset n = nikitaConnection.Query("SELECT accessusername FROM sys_user_access WHERE username=? AND accessusername=?", response.getContent().findComponentbyId("mf-txtSearch").getTag(), result.getData("id").toString());
                    if (n.getRows()==0) {
                        nikitaConnection.Query("INSERT INTO sys_user_access (username, accessusername,createdby,createddate) VALUES (?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getContent().findComponentbyId("mf-txtSearch").getTag(), result.getData("id").toString(),user);
                        Nikitaset nikitaset = nikitaConnection.Query("SELECT MAX(accessid) FROM sys_user_access");
            
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", nikitaset.getText(0, 0));
                        data.setData("activitytype", "useraccess");
                        data.setData("mode", "add");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);
                    } 
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("delete") && responsecode.equals("button2")){    
                    
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", result.getData("id").toString());
                    data.setData("activitytype", "useraccess");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    Nikitaset nikitaset = nikitaConnection.Query("delete from sys_user_access where accessid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("modul-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                response.write();

            }
        });
        
        
        
        
        if(lblmode.getTag().equals("access")){     
            nf.setText("User Access Form ["+nf.findComponentbyId("mf-txtSearch").getTag()+"]"); 
        }
        else{            
            nf.setText("User Access Form");       
        }
        
        response.setContent(nf);
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("mf-tblModul");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("mf-txtSearch");               
        
        if(txt.getText().equals("")){
            
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),
                                "select sys_user_access.accessid,sys_user_access.accessusername,sys_user.name,1 " +
                                "from sys_user_access LEFT JOIN sys_user ON (sys_user_access.accessusername=sys_user.username) " +
                                "WHERE sys_user_access.username = ? ",txt.getTag());
            tablegrid.setData(nikiset);
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select sys_user_access.accessid,sys_user_access.accessusername,sys_user.name,1 from sys_user_access LEFT JOIN sys_user ON (sys_user_access.accessusername=sys_user.username)"
                    + "WHERE sys_user_access.username = ? AND (sys_user_access.accessusername like ? OR sys_user.name like ?)  ",txt.getTag(),s,s);
            
            tablegrid.setData(nikiset);
            
        }
         tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        //rule1
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
                    horisontalLayout.setStyle(style);
                    
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

