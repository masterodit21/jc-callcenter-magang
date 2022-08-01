/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servlet.smart;

import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.Z;

/**
 *
 * @author rkrzmail
 */
public class SmartStatus {
    public static void execute(NikitaConnection nikitaConnection, NikitaRequest nikitaRequest, NikitaResponse nikitaResponse, Nson curdata){
       new SmartStatus().doAction(nikitaConnection, nikitaRequest, nikitaResponse, curdata);
    } 
    Nson profile;Nson session;
    NikitaConnection nikitaConnection; NikitaRequest nikitaRequest; NikitaResponse nikitaResponse; Nson curdata;
    private void doAction(NikitaConnection nikitaConnection, NikitaRequest nikitaRequest, NikitaResponse nikitaResponse, Nson curdata){
        this.nikitaConnection = nikitaConnection;
        this.nikitaRequest = nikitaRequest;
        this.nikitaResponse = nikitaResponse;
        this.curdata = curdata;
        this.profile = Nson.readJson(getText("profile"));
        this.session = Nson.readJson(getText("session"));
        
        doLocationUpdate();
        String status = getText("status");
        if (status.equalsIgnoreCase("DEL")) {
            doDEL( );
        }else if (status.startsWith("DEX")) {
            doDEX( );
        }else if (status.startsWith("POD")) {
            doPOD();
        }else if (status.startsWith("PUP")) {
            doPUP();
        }else if (status.equalsIgnoreCase("BCON")) {
            doConsolidasi();
        }else if (status.equalsIgnoreCase("IST")) {
            doInStation();
        }else if (status.equalsIgnoreCase("STA")) {
            doStation();
        }else if (status.equalsIgnoreCase("INC")) {
            doIncoming();
        }else if (status.equalsIgnoreCase("OTG")) {
            doOutgoing();
        }else if (status.equalsIgnoreCase("RET")) {
            doReturn();
        }else if (status.equalsIgnoreCase("REV")) {
            doRevisi();
        }else if (status.equalsIgnoreCase("DMG")) {
            doDamage();
        }else if (status.equalsIgnoreCase("EXC")) {
            doException();
        }else if (status.equalsIgnoreCase("RIS")) {
            doRIS();
        }else if (status.equalsIgnoreCase("ABS")) {
            doAbsen();
        }
    }   
    private void doAbsen( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "ABS");        
        
