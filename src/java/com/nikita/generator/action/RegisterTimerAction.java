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
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author rkrzmail
 */
public class RegisterTimerAction implements IAction{   
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString()); 
        
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());//ID
        String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());//url
        String param3 = response.getVirtualString(currdata.getData("args").getData("param3").toString());//arg(NSET)
        String param4 = response.getVirtualString(currdata.getData("args").getData("param4").toString());//callback at (yyy-mm-dd hh:nn:ss)
        
        if (code.equalsIgnoreCase("register")) {
            
        }else if (code.equalsIgnoreCase("remove")) {
            
        }else if (code.equalsIgnoreCase("clear")) {
            
        } 
        return true;
    }
    
    private ScheduledExecutorService serviceRegister;
    public void setup(){
       
    }
}
