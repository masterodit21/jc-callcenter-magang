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
import org.apache.commons.lang.StringUtils;
/**
 * created by 13k.mail@gmail.com
 */
public class StringExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = currdata.getData("code").toString().trim();
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        if (code.equals("regex")||code.equals("matches")) {
             String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
            return param1.matches(param2);
        }else if (code.equals("containchars")) {
            String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
            for (int i = 0; i < param1.length(); i++) {
                if (param2.contains(param1.substring(i,i+1))) {
                   return true;
                }                
            }
        }                
        return false;
    }
    
}
