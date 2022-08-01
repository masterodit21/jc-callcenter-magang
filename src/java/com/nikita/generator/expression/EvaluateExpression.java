/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.expression;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.action.EvaluateAction;
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
 * created by 13k.mail@gmail.com
 */
public class EvaluateExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");   
        String code = data.getData("evaluate").toString().trim();  
  
        if (code.contains("==")) {
            String[] comparator = Utility.split(code, "==");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) == new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains("!=")) {
            String[] comparator = Utility.split(code, "!=");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) != new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains(">=")) {
            String[] comparator = Utility.split(code, ">=");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) >= new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains("<=")) {
            String[] comparator = Utility.split(code, "<=");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) <= new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains(">")) {
            String[] comparator = Utility.split(code, ">");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) > new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains("<")) {
            String[] comparator = Utility.split(code, "<");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) < new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }else if (code.contains("=")) {
            String[] comparator = Utility.split(code, "=");
            if ( new EvaluateAction().evaluate(comparator[0], data, response) == new EvaluateAction().evaluate(comparator[1], data, response) ) {
                return true;
            }  
        }
         
        return false;
    }
    
}
