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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.NikitaForm;

/**
 *
 * @author rkrzmail
 */
public class webpublish extends NikitaServlet{
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Publish Project");
        
        Textsmart textsmart =new Textsmart();        
        textsmart.setId("url");
        textsmart.setLabel("URL Server Publish");
        textsmart.setStyle(new Style().setStyle("n-label-width", "180px").setStyle("n-width", "320px"));
        nf.addComponent(textsmart);
        
        
        Button
        button = new Button();
        button.setId("bublish");
        button.setText("Publish");
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
            }
        });
        
        
        nf.addComponent(button);
        response.setContent(nf);
    }
    
    
}
