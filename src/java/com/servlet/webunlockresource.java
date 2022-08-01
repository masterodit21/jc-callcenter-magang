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
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webunlockresource extends NikitaServlet{
    
String user ; 
Nikitaset formexist;
int dbCore;
NikitaConnection nikitaConnection ;
@Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Unlock Resources");        
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Textsmart txt = new Textsmart();
        txt.setId("con-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("user"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("con-tblConn"));
            }
        }); 
               
        nf.addComponent(txt);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("con-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|fname|Size|Type|Describe|Created By|"));
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(4, true);
        tablegrid.setColHide(6, true);
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
                
                if(reqestcode.equals("unlock") && responsecode.equals("button2")){
                    
                    
                        Nikitaset nikitaset = nikitaConnection.Query("INSERT INTO sys_lock("+
                                              "resid,createdby,createddate)"+
                                              "VALUES(?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              result.getData("id").toString(),
                                              response.getContent().findComponentbyId("con-txtSearch").getTag());
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            response.showDialogResult("Information", "Unlock resources success", "",null, "OK", "");
                        }
                    
                    
                }
                if(reqestcode.equals("lock") && responsecode.equals("button2")){
                    Nikitaset nikitaset = nikitaConnection.Query("DELETE FROM sys_lock WHERE resid=?",result.getData("id").toString());
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        response.showDialogResult("Information", "Lock resources success", "",null, "OK", "");
                    }
                    
                }
                
                fillGrid(response);
                response.writeContent();
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("con-btnLock");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                formexist = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", component.getTag());    
                if(formexist.getText(0, 0).equals("")){
                    response.showDialogResult("Warning", "Resources already lock", "lockz",null, "OK", "");
                }else{
                    response.showDialogResult("Lock", "Do you want to lock?", "lock",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                }
                response.write();


            }
        });        
        
        comp = new Component();
        comp.setId("con-btnUnlock");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                formexist = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", component.getTag());    
                if(formexist.getText(0, 0).equals(component.getTag())){                        
                    response.showDialogResult("Warning", "Resources already unlock", "unlockz",null, "OK", "");
                }else{
                    response.showDialogResult("Unlock", "Do you want to unlock?", "unlock",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
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
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("con-tblConn") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("con-txtSearch");
        
//        NikitaConnection nikitaConnection = NikitaConnection.getConnection("logic");
       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT resid,resname,resfname,ressize,restype,resdescribe,createdby,reshash "+ 
                                                       "FROM web_resource WHERE createdby=?",txt.getTag());
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT resid,resname,resfname,ressize,restype,resdescribe,createdby,reshash "+ 
                                                       "FROM web_resource WHERE createdby=? AND (resname LIKE ? OR resfname LIKE ?) ; ",txt.getTag(),s,s);
            tablegrid.setData(nikiset);
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 7){
                    formexist = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", data.getData(row).getData(0).toString());    
                    
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).getData(0).toString());
                    btn.setTag(data.getData(row).getData(0).toString());
                    
                    if(formexist.getText(0, 0).equals(data.getData(row).getData(0).toString())){                        
                        btn.setId("con-btnLock["+row+"]");
                        btn.setText("img/lock.png"); 
                    }else{        
                        btn.setId("con-btnUnlock["+row+"]");
                        btn.setText("img/unlock.png");                
                    }                     
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);        
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
