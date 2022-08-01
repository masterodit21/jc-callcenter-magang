/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.web.utility.WebUtility;

/**
 *
 * @author rkrzmail
 */
public class NikitaNotification extends NikitaService{

    public static String nikitaPushMessage(String user, String account, String target, String message, NikitaResponse response){
        NikitaConnection nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        int dbCore = WebUtility.getDBCore(nikitaConnection);
        String msg;
        
        Nikitaset registerdevice = nikitaConnection.Query("SELECT * FROM nikita_register_device WHERE registeraccount = ? ", account);
        Nikitaset targetdevice = nikitaConnection.Query("SELECT * FROM nikita_target_device WHERE targetaccount = ? ", target);
        if(registerdevice.getRows() > 0 && targetdevice.getRows() > 0){
            Nikitaset check = nikitaConnection.Query("SELECT * FROM nikita_register_device WHERE registeraccount = ? AND registertarget = ? ", account, target);
            if(check.getRows() < 10){
                nikitaConnection.Query("INSERT INTO nikita_push_message("+
                                        "pushaccount,pushtarget,pushmessage,createdby,createddate)"+
                                        "VALUES(?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                        
                                        account,
                                        target,
                                        message,
                                        user);
                msg = "success";
            }else{
                msg = "failed";
            }
        }else{            
            msg = "data not found";
        }
        return msg;
    }
    
    public static String nikitaRegisterDevice(String user, String account, String target, String imei, String application, NikitaResponse response){
        NikitaConnection nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        int dbCore = WebUtility.getDBCore(nikitaConnection);
        String msg;
        Nikitaset ns = nikitaConnection.Query("INSERT INTO nikita_register_device("+
                                "registeraccount,registertarget,registerimei,registerapp,createdby,createddate)"+
                                "VALUES(?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                        
                                account,
                                target,
                                imei,
                                application,
                                user);
        if(ns.getError().length() > 1){
            msg = ns.getError();
        }else{
            msg = "success";
        }
        return msg;
    }
    
    public static String nikitaTargetDevice(String user, String account, String imei, NikitaResponse response){
        NikitaConnection nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        int dbCore = WebUtility.getDBCore(nikitaConnection);
        String msg;
        Nikitaset ns = nikitaConnection.Query("INSERT INTO nikita_target_device("+
                                "targetaccount,targetimei,createdby,createddate)"+
                                "VALUES(?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                        
                                account,
                                imei,
                                user);
        if(ns.getError().length() > 1){
            msg = ns.getError();
        }else{
            msg = "success";
        }
        return msg;
    }
    
    
}
