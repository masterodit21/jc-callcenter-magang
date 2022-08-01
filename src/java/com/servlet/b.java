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
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class b extends home{
    /*
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        super.OnCreate(request, response, logic);

        NikitaForm nf  = response.getContent() ;
        nf.setId("home");
        nf.setOnReadyListener(new NikitaForm.OnReadyListener() {
            public void OnReady(NikitaRequest request, NikitaResponse response, Component component) {
                String s = response.getVirtualString("@+PATHINFO");
                if (s.startsWith("/static/")||s.startsWith("/res/")) {
                    s="";
                }else if (s.startsWith("/b/")) {
                    s=s.substring(3);
                  }
                
                if (s.contains("/")) {
                    String[] forms = Utility.split(s, "/");
                    for (int i = 0; i < forms.length; i++) {
                        if (!forms[i].trim().equals("")) {
                            response.showformGen(forms[i].trim(), request, "", false);
                        }
                    }
                }else if (!s.trim().equals("")) {
                    response.showformGen(s, request, "", false);
                }
                
            }
        });
        response.setContent(nf);
    }
    */
}
