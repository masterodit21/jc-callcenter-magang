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

/**
 *
 * @author user
 */
public class webassetadd extends NikitaServlet{
    NikitaConnection nikitaConnection;    
    int dbCore;  
    String user ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        String idasset = request.getParameter("id");
        nf.setText("Add Asset");
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        Label lbl = new Label();
        lbl.setId("mode");
        lbl.setText(mode);
        lbl.setVisible(false);
        nf.addComponent(lbl);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "320");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("webassetadd-txtassetname");
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webassetadd-txtassetfname");
        txt.setLabel("Form Name");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webassetadd-txtsize");
        txt.setLabel("Size");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("webassetadd-txttype");
        txt.setLabel("Type");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("webassetadd-describe");
        txt.setLabel("Describe");
        nf.addComponent(txt);
        
        
        FileUploder upd = new FileUploder();
        upd.setId("webassetadd-updfile");
        upd.setLabel("File");    
        upd.setStyle(new Style().setAttr("n-action", "webassetadd").setAttr("n-accept", "*.pdf"));
        nf.addComponent(upd);
        
        Button btn = new Button();
        btn.setId("webassetadd-btnsave");
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
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webassetadd-btnsave").getTag());
                String mode = nset.getData("mode").toString();
                
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
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
                             
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webassetadd-btnsave").getTag());
                String id = nset.getData("id").toString();  
                
                if (reqestcode.equals("submit")) {
                    
                    try{
                        String org = NikitaService.getDirTmp()+"\\" +  response.getComponent("$#webassetadd-updfile").getText() + ".tmp";
                        String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("asset").toString();
                        Nikitaset nikitaset;
                        if(response.getContent().findComponentbyId("mode").getText().equals("edit")){    
                            nikitaset = nikitaConnection.Query("update web_asset set assetname=?,assetfname=?,assetsize=?,assettype=?,assetdescribe=?,assethash=?,"+
                                              "modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                              "where assetid=?",
                                              response.getContent().findComponentbyId("webassetadd-txtassetname").getText(),
                                              result.getData("filename").toString(),
                                              result.getData("filesize").toInteger()+"",
                                              Utility.getFileExtention(result.getData("filename").toString()),
                                              response.getContent().findComponentbyId("webassetadd-describe").getText(),
                                              Utility.MD5(System.currentTimeMillis()+""),
                                              user,id);                            
                            
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webassetadd-txtassetname").getText());
                            data.setData("activitytype", "asset");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                            
                        }else{
                            nikitaset = nikitaConnection.Query("insert into web_asset("+
                                              "assetname,assetfname,assetsize,assettype,assetdescribe,assethash,createdby,createddate)"+
                                              "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              response.getContent().findComponentbyId("webassetadd-txtassetname").getText(),
                                              result.getData("filename").toString(),
                                              result.getData("filesize").toInteger()+"",
                                              Utility.getFileExtention(result.getData("filename").toString()),
                                              response.getContent().findComponentbyId("webassetadd-describe").getText(),
                                              Utility.MD5(System.currentTimeMillis()+""),user
                                              );
                            if(dbCore == WebUtility.CORE_ORACLE){
                                nikitaset = nikitaConnection.Query("SELECT assetid FROM web_asset WHERE ROWID=?", nikitaset.getText(0, 0));
                            }
                            id = nikitaset.getText(0, 0);//gen
                            
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webassetadd-txtassetname").getText());
                            data.setData("activitytype", "asset");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                        }
                        
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{ 
                            
                            Utility.copyFile(org, path+"\\"+id+".zip");
                            Utility.deleteFileAll(org);
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }catch(Exception e){ }
                    
                }
                if(reqestcode.equals("update") && responsecode.equals("button2")){                    
                    if(!response.getComponent("$#webassetadd-updfile").getText().equals("")){
                        String md5 = Utility.MD5( System.currentTimeMillis()+"" );
                        response.submit(response.getComponent("$#webassetadd-updfile"), md5);
                    }else{
                        Nikitaset nikitaset = nikitaConnection.Query("update web_asset set assetname=?,"+ 
                                              "assetdescribe=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                              "where assetid=?",
                                              response.getContent().findComponentbyId("webassetadd-txtassetname").getText(),
                                              response.getContent().findComponentbyId("webassetadd-describe").getText(),user,
                                              id);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{                                                
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webassetadd-txtassetname").getText());
                            data.setData("activitytype", "asset");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);
                            
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    }
                    
                    
                }
                               
                if(reqestcode.equals("save") && responsecode.equals("button2")){     
                    if(!response.getComponent("$#webassetadd-updfile").getText().equals("")){
                        response.submit(response.getComponent("$#webassetadd-updfile"), Utility.MD5( System.currentTimeMillis()+"" ));
                    }else{
                        Nikitaset nikitaset = nikitaConnection.Query("insert into web_asset("+
                                              "assetname,assetfname,assetsize,assettype,assetdescribe,createdby,createddate)"+
                                              "values(?,'',0,'',?,?,"+WebUtility.getDBDate(dbCore)+")",
                                              response.getContent().findComponentbyId("webassetadd-txtassetname").getText(),
                                              response.getContent().findComponentbyId("webassetadd-describe").getText(),user);
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", response.getContent().findComponentbyId("webassetadd-txtassetname").getText());
                            data.setData("activitytype", "asset");
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
            nf.setText("Asset Edit");
             
            Nikitaset nikitaset = nikitaConnection.Query("SELECT assetname,assetfname,assetsize,assettype,assetdescribe FROM web_asset WHERE assetid=?", idasset);
           
            nf.findComponentbyId("webassetadd-txtassetname").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("webassetadd-txtassetfname").setText(nikitaset.getText(0, 1));
            nf.findComponentbyId("webassetadd-txtsize").setText(nikitaset.getText(0, 2));
            nf.findComponentbyId("webassetadd-txttype").setText(nikitaset.getText(0, 3));
             nf.findComponentbyId("webassetadd-describe").setText(nikitaset.getText(0, 4));
            nf.findComponentbyId("webassetadd-btnsave").setTag(Nset.newObject().setData("mode", "edit").setData("id", idasset).toJSON());
        }
        else{
            nf.setText("Asset Add");            
            nf.findComponentbyId("webassetadd-btnsave").setTag(Nset.newObject().setData("mode", "add").toJSON());
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
