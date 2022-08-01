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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class bproxy extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String stringURL = NikitaConnection.getDefaultPropertySetting().getData("init").getData("bproxy").toString();
        String[] paramvalue = new String[5];
        paramvalue[0]="fname="  +request.getParameter("fname");
        paramvalue[1]="session="+request.getParameter("session");
        paramvalue[2]="auth="   +request.getParameter("auth");
        paramvalue[3]="user="   +request.getParameter("user");
        paramvalue[4]="token="  +request.getParameter("token");
        
        URL object;
        try {
             
            if (paramvalue!=null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");
                        String sdata = urlEncode(paramvalue[i].substring(split+1));
                        stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                    }
                }
                stringURL =  stringURL+(stringURL.contains("?")?"&":"?")+stringBuffer.toString();
            }
            object = new URL(stringURL);



            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                //display what returns the POST request
             
                 
                
                int HttpResult = con.getResponseCode(); 
                
                                
                Map<String,List<String>> map =  con.getHeaderFields() ;
                if (map!=null) {                 
                    Iterator iterator = map.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = String.valueOf(iterator.next());
                        List<String> lst = map.get(key);
                        if (lst!=null) {
                            if (lst.size()==0) {
                                response.getHttpServletResponse().setHeader(key, lst.get(0));
                            }else{
                                for (int i = 0; i < lst.size(); i++) {
                                    response.getHttpServletResponse().addHeader(key, lst.get(i));                                    
                                }
                            }
                        }
                    }
                }
                //content-disposition →inline; filename="out-9890816755791-9999-20201121-222115-1605972072.545.wav" 
                
                
                OutputStream os = response.getHttpServletResponse().getOutputStream();
                
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
                    InputStream is = con.getInputStream();
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = is.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }
                    os.flush();
                    os.close();
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());

                    InputStream is = con.getErrorStream();
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = is.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                
                 
                 
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                 
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Utility.nikitaErrorConn();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
             
        }

    }
}
