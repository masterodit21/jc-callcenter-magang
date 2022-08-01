/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.smart;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;

/* 
 * @author rkrzmail
 */

public class smActivitySync extends NikitaServlet{
    NikitaConnection nikitaConnection = NikitaConnection.getConnection("smart");
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String application  = request.getParameter("smartapplication"); //mysmart|dop|web|smart|java
        //if (!SmartUtility.validateAccount(nikitaConnection, request, response)) {
            //return;
        //}        
        Nset np = request.getRequestParameter();
        
        Nson nson = Nson.readJson(request.getParameter("xstreamdata"));
        Nson nResult = Nson.newObject();
        for (int i = 0; i < nson.getData("data").size(); i++) {
            Nson n = Nson.newObject();            
            String client = request.getParameter("imei")+":"+request.getHttpServletRequest().getRemoteAddr();
            n.setData("data", Nson.newArray().addData(nson.getData("data").getData(i)));
            n.setData("header", nson.getData("header"));    
            n.setData("imei", request.getParameter("imei")); 
            n.setData("app", "TOMCAT"); 
            n.setData("profile", "TOMCAT:"+request.getParameter("version")); 
            
            
            SmartStatus.execute(nikitaConnection, request, response, n);
            Nikitaset ni = nikitaConnection.Query("INSERT INTO trx_activity (STREAM,STATUS_TRX_STREAM,CREATED_DATE,MODUL,CLIENT) VALUES (?,?,?,?,?);", n.toJson(), "", Utility.Now(), "TOMCAT", client);
            if (ni.getError().length()>=3) {
                nResult.setData("status", "error");
                nResult.setData("error", ni.getError());
                response.writeStream(nResult.toJson());
                return;
            }                    
        }       
        nResult.setData("status", "OK");
        response.writeStream(nResult.toJson());
        return;
    }
}

