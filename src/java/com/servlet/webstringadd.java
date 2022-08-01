/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
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
public class webstringadd extends NikitaServlet{
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ;
    String mode;
    /*parameter
    mode
    data
    id
    */
    
    private Style getTextStyle(){
        return Style.createStyle("height", "60px");
    }
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        mode = request.getParameter("mode");
                
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Style style = new Style();
        style.setStyle("width", "410");
        style.setStyle("height", "510");
        nf.setStyle(style);
        
        
        
        //column
        ComponentGroup verticalLayout = new ComponentGroup();
        verticalLayout.setId("webstring-column");        
        nf.addComponent(verticalLayout);
        
        for (int i = 0; i < 250; i++) {
            Component component = new Textarea();
            component.setStyle(getTextStyle());
            if (i==0) {
                component = new Textsmart();
            }            
            component.setId("webstring-gen-"+i);//just bufferd
            verticalLayout.addComponent(component);            
        }
        
        
        
        Button btn = new Button();
        btn.setId("webstring-btnSave");
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
                ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webstring-column");
                        
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webstring-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                Nikitaset nikitaset = null;
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                    
                }
                else{
                    StringBuffer sb2 = new StringBuffer();
                    StringBuffer sb3 = new StringBuffer();
                    String z2 = ""; String z3 = "";
                    
                    for (int i = 0; i < verticalLayout.getComponentCount(); i++) {    
                        if (verticalLayout.getComponent(i).getTag().startsWith("val_")) {
                           sb2.append(",").append(verticalLayout.getComponent(i).getTag());
                           sb3.append(",").append("'").append( Component.escapeSql(verticalLayout.getComponent(i).getText())  ).append("'");
                        }           
                    }
                    
                    
                    z2 = sb2.toString();
                    z3 = sb3.toString();
                    
                    if(!z2.equals("") || !z3.equals("")){
                        z2 = z2.substring(1, sb2.toString().length());
                        z3 = z3.substring(1, sb3.toString().length());
                        nikitaset = nikitaConnection.Query("INSERT INTO web_string ("+z2+") VALUES ("+z3+")");
                    }
                            
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{
                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", verticalLayout.getComponent(0).getText());
                            data.setData("activitytype", "webstring");
                            data.setData("mode", "add");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    
                }
                response.write();
                
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webstring-column");
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                 
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webstring-btnSave").getTag());
                    String id = nset.getData("id").toString();                    
                    
                    Nikitaset nikitaset= null;
                          
                    
                    StringBuffer sb2 = new StringBuffer();
                    StringBuffer sb3 = new StringBuffer();
                    String z2 = ""; String z3 = "";
                    
                    for (int i = 0; i < verticalLayout.getComponentCount(); i++) {    
                        if (verticalLayout.getComponent(i).getTag().startsWith("val_")) {
                           sb2.append(",").append(verticalLayout.getComponent(i).getTag()).append("=").append("'").append( Component.escapeSql(verticalLayout.getComponent(i).getText()) ).append("'");
                           
                        }           
                    }
                    
                    
                    z2 = sb2.toString();
                    
                    if(!z2.equals("") || !z3.equals("")){
                        z2 = z2.substring(1, sb2.toString().length());
                        nikitaset = nikitaConnection.Query("UPDATE web_string SET "+z2+" WHERE string_id=?",id);
                        
                    }
                            
                    
                        
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{                    

                            //history timesheet
                            Nset data = Nset.newObject(); 
                            data.setData("username", user);
                            data.setData("application", "Nikita Generator");
                            data.setData("activityname", verticalLayout.getComponent(0).getText());
                            data.setData("activitytype", "webstring");
                            data.setData("mode", "edit");
                            data.setData("additional", "");
                            Utility.SaveActivity(data, response);

                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                    
                    response.write();
                }
            }
        });
        
        if(mode.equals("edit")){
            nf.setText("Web String Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("webstring-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
            if(mode.equals("edit")){
                    Nset nset = Nset.readJSON(request.getParameter("data"));
                    Nikitaset nikiset = nikitaConnection.Query("select * from web_string where string_id = ? ",nset.getData(0).toString());
            
                Nset narray = Nset.newArray();
                for (int i = 1; i < nikiset.getDataAllHeader().size(); i++) {                  
                    narray.addData(Nset.newObject().setData("text",nikiset.getText(0, i)).setData("id",nikiset.getHeader(i)));

                }                     
                fillcolumn(response, narray,0,null);  
            }else{
                    Nikitaset nikiset = nikitaConnection.Query("select * from web_string");
                    //??????????????
                    Nset narray = Nset.newArray();
                      for (int i = 1; i < nikiset.getDataAllHeader().size(); i++) {
                            narray.addData(Nset.newObject().setData("text","").setData("id",nikiset.getHeader(i)));

                        }                     
                    fillcolumn(response, narray,0,null);     
                }
                    
            }
        });
        
        response.setContent(nf);
    }
    
    
    public void fillcolumn(NikitaResponse response, Nset n, int x,Nset argsn) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webstring-column");
        String data = "";        
        
        if(x != 0){
            for (int i = 0; i < x; i++) {  
                if(n != null)            
                    data = n.getData(""+i+"").toString();
                
                createGeneratorVariable2(i, verticalLayout,data);
            }  
            for (int i = verticalLayout.getComponentCount()-1; i >= x; i--) {
               verticalLayout.removeComponent(i);
            }
        }else{
            for (int i = 0; i < n.getData().getArraySize(); i++) {    
                Nset v = n.getData(i);

                if(argsn != null)                            
                data = argsn.getData(v.getData("id").toString()).toString();
                
                createGeneratorVariable(i, v,verticalLayout,data);
            }  
            for (int i = verticalLayout.getComponentCount()-1; i >= n.getData().getArraySize(); i--) {
               verticalLayout.removeComponent(i);
            }
        }
        
    }
    
    private Component createGeneratorVariable(int i, Nset param,ComponentGroup verticalLayout,String temp){
        Component txt = verticalLayout.getComponent(i);
        if (txt instanceof Textarea) {
            txt.setStyle(getTextStyle());
        }
        txt.setVisible(true);
        txt.setEnable(true);
        if(!temp.equals(""))
            txt.setText(temp); 
        txt.setLabel(param.getData("id").toString());            
        txt.setTag(param.getData("id").toString());
        txt.setText(param.getData("text").toString());
        replaceLabel(txt);
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        return horisontalLayout;
    }
    
    private void replaceLabel(Component txt){
        String s = txt.getLabel();
        if (s.equals("nama")) {
            s = "Key Name";
        }else if (s.startsWith("val_def")) {
            s = "Default";
        }else if (s.startsWith("val_")) {
            s= s.substring(4).toUpperCase();
        }
        txt.setLabel(s);
    }
    
    private Component createGeneratorVariable2(int i, ComponentGroup verticalLayout,String temp){
        Component txt = verticalLayout.getComponent(i);
         if (txt instanceof Textarea) {
            txt.setStyle(getTextStyle());
        }
        txt.setVisible(true);
        txt.setEnable(true);         
        txt.setTag(i+"");
        if(!temp.equals(""))
            txt.setText(temp); 
        
        txt.setLabel("Param ["+(i+1)+"]");
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        });   
        replaceLabel(txt);
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        return horisontalLayout;
    }
}
