/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Hashtable;
import java.util.Vector;
 
/**
 *
 * @author rkrzmail
 */
public class weblinkservice extends NikitaServlet{
      /*parameter
    search
    mode
    data
    */
    
     
    NikitaConnection nikitaConnection;
    int dbCore;
    String user ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this)  ;
        nf.setText("Link Query Service");   
        nf.setStyle(new Style().setStyle("width", "540").setStyle("height", "650"));
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Label label = new Label();
        label.setId("serv-lblsender");
        label.setVisible(false);
        label.setText(request.getParameter("search"));
        label.setTag(request.getParameter("paramid"));
        nf.addComponent(label);
        Nset vArgs = Nset.readJSON(request.getParameter("search"));//first
        
        
        
        
        
        
        
        final Label lblidform = new Label();
        lblidform.setId("serv-lblidform2");
        lblidform.setTag(request.getParameter("idform"));
        lblidform.setVisible(false);
        request.retainData(lblidform);
        nf.addComponent(lblidform); 
        
        final Label lblidcomp = new Label();
        lblidcomp.setId("serv-lblidcomp2");
        lblidcomp.setTag(request.getParameter("idcomp"));
        lblidcomp.setVisible(false);
        request.retainData(lblidcomp);
        nf.addComponent(lblidcomp);
        
        
        
        
        Textsmart txt = new Textsmart();
        txt.setId("serv-Url");
        txt.setLabel("Base Url Server");
        txt.setText(vArgs.getData("baseurl").toString());
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));        
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    request.setParameter("search", component.getText());
                    request.setParameter("paramid", component.getId());            
                    request.setParameter("idcomp", response.getContent().findComponentbyId("serv-lblidcomp2").getTag()); 
                     request.setParameter("idform", response.getContent().findComponentbyId("serv-lblidform2").getTag());

                    response.showform("webvariablelist", request, "serv-Url", true);
                
            }
        }); 
        nf.addComponent(txt);
        
        
        txt = new Textsmart();
        txt.setId("serv-LinkName");
        txt.setLabel("Link Service");
        txt.setText(vArgs.getData("service").toString());
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", component.getText());
                request.setParameter("paramid", component.getId());            
                request.setParameter("idcomp", response.getContent().findComponentbyId("serv-lblidcomp2").getTag()); 
                request.setParameter("idform", response.getContent().findComponentbyId("serv-lblidform2").getTag());
                
                
                 response.showform("webvariablelist", request, "serv-LinkName", true);
            }
        }); 
        nf.addComponent(txt);
        
     
        
        
        Textarea area = new Textarea();
        area.setId("serv-varea");
        area.setLabel("Args Add");
        area.setText(vArgs.getData("argsadd").toString());
        nf.addComponent(area);
             
        
        
        Button button = new Button();
        button.setId("serv-tblView");
        button.setLabel("");
        button.setText("View Arguments");
        nf.addComponent(button);
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                String url =response.getVirtualString(response.getContent().findComponentbyId("serv-Url").getText())  + response.getVirtualString(response.getContent().findComponentbyId("serv-LinkName").getText()) ;
                if (url.endsWith("/")) {
                    url= url +"res/argument";
                }else{
                    url= url +"/res/argument";
                }
                        
                
                
                Nset nset = NikitaInternet.getNset( NikitaInternet.getHttp(url) ) ;
                Nset vArray = nset.getData("args");
                if (vArray.isNsetArray()) {    
                    String addArg = response.getComponent("$#serv-varea").getText().trim();
                    if (addArg.length()>=1) {
                        Nset n = Nset.readsplitString(addArg, ",");
                        for (int i = 0; i < n.getSize(); i++) {
                            if (vArray.containsValue(n.getData(i).toString())) {                                
                            }else{
                                vArray.addData(n.getData(i).toString());
                            }
                        }
                    }
                }else{
                    response.showDialog("Peringatan", nset.toJSON(), "Error", "OK");   
                    
                    vArray = Nset.newArray();
                    String addArg = response.getComponent("$#serv-varea").getText().trim();
                    if (addArg.length()>=1) {
                        Nset n = Nset.readsplitString(addArg, ",");
                        for (int i = 0; i < n.getSize(); i++) {
                            vArray.addData(n.getData(i).toString());
                        }
                    }
                }
                
                    
                            
                    
                    Nset buffer = Nset.newObject();
                    VerticalLayout layout = (VerticalLayout)response.getComponent("$#serv-layout");
                    for (int i = 0; i < layout.getComponentCount(); i++) {
                        Component component2 = layout.getComponent(i);                        
                        buffer.setData(component2.getLabel(), component2.getText());
                    }
                    
                    layout.removeAllComponents();
                    layout.setVisible(true);
                    for (int i = 0; i < vArray.getSize(); i++) {
                        if (vArray.getData(i).toString().equals("")) {                            
                        }else{
                            Textsmart txt = new Textsmart();
                            txt.setId("serv-arg-"+i);
                            txt.setLabel(vArray.getData(i).toString());
                            txt.setTag(  vArray.getData(i).toString());
                            txt.setText(buffer.getData(vArray.getData(i).toString()).toString());


                            txt.setVisible(true);
                            txt.setEnable(true);

                          
                            txt.setOnClickListener(new Component.OnClickListener() {
                                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                                }
                            });
                            layout.addComponent(txt);
                        }
                    }
                    response.refreshComponent(layout);

                    Component component1 =  response.getComponent("$#serv-argsAll");
                    component1.setText(populateArgs(response).toJSON());
                    response.refreshComponent(component1);
                    
                    
                    response.write();
               
            }
        });
        
        
        //buffer
        txt = new Textsmart();
        txt.setId("serv-argsAll");
        txt.setVisible(false);
        txt.setText(vArgs.getData("args").toJSON());//[[key,value]]       
        nf.addComponent(txt);
       
        request.retainData(txt);
        Nset nsonArgs = Nset.readJSON(txt.getText());
        
        
        
        VerticalLayout layout = new VerticalLayout();
        
        
        
        layout.setId("serv-layout");
        layout.setVisible(true);
        for (int i = 0; i < nsonArgs.getSize(); i++) {
            txt = new Textsmart();
            txt.setId("serv-arg-"+i);
            txt.setVisible(true);
            txt.setEnable(true);
            
            txt.setLabel(nsonArgs.getData(i).getData(0).toString());
            txt.setTag(  nsonArgs.getData(i).getData(0).toString());
            txt.setText( nsonArgs.getData(i).getData(1).toString());
            
     
            
            txt.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                }
            });
            layout.addComponent(txt);
        }
        request.retainData(layout);
        nf.addComponent(layout);
        
        /*dummy*/
        txt = new Textsmart();
        txt.setId("serv-arg-");
        txt.setVisible(false);
            txt.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                System.out.println("/* . . . . .. . . . . .*/");
                System.out.println("component:"+component);
                System.out.println("action:OnClick:");
             
                request.setParameter("search", component.getText());
                response.showform("webvariablelist", request, "argument"+component.getTag(), true);
            }
        });
        layout.addComponent(txt);
                    
        
        button = new Button();
        button.setId("serv-tblSave");
        button.setText("Save");        
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset ndata = Nset.newObject();
                ndata.setData("baseurl", response.getContent().findComponentbyId("serv-Url").getText());
                ndata.setData("service", response.getContent().findComponentbyId("serv-LinkName").getText());
                ndata.setData("argsadd", response.getContent().findComponentbyId("serv-varea").getText());
                
                 /*
                Nset args = Nset.newArray();
                VerticalLayout layout = ((VerticalLayout)response.getContent().findComponentbyId("serv-layout"));
                for (int i = 0; i < layout.getComponentCount(); i++) {
                    Component component1 = layout.getComponent(i);
                    //Hashtable<String, String> argspairdata = new Hashtable<>();
                    //argspairdata.put(component1.getTag(), component1.getText());
                    
                    if (component1.getTag().trim().equals("")) {                        
                    }else{
                        Vector<String> v = new Vector<String>();//[key,value]
                        v.addElement(component1.getTag());
                        v.addElement(component1.getText());

                        args.addData(v);
                    }                         
                    
                }
                */
                ndata.setData("args", populateArgs(response));//all     
                
                Nset result = Nset.newObject();
                result.setData("variable", ndata.toJSON()); 
                result.setData("paramid",response.getContent().findComponentbyId("serv-lblsender").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);  
                response.write();
            }
        });
        nf.addComponent(button);
        
        
         
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if (reqestcode.startsWith("argument") && responsecode.equalsIgnoreCase("webvariablelist")) {
                    String vID = reqestcode.substring(8);
                    Component component1 = response.getComponent("$"+vID);
                    component1.setText(result.getData("variable").toString());
                    response.refreshComponent(component1);
                    response.write();
                } else if (reqestcode.equalsIgnoreCase("serv-LinkName") && responsecode.equalsIgnoreCase("webvariablelist"))  {
                    Component component1 = response.getComponent("$#"+reqestcode);
                    component1.setText(result.getData("variable").toString());
                    response.refreshComponent(component1);
                    response.write();
                } else if (reqestcode.equalsIgnoreCase("serv-Url") && responsecode.equalsIgnoreCase("webvariablelist"))  {
                    Component component1 = response.getComponent("$#"+reqestcode);
                    component1.setText(result.getData("variable").toString());
                    response.refreshComponent(component1);
                    response.write();
                }else if (reqestcode.startsWith("serv-arg-") && responsecode.equalsIgnoreCase("webvariablelist")) {
                    Component component1 = response.getComponent("$#"+reqestcode);
                    component1.setText(result.getData("variable").toString());
                    response.refreshComponent(component1);
                    response.write();
                }
            }
        });
        
         
        
        response.setContent(nf);            
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                 
            }
        });
    }

    public Nset populateArgs(NikitaResponse response){
        Nset args = Nset.newArray();
        VerticalLayout layout = ((VerticalLayout)response.getContent().findComponentbyId("serv-layout"));
        for (int i = 0; i < layout.getComponentCount(); i++) {
            Component component1 = layout.getComponent(i);
            //Hashtable<String, String> argspairdata = new Hashtable<>();
            //argspairdata.put(component1.getTag(), component1.getText());

            if (component1.getLabel().trim().equals("")) {                        
            }else{
                Vector<String> v = new Vector<String>();//[key,value]
                v.addElement(component1.getLabel());
                v.addElement(component1.getText());

                args.addData(v);
            }                         

        }
        return  args;
    }
    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if (component.startsWith("serv-arg-")&& response.getContent()!=null) {
            Component component1 = response.getContent().findComponentbyId(component);
                request.setParameter("search", component1.getText());
                request.setParameter("paramid", component1.getId());            
                request.setParameter("idcomp", response.getContent().findComponentbyId("serv-lblidcomp2").getTag()); 
                request.setParameter("idform", response.getContent().findComponentbyId("serv-lblidform2").getTag());

                response.showform("webvariablelist", request, component1.getId(), true);
        }else{
            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }    
    }

    
     
    
}
