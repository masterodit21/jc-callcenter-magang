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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.DivLayout;
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
public class webchat extends NikitaServlet{
    Label idtype ;
    String priority;
    NikitaConnection nikitaConnection ;
    String user ;
    int dbCore;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Mobile Chat");
        nf.setStyle(new Style().setStyle("width", "320").setStyle("height", "420").setStyle("n-maximizable", "false"));
        
        Style style = new Style();
        style.setStyle("width", "100%");
        style.setStyle("height", "320px");
        style.setStyle("overflow-y", "scroll");
        style.setStyle("border-style", "solid");
        style.setStyle("border-color", "#7f7f7f");
        style.setStyle("border-width", "1px");
        style.setStyle("padding", "3px");
        style.addClass( "nikitareceiver");
        style.setAttr("receiver", "nikitachatreceiver");
        
        DivLayout divl = new DivLayout();
        divl.setId("webchat-div1");
        divl.setStyle(style);
        nf.addComponent(divl);
        
        divl = new DivLayout();
        divl.setId("message");
        divl.setText("<input type=\"text\" style=\"margin-top:10px;width:100%\" placeholder=\"type and press enter to chat\" id=\"chat\"  onkeydown=\"sendchat(event,this)\">");
        divl.setStyle(new Style().setStyle("width", "100%"));
        nf.addComponent(divl);
        
        response.setContent(nf);
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                
            }
        });
    }
    
    
}
