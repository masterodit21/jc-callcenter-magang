/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

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
public class mdownloadtable extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //super.OnRun(request, response, logic);
        
        NikitaConnection nc = response.getConnection(NikitaConnection.DEFAULT);  
        Nikitaset ns = nc.Query(  "SELECT * FROM " + request.getParameter("table")  );    
        
        response.writeStream(ns.toNset().toJSON());
    }
    
    
}
