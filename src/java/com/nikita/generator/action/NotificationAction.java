/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.Nset;
import java.util.Hashtable;

/**
 *
 * @author rkrzmail
 */
public class NotificationAction implements IAction{
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString()); 
        
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());///targetid(token)  
        Object param2 = response.getVirtual(currdata.getData("args").getData("param2").toString());//senderid
        String param3 = response.getVirtualString(currdata.getData("args").getData("param3").toString());//sendterauth
        String param4 = response.getVirtualString(currdata.getData("args").getData("param4").toString());//additional
        Object param5 = response.getVirtual(currdata.getData("args").getData("param5").toString());//message
        Object param6 = response.getVirtual(currdata.getData("args").getData("param6").toString());//msgtype
        
        String result = (currdata.getData("args").getData("param5").toString());//result
        if (code.equals("apns")) {
            String apache = NikitaConnection.getDefaultPropertySetting().getData("init").getData("apache").toString();
            if (apache.equals("")) {
                apache = "http://localhost/nikitagenerator/apns/send.php";
            }else{
                apache = apache+"/apns/apns.php";
            }
            Hashtable args = new Hashtable();
            args.put("target", param1);
            args.put("sender", param2);
            args.put("password", param3);
            args.put("additional", param4);
            args.put("message", param5);
            args.put("msgtype", param6);
            
            
            response.setVirtual(result,   NikitaInternet.getString(NikitaInternet.postHttp(apache, args))  );
        }else if (code.equals("gcm")) {
            String apache = NikitaConnection.getDefaultPropertySetting().getData("init").getData("apache").toString();
            if (apache.equals("")) {
                apache = "http://localhost/nikitagenerator/gcm/send.php";
            }else{
                apache = apache+"/gcm/send.php";
            }
            Hashtable args = new Hashtable();
            args.put("target", param1);
            args.put("sender", param2);
            args.put("password", param3);
            args.put("additional", param4);
            args.put("message", param5);
            args.put("msgtype", param6);
            
            response.setVirtual(result,   NikitaInternet.getString(NikitaInternet.postHttp(apache, args))  );
        }else if (code.equals("nikitapush")) {
             
        }
        
        return true;
    }
    
}
