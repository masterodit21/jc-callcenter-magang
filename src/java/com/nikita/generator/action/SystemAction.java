/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author rkrzmail
 */
public class SystemAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String data = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        if (code.equals("out")) {
            System.out.println(data);
        }else if (code.equals("logdb")) {
            response.getConnection().Query("INSERT INTO web_log (message) VALUES (?)", data);
        }else if (code.equals("filternext")) {
            response.filterNext(data);          
        }else if (code.equals("filterbase")) {
            response.filterBaseReload(data, request, response, logic);      
        }else if (code.equals("break")) {
           return false;
        }else if (code.equals("close")||code.equals("exit")||code.equals("stop")) {
            response.setLogicClose(true);
            response.setInterupt(true);
        }else if (code.equals("loop")) {            
            logic.setLoopCount(logic.getLoopCount()+1);
            int i = Utility.getInt(data) ;
            if ( i>=100000 ) {                
                return false;//break
            }
            if (logic.getLoopCount()>=i ) {                
                return true;//loop finish (next logic)
            } else{
                logic.setCurrentRow(0);//reset to 0
            }           
        }else if (code.equals("native")) {   
            String param1 = response.getVirtualString(currdata.getData("param1").toString()); 
            try {
                Object cls = Class.forName(param1).newInstance();
                
                
            } catch (Exception e) {  }
        }else if (code.equals("dateadd")) { 
            String date = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
            int add = Utility.getInt(    response.getVirtualString(currdata.getData("args").getData("param2").toString())    ) ;  
            String satuan = response.getVirtualString(currdata.getData("args").getData("param3").toString());  //day, month, year
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = new Date();
            try {
                myDate = format.parse(date);
            } catch (ParseException ex) { }
            if (satuan.equals("day")) {
                myDate = DateUtils.addDays(myDate, add);
            }else if (satuan.equals("month")) {
                myDate = DateUtils.addMonths(myDate, add);
            }else if (satuan.equals("year")) { 
                myDate = DateUtils.addYears(myDate, add);
            }   
            
            response.setVirtual(currdata.getData("args").getData("result").toString(), format.format(myDate));
        }else if (code.equals("dateiif")) { 
            String date1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
            String date2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");            
            try {
                Date myDate1 =  format.parse(date1);
                Date myDate2 =  format.parse(date2);
                long div = (myDate1.getTime() -  myDate2.getTime()) / 86400000;
                
                response.setVirtual(currdata.getData("args").getData("result").toString(), (int)div);
            } catch (ParseException ex) { 
                response.setVirtual(currdata.getData("args").getData("result").toString(), 0);
            }
            
        }else if (code.equals("isdate")){
            String date = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                format.parse(date);
                response.setVirtual(currdata.getData("args").getData("result").toString(), "true");
            } catch (ParseException ex) { 
                response.setVirtual(currdata.getData("args").getData("result").toString(), "false");
            }
        }else if (code.equals("property")){
            String result = "";
            if (data.equals("dispaysize")) {
                
            }else if (data.equals("dispayresolution")||data.equals("dispayresolutionpx")) {
                
            }else if (data.equals("dispayresolutiondp")) {
                
            }else if (data.equals("gadget")) {//smartphone,table,dekstop,web
                 
            }else if (data.equals("os")) {
                result = "ANDROID ";
            }else if (data.equals("merk")) {  
                
            }else if (data.equals("model")) {
                
            }else if (data.equals("generatorversion")) { 
                
            }
 
                     
            response.setVirtual(currdata.getData("args").getData("result").toString(), result);         
        }
        return true;
    }
    
}
