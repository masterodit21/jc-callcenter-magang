/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;

/**
 *
 * @author rkrzmail
 */
public class compile extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        if (request.getParameter("clean").equals("true")) {
            NikitaEngine.clean(request.getParameter("formname"));
            response.writeStream("OK");
        }else if (request.getParameter("clean").equals("all")) {
            NikitaEngine.cleanAll();
            response.writeStream("OK");
        }else if (request.getParameter("prefix").equals("true")) {
            Nikitaset ns = response.getConnection(NikitaConnection.LOGIC).Query("SELECT formname FROM web_form WHERE formname LIKE '"+request.getParameter("formname")+"%' " );
            for (int i = 0; i < ns.getRows(); i++) {
                NikitaEngine.compile(response.getConnection(NikitaConnection.LOGIC), ns.getText(i, 0) );  
                System.out.println( ns.getText(i, 0) +" : "+ (i+1) +" of "+ns.getRows());
            }
            response.writeStream("OK"+ns.getRows());
        }else{
            NikitaEngine.compile(response.getConnection(NikitaConnection.LOGIC), request.getParameter("formname"));
            response.writeStream("OK");
        }
        
    }
    
}
