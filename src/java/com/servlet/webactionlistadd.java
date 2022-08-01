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
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webactionlistadd extends NikitaServlet{
    private static int maxparams = 10;
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ; 
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Action List Add");
        String mode = request.getParameter("mode");
                
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        ComponentGroup gc = new ComponentGroup();
        
        Style style = new Style();
        style.setStyle("width", "760");
        style.setStyle("height", "520");
         nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("actlistadd-txtactcode");
        txt.setLabel("Action Code");
        txt.setVisible(false);
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("actlistadd-txtacttitle");
        txt.setLabel("Action Title");
        gc.addComponent(txt);
        
        Textarea txtarea = new Textarea();
        txtarea.setId("actlistadd-txtactdescribe");
        txtarea.setLabel("Action Describe");
        gc.addComponent(txtarea);
        
        
        txt = new Textbox();
        txt.setId("actlistadd-txtmobileversion");
        txt.setLabel("Mobile Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("actlistadd-txtlinkversion");
        txt.setLabel("Link Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("actlistadd-txtwebversion");
        txt.setLabel("Web Version");
        gc.addComponent(txt);
        
        
        
       
        
        txtarea = new Textarea();
        txtarea.setId("actlistadd-txtactparameter");
        txtarea.setLabel("Action Parameter");
        gc.addComponent(txtarea);
        txtarea.setVisible(false);
        
        txt = new Textbox();
        txt.setId("actlistadd-group");
        txt.setLabel("Group");
        gc.addComponent(txt); 
         
        txt = new Textbox();
        txt.setId("actlistadd-class");
        txt.setLabel("Javaclass");
        gc.addComponent(txt);
        
        
        
        txt = new Textbox();
        txt.setId("actlistadd-code");
        txt.setLabel("Javacode");
        gc.addComponent(txt);
        
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(gc);
        gc = new ComponentGroup();
        
        for (int i = 0; i < maxparams; i++) {
            txt = new Textbox();
            txt.setId("actlistadd-txtargs"+i);
            txt.setLabel("Args["+(i)+"]");
            txt.setHint("id=label;type");
            txt.setTooltip("[id: dari parameter]    [label: Param Text]   [type: (variabel,string,query,form,route)]");
            gc.addComponent(txt);            
        }
        
        hr.addComponent(gc);
        nf.addComponent(hr);
      
        
        Button btn = new Button();
        btn.setId("actlistadd-btnsave");
        btn.setText("save");
        nf.addComponent(btn);
        
        style = new Style();
        style.setStyle("width", "672px");
        style.setStyle("height", "40px");
        btn.setStyle(style);      
        
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("actlistadd-btnsave").getTag());
                String mode = nset.getData("mode").toString(); 
                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    String param = getParams(response);
                    Nikitaset nikitaset = nikitaConnection.Query("insert into all_action_list("+
                                      "actiontitle,actiondescribe,actionparameter,mobileversion,linkversion,webversion,createdby,createddate)"+
                                      "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                      response.getContent().findComponentbyId("actlistadd-txtacttitle").getText(),
                                      response.getContent().findComponentbyId("actlistadd-txtactdescribe").getText(),
                                      param,
                                      response.getContent().findComponentbyId("actlistadd-txtmobileversion").getText(),
                                      response.getContent().findComponentbyId("actlistadd-txtlinkversion").getText(),
                                      response.getContent().findComponentbyId("actlistadd-txtwebversion").getText(),user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        response.closeform(response.getContent());
                        response.setResult("OK",Nset.newObject() );
                    }

                }                            
                response.write();
        
            }
        });
        
        response.setContent(nf);
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("actlistadd-btnsave").getTag());
                    String id = nset.getData("id").toString(); 

                    
                    
                    String param = getParams(response);
                    Nikitaset nikitaset = nikitaConnection.Query("update all_action_list set actioncode=?,actiontitle=?,actiondescribe=?,actionparameter=?,"+ 
                                                                 "mobileversion=?,linkversion=?,webversion=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                                                 "where actioncode=?",                                                                 
                                          response.getContent().findComponentbyId("actlistadd-txtactcode").getText(),
                                          response.getContent().findComponentbyId("actlistadd-txtacttitle").getText(),
                                          response.getContent().findComponentbyId("actlistadd-txtactdescribe").getText(),
                                          param,
                                          response.getContent().findComponentbyId("actlistadd-txtmobileversion").getText(),
                                          response.getContent().findComponentbyId("actlistadd-txtlinkversion").getText(),
                                          response.getContent().findComponentbyId("actlistadd-txtwebversion").getText(),                               
                                          user,id);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{    
                        response.closeform(response.getContent());
                        response.setResult("OK", Nset.newObject() );
                    }
                    response.write();
                }
            }
        });
        
        if(mode.equals("edit")){
            nf.setText("Action List Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("actlistadd-txtactcode").setText(nset.getData(1).toString());
            nf.findComponentbyId("actlistadd-txtacttitle").setText(nset.getData(2).toString());
            nf.findComponentbyId("actlistadd-txtactdescribe").setText(nset.getData(3).toString());
            nf.findComponentbyId("actlistadd-txtactparameter").setText(nset.getData(4).toString());
            nf.findComponentbyId("actlistadd-txtmobileversion").setText(nset.getData(5).toString());
            nf.findComponentbyId("actlistadd-txtlinkversion").setText(nset.getData(6).toString());
            nf.findComponentbyId("actlistadd-txtwebversion").setText(nset.getData(7).toString());
            nf.findComponentbyId("actlistadd-btnsave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
            
            setParams(response, nset.getData(4).toString());
        }
        
        else{
            nf.setText("Action List Add");
            nf.findComponentbyId("actlistadd-btnsave").setTag(Nset.newObject().setData("mode", "add").toJSON());
        }
        
        
        
        
        
    }
    
    private String getParams(NikitaResponse response){
        Nset n =  Nset.newObject();
                n.setData("group", response.getContent().findComponentbyId("actlistadd-group").getText());
                n.setData("class", response.getContent().findComponentbyId("actlistadd-class").getText());
                n.setData("code", response.getContent().findComponentbyId("actlistadd-code").getText());
                 
                Nset args = Nset.newArray();
                for (int i = 0; i < maxparams; i++) {
                    String sv = response.getContent().findComponentbyId("actlistadd-txtargs"+i).getText();
                    if (sv.contains("=")&& sv.contains(";")) {
                        Nset v = Nset.newObject();
                        v.setData("id",  sv.substring(0,sv.indexOf("=")));
                        v.setData("text", sv.substring(sv.indexOf("=")+1,sv.indexOf(";")));
                        v.setData("type", sv.substring(sv.indexOf(";")+1));
                        args.addData(v);
                    }                                 
                }
                n.setData("args", args);

        return   n.toJSON();
    }
    private void setParams(NikitaResponse response, String value){
            Nset n =  Nset.readJSON(value);
            response.getContent().findComponentbyId("actlistadd-group").setText(n.getData("group").toString());
            response.getContent().findComponentbyId("actlistadd-class").setText(n.getData("class").toString());
            response.getContent().findComponentbyId("actlistadd-code").setText(n.getData("code").toString());
            
          
            for (int i = 0; i < Math.min(maxparams,n.getData("args").getArraySize()); i++) {
                Nset v = n.getData("args").getData(i);
                response.getContent().findComponentbyId("actlistadd-txtargs"+i).setText(v.getData("id").toString()+"="+v.getData("text").toString()+";"+v.getData("type").toString());
                       
            }  
    }
    private Component createGeneratorVariable(){
          ComponentGroup horisontalLayout = new ComponentGroup();
        
        
        
        Textbox 
        txt = new Textbox();
        txt.setId("webaction-1");
        txt.setLabel("Parameter ID");
        horisontalLayout.addComponent(txt);
        txt = new Textbox();
        txt.setId("webaction-1");
        txt.setLabel("Parameter Title");
        horisontalLayout.addComponent(txt);
        Combobox com = new Combobox();
        com.setId("webaction-1");
        com.setLabel("Parameter Type");
        com.setData(Nset.readJSON("[{'id':'string','text':'String'},{'id':'variable','text':'Component/Virtual'},{'id':'conn','text':'Connection'}]", true));
        
        horisontalLayout.addComponent(com);
        
         
        return horisontalLayout;
    }
    
    
}
