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
 

/**
 *
 * @author user
 */
public class webformlist extends NikitaServlet{
    
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Form List");        
        
        Textsmart txt = new Textsmart();
        txt.setId("webformlist-txtSearch");
        txt.setLabel("Searching");
        txt.setTag(request.getParameter("paramid"));
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));        
         
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);              
                response.refreshComponent("webformlist-tblForm");
                response.write();
            }
        });       
        
        
        
        nf.addComponent(txt);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webformlist-tblForm");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Title|Type|Style"));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(5, true);
        nf.addComponent(tablegrid);  
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                Nset result = Nset.newObject();
                result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()); 
                result.setData("paramid",response.getContent().findComponentbyId("webformlist-txtSearch").getTag());
                result.setData("formid", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                result.setData("parameter", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(4).toString()); 
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
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
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });   
    }
    
    
    public void fillGrid(NikitaResponse response){      
        
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webformlist-tblForm") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webformlist-txtSearch");
       
        String user = response.getVirtualString("@+SESSION-LOGON-USER");

        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select formid,formname,formtitle,formtype,formstyle "+ 
                                                       "from web_form "+(NikitaService.isModeCloud()?"WHERE createdby = '"+user+"' ":"") );
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select formid,formname,formtitle,formtype,formstyle "+ 
                                                       "from web_form where "+(NikitaService.isModeCloud()?"createdby = '"+user+"' AND ":"")+" (formname like ? OR formtitle like ? OR formtype like ? ) ; ",s,s,s);
            tablegrid.setData(nikiset);
        }
        
    }  
}
