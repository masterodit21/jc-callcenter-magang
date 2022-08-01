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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
 

/**
 *
 * @author rkrzmail
 */
public class webbrowser  extends NikitaServlet{
    boolean findsetting = false;
    Label fid ;Label fname ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        final NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Browser");
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
         
        Style style = new Style(); 
        nf.setStyle(style);
        style.setStyle("overflow", "hidden");
        style.setStyle("width", "1032").setStyle("height", "600");
         
        
         //add 13K
        fid = new Label();
        fid.setId("fid");
        fid.setText(request.getParameter("fid"));
        fid.setVisible(false);
        nf.addComponent(fid);        
        
        
        fname = new Label();
        fname.setId("fid");
        fname.setText(request.getParameter("fname"));
        fname.setVisible(false);
        nf.addComponent(fname);   
              
        
        DivLayout divLayout = new DivLayout();
        
       
        
        Textsmart  textsmart = new Textsmart();
        textsmart.setId("url");
        style = new Style();
        style.setStyle("width", "100%");
        style.setStyle("n-table-width", "100%");
        style.setStyle("n-table-padding-right", "150px");
        style.setStyle("n-table-float", "left");     
        style.setStyle("n-table-position", "absolute");  
        textsmart.setEnable(false);
        textsmart.setText(request.getParameter("url"));
        
        textsmart.setStyle(style); 
        divLayout.addComponent(textsmart); 
        
        Button  button = new Button(){
            private String href="";
            public String getView(NikitaViewV3 v3) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("<a href=\""+href+"\" target=\"_blank\">");
                            sb.append(super.getView(v3));
                            sb.append("</a>");
                            return sb.toString(); 
                            
                        }
            public Button get(String href){
                this.href=href;
                return this;
            }
        }.get(request.getParameter("url"));
        button.setId("go");
        button.setText("NEW TAB");
        style = new Style();
        style.setStyle("float", "right");       
        button.setStyle(style);
        
        divLayout.addComponent(button); 
        
        nf.addComponent(divLayout); 
        
        
        Webview view = new Webview();
        view.setId("webview");
        view.setText(request.getParameter("url"));
        style = new Style();
        style.setStyle("position", "absolute");
        style.setStyle("top", "40px");
      
        style.setStyle("bottom", "0px"); 
        style.setStyle("left", "0px"); 
        style.setStyle("right", "0px"); 
        style.setStyle("width", "100%"); 
        style.setStyle("height", "90%"); 
        //style.setStyle("overflow", "auto"); 
        view.setStyle(style); 
        
       
        nf.addComponent(view); 
        response.setContent(nf);
               
    }
    
    
}
