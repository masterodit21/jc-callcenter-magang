/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class ConverterAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");        
        String param1 = response.getVirtualString(data.get("param1").toString());
        String param2 = response.getVirtualString(data.get("param2").toString());
        String param3 =  data.get("param3").toString() ;
        if (param1.equals("toint")) {
            response.setVirtual(param3, Utility.getInt(param2));
        }else  if (param1.equals("todouble")) {
            response.setVirtual(param3, Utility.getDouble(param2));
        }else{
            response.setVirtual(param3, "0");
        }
        return true;
    }
}
