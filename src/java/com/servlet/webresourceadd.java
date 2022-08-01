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
import com.nikita.generator.ui.FileUploder;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author user
 */
public class webresourceadd extends NikitaServlet{
    NikitaConnection nikitaConnection;      
    int dbCore;
    String user ;
    /*parameter
    mode
    id
    */
    Label condition;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        String idres = request.getParameter("id");
        nf.setText("Add Resource");String desc = "";
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        
        condition = new Label();
        condition.setId("webresadd-lblcreatedby");
        condition.setText(request.getParameter("unlock")); 
        condition.setTag(request.getParameter("created"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        Label lbl = new Label();
        lbl.setId("mode");
        lbl.setText(mode);
        lbl.setVisible(false);
        nf.addComponent(lbl);
                
        
        Style style = new Style();
        style.setStyle("width", "415");
        style.setStyle("height", "320");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webresadd-txtresname");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webresadd-txtresfname");
        txt.setLabel("Form Name");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webresadd-txtsize");
        txt.setLabel("Size");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webresadd-txttype");
        txt.setLabel("Type");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("webresadd-describe");
        txt.setLabel("Describe");
        nf.addComponent(txt);
        
        FileUploder upd = new FileUploder();
        upd.setId("webresadd-updfile");
        upd.setLabel("File");    
        upd.setStyle(new Style().setAttr("n-action", "webresourceadd").setAttr("n-accept", "*.pdf"));
        nf.addComponent(upd);        
        
        Button btn = new Button();
        btn.setId("webresadd-btnsave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "280px");
        style.setStyle("margin-top", "15px");
        btn.setStyle(style); 
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webresadd-btnsave").getTag());
                String mode = nset.getData("mode").toString();
                if(mode.equals("edit")){                    
                    if(condition.getTag().equals(user) || !condition.getText().equals("")){
                        response.showDialog("Update", "Do you want to update?", "update", "No", "Yes"); 
                    }else{
                        response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to update", "warning",null, "OK", "");
                    } 
                    
                                                        
                }
                else{
                    response.showDialog("Save", "Do you want to save?", "save", "No", "Yes");                            
                }                   
                
                response.write();        
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                         
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webresadd-btnsave").getTag());
                String id = nset.getData("id").toString();  
                
                if (reqestcode.equals("submit")) {
                    String fname = response.getContent().findComponentbyId("webresadd-txtresname").getText();
                    if (NikitaService.isModeCloud()) {
                            String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                            if (fname.startsWith(prefix)) {                            
                            }else{
                                fname = prefix + fname;
                            }
                    }
                    
                    try{
                        
                        String org = NikitaService.getDirTmp()+NikitaService.getFileSeparator() +  result.getData("fsavename").toString()  + ".tmp";//response.getComponent("$#webresadd-updfile").getText()
                        String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                        Nikitaset nikitaset;
                        if(response.getContent().findComponentbyId("mode").getText().equals("edit")){  
                            

                            
                            nikitaset = nikitaConnection.Query("update web_resource set resname=?,resfname=?,ressize=?,restype=?,resdescribe=?,reshash=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                              "where resid=?",
                                              fname,
                                              result.getData("filename").toString(),
                                              result.getData("filesize").toInteger()+"",
                                              Utility.getFileExtention(result.getData("filename").toString()),
                                              response.getContent().findComponentbyId("webresadd-describe").getText(),
                                              Utility.MD5(System.currentTimeMillis()+""),
                                              user,id);
                            
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webresadd-txtresname").getText());
                            data.setData("activitytype", "resource");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                            
                        }else{
                            
                            nikitaset = nikitaConnection.Query("insert into web_resource("+
                                              "resname,resfname,ressize,restype,resdescribe,reshash,createdby,createddate)"+
                                              "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              fname,
                                              result.getData("filename").toString(),
                                              result.getData("filesize").toInteger()+"",
                                              Utility.getFileExtention(result.getData("filename").toString()),
                                              response.getContent().findComponentbyId("webresadd-describe").getText(),
                                              Utility.MD5(System.currentTimeMillis()+""),user);
                            if(dbCore == WebUtility.CORE_ORACLE){
                                nikitaset = nikitaConnection.Query("SELECT resid FROM web_resource WHERE ROWID=?", nikitaset.getText(0, 0));
                            }
                            id = nikitaset.getText(0, 0);//gen
                            
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webresadd-txtresname").getText());
                            data.setData("activitytype", "resource");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                        }
                        
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{ 
                            Utility.copyFile(org, path+NikitaService.getFileSeparator()+id+".res");
                            Utility.deleteFileAll(org);
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }catch(Exception e){ }
                    
                }
                
               
                if(reqestcode.equals("update") && responsecode.equals("button2")){     
                    String fname = response.getContent().findComponentbyId("webresadd-txtresname").getText();
                    if (NikitaService.isModeCloud()) {
                            String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                            if (fname.startsWith(prefix)) {                            
                            }else{
                                fname = prefix + fname;
                            }
                    }
                    if(!response.getComponent("$#webresadd-updfile").getText().equals("")){
                        String md5 = Utility.MD5( System.currentTimeMillis()+"" );
                        response.submit(response.getComponent("$#webresadd-updfile"), md5);
                    }else{
                        Nikitaset nikitaset = nikitaConnection.Query("update web_resource set resname=?,resdescribe=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                              "where resid=?",
                                              fname,
                                              response.getContent().findComponentbyId("webresadd-describe").getText(),
                                              user,id);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{                                          
                        
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webresadd-txtresname").getText());
                            data.setData("activitytype", "resource");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                            
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }
                    
                    
                }
                if(reqestcode.equals("save") && responsecode.equals("button2")){    
                    String fname = response.getContent().findComponentbyId("webresadd-txtresname").getText();
                    if (NikitaService.isModeCloud()) {
                            String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                            if (fname.startsWith(prefix)) {                            
                            }else{
                                fname = prefix + fname;
                            }
                    }
                    if(!response.getComponent("$#webresadd-updfile").getText().equals("")){
                        response.submit(response.getComponent("$#webresadd-updfile"), Utility.MD5( System.currentTimeMillis()+"" ));
                    }else{
                        Nikitaset nikitaset = nikitaConnection.Query("insert into web_resource("+
                                              "resname,resfname,ressize,restype,resdescribe,createdby,createddate)"+
                                              "values(?,'',0,'',?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              fname,response.getContent().findComponentbyId("webresadd-describe").getText(),user);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                        
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webresadd-txtresname").getText());
                            data.setData("activitytype", "resource");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }
                } 
                response.write();                
            }
        });

        if(mode.equals("edit")){
            
            
            nf.setText("Resource Edit");
            Nikitaset nikitaset = nikitaConnection.Query("SELECT resname,resfname,ressize,restype,resdescribe FROM web_resource WHERE resid=?", idres);
           
            String fname = nikitaset.getText(0, 0);
            if (NikitaService.isModeCloud()) {
                String prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                if (fname.startsWith(prefix)) {
                    fname = fname.substring(prefix.length());//1.34
                }
            }
            
            nf.findComponentbyId("webresadd-txtresname").setText(fname);
            nf.findComponentbyId("webresadd-txtresfname").setText(nikitaset.getText(0, 1));
            nf.findComponentbyId("webresadd-txtsize").setText(nikitaset.getText(0, 2));
            nf.findComponentbyId("webresadd-txttype").setText(nikitaset.getText(0, 3));
            nf.findComponentbyId("webresadd-describe").setText(nikitaset.getText(0, 4));
            nf.findComponentbyId("webresadd-btnsave").setTag(Nset.newObject().setData("mode", "edit").setData("id", idres).toJSON());
            
        }
        else{
            nf.setText("Resource Add");            
            nf.findComponentbyId("webresadd-btnsave").setTag(Nset.newObject().setData("mode", "add").toJSON());
        }
        
        response.setContent(nf);
               
    }
    
    
    @Override
    public void OnRun(NikitaRequest nikitaRequest, NikitaResponse nikitaResponse, NikitaLogic logic) {
        boolean isMultipart = nikitaResponse.handleFileMultipart(nikitaRequest);
        if (isMultipart) { 
        } else{
            super.OnRun(nikitaRequest, nikitaResponse, logic); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
     
}