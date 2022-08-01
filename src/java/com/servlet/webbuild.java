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
import static com.nikita.generator.NikitaService.getFileSeparator;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author rkrzmail
 */
public class webbuild extends NikitaServlet{
    NikitaConnection nikitaConnection  ;
    String user ; 
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
         nikitaConnection = response.getConnection(NikitaConnection.LOGIC);        
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Generate Application (Build)");
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "480");
        style.setStyle("n-resizable", "false");
        style.setStyle("n-maximizable", "false");
        
        nf.setStyle(style);
        
        
        VerticalLayout verticalLayout = new VerticalLayout();
        
        
        Checkbox 
        checkbox = new Checkbox();
        checkbox.setId("android");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['android','Android Mobile (*.apk)']]",true));
             
        
        checkbox.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
            }
        });
        verticalLayout.addComponent(checkbox);
        
        Textsmart txt = new Textsmart();
        txt.setId("module");
        txt.setLabel("Module Name");
        txt.setStyle( new Style().setStyle("n-div-margin-left", "10px"));
        verticalLayout.addComponent(txt);
        
        txt = new Textsmart();
        txt.setId("icon");
        txt.setLabel("Icon Resoure");
        txt.setStyle( new Style().setStyle("n-div-margin-left", "10px"));
        verticalLayout.addComponent(txt);
        
      
        
        Component com = new Component(){
            public String getView() {
                return "<hr>";  
            }
            
        };         
        verticalLayout.addComponent(com);
        
        
        
        
        
        
        
        
        
        
        //web jar
        nf.addComponent(verticalLayout);
        verticalLayout = new VerticalLayout();

         
        checkbox = new Checkbox();
        checkbox.setId("webwar");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['webwar','Web War (*.war)']]",true));         
        verticalLayout.addComponent(checkbox);
        
        txt = new Textsmart();
        txt.setId("warname");
        txt.setLabel("War Name");
        txt.setStyle( new Style().setStyle("n-div-margin-left", "10px"));
        verticalLayout.addComponent(txt);
        
        checkbox = new Checkbox();
        checkbox.setId("webgen");
        checkbox.setLabel("");
        checkbox.setData(Nset.readJSON("[['webgen','Web Generator Only (*.nikita)']]",true));        
        verticalLayout.addComponent(checkbox);
        
        txt = new Textsmart();
        txt.setId("webgenname");
        txt.setLabel("File Name");
        txt.setStyle( new Style().setStyle("n-div-margin-left", "10px"));
        verticalLayout.addComponent(txt);
        
        
        
        
        Button
        button = new Button();
        button.setId("build");
        button.setText("Build");
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 if (!response.getContent().findComponentbyId("webgen").getText().equals("[]")) {//   
                    String name = response.getContent().findComponentbyId("webgenname").getText();
                    String xname = Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                    
                    try {
                        //build nikita
                       
                        
                        //manifest
                        OutputStream manifestos = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"build_"+xname+".manifest.tmp");
                        manifestos.write(Nset.newObject().setData("nfid", "nikita").setData("user", response.getVirtualString("@+SESSION-LOGON-USER")).setData("format", "nsetline").setData("timestamp", Utility.Now()).setData("rows", "-1").toJSON().getBytes());
                        manifestos.write(Component.ENTER.getBytes());                        
                        //resource file out
                        OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"build_"+xname+".tmp");
                        ZipOutputStream zos = new ZipOutputStream(os);  
                        zos.setLevel(0);
                        
                            Nikitaset nikitaset = nikitaConnection.Query("SELECT resid,resname,resfname,restype,ressize,resdescribe,reshash FROM web_resource");                                
                            for (int i = 0; i < nikitaset.getRows(); i++) {//resid,resname,resfname,resdescribe,ressize,restype
                                try {
                                    String fid= nikitaset.getText(i, "resid");
                                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                                    FileInputStream fis = new FileInputStream(path+getFileSeparator()+fid+".res");
                                    if (fis!=null) {
                                        //manifest
                                        manifestos.write(new Nset(nikitaset.getDataAllVector().elementAt(i)).toJSON().getBytes());//row nikitaset
                                        manifestos.write(Component.ENTER.getBytes());
                                        //data resource                                        
                                        ZipEntry ze= new ZipEntry( "res/" + nikitaset.getText(i, "resid") );
                                        
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
                            
                            
                            zos.setLevel(8);//cimpresse now
                            
                            //manifest
                            ze= new ZipEntry( "manifest.resource" );
                            zos.putNextEntry(ze);  
                            FileInputStream fis = new FileInputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"build_"+xname+".manifest.tmp");
                                int length;byte[] buffer = new byte[1024];
                                while ((length = fis.read(buffer)) > 0) {
                                    zos.write(buffer, 0, length);
                                }        
                            zos.closeEntry(); 
                            fis.close();
                            
                            //build seeting 
                            OutputStream settingos = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"build_"+xname+".setting.tmp");
                            settingos.write(Nset.newObject().setData("nfid", "setting").setData("user", response.getVirtualString("@+SESSION-LOGON-USER")).setData("format", "nsetline").setData("timestamp", Utility.Now()).setData("rows", "-1").toJSON().getBytes());
                            settingos.write(Component.ENTER.getBytes());   
                            
                            Nikitaset ns = nikitaConnection.Query("SELECT * FROM web_form");
                            settingos.write(ns.toNset().setData("tname", "web_form").toJSON().getBytes()); 
                            settingos.write(Component.ENTER.getBytes()); 
                        
                            
                            ns = nikitaConnection.Query("SELECT * FROM web_component");
                            settingos.write(ns.toNset().setData("tname", "web_component").toJSON().getBytes()); 
                            settingos.write(Component.ENTER.getBytes()); 
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM web_route");
                            settingos.write(ns.toNset().setData("tname", "web_route").toJSON().getBytes()); 
                            settingos.write(Component.ENTER.getBytes()); 
                            
                            settingos.flush();
                            settingos.close();
                            
                            
                            ze= new ZipEntry( "generator.setting" );
                            zos.putNextEntry(ze);  
                            fis = new FileInputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"build_"+xname+".setting.tmp");
                                buffer = new byte[1024];
                                while ((length = fis.read(buffer)) > 0) {
                                    zos.write(buffer, 0, length);
                                }        
                            zos.closeEntry(); 
                            fis.close();
                            
                        zos.flush();
                        zos.close();
                        
                        
                        
                         
                     } catch (Exception e) { }
                         
                     response.openWindows( component.getBaseUrl("/base/webbuild?buildgenerator="+xname) , "_blank");//_self
                 }  
            }
        });
        verticalLayout.addComponent(button);
        
        nf.addComponent(verticalLayout);
        response.setContent(nf);
    }
    
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //System.err.println(request.getParameter("export"));
        if (!request.getParameter("buildgenerator").trim().equals("")) {
            try {
                NikitaService.getResourceFile(new File(NikitaService.getDirTmp()+NikitaService.getFileSeparator() + "build_"+ request.getParameter("buildgenerator")+ ".tmp"), request.getHttpServletRequest(), response.getHttpServletResponse(), request.getParameter("buildgenerator")+".nikita", true);
            } catch (Exception e) { }
             
        }else{
            super.OnRun(request, response, logic);   
        }     
    }
    
}
