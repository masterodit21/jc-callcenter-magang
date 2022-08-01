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
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
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
public class webcomplist extends NikitaServlet{
    NikitaConnection nikitaConnection;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Component List");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("webcomplist-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent("webcomplist-tblConn");
            }
        }); 
        
        Component btn = new Image();
        btn.setId("webcomplist-btnAdd");
        btn.setText("img/add.png");
        btn.setTag(request.getParameter("mode"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webcomplistadd", request,"add",true);
                response.write();
            }
        });
        
             
               
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webcomplist-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No,compcode,comptitle,compdescribe,compparameter,mobileversion,linkversion,webversion",","));
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(4, true);
        nf.addComponent(tablegrid);                
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                if(response.getContent().findComponentbyId("webcomplist-btnAdd").getTag().equals("edit")){
                    request.setParameter("mode", "edit");
                    request.setParameter("data", tablegrid.getData().getData(tablegrid.getSeletedRow()).toJSON());
                    response.showform("webcomplistadd", request,"edit",true);
                }
                else if(response.getContent().findComponentbyId("webcomplist-btnAdd").getTag().equals("selected")){         
                    Nset nset = Nset.newObject();
                    nset.setData("code", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                    nset.setData("title", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());
                    response.closeform(response.getContent());
                    response.setResult("OK",nset);
                }
                else{
                    Nset result = Nset.newObject();
                    result.setData("id", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()); //compcode to id
                    result.setData("title", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());  
                    result.setData("describe", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString()); 
                    result.setData("parameter", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(4).toString()); 
                    
                    response.closeform(response.getContent());
                    response.setResult("OK", result);
                }
                
                
                
                response.write();
            }
        });
        
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
               // response.showAlert(tablegrid.getCurrentPage()+":"+tablegrid.getShowPerPage());
                   fillGrid(response);
                    response.writeContent();
            }
        });
        
        //ngambil data lemparan dari form moduleadd
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(responsecode.equals("OK")){
                    fillGrid(response);
                   
                }
                response.writeContent();
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webcomplist-tblConn") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webcomplist-txtSearch");
       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,compcode,comptitle,compdescribe,compparameter,mobileversion,linkversion,webversion  "+ 
                                                       "from all_component_list");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,compcode,comptitle,compdescribe,compparameter,mobileversion,linkversion,webversion "+ 
                                                       "from all_component_list where compcode  like ? OR comptitle like ? OR compdescribe like ? OR compparameter like ? ; ",s,s,s,s);
            tablegrid.setData(nikiset);
        }
        
        
    }
}
