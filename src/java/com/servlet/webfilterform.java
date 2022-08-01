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
public class webfilterform extends NikitaServlet{
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Filter Form");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Textbox txt2 = new Textsmart();
        txt2.setId("filter-txtFinder");
        txt2.setLabel("Filter Name");
        txt2.setText(request.getParameter("filtername"));
        txt2.setTag(request.getParameter("filterid"));
        txt2.setStyle(new Style().setStyle("n-lock", "true"));
        nf.addComponent(txt2);
        
        Textsmart txt = new Textsmart();
        txt.setId("ff-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("ff-txtSearch_text");
            }
        }); 
        
        Component btn = new Image();
        btn.setId("ff-btnAdd");
        btn.setText("img/add.png");
        Style style = new Style();
        style.setStyle("margin-top", "3px");
        btn.setStyle(style);
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
                request.setParameter("code", "selectedmodul");
                request.setParameter("type", "");
                response.showform("webform", request,"filter",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("ff-tblFilter");
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
                
                if(reqestcode.equals("filter") && responsecode.equals("OK")){
                    
                    Nikitaset n = nikitaConnection.Query("SELECT formname FROM web_filter_form WHERE filterid=? AND formname=?", response.getContent().findComponentbyId("filter-txtFinder").getTag(), result.getData("name").toString());
                    if (n.getRows()==0) {
                         nikitaConnection.Query("INSERT INTO web_filter_form (filterid, formid,formname,createdby,createddate) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getContent().findComponentbyId("filter-txtFinder").getTag(), result.getData("id").toString(),result.getData("name").toString(),user);
                         
                        n = nikitaConnection.Query("SELECT MAX(ffid) FROM web_filter_form");
            
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", n.getText(0, 0));
                        data.setData("activitytype", "filterform");
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
                    data.setData("activitytype", "filterform");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);

                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_filter_form where ffid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }else  if(reqestcode.equals("delete") && responsecode.equals("button3")){    
                    
                    //history timesheet
                    Nset data = Nset.newObject(); 
                    data.setData("username", user);
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", result.getData("id").toString());
                    data.setData("activitytype", "filterform");
                    data.setData("mode", "delete");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);

                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_filter_form where formname=?",nikitaConnection.Query("SELECT formname FROM web_filter_form  where ffid=?", result.getData("id").toString()).getText(0, 0));
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        //rule2
        Component comp = new Component();
        comp.setId("filter-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialogResult("Delete", "Do you want to delete? All : Delete all contain formname", "delete",Nset.newObject().setData("id",component.getTag() ), "No", "Yes", "All");
                response.write();

            }
        });
        
        if(lblmode.getTag().equals("comp")){     
            nf.setText("Filter Form ["+nf.findComponentbyId("filter-txtFinder").getText()+"]"); 
            nf.findComponentbyId("filter-txtFinder").setVisible(false);
        }
        else{            
            nf.setText("Filter Form");       
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
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("ff-tblFilter");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("ff-txtSearch");
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("filter-txtFinder");
               
        
        if(txt.getText().equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select web_filter_form.ffid,web_form.formname,web_form.formtitle,web_filter.filterid,web_filter.filtername,web_form.formid,web_filter_form.counter from (select *,COUNT(*)as counter from web_filter_form GROUP BY formname)  web_filter_form LEFT JOIN web_filter ON (web_filter_form.filterid=web_filter.filterid)  LEFT JOIN web_form ON (web_filter_form.formname=web_form.formname)  WHERE web_filter_form.filterid=?  ",txtFinder.getTag());
            tablegrid.setData(nikiset);
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            String p = txtFinder.getTag();
            
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select web_filter_form.ffid,web_form.formname,web_form.formtitle,web_filter.filterid,web_filter.filtername,web_form.formid,web_filter_form.counter from  (select *,COUNT(*)as counter from web_filter_form GROUP BY formname)  web_filter_form LEFT JOIN web_filter ON (web_filter_form.filterid=web_filter.filterid)  LEFT JOIN web_form ON (web_filter_form.formname=web_form.formname)  WHERE web_filter_form.filterid=?  ",txtFinder.getTag());
            tablegrid.setData(nikiset);
            
        }
         tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        //rule1
        tablegrid.setAdapterListener(new IAdapterListener() {

            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 1){
                    Component component = new Component();
                    component.setVisible(false);component.setEnable(false);
                    if (Utility.getInt(data.getData(row).getData(6).toString())>=2) {
                        component.setText(data.getData(row).getData(1).toString() +" <b>("+ data.getData(row).getData(6).toString()+")<b>");

                    }else{
                        component.setText(data.getData(row).getData(1).toString() );
                    }
                    return component;
                }else   if(col == 5){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("filter-btnDelete["+row+"]");
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
