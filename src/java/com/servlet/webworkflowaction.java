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
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 *
 * @author rkrzmail
 */
public class webworkflowaction extends NikitaServlet{

    Label lbldata ;
    Label lblargs ;
  
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Workflow Action" );
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "300");
        nf.setStyle(style);
        
          
        lbldata = new Label();
        lbldata.setId("lblwfdata");
        lbldata.setLabel("");  
        lbldata.setVisible(false);
        lbldata.setText(request.getParameter("wfdata"));
        nf.addComponent(lbldata);
        
        request.retainData(lbldata);
        
        lblargs = new Label();
        lblargs.setId("lblwfargs");
        lblargs.setLabel("");  
        lblargs.setVisible(false);
        lblargs.setText(request.getParameter("wfargs"));
        nf.addComponent(lblargs);
        
        request.retainData(lblargs);
        
        Nset n = Nset.readJSON(lbldata.getText());
        Nset v = Nset.readJSON(lblargs.getText());
        nf.setText(v.getData("type").toString() + " : " + v.getData("text").toString());
        
         
        
        
        response.setContent(nf);
         
    }
    
}
