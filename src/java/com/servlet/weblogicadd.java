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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author user
 */
public class weblogicadd extends NikitaServlet{

    String user ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Add Logic");
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "220");
        nf.setStyle(style);
        
        Textbox txt = new Textbox();
        txt.setId("logicadd-txtcompname");
        txt.setLabel("Comp Name");
        txt.setEnable(false);
        txt.setText(request.getParameter("compname"));
        txt.setTag(request.getParameter("compid"));
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("logicadd-txtrouteindex");
        txt.setLabel("Route Index");
        txt.setText("1");
        txt.setVisible(false);
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("logicadd-txtexpression");
        txt.setLabel("Expression");
        nf.addComponent(txt);
        
        txt = new Textbox();
        txt.setId("logicadd-txtaction");
        txt.setLabel("Action");
        nf.addComponent(txt);        
              
        
        Button btn = new Button();
        btn.setId("logicadd-btnsave");
        btn.setText("Save");
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {               
                Nikitaset nikitaset = nikitaConnection.Query("insert into web_route("+
                                      "compid, routeindex, action, expression,createdby,createddate)"+
                                      "values(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",
                                      response.getContent().findComponentbyId("logicadd-txtcompname").getTag(),
                                      response.getContent().findComponentbyId("logicadd-txtrouteindex").getText(),
                                      response.getContent().findComponentbyId("logicadd-txtexpression").getText(),
                                      response.getContent().findComponentbyId("logicadd-txtaction").getText(),user);
                if(nikitaset.getError().length() > 1)
                    response.showAlert(nikitaset.getError());                    
                else{
                    response.closeform(response.getContent());
                    response.setResult("OK",Nset.newObject() );
                }
                    
               
                response.write();
        
            }
        });
        
        response.setContent(nf);
    }
    
    
}
