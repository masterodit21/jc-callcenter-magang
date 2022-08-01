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

/**
 *
 * @author rkrzmail
 */
public class StackAction implements IAction{
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String comp = (currdata.getData("args").getData("param1").toString());
        String modal = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
        String reqcode = response.getVirtualString(currdata.getData("args").getData("param4").toString());  
        if (code.equals("clear")) {
            
        }else if (code.equals("read")) {
            
        }else if (code.equals("write")) {
            //form, data              
        }        
        return true;
    }
}
