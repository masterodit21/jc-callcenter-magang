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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class mgpstrack extends NikitaServlet{

    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);  
        String data = request.getParameter("gpsfile");//newformat
        if (data.equals("")) {
            data = request.getParameter("gps");//lastformat
        }
        Vector<String> array = Utility.splitVector(data, "\n") ;
        //System.err.println("mgpstrack:"+array.size()+"-"+data);
        for (int i = 0; i < array.size(); i++) {
            Nset v = Nset.readJSON(array.elementAt(i));
            if (v.getSize()>=5) {
                String[] args = new String[11];
                args[0]  = "decimal|"+ (v.isNsetArray()?v.getData(0).toString():v.getData("latitude").toString()) ;
                args[1]  = "decimal|"+ (v.isNsetArray()?v.getData(1).toString():v.getData("longitude").toString());
                args[2]  = "s|"+       (v.isNsetArray()?v.getData(2).toString():v.getData("provider").toString());
                args[3]  = "decimal|"+ (v.isNsetArray()?v.getData(3).toString():v.getData("accuracy").toString());
                args[4]  = "dt|"+      (Utility.formatDate(  (v.isNsetArray()?v.getData(4).toLong():v.getData("time").toLong()) , "yyyy-MM-dd HH:mm:ss"));//gpstime
                args[5]  = "decimal|"+ (v.isNsetArray()?v.getData(5).toString():v.getData("altitude").toString());
                args[6]  = "decimal|"+ (v.isNsetArray()?v.getData(6).toString():v.getData("speed").toString());
                args[7]  = "i|"+       (v.isNsetArray()?v.getData(7).toInteger():v.getData("status").toInteger());//status
                args[8]  = "s|"+request.getParameter("imei");//imei
                args[9]  = "s|"+request.getParameter("username");//username           
                args[10] = "dt|"+Utility.Now();//createddate    
                nc.QueryNF("INSERT INTO gps_track (latitude, longitude, provider, accuracy, gpstime, altitude, speed, status,  imei, username, createddate) VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", args); 
            }
        }     
        response.writeStream("{'status':'OK','error':''}".replace("'", "\""));
    }
}
