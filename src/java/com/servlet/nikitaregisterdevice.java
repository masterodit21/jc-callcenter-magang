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
public class nikitaregisterdevice extends NikitaServlet{

    /*parameter
    search
    mode
    data
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Register Device");        
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("regis-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("regis-tblRegister"));
            }
        }); 
                
        Component btn = new Image();
        btn.setId("regis-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode","add");
                response.showform("nikitaregisterdeviceadd", request,"add", true);
                response.write();
            }
        });        
               
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("regis-tblRegister");
        tablegrid.setDataHeader(Nset.readsplitString("No|Account|Target|Application|Imei|"));
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
                    NikitaConnection nikitaConnection = response.getConnection("logic");
                    
                    nikitaConnection.Query("DELETE FROM nikita_register_device WHERE registerid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("regis-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("nikitaregisterdeviceadd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("regis-btnDelete");
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
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("regis-tblRegister") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("regis-txtSearch");
                
        NikitaConnection nikitaConnection = response.getConnection("logic");
       
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT registerid,registeraccount,registertarget,registerapp,registerimei,1 "+ 
                                                       "FROM nikita_register_device");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT registerid,registeraccount,registertarget,registerapp,registerimei,1 "+ 
                                                       "FROM nikita_register_device WHERE registeraccount LIKE ? OR registertarget LIKE ? OR registerapp LIKE ? ; ",s,s,s);
            tablegrid.setData(nikiset);
        }
        
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("regis-btnEdit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).toJSON());
                                        
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("regis-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setTag(data.getData(row).getData(0).toString());
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
