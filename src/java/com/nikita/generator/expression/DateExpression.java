/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.expression;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * created by 13k.mail@gmail.com
 */
public class DateExpression implements IExpression{

    @Override
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");        
        
        String code = currdata.getData("code").toString().trim();
        String param1 = response.getVirtualString(data.get("param1").toString());
        String param2 = response.getVirtualString(data.get("param2").toString());
        
//        long date1,date2;
       
        long date1, date2;
        
        if (code.equalsIgnoreCase("date>")) {
            date1 = converDateToLong(param1);
            date2 = converDateToLong(param2);
            
            if (date1 > date2) {
                return true;
            }
            
        }else if (code.equalsIgnoreCase("date>=")) {
            date1 = converDateToLong(param1);
            date2 = converDateToLong(param2);
            
            if (date1 >= date2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("date<")) {
            date1 = converDateToLong(param1);
            date2 = converDateToLong(param2);
            
            if (date1 < date2) {
                return true;
            }
        }else if (code.equalsIgnoreCase("date<=")) {
            date1 = converDateToLong(param1);
            date2 = converDateToLong(param2);
            
            if (date1 <= date2) {
                return true;
            }
        }
        return false;
    }
    
    public static int convert(String date){
        if (date.contains("-")) {
            date = date.replace("-", "");
        }else if (date.contains("/")) {
            date = date.replace("/", "");
        }
        
        return Utility.getInt(date);
    }
    
    public static long converDateToLong(String date) {

		SimpleDateFormat df = null;

		if (date.length() == 10) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		} else if (date.length() == 16) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			}
		} else if (date.length() == 19) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}
		} else {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		}

		try {
			Date dt = df.parse(date);
			long hasil = dt.getTime();
			return hasil;
		} catch (Exception e) {
		}

		return 0;
	}
    
}
