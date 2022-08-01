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
import com.nikita.generator.ui.Image;
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
public class webuser extends NikitaServlet{
  
    NikitaConnection nikitaConnection;    
    String user ;
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {    
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);  
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("User");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("webuser-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("users"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("webuser-tblUser"));
                response.refreshComponent(response.getContent().findComponentbyId("webuser-btnAdd"));
            }
        }); 
                
        Component btn = new Image();
        btn.setId("webuser-btnAdd");
        btn.setText("img/add.png");
        btn.setTag(request.getParameter("code"));
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode","add");
                response.showform("webuseradd", request,"add", true);
                response.write();
            }
        });        
               
        if (NikitaService.isModeCloud()) {
           horisontalLayout.setVisible(false);            
        }
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webuser-tblUser");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|User Name|Password|Imei|Position|"));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(5, true);
        nf.addComponent(tablegrid);       
        tablegrid.setStyle(new Style().setStyle("n-ondblclick", "true"));     
        
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
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT username,position FROM sys_user where userid=?",result.getData("id").toString());
                    
                    if (NikitaService.isModeCloud()) {                           
                    }else if (Utility.getInt(nikitaset.getText(0, 1))!=9) {                   
                    }else{
                          //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", nikitaset.getText(0, 0));
                        data.setData("activitytype", "user");
                        data.setData("mode", "delete");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);                    
                        
                        
                        nikitaset = nikitaConnection.Query("delete from sys_user where userid=?",result.getData("id").toString());
                        fillGrid(response);
                        response.writeContent();
                    }                    
                    
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("webuser-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webuseradd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("webuser-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if (NikitaService.isModeCloud()) {   
                    response.showDialog("Delete", "Can't Delete Yourself",   "", "OK");
                    response.write();
                }else{
                    response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                    response.write();
                }
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
    private int position = 0;
    private String username = "";
    private Nikitaset nikiset;
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webuser-tblUser") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webuser-txtSearch");
                
        Nikitaset ns = nikitaConnection.Query("SELECT position FROM sys_user WHERE username=?", response.getVirtualString("@+SESSION-LOGON-USER"));
        position=Utility.getInt((ns.getText(0, 0)));
        username=response.getVirtualString("@+SESSION-LOGON-USER");
        
        response.getContent().findComponentbyId("webuser-btnAdd").setVisible(position>=1?true:false);
          
        if(response.getContent().findComponentbyId("webuser-btnAdd").getTag().equals("selectedmodul")){
            response.getContent().findComponentbyId("webuser-btnAdd").setVisible(false);
            tablegrid.setColHide(6, true);            
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webuser-tblUser") ;
                    Nset nset = Nset.newObject();
                    nset.setData("id", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());
                    response.setResult("OK",nset);
                    response.closeform(response.getContent());
                    
                    response.write();
                }
            });
            String user = response.getVirtualString("@+SESSION-LOGON-USER");

            if(txt.getText().equals("")){
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),
                            "SELECT sys_user.userid,sys_user.name,sys_user.username " +
                            "FROM sys_user " +
                            "LEFT JOIN sys_user_access ON ( sys_user_access.accessusername = sys_user.username ) " +
                            "WHERE "+(NikitaService.isModeCloud()?"sys_user.username = '"+user+"' AND ":"")+"   sys_user.username NOT IN (?) " +
                            "AND sys_user.username NOT IN (SELECT sys_user_access.accessusername FROM sys_user_access WHERE sys_user_access.username = ?" +
                            " GROUP BY sys_user_access.accessusername ) GROUP BY sys_user.userid, sys_user.name, sys_user.username ",txt.getTag(),txt.getTag());
                tablegrid.setData(nikiset);
            }
            else{
                String s = "%"+txt.getText()+"%";
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),
                            "SELECT sys_user.userid,sys_user.name,sys_user.username " +
                            "FROM sys_user " +
                            "LEFT JOIN sys_user_access ON ( sys_user_access.accessusername = sys_user.username ) " +
                            "WHERE "+(NikitaService.isModeCloud()?"sys_user.username = '"+user+"' AND ":"")+"  sys_user.username NOT IN (?) " +
                            "AND sys_user.username NOT IN (SELECT sys_user_access.accessusername FROM sys_user_access WHERE sys_user_access.username = ? GROUP BY sys_user_access.accessusername) "+ 
                            "AND (sys_user.username LIKE ? OR sys_user.name LIKE ?) GROUP BY sys_user.userid, sys_user.name, sys_user.username; ",txt.getTag(),txt.getTag(),s,s);
                
                tablegrid.setData(nikiset);
            }
            
        }else{
            if(txt.getText().equals("")){
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select userid,name,username,password,imei,position,avatar,1 "+ 
                                                           "from sys_user "+(NikitaService.isModeCloud()?"WHERE username = '"+user+"' ":""));
                tablegrid.setData(nikiset);
            }
            else{
                String s = "%"+txt.getText()+"%";
                nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select userid,name,username,password,imei,position,avatar,1 "+ 
                                                           "from sys_user where "+(NikitaService.isModeCloud()?"username = '"+user+"' AND ":"")+"  username like ? ; ",s);
                tablegrid.setData(nikiset);
            }
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 6 && (position>Utility.getInt(data.getData(row).getData(5).toString()) || data.getData(row).getData(2).toString().equals( username)  )   ){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("webuser-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                                        
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    if (data.getData(row).getData(2).toString().equals( username) && Utility.getInt(data.getData(row).getData(5).toString())==0 ) {
                    }else{                    
                        btn = new Image();
                        btn.setId("webuser-btnDelete["+row+"]");
                        btn.setText("img/minus.png");
                        btn.setTag(data.getData(row).getData(0).toString());
                        horisontalLayout.addComponent(btn);
                        btn.setOnClickListener(new Component.OnClickListener() {
                            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                            }
                        });   
                    }
                    return horisontalLayout;
                }  else if(col == 6){
                    Component component = new Component();
                    component.setVisible(false);
                    component.setText("");
                    return component;
                }  else if(col == 4){
                    Component component = new Component();
                    component.setVisible(false);
                    component.setText( Utility.insertString(data.getData(row).getData(4).toString(), " ", 4) );
                    return component;
                }  else if(col == 3){
                    Component component = new Component();
                    component.setVisible(false);
                    component.setText( data.getData(row).getData(3).toString().equals("")?"":"******");
                    return component;
                }               
                return null;
            }
        });
    }
}
