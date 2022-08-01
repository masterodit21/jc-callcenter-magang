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

/**
 *
 * @author user
 */
public class webaccessform  extends NikitaServlet{    
    NikitaConnection nikitaConnection;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("User Access Form");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("modul-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("modul-tblModul"));
            }
        }); 
        
        nf.addComponent(txt);
        
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("modul-tblModul");
        tablegrid.setDataHeader(Nset.readsplitString("No|User Name|Name")); //set header table        
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                request.setParameter("username", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                request.setParameter("mode","access");
                response.showform("webuseraccessform", request, "module", true);
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
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select userid,username,name from sys_user");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select userid,username,name "+ 
                                                       "from sys_user where username like ? OR name like ? ; ",s,s);
            tablegrid.setData(nikiset);
        }
        tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
                
    }
    
    
}
