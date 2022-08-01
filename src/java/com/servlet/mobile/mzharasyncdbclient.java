/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class mzharasyncdbclient extends mzharasyncfileclient{
    
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String realPath = NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("pathfile").toString();
        dumpFile(realPath, response);
        
        super.runZhara(request, response, logic, "init-syncdb-client", true, "down", true );
        executeFile(realPath+NikitaService.getFileSeparator()+"down", response);
        
        response.closeAllConnection();
    }
         
    public static String getAlpaNumber(String s) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if ("QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm01234567890".indexOf(s.charAt(i)) != -1) {
                    buf.append(s.charAt(i));
            }
        }
        return buf.toString();
    }
            
    private void dumpFile(String path, NikitaResponse response){
        String connection = NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("connection").toString(); 
        String client = NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("clientcode").toString();
        NikitaConnection nc = response.getConnection(connection);
        for (int i = 0; i < 1000; i++) {
            Nikitaset ns = nc.QueryPage("SELECT id,syncdata,syncmode FROM zharasync ORDER BY id ASC", Nset.newArray(), 1, 50);//id(autoinc),data,mode(insert/delete/update)
            if (ns.getRows()>0) {
                if (i>=1) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) { }  
                }
                try {
                    FileOutputStream fos = new FileOutputStream(path+NikitaService.getFileSeparator()+getAlpaNumber(client)+"-"+System.currentTimeMillis()+"-"+i+".dbsql");
                    fos.write(ns.toNset().toJSON().getBytes());
                    fos.flush();
                    fos.close();
                    nc.Query("DELETE FROM zharasync WHERE id <= ? ", ns.getText(ns.getRows()-1, 0) );
                } catch (Exception e) {
                    System.err.println("dumpFile.:");
                    System.err.println(e.getMessage());
                }
            }else{
                break;//return
            }
        }   
    }
    
    private void executeFile(String path, NikitaResponse response){
         
        String connection = NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("connection").toString(); 
        NikitaConnection nc = response.getConnection(connection);
        
        try {

            
            
            nc.setAutoCommit(false); 
            File file = new File( path );
            File[] files = file.listFiles();
            //Sorted
            mzharasyncfileclient.sortZharaDBFile (files);
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()||files[i].isHidden()) {                
                }else if (files[i].isFile()) {
                    FileInputStream fis = new FileInputStream(files[i]);
                    Nikitaset ns= new Nikitaset(Nset.readJSON(Utility.readFile(fis)));
                    try {       
                        nc.setSavepoint();
                        for (int j = 0; j < ns.getRows(); j++) {
                            nc.Query(ns.getText(j, 1));
                        }
                        nc.commit();//nc.getInternalConnection().commit();       
                        
                        fis.close();
                        files[i].delete();
                        
                        try {
                             //new File( path + NikitaService.getFileSeparator() + "sync"+ NikitaService.getFileSeparator() +files[i].getName()).delete();
                        } catch (Exception e) { }
                    } catch (Exception e) { 
                        System.err.println("executeFile.:"+files[i]);
                        System.err.println(e.getMessage());
                        
                        nc.rollback();
                        break;//stop
                    }  
                }
            }  
            nc.setAutoCommit(true); 
        } catch (Exception e) { 
            System.err.println("executeFile..:");
            System.err.println(e.getMessage());
        }
        
        
        
    }

}
