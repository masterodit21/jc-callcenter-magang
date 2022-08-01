/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.naa.data.Nson;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaControler;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Receiver;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class dashboardsip extends NikitaServlet{
    NikitaConnection nikitaConnection ;
    Nikitaset position;
    @Override
    public void OnCreate(NikitaRequest request,final NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        if (response.getVirtualString("@+SESSION-THEME").equals("")) {
            response.setVirtual("@+SESSION-THEME","south");
        }
        
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Dashboard");
        nf.setIcon("/static/img/apps/dashboard.png");
        
        
         
        Label 
        lbl = new Label();
        lbl.setId("dsip-fname");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("fname"));   
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("dsip-sdata");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("sdata"));   
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("dsip-sheader");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("sheader"));   
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        DivLayout top = new DivLayout();
        top.setStyle(new Style());
        top.setStyle(new Style().setStyle("width", "auto").setStyle("height", "120px").setStyle("overflow", "hidden"));
        top.getStyle().setStyle("top", "0px").setStyle("left", "0px");
        top.getStyle().setStyle("right", "0px");
        top.getStyle().setStyle("position", "absolute");       
        
            Image mobapp =new Image();
            mobapp.setText("/static/img/generator.png");
            mobapp.setStyle(new Style());
            mobapp.getStyle().setStyle("width", "100px");
            mobapp.getStyle().setStyle("height", "100px");
            mobapp.getStyle().setStyle("margin-left", "48px");           
            mobapp.getStyle().setStyle("margin-top", "10px");
            top.addComponent(mobapp);
        lbl = new Label();
        lbl.setText("Mobile Application");
        lbl.setStyle(new Style());
        lbl.getStyle().setStyle("position", "absolute");    
        lbl.getStyle().setStyle("font-size", "20px");
        lbl.getStyle().setStyle("font-family", "Open Sans");
        lbl.getStyle().setStyle("top", "40px");
        lbl.getStyle().setStyle("left", "160px");
        lbl.getStyle().setStyle("overflow", "hidden");
        top.addComponent(lbl);
        
        DivLayout topsearch = new DivLayout();
        Textsmart txt = new Textsmart();
            txt.setId("search");   
            txt.setHint("Search");
            txt.setStyle(new Style().setStyle("n-searchicon", "/static/img/apps/isearch.png"));
            txt.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                    response.writeContent();
               }
            });
              
        topsearch.addComponent(txt);
        topsearch.setStyle(new Style());
        topsearch.getStyle().setStyle("position", "absolute");    
        topsearch.getStyle().setStyle("top", "0px");
        topsearch.getStyle().setStyle("right", "0px");
        top.addComponent(topsearch);
        
        request.retainData(txt);
        
        Image ngen =new Image(){
                public String getView(NikitaViewV3 v3) {
                    return "<a href=\""+ response.getVirtualString("@+CONTEXT") +"/\" target=\"_blank\">"+super.getView(v3)+"</a>"; 
                }
            };
            ngen.setId("generator");      
            ngen.setText("/static/img/generator.png");
            ngen.setStyle(new Style());
            ngen.getStyle().setStyle("position", "absolute");    
            ngen.getStyle().setStyle("top", "60px");
            ngen.getStyle().setStyle("right", "10px");
            top.addComponent(ngen);
        
        
        //nf.addComponent(top);
        
        DivLayout h = new DivLayout();
        h.setStyle(new Style());
        h.setId("dsip-h");
        h.setStyle(new Style().setStyle("width", "auto").setStyle("height", "auto").setStyle("overflow-y", "scroll"));
        h.getStyle().setStyle("top", "0px").setStyle("left", "0px");
        h.getStyle().setStyle("right", "0px").setStyle("bottom", "0px");
        h.getStyle().setStyle("position", "absolute");
        h.getStyle().setStyle("background-image", "url(static/img/apps/bg.jpg)");
        h.getStyle().setStyle("padding-top", "5px");
        h.getStyle().setStyle("padding-left", "5px");
        h.getStyle().setStyle("padding-right", "5px");
        h.getStyle().setStyle("padding-bottom", "5px");
               
        DivLayout apps = new DivLayout();
        apps.setStyle(new Style());
        apps.setStyle(new Style().setStyle("width", "100%").setStyle("height", "120px").setStyle("overflow", "hidden"));
        apps.getStyle().setStyle("position", "relative");
        apps.getStyle().setStyle("margin-left", "auto");
        apps.getStyle().setStyle("margin-right", "auto");
     
        
        Nson nson = Nson.readJson(nf.findComponentbyId("dsip-sheader").getText());
 
        h.addComponent(apps);
        for (int i = 0; i < nson.size(); i++) {
            DivLayout 
            happ = new DivLayout();
            happ.setStyle(new Style().setStyle("width", "112px").setStyle("height", "120px"));
            happ.getStyle().setStyle("position", "relative");
   
            happ.getStyle().setStyle("float", "left");
            happ.getStyle().setStyle("margin", "5px");
            

            DivLayout app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "112px").setStyle("height", "30px"));
            app.getStyle().setStyle("background-color", "#00ff00");             
            app.getStyle().setStyle("border-radius", "20px");    
            app.getStyle().setStyle("vertical-align", "middle");    
            app.getStyle().setStyle("display", "table-cell");  
         
            lbl = new Label();
            lbl.setText(nson.get(i).get(0).asString());
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-left", "auto");
            lbl.getStyle().setStyle("margin-top", "0px");
            lbl.getStyle().setStyle("margin-right", "auto");           
            lbl.getStyle().setStyle("margin-bottom", "0px");
            lbl.getStyle().setStyle("text-align", "center");            
            lbl.getStyle().setStyle("overflow", "hidden");
            app.addComponent(lbl);
            happ.addComponent(app);
            
             app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "1px").setStyle("height", "1px"));  
            app.getStyle().setStyle("display", "table-row");  
            happ.addComponent(app);
            
            app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "112px").setStyle("height", "30px"));
            app.getStyle().setStyle("background-color", "#00ff00");
            app.getStyle().setStyle("margin-top", "3px");
            app.getStyle().setStyle("border-radius", "20px");      
            app.getStyle().setStyle("vertical-align", "middle");    
            app.getStyle().setStyle("display", "table-cell");            

            lbl = new Label();
            lbl.setText(nson.get(i).get(1).asString());
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-left", "auto");
            lbl.getStyle().setStyle("margin-right", "auto");  
            lbl.getStyle().setStyle("margin-top", "0px");
            lbl.getStyle().setStyle("margin-bottom", "0px");
            lbl.getStyle().setStyle("text-align", "center");            
            lbl.getStyle().setStyle("overflow", "hidden");
            app.addComponent(lbl); 
            happ.addComponent(app);
            
              app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "1px").setStyle("height", "1px"));  
            app.getStyle().setStyle("display", "table-row");  
            happ.addComponent(app);
            
            
            app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "112px").setStyle("height", "30px"));
            app.getStyle().setStyle("background-color", "#00ff00");
            app.getStyle().setStyle("margin-top", "3px");
            app.getStyle().setStyle("border-radius", "20px");       
            app.getStyle().setStyle("vertical-align", "middle");    
            app.getStyle().setStyle("display", "table-cell");              

            lbl = new Label();
            lbl.setText(nson.get(i).get(2).asString());
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-left", "auto");
            lbl.getStyle().setStyle("margin-right", "auto");
            lbl.getStyle().setStyle("margin-top", "0px");
            lbl.getStyle().setStyle("margin-bottom", "0px");
            lbl.getStyle().setStyle("text-align", "center");            
            lbl.getStyle().setStyle("overflow", "hidden");
            app.addComponent(lbl); 
            happ.addComponent(app);
            
            apps.addComponent(happ);                
        }
        h.addComponent(apps);
        nf.addComponent(h);
        
        
        //contnet   
        h = new DivLayout();
        h.setStyle(new Style());
         h.setId("dsip-c");
        h.setStyle(new Style().setStyle("width", "auto").setStyle("height", "auto").setStyle("overflow-y", "scroll"));
        h.getStyle().setStyle("top", "120px").setStyle("left", "0px");
        h.getStyle().setStyle("right", "0px").setStyle("bottom", "20px");
        h.getStyle().setStyle("position", "absolute");
        h.getStyle().setStyle("background-image", "url(static/img/apps/bg.jpg)");
        h.getStyle().setStyle("padding-top", "5px");
        h.getStyle().setStyle("padding-left", "5px");
        h.getStyle().setStyle("padding-right", "5px");
        h.getStyle().setStyle("padding-bottom", "5px");
        
        apps = new DivLayout();
        apps.setStyle(new Style());
        apps.setStyle(new Style().setStyle("width", "100%").setStyle("height", "auto"));
        apps.getStyle().setStyle("position", "relative");
        apps.getStyle().setStyle("margin-left", "auto");
        apps.getStyle().setStyle("margin-right", "auto");
        
        /*
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);
        String like ="%"+txt.getText()+"%";
        Nikitaset ns = nc.Query("SELECT * FROM dashboard WHERE dname LIKE ? OR dtitle LIKE ? OR ddescribe LIKE ?  OR ddetail LIKE ?   ;", like, like, like, like);
        */         
        Nikitaset  ns =  new Nikitaset(Nset.readJSON( (nf.findComponentbyId("dsip-sdata").getText())));
        
        
        for (int i = 0; i < ns.getRows(); i++) {
            DivLayout app = new DivLayout();
            app.setStyle(new Style().setStyle("width", "112px").setStyle("height", "138px"));
            app.getStyle().setStyle("position", "relative");
            app.getStyle().setStyle("background-color", "#ffffff");
            app.getStyle().setStyle("float", "left");
            app.getStyle().setStyle("margin", "5px");
            app.getStyle().setStyle("border-radius", "10px");            
             
            Image icon =new Image();
            if (ns.getText(i, "STATUS").equalsIgnoreCase("talk")) {
                icon.setText("/static/img/apps/calltalk.png");
            }else if (ns.getText(i, "status").equalsIgnoreCase("busy")) {
                icon.setText("/static/img/apps/callbusy.png");
            }else{
                icon.setText("/static/img/apps/callready.png");//default
            }
            icon.setTag(ns.getText(i, "USER"));   
            
            icon.setStyle(new Style());
            icon.setId("dsip-oclik"+i);              
            icon.getStyle().setStyle("n-div-width", "64px");
            icon.getStyle().setStyle("n-div-height", "64px");
            icon.getStyle().setStyle("width", "64px");
            icon.getStyle().setStyle("height", "64px");
            icon.getStyle().setStyle("margin-left", "24px");
            icon.getStyle().setStyle("margin-right", "24px");
            icon.getStyle().setStyle("margin-top", "5px");
            icon.getStyle().setStyle("margin-bottom", "5px");
            icon.getStyle().setStyle("text-align", "center"); 
            icon.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    String fname = response.getContent().findComponentbyId("dsip-fname").getText();
                    request.setParameter("id", component.getId());
                    request.setParameter("sdat", component.getTag());
                    response.showformGen(fname, request,"",true);
                    response.write();
                }
            });
            app.addComponent(icon);
            
              
            lbl = new Label();
            lbl.setId("dsip-cnama-"+i);
            lbl.setText(ns.getText(i, "NAMA"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-left", "10px");
            lbl.getStyle().setStyle("margin-right", "0px");
            lbl.getStyle().setStyle("margin-top", "10px"); 
            lbl.getStyle().setStyle("text-align", "center");            
            lbl.getStyle().setStyle("overflow", "hidden");
            app.addComponent(lbl);
            
            lbl = new Label();
            lbl.setId("dsip-cext-"+i);
            lbl.setText(ns.getText(i, "EXT"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
             lbl.getStyle().setStyle("margin-left", "10px");
            lbl.getStyle().setStyle("margin-right", "0px");
            lbl.getStyle().setStyle("margin-top", "2px"); 
            lbl.getStyle().setStyle("overflow", "hidden");         
            lbl.getStyle().setStyle("text-align", "center");    
            app.addComponent(lbl);
            
            
            
            lbl = new Label();
            lbl.setId("dsip-cst-"+i);
            lbl.setText(ns.getText(i, "STATUS_TIME"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "10px");
            lbl.getStyle().setStyle("font-style", "italic"); 
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-top", "2px"); 
            lbl.getStyle().setStyle("margin-left", "10px");
            lbl.getStyle().setStyle("margin-right", "0px");
            lbl.getStyle().setStyle("text-align", "center");    
            app.addComponent(lbl);
            
            
            lbl = new Label();
            lbl.setId("dsip-crem-"+i);
            lbl.setText(ns.getText(i, "REMARK"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "10px");
            lbl.getStyle().setStyle("font-style", "italic"); 
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin-top", "2px"); 
            lbl.getStyle().setStyle("margin-left", "10px");
            lbl.getStyle().setStyle("margin-right", "auto");
            lbl.getStyle().setStyle("text-align", "center");    
            app.addComponent(lbl);
            
            /*
            DivLayout download = new DivLayout();
            download.setStyle(new Style().setStyle("width", "180px").setStyle("height", "42px"));
            download.getStyle().setStyle("position", "relative");
            download.getStyle().setStyle("background-color", "#ffffff");
            download.getStyle().setStyle("float", "left");
            download.getStyle().setStyle("margin", "0px"); 
            download.getStyle().setStyle("padding-bottom", "2px"); 
            download.getStyle().setStyle("padding-left", "20px");
            app.addComponent(download);
            
           
            download.addComponent(getRun("chrome.png", ns.getText(i, "dweb")));
            download.addComponent(getRun("winphone.png", ns.getText(i, "dwinphone")));
            download.addComponent(getRun("android.png", ns.getText(i, "dandroid")));
            download.addComponent(getRun("apple.png", ns.getText(i, "dwios")));
            download.addComponent(getRun("bb.png", ns.getText(i, "dbb")));
      */
            
            apps.addComponent(app);            
        }
        h.addComponent(apps);
        
        
        lbl = new Label();
        lbl.setText("&#169; 2014 Nikita Generator at Indocyber Company");
        lbl.setStyle(new Style());
        lbl.getStyle().setStyle("position", "absolute");    
        lbl.getStyle().setStyle("font-size", "10px");
        lbl.getStyle().setStyle("font-family", "Open Sans");
        lbl.getStyle().setStyle("right", "5px");
        lbl.getStyle().setStyle("bottom", "5px");
        lbl.getStyle().setStyle("overflow", "hidden");
        // nf.addComponent(lbl);
        
         
          
        Receiver receiver= new Receiver(); 
        receiver.setId("dsip-rx");
        receiver.setText("wsmonitor");    
        receiver.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { 
                //response.refreshComponent(response.getContent().findComponentbyId("dsip-h"));
                //response.refreshComponent(response.getContent().findComponentbyId("dsip-c"));
                //response.write();
            }
        });
        //nf.addComponent(receiver);        
               
        
        nf.addComponent(h);
        response.setContent(nf);
    }
    
    private Image getRun(String icon, String link){
        Image chrome =new Image(){
                private String link;
                public Image get(String link){
                    this.link=link;
                    return this;
                }
                public String getView(NikitaViewV3 v3) {
                    if (link.trim().length()>=3) {
                        return "<a href=\""+link+"\" target=\"_blank\">"+super.getView(v3)+"</a>"; 
                    }else{
                        return super.getView(v3); 
                    }
                }
            }.get(link);
            chrome.setText("/static/img/apps/"+icon);
            chrome.setStyle(new Style());            
            chrome.getStyle().setStyle("margin-left", "2px");
            chrome.getStyle().setStyle("margin-right", "2px");
            chrome.getStyle().setStyle("float", "left");
            chrome.getStyle().setAttr("href", "nikita");
            chrome.getStyle().setStyle("opacity", link.trim().length()>=3?"1":"0.1");
        return chrome;
    }
    
}
