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
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;

/**
 *
 * @author user
 */
public class webformargs extends NikitaServlet{

    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Form List Argument and Result");  
         
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "350");
        nf.setStyle(style);
        
        final Label lbldata = new Label();
        lbldata.setId("webformargs-lbldata");
        lbldata.setTag(request.getParameter("search"));
        lbldata.setVisible(false);
        nf.addComponent(lbldata);   
             
        final Label lblparam = new Label();
        lblparam.setId("webformargs-lblparam");
        lblparam.setVisible(false);
        nf.addComponent(lblparam);  
        
         final Label lblcurrid = new Label();
        lblcurrid.setId("webformargs-lblcurridform");
        lblcurrid.setTag(request.getParameter("idform"));
        lblcurrid.setVisible(false);
        nf.addComponent(lblcurrid); 
        
        
        final Label lblidform = new Label();
        lblidform.setId("webformargs-lblidform");
 
        lblidform.setVisible(false);
        nf.addComponent(lblidform); 
        
        
        Textsmart txt = new Textsmart();
        txt.setId("webformargs-txtSearch");
        txt.setLabel("Form Name");
        txt.setTag(request.getParameter("paramid"));
        txt.setStyle(new Style().setStyle("n-lock", "true")); 
        nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("paramid",response.getContent().findComponentbyId("webformargs-txtSearch").getTag());
                request.setParameter("search",response.getContent().findComponentbyId("webformargs-txtSearch").getText());
                response.showform("webformlist", request,"fromlist", true);
                response.write();
            }
        });
        
        //column
        ComponentGroup verticalLayout2 = new ComponentGroup();
        verticalLayout2.setId("webformargs-column");        
        nf.addComponent(verticalLayout2);
        for (int i = 0; i < 100; i++) {
            Textsmart component = new Textsmart();
            component.setId("webformargs-gen-"+i);//just bufferd
            verticalLayout2.addComponent(component);
            
            component.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {        }
            }); 
        }
