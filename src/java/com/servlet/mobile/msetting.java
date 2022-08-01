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

/**
 *
 * @author rkrzmail
 */
public class msetting extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        response.setVirtual("@SETTING", "Sudah lah, akan kucari");
        response.setVirtual("@COBACOBA", response.getVirtual("@COBA"));
    }
    
            
}
