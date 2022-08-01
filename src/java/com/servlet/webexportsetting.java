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
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
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

/**
 *
 * @author rkrzmail
 */
public class webexportsetting extends NikitaServlet{
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
        nf.setText("Export Generator Archive (*.nar)");        
        VerticalLayout verticalLayout = new VerticalLayout();
        
        
        Style style = new Style();
        style.setStyle("width", "520");
        style.setStyle("height", "670");
        style.setStyle("n-resizable", "false");
        style.setStyle("n-maximizable", "false");
        nf.setStyle(style);
        
        style = new Style();
        style.setStyle("n-label-width", "160px");
        
         
        Checkbox 
        checkbox = new Checkbox();
        checkbox.setId("filter");
        checkbox.setLabel("Filter");
        checkbox.setData(Nset.readJSON("['All Filter']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);        
               
        Component
        component = new Component(){
            public String getView() {
                return "<hr>"; 
            }        
         };
        verticalLayout.addComponent(component);
        
        
        Nikitaset 
        ns = nikitaConnection.Query("SELECT connname,connname FROM web_connection");
        
        checkbox = new Checkbox();
        checkbox.setId("connection");
        checkbox.setLabel("Connection");
        checkbox.setData(new Nset(ns.getDataAllVector()));
        
        Style stylesingle = new Style();
        stylesingle.setStyle("n-label-width", "160px");
        stylesingle.setStyle("n-label-width", "160px");
        stylesingle.setAttr("n-label-valign", "top");
        checkbox.setStyle(stylesingle.setStyle("n-cols", "2").setStyle("n-col-0-width", "140px").setStyle("n-col-1-width", "140px"));
        
        verticalLayout.addComponent(checkbox);
         
        
        component = new Component(){
            public String getView() {
                return "<hr>"; 
            }        
         };
        verticalLayout.addComponent(component);
        
        style = new Style();
        style.setStyle("n-label-width", "160px");       
        
        checkbox = new Checkbox();
        checkbox.setId("webparam");
        checkbox.setLabel("Web Parameter");
        checkbox.setData(Nset.readJSON("['All Web Parameter']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        
        
        checkbox = new Checkbox();
        checkbox.setId("mobileconnection");
        checkbox.setLabel("Mobile Connection");
        checkbox.setData(Nset.readJSON("['All Mobile Connection']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        checkbox = new Checkbox();
        checkbox.setId("module");
        checkbox.setLabel("Mobile Module");
        checkbox.setData(Nset.readJSON("['All Mobile Module']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        checkbox = new Checkbox();
        checkbox.setId("mobileparam");
        checkbox.setLabel("Mobile Parameter");
        checkbox.setData(Nset.readJSON("['All Mobile Parameter']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        component = new Component(){
            public String getView() {
                return "<hr>"; 
            }        
         };
        verticalLayout.addComponent(component);
        
        
        ns = nikitaConnection.Query("SELECT username,name FROM sys_user");
        
        checkbox = new Checkbox();
        checkbox.setId("userlogin");
        checkbox.setLabel("User Management");
        checkbox.setData(new Nset(ns.getDataAllVector()));
        checkbox.setStyle(stylesingle);
        verticalLayout.addComponent(checkbox);
        
        component = new Component(){
            public String getView() {
                return "<hr>"; 
            }        
         };
        verticalLayout.addComponent(component);
        
        checkbox = new Checkbox();
        checkbox.setId("generatorset");
        checkbox.setLabel("Generator Setting");
        checkbox.setData(Nset.readJSON("['Action,Expression,Component (All List)']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        checkbox = new Checkbox();
        checkbox.setId("generatorform");
        checkbox.setLabel("Generator Forms");
        checkbox.setData(Nset.readJSON("['All Forms [Mobile,Web,Task] (*.gen)']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        checkbox = new Checkbox();
        checkbox.setId("generatorsch");
        checkbox.setLabel("Generator Scheduller");
        checkbox.setData(Nset.readJSON("['All Scheduller [Mobile,Web] (*.sch)']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        
        checkbox = new Checkbox();
        checkbox.setId("generatorflow");
        checkbox.setLabel("Generator WorkFlow");
        checkbox.setData(Nset.readJSON("['All WorkFlow']", true));
        checkbox.setStyle(style);
        verticalLayout.addComponent(checkbox);
        
        component = new Component(){
            public String getView() {
                return "<hr>"; 
            }        
         };
        verticalLayout.addComponent(component);
        
        
        HorizontalLayout hr = new HorizontalLayout();
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        hr.addComponent(verticalLayout);
         
        
        nf.addComponent(hr);
        
        
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        Button 
        btn = new Button();
        btn.setId("exp-btnexp");
        btn.setText("Export");
        btn.setStyle(new Style().setStyle("width", "150px").setStyle("height", "50px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
                
                
           
                if (true) {
                    String xname = Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                    try {                    
                        OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +"export_"+xname+".tmp");
                        os.write(Nset.newObject().setData("nfid", "setting").setData("user", response.getVirtualString("@+SESSION-LOGON-USER")).setData("format", "nsetline").setData("timestamp", Utility.Now()).setData("rows", "").toJSON().getBytes());
                        os.write(Component.ENTER.getBytes());
                        //filter           
                        if (response.getContent().findComponentbyId("filter").getText().contains("Filter")) {//
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_filter");
                            os.write(ns.toNset().setData("tname", "web_filter").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                filterid
                                filtername
                                filterform
                                filterdesc
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */                                        
                            ns = nikitaConnection.Query("SELECT * FROM web_filter_form");
                            os.write(ns.toNset().setData("tname", "web_filter_form").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                ffid
                                filterid
                                formid
                                formname
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                            */
                        }
                        //connection                                              
                        if (!response.getContent().findComponentbyId("connection").getText().equals("[]")) {//
                            String s = response.getContent().findComponentbyId("connection").getText();
                            s=s.substring(1,s.length()-1);
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_connection WHERE connname IN ("+s+");");
                            os.write(ns.toNset().setData("tname", "web_connection").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                connid
                                connname
                                connusername
                                connpassword
                                connclass
                                connurl
                                connoption
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                        //webparam
                        if (!response.getContent().findComponentbyId("webparam").getText().equals("[]")) {//                          
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_parameter WHERE paramtype <> 'mobile';");
                            os.write(ns.toNset().setData("tname", "web_parameter").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                paramkey
                                paramvalue
                                paramdescribe
                                paramtype
                                parampriority
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                       //mobileconnection 
                        if (!response.getContent().findComponentbyId("mobileconnection").getText().equals("[]")) {//
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_connection_mobile ");
                            os.write(ns.toNset().setData("tname", "web_connection_mobile").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                connid
                                connname
                                connusername
                                connpassword
                                connclass
                                connurl
                                connoption
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        //module
                        if (!response.getContent().findComponentbyId("module").getText().equals("[]")) {//                          
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_module;");
                            os.write(ns.toNset().setData("tname", "web_module").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                moduleid
                                modulename
                                moduletitle
                                modulestyle
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                                             
                             
                            ns = nikitaConnection.Query("SELECT * FROM web_module_form;");
                            os.write(ns.toNset().setData("tname", "web_module_form").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                mfid
                                moduleid
                                formid
                                formname
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM web_module_resource;");
                            os.write(ns.toNset().setData("tname", "web_module_resource").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                mrid
                                moduleid
                                resid
                                resname
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                        
                        
                        //mobileparam
                        if (!response.getContent().findComponentbyId("mobileparam").getText().equals("[]")) {//                          
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_parameter WHERE paramtype = 'mobile';");
                            os.write(ns.toNset().setData("tname", "mobile_parameter").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                paramkey
                                paramvalue
                                paramdescribe
                                paramtype
                                parampriority
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                        //userlogin
                        if (!response.getContent().findComponentbyId("userlogin").getText().equals("[]")) {//
                            String s = response.getContent().findComponentbyId("userlogin").getText();
                            s=s.substring(1,s.length()-1);
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM sys_user WHERE username IN ("+s+");");
                            os.write(ns.toNset().setData("tname", "sys_user").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                userid
                                username
                                password
                                name
                                avatar
                                theme
                                imei
                                auth
                                status
                                position
                                lastlogin
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                        //generatorset
                        if (!response.getContent().findComponentbyId("generatorset").getText().equals("[]")) {// 
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM all_action_list");
                            os.write(ns.toNset().setData("tname", "all_action_list").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                actioncode
                                actiontitle
                                actiondescribe
                                actionparameter
                                mobileversion
                                linkversion
                                webversion
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            ns = nikitaConnection.Query("SELECT * FROM all_component_list");
                            os.write(ns.toNset().setData("tname", "all_component_list").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                compcode
                                comptitle
                                compdescribe
                                compparameter
                                mobileversion
                                linkversion
                                webversion
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            ns = nikitaConnection.Query("SELECT * FROM all_expression_list");
                            os.write(ns.toNset().setData("tname", "all_expression_list").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                expcode
                                exptitle
                                expdescribe
                                expparameter
                                mobileversion
                                linkversion
                                webversion
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM all_register_list");
                            os.write(ns.toNset().setData("tname", "all_register_list").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                registerid
                                registername
                                registerdescribe
                                mobileversion
                                linkversion
                                webversion
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM all_style_list");
                            os.write(ns.toNset().setData("tname", "all_style_list").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                registerid
                                registername
                                registerdata
                                registerdescribe
                                mobileversion
                                linkversion
                                webversion
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM all_var_function");
                            os.write(ns.toNset().setData("tname", "all_var_function").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            /*
                                varid
                                varcode
                                vardescribe
                                varparameter
                                vartype
                                createdby
                                createddate
                                modifiedby
                                modifieddate
                              */
                        }
                        
                        //generatorform
                        if (!response.getContent().findComponentbyId("generatorform").getText().equals("[]")) {// 
                            Nikitaset 
                            ns = nikitaConnection.Query("SELECT * FROM web_form");
                            os.write(ns.toNset().setData("tname", "web_form").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                        
                            
                            ns = nikitaConnection.Query("SELECT * FROM web_component");
                            os.write(ns.toNset().setData("tname", "web_component").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                            
                            
                            ns = nikitaConnection.Query("SELECT * FROM web_route");
                            os.write(ns.toNset().setData("tname", "web_route").toJSON().getBytes()); 
                            os.write(Component.ENTER.getBytes()); 
                        }
                        
                        os.flush();
                        os.close();
                    } catch (Exception e) {  }                

                    response.openWindows( component.getBaseUrl("/base/webexportsetting?export="+xname) , "_blank");//_self
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
    }
    
     public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //System.err.println(request.getParameter("export"));
        if (request.getParameter("export").trim().equals("")) {
            super.OnRun(request, response, logic);    
        }else{
            try {
                NikitaService.getResourceFile(new File(NikitaService.getDirTmp()+NikitaService.getFileSeparator() +"export_"+request.getParameter("export")+".tmp"), request.getHttpServletRequest(), response.getHttpServletResponse(), request.getParameter("file").equals("")? "export.nar":request.getParameter("file"), true);
            } catch (Exception e) { }
        }     
    }

    
}
