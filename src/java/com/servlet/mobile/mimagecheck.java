/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nset;
import java.io.File;

/**
 *
 * @author rkrzmail
 */
public class mimagecheck extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()+ NikitaService.getFileSeparator() +  request.getParameter("imagename");  
                               
        if (new File(path).exists() ) {
            response.writeStream(Nset.readJSON("{'status':'exist'}", true).toJSON()); 
        }else{
            response.writeStream(Nset.readJSON("{'status':''}", true).toJSON()); 
        }
    }
}
