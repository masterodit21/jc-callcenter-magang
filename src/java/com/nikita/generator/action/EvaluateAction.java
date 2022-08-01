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
import com.rkrzmail.nikita.utility.Utility;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author lenovo
 */
public class EvaluateAction implements IAction{
    
    
    public double evaluate (String code, Nset data, NikitaResponse response ){
        double result = 0;
          
        Map map = new HashMap();
        map.put("A", Utility.getDouble(response.getVirtualString(data.get("parama").toString())));
        map.put("B", Utility.getDouble(response.getVirtualString(data.get("paramb").toString())));
        map.put("C", Utility.getDouble(response.getVirtualString(data.get("paramc").toString())));
        map.put("D", Utility.getDouble(response.getVirtualString(data.get("paramd").toString())));
        map.put("E", Utility.getDouble(response.getVirtualString(data.get("parame").toString())));
        map.put("F", Utility.getDouble(response.getVirtualString(data.get("paramf").toString())));
        map.put("G", Utility.getDouble(response.getVirtualString(data.get("paramg").toString())));
        map.put("H", Utility.getDouble(response.getVirtualString(data.get("paramh").toString())));
        
      
        try {
            Calculable cc =  new ExpressionBuilder(code).withVariables(map).build();
            result = cc.calculate();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return result;
    }
    
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");   
        String code = data.getData("evaluate").toString().trim();
   
        
        response.setVirtual(data.getData("result").toString(), Double.toString(evaluate(code, data, response)));
        return true;
    }
    
}
