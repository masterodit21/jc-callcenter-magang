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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.ui.layout.NikitaForm;

/**
 *
 * @author rkrzmail
 */
public class base extends NikitaServlet{
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Nikita Generator");
        nf.setOnReadyListener(new NikitaForm.OnReadyListener() {
            public void OnReady(NikitaRequest request, NikitaResponse response, Component component) {
                String s = response.getVirtualString("@+PATHINFO");
                if (s.startsWith("/base/")) {
                    s=s.substring(6);
                }
                if (s.indexOf("/")>=0) {
                    s=s.substring(0, s.indexOf("/"));
                }
                response.showformGen(s, request, "", false);
            }
        });
        response.setContent(nf);
    }
    
}
