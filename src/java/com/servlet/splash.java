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
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class splash extends NikitaServlet{
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        if (response.getVirtualString("@+SESSION-THEME").equals("")) {
            response.setVirtual("@+SESSION-THEME","south");
        }
                
        NikitaForm nikitaForm = new NikitaForm(this);
        nikitaForm.setText("Welcome Nikita");
        nikitaForm.setIcon("/base/static/img/generator.png");
        nikitaForm.setStyle(new Style().setStyle("n-body-background-image", "url(static/img/sailormoon.jpg)"));
      

        /*
        nikitaForm.setOnReadyListener(new NikitaForm.OnReadyListener() {
            public void OnReady(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("login", request,"",false);
                response.write();
            }
        });
        */
        nikitaForm.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("login", request,"",false);
                //response.write();
                //response.showAlert("ss");
            }
        });     
        
        response.setContent(nikitaForm);
       
    }
}
