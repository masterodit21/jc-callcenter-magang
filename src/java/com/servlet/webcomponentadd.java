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
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
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
public class webcomponentadd extends NikitaServlet{

    
    /*parameter
    mode
    formName
    formId
    id
    data
    */
     
    Label condition;
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
      NikitaForm nf = new NikitaForm(this);
        String mode = request.getParameter("mode");
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        condition = new Label();
        condition.setId("logic-lblcreatedby");
        condition.setTag(request.getParameter("created"));
        condition.setText(request.getParameter("unlock"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        Style style = new Style();
        style.setStyle("width", "420");
        style.setStyle("height", "600");
        style.setStyle("n-maximizable", "false");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("compAdd-txtFormId");
        txt.setLabel("Form Name");
        txt.setVisible(false);
        txt.setText(request.getParameter("formName"));
        txt.setTag(request.getParameter("formId"));
        nf.addComponent(txt);
        
        txt = new Textsmart();
        txt.setId("compAdd-txtName");
        txt.setStyle(Style.createStyle().setStyle("n-char-accept", "[a-z0-9A-Z._]+").setStyle("n-char-lcase", "true"));
        txt.setLabel("Name");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("compAdd-txtIndex");
        txt.setText(request.getParameter("formindex"));
        txt.setLabel("Index");
        txt.setVisible(false);
        //txt.setText("1");
        nf.addComponent(txt);
        request.retainData(txt);
         
  
        Nikitaset nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select compcode,comptitle from all_component_list ORDER BY comptitle;");
        
        Combobox com = new Combobox();
        com.setId("compAdd-txtType");
        com.setLabel("Type");
        com.setData(  new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text")  );   
        com.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                changeUI(response);
                response.writeContent();
            }
        });
        
        horisontalLayout.addComponent(com);
        
        Image img = new Image();
        img.setId("comp-btnFinders");
        img.setText("/static/img/find.png");
        horisontalLayout.addComponent(img);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("compAdd-txtType").getText() );
                response.showform("webcomplist", request,"complist",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
                
        txt = new Textbox();
        txt.setId("compAdd-txtLabel");
        txt.setLabel("Label");  
        nf.addComponent(txt);
        
        txt = new Textsmart();
        txt.setId("compAdd-txtText");
        txt.setLabel("Text");  
        nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
          public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", response.getContent().findComponentbyId("compAdd-txtText").getText());
                request.setParameter("paramid", "");
                request.setParameter("idcomp", ""); 
                request.setParameter("idform", response.getContent().findComponentbyId("compAdd-txtFormId").getTag());  
                
               
                response.showform("webvariablelist", request,"comptext",true);      
                response.write();
           }
        });
        
        
        txt = new Textbox();
        txt.setId("compAdd-txtHint");
        txt.setLabel("Hint");  
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("compAdd-txtDefault");
        txt.setLabel("Default");  
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textarea();
        txt.setId("compAdd-txtList");
        txt.setLabel("Data");  
        txt.setStyle(new Style().setStyle("height", "64px"));
        nf.addComponent(txt);
        
        Textarea txtarea = new Textarea();
        txtarea.setId("compAdd-txtStyle");
        txtarea.setLabel("Style");
        txtarea.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("type", response.getContent().findComponentbyId("compAdd-txtType").getText());
                request.setParameter("style", response.getContent().findComponentbyId("compAdd-txtStyle").getText());
                response.showform("webcompproperty", request,"property",true);      
            }
        });
        
        nf.addComponent(txtarea);
        horisontalLayout = new HorizontalLayout();
        
        
        
        //Viseble Enable Mandatary
        Nset nsetData = Nset.readJSON("[{'id':'1','text':'true'},{'id':'0','text':'false'}]",true);
        Combobox 
        
        combobox = new Combobox();
        combobox.setLabel("Visible");
        combobox.setData(nsetData);   
        combobox.setText("1");
        combobox.setId("compAdd-txtVisible");           
        nf.addComponent(combobox);          
combobox.setVisible(false);      
        combobox = new Combobox();
        combobox.setLabel("Enable");
        combobox.setData(nsetData);   
        combobox.setText("1");
        combobox.setId("compAdd-txtEnable");           
        nf.addComponent(combobox);  
combobox.setVisible(false);
        combobox = new Combobox();
        combobox.setLabel("Mandatory");
        combobox.setData(nsetData);   
        combobox.setId("compAdd-txtMandatory");           
        nf.addComponent(combobox);  