//        nf.addComponent(verticalLayout2);
        
        HorizontalLayout h = new HorizontalLayout();
        Button btn = new Button();
        btn.setId("webformargs-btnexec");
        btn.setText("Execute");
        btn.setVisible(false);
        btn.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
            }
        });
        h.addComponent(btn);
        
        btn = new Button();
        btn.setId("webformargs-btndone");
        btn.setText("Done");          
        style = new Style();
        style.setStyle("width", "156px");
        style.setStyle("height", "40px");
        style.setStyle("margin-left", "200px");
        btn.setStyle(style);          
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {   
                    Nset exp = Nset.newObject();
                    exp.setData("args", getAction(response));
                    exp.setData("formname",response.getContent().findComponentbyId("webformargs-txtSearch").getText()); 
                    //exp.setData("formid",response.getContent().findComponentbyId("webformargs-lblidform").getTag());                    
                     /*
                    update 16/01/2021 tidak mennggunakan idform
                    */

                    
                    
                    Nset result = Nset.newObject();                                        
                    result.setData("variable", exp.toJSON());             
                    result.setData("paramid",response.getContent().findComponentbyId("webformargs-txtSearch").getTag());
                    response.closeform(response.getContent());
                
                    response.setResult("webdatabase", result);                
                    response.write();
                
            }
        }); 
        h.addComponent(btn);
        nf.addComponent(h);
        
        
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            @Override
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                if(!response.getContent().findComponentbyId("webformargs-btnexec").getTag().equals("")){
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webformargs-lbldata").getTag());  
                    Nset act = Nset.readJSON( nset.getData("args").toString());
                    fillAction(response, response.getContent().findComponentbyId("webformargs-btnexec").getTag(),act);
                }
                
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webformargs-column");
                Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webformargs-lbldata").getTag());  
 
                String idform = nset.getData("formid").toString();
                String formname = nset.getData("formname").toString();
                
                /*
                Nikitaset nikitaset ;
                if (formname.equals("")) {
                    nikitaset = nikitaConnection.Query("SELECT formid,formname,formstyle FROM web_form where formid= ? ",idform);
                }else{
                    nikitaset = nikitaConnection.Query("SELECT formid,formname,formstyle FROM web_form where formname= ? ",formname);
                    if (nikitaset.getRows()==0) {
                        nikitaset = nikitaConnection.Query("SELECT formid,formname,formstyle FROM web_form where formid= ? ",idform);
                    }
                }
                */
                /*
                update 16/01/2021 tidak mennggunakan idform
                */
                Nikitaset nikitaset = nikitaConnection.Query("SELECT formid,formname,formstyle FROM web_form where formname= ? ",formname);
                response.getContent().findComponentbyId("webformargs-txtSearch").setText(formname);     
                
               //response.getContent().findComponentbyId("webformargs-txtSearch").setText(nikitaset.getText(0, 1));
                response.getContent().findComponentbyId("webformargs-lblidform").setTag(nikitaset.getText(0, 0));        
                response.getContent().findComponentbyId("webformargs-lblparam").setTag( nikitaset.getText(0, 2));
                
                fillAction(response, nikitaset.getText(0, 2), nset.getData("args"));
                    
            }
        }); 
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
                if(reqestcode.equals("fromlist") && responsecode.equals("webvariablelist")){   
                    response.getContent().findComponentbyId("webformargs-btnexec").setTag(result.getData("parameter").toString());
                    response.getContent().findComponentbyId("webformargs-lblidform").setTag(result.getData("formid").toString());
                    response.getContent().findComponentbyId("webformargs-txtSearch").setText(result.getData("variable").toString());
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webformargs-lbldata").getTag());  
                    Nset act = Nset.readJSON( nset.getData("args").toString());
                    fillAction(response, result.getData("parameter").toString(),act);
                }
                
                if(reqestcode.equals("")  && responsecode.equals("webvariablelist")){
 
                    response.getContent().findComponentbyId(result.getData("paramid").toString()).setText(result.getData("variable").toString());
                    
                    Nset nset = Nset.readJSON(response.getContent().findComponentbyId("webformargs-lbldata").getTag());  
                    Nset act = Nset.readJSON( nset.getData("args").toString());
                    fillAction(response,  response.getContent().findComponentbyId("webformargs-lblparam").getTag() .toString(),act);
                }
                
                
                
                    response.writeContent();
            }
        });
        
        
        
        response.setContent(nf);
    }
    
    
    
    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if(component.startsWith("webformargs-gen-") ){
            
            
            request.setParameter("search", response.getContent().findComponentbyId(component).getText());
            request.setParameter("paramid", component);       
            
            request.setParameter("idform", response.getContent().findComponentbyId("webformargs-lblcurridform").getTag());  
            response.showform("webvariablelist", request);
        }else{
            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }
    }    
    
    public void fillAction(NikitaResponse response, String parameter, Nset buffered) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webformargs-column");
        Nset n =  Nset.newArray();
        
       
        parameter = Utility.replace(parameter, "\r\n", ";");
        Vector<String> vv = Utility.splitVector(parameter, ";");
        for (int i = 0; i < vv.size(); i++) {
            if (vv.elementAt(i).trim().startsWith("n-arg")) {
                if (vv.elementAt(i).contains(":")) {
                    String s= vv.elementAt(i);
                    s=s.substring(s.indexOf(":")+1).trim();
                    n.addData(Nset.newObject().setData("id", s).setData("text", s));
                }
                
            }
        }
        
       
        Nikitaset nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null AND web_route.action LIKE '%\"class\":\"DefinitionAction\"%' ", response.getContent().findComponentbyId("webformargs-lblidform").getTag());
        for (int i = 0; i < nikiset.getRows(); i++) {            
                Nset nv = Nset.readJSON(nikiset.getText(i, 1));
                String[] keys = nv.getData("args").getObjectKeys();
                
                boolean arg = nv.getData("class").toString().equals("DefinitionAction") && nv.getData("code").toString().equals("arg");
                boolean res = nv.getData("class").toString().equals("DefinitionAction") && nv.getData("code").toString().equals("result");
                if ( arg ||res ) {
                    for (int j = 0; j < keys.length; j++) {
                        String s = nv.getData("args").getData(keys[j]).toString();
                        if (s.startsWith("@+")) {
                            s=s.substring(2);
                        }else if (s.startsWith("@")) {
                            s=s.substring(1);
                        }else if (s.startsWith("$#")) {
                            s=s.substring(2);
                        }else if (s.startsWith("$")) {
                            s=s.substring(1);
                        }else if (s.startsWith("&")) {
                            s=s.substring(1);
                        }
                        if (s.trim().equals("")) {
                        }else if ( !n.getInternalObject().toString().contains("\""+s+"\"") ) {
                            n.addData(Nset.newObject().setData("id",  res?("["+s+"]"):(s) ).setData("text", s).setData("direct", res?"result":"arg"));
                        }
                    }
               }

        }       
         
        n = Nset.newObject().setData("args", n);
        for (int i = 0; i < n.getData("args").getArraySize(); i++) {    
            Nset v = n.getData("args").getData(i);
            
            createGeneratorVariable(i, v, buffered.getData(v.getData("id").toString()).toString(),verticalLayout);
             
        }        
        for (int i = verticalLayout.getComponentCount()-1; i >= n.getData("args").getArraySize(); i--) {    
           verticalLayout.removeComponent(i);
        } 
        
    }
    
    private Component createGeneratorVariable(int i, Nset param, String bufferd,ComponentGroup verticalLayout){
        Textsmart txt = (Textsmart)verticalLayout.getComponent(i);
        txt.setId("webformargs-gen-"+i);
        txt.setVisible(true);
        txt.setEnable(true);
        if (param.getData("direct").toString().equals("result")) {
            txt.setLabel("["+param.getData("text").toString()+"]");
            txt.setStyle(new Style().setStyle("n-label-color", "blue"));
        }else{
            txt.setLabel(param.getData("text").toString());
        }
        
        txt.setTag(param.toJSON());
        if(!bufferd.equals("")) 
            txt.setText(bufferd);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                   
                
            }
        });         
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        return horisontalLayout;     
 
    }
    
    public Nset getAction(NikitaResponse response ) {
        ComponentGroup verticalLayout = (ComponentGroup)response.getContent().findComponentbyId("webformargs-column");
        Nset result =  Nset.newObject();
 
        for (int i = 0; i < verticalLayout.getComponentCount(); i++) {             
            if (!verticalLayout.getComponent(i).getTag() .equals("")) {

               result.setData(Nset.readJSON(verticalLayout.getComponent(i).getTag() ).getData("id").toString(), verticalLayout.getComponent(i).getText() );   
            }
           
        } 
        
        return result;
    }
    
}
