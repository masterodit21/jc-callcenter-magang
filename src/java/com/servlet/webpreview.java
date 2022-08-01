/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servlet;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class webpreview extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        final String text = request.getParameter("preview");
        NikitaForm nf = new NikitaForm(this);  
        nf.setText("Preview");  
        
        Label label = new Label(){
              public String getView() {
                  
                  return Utility.replace(text, "\r\n", "<br>");  
              }
            
        };
        label.setVisible(true);
        
      
        nf.addComponent(label);
    }
    
    
}
