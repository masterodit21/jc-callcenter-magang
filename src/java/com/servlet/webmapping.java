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
 * @author lenovo
 */
public class webmapping extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Mapping");
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("map-txtfindform");
        txt.setLabel("Form Name");
        txt.setText(request.getParameter("formname"));
        txt.setTag(request.getParameter("formid"));
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");                
                request.setParameter("search", response.getContent().findComponentbyId("map-txtfindform").getText() );
                response.showform("webform", request,"form",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        horisontalLayout = new HorizontalLayout();
        txt = new Textsmart();
        txt.setId("map-txtfindmodel");
        txt.setLabel("Model Name");
        txt.setText(request.getParameter("modelname"));
        txt.setTag(request.getParameter("modelid"));
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("map-txtfindmodel").getText() );
                response.showform("webmodel", request,"model",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        
        horisontalLayout = new HorizontalLayout();
        txt = new Textsmart();
        txt.setId("map-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
                response.writeContent();
            }
        });  
        
        Component btn = new Image();
        btn.setId("map-btnAdd");
        btn.setText("img/add.png");
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode","add");
                
                request.setParameter("formName", response.getContent().findComponentbyId("map-txtfindform").getText());
                request.setParameter("formId", response.getContent().findComponentbyId("map-txtfindform").getTag());
                request.setParameter("modelName", response.getContent().findComponentbyId("map-txtfindmodel").getText());
                request.setParameter("modelId", response.getContent().findComponentbyId("map-txtfindmodel").getTag());
                    
                response.showform("webmapadd", request,"add", true);
                response.write();
            }
        });    
        
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("map-tblMap");
        tablegrid.setDataHeader(Nset.readsplitString("No|mfid|Model Id|Form Id|index|")); //set header table
        tablegrid.setRowCounter(true);        
        tablegrid.setColHide(4, true);
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                   fillGrid(response);
                    response.writeContent();
            }
        });

        nf.addComponent(tablegrid); 
        
        
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
                
                if(reqestcode.equals("form") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("map-txtfindform").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("map-txtfindform").setTag(result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("model") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("map-txtfindmodel").setText(result.getData("namemodel").toString());
                    response.getContent().findComponentbyId("map-txtfindmodel").setTag(result.getData("idmodel").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                    
                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_model_form where mfid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });       
        
        
        //rule2
        Component comp = new Component();
        comp.setId("map-btnUp");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);  
                
                Nikitaset nikiset = nikitaConnection.Query("select mfid,mfindex from web_model_form WHERE mfindex < ? AND modelid = ? AND formid = ? ORDER BY mfindex DESC,mfid DESC", nset.getData(4).toString(), nset.getData(2).toString(), nset.getData(3).toString());
                
                nikitaConnection.Query("UPDATE web_model_form set mfindex=? WHERE mfid =? ", nikiset.getText(0, 1),nset.getData(1).toString());                
                nikitaConnection.Query("UPDATE web_model_form set mfindex=? WHERE mfid =? ", nset.getData(4).toString(),nikiset.getText(0, 0)); 
                
                fillGrid(response);
                response.writeContent();

            }
        });
        
        comp = new Component();
        comp.setId("map-btnDown");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);  
                
                Nikitaset nikiset = nikitaConnection.Query("select mfid,mfindex from web_model_form WHERE mfindex > ? AND modelid = ? AND formid = ? ORDER BY mfindex ASC,mfid ASC", nset.getData(4).toString(), nset.getData(2).toString(), nset.getData(3).toString());
                
                nikitaConnection.Query("UPDATE web_model_form set mfindex=? WHERE mfid =? ", nikiset.getText(0, 1),nset.getData(1).toString());                
                nikitaConnection.Query("UPDATE web_model_form set mfindex=? WHERE mfid =? ", nset.getData(4).toString(),nikiset.getText(0, 0)); 
                
                fillGrid(response);
                response.writeContent();

            }
        });
        
        comp = new Component();
        comp.setId("map-btnInsert");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
                nikitaConnection.Query("UPDATE web_model_form set mfindex=mfindex+2 WHERE mfindex > ? AND modelid=? AND formid=?", nset.getData(4).toString(),nset.getData(2).toString(),nset.getData(3).toString());                
                
                nikitaConnection.Query("INSERT INTO web_model_form(mfindex,modelid,formid) VALUES(?,?,?) ", (nset.getData(4).toInteger()+1)+"",nset.getData(2).toString(),nset.getData(3).toString());
                
                fillGrid(response);
                response.writeContent();

            }
        });

        comp = new Component();
        comp.setId("map-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webmapadd", request,"edit", true);
                response.write();

            }
        });        
        
        comp = new Component();
        comp.setId("map-btnDelete");
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
    
    
    //ini dibuat berdasarkan query dari table mobile_model_form, tapi dicari berdasarkan apa ya?
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("map-tblMap");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("map-txtSearch");
        Image cmpBtnAdd =(Image) response.getContent().findComponentbyId("map-btnAdd") ;
        Textbox txtFinderf= (Textbox)response.getContent().findComponentbyId("map-txtfindform");
        Textbox txtFinderm= (Textbox)response.getContent().findComponentbyId("map-txtfindmodel");
        NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);

        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,mfid,modelid,formid,mfindex,1 "+ 
                                                       "from web_model_form ORDER BY mfindex ASC");
            tablegrid.setData(new Nset(nikiset.getDataAllVector()));
            cmpBtnAdd.setVisible(nikiset.getRows()>=1 || (txtFinderf.getText().equals("") && txtFinderm.getText().equals("")) ?false:true);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,mfid,modelid,formid,mfindex,1 "+ 
                                                       "from web_model_form where modelid like ? OR formid like ? ORDER BY mfid ASC; ",s,s);
            tablegrid.setData(new Nset(nikiset.getDataAllVector()));
            cmpBtnAdd.setVisible(nikiset.getRows()>=1 || (txtFinderf.getText().equals("") && txtFinderm.getText().equals("")) ?false:true);
        }
        
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("map-btnUp["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setText("img/up.png");
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
                    btn.setId("map-btnDown["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setText("img/down.png");
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                    btn = new Image();
                    btn.setId("map-btnInsert["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setText("img/add.png");
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });

                    
                    btn = new Image();
                    btn.setId("map-btnEdit["+row+"]");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setText("img/edit.png");
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    btn = new Image();
                    btn.setId("map-btnDelete["+row+"]");
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
