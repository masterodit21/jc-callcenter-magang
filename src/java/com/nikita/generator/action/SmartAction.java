/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author rkrzmail
 */
public class SmartAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());           
       
        final int SATURDAY = 7;
        final int SUNDAY = 1;
        final long ONE_DAY_MS = 86400000;
        
        if (code.equalsIgnoreCase("smart")) {
            String args = response.getVirtualString(currdata.getData("args").getData("param1").toString());//args
            String result = response.getVirtualString(currdata.getData("args").getData("param2").toString());//args
            Nset nargs = Nset.readJSON(args);
            if (nargs.getData("code").toString().equalsIgnoreCase("comdate")) {
                int dcount = 0;
                int day = nargs.getData("day").toInteger();
                Date startdate = Calendar.getInstance().getTime();//now server
                String cutofftime = nargs.getData("cuttime").toString();//jam max bisa di mde [12:12]
                String mdedate = Utility.NowDateDB();//now
                String comdate = Utility.NowDateDB();//now
                boolean srv_sun = false;
                boolean srv_sat = false;
                
          
                
                Nikitaset holiday ;
                Object objholiday = response.getVirtual(nargs.getData("holiday").toString());
                if (objholiday instanceof Nikitaset) {
                    holiday = (Nikitaset)objholiday;
                }else if (objholiday instanceof Nset) {
                    holiday = new Nikitaset((Nset)objholiday);
                }else{
                    holiday = new Nikitaset(Nset.readJSON(String.valueOf(objholiday)));
                }
                        
                
                
                Calendar calendar = Calendar.getInstance();
              
                if (strtosmartjam(Utility.NowTime().substring(0,5)) > strtosmartjam(cutofftime.equals("")?"18:00":cutofftime)  ) {
                    startdate = getNextComdate(startdate, holiday, false, true);
                }  
                mdedate = Utility.formatDate(startdate.getTime(), "yyyy-MM-dd");
                for (int i = 0; i < 1000; i++) {
                    if (dcount>=day) {
                        //save
                        /*
                        mdedate:
                        comdate:
                        sun_comdate:
                        sat_comdate:
                        hol_comdate:
                        error:
                        errno:
                        */
                        calendar.setTime(startdate);
                        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);      
                        if (dayofweek==SATURDAY) {
                            srv_sat = true;  
                            if (nargs.getData("sat").toString().equalsIgnoreCase("true")||nargs.getData("sat").toString().equalsIgnoreCase("")) {
                                
                            }else{
                                startdate = getNextComdate(startdate, holiday, false, true);//nextday
                            }                                
                        }else if (dayofweek==SUNDAY) {
                            srv_sun = true;
                            if (nargs.getData("sun").toString().equalsIgnoreCase("true")) {                                
                            }else {
                                startdate = getNextComdate(startdate, holiday, false, true);//nextday
                            }
                        }        
                        
                        comdate = Utility.formatDate(startdate.getTime(), "yyyy-MM-dd");
                        break;
                    }                    
                    dcount++; 
                    
                    startdate = getNextComdate(startdate, holiday, false, false);
                    if (dcount>=day) {                         
                    }else{
                        calendar.setTime(startdate);
                        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);      
                        if (dayofweek==SUNDAY) {
                            startdate = getNextComdate(startdate, holiday, false, true);
                        }                        
                    }
                } 
                
                //code end
                Nset nresult = Nset.readJSON(result);
                response.setVirtual(nresult.getData("mdedate").toString(), mdedate);
                response.setVirtual(nresult.getData("comdate").toString(), comdate);
                response.setVirtual(nresult.getData("sat").toString(), srv_sat);
                response.setVirtual(nresult.getData("sun").toString(), srv_sun);
             
            }else  if (code.equalsIgnoreCase("datesplit")) { 
                
                Nset nresult = Nset.readJSON(result);
                response.setVirtual(nresult.getData("month").toString(), "");
                response.setVirtual(nresult.getData("day").toString(), "");
                response.setVirtual(nresult.getData("year").toString(), "");
                
                response.setVirtual(nresult.getData("hour").toString(), "");
                response.setVirtual(nresult.getData("minute").toString(), "");
                response.setVirtual(nresult.getData("second").toString(), "");
                
            }else  if (code.equalsIgnoreCase("dateadd")) { 
                
            }else  if (code.equalsIgnoreCase("dateinterval")) { 
                
            }else  if (code.equalsIgnoreCase("dateaformat")) {  
                //Utility.formatCurrency(args)
                
            }
 
          
           
        }
        return true;
    }
    private int strtosmartjam(String jam){
        if (jam.contains(":")) {
            String[] h = Utility.split(jam, ":");
            return  Utility.getInt(h[0])*60+Utility.getInt(h[0]);
        }else if (jam.length()>=3) {
            String h1 = jam.substring(jam.length()-2);
            return  Utility.getInt(jam.substring(0, jam.length()-2))*60+Utility.getInt(h1);
        
        }
        return Utility.getInt(jam);
    }
    private Date getNextComdate(Date startdate, Nikitaset holiday, boolean nextsaturday, boolean nextsunday){
        Date currdate = new Date(startdate.getTime());
        final int SUNDAY = 1;
        final int SATURDAY = 7;
        final long ONE_DAY_MS = 86400000;
        Calendar calendar = Calendar.getInstance();
                
        for (int i = 0; i < 360; i++) {
            currdate.setTime(currdate.getTime()+ONE_DAY_MS);
            
            boolean isholiday = false;
            for (int row = 0; row < holiday.getRows(); row++) {
                if (holiday.getText(row, 0).equalsIgnoreCase(Utility.formatDate(currdate.getTime(), "yyyy-MM-dd"))) {
                    isholiday = true;
                    break;
                }
            }               
            if (isholiday) {
                continue;
            }else{  
                if (nextsunday) {
                    if ( calendar.get(Calendar.DAY_OF_WEEK) == SUNDAY) { 
                        continue;
                    }
                }
                if (nextsaturday) {
                    if ( calendar.get(Calendar.DAY_OF_WEEK) == SATURDAY) { 
                        continue;
                    }
                }
                break;  
            }
        }
                       
        return currdate;
    }
    private void encodeBarcode(String barcode){
        
    }
    private void decodeBarcode(String barcode){
        
    }
}
