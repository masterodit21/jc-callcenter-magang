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
 * @author rkrzmail
 */
public class webschhistory extends NikitaServlet{
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
        nf.setText("Scheduller History");
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        
        
        idtype = new Label();
        idtype.setId("websch-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        Textsmart txt = new Textsmart();
        txt.setId("websch-txtSearch");
        txt.setLabel("Searching");
        nf.addComponent(txt);
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
                response.writeContent();
            }
        });
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("websch-tblSch");
        tablegrid.setDataHeader(Nset.readsplitString("No|Thread Name|Task Name|Start Date|Finish Date|Activity Status|")); //set header table
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
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,threadname,taskname,startdate,finishdate,activitystatus,1 "+ 
                                                       "from web_scheduller_history ORDER BY threadid");
            
            tablegrid.setData(nikiset);
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,threadname,taskname,startdate,finishdate,activitystatus,1 "+ 
                                                       "from web_scheduller_history where (threadname like ? OR taskname like ? ) ORDER BY threadid ",s,s);
            tablegrid.setData(nikiset);
        }
        
        
    }
    
    
}
