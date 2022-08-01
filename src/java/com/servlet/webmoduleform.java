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
import com.nikita.generator.ui.Label;
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
public class webmoduleform extends NikitaServlet{
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Module Form");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Textbox txt2 = new Textsmart();
        txt2.setId("modul-txtFinder");
        txt2.setLabel("Module Name");
        txt2.setText(request.getParameter("modulename"));
        txt2.setTag(request.getParameter("moduleid"));
        txt2.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt2);
        
        txt2 = new Textsmart();
        txt2.setId("modul-txtFinder2");
        txt2.setLabel("Form Name");
        txt2.setVisible(false);
        txt2.setText(request.getParameter("formname"));
        txt2.setTag(request.getParameter("formid"));
        txt2.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt2);
        
        Textsmart txt = new Textsmart();
        txt.setId("mf-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("mf-txtSearch_text");
            }
        }); 
        
        Component btn = new Image();
        btn.setId("mf-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                request.setParameter("code", "selectedmodul");
                request.setParameter("type", "mobileform");
                response.showform("webform", request,"modul",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("mf-tblModul");
        tablegrid.setDataHeader(Nset.readsplitString("|Name|Title|||")); //set header table
        style = new Style();
        style.setStyle("width", "10px");
        tablegrid.setColStyle(5, style);
        tablegrid.setColHide(0, true);
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(4, true);
        tablegrid.showRowIndex(true);
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
                
                if(reqestcode.equals("modul") && responsecode.equals("OK")){
                    
                    response.getContent().findComponentbyId("modul-txtFinder2").setTag(result.getData("id").toString());
                    Nikitaset n = nikitaConnection.Query("SELECT formname FROM web_module_form WHERE moduleid=? AND formname=?", response.getContent().findComponentbyId("modul-txtFinder").getTag(), result.getData("name").toString());
                    if (n.getRows()==0) {
                         nikitaConnection.Query("INSERT INTO web_module_form (moduleid, formid,formname,createdby,createddate) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getContent().findComponentbyId("modul-txtFinder").getTag(), result.getData("id").toString(),result.getData("name").toString(),user);
                         
                        n = nikitaConnection.Query("SELECT MAX(mfid) FROM web_module_form");
            
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", n.getText(0, 0));
                        data.setData("activitytype", "moduleform");
                        data.setData("mode", "add");
                        data.setData("additional", "");
                        Utility.SaveActivity(data, response);
                        
                    } 
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("delete") && responsecode.equals("button2")){    
                    
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", result.getData("id").toString());
                    data.setData("activitytype", "moduleform");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);

                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_module_form where mfid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("modul-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete?", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes");
                response.write();

            }
        });
        
        if(lblmode.getTag().equals("comp")){     
            nf.setText("Module Form ["+nf.findComponentbyId("modul-txtFinder").getText()+"]"); 
            nf.findComponentbyId("modul-txtFinder").setVisible(false);
        }
        else{            
            nf.setText("Module Form");       
        }
        
        response.setContent(nf);
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("mf-tblModul");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("mf-txtSearch");
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("modul-txtFinder");
               
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select web_module_form.mfid,web_form.formname,web_form.formtitle,web_module.moduleid,web_module.modulename,web_form.formid from web_module_form LEFT JOIN web_module ON (web_module_form.moduleid=web_module.moduleid)  LEFT JOIN web_form ON (web_module_form.formname=web_form.formname)  WHERE web_module_form.moduleid=?  ",txtFinder.getTag());
            tablegrid.setData(nikiset);
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            String p = txtFinder.getTag();
            
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select web_module_form.mfid,web_form.formname,web_form.formtitle,web_module.moduleid,web_module.modulename,web_form.formid  from web_module_form LEFT JOIN web_module ON (web_module_form.moduleid=web_module.moduleid)  LEFT JOIN web_form ON (web_module_form.formname=web_form.formname)  WHERE web_module_form.moduleid=?  ",txtFinder.getTag());
            tablegrid.setData(nikiset);
            
        }
         tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("modul-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    btn.setTag(data.getData(row).getData(0).toString());
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
                    horisontalLayout.setStyle(style);
                    
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
