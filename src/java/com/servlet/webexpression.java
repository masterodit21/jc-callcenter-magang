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
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
 

/**
 *
 * @author rkrzmail
 */
public class webexpression extends NikitaServlet{
    //mode,data={id:?,data:?}
    Label condition;
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
        @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Expression");                       
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        condition = new Label();
        condition.setId("logic-lblcreatedby");
        condition.setTag(request.getParameter("created")); 
        condition.setText(request.getParameter("unlock"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        final Label lblmode = new Label();
        lblmode.setId("webexpression-mode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        final Label idcomp = new Label();
        idcomp.setId("webexpression-lblidcomp");
        idcomp.setTag(request.getParameter("idcomp"));
        idcomp.setVisible(false);
        nf.addComponent(idcomp);
        
        final Label idform = new Label();
        idform.setId("webexpression-lblidform");
        idform.setTag(request.getParameter("idform"));
        idform.setVisible(false);
        nf.addComponent(idform);
        
        Style style = new Style();
        style.setStyle("width", "408");
        style.setStyle("height", "480");
        nf.setStyle(style);
        
        Textbox txt = new  Textsmart();
        txt.setId("webexpression-txtexp");
        txt.setLabel("Expression");
        
        Button btn = new Button();
        btn.setId("webexpression-Find");
        btn.setText("...");
        btn.setVisible(false);
        txt.setOnClickListener(new Component.OnClickListener() {        
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", response.getContent().findComponentbyId("webexpression-txtexp").getText());
                response.showform("webexpressionlist",request, "expressionlist", true);
                response.write();
                
            }
        });
         
        Textarea txtarea = new Textarea();
        txtarea.setId("webexpression-txtDescribe");
        txtarea.setLabel("Describe");
        txtarea.setEnable(false);
        txtarea.setTag(request.getParameter("data"));  
        txtarea.setText(request.getParameter("data"));       
        
        
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        horisontalLayout.addComponent(txt);
        horisontalLayout.addComponent(btn);
        nf.addComponent(horisontalLayout);
        nf.addComponent(txtarea);
        
        ComponentGroup verticalLayout = new ComponentGroup();
        verticalLayout.setId("webexpression-variable");        
        nf.addComponent(verticalLayout);
        
        for (int i = 0; i < 100; i++) {
            Component component = new Textsmart();
            component.setId("webexpression-gen-"+i);//just bufferd
            verticalLayout.addComponent(component);  
        }
 
        Component component = new Component(){
            public String getView() {
                return "<hr>"; 
            }            
        };
        nf.addComponent(component);
        
        Textsmart textsmart = new Textsmart();
        textsmart.setId("webexpression-result");
        textsmart.setLabel("Result");
        textsmart.setHint("@EXPRESSION  [true|false]");
        nf.addComponent(textsmart);
        
        
        Checkbox cckbtn = new Checkbox();
        cckbtn.setId("webexpression-flag");
        cckbtn.setText("Comment");
        cckbtn.setLabel(" ");
        cckbtn.setData(Nset.readJSON("[['hide','Hide/Comment this Logic']]",true));
        nf.addComponent(cckbtn);
        
        
        btn = new Button();
        btn.setId("webexpression-btnDone");
        btn.setText("Save");
        style = new Style();
        style.setStyle("width", "156px");
        style.setStyle("height", "40px");
        style.setStyle("margin-left", "200px");
        btn.setStyle(style);          
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset exp = Nset.newObject();
                
                Nset id = Nset.readJSON(  response.getContent().findComponentbyId(  "webexpression-txtDescribe" ).getTag()  );//databaufferd   
                Nset parameter = Nset.readJSON(  response.getContent().findComponentbyId(  "webexpression-Find" ).getTag()  );//parameter from lsit         
                
                
                
                    exp.setData("id", response.getContent().findComponentbyId(  "webexpression-txtexp" ).getTag() );//expid
                    
                    
                    exp.setData("class", parameter.getData("class").toString());
                    exp.setData("code",  parameter.getData("code").toString());
                    exp.setData("args", getExpression(response, parameter ));//args:{"id":"text"}; 
                    exp.setData("result", response.getContent().findComponentbyId("webexpression-result").getText()  );
                    exp.setData("flag", response.getContent().findComponentbyId("webexpression-flag").getText().length()>=4 ? "hide": ""  );//
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    response.showDialogResult("Update",  "Do you want to update?", "update", Nset.newObject().setData("logicid", id.getData("logicid").toString() ).setData("expression", exp.toJSON()), "No", "Yes");                  
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to update", "warning",null, "OK", "");
                }
                response.write();
        
            }
        });      
        
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webexpression-variable");
                Nset n = Nset.readJSON(  response.getContent().findComponentbyId( "webexpression-Find" ).getTag()  );
                
                
                for (int i = 0; i < verticalLayout.getComponentCount(); i++) {
                    if (!verticalLayout.getComponent(i).getTag().equals("")) {
                        verticalLayout.getComponent(i).setLabel(n.getData("args").getData(i).getData("text").toString());
                        if (n.getData("args").getData(i).getData("type").toString().startsWith("{")||n.getData("args").getData(i).getData("type").toString().startsWith("[")) {
                            verticalLayout.getComponent(i).setStyle(new Style().setStyle("n-lock", "true"));
                            if (n.getData("args").getData(i).getData("type").toString().startsWith("{")) {
                                verticalLayout.getComponent(i).setHint(Nset.readJSON(n.getData("args").getData(i).getData("type").toString()).getData("hint").toString());
                            }else{
                                verticalLayout.getComponent(i).setHint("Pilih salah satu");
                            } 
                        }else if (n.getData("args").getData(i).getData("type").toString().endsWith("s")){
                            chageTexttoArea(verticalLayout,i);
                        }
                        if (n.getData("args").getData(i).getData("type").toString().trim().equals("")||n.getData("args").getData(i).getData("type").toString().trim().equals("string")) {
                        }else{
                            verticalLayout.getComponent(i).setOnClickListener(new Component.OnClickListener() {
                                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                                }
                            });
                        }  
                        
                        
                    }
                }
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {             
                Nset n = Nset.readJSON(  response.getContent().findComponentbyId( "webexpression-txtDescribe" ).getTag()  );
                Nset exp = Nset.readJSON( n.getData("expression").toString() );
                
                Nikitaset nikitaset = nikitaConnection.Query("SELECT exptitle,expdescribe,expparameter FROM all_expression_list WHERE expcode= ? ",exp.getData("id").toString());
                
                response.getContent().findComponentbyId("webexpression-txtexp").setText(nikitaset.getText(0, 0));
                response.getContent().findComponentbyId("webexpression-txtexp").setTag(exp.getData("id").toString());
                
                response.getContent().findComponentbyId("webexpression-txtDescribe").setText(nikitaset.getText(0, 1));
                response.getContent().findComponentbyId("webexpression-Find").setTag(nikitaset.getText(0, 2));
                
                response.getContent().findComponentbyId("webexpression-result").setText( exp.getData("result").toString()  ); 
                response.getContent().findComponentbyId("webexpression-flag").setText( exp.getData("flag").toString().equals("hide")?"[\"hide\"]":""  );
                
                fillExpression(response, nikitaset.getText(0, 2), exp);
               
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webexpression-btnDone").getTag());
        
                if(responsecode.equals("webvariablelist")||responsecode.equals("webfinderlist")||responsecode.equals("webdatabase")){
                    response.getContent().findComponentbyId(result.getData("paramid").toString()).setText(result.getData("variable").toString());
                    response.writeContent();
                }
                
                if(lblmode.getTag().equals("edit")){                        
                    
                    if(reqestcode.equals("update") && responsecode.equals("button2")){
                        //form sync
                        Utility.FormSync(response.getContent().findComponentbyId("webexpression-lblidform").getTag());
                        
                        Nikitaset nikitaset = nikitaConnection.Query("UPDATE web_route SET modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+",expression=?"+
                                                                     " WHERE routeid=?",                                                                 
                                              user,result.getData("expression").toString(),  result.getData("logicid").toString());
                        if(nikitaset.getError().length() > 1)
                            response.showAlert(nikitaset.getError());                    
                        else{   
     
                            response.closeform(response.getContent());
                            response.setResult("OK",Nset.newObject() );
                        }
                        response.write();
                    }
                }
                if(reqestcode.equals("expressionlist") && responsecode.equals("OK")){                     
                    response.getContent().findComponentbyId("webexpression-txtexp").setText(result.getData("title").toString());
                    response.getContent().findComponentbyId("webexpression-txtexp").setTag(result.getData("id").toString());//actid
                    
                    response.getContent().findComponentbyId("webexpression-txtDescribe").setText(result.getData("describe").toString());
                    response.getContent().findComponentbyId("webexpression-Find").setTag(result.getData("parameter").toString());
                    
                      
                    Nset n = Nset.readJSON(  response.getContent().findComponentbyId( "webexpression-txtDescribe" ).getTag()  );
                    Nset exp = Nset.readJSON( n.getData("expression").toString() );                  
                     
                    
                    fillExpression(response, result.getData("parameter").toString(), exp);
                    response.writeContent();
                }
                
            }
        });
        
           
        response.setContent(nf);
        
        if(lblmode.getTag().equals("edit")){
            Nset nset = Nset.readJSON(request.getParameter("data"));
            nf.findComponentbyId("webexpression-btnDone").setTag(Nset.newObject().setData("mode", "edit").setData("idroute", nset.getData(0).toString()).toJSON());
        }
        
    }
    
    
    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if(component.startsWith("webexpression-gen-")){
            request.setParameter("search", response.getContent().findComponentbyId(component).getText());
            request.setParameter("paramid", component);
            request.setParameter("idcomp", response.getContent().findComponentbyId("webexpression-lblidcomp").getTag()); 
            request.setParameter("idform", response.getContent().findComponentbyId("webexpression-lblidform").getTag());  
            
            Nset n = Nset.readJSON(response.getContent().findComponentbyId(component).getTag() );
            if (n.getData("type").toString().startsWith("variable")) {
                response.showform("webvariablelist", request);  
            }else if (n.getData("type").toString().startsWith("query")) {
                response.showform("webdatabase", request);  
            }else if (n.getData("type").toString().startsWith("route")) {
                response.showform("webroute", request);      
            }else if (n.getData("type").toString().startsWith("form")) {
                
            }else if (n.getData("type").toString().startsWith("resource")) {
                request.setParameter("code", "selected");
                response.showform("webresource", request);    
            }else if (n.getData("type").toString().startsWith("{")||n.getData("type").toString().startsWith("[")) {
                request.setParameter("mode", "combobox");   
                if (n.getData("type").toString().startsWith("{")) {
                    request.setParameter("data", Nset.readJSON(n.getData("type").toString()).getData("data").toJSON() );
                }else{
                    request.setParameter("data", n.getData("type").toString() );
                } 
                response.showform("webfinderlist", request);  
            }else {   
                
            }
            
           
            
        }else{
            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }
    }   
    
    
    public void fillExpression(NikitaResponse response, String parameter, Nset buffered) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webexpression-variable");
        Nset n =  Nset.readJSON(parameter);         

        for (int i = 0; i < n.getData("args").getArraySize(); i++) {    
            Nset v = n.getData("args").getData(i);
              
                createGeneratorVariable(i, v, buffered.getData("args").getData(v.getData("id").toString()).toString(),verticalLayout);
             
        }        
        for (int i = verticalLayout.getComponentCount()-1; i >= n.getData("args").getArraySize(); i--) {    
           verticalLayout.removeComponent(i);
        } 
        
    }
    private void createGeneratorVariable(int i, Nset param, String bufferd,ComponentGroup verticalLayout){
        Component txt = verticalLayout.getComponent(i);
        txt.setId("webexpression-gen-"+i);
        txt.setVisible(true);
        txt.setEnable(true);
        txt.setLabel(param.getData("text").toString());
        txt.setTag(param.toJSON());
        txt.setText(bufferd);
        
        if (param.getData("type").toString().startsWith("{")||param.getData("type").toString().startsWith("[")) {
            txt.setStyle(new Style().setStyle("n-lock", "true"));
            if (param.getData("type").toString().startsWith("{")) {
                 txt.setHint(Nset.readJSON(param.getData("type").toString()).getData("hint").toString());
            }else{
                 txt.setHint("Pilih salah satu");
            } 
        }else if (param.getData("type").toString().endsWith("s")){
            chageTexttoArea(verticalLayout,i);
        }
        if (param.getData("type").toString().trim().equals("")||param.getData("type").toString().trim().equals("string")) {
        }else{
            txt.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                  
                }
            });
        }        
 
 
    }
    private void chageTexttoArea(ComponentGroup com, int i){
        Component org = com.getComponent(i);
        Component component = new Textarea();
        component.setId(org.getId());
        component.setText(org.getText());
        component.setLabel(org.getLabel());
        component.setTag(org.getTag());
        component.setHint(org.getHint());
        component.setName(org.getName());
        component.setForm(org.getForm());
        component.setTooltip(org.getTooltip());
        
        component.setVisible(org.isVisible());
        component.setEnable(org.isEnable());
        
        
        com.setComponentAt(component, i);
    }
    
    public Nset getExpression(NikitaResponse response, Nset param ) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webexpression-variable");
        Nset result =  Nset.newObject();
        Nset n =  param;
 
        for (int i = 0; i < verticalLayout.getComponentCount(); i++) {             
            if (!verticalLayout.getComponent(i).getTag() .equals("")) {

               result.setData(Nset.readJSON(verticalLayout.getComponent(i).getTag() ).getData("id").toString(), verticalLayout.getComponent(i).getText() );   
            }
           
        } 
        
        return result;
    }
}
