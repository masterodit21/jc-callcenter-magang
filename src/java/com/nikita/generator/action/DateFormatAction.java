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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author lenovo
 */
public class DateFormatAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        Nset data =currdata.getData("args");        
        String param = response.getVirtualString(data.get("param").toString());
        String format = response.getVirtualString(data.get("format").toString());
        
        String result = FormatDate(Utility.getInt(format), param, "");        
        response.setVirtual(data.get("result").toString(), result);
        return true;
    }
    
    public static String FormatDate(int code, String date, String time) {
            //- to yyyy-mm-dd hh:nn:ss
            Nset n = Nset.readJSON("['yyyy-MM-dd','dd/MM/yyyy','dd/MM/yy','dd/MMM/yyyy','dd/MMM/yy','MM/dd/yyyy','MM/dd/yy','MMM/dd/yyyy','MMM/dd/yy','yyyyMMdd','yyyyMMdd','']", true);
        
            String sDate = "" ;
            SimpleDateFormat dateFormatOfStringInDB = new SimpleDateFormat(code<=0?n.getData(code*-1).toString():"yyyy-MM-dd");
            try {
                Date d1 = dateFormatOfStringInDB.parse(date);
                SimpleDateFormat dateFormatYouWant = new SimpleDateFormat(code<=0?"yyyy-MM-dd":n.getData(code).toString());
                sDate = dateFormatYouWant.format(d1);
            } catch (Exception e) {  }    
             
          
            time=(time!=null)?time:"";            
            return sDate+(time.equals("")?"":" ")+time;
	}

    
}
