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
import com.nikita.generator.NikitaViewV3;
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
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webresource extends NikitaServlet{
    
    /*parameter
    mode
    id
    */
    NikitaConnection nikitaConnection;  
    Nikitaset formunlock;    
    Nikitaset formaccess1;   
    String user ; 
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Resource");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textsmart txt = new Textsmart();
        txt.setId("webres-txtsearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("paramid"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("webres-txtSearch_text");
            }
        });
                
        Component btn = new Image();
        btn.setId("webres-btnadd");
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
                response.showform("webresourceadd", request,"add",true);
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webres-tblresource");
        tablegrid.setDataHeader(Nset.readsplitString("No|Id|Name|File Name|Describe|Size|Type|Createdby|")); //set header table
        tablegrid.setRowCounter(true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(7, true);
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
                    nikitaConnection.Query("DELETE FROM sys_lock WHERE resid=?",result.getData("id").toString());   
                    
                    Nikitaset nikitaset = nikitaConnection.Query("SELECT resname FROM web_resource where resid=?",result.getData("id").toString());
            
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", nikitaset.getText(0, 0));
                    data.setData("activitytype", "resource");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);
                    
                    nikitaConnection.Query("delete from web_resource where resid=?",result.getData("id").toString());
                    
                    try {
                        String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                        Utility.deleteFileAll(path+"\\"+result.getData("id").toString()+".res");
                    } catch (Exception e) {  }
                        
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2 
        Component comp = new Component();
        comp.setId("webres-btnedit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                formunlock = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", n.getData(1).toString());
                if(!formunlock.getText(0, 0).equals("")){                            
                    request.setParameter("unlock","open");
                }else{                            
                    request.setParameter("unlock","");
                }
                request.setParameter("id", n.getData(1).toString());
                request.setParameter("mode","edit");
                request.setParameter("created",n.getData(7).toString());
                response.showform("webresourceadd", request,"edit", true);
                response.write();

            }
        });       
        
        comp = new Component();
        comp.setId("webres-btndelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset n = Nset.readJSON(component.getTag()); 
                formunlock = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", n.getData(1).toString());    
                formaccess1 = nikitaConnection.Query("SELECT createdby FROM web_resource WHERE resid = ? ", n.getData(1).toString());  
                if(n.getData(7).toString().equals(user) || !formunlock.getText(0, 0).equals("")){
                    response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",n.getData(1).toString() ), "No", "Yes");
                }else{
                    response.showDialogResult("Warning from "+formaccess1.getText(0, 0), "You don't have access to delete", "warning",null, "OK", "");
                } 
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webres-tblresource");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("webres-txtsearch");
        if(response.getContent().findComponentbyId("webres-btnadd").getTag().equals("selected")){ 
            response.getContent().findComponentbyId("webres-btnadd").setVisible(false);
            tablegrid.setColHide(6, true);             
            tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webres-tblresource") ;
                    Nset result = Nset.newObject();
                    result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString()); 
                    result.setData("paramid",response.getContent().findComponentbyId("webres-txtsearch").getTag());
                    response.closeform(response.getContent());

                    response.setResult("webvariablelist", result); 
                }
            });       
            
        }
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,resid,resname,resfname,resdescribe,ressize,restype,createdby,1 "+ 
                                                       "from web_resource "+(NikitaService.isModeCloud()?"WHERE createdby = '"+user+"' ":"") );
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,resid,resname,resfname,resdescribe,ressize,restype,createdby,1 "+ 
                                                       "from web_resource where "+(NikitaService.isModeCloud()?"createdby = '"+user+"' AND ":"")+" resname like ? OR resfname like ? ; ",s,s);
            tablegrid.setData(nikiset);
        }
         tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setColStyle(6, new Style().setStyle("width", "50px").setStyle("width", "50px") );
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                formunlock = nikitaConnection.Query("SELECT resid FROM sys_lock WHERE resid = ? ", data.getData(row).getData(1).toString());
                if(col == 8){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component
                    btn = new Image(){
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
                    }.get("res/resource/?attachment=true&resname="+data.getData(row).getData(2).toString());;
          
                    btn.setId("webasset-btnattach["+row+"]");
                    btn.setText("img/download.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    //btn.setTag(data.getData(row).getData(1).toString()+"/"+data.getData(row).getData(2).toString());
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        @Override
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                            
                        }
                    });
                    
                    
                    btn = new Image(){
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
                    }.get("res/resource/?resname="+data.getData(row).getData(2).toString());;
          
                    btn.setId("webasset-btnview["+row+"]");
                    btn.setText("img/view.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    //btn.setTag(data.getData(row).getData(1).toString()+"/"+data.getData(row).getData(2).toString());
                    
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
                    btn.setId("webres-btnedit["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(data.getData(row).toJSON());
                    if(data.getData(row).getData(7).toString().equals(user) || !formunlock.getText(0, 0).equals("")){                        
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    } else{
                        btn.setStyle(new Style().setStyle("opacity","0.2").setStyle("margin-right","3px")); 
                    }
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
                    btn.setId("webres-btndelete["+row+"]");
                    btn.setText("img/minus.png");
                    if(data.getData(row).getData(7).toString().equals(user) || !formunlock.getText(0, 0).equals("")){
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0.2")); 
                    }    
                    btn.setTag(data.getData(row).toJSON());
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
