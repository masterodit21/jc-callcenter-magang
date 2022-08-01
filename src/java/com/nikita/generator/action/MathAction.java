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
public class MathAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        
        Object obj; 
        if (code.equals("abs")) {
            obj = Math.abs(0);
           
        }else if (code.equals("floor")) {
            obj = Math.floor(0);
        }else if (code.equals("floor")) {
            obj = Math.round(0);
        }else if (code.equals("max")) {
            obj = Math.max(0,0 );
        }else if (code.equals("min")) {
            obj = Math.min(0,0 );
        }else if (code.equals("sin")) {
            obj = Math.sin( 0 );    
        }else if (code.equals("cos")) {
            obj = Math.cos( 0 );   
        }else if (code.equals("tan")) {
            obj = Math.tan( 0 );  
        }else if (code.equals("exp")) {
            obj = Math.exp( 0 );      
        }else if (code.equals("log")) {
            obj = Math.log(0 );      
        }else if (code.equals("log")) {
            obj = Math.log(0 );     
        } 
        
        return true;
    }
    
}
