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
import com.nikita.generator.ui.layout.NikitaForm;

/**
 *
 * @author rkrzmail
 */
public class webflow  extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Logic Flow");        
        nf.setStyle(new Style().setStyle("width", "1124").setStyle("height", "650"));
        
        
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\""+Component.getBaseUrl("/base/static/lib/flow/jquery.jsPlumb-1.6.4.js")+"\"  ></script>");
        sb.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\""+Component.getBaseUrl("/base/static/lib/flow/demo.js")+"\"  ></script>");
        sb.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\""+Component.getBaseUrl("/base/static/lib/flow/jsplumb-persistence-plugin.js")+"\"  ></script>");
        
        
        nf.getStyle().setAttr("n-html-", sb.toString());
        
        
        
        
        
        
        
        
        
        
        response.setContent(nf);
    }
    
}
