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
public class webactionlist extends NikitaServlet{

    NikitaConnection nikitaConnection ;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Action List");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textbox txt = new Textsmart();
        txt.setId("webactionlist-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("webactionlist-btnSearch");
        btn.setText("Searching");
        btn.setVisible(false);
        horisontalLayout.addComponent(btn);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response); 
                response.refreshComponent("webactionlist-tblConn");
                response.write();
            }
        });      
        
        Image img = new Image();
        img.setId("webactionlist-btnAdd");
        img.setText("img/add.png");
        img.setTag(request.getParameter("mode"));
        img.setStyle(new Style().setStyle("margin-top", "6px"));
        horisontalLayout.addComponent(img);
        img.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webactionlistadd", request,"add",true);
                response.write();
            }
        });
        if(img.getTag().equals("edit")){
            img.setVisible(true);
        }else{
            img.setVisible(false);
        }
             
               
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webactionlist-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No,actioncode,actiontitle,actiondescribe,actionparameter,mobileversion,linkversion,webversion",","));
        tablegrid.setRowCounter(true);
        
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(4, true);
        
        nf.addComponent(tablegrid);                
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                if(response.getContent().findComponentbyId("webactionlist-btnAdd").getTag().equals("edit")){
                    request.setParameter("mode", "edit");
                    request.setParameter("data", tablegrid.getData().getData(tablegrid.getSeletedRow()).toJSON());
                    response.showform("webactionlistadd", request,"edit",true);
                }
                else{
                    Nset result = Nset.newObject();
                    result.setData("id", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()); //actcode to id
                    result.setData("title", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());  
                    result.setData("describe", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(3).toString()); 
                    result.setData("parameter", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(4).toString()); 
                    
                    response.closeform(response.getContent());
                    response.setResult("OK", result);
                }
                
                response.write();
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webactionlist-tblConn") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webactionlist-txtSearch");
                       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.Query("select 1,actioncode,actiontitle,actiondescribe,actionparameter,mobileversion,linkversion,webversion  "+ 
                                                       "from all_action_list ORDER BY actiontitle;");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.Query("select 1,actioncode,actiontitle,actiondescribe,actionparameter,mobileversion,linkversion,webversion "+ 
                                                       "from all_action_list where actiontitle like ? OR actiondescribe like ? OR actionparameter like ? ORDER BY actiontitle; ",s,s,s);
            tablegrid.setData(nikiset);
        }
        
        
    }
}
