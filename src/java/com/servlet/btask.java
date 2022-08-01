/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.naa.data.Nson;
import com.naa.utils.InternetX;
import static com.naa.utils.InternetX.nikitaYToken;
import static com.naa.utils.InternetX.sendBroadcastIfUnauthorized401;
import static com.naa.utils.InternetX.urlEncode;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaControler;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaRz;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class btask extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
    }   
  
    public static BlockingQueue  blockingQueue = new ArrayBlockingQueue(100);
    public static void init(){//must call first
        new Thread(new Runnable() {
            @Override
            public void run() {  
                long counter = 0;
                while (true) {    //loop foraver                 
                    try {
                        //blockingQueue.take();                         
                    } catch (Exception ex) { }
                    counter ++;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) { }
                    
                    String swa = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btaskwa-native").toString().trim();
                    if (swa.equalsIgnoreCase("true") && String.valueOf(counter).endsWith("0")) {
                        String taskname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btaskwa-url").toString().trim();
                        Hashtable<String, String> args = new Hashtable<String, String>();                       
                        NikitaInternet.postHttp(taskname, args);
                    }
                    
                    //blockingQueue.clear();//clear all antrian  
                    String s = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-native").toString().trim();
                    if (s.equalsIgnoreCase("true")) {//check dimastete sudah ok blm & native call)
                        NikitaConnection connection = NikitaConnection.getConnection("btask");
                        Nikitaset ns = connection.Query("SELECT A.`USER`, B.NAMA, IF(A.STATUS= 'AUX','AUX','IDLE')  AS STATUS, A.ACTIVTY, STATUS_CALL AS STATUS_CALL ,  IF(TIMESTAMPDIFF(MINUTE,A.TRIGGER_DATE, NOW())>=1,'GONE','HIRE')  AS HIRE FROM USER_MONITOR  A LEFT JOIN `USER` B ON (A.`USER`=B.`USER`) WHERE A.STATUS <>'AUX' AND  NOT (A.ACTIVTY LIKE '%ACTIVITY%') AND  NOT (A.ACTIVTY LIKE 'LOGOUT')  AND B.DISTRIBUSI_MODE ='AUTO' AND  TIMESTAMPDIFF(MINUTE,A.TRIGGER_DATE, NOW())<=3;", null);                        
                        for (int i = 0; i < ns.getRows(); i++) {
                             //Query yang callbancl
                            Nikitaset od = connection.Query("SELECT ID FROM ORDER_DATA D WHERE STATUS_RESULT in ('5','6','2','4','32','35') AND (USER_ASSIGN ='AUTO' OR USER_ASSIGN = ? )  AND CALL_SEMPAT = 0  AND DATE(TANGGAL_SEMPAT)=CURDATE()  AND TIMEDIFF(CURTIME(),JAM_SEMPAT) >=1 AND TIMEDIFF(CURTIME(),JAM_SEMPAT) <=1800 ORDER BY DIST_COUNT   ASC LIMIT 1 ;",ns.getText(i, 0));
                            if (od.getRows()>=1) {
                                //send call
                                Nson nMsg = Nson.newObject();
                                nMsg.set("to",ns.getText(i, 0));
                                nMsg.set("id",  od.getText(0, 0));
                                NikitaRz.sendtoBroadcastA(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "activity", nMsg.toJson());
                                 //update fcount
                                connection.Query("UPDATE ORDER_DATA SET CALL_SEMPAT = 1, DIST_COUNT =  (SELECT A FROM (SELECT IFNULL(MAX(DIST_COUNT),1) +1 AS A FROM ORDER_DATA )A)  WHERE ID = ?;",ns.getText(i, 0));
                            }else{                            
                                //query task
                                od = connection.Query("SELECT ID FROM ORDER_DATA WHERE STATUS_RESULT in ('0','30') AND (USER_ASSIGN ='AUTO' OR USER_ASSIGN = ?)  ORDER BY DATA_CLASS,DIST_COUNT ASC LIMIT 1;",ns.getText(i, 0));
                                if (od.getRows()>=1) {
                                    //send call
                                    Nson nMsg = Nson.newObject();
                                    nMsg.set("to",ns.getText(i, 0));
                                    nMsg.set("id",  od.getText(0, 0));
                                    NikitaRz.sendtoBroadcastA(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "activity", nMsg.toJson());
                                     //update fcount
                                    connection.Query("UPDATE ORDER_DATA SET DIST_COUNT =  (SELECT A FROM (SELECT IFNULL(MAX(DIST_COUNT),1) +1 AS A FROM ORDER_DATA )A)  WHERE ID = ?;",ns.getText(i, 0));
                                }
                            }
                           
                           
                        } 
                        
                        int idle =  NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-confirm-idle").toInteger();
                        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-confirm").toString().trim().equalsIgnoreCase("true")) {
                            idle = idle <=0 ? 15: idle;                            
                            StringBuilder builder = new StringBuilder();
                            //query autokill (wait)
                            ns = connection.Query("SELECT USER, TIMESTAMPDIFF(SECOND, ACTIVTY_DATE, NOW())AS IDLE  FROM USER_MONITOR WHERE ACTIVTY = 'ACTIVITY - WAIT'  AND TIMESTAMPDIFF(SECOND, ACTIVTY_DATE, NOW()) > "+idle+" ;", null);                        
                            for (int i = 0; i < ns.getRows(); i++) {
                                builder.append(ns.getText(i, 0)).append(";");     
                                //send broadcast
                                Nson nMsg = Nson.newObject();
                                nMsg.set("to",ns.getText(i, 0));
                                nMsg.set("id",ns.getText(i, 0));
                                NikitaRz.sendtoBroadcastA(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "activity-wait", nMsg.toJson());
                            }
                            //activity
                            ns = connection.Query("SELECT M.USER,  TIMESTAMPDIFF(SECOND, M.STATUS_CALL_DATE, NOW()) AS IDLE  FROM USER_MONITOR M LEFT JOIN USER U ON (M.`USER` = U.`USER`) WHERE M.ACTIVTY = 'ACTIVITY'  AND M.STATUS_CALL ='NOT_INUSE'   AND    TIMESTAMPDIFF(SECOND, M.STATUS_CALL_DATE, NOW()) > "+idle+" AND U.DISTRIBUSI_MODE='AUTO';", null);                        
                            for (int i = 0; i < ns.getRows(); i++) {
                                if (!builder.toString().contains(ns.getText(i, 0))) {
                                        //send broadcast
                                        Nson nMsg = Nson.newObject();
                                        nMsg.set("to",ns.getText(i, 0));
                                        nMsg.set("id",ns.getText(i, 0));
                                        NikitaRz.sendtoBroadcastA(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "activity-idle", nMsg.toJson());
                                }                                    
                                builder.append(ns.getText(i, 0)).append(";");                            
                            }
                            //aux
                            ns = connection.Query("SELECT M.USER,  TIMESTAMPDIFF(SECOND, M.STATUS_DATE, NOW()) AS IDLE  FROM USER_MONITOR  M LEFT JOIN USER U ON (M.`USER` = U.`USER`) WHERE M.STATUS = 'AUX'  AND U.DISTRIBUSI_MODE='AUTO' AND    TIMESTAMPDIFF(SECOND, M.STATUS_DATE, NOW()) > "+idle+";", null);                        
                            for (int i = 0; i < ns.getRows(); i++) {
                                if (!builder.toString().contains(ns.getText(i, 0))) {
                                    //send broadcast
                                    Nson nMsg = Nson.newObject();
                                    nMsg.set("to",ns.getText(i, 0));
                                    nMsg.set("id",ns.getText(i, 0));
                                    NikitaRz.sendtoBroadcastA(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "activity-aux", nMsg.toJson());
                                }                            
                            }
                        }                        
                        connection.closeConnection();
                    }                    
                    //proses disini
                    String taskname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-url").toString().trim();
                    if (taskname.length()>=3) {
                        InternetX.getHttpConnectionX(taskname, "");
                    }                    
                }
            }
        }).start();
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-status").toString().trim().equalsIgnoreCase("true")) {
            new Thread(new Runnable() {
                @Override
                public void run() {                
                    while (true) {    //loop foraver                 
                        String sUrl = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-status-url").toString() ;       
                        int sleep = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-status-sleep").toInteger() ;       
                        
                        try { 
                            if (!sUrl.equalsIgnoreCase("")) {                                
                                String s = NikitaInternet.getString(NikitaInternet.getHttp(sUrl  , "" ));  
                                Nson n = Nson.readJson(s).get("data");
                                NikitaConnection connection = NikitaConnection.getConnection("btask");
                                for (int i = 0; i < n.size(); i++) {
                                    String ext = n.get(i).get("interface").asString();
                                    if (ext.startsWith("SIP/")) {
                                        ext = ext.substring(4);                                    
                                    }
                                    String status = n.get(i).get("status").asString();
                                    String incall = n.get(i).get("incall").asString();
                                    
                                    connection.Query(" UPDATE USER_MONITOR SET  STATUS_CALL  =?, LOCATION =? WHERE USER = (SELECT USER FROM USER WHERE EXT = ? LIMIT 1) ;", status, incall , ext);
                                }  
                                connection.closeConnection();
                            }                                                    
                        } catch (Exception ex) {
                            try {
                                Thread.sleep(10000);//10detik
                            } catch (Exception e) { } 
                        }
                        try {
                            Thread.sleep(sleep<=1000?1000:sleep);
                        } catch (Exception e) { }                
                    }
                }
            }).start();
        }
    }
}
