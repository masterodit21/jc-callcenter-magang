/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class autentification extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);
        
        Nikitaset ns = nc.Query("SELECT auth,theme,position FROM sys_user WHERE username=? ",response.getVirtualString("@+SESSION-LOGON-USER"));
        if (ns.getRows()>=1) {
            if (ns.getText(0, "auth").equals(response.getVirtualString("@+SESSION-LOGON"))) {
                response.setVirtual("@+SESSION-THEME", ns.getText(0, "theme")) ;
                //response.setVirtualRegistered("@+POSITION", ns.getText(0, "position")) ;
                response.filterNext();
            }else{
                response.filterNext("splash");
            }     
        }else if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitacode").toString().contains("cookielogin")) {
            //new methode
            String user = Utility.fromHexString(response.getVirtualString("@+COOKIE-LOGUSE"));
            ns = nc.Query("SELECT auth,theme,position FROM sys_user WHERE username=? ", user);
            if (ns.getRows()>=1) {                
                String hashsession = response.getVirtualString("@+COOKIE-LOGSES") ;
                String hashdb = Utility.MD5(Utility.NowDate()+ns.getText(0, "auth"));
                
                if (hashdb.equals(hashsession)) {
                    response.setVirtual("@+SESSION-THEME", ns.getText(0, "theme")) ;
                 
                    //refill
                    ns = nc.Query("SELECT username,auth,status,name,avatar FROM sys_user WHERE username=? ", user  );               
                    response.setVirtual("@+SESSION-LOGON", ns.getText(0, "auth"));
                    response.setVirtual("@+SESSION-LOGON-NAME", ns.getText(0, "name"));//                  
                    response.setVirtual("@+SESSION-LOGON-USER", user);
                    response.setVirtual("@+SESSION-LOGON-AVATAR", ns.getText(0, "avatar").trim());
                    
                    
                    response.filterNext();
                }else{
                    response.filterNext("splash");
                } 
            }else{
                response.filterNext("splash");
                //response.filterNext();
            }
        }else{
            response.filterNext("splash");
            //response.filterNext();
        }
        //nc.closeConnection();
    }    
}
