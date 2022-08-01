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
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webunlockform extends NikitaServlet{
    
String user ; 
Nikitaset formexist;
int dbCore;
NikitaConnection nikitaConnection ;
@Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Unlock Form");        
        
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
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Title|Type|Style|Describe|Created By|"));
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
                    
                    
                        Nikitaset nikitaset = nikitaConnection.Query("INSERT INTO sys_lock_form("+
                                              "formid,createdby,createddate)"+
                                              "VALUES(?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              result.getData("id").toString(),
                                              response.getContent().findComponentbyId("con-txtSearch").getTag());
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            nikitaset = nikitaConnection.Query("SELECT formname FROM web_form where formid=?",result.getData("id").toString());
            
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", nikitaset.getText(0, 0));
                            data.setData("activitytype", "unlockform");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                            
                            response.showDialogResult("Information", "Unlock form success", "",null, "OK", "");
                        }
                    
                    
                }
                if(reqestcode.equals("lock") && responsecode.equals("button2")){
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT formname FROM web_form where formid=?",result.getData("id").toString());
            
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", nikitaset.getText(0, 0));
                    data.setData("activitytype", "unlockform");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    nikitaset = nikitaConnection.Query("DELETE FROM sys_lock_form WHERE formid=?",result.getData("id").toString());
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        response.showDialogResult("Information", "Lock form success", "",null, "OK", "");
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
                formexist = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", component.getTag());    
                if(formexist.getText(0, 0).equals("")){
                    response.showDialogResult("Warning", "Form already lock", "lockz",null, "OK", "");
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
                formexist = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", component.getTag());    
                if(formexist.getText(0, 0).equals(component.getTag())){                        
                    response.showDialogResult("Warning", "Form already unlock", "unlockz",null, "OK", "");
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
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE createdby=?",txt.getTag());
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT formid,formname,formtitle,formtype,formstyle,formdescribe,createdby,formindex "+ 
                                                       "FROM web_form WHERE createdby=? AND (formname LIKE ? OR formtitle LIKE ?) ; ",txt.getTag(),s,s);
            tablegrid.setData(nikiset);
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 7){
                    formexist = nikitaConnection.Query("SELECT formid FROM sys_lock_form WHERE formid = ? ", data.getData(row).getData(0).toString());    
                    
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
