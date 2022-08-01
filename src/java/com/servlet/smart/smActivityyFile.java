/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.smart;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;

/**
 *
 * @author rkrzmail
 */
public class smActivityyFile  extends NikitaServlet{     
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        response.handleFileMultipart(request);
    }
    
    
}
