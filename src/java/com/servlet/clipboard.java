/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class clipboard extends NikitaServlet{

    @Override
    public boolean isPrivateName() {
        return true; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Clipboard (Copy/Paste)");        
        
         Style style = new Style();
        style.setStyle("width", "400px");
        style.setStyle("height", "600");
        nf.setStyle(style);
        
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("clipboard-tablegrid");
        tablegrid.setDataHeader(Nset.readsplitString(""));
         // tablegrid.setRowCounter(true);
        tablegrid.setData(Nset.newArray().addData(Nset.newArray().addData("<a style=\"margin:3px;color:blue\" href=\"#\">{\"args\":{\"param3\":\"d45t5t5\",\"param2\":\"dfd\",\"param1\":\"sdsdsdssfed'\\\"=~1@\\\"\"},\"code\":\"&gt;=\",\"class\":\"string\",\"id\":\"1\"}</a><img src=\"static/img/delete.png\" style=\"width:30px;float:right;margin-top:25px;margin-right:3px;color:red\" href=\"#\" onclick=\"clearclipboard()\"></img>")));
         
        for (int i = 0; i < 3; i++) {
            tablegrid.setData(tablegrid.getData().addData(tablegrid.getData().getData(0).clone()));
            
        }
        
        style = new Style();
        style.setStyle("width", "380px"); 
 
        tablegrid.setColStyle(1, style);
        
        nf.addComponent(tablegrid);        
        
        //ngambil data lemparan dari form connectionadd
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                 
            }
        });
        
         
        
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("con-tblConn") ;
        
         
    }
}
