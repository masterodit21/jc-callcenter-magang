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
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class test extends NikitaServlet{

    @Override
    public String getVersion() {
        return "1.1.1 beat"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nikitaForm = new NikitaForm(this);
        nikitaForm.setText("Test A");
        Textsmart textbox = new Textsmart();
        textbox.setId("1");
        textbox.setLabel("Nama");
        textbox.setHint("maskukn nama");
        
        textbox.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showAlert("AAAAAAAAAA");
                response.write();
            }
        });
        
        
        
        
        Button tbl = new Button();
        tbl.setText("Done");
        tbl.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("formname", response.getContent().findComponentbyId("1").getText());
                    NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);  
                    Tablegrid tb = (Tablegrid)response.getContent().findComponentbyId("6");
                    tb.setData(nc.QueryPage(tb.getCurrentPage(),tb.getShowPerPage(),"SELECT * FROM web_component", null));
                    
                    response.writeContent();
                
            }
        });
        
        
        HorizontalLayout layout = new HorizontalLayout();
        
        layout.addComponent(textbox);
        layout.addComponent(tbl);
    
        
        nikitaForm.setOnActionResultListener(new NikitaForm.OnActionResultListener() {

            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
            }
        });
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("6");
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {

            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                    NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC); 
                    Tablegrid tb = (Tablegrid)response.getContent().findComponentbyId("6");
                    tb.setData(nc.QueryPage(tb.getCurrentPage(),tb.getShowPerPage(),"SELECT * FROM web_component", null));
                    
                    response.writeContent();
            }
        });
        
        nikitaForm.addComponent(layout);
        nikitaForm.addComponent(tablegrid);
        
        response.setContent(nikitaForm);
                
        response.getContent().setId("test");
    
    }

    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if (response.getComponent("$nama").getId().equals(component)) {
            
            response.showAlert("asas");
            return ;             
        }       
        super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
