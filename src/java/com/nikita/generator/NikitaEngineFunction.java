/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class NikitaEngineFunction {
    
    public Object getNikitaFunction (Object data, Nset array){
        if (array.getData(0).toString().equals("$")) {
            if (array.getData(1).toString().equals("numberformat")||array.getData(1).toString().equals("decimalformat")) {
                
            }else if (array.getData(1).toString().equals("dateformat")) {
                
            }else if (array.getData(1).toString().equals("string")) {  
                
            }else if (array.getData(1).toString().equals("concat")) {
                
            }else if (array.getData(1).toString().equals("escape")) { 
                
            }else if (array.getData(1).toString().equals("new")) { 
                
            }
        }
        return data;
    }
}
