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
public class NikitaAppAction implements IAction{
    public boolean OnAction(final Nset currdata, final NikitaRequest request, final NikitaResponse response, final NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String comp = (currdata.getData("args").getData("param1").toString());
        String modal = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
       
        
        if (code.equals("serialport")) {    
            String com = response.getVirtualString(currdata.getData("args").getData("param1").toString());
            String setting = response.getVirtualString(currdata.getData("args").getData("param2").toString());              
            String data = response.getVirtualString(currdata.getData("args").getData("param3").toString());  
            String prefix = response.getVirtualString(currdata.getData("args").getData("param4").toString()); 
            
            String mode = response.getVirtualString(currdata.getData("args").getData("param7").toString());              
            String reqcode = response.getVirtualString(currdata.getData("args").getData("param8").toString());  
                
            response.getSerialPort(reqcode, com,  setting, prefix, mode, data );
        }else  if (code.equals("print")) {       
            String printer = response.getVirtualString(currdata.getData("args").getData("param1").toString());
            String file = response.getVirtualString(currdata.getData("args").getData("param2").toString());              
            String datastring = response.getVirtualString(currdata.getData("args").getData("param3").toString());  
            String reqcode = response.getVirtualString(currdata.getData("args").getData("param8").toString()); 
            
            //response.getClientPrint(reqcode, printer, file, datastring);
        }        
        return true;
    }
}
