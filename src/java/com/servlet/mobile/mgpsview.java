/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.servlet.a;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class mgpsview extends NikitaServlet{

    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);  
       
        Nikitaset ns=nc.Query("SELECT latitude, longitude FROM  gps_track  WHERE username=?  ORDER BY gpstime DESC LIMIT 100 ", request.getParameter("username"));
        System.out.println(ns.toNset().toJSON());
        StringBuffer sb =new StringBuffer();
        for (int i = 0; i < ns.getRows(); i++) {
             int row = ns.getRows()-i-1;
             sb.append( sb.toString().length()>=1 ?"|":"" ).append(Utility.getDouble(ns.getText(row, 0)) ).append(",").append(Utility.getDouble(ns.getText(row, 1)) );
            
        }
          
                 
      /*
                 String path = URLEncoder.encode( sb.toString() );
                        
                HttpResponse httpResponse = NikitaInternet.getHttp( "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&apiKey=AIzaSyA7iBppwcTV5FPrEl_4qavvSjseq2KSDaU&path="+path);
                 
                InputStream is;   sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();                    
                    byte[] buffer = new byte[1024];int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) {  }   
                */
 response.writeStreamHeader("Content-Type", "text/plain");
                response.writeStream(sb.toString());
         
    }
        
        
        
 
    
    
}
