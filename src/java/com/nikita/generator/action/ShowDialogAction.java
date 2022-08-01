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
public class ShowDialogAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String title = response.getVirtualString(currdata.getData("args").getData("title").toString());
        String message = response.getVirtualString(currdata.getData("args").getData("message").toString());
        String button1 = response.getVirtualString(currdata.getData("args").getData("button1").toString());
        String button2 = response.getVirtualString(currdata.getData("args").getData("button2").toString());
        String reqcode = response.getVirtualString(currdata.getData("args").getData("reqcode1").toString());
        
        
        if (reqcode.equals("") && currdata.getData("args").getData("data").toString().equals("")) {
            response.showDialog(title, message, reqcode, button1, button2);
        }else{
            System.err.println(response.getVirtual(currdata.getData("args").getData("data").toString()));
            response.showDialogResult(title, message, reqcode, new Nset(response.getVirtual(currdata.getData("args").getData("data").toString())) , button1, button2);
        }
        
        return true;
    }
    
}
