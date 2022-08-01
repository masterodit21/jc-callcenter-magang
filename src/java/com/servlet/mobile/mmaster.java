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
import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class mmaster extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //mobileform, mobilecomponent, mobilelogic, mobilemodule, mobileparam, mobileasset
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);  
        Nikitaset ns = nc.Query( "SELECT * FROM web_route");   
        
        
                    
        System.err.println(ns.toNset().toJSON().length());
        System.err.println(Utility.toZip( ns.toNset().toJSON()) .length());
        
        super.OnRun(request, response, logic);
    }
    
}
