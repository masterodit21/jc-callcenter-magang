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
public class ChatAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());           
        String target = response.getVirtualString(currdata.getData("args").getData("param1").toString());
        
        
        if (code.equalsIgnoreCase("send")) {
            String message = response.getVirtualString(currdata.getData("args").getData("param2").toString());
            
        }else if (code.equalsIgnoreCase("sendimage")) {
            String image = response.getVirtualString(currdata.getData("args").getData("param2").toString());
            String message = response.getVirtualString(currdata.getData("args").getData("param3").toString());
            
            
        }else if (code.equalsIgnoreCase("resend")) {  
            
        }else if (code.equalsIgnoreCase("delete")) {
            
        }else if (code.equalsIgnoreCase("archive")) {
            
        }else if (code.equalsIgnoreCase("login")) {
            
        }else if (code.equalsIgnoreCase("logout")) {  
            
        }else if (code.equalsIgnoreCase("typing")) {  
            
        }
        return true;
    }
    
    
    public class NikiChat{
        
    }
}
