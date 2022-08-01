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
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class webfinderlist extends NikitaServlet{
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Finder List");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        
        final Label iddata = new Label();
        iddata.setId("finder-lbliddata");
        iddata.setTag(request.getParameter("data"));
        iddata.setVisible(false);
        nf.addComponent(iddata);
        
        final Label idmode = new Label();
        idmode.setId("finder-lblidmode");
        idmode.setTag(request.getParameter("mode"));
        idmode.setVisible(false);
        nf.addComponent(idmode);
       
        
        
        final Label idcomp = new Label();
        idcomp.setId("finder-lblidcomp");
        idcomp.setTag(request.getParameter("idcomp"));
        idcomp.setVisible(false);
        nf.addComponent(idcomp);
        
        final Label idform = new Label();
        idform.setId("finder-lblidform");
        idform.setTag(request.getParameter("idform"));
        idform.setVisible(false);
        nf.addComponent(idform);
        
        Textbox txt = new Textsmart();
        txt.setId("finder-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("paramid"));
        horisontalLayout.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("finder-btnSearch");
        btn.setText("Searching");
        btn.setVisible(false);
        horisontalLayout.addComponent(btn);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
                response.writeContent();
            }
        });      
        
             
               
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("finder-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Describe"));
        
        
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);        
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                Nset result = Nset.newObject();
                result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()); 
                ///
                result.setData("paramid",response.getContent().findComponentbyId("finder-txtSearch").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webfinderlist", result);                
                response.write();
            }
        });
        
        //rule2 
        response.setContent(nf);       
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("finder-tblConn") ;
        Component iddata=  response.getContent().findComponentbyId("finder-lbliddata");
        //NikitaConnection nikitaConnection = NikitaConnection.getConnection("logic");
        
        
        if (iddata.getTag().startsWith("{")) {
            tablegrid.setData(Nset.readJSON(iddata.getTag()));
        }else if (iddata.getTag().startsWith("[")) {
            tablegrid.setData(Nset.readJSON(iddata.getTag()));
        }else{
            tablegrid.setData(Nset.readsplitString(iddata.getTag()));
        }
    }
     
}
