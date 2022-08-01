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
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author user
 */
public class webimportresource extends NikitaServlet{
    Label  lblfname ;
    int dbCore;
        Nikitaset ns; 
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Detail Import Resource");        
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
              
        Image img = new Image();
        img.setText("img/black.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        horisontalLayout.addComponent(img);
        
        Label lbl = new Label();
        lbl.setText("Resource doesn't exist");
        horisontalLayout.addComponent(lbl);
           
        horisontalLayout2.addComponent(horisontalLayout);
        
        
        horisontalLayout = new HorizontalLayout();
        img = new Image();
        img.setText("img/orange.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        horisontalLayout.addComponent(img);
        
        lbl = new Label();
        lbl.setText("Resource is exist");
        horisontalLayout.addComponent(lbl);
        
        horisontalLayout2.setStyle(new Style().setStyle("float", "right"));        
        horisontalLayout2.addComponent(horisontalLayout);
        nf.addComponent(horisontalLayout2);
        
        horisontalLayout = new HorizontalLayout();
        Checkbox checkbox = new Checkbox();
        checkbox.setId("replace");
        checkbox.setData(Nset.readJSON("[['replace','Replace Resource']]", true));
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
                boolean replace = response.getContent().findComponentbyId("replace").getText().equals("[\"replace\"]");                            
                String readln= "";  Nset data = Nset.newObject();
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                
                
                try {
                    String pathfile = NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp";
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(pathfile));  
                    ZipEntry entry = null;
                    while ( (entry = zis.getNextEntry()) != null ) {
                        if (entry.getName().equals("manifest.resource")) {                                           
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis));    
                            Nset id = Nset.readJSON(bufferedReader.readLine());

                            if (id.getData("nfid").toString().equals("resource")) {
                                while ((readln = bufferedReader.readLine())!=null) { 
                                    Nset datarow = Nset.readJSON(readln);
                                     
                                    data.setData(datarow.getData(0).toString(), datarow);//resid==nset data
                                }
                            }            
                        }
                    } 
                    zis.close(); 
                    zis = new ZipInputStream(new FileInputStream(pathfile));  
                    
                     
                    while ( (entry = zis.getNextEntry()) != null ) {
                        if (entry.getName().startsWith("res/")) {                                           
                             
                            String resid= entry.getName().substring(4);
                            //resid,resname,resfname,restype,ressize,resdescribe,reshash
                            String resname  =data.getData(resid).getData(1).toString();
                            String filename =data.getData(resid).getData(2).toString();
                            String filetype =data.getData(resid).getData(3).toString();
                            String filesize =data.getData(resid).getData(4).toString();
                            String describe =data.getData(resid).getData(5).toString();
                            String hash     =data.getData(resid).getData(6).toString();
                            String user     =response.getVirtualString("@+SESSION-LOGON-USER");
                            
                            
                            
                            if(replace){
                                Nikitaset ns = nikitaConnection.Query("SELECT resid from web_resource where resname=?",resname);
                                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                                for (int i = 0; i < ns.getRows(); i++) {
                                    try {
                                        new File(path+NikitaService.getFileSeparator()+resid+".res").delete();
                                    } catch (Exception e) { } 
                                }                          
                                nikitaConnection.Query("DELETE from web_resource where resname=?",resname);
                            }
                            Nikitaset ns =  nikitaConnection.Query("insert into web_resource(resname,resfname,ressize,restype,resdescribe,reshash,createdby,createddate)"+
                                              "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              resname,filename,filesize,filetype,describe,hash,user);
                            //capo file with new id
                            resid = ns.getText(0, 0);
                            String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                            try {
                                FileOutputStream fos = new FileOutputStream( path+NikitaService.getFileSeparator()+resid+".res" );
                                IOUtils.copy(zis, fos );
                                fos.flush();fos.close();
                            } catch (Exception e) {  }                              
                            
                        }
                    } 
                    
                    zis.close();    

                } catch (Exception e) {  System.err.println(e.getMessage() ); }
                     
                   
                response.showDialog("Import Resource", "Complete", "",  "OK");
                response.write();
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
        String readln= "";  Nset data = Nset.newArray();
        try {
             
            
            InputStream in = (new FileInputStream(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp"));    
            ZipInputStream zis = new ZipInputStream(in);  
            ZipEntry entry = null;
            while ( (entry = zis.getNextEntry()) != null ) {
                if (entry.getName().equals("manifest.resource")) {                                           
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zis));    
                    Nset id = Nset.readJSON(bufferedReader.readLine());

                    if (id.getData("nfid").toString().equals("resource")) {
                        while ((readln = bufferedReader.readLine())!=null) {               
                            data.addData(Nset.newArray().readJSON(readln));
                        }
                    }            
                }
            } 
            in.close();    
             
        } catch (Exception e) {  System.err.println(e.getMessage() ); }
        
        tablegrid.setData(data);                    
                    
        tablegrid.setDataHeader(Nset.readsplitString("|Name|File Name|Type|Size|Describe|"));//resid,resname,resfname,restype,ressize,resdescribe,reshash
        tablegrid.setColHide("", true);
        
        Label lbl = new Label();
        lbl.setId("logic-btnLabel");
               
        for (int i = 0; i < data.getArraySize(); i++) {
            ns = nikitaConnection.Query("SELECT resname FROM web_resource WHERE resname = ?", data.getData(i).getData(1).toString());
            if(ns.getRows()>=1){                    
                tablegrid.setChecked(i, true);
            }                     
        }
         
        response.getContent().findComponentbyId("detailimport-btnimport").setEnable(data.getArraySize()>=1?true:false);
       
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                HorizontalLayout horisontalLayout = new HorizontalLayout();
                Label lbl = new Label();
                lbl.setId("logic-btnLabel");
                ns = nikitaConnection.Query("SELECT resname FROM web_resource WHERE resname = ?", data.getData(row).getData(1).toString());
                if(ns.getRows()>=1){              
                    lbl.setText("<a style=\"color:orange\">"+data.getData(row).getData(col).toString()+"</a>");          
                }else{
                    lbl.setText(data.getData(row).getData(col).toString()); 
                }
                
                horisontalLayout.addComponent(lbl);
                return horisontalLayout;      
            }
        });
        
        
    }
    
}
