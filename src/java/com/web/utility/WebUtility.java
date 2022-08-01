/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author user
 */

public class WebUtility {
public static final int CORE_MYSQL = NikitaConnection.CORE_MYSQL;
public static final int CORE_SQLSERVER = NikitaConnection.CORE_SQLSERVER;
public static final int CORE_ORACLE = NikitaConnection.CORE_ORACLE;
public static final int CORE_SQLITE = NikitaConnection.CORE_SQLITE;

    public static int getDBCore(NikitaConnection nc){
        Nikitaset ns = nc.Query("SELECT VERSION()");
        if(ns.getRows() >= 1 && ns.getError().equals("")){
            return CORE_MYSQL;
        }
        ns = nc.Query("SELECT @@VERSION");
        if(ns.getRows() >= 1 && ns.getError().equals("")){
            return CORE_SQLSERVER;
        }
        ns = nc.Query("SELECT * FROM v$version");
        if(ns.getRows() >= 1 && ns.getError().equals("")){
            return CORE_ORACLE;
        }
        return CORE_SQLITE;
    }
    
    public static String getDBDate(int dbCOre) {
        if(dbCOre == CORE_MYSQL){
            return "NOW()";
        }else if(dbCOre == CORE_SQLSERVER){
            return "getdate()";
        }else if(dbCOre == CORE_ORACLE){
            return "sysdate";
        }
        return "'"+Utility.Now()+"'";
    }
    
    class Sequencer {
        private final AtomicLong sequenceNumber = new AtomicLong(0);
        public long next() {
          return sequenceNumber.getAndIncrement();
        }
     }
    private void aas(){
        Number ss;
        ss=1;
        
    }
}
