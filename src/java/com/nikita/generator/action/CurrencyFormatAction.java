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
import java.text.DecimalFormat;
import java.text.*;

/**
 *
 * @author lenovo
 */
public class CurrencyFormatAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        Nset data =currdata.getData("args");        
        String param = response.getVirtualString(data.get("param").toString());
        String separator = response.getVirtualString(data.get("separator").toString());
        String decimal = response.getVirtualString(data.get("decimal").toString());
        StringBuffer sb = new StringBuffer();
        String format = null;
        if (separator.equalsIgnoreCase("IDR")) {
            format = Utility.formatCurrency(param).replace(",", ".");
        }else{            
            format = Utility.formatCurrency(param);
        }
        if (decimal.equalsIgnoreCase("None")) {
        }else{            
            if (separator.equalsIgnoreCase("IDR")) {
                format = sb.append(format).append(",00").toString();
            }else{            
                format = sb.append(format).append(".00").toString();
            }
        }
        
        response.setVirtual(data.get("result").toString(), format);
        return true;
    }
    
        
        public static String FormatCurrency(String curr, double val){
            DecimalFormat format = null;
            String strValue = null;

            format = (DecimalFormat)NumberFormat.getCurrencyInstance();
            DecimalFormatSymbols symbol = format.getDecimalFormatSymbols();
            symbol.setGroupingSeparator('.');
            format.setDecimalFormatSymbols(symbol);

            strValue = format.format(val); // $200.000.00
            
            return strValue;
        }
    
    
}
