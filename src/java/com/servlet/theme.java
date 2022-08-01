/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author rkrzmail
 */
public class theme extends NikitaServlet{
    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
     public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Action");                       
        nf.setStyle(new Style().setStyle("width", "400").setStyle("height", "140"));
        
         
        Combobox combo = new Combobox();
        combo.setId("webtheme-combo");
        combo.setLabel("Theme");
        combo.setData(Nset.readJSON("[{'id':'lightness','text':'UI Lightness'},{'id':'black','text':'Black UI'},{'id':'darkness','text':'darkness'},{'id':'flick','text':'flick UI'},{'id':'humanity','text':'humanity UI'},{'id':'overcast','text':'overcast UI'},{'id':'redmond','text':'redmond UI'},{'id':'smoothness','text':'smoothness UI'},{'id':'south','text':'south UI'},{'id':'nflat','text':'Flat UI'},{'id':'cupertino','text':'cupertino UI'},{'id':'start','text':'start UI'}]",true));
        
        
      
        nf.addComponent(combo);
        
        Button btn = new Button();
        btn.setId("webtheme-change");
        btn.setText("Change");
        Style style = new Style();
        style.setStyle("width", "156px");
        style.setStyle("height", "40px");
        style.setStyle("margin-left", "200px");
        btn.setStyle(style);          
        nf.addComponent(btn);
        
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    response.setVirtual("@+SESSION-THEME", response.getContent().findComponentbyId("webtheme-combo").getText()) ;
                    response.setVirtual("@+COOKIE-THEME", response.getContent().findComponentbyId("webtheme-combo").getText()) ;
                    
                    nikitaConnection.Query("UPDATE sys_user SET theme=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+" WHERE username=? ", response.getContent().findComponentbyId("webtheme-combo").getText(),user,user);
                     
                    
                    response.openWindows("home", "");
                    response.write();
            }
        });      
        
           
           
        response.setContent(nf);
         
        
    }
   
}
