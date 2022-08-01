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
/**
 * created by 13k.mail@gmail.com
 */
public class StringComparationExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");
        
        String code = currdata.getData("code").toString().trim();
        if (code.equalsIgnoreCase("equal")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (val1.equals(val2)) {
                return true;
            }
        }else if (code.equalsIgnoreCase("notequal")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (!val1.equals(val2)) {
                return true;
            }
        }else if (code.equalsIgnoreCase("startwith")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (val1.startsWith(val2)) {
                return true;
            }
        }else if (code.equalsIgnoreCase("endwith")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (val1.endsWith(val2)) {
                return true;
            }
        }else if (code.equalsIgnoreCase("contain")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

            if (val1.contains(val2)) {
                return true;
            }
        }else if (code.equalsIgnoreCase("indexof")) {
            
            String key1 = data.getData("param1").toString().trim();
            String val1 = response.getVirtualString(key1);

            String key2 = data.getData("param2").toString().trim();
            String val2 = response.getVirtualString(key2);

//            int x = val1.indexOf(val2);
            if (val1.indexOf(val2) != -1) {
                return true;
            }
        }
        
        return false;
    }
    
}