        //check barang dari con
    }
    private void doTrace(Nson args){
        final String trace = "|CHK02|COM01|COM04|COM05|COM06|DELAY|LOS01|"
                + "|NOLBL|NOMPS|REJ01|REJ02|REQ02|REQ03|"
                + "|REQ04|REQ05|REQ06|RET03|SPLIT|TRP02|";
        if ( trace.contains(args.getData("KODE_STATUS").asString()) ) {
            //check track, 
            if (true) {
                //insert trace                
                Nson arg2 = Nson.newObject();
                setIfNotExist(arg2, "CREATED_BY", session.getData("xiduser").asLong());
                setIfNotExist(arg2, "CREATED_DATE", Utility.Now());
                setIfNotExist(arg2, "MODIFIED_BY", session.getData("xiduser").asLong());
                setIfNotExist(arg2, "MODIFIED_DATE", Utility.Now());

                setIfNotExist(arg2, "ID_TRACE", "0"); 
                setIfNotExist(arg2, "ID_KASUS", "0"); 
                
                
                setIfNotExist(arg2, "MODULE", "TOMCAT");             
                setIfNotExist(arg2, "ID_ASP_TUJUAN", "0"); 
                setIfNotExist(arg2, "ID_KIRIMAN", getText("track")); 
                
                StringBuilder query  = new StringBuilder();                 
                query.append("UPDATE trx_kiriman SET ");
                Nson nfield = args.getObjectKeys();  
                for (int i = 0; i < nfield.size(); i++) {
                    query.append(i>=1?",":"");
                    query.append(nfield.getData(i).asString());
                    query.append("='"); 
                    query.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
                    query.append("'"); 
                }
                query.append(" WHERE ID_REFERENSI = ");
                query.append("'");       
                query.append( Utility.escapeSQL( getText("TRACK") )   );
                query.append("';");// (date_field BETWEEN '2010-01-30 14:15:55' AND '2010-09-29 10:15:55')
                Nikitaset ns = nikitaConnection.Query(query.toString(), null);
                String err = ns.getError();
        
            }
        }
    }
    private void doDEL( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "DEL01");        
        setIfNotExist(args, "address", getText("address"));
        setIfNotExist(args, "company", getText("company"));
        setIfNotExist(args, "location", getText("location"));
        
        //check barang dari con     
        if (getText("track").startsWith("CON")) {
            Nikitaset ns = nikitaConnection.Query("SELECT data_cons FROM trx_cons WHERE track=?", getText("track"));
            Nson data = Nson.readJson(ns.getText(0, "data_cons"));            
            for (int i = 0; i < data.getData("data").size(); i++) {                    
                    args.setData("TRACK", getText("TRACK"));
                    args.setData("BARCODE", getText("BARCODE"));
                    args.setData("TRACK_CON", getText("TRACK"));
                    insertStatus(args);
            }
        }else{
            insertStatus(args);
        }   
        
    }
    private void doDEX( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", getText("status"));      
        
      
        
        Nson data = Nson.readJson(getText("data"));
        //check pod did
        if (data.containsKey("NAMA")) {
            args.setData("NAMA_PENERIMA", data.getData("NAMA").asString());   
        }
        if (data.containsKey("ALAMAT")) {
            args.setData("ALAMAT_POD", data.getData("ALAMAT").asString());   
        }       
        if (data.containsKey("CAMERA")) {
            args.setData("FILE_CAMERA", data.getData("CAMERA").asString());   
        }        
        if (data.containsKey("KETERANGAN")) {
            args.setData("KETERANGAN_1", data.getData("KETERANGAN").asString());   
        }        
        //check dex menyebabkan trace
         doTrace(args);
         
        //status insert 
        insertStatus(args);
    }
    private void doPOD( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", getText("status"));      
        
        Nson data = Nson.readJson(getText("data"));
        //check pod did
        if (data.containsKey("NAMA")) {
            args.setData("NAMA_PENERIMA", data.getData("NAMA").asString());   
        }
        if (data.containsKey("ALAMAT")) {
            args.setData("ALAMAT_POD", data.getData("ALAMAT").asString());   
        }       
        if (data.containsKey("TIPE_PENERIMA")) {
            args.setData("TIPE_PENERIMA", data.getData("TIPE_PENERIMA").asString());   
        }
        if (data.containsKey("CAMERA")) {
            args.setData("FILE_CAMERA", data.getData("CAMERA").asString());   
        } 
        if (data.containsKey("PROFILE")) {
            args.setData("FILE_PROFILE", data.getData("PROFILE").asString());   
        } 
        if (data.containsKey("PROSIGN")) {
            args.setData("FILE_SIGNATURE", data.getData("PROSIGN").asString());   
        } 
        if (data.containsKey("RATING")) {
            args.setData("RATING", data.getData("RATING").asInteger());   
        }    
        if (data.containsKey("GROUP")) {
            args.setData("GROUP_STATUS", data.getData("GROUP").asString());   
        }    
        //check pod opendel
        
        
        //check pod return   
            
        
        //insert
        insertStatus(args);
    }
    private void doPUP( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "INC02");       
        
        Nson data = Nson.readJson(getText("data"));
        if (data.containsKey("GROUP")) {
            args.setData("GROUP_STATUS", data.getData("GROUP").asString());   
        } 
        if (data.containsKey("DISID")) {
            args.setData("KETERANGAN_1", data.getData("DISID").asString());   
        } 
        if (data.containsKey("PUPID")) {
            args.setData("KETERANGAN_2", data.getData("PUPID").asString());   
        }         
        if (data.containsKey("revisiberat")&& data.getData("revisiberat").asDouble()>=1) {
            //REV02
            Nson cargs = Nson.newObject();
            cargs.setData("KODE_STATUS", "REV02");
            setIfNotExist(cargs, "TRACK", getText("TRACK"));
            setIfNotExist(cargs, "BARCODE", getText("BARCODE"));
            
            insertStatus(cargs);
            
            //update manifest ??            
            if (true) {
                Nson arg2 = Nson.newObject();
                setIfNotExist(arg2, "TOTAL_ONGKIR_REVISI", data.getData("revisiongkir").asDouble() ); 
                setIfNotExist(arg2, "TOTAL_BERAT_REVISI", data.getData("revisiberat").asDouble());             
                setIfNotExist(arg2, "TOTAL_BERAT_VOL_REVISI", data.getData("revisiberat").asDouble()); 
                setIfNotExist(arg2, "TOTAL_BERAT_VOL", data.getData("revisiberat").asDouble()); 

                StringBuilder query  = new StringBuilder();                 
                query.append("UPDATE trx_kiriman SET ");
                Nson nfield = args.getObjectKeys();  
                for (int i = 0; i < nfield.size(); i++) {
                    query.append(i>=1?",":"");
                    query.append(nfield.getData(i).asString());
                    query.append("='"); 
                    query.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
                    query.append("'"); 
                }
                query.append(" WHERE ID_REFERENSI = ");
                query.append("'");       
                query.append( Utility.escapeSQL( getText("TRACK") )   );
                query.append("';");// (date_field BETWEEN '2010-01-30 14:15:55' AND '2010-09-29 10:15:55')
                Nikitaset ns = nikitaConnection.Query(query.toString(), null);
                String err = ns.getError();
            }   
        } 
        
        
        
        insertStatus(args);
    }
    private void doInStation( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "INC01");        
        
        insertStatus(args);  
    }
    private void doStation( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "STATION");        
        
        insertStatus(args);  
    }
    private void doRIS( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "RAMPI");        
        
        insertStatus(args);  
    }
    private void doIncoming( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "INC02");        
        
        insertStatus(args);  
    }
    private void doOutgoing( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "OUTFR");        
        
        insertStatus(args);  
    }
    private void doReturn( ){
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "RET");        
        
        insertStatus(args);  
    }private void doDamage( ){
        Nson args = Nson.newObject();
        Nson data = Nson.readJson(getText("data"));
        if (data.getData("kondisi").asString().equalsIgnoreCase("LANJUT")) {
            args.setData("KODE_STATUS", "DMG01");   
        }else{
            args.setData("KODE_STATUS", "DMG02");   
        }
        args.setData("TIPE_STATUS", data.getData("type").asString());    
        args.setData("FILE_CAMERA", data.getData("image").asString());     
        args.setData("KETERANGAN_1", data.getData("keterangan").asString()); 
        
        
        insertStatus(args);  
    }
    private void doException( ){
        Nson args = Nson.newObject();          
        //exception
        Nson data = Nson.readJson(getText("data"));
        args.setData("KODE_STATUS", data.getData("exception").asString());
        args.setData("KETERANGAN_1", data.getData("keterangan").asString()); 
         
        insertStatus(args);
        
        doTrace(args);
    }
    private void doConsolidasi( ){
        //buat con    
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "CON");        
        
        Nson data = Nson.readJson(getText("data"));
        for (int i = 0; i < data.getData("data").size(); i++) {
                Nson cargs = Nson.newObject();
                cargs.setData("KODE_STATUS", "CONSL");
                setIfNotExist(cargs, "TRACK", getText(data, i, "TRACK"));
                setIfNotExist(cargs, "BARCODE", getText(data, i, "BARCODE"));
                setIfNotExist(cargs, "TRACK_CON", getText("TRACK"));
                
                insertStatus(cargs);

        }        
        
        insertStatus(args);
        
        //insert ref  
        if (true) {
            args = Nson.newObject();
            
            setIfNotExist(args, "CREATED_BY", session.getData("xiduser").asLong());
            setIfNotExist(args, "CREATED_DATE", Utility.Now());
            setIfNotExist(args, "MODIFIED_BY", session.getData("xiduser").asLong());
            setIfNotExist(args, "MODIFIED_DATE", Utility.Now());

            setIfNotExist(args, "ID_LABEL_REFERENSI", Z.getNumberRefFromCon(getText("track")) ); 
            setIfNotExist(args, "BARCODE", getText("barcode"));             
            setIfNotExist(args, "INPUT_REFERENSI", "CON"); 
            setIfNotExist(args, "ID_KIRIMAN", getText("track")); 
            
            StringBuilder query  = new StringBuilder();
            StringBuilder fields = new StringBuilder();
            StringBuilder values = new StringBuilder();

            Nson nfield = args.getObjectKeys();  
            for (int i = 0; i < nfield.size(); i++) {
                fields.append(i>=1?",":"");
                fields.append(nfield.getData(i).asString());

                values.append(i>=1?",":""); 
                values.append("'"); 
                values.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
                values.append("'"); 
            }

            query.append("INSERT INTO trx_label_referensi (");
            query.append(fields.toString());
            query.append(")  VALUES (");
            query.append(values.toString());
            query.append(");");
            Nikitaset ns = nikitaConnection.Query(query.toString(), null);
            String err = ns.getError();
        }       
        
        //insert trxcons
        if (true) {
            args = Nson.newObject();
            setIfNotExist(args, "ID_USER", session.getData("xiduser").asLong()); 
            setIfNotExist(args, "CREATED_BY", session.getData("xiduser").asLong());
            setIfNotExist(args, "CREATED_DATE", Utility.Now());
            setIfNotExist(args, "MODIFIED_BY", session.getData("xiduser").asLong());
            setIfNotExist(args, "MODIFIED_DATE", Utility.Now());

            setIfNotExist(args, "TRACK", getText("track"));
            setIfNotExist(args, "BARCODE", getText("barcode"));
            setIfNotExist(args, "DATA_CONS", getText("data"));        
            StringBuilder query  = new StringBuilder();
            StringBuilder fields = new StringBuilder();
            StringBuilder values = new StringBuilder();

            Nson nfield = args.getObjectKeys();  
            for (int i = 0; i < nfield.size(); i++) {
                fields.append(i>=1?",":"");
                fields.append(nfield.getData(i).asString());

                values.append(i>=1?",":""); 
                values.append("'"); 
                values.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
                values.append("'"); 
            }

            query.append("INSERT INTO trx_cons (");
            query.append(fields.toString());
            query.append(")  VALUES (");
            query.append(values.toString());
            query.append(");");
            Nikitaset ns = nikitaConnection.Query(query.toString(), null);
            String err = ns.getError();
        }
    }
    private void doLabel( ){
        //print label
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "LABEL");        
        
        insertStatus(args);     
    }
    private void doRevisi( ){
        //revisi berat
        Nson args = Nson.newObject();
        args.setData("KODE_STATUS", "REV");        
        
        insertStatus(args);     
    }
    
    private void doLocationUpdate( ){
        Nson args = Nson.newObject();
 
        setIfNotExist(args, "LOCATION", "");
        setIfNotExist(args, "AKURASI", 0);
        
        setIfNotExist(args, "LATITUDE", 0);
        setIfNotExist(args, "LONGITUDE", 0);
        setIfNotExist(args, "ID_USER_EXACT", session.getData("xiduser").asLong());
         

        setIfNotExist(args, "CREATED_BY", session.getData("xiduser").asLong());
        setIfNotExist(args, "CREATED_DATE", Utility.Now());
        setIfNotExist(args, "MODIFIED_BY", session.getData("xiduser").asLong());
        setIfNotExist(args, "MODIFIED_DATE", Utility.Now());
                
        
        StringBuilder query  = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        Nson nfield = args.getObjectKeys();  
        for (int i = 0; i < nfield.size(); i++) {
            fields.append(i>=1?",":"");
            fields.append(nfield.getData(i).asString());
            
            values.append(i>=1?",":""); 
            values.append("'"); 
            values.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
            values.append("'"); 
        }
        
        if (!NikitaConnection.getDefaultPropertySetting().getData("init").getData("location_history").toString().equalsIgnoreCase("false")) {
            query.append("INSERT INTO trx_location_history (");
            query.append(fields.toString());
            query.append(")  VALUES (");
            query.append(values.toString());
            query.append(");");
            Nikitaset ns = nikitaConnection.Query(query.toString(), null);
            String err = ns.getError();
        }
        if (!NikitaConnection.getDefaultPropertySetting().getData("init").getData("location").toString().equalsIgnoreCase("false")) {
            query = new StringBuilder();
            query.append("REPLACE INTO trx_location (");
            query.append(fields.toString());
            query.append(")  VALUES (");
            query.append(values.toString());
            query.append(") ;");
            Nikitaset ns = nikitaConnection.Query(query.toString(), null);
            String err = ns.getError();
        }
    }
    private void insertStatus(Nson args){
        //cari detail user, dan auth lengkapi keperluda dari asp dll
        
        
        
        setIfNotExist(args, "TRACK", getText("TRACK"));
        setIfNotExist(args, "BARCODE", getText("BARCODE"));
        setIfNotExist(args, "IS_SCAN", args.getData("BARCODE").asString().length()>=23?"YES":"NO");
               
        setIfNotExist(args, "CREATED_BY",   session.getData("xiduser").asLong());
        setIfNotExist(args, "CREATED_DATE", Utility.Now());
        setIfNotExist(args, "MODIFIED_BY",  session.getData("xiduser").asLong());
        setIfNotExist(args, "MODIFIED_DATE", Utility.Now());
                
        setIfNotExist(args, "WAKTU_STATUS", profile.getData("cdate").asString());
        setIfNotExist(args, "ZONA",  profile.getData("tdate").asInteger());
        setIfNotExist(args, "IMEI", curdata.getData("imei").asString());
        setIfNotExist(args, "APP", curdata.getData("app").asString());
        setIfNotExist(args, "DEVICE_PROFILE", curdata.getData("profile").asString());
        
        setIfNotExist(args, "ID_ASP", 0);
        setIfNotExist(args, "ID_USER_EXACT", session.getData("xiduser").asLong());
        
        
        StringBuilder query = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        Nson nfield = args.getObjectKeys();  
        for (int i = 0; i < nfield.size(); i++) {
            fields.append(i>=1?",":"");
            fields.append(nfield.getData(i).asString());
            
            values.append(i>=1?",":""); 
            values.append("'"); 
            values.append( Utility.escapeSQL( args.getData(nfield.getData(i).asString()).asString() )   );
            values.append("'"); 
        }
        query.append("INSERT INTO trx_status (");
        query.append(fields.toString());
        query.append(")  VALUES (");
        query.append(values.toString());
        query.append(");");
        Nikitaset ns = nikitaConnection.Query(query.toString(), null);
        String err = ns.getError();
    } 
    private void setIfNotExist(Nson args, String key, Number value){
        setIfNotExist(args, key, String.valueOf(value));
    }
    private void setIfNotExist(Nson args, String key, String value){
        if (!args.containsKey(key)) {
            args.setData(key, value);
        }
    }
    private String getText(String colname) {
        return getText(curdata, 0, colname);
    }
    private String getText(Nson data, int row, String colname) {
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