combobox.setVisible(false);

        //new 29/01/2016 //Viseble Enable Mandatary
        Checkbox checkbox = new Checkbox();
        checkbox.setId("comp-vem");
        checkbox.setLabel("View");   
        checkbox.setStyle(new Style().setStyle("n-cols", "1"));
        checkbox.setData(Nset.readJSON("[['visible','Visible'],['enable','Enable'],['mandatory','Mandatory']]", true));
        checkbox.setText(Nset.readJSON("['visible','enable']", true).toJSON());
        nf.addComponent(checkbox);         
        
        //new 29/01/2016
        Textsmart txtsmart = new Textsmart();
        txtsmart.setId("compAdd-txtValidation");
        txtsmart.setLabel("Validation");          
        txtsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "selected");
                request.setParameter("comp", "layout");
                request.setParameter("idform", response.getContent().findComponentbyId("compAdd-txtFormId").getTag() );
                request.setParameter("search", response.getContent().findComponentbyId("compAdd-txtValidation").getText() );
                //response.showform("webcompparent", request,"compvalidation",true);      
                //response.write();
            }
        });       
        nf.addComponent(txtsmart);

        txtsmart = new Textsmart();
        txtsmart.setId("compAdd-txtParent");
        txtsmart.setLabel("Parent");          
        txtsmart.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "selected");
                request.setParameter("comp", "layout");
                request.setParameter("idform", response.getContent().findComponentbyId("compAdd-txtFormId").getTag() );
                request.setParameter("search", response.getContent().findComponentbyId("compAdd-txtParent").getText() );
                response.showform("webcompparent", request,"compparent",true);      
                response.write();
            }
        });
        nf.addComponent(txtsmart);
        
        
        Button btn = new Button();
        btn.setId("compAdd-btnSave");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "70px");
        style.setStyle("height", "35px");
        style.setStyle("margin-left", "285px");
        style.setStyle("margin-top", "10px");
        btn.setStyle(style); 
        nf.addComponent(btn);        
        btn.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("compAdd-btnSave").getTag());
                String mode = nset.getData("mode").toString();
                String id = nset.getData("id").toString();
                if(mode.equals("edit")){                    
                    if(condition.getTag().equals(user) || 
                       !condition.getText().equals("")){
                        response.showDialog("Update", "Do you want to update?", "update", "No", "Yes");    
                    }else{
                        response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to update", "warning",null, "OK", "");
                    }
                }  else{

                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("compAdd-txtFormId").getTag());
                    
                    String index = response.getContent().findComponentbyId("compAdd-txtIndex").getText();                    
                    if (Utility.getInt(index)>=1) {
                        nikitaConnection.Query("UPDATE web_component set modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",compindex=compindex+2 WHERE compindex > ? AND formid=?", user, index ,response.getContent().findComponentbyId("compAdd-txtFormId").getTag());                
                        index=(Utility.getInt(index)+1)+"";
                    }else{
                        index = "1";
                    }
                    
                     //new 29/01/2016
                    Nset n = Nset.readJSON(response.getContent().findComponentbyId("comp-vem").getText());
                    String visible      = n.containsValue("visible")?"1":"0";     //response.getContent().findComponentbyId("compAdd-txtVisible").getText()
                    String enable       = n.containsValue("enable")?"1":"0";      //response.getContent().findComponentbyId("compAdd-txtEnable").getText()
                    String mandatory    = n.containsValue("mandatory")?"1":"0";   //response.getContent().findComponentbyId("compAdd-txtMandatory").getText()
            
                    Nikitaset nikitaset = nikitaConnection.Query("INSERT INTO web_component("+
                                          "formid,compname,compindex,comptype,complabel,comptext,comphint,"+ 
                                          "compdefault,complist,compstyle,enable,visible,mandatory,parent,validation,createdby,createddate)"+
                                          "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                          response.getContent().findComponentbyId("compAdd-txtFormId").getTag(),
                                          response.getContent().findComponentbyId("compAdd-txtName").getText(),
                                          index,
                                          response.getContent().findComponentbyId("compAdd-txtType").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtLabel").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtText").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtHint").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtDefault").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtList").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtStyle").getText(),
                                          enable,
                                          visible,
                                          mandatory,
                                          response.getContent().findComponentbyId("compAdd-txtParent").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtValidation").getText(),
                                          user);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("compAdd-txtName").getText());
                        data.setData("activitytype", "component");
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
                if(responsecode.equals("webvariablelist")){
                    response.getContent().findComponentbyId("compAdd-txtText").setText(result.getData("variable").toString());
                    response.writeContent();
                }
                if(reqestcode.equals("property") && responsecode.equals("compproperty")){
                    response.getContent().findComponentbyId("compAdd-txtStyle").setText(result.getData("data").toString());
                    response.writeContent();
                }
                if(reqestcode.equals("complist") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("compAdd-txtType").setText(result.getData("code").toString());
                    response.getContent().findComponentbyId("compAdd-txtType").setTag(result.getData("title").toString());
                    changeUI(response);
                    response.writeContent();
                }
                if(reqestcode.equals("compparent") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("compAdd-txtParent").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("compAdd-txtParent").setTag(result.getData("code").toString());
                    response.writeContent();
                }
                 if(reqestcode.equals("compvalidation") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("compAdd-txtValidation").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("compAdd-txtValidation").setTag(result.getData("code").toString());
                    response.writeContent();
                }
                 
                if(reqestcode.equals("update") && responsecode.equals("button2")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("compAdd-btnSave").getTag());
                    String mode = nset.getData("mode").toString();
                    String id = nset.getData("id").toString();                    
                    
                    String namecomp = response.getContent().findComponentbyId("compAdd-txtName").getText();
                    Nikitaset flagroute2 = nikitaConnection.Query("SELECT compname FROM web_component WHERE compid = ? ", id);
                    String x = "%\"$"+flagroute2.getText(0, 0)+"\"%";         
                    String y = "%\"$"+flagroute2.getText(0, 0)+"\\\\%";                      
                    Nikitaset flagroute = nikitaConnection.Query("SELECT routeid,action FROM web_route WHERE action LIKE ? OR action LIKE ? AND compid IN (SELECT compid FROM web_component WHERE formid = ?) ", x,y,response.getContent().findComponentbyId("compAdd-txtFormId").getTag());
                    for (int i = 0; i < flagroute.getRows(); i++) {
                        x = flagroute.getText(i, 1);
                        x = x.replace("$"+flagroute2.getText(i, 0),"$"+namecomp );
                    }
                    
                    //form sync
                    Utility.FormSync(response.getContent().findComponentbyId("compAdd-txtFormId").getTag());
                    //new 29/01/2016
                    Nset n = Nset.readJSON(response.getContent().findComponentbyId("comp-vem").getText());
                    String visible      = n.containsValue("visible")?"1":"0";     //response.getContent().findComponentbyId("compAdd-txtVisible").getText()
                    String enable       = n.containsValue("enable")?"1":"0";      //response.getContent().findComponentbyId("compAdd-txtEnable").getText()
                    String mandatory    = n.containsValue("mandatory")?"1":"0";   //response.getContent().findComponentbyId("compAdd-txtMandatory").getText()
                    
                    Nikitaset nikitaset = nikitaConnection.Query("update web_component set formid=?,compname=?,compindex=?,comptype=?,"+ 
                                                                 "complabel=?,comptext=?,comphint=?,compdefault=?,complist=?,"+ 
                                                                 "compstyle=?,enable=?,visible=?,mandatory=?,parent=?,validation=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" "+
                                                                 "where compid=?",
                                          response.getContent().findComponentbyId("compAdd-txtFormId").getTag(),
                                          response.getContent().findComponentbyId("compAdd-txtName").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtIndex").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtType").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtLabel").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtText").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtHint").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtDefault").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtList").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtStyle").getText(),
                                          enable,
                                          visible,
                                          mandatory,
                                          response.getContent().findComponentbyId("compAdd-txtParent").getText(),
                                          response.getContent().findComponentbyId("compAdd-txtValidation").getText(),
                                          user,id);
                    if(nikitaset.getError().length() > 1)
                        response.showAlert(nikitaset.getError());                    
                    else{             
                        
                        //history timesheet
                        Nset data = Nset.newObject(); 
                        data.setData("username", user);
                        data.setData("application", "Nikita Generator");
                        data.setData("activityname", response.getContent().findComponentbyId("compAdd-txtName").getText());
                        data.setData("activitytype", "component");
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
            nf.setText("Component Edit");
            Nset nset = Nset.readJSON(request.getParameter("data"));
            
            Nikitaset nikitaset = nikitaConnection.Query("SELECT formname FROM web_form WHERE formid=?", nset.getData(1).toString());
            
            
            nf.findComponentbyId("compAdd-txtFormId").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("compAdd-txtFormId").setTag(nset.getData(1).toString());
            nf.findComponentbyId("compAdd-txtName").setText(nset.getData(2).toString());
            nf.findComponentbyId("compAdd-txtIndex").setText(nset.getData(3).toString());
            nf.findComponentbyId("compAdd-txtType").setText(nset.getData(4).toString());
            nf.findComponentbyId("compAdd-txtLabel").setText(nset.getData(5).toString());
            nf.findComponentbyId("compAdd-txtText").setText(nset.getData(6).toString());
            nf.findComponentbyId("compAdd-txtHint").setText(nset.getData(7).toString());
            nf.findComponentbyId("compAdd-txtDefault").setText(nset.getData(14).toString());
            nf.findComponentbyId("compAdd-txtList").setText(nset.getData(9).toString());
            nf.findComponentbyId("compAdd-txtStyle").setText(nset.getData(10).toString());
            //nf.findComponentbyId("compAdd-txtEnable").setText(nset.getData(11).toString());
            //nf.findComponentbyId("compAdd-txtVisible").setText(nset.getData(12).toString());
            //nf.findComponentbyId("compAdd-txtMandatory").setText(nset.getData(13).toString());
            nf.findComponentbyId("compAdd-txtParent").setText(nset.getData(8).toString());//parent
            nf.findComponentbyId("compAdd-txtValidation").setText(nset.getData(15).toString());//last
            
            //new VisibleEnableMandator
            Nset n = Nset.newArray();
            if (nset.getData(12).toString().equalsIgnoreCase("1")) {
                n.addData("visible");
            }
            if (nset.getData(11).toString().equalsIgnoreCase("1")) {
                n.addData("enable");
            }
            if (nset.getData(13).toString().equalsIgnoreCase("1")) {
                n.addData("mandatory");
            }            
            nf.findComponentbyId("comp-vem").setText(n.toJSON());
            
            
            
            nf.findComponentbyId("compAdd-btnSave").setTag(Nset.newObject().setData("mode", "edit").setData("id", nset.getData(0).toString()).toJSON());
        }
        
        else if(mode.equals("insert")){
            nf.setText("Component Insert");
            nf.findComponentbyId("compAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        else{
            nf.setText("Component Add");            
            nf.findComponentbyId("compAdd-btnSave").setTag(Nset.newObject().setData("mode", "insert").toJSON());
        }
        
        response.setContent(nf);
        changeUI(response);
    }
    private void changeUI(NikitaResponse response){
        NikitaForm nf = response.getContent();boolean locked =true;
        String codeType = response.getContent().findComponentbyId("compAdd-txtType").getText();
        if (codeType.startsWith("navreceiver")) {
            locked=false;
            nf.findComponentbyId("compAdd-txtText").setLabel("Receiver Text");
        }else{
            locked=true;
            nf.findComponentbyId("compAdd-txtText").setLabel("Text");
        }
        /*
        nf.findComponentbyId("compAdd-txtFormId").setVisible(locked);
        nf.findComponentbyId("compAdd-txtFormId").setVisible(locked);
        nf.findComponentbyId("compAdd-txtName").setVisible(locked);
        nf.findComponentbyId("compAdd-txtIndex").setVisible(locked);
        nf.findComponentbyId("compAdd-txtType").setEnable(locked);
        nf.findComponentbyId("compAdd-txtLabel").setEnable(locked);
        nf.findComponentbyId("compAdd-txtText").setEnable(locked);
        nf.findComponentbyId("compAdd-txtHint").setEnable(locked);
        nf.findComponentbyId("compAdd-txtDefault").setEnable(locked);
        nf.findComponentbyId("compAdd-txtList").setEnable(locked);
        nf.findComponentbyId("compAdd-txtStyle").setEnable(locked);
        nf.findComponentbyId("compAdd-txtEnable").setEnable(locked);
        nf.findComponentbyId("compAdd-txtVisible").setEnable(locked);
        nf.findComponentbyId("compAdd-txtMandatory").setEnable(locked);
        nf.findComponentbyId("compAdd-txtParent").setEnable(locked);
         */
    }
        
    
}
