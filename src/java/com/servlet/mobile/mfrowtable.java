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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
/**
 *
 * @author rkrzmail
 */
public class mfrowtable extends NikitaServlet{
    
    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String fname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString()+NikitaService.getFileSeparator() + request.getParameter("userid")+ request.getParameter("imei")+ response.getVirtual("@+RANDOM");
        //NikitaConnection nc = response.getConnection(NikitaConnection.MOBILE); 
        NikitaConnection nc = response.getConnection("mfrowtable"); 
        String udate = request.getParameter("udate");
        String nameUdate = NikitaConnection.getDefaultPropertySetting().getData("mfrowtable").getData("udate").toString() ;
        
        String  sql="SELECT * FROM " + request.getParameter("table") + " WHERE "+nameUdate+" > "+Utility.escapeSQL(udate) + " ORDER BY "+nameUdate+"  ASC ";
        Nikitaset ns = nc.Query("::"+fname+"::"+request.getParameter("table")+"::" +sql );    
        
        try {
            FileInputStream fileInputStream = new FileInputStream(fname);
            Hashtable<String, String> hdr =new Hashtable<String, String>();
            hdr.put("rows", Nset.readJSON(ns.getError()).getData("rows").toString());
            NikitaService.getResourceStream(new FileInputStream(fname), request.getHttpServletRequest(), response.getHttpServletResponse(),hdr,  "nikitaftable.db", true);
            fileInputStream.close();
            new File(fname).delete();//delete
        } catch (Exception e) { }
    }
    
    
}
