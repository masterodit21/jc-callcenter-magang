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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.NikitaSpreadsheet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * @author rkrzmail
 */
public class webreportexporter extends NikitaServlet{
    NikitaConnection nikitaConnection;
    String user ;boolean findsetting = false;
     
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Report Exporter");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        final Tablegrid tablegrid = new Tablegrid();
        final Combobox combobox = new Combobox();
        combobox.setId("origin");
        combobox.setLabel("Asal Data");
        combobox.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if (!combobox.getText().equals("query")) {
                    response.getContent().findComponentbyId("query").setEnable(false);
                }else{
                    response.getContent().findComponentbyId("query").setEnable(true);
                }
                response.refreshComponent("query");
                response.getContent().findComponentbyId("export").setEnable(false);
                response.refreshComponent("export");
            }
        });
      
        nf.addComponent(combobox);
        
        final Textarea txt = new Textarea();
        txt.setId("query");
        txt.setLabel("QUERY SQL");       
        txt.setStyle(new Style().setStyle("width", "540px"));
                
        horisontalLayout.addComponent(txt);        
               
        VerticalLayout vl = new VerticalLayout();
        
        Component btn = new Button();
        btn.setId("run");
        btn.setText("Run/Execute");
      
        Style style = new Style();        
        btn.setStyle(style);
        vl.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nbuff = Nset.newObject(); 
                nbuff.setData("origin",combobox.getText());
                nbuff.setData("query",txt.getText());

                if (findsetting) {
                    nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(nikitaConnection.getDatabaseCore())+"  WHERE settingusername=? AND settingkey=?", nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-reportexporter");
                }else{
                    nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(nikitaConnection.getDatabaseCore())+")", response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-reportexporter",nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                } 
                
                response.getContent().findComponentbyId("export").setEnable(true);
                response.refreshComponent("export");
                
                fillGrid(combobox, tablegrid, txt, response);
            }
        });
        btn = new Button();
        btn.setId("export");
        btn.setText("Grid to excel");
        btn.setEnable(false);
        style = new Style();
        
        btn.setStyle(style);
        vl.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                String xname = "export_"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                try {
                    Nikitaset nikitaset;
                    if (!combobox.getText().equals("query")) {
                        nikitaset = nikitaConnection.Query( "SELECT * FROM "+ combobox.getText());
                    }else{
                        nikitaset = nikitaConnection.Query(  txt.getText());
                    }                    
                    OutputStream os = new FileOutputStream(NikitaService.getDirTmp() +"\\" +xname+".tmp");
                    NikitaSpreadsheet.saveToExcel(nikitaset, os);
                    os.close();
                } catch (Exception e) {  }  
                response.openWindows("res/file/export.xls?file="+xname+"&filename=export.xls", "_self"); 
            }
        });
        
        style = new Style();
        style.setAttr("n-layout-valign", "top");
        vl.setStyle(style);
        horisontalLayout.addComponent(vl);
        nf.addComponent(horisontalLayout);
        
        
        
          
        Nikitaset nikiset =nikitaConnection.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
        if (nikiset.getRows()==0) {
            nikiset = nikitaConnection.Query("SELECT table_name,table_name FROM user_tables ");
        }
        if (nikiset.getRows()==0) {
            combobox.setData(Nset.newArray().addData(Nset.newArray().addData("query").addData("QUERY SQL")));
        }else{
            combobox.setData(new Nset(nikiset.getDataAllVector()).addData(Nset.newArray().addData("query").addData("QUERY SQL")));
            txt.setEnable(false);
            btn.setEnable(false);
        }
        
        
        nikiset = nikitaConnection.QueryPage(1,1,"SELECT settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-reportexporter");
        Nset nbuff = Nset.readJSON(nikiset.getText(0, 0)); 
        findsetting = nikiset.getRows()>=1;
        
        combobox.setText(nbuff.getData("origin").toString());
        txt.setText(nbuff.getData("query").toString());
        if (combobox.getText().equals("query")) {
            txt.setEnable(true);
        }
        
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                if (combobox.getText().equals("query")) {
                    txt.setEnable(true);
                }
            }
        });
                
        
        
        tablegrid.setId("grid");
        nf.addComponent(tablegrid);        
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(combobox, tablegrid, txt, response);
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
        
        
  
    }
    
    private void fillGrid(Combobox combobox, Tablegrid tablegrid, Textarea txt, NikitaResponse response){
        Nikitaset ns;
        if (!combobox.getText().equals("query")) {
                    ns = nikitaConnection.QueryPage( tablegrid.getCurrentPage(), tablegrid.getShowPerPage(), "SELECT * FROM "+ combobox.getText());
                    tablegrid.setData(ns);
        }else{
                    ns = nikitaConnection.QueryPage( tablegrid.getCurrentPage(), tablegrid.getShowPerPage(), txt.getText());
                    tablegrid.setData(ns);
        }
              
        response.refreshComponent(tablegrid);
        if (ns.getError().length()>=1) {
            response.showDialog("Query Error", ns.getError(), "", "Done");
        }  
    }
    
}
