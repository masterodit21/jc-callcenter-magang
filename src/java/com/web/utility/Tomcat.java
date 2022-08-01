/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 *
 * @author rkrzmail
 */
public class Tomcat {
    private NikitaConnection connection;
    public void exec(NikitaRequest  nikitaRequest, NikitaResponse nikitaResponse){
        Nson xdata = Nson.readJson(nikitaRequest.getParameter("xstreamdata")) ;
        for (int i = 0; i < xdata.getData("data").size(); i++) {
            exec(nikitaRequest, nikitaResponse, xdata, i);            
        }    
        Nson nson = Nson.newObject();
        nson.setData("status", "OK");        
        nikitaResponse.writeStream(nson.toJson());
    }  
    private boolean exec(NikitaRequest  nikitaRequest, NikitaResponse nikitaResponse, Nson nsdata, int pos){
        if (getText(nsdata, pos, "status").equalsIgnoreCase("TRC02")) {
            
        }
        
        //gettracestatus, 
        return true;
    }
    private void iStatus(NikitaConnection connection, String status, Nson fields){
         
    }
    private void uMDE(NikitaConnection connection, String id, String track, Nson fields){
        
    }
    private Nson query(Connection conn, String sql, Nson args){
        try {
            conn.prepareCall(sql);
            PreparedStatement statement = conn.prepareStatement(sql);
            for (int i = 0; i < args.size(); i++) {
                if (args.isNumber()) {                    
                    statement.setLong(i+1, args.getData(i).asLong());
                }else  {                    
                    statement.setString(i+1, args.getData(i).asString());
                }                
            }
            statement.execute();             
            //System.err.println( new Nikitaset(statement.getResultSet()).toNset().toJSON());
            Nikitaset n = new Nikitaset(statement.getResultSet());
            return new Nson((Map)n.toNset().getInternalObject()).setData("UpdateCount", statement.getUpdateCount());
        } catch (Exception e) {
            return Nson.newObject().setData("Exception", e.getMessage());
        }        
    }
          
    public static String getText(Nson data, int row, String colname) {
        int col = -1;
        if (!data.getData("header").isNull()) {
            for (int i = 0; i < data.getData("header").size(); i++) {
                if (data.getData("header").getData(i).asString().equalsIgnoreCase(colname)) {//nocase sensitife
                    col = i;
                    break;
                }
            }
        }
        if (col >=0 ) {
            return data.getData("data").getData(row).getData(col).asString();
        }else{
            if (data.getData("info").isNson()) {
                Nson n = data.getData("info") ;
                if (n.containsKey("metadata") && n.getData("metadata").containsKey("name")) {
                    Nson nAS = n.getData("metadata").getData("name");
                    for (int i = 0; i < nAS.size(); i++) {
                        String name = nAS.getData(i).toString();
                        if (name.equalsIgnoreCase(colname)) {//nocase sensitife
                            return data.getData("data").getData(row).getData(col).asString();
                        }
                    }
                }
            }
        }
        return "";
    }
}
