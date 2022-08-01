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
import com.nikita.generator.ui.Checkbox;

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
public class webmessageoutbox extends NikitaServlet{

    /*parameter
    search
    mode
    data
    */
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        
        Style style = new Style();
        
        Label lbl = new Label();
        lbl.setId("outbox-title");
        lbl.setText("Outbox");
        style.setStyle("font-family", "Times new roman");
        style.setStyle("margin-bottom", "30px");
        style.setStyle("font-weight", "bold");
        style.setStyle("margin-top", "10px");
        style.setStyle("font-size", "20px");
        style.setStyle("border-style", "none");
        lbl.setStyle(style);
        nf.addComponent(lbl);
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("outbox-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                //response.writeContent("con-txtSearch_text");
                response.refreshComponent(response.getContent().findComponentbyId("outbox-tblMsg"));
            }
        }); 
          
        nf.addComponent(horisontalLayout);
        
        
        
        Checkbox chk = new Checkbox(); 
        chk.setId("outbox-chkall");
        chk.setData(Nset.readJSON("[['chkall','Check All']]", true)); 
        chk.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                if(response.getContent().findComponentbyId("outbox-chkall").getText().contains("chkall")){
                    response.getContent().findComponentbyId("outbox-tblMsg").setText("[*]");
                }else{
                    response.getContent().findComponentbyId("outbox-tblMsg").setText("[]");
                }
                    response.refreshComponent(response.getContent().findComponentbyId("outbox-tblMsg"));
            }
        });
        nf.addComponent(chk);
        
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("outbox-tblMsg");
        tablegrid.setDataHeader(Nset.readsplitString("No|Sender|Message|Sent Date|"));
        tablegrid.showRowIndex(false, true);tablegrid.setColHide(0, true);
        nf.addComponent(tablegrid);        
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);              
                nikitaConnection.Query("UPDATE nikita_message SET msgread = '1' WHERE messageid=? ",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                request.setParameter("msgid", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                request.setParameter("mode","outbox");
                response.showform("webcomposemsg", request, "outbox", true);
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
                
                fillGrid(response);
                response.writeContent();
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("outbox-tblMsg") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("outbox-txtSearch");
                       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT messageid,sender,body,sdate,1 "+ 
                                                       "FROM nikita_message WHERE status = 'outbox' AND username = ? ORDER BY sdate DESC ",user);
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT messageid,sender,body,sdate,1 "+ 
                                                       "FROM nikita_message WHERE status = 'outbox' AND (sender LIKE ? OR body LIKE ?) AND username = ? ORDER BY sdate DESC ; ",s,s,user);
            tablegrid.setData(nikiset);
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 4){
                    Nikitaset flag = nikitaConnection.Query("SELECT msgread FROM nikita_message WHERE messageid = ? ", data.getData(row).getData(0).toString());    
                    
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).getData(0).toString());
                    btn.setTag(data.getData(row).getData(0).toString());
                    
                    if(flag.getText(0, 0).equals("")){                        
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
