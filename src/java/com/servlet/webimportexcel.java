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
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.NikitaSpreadsheet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author user
 */
public class webimportexcel extends NikitaServlet{
    Label  lblfname ;
    int dbCore;
        Nikitaset ns; 
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Detail Import Excel");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        lblfname = new Label();
        lblfname.setId("fname");
        lblfname.setText(request.getParameter("fname"));
        lblfname.setVisible(false);
        nf.addComponent(lblfname);
        request.retainData(lblfname);
        
        Style style = new Style();
        style.setStyle("width", "800");
        style.setStyle("height", "670");
        nf.setStyle(style);
        
        Textsmart txt = new Textsmart();
        txt.setId("detailimport-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        //nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("detailimport-tblForm"));
            }
        }); 
                    
        HorizontalLayout horisontalLayout2 = new HorizontalLayout();
        horisontalLayout2.setVisible(false);
        
        Image img = new Image();
        img.setText("img/black.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        horisontalLayout.addComponent(img);
        
        Label lbl = new Label();
        lbl.setText("Form doesn't exist");
        horisontalLayout.addComponent(lbl);
           
        horisontalLayout2.addComponent(horisontalLayout);
        
        
        horisontalLayout = new HorizontalLayout();
        horisontalLayout.setVisible(false);
        
        img = new Image();
        img.setText("img/orange.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        horisontalLayout.addComponent(img);
        
        lbl = new Label();
        lbl.setText("Form is exist");
        horisontalLayout.addComponent(lbl);
        horisontalLayout.setVisible(false);
        
        
        horisontalLayout2.setStyle(new Style().setStyle("float", "right"));        
        horisontalLayout2.addComponent(horisontalLayout);
        nf.addComponent(horisontalLayout2);
        
        horisontalLayout = new HorizontalLayout();
        
        Checkbox checkbox = new Checkbox();
        checkbox.setId("replace");
        checkbox.setData(Nset.readJSON("[['replace','Replace Forms']]", true));
        checkbox.setText("[\"replace\"]");   
        horisontalLayout.addComponent(checkbox);
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("detailimport-tblForm");
        
        tablegrid.setColHide(3, true);
        tablegrid.showRowIndex(true, true);
        nf.addComponent(tablegrid);        
        
         
        /*
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
        */        
        Button btn = new Button();
        btn.setId("detailimport-btnimport");
        btn.setText("Import");
        btn.setStyle(new Style().setStyle("width", "150px").setStyle("height", "50px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
                String xname = "export_"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                try {
                    Nikitaset nikitaset= nikitaConnection.Query("SELECT * FROM web_form ");
                    OutputStream os = new FileOutputStream(NikitaService.getDirTmp() +"\\" +xname+".tmp");
                    NikitaSpreadsheet.saveToExcel(nikitaset, os);
                    os.close();
                } catch (Exception e) {  }                
                //response.openWindows("webexport/sample.xls?export="+xname+"&file=sample.xls", "_self"); 
                response.openWindows("res/file/sample.xls?file="+xname+"&filename=sample.xls", "_self"); 
                //response.showDialog("Import", "Complete", "",  "OK");
                //response.write();
            }
        });
        horisontalLayout = new HorizontalLayout();
        horisontalLayout.addComponent(btn);
        horisontalLayout.setStyle(new Style().setStyle("float", "right"));
        
        nf.addComponent(horisontalLayout);
        
        
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("detailimport-tblForm") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("detailimport-txtSearch");
 
        try {                    
            Nikitaset ns = NikitaSpreadsheet.readCsvToNikitaset(new FileInputStream(NikitaService.getDirTmp()+NikitaService.getFileSeparator() +lblfname.getText()+".tmp"));
            System.out.println(ns.toNset().toJSON());
            tablegrid.setData(ns);     
        } catch (Exception e) {  System.err.println(e.getMessage() ); }
        
        
        tablegrid.setColHide("", true);
       // response.getContent().findComponentbyId("detailimport-btnimport").setEnable(false);
       
         
        
        
    }
    
}
