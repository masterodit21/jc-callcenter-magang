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
import static com.nikita.generator.NikitaService.getFileSeparator;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 

/**
 *
 * @author user
 */
public class webexportresource extends NikitaServlet{
    private static final String ALL = "*";
    NikitaConnection nikitaConnection  ;
    String user ; 
    /*parameter
    search
    mode
    data
    */
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);        
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Export Resources  (*.resources)");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        
        Style style = new Style();
        style.setStyle("width", "800");
        style.setStyle("height", "670");
        nf.setStyle(style);
        
        
        
        Textsmart txt = new Textsmart();
        txt.setId("exp-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("exp-tblForm"));
            }
        }); 
        Checkbox checkbox = new Checkbox();
        checkbox.setId("recurse");
        checkbox.setData(Nset.readJSON("[['recurse','Recuse Forms']]", true));
        checkbox.setText("[\"recurse\"]");
        horisontalLayout.addComponent(checkbox);
        checkbox.setVisible(false);
        
        //add right forparent
        Combobox combo = new Combobox();
        combo.setId("exp-typez");
        combo.setLabel("Type");
        combo.setText(ALL);
        combo.setStyle(new Style().setStyle("float", "right").setStyle("n-searchicon", "true").setAttr("n-layout-align", "right").setStyle("n-label-width", "80px").setStyle("width", "200px"));
        combo.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);     
                response.refreshComponent(response.getContent().findComponentbyId("exp-typez"));
                response.refreshComponent(response.getContent().findComponentbyId("exp-tblForm"));
                response.write();
            }
        });
        combo.setVisible(false);
         
        HorizontalLayout hr = new HorizontalLayout();
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        hr.addComponent(horisontalLayout);
        hr.addComponent(combo);
        
        nf.addComponent(hr);
        
              
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("exp-tblForm");
        //tablegrid.setDataHeader(Nset.readsplitString("No|Name|Title|Type|Style|Describe|"));   
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|File Name|Describe|Size|Type|")); //set header table
        
        tablegrid.setColHide(6, true);
        tablegrid.setColHide(4, true);
        tablegrid.setColHide(0, true);
        tablegrid.showRowIndex(true, true);
        tablegrid.setMultiCheckCol(0);
        
        nf.addComponent(tablegrid);        
        
         
 
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(response.getContent().findComponentbyId("exp-typez"));
                response.writeContent();
            }
        });
        
        horisontalLayout = new HorizontalLayout();
        Button btn = new Button();
        btn.setId("exp-btncode");
        btn.setText("Generate Code");
        btn.setStyle(new Style().setStyle("width", "150px").setStyle("height", "50px").setStyle("margin-right", "10px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialog("Surce code", "Jangan Males2 donk, bikin sendiri", "OK", "NO");
                response.write();
            }
        });
        horisontalLayout.addComponent(btn);
        btn.setVisible(false);
        
        btn = new Button();
        btn.setId("exp-btnexp");
        btn.setText("Export");
        btn.setStyle(new Style().setStyle("width", "150px").setStyle("height", "50px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                Nset result = Nset.newArray();   
                
                
              
                
                
                for (int j = 0; j < check.getArraySize(); j++) {
                    result.addData(check.getData(j).toString());
                }
               
                
               
                
                
           
                if (result.getArraySize()>=1) {
                    String xname = Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                    try {    
                        //manifest
                        OutputStream manifestos = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"export_"+xname+".manifest.tmp");
                        manifestos.write(Nset.newObject().setData("nfid", "resource").setData("user", response.getVirtualString("@+SESSION-LOGON-USER")).setData("format", "nsetline").setData("timestamp", Utility.Now()).setData("rows", result.getArraySize()).toJSON().getBytes());
                        manifestos.write(Component.ENTER.getBytes());                        
                        //resource file out
                        OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"export_"+xname+".tmp");
                        ZipOutputStream zos = new ZipOutputStream(os);  
                        zos.setLevel(0);
                            for (int i = 0; i < result.getArraySize(); i++) {//resid,resname,resfname,resdescribe,ressize,restype
                                try {
                                    String fid= result.getData(i).toString();
                                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                                    FileInputStream fis = new FileInputStream(path+getFileSeparator()+fid+".res");
                                    if (fis!=null) {
                                        Nikitaset nikitaset = nikitaConnection.Query("SELECT resid,resname,resfname,restype,ressize,resdescribe,reshash FROM web_resource WHERE resid=?",fid);                                        
                                        //manifest
                                        manifestos.write(new Nset(nikitaset.getDataAllVector()).getData(0).toJSON().getBytes());
                                        manifestos.write(Component.ENTER.getBytes());
                                        //data resource                                        
                                        ZipEntry ze= new ZipEntry( "res/" + nikitaset.getText(0, "resid") );
                                        
                                        //ze.setComment(new Nset(nikitaset.getDataAllVector()).toJSON());
                                        zos.putNextEntry(ze);                                         
                                        int length;byte[] buffer = new byte[1024];
                                        while ((length = fis.read(buffer)) > 0) {
                                            zos.write(buffer, 0, length);
                                        }                               
                                        zos.closeEntry();  
                                        fis.close();
                                    }
                                } catch (Exception e) {  }                                   
                            }    
                            
                            ZipEntry ze= new ZipEntry( "META-INF/" );
                            zos.putNextEntry(ze);                   
                            zos.closeEntry();  
                            
                            manifestos.flush();
                            manifestos.close();
                            //manifest
                            ze= new ZipEntry( "manifest.resource" );
                            zos.putNextEntry(ze);  
                            FileInputStream fis = new FileInputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator()  +"export_"+xname+".manifest.tmp");
                                int length;byte[] buffer = new byte[1024];
                                while ((length = fis.read(buffer)) > 0) {
                                    zos.write(buffer, 0, length);
                                }        
                            zos.closeEntry(); 
                            fis.close();
                        zos.flush();
                        zos.close();                        
                        
                    } catch (Exception e) {  }                

                    response.openWindows( component.getBaseUrl("/base/webexportresource?export="+xname) , "_blank");//_self
                }else{
                    response.showDialog("Export", "Please choose one of the following", "", "OK");
                }               
                response.write();
            }
        });
        
        
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
    private Nset populateAllForms(Nset forms, NikitaConnection nc, String formid){
        
        Nikitaset ns = nc.Query("SELECT action FROM web_route WHERE compid IN (SELECT compid FROM web_component WHERE formid = ?) ",  formid); 
        for (int i = 0; i < ns.getRows(); i++) {
            Nset nv = Nset.readJSON(ns.getText(i, 0));
            String code = nv.getData("code").toString();
            String classes = nv.getData("class").toString();
            if(classes.equals("FormAction")){
                String param = "";
                if (code.equals("calllink")||code.equals("calltask")||code.equals("callfunction")||code.equals("showform")||code.equals("showwindows")||code.equals("openwindows")||code.equals("showfinder")){
                    param = nv.getData("args").getData("param1").toString();
                    if (param.startsWith("{")) {
                            Nset n = Nset.readJSON(param);
                            param=n.getData("formid").toString();
                            if (!n.getData("formname").toString().equals("")) {
                                param=n.getData("formname").toString();
                            }
                    }                    
                }else if(code.equals("inflate")){
                    param = nv.getData("args").getData("param3").toString();                         
                }
                if (param.length()>=1){
                    String fid = nc.Query("SELECT formid FROM web_form WHERE formname = ?",  param).getText(0, 0); 
                    
                    boolean found = false;
                    for (int j = 0; j < forms.getArraySize(); j++) {
                        if (forms.getData(j).toString().equals(fid)) {
                            found=true;
                            break;
                        }
                    }
                    if (!found) { 
                        forms.addData(fid);
                        populateAllForms(forms, nc, fid);
                    }                           
                }
            }
        }        
        return forms;
    }
    
    private Nset formExport(NikitaConnection nikitaConnection, String fid ){
        Nset out = Nset.newArray();
        //form  [form:[comp:[logic...]]]
        //componet
        //logic       
        
        Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_form WHERE formid=?",fid);
        out.addData(nikitaset.getText(0, "formname"));
        out.addData(nikitaset.getText(0, "formtitle"));
        out.addData(nikitaset.getText(0, "formtype"));
        out.addData(nikitaset.getText(0, "formstyle"));
        out.addData(nikitaset.getText(0, "formdescribe"));
        out.addData(nikitaset.getText(0, "formindex"));
        out.addData(user);
                
        nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid=?",fid);
        Nset comset = Nset.newArray();
        for (int i = 0; i < nikitaset.getRows(); i++) {
            String copycompid = nikitaset.getText(i, "compid");  
  
            
            Nikitaset ns = nikitaConnection.Query("SELECT * FROM web_route WHERE compid=?",copycompid);
            Nset logset = Nset.newArray();
            for (int j = 0; j < ns.getRows(); j++) {
                 
                 Nset singlelogic = Nset.newArray();
                 singlelogic.addData(ns.getText(j, "action"));//action
                 singlelogic.addData(ns.getText(j, "expression"));//expression
                 singlelogic.addData(ns.getText(j, "routeindex"));//routeindex

                 logset.addData(  singlelogic  );
            }  
            
            Nset singlecomp = Nset.newArray();
            
            singlecomp.addData(nikitaset.getText(i, "compname"));
            singlecomp.addData(nikitaset.getText(i, "compindex"));
            singlecomp.addData(nikitaset.getText(i, "comptype"));
            singlecomp.addData(nikitaset.getText(i, "complabel"));
            singlecomp.addData(nikitaset.getText(i, "comptext"));
            singlecomp.addData(nikitaset.getText(i, "comphint"));
            singlecomp.addData(nikitaset.getText(i, "compdefault"));
            singlecomp.addData(nikitaset.getText(i, "complist"));
            singlecomp.addData(nikitaset.getText(i, "compstyle"));
            singlecomp.addData(nikitaset.getText(i, "enable"));
            singlecomp.addData(nikitaset.getText(i, "visible"));
            singlecomp.addData(nikitaset.getText(i, "mandatory"));
            singlecomp.addData(nikitaset.getText(i, "parent"));
            singlecomp.addData(logset);
            comset.addData(singlecomp);
        }
        
        
        out.addData(comset);
        return out;
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("exp-tblForm") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("exp-txtSearch");
        Combobox combo = (Combobox)response.getContent().findComponentbyId("exp-typez") ;
                
        Nikitaset nikiset;
        if(txt.getText().equals("")){
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT resid,resname,resfname,resdescribe,ressize,restype,createdby "+ 
                                                       "FROM web_resource ;");
            tablegrid.setData(nikiset);
        }
        else{
            String s = "%"+txt.getText()+"%";
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"SELECT resid,resname,resfname,resdescribe,ressize,restype,createdby "+ 
                                                       "FROM web_resource WHERE (resname LIKE ? OR resfname LIKE ?); ",s,s);
            tablegrid.setData(nikiset);
        }
        
        /*
        combo.setData(new Nset(nikitaConnection.Query("SELECT DISTINCT formtype FROM web_form WHERE formtype <> '' ").getDataAllVector()));
        if (combo.getData().getInternalObject() instanceof Vector) {
            ((Vector)combo.getData().getInternalObject() ).insertElementAt(ALL, 0);
        }else{
            combo.setData(Nset.readsplitString(ALL));
        }
        */
        
    }

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //System.err.println(request.getParameter("export"));
        if (request.getParameter("export").trim().equals("")) {
            super.OnRun(request, response, logic);    
        }else{
            try {
                NikitaService.getResourceFile(new File(NikitaService.getDirTmp()+NikitaService.getFileSeparator() +"export_"+request.getParameter("export")+".tmp"), request.getHttpServletRequest(), response.getHttpServletResponse(), request.getParameter("file").equals("")? "export.resources":request.getParameter("file"), true);
            } catch (Exception e) { }
        }     
    }

    
    
    
}

