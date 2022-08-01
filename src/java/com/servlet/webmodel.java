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
public class webmodel extends NikitaServlet{
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Model");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("model-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("model-txtSearch_text");
            }
        });
        
        Component btn = new Image();
        btn.setId("model-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        btn.setTag(request.getParameter("code"));
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webmodeladd", request,"add",true);
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("model-tblModel");
        tablegrid.setDataHeader(Nset.readsplitString("No|Model Id|Name|Order Name|")); //set header table
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
                if(reqestcode.equals("add") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("edit") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                    
                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_model where modelid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("model-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webmodeladd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("model-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
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
              
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("model-tblModel");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("model-txtSearch");
        
        if(response.getContent().findComponentbyId("model-btnAdd").getTag().equals("selected")){
            response.getContent().findComponentbyId("model-btnAdd").setVisible(false);
            tablegrid.setColHide(4, true);
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("model-tblModel") ;
                    Nset nset = Nset.newObject();
                    nset.setData("idmodel", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());
                    nset.setData("namemodel", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());
                    response.closeform(response.getContent());
                    response.setResult("OK",nset);
                    
                    response.write();
                }
            });
        }
        
        NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,modelid,modelname,ordername,1 "+ 
                                                       "from web_model");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,modelid,modelname,ordername,1 "+ 
                                                       "from web_model where modelname like ? OR modelid like ? ; ",s,s);
            tablegrid.setData(nikiset);
        }
        
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 4){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();

                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
            
                    btn.setId("model-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    btn = new Image();
                    btn.setId("model-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setTag(data.getData(row).getData(1).toString());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {

                        @Override
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
