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
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.FileUploder;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.BorderLayout;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.FrameLayout;
import com.nikita.generator.ui.layout.GridLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;

/**
 *
 * @author user
 */
public class dashboardsipitem extends NikitaServlet{

    NikitaConnection nikitaConnection ;
    Nikitaset position;
    @Override
    public void OnCreate(NikitaRequest request,final NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        if (response.getVirtualString("@+SESSION-THEME").equals("")) {
            response.setVirtual("@+SESSION-THEME","south");
        }
        
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);
         
        NikitaForm nf = new NikitaForm(this);
        
        nf.setIcon("/static/img/apps/dashboard.png");
        
        Label lblid = new Label();
        lblid.setId("id");
        lblid.setText(request.getParameter("id"));
        lblid.setVisible(false);
        nf.addComponent(lblid);
        request.retainData(lblid);
        
        Nikitaset nst = nc.Query("SELECT * FROM dashboard WHERE dashboardid=? ", lblid.getText());
        nf.setText(nst.getText(0, "dtitle"));
        
        
        DivLayout top = new DivLayout();
        top.setStyle(new Style());
        top.setStyle(new Style().setStyle("width", "auto").setStyle("height", "120px").setStyle("overflow", "hidden"));
        top.getStyle().setStyle("top", "0px").setStyle("left", "0px");
        top.getStyle().setStyle("right", "0px");
        top.getStyle().setStyle("position", "absolute");       
        
            Image mobapp =new Image();
            mobapp.setText(nst.getText(0, "dicon"));
            mobapp.setStyle(new Style());
            mobapp.getStyle().setStyle("width", "100px");
            mobapp.getStyle().setStyle("height", "100px");
            mobapp.getStyle().setStyle("margin-left", "48px");           
            mobapp.getStyle().setStyle("margin-top", "10px");
            top.addComponent(mobapp);
        Label lbl = new Label();
        lbl.setText(nst.getText(0, "dtitle"));
        lbl.setStyle(new Style());
        lbl.getStyle().setStyle("position", "absolute");    
        lbl.getStyle().setStyle("font-size", "20px");
        lbl.getStyle().setStyle("font-family", "Open Sans");
        lbl.getStyle().setStyle("top", "10px");
        lbl.getStyle().setStyle("left", "160px");
        lbl.getStyle().setStyle("overflow", "hidden");
        top.addComponent(lbl);
        
        
            lbl = new Label();
            lbl.setText(nst.getText(0, "ddescribe"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("position", "absolute"); 
            lbl.getStyle().setStyle("height", "62px");
            lbl.getStyle().setStyle("font-size", "12px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("top", "40px");
            lbl.getStyle().setStyle("left", "160px");
            lbl.getStyle().setStyle("overflow", "hidden"); 
            lbl.getStyle().setStyle("line-height", "17px"); 
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
        //top.addComponent(topsearch);
        
        request.retainData(txt);
        
     
        
        
        nf.addComponent(top);
        
        DivLayout h = new DivLayout();
        h.setStyle(new Style());
        h.setStyle(new Style().setStyle("width", "auto").setStyle("height", "auto").setStyle("overflow-x", "scroll"));
        h.getStyle().setStyle("top", "120px").setStyle("left", "0px");
        h.getStyle().setStyle("right", "0px").setStyle("bottom", "0px");
        h.getStyle().setStyle("position", "absolute");
        //h.getStyle().setStyle("background-image", "url(static/img/apps/bg.jpg)");
        h.getStyle().setStyle("padding", "5px");
        
        
        
        
        GridLayout apps = new GridLayout();
        apps.setStyle(new Style());
        apps.setStyle(new Style().setStyle("width", "auto").setStyle("height", "100%"));
        apps.getStyle().setStyle("position", "relative");
        apps.getStyle().setStyle("margin", "0px");
         
 
       
        String like ="%"+txt.getText()+"%";
        Nikitaset ns = nc.Query("SELECT * FROM dashboard WHERE dname LIKE ? OR dtitle LIKE ? OR ddescribe LIKE ?  OR ddetail LIKE ?   ;", like, like, like, like);
        
        Vector<String> v = Utility.splitVector(nst.getText(0, "dimages"), ";");
        if (nst.getText(0, "dimages").length()>=3) {
            apps.getStyle().setStyle("n-cols", v.size()+"");        
            for (int i = 0; i < v.size(); i++) {
                DivLayout app = new DivLayout();
                app.setStyle(new Style().setStyle("width", "214px").setStyle("height", "214px"));
                app.getStyle().setStyle("position", "relative");
                app.getStyle().setStyle("background-color", "#fafafa");
                app.getStyle().setStyle("margin", "10px");
                 
                Image icon =new Image();
                icon.setText(v.elementAt(i));
                icon.setStyle(new Style());
                icon.getStyle().setStyle("width", "214px");
                icon.getStyle().setStyle("height", "214px");
                icon.getStyle().setStyle("margin", "0px");

                app.addComponent(icon);


                apps.addComponent(app);            
            }
        }        
        h.addComponent(apps);
        
        
        if (nst.getText(0, "ddetail").length()>=3) {
            lbl = new Label();
            lbl.setText(nst.getText(0, "ddetail"));
            lbl.setStyle(new Style());
            lbl.getStyle().setStyle("font-size", "14px");
            lbl.getStyle().setStyle("font-family", "Open Sans");
            lbl.getStyle().setStyle("margin", "10px");
                  
            h.addComponent(lbl);
        }
        
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
