/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.Style;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class StyleAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 = currdata.getData("args").getData("param1").toString();         
        if (code.equals("style")  && param1.startsWith("$")) {
            Component com = response.getComponent(param1);
            com.setStyle(Style.createStyle( response.getVirtualString(currdata.getData("args").getData("param2").toString())));
            response.refreshComponent(com);
        }
        return true;
    }
    
 
}
