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
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;

import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class webmessage extends NikitaServlet{

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
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Message");   
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        HorizontalLayout h = new HorizontalLayout();
        VerticalLayout v = new VerticalLayout();
        v.setText("Message");
        
        Accordion accordion = new Accordion();
        accordion.setId("acc1");
        accordion.setStyle(new Style().setStyle("width", "200px"));
        accordion.setActive(0);
        
        DivLayout 
        mcontent = new DivLayout();
        mcontent.setId("master-content");
        DivLayout content = new DivLayout();
        content.setId("home-content");
        mcontent.addComponent(content);
        HorizontalLayout conH = new HorizontalLayout();
        
        Button btn = new Button();
        btn.setId("msg-compose");
        btn.setText("Compose");
        btn.setStyle(new Style().setStyle("width", "100%"));
        v.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
//                response.openWindows("webcomposemsg", "home-content", request);
                response.showformGen("webmessagecompose", request, "", false, ""+System.currentTimeMillis() );
                response.write();
            }
        });
        
        btn = new Button();
        btn.setId("msg-inbox");
        btn.setText("Inbox");
        btn.setStyle(new Style().setStyle("width", "100%"));
        v.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.openWindows("webmessageinbox", "home-content", request);
                response.write();
            }
        });
        
        
        btn = new Button();
        btn.setId("msg-outbox");
        btn.setText("Outbox");
        btn.setStyle(new Style().setStyle("width", "100%"));
        v.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.openWindows("webmessageoutbox", "home-content", request);
                response.write();
            }
        });
        
        accordion.addComponent(v);
        
        
        conH.addComponent(accordion);
        accordion.setStyle(new Style().setStyle("width", "200px").setStyle("n-layout-vertical-align", "top"));
        
        conH.addComponent(mcontent);
        mcontent.setStyle(new Style().setStyle("n-layout-width", "100%").setStyle("n-layout-vertical-align", "top").setStyle("position", "absolute").setStyle("right", "0px").setStyle("left", "230px"));
        
        
        
        conH.setStyle(new Style().setStyle("overflow", "auto"));        
        nf.addComponent(conH);
     
        
        
        
//        h.addComponent(accordion);
//        nf.addComponent(h);
        response.setContent(nf);    
    
    }
    
    
}
