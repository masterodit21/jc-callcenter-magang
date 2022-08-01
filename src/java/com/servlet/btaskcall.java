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
public class btaskcall extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
    }   
  
    public static BlockingQueue  blockingQueue = new ArrayBlockingQueue(100);
    public static void init(){//must call first
        new Thread(new Runnable() {
            @Override
            public void run() {  
                Nson nsonLast  = Nson.newObject() ;
                while (true) {    //loop foraver                 
                    try {
                        //blockingQueue.take();                         
                    } catch (Exception ex) { }
                     
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) { }
                    //blockingQueue.clear();//clear all antrian  
                    String s = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btaskcall-native").toString().trim();
                    if (s.equalsIgnoreCase("true")) {//check dimastete sudah ok blm & native call)
                        String taskname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btaskcall-url").toString().trim();
                        String pushName = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btaskcall-push").toString().trim();
                        
                        if (taskname.length()>=3) {
                            Nson nson  =  Nson.readJson(NikitaInternet.getString(NikitaInternet.getHttp(taskname, "")));
                            Nson push = Nson.newArray();
                            Nson endcall = Nson.newArray();
                            Nson exten = nson.getObjectKeys();
                            for (int i = 0; i < exten.size(); i++) {
                                String sext = exten.get(i).asString();
                                if (nson.get(sext).asString().length()>=2 && nsonLast.get(sext).asString().equalsIgnoreCase("")) {
                                    push.add(sext);
                                }
                            }
                            exten = nsonLast.getObjectKeys();
                            for (int i = 0; i < exten.size(); i++) {
                                String sext = exten.get(i).asString();                               
                                if (nsonLast.get(sext).asString().length()>=2 && nson.get(sext).asString().equalsIgnoreCase("")) {
                                    endcall.add(sext);
                                }   
                            }
                            nsonLast = nson;
                            if (push.size()>=1 || endcall.size()>=1) {
                                Hashtable<String, String> args = new Hashtable<String, String>();
                                args.put("extens", push.toJson());
                                args.put("endcall", endcall.toJson());
                                NikitaInternet.postHttp(pushName, args);
                            }
                                    
                        };
                    }                    
                    //proses disini
                                       
                }
            }
        }).start();
         
    }
}
