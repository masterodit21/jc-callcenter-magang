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
import com.nikita.generator.NikitaViewV3;
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
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webasset extends NikitaServlet {
    NikitaConnection nikitaConnection;
    String user ;
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Asset");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("webasset-txtsearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("paramid"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("webasset-txtSearch_text");
            }
        });
                
        Component btn = new Image();
        btn.setId("webasset-btnadd");
        btn.setText("img/add.png");
        btn.setTag(request.getParameter("code"));
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webassetadd", request,"add",true);
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webasset-tblasset");
        tablegrid.setDataHeader(Nset.readsplitString("No|Id|Name|File Name|Describe|Size|Type|")); //set header table
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColStyle(5, new Style().setStyle("text-align", "right"));
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
                    Nikitaset n = nikitaConnection.Query("SELECT assetname FROM web_asset where assetid=?",result.getData("id").toString());
            
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", n.getText(0, 0));
                    data.setData("activitytype", "asset");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    nikitaConnection.Query("delete from web_asset where assetid=?",result.getData("id").toString());
                    
                    try {
                        String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("asset").toString();
                        Utility.deleteFileAll(path+"\\"+result.getData("id").toString()+".zip");
                    } catch (Exception e) {  }
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("webasset-btnedit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("id", component.getTag());
                request.setParameter("mode","edit");
                response.showform("webassetadd", request,"edit", true);
                response.write();

            }
        });       
        
        comp = new Component();
        comp.setId("webasset-btndelete");
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webasset-tblasset");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("webasset-txtsearch");      
        if(response.getContent().findComponentbyId("webasset-btnadd").getTag().equals("selected")){ 
            response.getContent().findComponentbyId("webasset-btnadd").setVisible(false);
            tablegrid.setColHide(6, true);             
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webasset-tblasset") ;
                    Nset result = Nset.newObject();
                    result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString()); 
                    result.setData("paramid",response.getContent().findComponentbyId("webasset-txtsearch").getTag());
                    response.closeform(response.getContent());

                    response.setResult("webvariablelist", result); 
                }
            });       
            
        }
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,assetid,assetname,assetfname,assetdescribe,assetsize,assettype,1 "+ 
                                                       "from web_asset");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,assetid,assetname,assetfname,assetdescribe,assetsize,assettype,1 "+ 
                                                       "from web_asset where assetname like ? OR assetfname like ? ; ",s,s);
            tablegrid.setData(nikiset);
        }
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setColStyle(6, new Style().setStyle("width", "50px").setStyle("width", "50px"));
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 7){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image(){
                        private String href="";
                        public String getView(NikitaViewV3 v3) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("<a href=\""+href+"\" target=\"_blank\">");
                            sb.append(super.getView(v3));
                            sb.append("</a>");
                            return sb.toString();                             
                        }
                        public Image get(String href){
                            this.href=href;
                            return this;
                        }
                    }.get("res/asset/?assetid="+data.getData(row).getData(1).toString()+"&assetname="+data.getData(row).getData(2).toString());;
         
                    
                    btn.setId("webasset-btnview["+row+"]");
                    btn.setText("img/view.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                   // btn.setTag(data.getData(row).getData(1).toString()+"&assetname="+data.getData(row).getData(2).toString());
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                  
                    
                    btn = new Image();
                    btn.setId("webasset-btnedit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).getData(1).toString());
                    
                    style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    btn = new Image();
                    btn.setId("webasset-btndelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setTag(data.getData(row).getData(1).toString());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {

                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    return horisontalLayout;                    
                }else if (col==5){
                    Component comp = new Component();
                    comp.setVisible(false);
                    comp.setText(Utility.formatsizeByte(Utility.getLong(data.getData(row).getData(col).toString())));
                    return comp;
                }
                return null;
            }
        });
    }
    
}
