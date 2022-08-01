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
public class ComparationExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");
       
        String param = currdata.getData("code").toString().trim();
        if (param.equalsIgnoreCase("=")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (val1.equals(val2)) {
                return true;
            }
        }else if (param.equalsIgnoreCase("!=")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (!val1.equals(val2)) {
                return true;
            }
        }else if (param.equalsIgnoreCase(">")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Utility.getInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 = Utility.getInt(response.getVirtualString(key2));

            if (val > val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase(">=")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Utility.getInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 = Utility.getInt(response.getVirtualString(key2));

            if (val >= val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("<")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Utility.getInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 =Utility.getInt(response.getVirtualString(key2));

            if (val < val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("<=")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Utility.getInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 = Utility.getInt(response.getVirtualString(key2));

            if (val <= val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("equalint")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Integer.parseInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 = Integer.parseInt(response.getVirtualString(key2));

            if (val == val2) {
                return true;
            }
        }else if (param.equalsIgnoreCase("notequalint")) {
            
            String key1 = data.getData("param1").toString().trim();
            int val = Integer.parseInt(response.getVirtualString(key1));

            String key2 = data.getData("param2").toString().trim();
            int val2 = Integer.parseInt(response.getVirtualString(key2));

            if (val != val2) {
                return true;
            }
        }
        
        return false;
    }
    
}
