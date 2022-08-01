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
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webexpressionlistadd extends NikitaServlet{
    private static int maxparams = 10;
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ; 
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Expression List Add");
        String mode = request.getParameter("mode");
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        ComponentGroup gc = new ComponentGroup();
        
        Style style = new Style();
        style.setStyle("width", "760");
        style.setStyle("height", "520");
         nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("explistadd-txtexpcode");
        txt.setLabel("Expression Code");
        txt.setVisible(false);
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("explistadd-txtexptitle");
        txt.setLabel("Expression Title");
        gc.addComponent(txt);
        
        Textarea txtarea = new Textarea();
        txtarea.setId("explistadd-txtexpdescribe");
        txtarea.setLabel("Expression Describe");
        gc.addComponent(txtarea);
        
        
        txt = new Textbox();
        txt.setId("explistadd-txtmobileversion");
        txt.setLabel("Mobile Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("explistadd-txtlinkversion");
        txt.setLabel("Link Version");
        gc.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("explistadd-txtwebversion");
        txt.setLabel("Web Version");
        gc.addComponent(txt);
       
        
        txtarea = new Textarea();
        txtarea.setId("explistadd-txtexpparameter");
        txtarea.setLabel("Expression Parameter");
        gc.addComponent(txtarea);
        txtarea.setVisible(false);
        
        txt = new Textbox();
        txt.setId("explistadd-group");
        txt.setLabel("Group");
        gc.addComponent(txt); 
         
        txt = new Textbox();
        txt.setId("explistadd-class");
        txt.setLabel("Javaclass");
        gc.addComponent(txt);
        
        
        
        txt = new Textbox();
        txt.setId("explistadd-code");
        txt.setLabel("Javacode");
        gc.addComponent(txt);
        
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(gc);
        gc = new ComponentGroup();
        
        for (int i = 0; i < maxparams; i++) {
            txt = new Textbox();
            txt.setId("explistadd-txtargs"+i);
            txt.setLabel("Args["+(i)+"]");
            txt.setHint("id=label;type");
            txt.setTooltip("[id: dari parameter]    [label: Param Text]   [type: (variabel,string)]");
            gc.addComponent(txt);            
        }
        
        hr.addComponent(gc);
        nf.addComponent(hr);
      
        
        Button btn = new Button();
        btn.setId("explistadd-btnsave");
        btn.setText("save");
        nf.addComponent(btn);
        
        style = new Style();
        style.setStyle("width", "672px");
        style.setStyle("height", "40px");
        btn.setStyle(style);      
        
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("explistadd-btnsave").getTag());
                String mode = nset.getData("mode").toString(); 

                if(mode.equals("edit")){                    
                    response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");                                     
                }
                else{
                    String param = getParams(response);
                    Nikitaset nikitaset = nikitaConnection.Query("insert into all_expression_list("+
                                      "exptitle,expdescribe,expparameter,mobileversion,linkversion,webversion,createdby,createddate)"+
                                      "values(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                       
                                      response.getContent().findComponentbyId("explistadd-txtexptitle").getText(),
                                      response.getContent().findComponentbyId("explistadd-txtexpdescribe").getText(),
                                      param,
                                      response.getContent().findComponentbyId("explistadd-txtmobileversion").getText(),
                                      response.getContent().findComponentbyId("explistadd-txtlinkversion").getText(),
                                      response.getContent().findComponentbyId("explistadd-txtwebversion").getText(),user);
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
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("explistadd-btnsave").getTag());
                    String id = nset.getData("id").toString(); 

                    
                    
                    String param = getParams(response);
                    Nikitaset nikitaset = nikitaConnection.Query("update all_expression_list set expcode=?,exptitle=?,expdescribe=?,expparameter=?,"+ 
                                                                 "mobileversion=?,linkversion=?,webversion=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+""+
                                                                 "where expcode=?",                                                                 
                                          response.getContent().findComponentbyId("explistadd-txtexpcode").getText(),
                                          response.getContent().findComponentbyId("explistadd-txtexptitle").getText(),
                                          response.getContent().findComponentbyId("explistadd-txtexpdescribe").getText(),
                                          param,
                                          response.getContent().findComponentbyId("explistadd-txtmobileversion").getText(),
                                          response.getContent().findComponentbyId("explistadd-txtlinkversion").getText(),
                                          response.getContent().findComponentbyId("explistadd-txtwebversion").getText(),                               
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
            nf.setText("Expression List Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            
            nf.findComponentbyId("explistadd-txtexpcode").setText(nset.getData(1).toString());
            nf.findComponentbyId("explistadd-txtexptitle").setText(nset.getData(2).toString());
            nf.findComponentbyId("explistadd-txtexpdescribe").setText(nset.getData(3).toString());
            nf.findComponentbyId("explistadd-txtexpparameter").setText(nset.getData(4).toString());
            nf.findComponentbyId("explistadd-txtmobileversion").setText(nset.getData(5).toString());
            nf.findComponentbyId("explistadd-txtlinkversion").setText(nset.getData(6).toString());
            nf.findComponentbyId("explistadd-txtwebversion").setText(nset.getData(7).toString());
            nf.findComponentbyId("explistadd-btnsave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(1).toString()).toJSON());
            
            setParams(response, nset.getData(4).toString());
        }
        
        else{
            nf.setText("Expression List Add");
            nf.findComponentbyId("explistadd-btnsave").setTag(Nset.newObject().setData("mode", "add").toJSON());
        }
        
        
        
        
        
    }
    
    private String getParams(NikitaResponse response){
        Nset n =  Nset.newObject();
                n.setData("group", response.getContent().findComponentbyId("explistadd-group").getText());
                n.setData("class", response.getContent().findComponentbyId("explistadd-class").getText());
                n.setData("code", response.getContent().findComponentbyId("explistadd-code").getText());
                 
                Nset args = Nset.newArray();
                for (int i = 0; i < maxparams; i++) {
                    String sv = response.getContent().findComponentbyId("explistadd-txtargs"+i).getText();
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
            response.getContent().findComponentbyId("explistadd-group").setText(n.getData("group").toString());
            response.getContent().findComponentbyId("explistadd-class").setText(n.getData("class").toString());
            response.getContent().findComponentbyId("explistadd-code").setText(n.getData("code").toString());
            
          
            for (int i = 0; i < Math.min(maxparams,n.getData("args").getArraySize()); i++) {
                Nset v = n.getData("args").getData(i);
                response.getContent().findComponentbyId("explistadd-txtargs"+i).setText(v.getData("id").toString()+"="+v.getData("text").toString()+";"+v.getData("type").toString());
                       
            }  
    }
    private Component createGeneratorVariable(){
          ComponentGroup horisontalLayout = new ComponentGroup();
        
        
        
        Textbox 
        txt = new Textbox();
        txt.setId("webexpression-1");
        txt.setLabel("Parameter ID");
        horisontalLayout.addComponent(txt);
        txt = new Textbox();
        txt.setId("webexpression-1");
        txt.setLabel("Parameter Title");
        horisontalLayout.addComponent(txt);
        Combobox com = new Combobox();
        com.setId("webexpression-1");
        com.setLabel("Parameter Type");
        com.setData(Nset.readJSON("[{'id':'string','text':'String'},{'id':'variable','text':'Component/Virtual'},{'id':'conn','text':'Connection'}]", true));
        
        horisontalLayout.addComponent(com);
         
        
       
        
         
        return horisontalLayout;
    }
    
}
