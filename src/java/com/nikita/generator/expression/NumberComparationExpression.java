/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.expression;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.expression.IExpression;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */
public class NumberComparationExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");
        String code = currdata.getData("code").toString().trim();
        
        if (code.equalsIgnoreCase("=")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val == val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("!=")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val != val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase(">")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val > val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase(">=")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val >= val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("<")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val < val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("<=")) {
            
            String key1 = data.getData("param1").toString().trim();
            double val = Utility.getDouble(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            double val2 = Utility.getDouble(response.getVirtualString(key2));

            if (val <= val2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("evaluate")) {
            
        }
        
        return false;
    }
    
}
