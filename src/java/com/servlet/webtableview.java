/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.action.ConnectionAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author user
 */
public class webtableview extends NikitaServlet{
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Table View");        
        
        
        Label lbl = new Label();
        lbl.setId("webtableview-lblsql");
        lbl.setVisible(false);
        if(!request.getParameter("sql").equals(""))
            lbl.setTag(request.getParameter("sql")); 
        else
            lbl.setTag(request.getParameter("callz")); 
        nf.addComponent(lbl);      
        
        lbl = new Label();
        lbl.setId("webtableview-lblconn");
        lbl.setVisible(false);
        lbl.setTag(request.getParameter("conn")); 
        nf.addComponent(lbl);
       
        
        lbl = new Label();
        lbl.setId("webtableview-lblargs");
        lbl.setVisible(false);
        lbl.setTag(request.getParameter("args")); 
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("argsname");
        lbl.setVisible(false);
        lbl.setTag(request.getParameter("argsname")); 
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("webtableview-lblerror");
        lbl.setVisible(false);
        nf.addComponent(lbl);
        
        Button btn = new Button();
        btn.setId("webtableview-btnexec");
        btn.setVisible(false);
        btn.setText("Execute");
        nf.addComponent(btn);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webtableview-tableview");
        //tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);  
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
            }
        });
        
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
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
        
//        nf.setOnReadyListener(new NikitaForm.OnReadyListener() {
//
//            @Override
//            public void OnReady(NikitaRequest request, NikitaResponse response, Component component) {
//                fillGrid(response);
//                response.writeContent();
//            }
//        });
    }
    
    
    public void fillGrid(NikitaResponse response){      
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webtableview-tableview") ;
        Label lblsql= (Label)response.getContent().findComponentbyId("webtableview-lblsql");
        Label lblconn= (Label)response.getContent().findComponentbyId("webtableview-lblconn");
        Label lblerror= (Label)response.getContent().findComponentbyId("webtableview-lblerror");
        String args=response.getContent().findComponentbyId("webtableview-lblargs").getTag();
        String argsname=response.getContent().findComponentbyId("argsname").getTag();
     
        Nset n   =  Nset.readJSON(args);
        String[] arg = new String[n.getArraySize()<=0?0:n.getArraySize()];
        for (int i = 0; i < arg.length; i++) {
            arg[i] = n.getData(i).toString();
        }
        
        Nikitaset nikiset ;
        if (NikitaService.isModeCloud()) {
            String cname = lblconn.getTag();
            String user = response.getVirtualString("@+SESSION-LOGON-USER");
            String prefix = NikitaService.getPrefixUserCloud(response.getConnection(NikitaConnection.LOGIC), user);
            if (cname.startsWith(prefix) || cname.equalsIgnoreCase("sample") || cname.equalsIgnoreCase("default") ) {
                NikitaConnection nikitaConnection = response.getConnection(lblconn.getTag());
                nikiset = nikitaConnection.QueryPage(lblsql.getTag(), ConnectionAction.parseArgs(Nset.readJSON(args), Nset.readJSON(argsname)) ,tablegrid.getCurrentPage(),tablegrid.getShowPerPage());
            }else{
                nikiset = new Nikitaset("You don't have Access this Connection");
            }
        }else{
            NikitaConnection nikitaConnection = response.getConnection(lblconn.getTag());
            //Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),lblsql.getTag(), arg);
            nikiset = nikitaConnection.QueryPage(lblsql.getTag(), ConnectionAction.parseArgs(Nset.readJSON(args), Nset.readJSON(argsname)) ,tablegrid.getCurrentPage(),tablegrid.getShowPerPage());
        }
       
        if (nikiset.getError().length()>=1) {   
            lblerror.setVisible(true);
            lblerror.setText(nikiset.getError());
            tablegrid.setVisible(false);
            
        }else{
                tablegrid.setData(nikiset);
        }
        

        
    } 
}
